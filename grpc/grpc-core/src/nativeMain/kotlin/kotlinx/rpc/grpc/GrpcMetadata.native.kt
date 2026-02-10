/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class, ExperimentalEncodingApi::class)

package kotlinx.rpc.grpc

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.SourcedMessageCodec
import kotlinx.rpc.grpc.internal.toByteArray
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.input.stream.asInputStream
import libkgrpc.grpc_metadata
import libkgrpc.grpc_metadata_array
import libkgrpc.grpc_metadata_array_init
import libkgrpc.grpc_slice_from_copied_buffer
import libkgrpc.grpc_slice_from_copied_string
import libkgrpc.grpc_slice_ref
import libkgrpc.grpc_slice_unref
import libkgrpc.kgrpc_metadata_array_append
import kotlin.experimental.ExperimentalNativeApi
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

public actual class GrpcMetadataKey<T> actual constructor(name: String, public val codec: MessageCodec<T>) {
    public val name: String = name.lowercase()
    internal val isBinary get() = name.endsWith("-bin")

    internal fun encode(value: T): ByteArray = codec.encode(value).buffer.readByteArray()
    internal fun decode(value: ByteArray): T = Buffer().let { buffer ->
        buffer.write(value)
        codec.decode(buffer.asInputStream())
    }

    internal fun validateForString() {
        validateName()
        require(!isBinary) { "String header is named ${name}. It must not end with '-bin'" }
    }

    internal fun validateForBinary() {
        validateName()
        require(isBinary) { "Binary header is named ${name}. It must end with '-bin'" }
    }

    internal companion object
}

@Suppress(names = ["RedundantConstructorKeyword"])
public actual class GrpcMetadata actual constructor() {
    internal val map: LinkedHashMap<String, MutableList<ByteArray>> = linkedMapOf()

    @OptIn(UnsafeNumber::class)
    public constructor(raw: grpc_metadata_array) : this() {
        for (i in 0 until raw.count.toInt()) {
            val metadata = raw.metadata?.get(i)
            if (metadata != null) {
                val key = metadata.key.toByteArray().toAsciiString()
                val value = metadata.value.toByteArray()
                map.getOrPut(key) { mutableListOf() }.add(value)
            }
        }
    }

    @OptIn(UnsafeNumber::class)
    @InternalRpcApi
    public fun NativePlacement.allocRawGrpcMetadata(): grpc_metadata_array {
        val raw = alloc<grpc_metadata_array>()
        grpc_metadata_array_init(raw.ptr)

        // the sum of all values
        val entryCount = map.entries.sumOf { it.value.size }

        raw.count = 0u
        raw.capacity = entryCount.convert()
        raw.metadata = allocArray<grpc_metadata>(entryCount)

        map.entries.forEach { (key, values) ->
            val keySlice = grpc_slice_from_copied_string(key)

            for (entry in values) {
                val size = entry.size.toULong()
                val valSlice = entry.usePinned { pinned ->
                    grpc_slice_from_copied_buffer(pinned.addressOf(0), size.convert())
                }
                // we create a fresh reference for each entry
                val keySliceRef = grpc_slice_ref(keySlice)

                check(kgrpc_metadata_array_append(raw.ptr, keySliceRef, valSlice)) {
                    "Failed to append metadata to array"
                }
            }

            // we unref/drop the original keySlice, as it isn't used anymore
            grpc_slice_unref(keySlice)
        }

        return raw
    }

    @OptIn(ExperimentalEncodingApi::class)
    override fun toString(): String {
        val sb = StringBuilder("Metadata(")
        var first = true
        for ((key, values) in map) {
            for (value in values) {
                if (!first) {
                    sb.append(',')
                }
                first = false
                sb.append(key).append('=')
                if (key.endsWith("-bin")) {
                    sb.append(Base64.encode(value))
                } else {
                    sb.append(value.toAsciiString())
                }
            }
        }
        return sb.append(')').toString()
    }

}

public actual operator fun GrpcMetadata.get(key: String): String? {
    return get(key.toAsciiKey())
}

public actual operator fun <T> GrpcMetadata.get(key: GrpcMetadataKey<T>): T? {
    key.validateForString()
    return map[key.name]?.lastOrNull()?.let {
        key.decode(it)
    }
}

public actual fun GrpcMetadata.getBinary(key: String): ByteArray? {
    val key = key.toBinaryKey()
    key.validateForBinary()
    return map[key.name]?.lastOrNull()
}

public actual fun <T> GrpcMetadata.getBinary(key: GrpcMetadataKey<T>): T? {
    key.validateForBinary()
    return map[key.name]?.lastOrNull()?.let {
        key.decode(it)
    }
}

public actual fun GrpcMetadata.getAll(key: String): List<String> {
    return getAll(key.toAsciiKey())
}

public actual fun <T> GrpcMetadata.getAll(key: GrpcMetadataKey<T>): List<T> {
    key.validateForString()
    return map[key.name]?.map { key.decode(it) } ?: emptyList()
}

public actual fun GrpcMetadata.getAllBinary(key: String): List<ByteArray> {
    val key = key.toBinaryKey()
    key.validateForBinary()
    return map[key.name] ?: emptyList()
}

public actual fun <T> GrpcMetadata.getAllBinary(key: GrpcMetadataKey<T>): List<T> {
    key.validateForBinary()
    return map[key.name]?.map { key.decode(it) } ?: emptyList()
}

