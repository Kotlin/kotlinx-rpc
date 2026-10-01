/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.internal.utils.InternalRpcApi
import platform.posix.memcpy

/** An exact-size writer over native storage whose pointer is valid only inside [accessStorage]. */
@InternalRpcApi
public abstract class NativeRegionMessageWriter(size: Int) : GrpcMessageWriter(size) {
    private var state: State = if (size == 0) State.Complete else State.Open
    private val scopedAccess = ScopedNativeRegionAccess()

    /** Invokes [block] with the storage base address; null is allowed only for an empty message. */
    public abstract fun accessStorage(block: (base: CPointer<ByteVar>?) -> Boolean): Boolean

    /** Publishes complete storage to its consumer. */
    protected abstract fun onSeal()

    /** Releases storage that has not been taken by its consumer. */
    protected abstract fun onDiscard()

    /** Seals the storage after exactly [size] bytes have been written. */
    public fun seal() {
        if (scopedAccess.isAccessing) markFailed()
        check(state == State.Complete) {
            "NativeRegionMessageWriter.seal requires a complete writer, but its state is $state " +
                "($written of $size declared bytes written). GrpcEncodedMessage.writeTo must write exactly " +
                "GrpcEncodedMessage.size bytes before sealing; a failed, sealed or discarded writer cannot be sealed."
        }
        try {
            onSeal()
            state = State.Sealed
            scopedAccess.invalidate()
        } catch (cause: Throwable) {
            markFailed()
            throw cause
        }
    }

    /** Releases storage. Repeated calls are safe, including after the consumer has taken it. */
    public fun discard() {
        if (scopedAccess.isAccessing) {
            markFailed()
            error(
                "NativeRegionMessageWriter.discard was called inside a storage block. " +
                    "Discard the writer only after accessStorage returns, so storage is not released " +
                    "while the write action is using its pointer.",
            )
        }
        if (state == State.Discarded) return
        state = State.Discarded
        scopedAccess.invalidate()
        onDiscard()
    }

    override fun writeByte(byte: Byte): Unit = runOrMarkFailed {
        UnsafeGrpcMessageWriterOperations.writeToTail(this, 1) { pointer, start, _ ->
            pointer[start] = byte
            1
        }
    }

    @OptIn(UnsafeNumber::class)
    override fun write(bytes: ByteArray, startIndex: Int, endIndex: Int): Unit = runOrMarkFailed {
        requireWritable(allowEmptyComplete = true)
        val count = checkedWriteRangeSize(bytes, startIndex, endIndex)
        if (count == 0) return@runOrMarkFailed
        UnsafeGrpcMessageWriterOperations.writeToTail(this, count) { pointer, start, _ ->
            bytes.usePinned { pinned ->
                memcpy(pointer + start, pinned.addressOf(startIndex), count.convert())
            }
            count
        }
    }

    @OptIn(UnsafeIoApi::class, UnsafeNumber::class)
    override fun write(source: Buffer, byteCount: Long): Unit = runOrMarkFailed {
        requireWritable(allowEmptyComplete = true)
        val count = checkedWriteByteCount(source, byteCount)
        if (count == 0) return@runOrMarkFailed
        // Shared segment views preserve the source if the storage rejects the write, without
        // allocating and copying the entire message into an intermediate ByteArray.
        val view = Buffer()
        source.copyTo(view, endIndex = byteCount)
        UnsafeGrpcMessageWriterOperations.writeToTail(this, count) { pointer, start, _ ->
            var copied = 0
            while (view.size > 0L) {
                UnsafeBufferOperations.readFromHead(view) { bytes, from, to ->
                    val chunk = to - from
                    bytes.usePinned { pinned ->
                        memcpy(pointer + start + copied, pinned.addressOf(from), chunk.convert())
                    }
                    copied += chunk
                    chunk
                }
            }
            check(copied == count) { "The source view contained $copied bytes instead of $count" }
            copied
        }
        source.skip(byteCount)
    }

    private fun requireWritable(allowEmptyComplete: Boolean = false) {
        if (state == State.Open) return
        if (allowEmptyComplete && state == State.Complete && size == 0) return
        error(
            "NativeRegionMessageWriter cannot accept a write in state $state " +
                "($written of $size declared bytes already written). GrpcEncodedMessage.writeTo must write " +
                "exactly GrpcEncodedMessage.size bytes and must not reuse a writer after a failed write, " +
                "seal() or discard().",
        )
    }

    @PublishedApi
    internal fun writeToTail(
        minimumCapacity: Int,
        writeAction: (pointer: CPointer<ByteVar>, startIndex: Int, endIndexExclusive: Int) -> Int,
    ): Int = runOrMarkFailed {
        requireWritable()
        if (minimumCapacity !in 0..remaining) {
            throw IndexOutOfBoundsException(
                "UnsafeGrpcMessageWriterOperations.writeToTail received minimumCapacity=$minimumCapacity " +
                    "with $remaining bytes remaining ($written of $size declared bytes already written). " +
                    "Request a capacity between 0 and $remaining. Ensure GrpcEncodedMessage.size matches " +
                    "the total number of bytes written by writeTo.",
            )
        }

        val start = written
        val count = scopedAccess.access(::accessStorage) { pointer ->
            val base = checkNotNull(pointer) {
                "NativeRegionMessageWriter.accessStorage supplied a null pointer for a message of size $size. " +
                    "The implementation must provide a valid base address for every non-empty message " +
                    "for the duration of the storage block."
            }
            writeAction(base, start, size).also { count ->
                check(count in 0..remaining) {
                    "UnsafeGrpcMessageWriterOperations.writeToTail action returned byteCount=$count " +
                        "for unwritten range [$start, $size). Return the number of bytes actually written, " +
                        "between 0 and $remaining; do not call another writer operation inside the action."
                }
            }
        }
        recordWrite(count) {}
        state = if (remaining == 0) State.Complete else State.Open
        count
    }

    /** Terminal storage stays sealed or discarded even when a later operation is rejected. */
    private fun markFailed() {
        if (state == State.Discarded || state == State.Sealed || state == State.Failed) return
        state = State.Failed
        scopedAccess.invalidate()
        fail()
    }

    private inline fun <T> runOrMarkFailed(block: () -> T): T = try {
        block()
    } catch (cause: Throwable) {
        markFailed()
        throw cause
    }

    private enum class State { Open, Complete, Sealed, Failed, Discarded }
}

/** Unsafe access to the unwritten tail of a [NativeRegionMessageWriter]. */
@InternalRpcApi
public object UnsafeGrpcMessageWriterOperations {
    /**
     * Calls [writeAction] once with the storage base and the unwritten range. The returned byte count is committed.
     * The pointer must not escape the action, and the action must not call another writer operation.
     */
    public inline fun writeToTail(
        writer: NativeRegionMessageWriter,
        minimumCapacity: Int,
        crossinline writeAction: (pointer: CPointer<ByteVar>, startIndex: Int, endIndexExclusive: Int) -> Int,
    ): Int = writer.writeToTail(minimumCapacity) { pointer, start, end -> writeAction(pointer, start, end) }
}
