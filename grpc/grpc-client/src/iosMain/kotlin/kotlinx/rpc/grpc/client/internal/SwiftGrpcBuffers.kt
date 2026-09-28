/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.io.UnsafeIoApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.plus
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.grpc.marshaller.internal.NativeRegionMessageWriter
import platform.darwin.NSObject
import platform.posix.memcpy
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestMessageProtocol
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestBytes

/** Writes an exact-size request into storage that grpc-swift takes without copying. */
internal class SwiftRequestMessageWriter(size: Int) : NativeRegionMessageWriter(size) {
    private val bytes = SwiftGrpcRequestBytes(count = size.convert())

    override fun accessStorage(block: (kotlinx.cinterop.CPointer<ByteVar>?) -> Boolean): Boolean =
        bytes.withWritableBytes { base, _ -> block(base?.reinterpret()) }

    override fun onSeal() {
        check(bytes.seal()) { "Swift rejected the completed request" }
    }

    override fun onDiscard() {
        bytes.discard()
    }

    fun requestBytes(): SwiftGrpcRequestMessageProtocol = bytes
}

/** Releases an owned request only if grpc-swift has not already taken its bytes. */
internal fun SwiftGrpcRequestMessageProtocol.discardIfOwned() {
    (this as? SwiftGrpcRequestBytes)?.discard()
}

/** Owns one encoded request until Swift has copied it into grpc-swift-owned storage. */
internal class KotlinGrpcRequestMessage(
    private val buffer: Buffer,
) : NSObject(), SwiftGrpcRequestMessageProtocol {
    private val byteCount: Long = buffer.size

    override fun length(): Long = byteCount

    override fun fillBuffer(buffer: COpaquePointer?, capacity: Long): Boolean {
        if (capacity != byteCount || (byteCount > 0 && buffer == null)) return false

        val destination = buffer?.reinterpret<ByteVar>()
        var offset = 0L
        while (this.buffer.size > 0L) {
            UnsafeBufferOperations.readFromHead(this.buffer) { bytes, start, endExclusive ->
                val count = endExclusive - start
                bytes.usePinned { pinned ->
                    memcpy(destination!! + offset, pinned.addressOf(start), count.convert())
                }
                offset += count
                count
            }
        }
        return offset == byteCount
    }
}

/** Copies scoped Swift bytes directly into kotlinx-io buffer segments. */
internal fun copySwiftBytes(bytes: COpaquePointer?, length: Long): Buffer {
    require(length >= 0) { "A grpc-swift message cannot have a negative length" }
    require(length == 0L || bytes != null) { "grpc-swift returned a null pointer for a non-empty message" }

    val result = Buffer()
    val source = bytes?.reinterpret<ByteVar>()
    var offset = 0L
    while (offset < length) {
        val remaining = (length - offset).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        UnsafeBufferOperations.writeToTail(result, 1) { destination, start, endExclusive ->
            val count = minOf(remaining, endExclusive - start)
            destination.usePinned { pinned ->
                memcpy(pinned.addressOf(start), source!! + offset, count.convert())
            }
            offset += count
            count
        }
    }
    return result
}

/** Copies scoped Swift bytes directly into the ByteArray representation used by metadata. */
internal fun copySwiftByteArray(bytes: COpaquePointer?, length: Long): ByteArray {
    require(length in 0..Int.MAX_VALUE.toLong()) { "A metadata value is too large: $length bytes" }
    require(length == 0L || bytes != null) { "grpc-swift returned a null pointer for non-empty metadata" }
    if (length == 0L) return ByteArray(0)

    return ByteArray(length.toInt()).also { result ->
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length.convert())
        }
    }
}
