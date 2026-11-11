/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageWriter
import platform.posix.memcpy
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes

/** Writes an exact-size request into storage that grpc-swift takes without copying. */
internal class SwiftRequestMessageWriter(size: Int) : NativeBufferMessageWriter(size) {
    private val bytes = SwiftGrpcRequestBytes(count = size.convert())

    override fun accessBuffer(block: (CPointer<ByteVar>?) -> Unit) {
        bytes.withWritableBytes { base, _ -> block(base?.reinterpret()) }
    }

    fun requestBytes(): SwiftGrpcRequestBytes = bytes
}

/** Borrows one response message while the enclosing Swift unsafe-bytes scope is open. */
internal class SwiftResponseMessageReader(bytes: COpaquePointer?, size: Int) : NativeBufferMessageReader(size) {
    private val base = bytes?.reinterpret<ByteVar>()

    init {
        require(size == 0 || base != null) { "grpc-swift returned a null pointer for a non-empty message" }
    }

    override fun accessBuffer(block: (CPointer<ByteVar>?) -> Unit) {
        checkOpen()
        block(base)
    }
}

/** Copies scoped Swift bytes directly into the ByteArray representation used by metadata. */
internal fun copySwiftByteArray(bytes: COpaquePointer?, length: Long): ByteArray {
    require(length == 0L || bytes != null) { "grpc-swift returned a null pointer for non-empty metadata" }
    if (length == 0L) {
        return ByteArray(0)
    }

    return ByteArray(length.toInt()).also { result ->
        result.usePinned { pinned ->
            memcpy(pinned.addressOf(0), bytes, length.convert())
        }
    }
}
