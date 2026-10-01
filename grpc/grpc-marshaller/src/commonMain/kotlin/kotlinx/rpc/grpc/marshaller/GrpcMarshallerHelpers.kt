/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.rpc.internal.utils.InternalRpcApi::class)

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

/** Encodes [value] into a byte array. */
@ExperimentalRpcApi
public fun <T> GrpcMarshaller<T>.encodeToByteArray(
    value: T,
    config: GrpcMarshallerConfig? = null,
): ByteArray = encodeToBuffer(value, config).readByteArray()

/** Encodes [value] into a [Buffer]. */
@ExperimentalRpcApi
public fun <T> GrpcMarshaller<T>.encodeToBuffer(
    value: T,
    config: GrpcMarshallerConfig? = null,
): Buffer {
    val message = prepare(value, config)
    val buffer = Buffer()
    val writer = BufferMessageWriter(buffer, message.size)
    message.writeTo(writer)
    check(writer.isComplete) {
        "Marshaller $this wrote ${writer.written} bytes, but declared ${writer.size} bytes"
    }
    return buffer
}

/** Decodes a value from [bytes]. */
@ExperimentalRpcApi
public fun <T> GrpcMarshaller<T>.decodeFromByteArray(
    bytes: ByteArray,
    config: GrpcMarshallerConfig? = null,
): T = decode(ByteArrayMessageReader(bytes), config)

/**
 * Decodes a value from [source].
 *
 * A [Buffer] is passed through unchanged. Any other source is read fully into a buffer first.
 */
@ExperimentalRpcApi
public fun <T> GrpcMarshaller<T>.decodeFromSource(
    source: Source,
    config: GrpcMarshallerConfig? = null,
): T {
    val buffer = source as? Buffer ?: Buffer().also { source.transferTo(it) }
    require(buffer.size <= Int.MAX_VALUE) {
        "A gRPC message cannot be larger than Int.MAX_VALUE bytes: ${buffer.size}"
    }
    return decode(BufferMessageReader(buffer, buffer.size.toInt()), config)
}
