/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(UnsafeIoApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.io.UnsafeIoApi
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageWriter
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig

/**
 * Runs [block] with a decoder over all bytes of [buffer], and then removes these bytes from [buffer].
 *
 * The decoder is valid only inside [block].
 */
@InternalRpcApi
public fun <R> decodeWire(
    buffer: Buffer,
    recursionLimit: Int = ProtoConfig.DEFAULT_RECURSION_LIMIT,
    block: (WireDecoder) -> R,
): R {
    require(buffer.size <= Int.MAX_VALUE) {
        "A protobuf message can have at most ${Int.MAX_VALUE} bytes, but the buffer has ${buffer.size}."
    }
    val size = buffer.size.toInt()
    try {
        val result: R
        UnsafeBufferOperations.iterate(buffer) { context, head ->
            result = decodeSegments(BufferInputSegments(context, head), size, recursionLimit, block)
        }
        return result
    } finally {
        buffer.skip(size.toLong())
    }
}

internal fun <R> decodeSegments(
    segments: InputSegments,
    size: Int,
    recursionLimit: Int,
    block: (WireDecoder) -> R,
): R {
    val input = WireInput(segments, size)
    try {
        return block(WireDecoder(input, recursionLimit))
    } finally {
        input.close()
    }
}

internal fun encodeSegments(segments: OutputSegments, size: Int, block: (WireEncoder) -> Unit) {
    val output = WireOutput(segments)
    try {
        block(WireEncoder(output))
        output.flush()
        check(output.written == size) {
            "The protobuf message encoded ${output.written} bytes, but its size is $size."
        }
    } finally {
        output.close()
    }
}

/**
 * Encodes into [writer]. The encoder must write exactly `writer.remaining` bytes.
 *
 * A platform can handle its own writer types and delegate all others to [encodeToCommon].
 */
internal expect fun encodeTo(writer: GrpcMessageWriter, block: (WireEncoder) -> Unit)

/**
 * Decodes all unread bytes of [reader]. After the call, `reader.remaining` is 0.
 *
 * A platform can handle its own reader types and delegate all others to [decodeFromCommon].
 */
internal expect fun decodeFrom(reader: GrpcMessageReader, recursionLimit: Int, block: (WireDecoder) -> Unit)

internal fun encodeToCommon(writer: GrpcMessageWriter, block: (WireEncoder) -> Unit) {
    when (writer) {
        is ByteArrayMessageWriter -> {
            writer.writeDirect { array, start, end ->
                encodeSegments(ArrayOutputSegments(array, start, end), end - start, block)
            }
        }

        is BufferMessageWriter -> {
            val size = writer.remaining
            writer.writeDirect { buffer ->
                encodeSegments(BufferOutputSegments(buffer, size), size, block)
            }
        }

        else -> {
            encodeSegments(WriterOutputSegments(writer), writer.remaining, block)
        }
    }
}

internal fun decodeFromCommon(reader: GrpcMessageReader, recursionLimit: Int, block: (WireDecoder) -> Unit) {
    when (reader) {
        is ByteArrayMessageReader -> {
            reader.readDirect { array, start, end ->
                decodeSegments(ArrayInputSegments(array, start, end), end - start, recursionLimit, block)
            }
        }

        is BufferMessageReader -> {
            reader.readDirect { buffer -> decodeWire(buffer, recursionLimit, block) }
        }

        else -> {
            decodeSegments(ReaderInputSegments(reader), reader.remaining, recursionLimit, block)
        }
    }
}
