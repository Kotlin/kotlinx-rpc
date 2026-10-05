/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(InternalRpcApi::class, ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
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
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageWriterOperations
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufEncodingException
import kotlin.experimental.ExperimentalNativeApi

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

/**
 * Encodes directly into the [writer]'s native buffer, which must be filled exactly.
 */
@PublishedApi
internal fun encodeDirect(writer: NativeBufferMessageWriter, block: (WireEncoder) -> Unit) {
    val expected = writer.remaining
    if (expected == 0) {
        encodeInto(null, 0, block)
        return
    }
    UnsafeGrpcMessageWriterOperations.writeToTail(writer, expected) { pointer, start, end ->
        // Checked inside the write so that a short encoding fails the writer.
        encodeInto(pointer + start, end - start, block).also { count ->
            check(count == expected) { "Protobuf encoded $count bytes instead of $expected" }
        }
    }
}

@OptIn(ExperimentalNativeApi::class)
private fun encodeInto(base: CPointer<ByteVar>?, capacity: Int, block: (WireEncoder) -> Unit): Int {
    val output = NativeBufferOutput(base, capacity)
    val handle = EncoderHandle(output)
    try {
        val encoder = WireEncoderNative(handle)
        block(encoder)
        encoder.flush()
        return output.byteCount
    } catch (cause: ProtobufEncodingException) {
        // A full output fails the next write, which hides that the declared size was too small,
        // so we check specifically for that before rethrowing the reported exception.
        output.checkWithinCapacity(cause)
        throw cause
    } finally {
        handle.close()
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
