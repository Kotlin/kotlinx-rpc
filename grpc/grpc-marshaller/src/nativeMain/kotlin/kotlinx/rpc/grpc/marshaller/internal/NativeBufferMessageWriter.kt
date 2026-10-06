/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.internal.utils.InternalRpcApi
import platform.posix.memcpy

/**
 * An exact-size writer over a native buffer whose pointer is valid only inside [accessBuffer].
 *
 * It allows to directly write to a native buffer of a potentially foreign runtime.
 */
@InternalRpcApi
public abstract class NativeBufferMessageWriter(size: Int) : AbstractGrpcMessageWriter(size) {
    /** Invokes [block] once with the buffer base address; null is allowed only for an empty message. */
    public abstract fun accessBuffer(block: (base: CPointer<ByteVar>?) -> Unit)

    override fun writeByte(byte: Byte) {
        recordWrite(1) {
            val start = written
            withPointerNotNull { pointer -> pointer[start] = byte }
        }
    }

    @OptIn(UnsafeNumber::class)
    override fun write(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val count = checkedWriteRangeSize(bytes, startIndex, endIndex)
        recordWrite(count) {
            if (count == 0) return@recordWrite
            val start = written
            withPointerNotNull { pointer ->
                bytes.usePinned { pinned ->
                    memcpy(pointer + start, pinned.addressOf(startIndex), count.convert())
                }
            }
        }
    }

    @OptIn(UnsafeIoApi::class, UnsafeNumber::class)
    override fun write(source: Buffer, byteCount: Long) {
        val count = checkedWriteByteCount(source, byteCount)
        recordWrite(count) {
            if (count == 0) return@recordWrite
            val start = written
            // Shared segment views preserve the source if the write fails, without
            // allocating and copying the entire message into an intermediate ByteArray.
            val view = Buffer()
            source.copyTo(view, endIndex = byteCount)
            withPointerNotNull { base ->
                var copied = 0
                while (view.size > 0L) {
                    UnsafeBufferOperations.readFromHead(view) { bytes, from, to ->
                        val chunk = to - from
                        bytes.usePinned { pinned ->
                            memcpy(base + start + copied, pinned.addressOf(from), chunk.convert())
                        }
                        copied += chunk
                        chunk
                    }
                }
                check(copied == count) { "The source view contained $copied bytes instead of $count" }
            }
            source.skip(byteCount)
        }
    }

    @PublishedApi
    internal fun writeToTail(
        minimumCapacity: Int,
        writeAction: (pointer: CPointer<ByteVar>, startIndex: Int, endIndexExclusive: Int) -> Int,
    ): Int {
        check(!isFailed) {
            "GrpcEncodedMessage.writeTo attempted to use a writer after a previous write failed. " +
                "Do not catch and ignore write failures."
        }
        if (minimumCapacity !in 0..remaining) {
            fail()
            throw IndexOutOfBoundsException(
                "writeToTail received minimumCapacity=$minimumCapacity, but only $remaining of $size bytes remain; " +
                    "expected a capacity between 0 and $remaining. " +
                    "GrpcEncodedMessage.size must match the total number of bytes written by writeTo.",
            )
        }
        if (remaining == 0) return 0

        val start = written
        val count = try {
            withPointerNotNull { pointer ->
                writeAction(pointer, start, size).also { count ->
                    check(count in 0..remaining) {
                        "writeToTail action returned byteCount=$count for unwritten range [$start, $size); " +
                            "Return the number of bytes actually written, between 0 and $remaining."
                    }
                }
            }
        } catch (cause: Throwable) {
            fail()
            throw cause
        }
        recordWrite(count) {}
        return count
    }

    private inline fun <T> withPointerNotNull(crossinline action: (base: CPointer<ByteVar>) -> T): T =
        GuardedNativeBufferAccess.access(::accessBuffer) { pointer ->
            val base = checkNotNull(pointer) {
                "NativeBufferMessageWriter.accessBuffer supplied a null pointer for a non-empty message " +
                    "(size=$size). A non-null base address is required while the buffer block runs."
            }
            action(base)
        }
}

/** Unsafe access to the unwritten tail of a [NativeBufferMessageWriter]. */
@InternalRpcApi
public object UnsafeGrpcMessageWriterOperations {
    /**
     * Calls [writeAction] once with the buffer base and the unwritten range. The returned byte count is committed.
     * The pointer must not escape the action, and the action must not call another writer operation.
     */
    public inline fun writeToTail(
        writer: NativeBufferMessageWriter,
        minimumCapacity: Int,
        crossinline writeAction: (pointer: CPointer<ByteVar>, startIndex: Int, endIndexExclusive: Int) -> Int,
    ): Int = writer.writeToTail(minimumCapacity) { pointer, start, end -> writeAction(pointer, start, end) }
}
