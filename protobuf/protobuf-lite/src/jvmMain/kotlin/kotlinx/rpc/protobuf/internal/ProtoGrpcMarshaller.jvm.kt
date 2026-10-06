/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.OutputStreamMessageWriter

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

        is ByteArrayMessageWriter -> {
            writer.writeDirect { bytes, startIndex, endIndex ->
                WireEncoder(bytes, startIndex, endIndex).also(block).apply {
                    flush()
                    requireComplete()
                }
            }
        }

        is OutputStreamMessageWriter -> {
            writer.writeDirect { output ->
                WireEncoder(output, minOf(maxOf(writer.remaining, 1), 4096)).also(block).flush()
            }
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

    is ByteArrayMessageReader -> reader.readDirect { bytes, startIndex, endIndex ->
        WireDecoder(bytes, startIndex, endIndex).use(block)
    }

    else -> WireDecoder(reader.asSource()).use(block)
}
