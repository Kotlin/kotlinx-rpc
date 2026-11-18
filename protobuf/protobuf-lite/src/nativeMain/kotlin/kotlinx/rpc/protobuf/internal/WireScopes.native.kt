/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.plus
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageReaderOperations
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageWriterOperations

internal actual fun encodeTo(writer: GrpcMessageWriter, block: (WireEncoder) -> Unit) {
    if (writer !is NativeBufferMessageWriter) {
        encodeToCommon(writer, block)
        return
    }
    val size = writer.remaining
    if (size == 0) {
        encodeSegments(ArrayOutputSegments(ByteArray(0), 0, 0), 0, block)
        return
    }
    UnsafeGrpcMessageWriterOperations.writeToTail(writer, size) { pointer, start, end ->
        encodeSegments(NativeOutputSegments((pointer + start)!!, end - start), end - start, block)
        end - start
    }
}

internal actual fun decodeFrom(reader: GrpcMessageReader, recursionLimit: Int, block: (WireDecoder) -> Unit) {
    if (reader !is NativeBufferMessageReader) {
        decodeFromCommon(reader, recursionLimit, block)
        return
    }
    if (reader.remaining == 0) {
        reader.checkOpen()
        decodeSegments(ArrayInputSegments(ByteArray(0), 0, 0), 0, recursionLimit, block)
        return
    }
    UnsafeGrpcMessageReaderOperations.readFromHead(reader) { pointer, start, end ->
        decodeSegments(NativeInputSegments((pointer + start)!!, end - start), end - start, recursionLimit, block)
        end - start
    }
}
