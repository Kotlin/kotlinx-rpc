/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.rpc.internal.utils.InternalRpcApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.NativeRegionMessageWriter

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

        is NativeRegionMessageWriter -> {
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

    else -> WireDecoder(reader.asSource()).use(block)
}
