/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import io.grpc.Metadata
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller

public actual typealias GrpcMetadata = Metadata

public actual class GrpcMetadataKey<T> public actual constructor(
    name: String,
    private val marshaller: GrpcMarshaller<T>,
) {
    private val name: String = name.lowercase()

    internal fun encode(value: T): ByteArray {
        val source = marshaller.encode(value)
        return source.readByteArray()
    }
    internal fun decode(value: ByteArray): T = Buffer().let { buffer ->
        buffer.write(value)
        marshaller.decode(buffer)
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
    return get(key.toAsciiKey())
}

public actual operator fun <T> GrpcMetadata.get(key: GrpcMetadataKey<T>): T? {
    return get(key.toAsciiKey())
}

public actual fun GrpcMetadata.getBinary(key: String): ByteArray? {
    return get(key.toBinaryKey())
}

public actual fun <T> GrpcMetadata.getBinary(key: GrpcMetadataKey<T>): T? {
    return get(key.toBinaryKey())
}

public actual fun GrpcMetadata.getAll(key: String): List<String> {
    return getAll(key.toAsciiKey())?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.getAll(key: GrpcMetadataKey<T>): List<T> {
    return getAll(key.toAsciiKey())?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.getAllBinary(key: String): List<ByteArray> {
    return getAll(key.toBinaryKey())?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.getAllBinary(key: GrpcMetadataKey<T>): List<T> {
    return getAll(key.toBinaryKey())?.toList() ?: emptyList()
}

public actual operator fun GrpcMetadata.contains(key: String): Boolean {
    val normalizedKey = key.lowercase()
    val javaKey = if (normalizedKey.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
        normalizedKey.toBinaryKey()
    } else {
        normalizedKey.toAsciiKey()
    }
    return containsKey(javaKey)
}

public actual fun GrpcMetadata.keys(): Set<String> {
    return this.keys()
}

public actual fun GrpcMetadata.append(key: String, value: String) {
    return put(key.toAsciiKey(), value)
}

public actual fun <T> GrpcMetadata.append(key: GrpcMetadataKey<T>, value: T) {
    return put(key.toAsciiKey(), value)
}

public actual fun GrpcMetadata.appendBinary(key: String, value: ByteArray) {
    return put(key.toBinaryKey(), value)
}

public actual fun <T> GrpcMetadata.appendBinary(key: GrpcMetadataKey<T>, value: T) {
    return put(key.toBinaryKey(), value)
}

public actual fun GrpcMetadata.remove(key: String, value: String): Boolean {
    return remove(key.toAsciiKey(), value)
}

public actual fun <T> GrpcMetadata.remove(key: GrpcMetadataKey<T>, value: T): Boolean {
    return remove(key.toAsciiKey(), value)
}

public actual fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean {
    return remove(key.toBinaryKey(), value)
}

public actual fun <T> GrpcMetadata.removeBinary(key: GrpcMetadataKey<T>, value: T): Boolean {
    return remove(key.toBinaryKey(), value)
}

public actual fun GrpcMetadata.removeAll(key: String): List<String> {
    return removeAll(key.toAsciiKey())?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.removeAll(key: GrpcMetadataKey<T>): List<T> {
    return removeAll(key.toAsciiKey())?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray> {
    return removeAll(key.toBinaryKey())?.toList() ?: emptyList()
}

public actual fun <T> GrpcMetadata.removeAllBinary(key: GrpcMetadataKey<T>): List<T> {
    return removeAll(key.toBinaryKey())?.toList() ?: emptyList()
}

public actual fun GrpcMetadata.merge(other: GrpcMetadata) {
    this.merge(other)
}

private fun String.toAsciiKey(): Metadata.Key<String> =
    Metadata.Key.of(lowercase(), Metadata.ASCII_STRING_MARSHALLER)

private fun String.toBinaryKey(): Metadata.Key<ByteArray> =
    Metadata.Key.of(lowercase(), Metadata.BINARY_BYTE_MARSHALLER)
