/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.rpc.internal.utils.InternalRpcApi::class, ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.plus
import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageReaderOperations

public actual inline fun withWireEncoder(
    writer: GrpcMessageWriter,
    crossinline block: (WireEncoder) -> Unit,
) {
    when (writer) {
        is BufferMessageWriter -> {
            writer.writeDirect { buffer ->
                WireEncoder(buffer).also(block).flush()
            }
        }

        is NativeBufferMessageWriter -> {
            encodeDirect(writer) { block(it) }
        }

        else -> {
            val buffer = Buffer()
            WireEncoder(buffer).also(block).flush()
            writer.write(buffer)
        }
    }
}

public actual inline fun <R> withWireDecoder(
    reader: GrpcMessageReader,
    crossinline block: (WireDecoder) -> R,
): R = when (reader) {
    is BufferMessageReader -> reader.readDirect { buffer ->
        WireDecoder(buffer).use(block)
    }

    is NativeBufferMessageReader -> decodeDirect(reader) { block(it) }

    else -> WireDecoder(reader.asSource()).use(block)
}

/**
 * Decodes directly from the unread part of the [reader]'s native buffer.
 */
@PublishedApi
internal fun <R> decodeDirect(reader: NativeBufferMessageReader, block: (WireDecoder) -> R): R {
    if (reader.remaining == 0) {
        reader.checkOpen()
        return WireDecoderNative(NativeBufferZeroCopyInput(null, 0)).use(block)
    }
    var result: R? = null
    UnsafeGrpcMessageReaderOperations.readFromHead(reader) { base, start, end ->
        // Create a decoder input that directly reads from the buffer slice.
        val input = NativeBufferZeroCopyInput(base + start, end - start)
        result = WireDecoderNative(input).use(block)
        // Return the consumed number of bytes.
        input.position
    }
    @Suppress("UNCHECKED_CAST")
    return result as R
}