public actual operator fun GrpcMetadata.contains(key: String): Boolean {
    return map.containsKey(key.lowercase())
}

public actual fun GrpcMetadata.keys(): Set<String> {
    return map.entries.filter { it.value.isNotEmpty() }.mapTo(mutableSetOf()) { it.key }
}

public actual fun GrpcMetadata.append(key: String, value: String) {
    append(key.toAsciiKey(), value)
}

public actual fun <T> GrpcMetadata.append(key: GrpcMetadataKey<T>, value: T) {
    key.validateForString()
    map.getOrPut(key.name) { mutableListOf() }.add(key.encode(value))
}

public actual fun GrpcMetadata.appendBinary(key: String, value: ByteArray) {
    val key = key.toBinaryKey()
    key.validateForBinary()
    map.getOrPut(key.name) { mutableListOf() }.add(value)
}

public actual fun <T> GrpcMetadata.appendBinary(key: GrpcMetadataKey<T>, value: T) {
    key.validateForBinary()
    map.getOrPut(key.name) { mutableListOf() }.add(key.encode(value))
}

public actual fun GrpcMetadata.remove(key: String, value: String): Boolean {
    return remove(key.toAsciiKey(), value)
}

public actual fun <T> GrpcMetadata.remove(key: GrpcMetadataKey<T>, value: T): Boolean {
    key.validateForString()
    val index = getAll(key).indexOf(value)
    if (index == -1) return false
    map[key.name]!!.removeAt(index)
    return true
}

public actual fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean {
    val keyObj = key.toBinaryKey()
    keyObj.validateForBinary()
    val index = getAllBinary(key).indexOf(value)
    if (index == -1) return false
    map[keyObj.name]!!.removeAt(index)
    return true
}

public actual fun <T> GrpcMetadata.removeBinary(key: GrpcMetadataKey<T>, value: T): Boolean {
    key.validateForBinary()
    val index = getAllBinary(key).indexOf(value)
    if (index == -1) return false
    map[key.name]!!.removeAt(index)
    return true
}

public actual fun GrpcMetadata.removeAll(key: String): List<String> {
    return removeAll(key.toAsciiKey())
}

public actual fun <T> GrpcMetadata.removeAll(key: GrpcMetadataKey<T>): List<T> {
    key.validateForString()
    return map.remove(key.name)?.map { key.decode(it) } ?: emptyList()
}

public actual fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray> {
    return removeAllBinary(key.toBinaryKey())
}

public actual fun <T> GrpcMetadata.removeAllBinary(key: GrpcMetadataKey<T>): List<T> {
    key.validateForBinary()
    return map.remove(key.name)?.map { key.decode(it) } ?: emptyList()
}

public actual fun GrpcMetadata.merge(other: GrpcMetadata) {
    for ((key, values) in other.map) {
        map.getOrPut(key) { mutableListOf() }.addAll(values)
    }
}

/**
 * Converts the ByteArray to a string containing only ASCII characters.
 * For bytes within the ASCII range (0x00 to 0x7F), the corresponding character is used.
 * For bytes outside this range, the replacement character '�' (`\uFFFD`) is used.
 *
 * @return A string representation of the ByteArray,
 *  where non-ASCII bytes are replaced with '�' (`\uFFFD`).
 */
private fun ByteArray.toAsciiString(): String {
    return buildString(size) {
        for (b in this@toAsciiString) {
            val ub = b.toInt() and 0xFF
            append(if (ub in 0..0x7F) ub.toChar() else '\uFFFD')
        }
    }
}

/**
 * Converts the string to a byte array encoded in US-ASCII.
 * Characters outside the ASCII range are replaced with the '?' character.
 *
 * @return a byte array representing the ASCII-encoded version of the string
 */
private fun String.toAsciiBytes(): ByteArray {
    // encode as US_ASCII bytes, replacing non-ASCII chars with '?'
    return ByteArray(length) { idx ->
        val c = this[idx]
        if (c.code in 0..0x7F) c.code.toByte() else '?'.code.toByte()
    }
}

@OptIn(ObsoleteNativeApi::class)
private val VALID_KEY_CHARS by lazy {
    BitSet(0x7f).apply {
        set('-'.code)
        set('_'.code)
        set('.'.code)
        set('0'.code..'9'.code)
        set('a'.code..'z'.code)
    }
}

@OptIn(ObsoleteNativeApi::class)
private fun <T> GrpcMetadataKey<T>.validateName() {
    for (char in name) {
        require(VALID_KEY_CHARS[char.code]) { "Header is named $name. It contains illegal character $char." }
    }
}

private val AsciiCodec = object : SourcedMessageCodec<String> {
    override fun encodeToSource(value: String, config: CodecConfig?): Source = Buffer().apply {
        write(value.toAsciiBytes())
    }

    override fun decodeFromSource(stream: Source, config: CodecConfig?): String = stream.use { buffer ->
        buffer.readByteArray().toAsciiString()
    }
}

private val BinaryCodec = object : SourcedMessageCodec<ByteArray> {
    override fun encodeToSource(value: ByteArray, config: CodecConfig?): Source = Buffer().apply {
        write(value)
    }

    override fun decodeFromSource(stream: Source, config: CodecConfig?): ByteArray = stream.readByteArray()
}

private fun String.toAsciiKey() = GrpcMetadataKey(this, AsciiCodec)
private fun String.toBinaryKey() = GrpcMetadataKey(this, BinaryCodec)

