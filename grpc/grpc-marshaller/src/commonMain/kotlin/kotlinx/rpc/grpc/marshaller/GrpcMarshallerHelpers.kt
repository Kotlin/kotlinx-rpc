/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
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
    val encoded = encode(value, config)
    if (encoded is Buffer) return encoded

    return Buffer().also { encoded.transferTo(it) }
}

/** Decodes a value from [bytes]. */
@ExperimentalRpcApi
public fun <T> GrpcMarshaller<T>.decodeFromByteArray(
    bytes: ByteArray,
    config: GrpcMarshallerConfig? = null,
): T = decode(Buffer().apply { write(bytes) }, config)

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
    return decode(buffer, config)
}
