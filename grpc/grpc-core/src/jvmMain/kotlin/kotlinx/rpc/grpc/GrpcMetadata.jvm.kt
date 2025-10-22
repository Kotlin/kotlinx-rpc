/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import io.grpc.Metadata
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias GrpcMetadata = io.grpc.Metadata

public actual operator fun GrpcMetadata.get(key: String): String? {
    return get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))
}

public actual fun GrpcMetadata.getBinary(key: String): ByteArray? {
    return get(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER))
}

public actual fun GrpcMetadata.getAll(key: String): List<String> {
    return getAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.getAllBinary(key: String): List<ByteArray> {
    return getAll(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER))?.toList() ?: emptyList()
}

public actual operator fun GrpcMetadata.contains(key: String): Boolean {
    val javaKey = if (key.endsWith(Metadata.BINARY_HEADER_SUFFIX))
        Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER) else
        Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)
    return containsKey(javaKey)
}

public actual fun GrpcMetadata.keys(): Set<String> {
    return this.keys()
}

public actual fun GrpcMetadata.append(key: String, value: String) {
    return put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value)
}

public actual fun GrpcMetadata.appendBinary(key: String, value: ByteArray) {
    return put(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER), value)
}

public actual fun GrpcMetadata.remove(key: String, value: String): Boolean {
    return remove(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value)
}

public actual fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean {
    return remove(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER), value)
}

public actual fun GrpcMetadata.removeAll(key: String): List<String> {
    return removeAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray> {
    return removeAll(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER))?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.merge(other: GrpcMetadata) {
    this.merge(other)
}