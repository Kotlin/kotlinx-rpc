/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalEncodingApi::class)

package kotlinx.rpc.grpc

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

public actual class GrpcMetadataKey<T> actual constructor(
    name: String,
    public val marshaller: GrpcMarshaller<T>,
) {
    public val name: String = name.lowercase()
    internal val isBinary: Boolean get() = name.endsWith("-bin")

    internal fun encode(value: T): ByteArray = marshaller.encode(value).readByteArray()

    internal fun decode(value: ByteArray): T = Buffer().let { buffer ->
        buffer.write(value)
        marshaller.decode(buffer)
    }

    internal fun validateForString() {
        validateName()
        require(!isBinary) { "String header is named $name. It must not end with '-bin'" }
    }

    internal fun validateForBinary() {
        validateName()
        require(isBinary) { "Binary header is named $name. It must end with '-bin'" }
        require(name != "-bin") { "Binary header must have a non-empty name before '-bin'" }
    }
}

public actual class GrpcMetadata @InternalRpcApi actual constructor() {
    internal val map: LinkedHashMap<String, MutableList<ByteArray>> = linkedMapOf()

    override fun toString(): String = buildString {
        append("Metadata(")
        var first = true
        for ((key, values) in map) {
            for (value in values) {
                if (!first) append(',')
                first = false
                append(key).append('=')
                append(if (key.endsWith("-bin")) Base64.encode(value) else value.toAsciiString())
            }
        }
        append(')')
    }
}

public actual operator fun GrpcMetadata.get(key: String): String? = get(key.toAsciiKey())

public actual operator fun <T> GrpcMetadata.get(key: GrpcMetadataKey<T>): T? {
    key.validateForString()
    return map[key.name]?.lastOrNull()?.let(key::decode)
}

public actual fun GrpcMetadata.getBinary(key: String): ByteArray? = getBinary(key.toBinaryKey())

public actual fun <T> GrpcMetadata.getBinary(key: GrpcMetadataKey<T>): T? {
    key.validateForBinary()
    return map[key.name]?.lastOrNull()?.let(key::decode)
}

public actual fun GrpcMetadata.getAll(key: String): List<String> = getAll(key.toAsciiKey())

public actual fun <T> GrpcMetadata.getAll(key: GrpcMetadataKey<T>): List<T> {
    key.validateForString()
    return map[key.name]?.map(key::decode) ?: emptyList()
}

public actual fun GrpcMetadata.getAllBinary(key: String): List<ByteArray> = getAllBinary(key.toBinaryKey())

public actual fun <T> GrpcMetadata.getAllBinary(key: GrpcMetadataKey<T>): List<T> {
    key.validateForBinary()
    return map[key.name]?.map(key::decode) ?: emptyList()
}

public actual fun GrpcMetadata.keys(): Set<String> =
    map.entries.filter { it.value.isNotEmpty() }.mapTo(mutableSetOf()) { it.key }

/**
 * Visits entries synchronously in key insertion order and value order within each key.
 *
 * Callbacks must not modify this metadata. Binary arrays are borrowed for the callback only;
 * they must not be mutated or retained. Copy them if ownership is required.
 *
 * @param onString Receives each string entry decoded using the metadata ASCII rules.
 * @param onBinary Receives each binary entry without copying its stored bytes.
 */
@InternalRpcApi
public fun GrpcMetadata.visitEntries(
    onString: (key: String, value: String) -> Unit,
    onBinary: (key: String, value: ByteArray) -> Unit,
): Unit {
    for ((key, values) in map) {
        if (key.endsWith("-bin")) {
            for (value in values) onBinary(key, value)
        } else {
            for (value in values) onString(key, value.toAsciiString())
        }
    }
}

public actual operator fun GrpcMetadata.contains(key: String): Boolean = map.containsKey(key.lowercase())

public actual fun GrpcMetadata.append(key: String, value: String): Unit = append(key.toAsciiKey(), value)

public actual fun <T> GrpcMetadata.append(key: GrpcMetadataKey<T>, value: T) {
    key.validateForString()
    map.getOrPut(key.name) { mutableListOf() }.add(key.encode(value))
}

public actual fun GrpcMetadata.appendBinary(key: String, value: ByteArray): Unit =
    appendBinary(key.toBinaryKey(), value)

public actual fun <T> GrpcMetadata.appendBinary(key: GrpcMetadataKey<T>, value: T) {
    key.validateForBinary()
    map.getOrPut(key.name) { mutableListOf() }.add(key.encode(value))
}

public actual fun GrpcMetadata.remove(key: String, value: String): Boolean = remove(key.toAsciiKey(), value)

public actual fun <T> GrpcMetadata.remove(key: GrpcMetadataKey<T>, value: T): Boolean {
    key.validateForString()
    val index = getAll(key).indexOf(value)
    if (index == -1) return false
    map.getValue(key.name).removeAt(index)
    return true
}

public actual fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean =
    removeBinary(key.toBinaryKey(), value)

public actual fun <T> GrpcMetadata.removeBinary(key: GrpcMetadataKey<T>, value: T): Boolean {
    key.validateForBinary()
    val index = getAllBinary(key).indexOf(value)
    if (index == -1) return false
    map.getValue(key.name).removeAt(index)
    return true
}

public actual fun GrpcMetadata.removeAll(key: String): List<String> = removeAll(key.toAsciiKey())

public actual fun <T> GrpcMetadata.removeAll(key: GrpcMetadataKey<T>): List<T> {
    key.validateForString()
    return map.remove(key.name)?.map(key::decode) ?: emptyList()
}

public actual fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray> = removeAllBinary(key.toBinaryKey())

public actual fun <T> GrpcMetadata.removeAllBinary(key: GrpcMetadataKey<T>): List<T> {
    key.validateForBinary()
    return map.remove(key.name)?.map(key::decode) ?: emptyList()
}

public actual fun GrpcMetadata.merge(other: GrpcMetadata) {
    for ((key, values) in other.map) {
        map.getOrPut(key) { mutableListOf() }.addAll(values)
    }
}

private fun ByteArray.toAsciiString(): String = buildString(size) {
    for (byte in this@toAsciiString) {
        val value = byte.toInt() and 0xff
        append(if (value <= 0x7f) value.toChar() else '\uFFFD')
    }
}

private fun String.toAsciiBytes(): ByteArray = ByteArray(length) { index ->
    this[index].let { if (it.code <= 0x7f) it.code.toByte() else '?'.code.toByte() }
}

private fun <T> GrpcMetadataKey<T>.validateName() {
    require(name.isNotEmpty()) { "Header name must not be empty." }
    for (char in name) {
        require(char == '-' || char == '_' || char == '.' || char in '0'..'9' || char in 'a'..'z') {
            "Header is named $name. It contains illegal character $char."
        }
    }
}

private val AsciiMarshaller = object : GrpcMarshaller<String> {
    override fun encode(value: String, config: GrpcMarshallerConfig?): Source = Buffer().apply {
        write(value.toAsciiBytes())
    }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): String =
        source.use { it.readByteArray().toAsciiString() }
}

private val BinaryMarshaller = object : GrpcMarshaller<ByteArray> {
    override fun encode(value: ByteArray, config: GrpcMarshallerConfig?): Source = Buffer().apply { write(value) }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): ByteArray = source.readByteArray()
}

private fun String.toAsciiKey(): GrpcMetadataKey<String> = GrpcMetadataKey(this, AsciiMarshaller)
private fun String.toBinaryKey(): GrpcMetadataKey<ByteArray> = GrpcMetadataKey(this, BinaryMarshaller)
