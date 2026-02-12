/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import io.grpc.Metadata
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias GrpcMetadata = io.grpc.Metadata

public actual class GrpcMetadataKey<T> public actual constructor(
    private val name: String,
    private val codec: MessageCodec<T>,
) {

    internal fun encode(value: T): ByteArray {
        val source = codec.encode(value)
        return source.readByteArray()
    }
    internal fun decode(value: ByteArray): T = Buffer().let { buffer ->
        buffer.write(value)
        codec.decode(buffer)
    }

    internal fun toAsciiKey(): Metadata.Key<T> = Metadata.Key.of(name, AsciiMarshaller(this))
    internal fun toBinaryKey(): Metadata.Key<T> = Metadata.Key.of(name, BinaryMarshaller(this))
}

@JvmInline
private value class AsciiMarshaller<T>(val key: GrpcMetadataKey<T>) : Metadata.AsciiMarshaller<T> {
    override fun toAsciiString(value: T): String {
        return key.encode(value).decodeToString()
    }

    override fun parseAsciiString(serialized: String?): T? {
        return key.decode(serialized!!.encodeToByteArray())
    }
}

@JvmInline
private value class BinaryMarshaller<T>(val key: GrpcMetadataKey<T>) : Metadata.BinaryMarshaller<T> {
    override fun toBytes(value: T): ByteArray {
        return key.encode(value)
    }

    override fun parseBytes(serialized: ByteArray): T {
        return key.decode(serialized)
    }
}

public actual operator fun GrpcMetadata.get(key: String): String? {
    return get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))
}

public actual operator fun <T> GrpcMetadata.get(key: GrpcMetadataKey<T>): T? {
    return get(key.toAsciiKey())
}

public actual fun GrpcMetadata.getBinary(key: String): ByteArray? {
    return get(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER))
}

public actual fun <T> GrpcMetadata.getBinary(key: GrpcMetadataKey<T>): T? {
    return get(key.toBinaryKey())
}

public actual fun GrpcMetadata.getAll(key: String): List<String> {
    return getAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.getAll(key: GrpcMetadataKey<T>): List<T> {
    return getAll(key.toAsciiKey())?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.getAllBinary(key: String): List<ByteArray> {
    return getAll(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER))?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.getAllBinary(key: GrpcMetadataKey<T>): List<T> {
    return getAll(key.toBinaryKey())?.toList() ?: emptyList()
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

public actual fun <T> GrpcMetadata.append(key: GrpcMetadataKey<T>, value: T) {
    return put(key.toAsciiKey(), value)
}

public actual fun GrpcMetadata.appendBinary(key: String, value: ByteArray) {
    return put(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER), value)
}

public actual fun <T> GrpcMetadata.appendBinary(key: GrpcMetadataKey<T>, value: T) {
    return put(key.toBinaryKey(), value)
}

public actual fun GrpcMetadata.remove(key: String, value: String): Boolean {
    return remove(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value)
}

public actual fun <T> GrpcMetadata.remove(key: GrpcMetadataKey<T>, value: T): Boolean {
    return remove(key.toAsciiKey(), value)
}

public actual fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean {
    return remove(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER), value)
}

public actual fun <T> GrpcMetadata.removeBinary(key: GrpcMetadataKey<T>, value: T): Boolean {
    return remove(key.toBinaryKey(), value)
}

public actual fun GrpcMetadata.removeAll(key: String): List<String> {
    return removeAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.removeAll(key: GrpcMetadataKey<T>): List<T> {
    return removeAll(key.toAsciiKey())?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray> {
    return removeAll(Metadata.Key.of(key, Metadata.BINARY_BYTE_MARSHALLER))?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.removeAllBinary(key: GrpcMetadataKey<T>): List<T> {
    return removeAll(key.toBinaryKey())?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.merge(other: GrpcMetadata) {
    this.merge(other)
}