/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.cinterop.UnsafeNumber::class,
    kotlinx.io.UnsafeIoApi::class,
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.internal.utils.InternalRpcApi
import platform.posix.memcpy

/** A reader over native storage whose pointer is valid only inside [accessStorage]. */
@InternalRpcApi
public abstract class NativeRegionMessageReader(size: Int) : GrpcMessageReader(size), AutoCloseable {
    private val scopedAccess = ScopedNativeRegionAccess()
    // Cached copied buffer after first call to [asSource]
    private var source: Buffer? = null

    /** Invokes [block] with the storage base address; null is allowed only for an empty message. */
    public abstract fun accessStorage(block: (base: CPointer<ByteVar>?) -> Boolean): Boolean

    /** Invalidates this reader and every pointer obtained through it. Repeated calls are safe. */
    public final override fun close() {
        try {
            scopedAccess.close()
        } finally {
            onClose()
        }
    }

    /** Clears any borrowed storage reference when the reader closes. */
    protected open fun onClose() {}

    override fun readByte(): Byte {
        scopedAccess.ensureOpen()
        if (remaining == 0) {
            throw IndexOutOfBoundsException(
                "GrpcMessageReader.readByte cannot read beyond a message of size $size: no bytes remain. " +
                    "After asSource(), read from the returned Source instead.",
            )
        }
        var byte: Byte = 0
        readFromHead { pointer, start, _ ->
            byte = pointer[start]
            1
        }
        return byte
    }

    override fun readTo(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        scopedAccess.ensureOpen()
        if (startIndex !in 0..endIndex || endIndex > bytes.size) {
            throw IndexOutOfBoundsException(
                "GrpcMessageReader.readTo received range [$startIndex, $endIndex) " +
                    "outside a destination array of size ${bytes.size}. " +
                    "Expected 0 <= startIndex <= endIndex <= bytes.size.",
            )
        }
        val count = endIndex - startIndex
        if (count > remaining) {
            throw IndexOutOfBoundsException(
                "GrpcMessageReader.readTo requested $count bytes, but only $remaining of $size bytes remain. " +
                    "Check the lengths used by GrpcMarshaller.decode; after asSource(), use the returned Source.",
            )
        }
        if (count == 0) return
        readFromHead { pointer, start, _ ->
            bytes.usePinned { pinned ->
                memcpy(pinned.addressOf(startIndex), pointer + start, count.convert())
            }
            count
        }
    }

    override fun asSource(): Source {
        scopedAccess.ensureOpen()
        source?.let { return it }
        val copied = Buffer()
        readFromHead { pointer, start, end ->
            var position = start
            while (position < end) {
                UnsafeBufferOperations.writeToTail(copied, 1) { destination, from, to ->
                    val count = minOf(end - position, to - from)
                    destination.usePinned { pinned ->
                        memcpy(pinned.addressOf(from), pointer + position, count.convert())
                    }
                    position += count
                    count
                }
            }
            end - start
        }
        source = copied
        return copied
    }

    @PublishedApi
    internal fun readFromHead(
        readAction: (pointer: CPointer<ByteVar>, startIndex: Int, endIndexExclusive: Int) -> Int,
    ): Int {
        scopedAccess.ensureOpen()
        if (remaining == 0) return 0
        val start = size - remaining
        val count = scopedAccess.access(::accessStorage) { pointer ->
            val base = checkNotNull(pointer) {
                "NativeRegionMessageReader.accessStorage supplied a null pointer for a non-empty message " +
                    "(size=$size). A non-null base address is required while the storage block runs."
            }
            readAction(base, start, size).also { count ->
                check(count in 0..remaining) {
                    "readFromHead action returned byteCount=$count for unread range [$start, $size); " +
                        "Return the number of bytes actually consumed, between 0 and $remaining."
                }
            }
        }
        recordRead(count) {}
        return count
    }
}

/** Unsafe access to the unread head of a [NativeRegionMessageReader]. */
@InternalRpcApi
public object UnsafeGrpcMessageReaderOperations {
    /**
     * Calls [readAction] with the storage base and unread range. The returned byte count is committed.
     * The pointer must not escape the action, and the action must not call another reader operation.
     */
    public inline fun readFromHead(
        reader: NativeRegionMessageReader,
        crossinline readAction: (pointer: CPointer<ByteVar>, startIndex: Int, endIndexExclusive: Int) -> Int,
    ): Int = reader.readFromHead { pointer, start, end -> readAction(pointer, start, end) }
}
