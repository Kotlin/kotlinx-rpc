/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)

package kotlinx.rpc.grpc

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.rpc.grpc.internal.toByteArray
import kotlinx.rpc.internal.utils.InternalRpcApi
import libkgrpc.grpc_metadata
import libkgrpc.grpc_metadata_array
import libkgrpc.grpc_metadata_array_init
import libkgrpc.grpc_slice_from_copied_buffer
import libkgrpc.grpc_slice_from_copied_string
import libkgrpc.grpc_slice_ref
import libkgrpc.grpc_slice_unref
import libkgrpc.kgrpc_metadata_array_append
import kotlin.experimental.ExperimentalNativeApi

private value class GrpcKey private constructor(val name: String) {
    val isBinary get() = name.endsWith("-bin")

    companion object {
        fun binary(name: String): GrpcKey {
            val key = GrpcKey(validateName(name.lowercase()))
            require(key.isBinary) { "Binary header is named ${key.name}. It must end with '-bin'" }
            return key
        }

        fun string(name: String): GrpcKey {
            val key = GrpcKey(validateName(name.lowercase()))
            require(!key.isBinary) { "String header is named ${key.name}. It must not end with '-bin'" }
            return key
        }
    }
}

@Suppress(names = ["RedundantConstructorKeyword"])
public actual class GrpcMetadata actual constructor() {
    internal val map: LinkedHashMap<String, MutableList<ByteArray>> = linkedMapOf()

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
                    grpc_slice_from_copied_buffer(pinned.addressOf(0), size)
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
}

public actual operator fun GrpcMetadata.get(key: String): String? {
    return map[GrpcKey.string(key).name]?.lastOrNull()?.toAsciiString()
}

public actual fun GrpcMetadata.getBinary(key: String): ByteArray? {
    return map[GrpcKey.binary(key).name]?.lastOrNull()
}

public actual fun GrpcMetadata.getAll(key: String): List<String> {
    return map[GrpcKey.string(key).name]?.map { it.toAsciiString() } ?: emptyList()
}

public actual fun GrpcMetadata.getAllBinary(key: String): List<ByteArray> {
    return map[GrpcKey.binary(key).name]?.map { it } ?: emptyList()
}

public actual operator fun GrpcMetadata.contains(key: String): Boolean {
    return map.containsKey(key.lowercase())
}

public actual fun GrpcMetadata.keys(): Set<String> {
    return map.entries.filter { it.value.isNotEmpty() }.mapTo(mutableSetOf()) { it.key }
}

public actual fun GrpcMetadata.append(key: String, value: String) {
    val k = GrpcKey.string(key) // non-bin key
    map.getOrPut(k.name) { mutableListOf() }.add(value.toAsciiBytes())
}

public actual fun GrpcMetadata.appendBinary(key: String, value: ByteArray) {
    val k = GrpcKey.binary(key)
    map.getOrPut(k.name) { mutableListOf() }.add(value)
}

public actual fun GrpcMetadata.remove(key: String, value: String): Boolean {
    val index = getAll(key).indexOf(value)
    if (index == -1) return false
    map[GrpcKey.string(key).name]!!.removeAt(index)
    return true
}

public actual fun GrpcMetadata.removeBinary(key: String, value: ByteArray): Boolean {
    val index = getAllBinary(key).indexOf(value)
    if (index == -1) return false
    map[GrpcKey.binary(key).name]!!.removeAt(index)
    return true
}

public actual fun GrpcMetadata.removeAll(key: String): List<String> {
    return map.remove(GrpcKey.string(key).name)?.map { it.toAsciiString() } ?: emptyList()
}

public actual fun GrpcMetadata.removeAllBinary(key: String): List<ByteArray> {
    return map.remove(GrpcKey.binary(key).name) ?: emptyList()
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
private fun GrpcKey.Companion.validateName(name: String): String {
    require(!name.startsWith("grpc-")) { "Header is named $name. It must not start with 'grpc-' as it is reserved for internal use." }

    for (char in name) {
        require(VALID_KEY_CHARS[char.code]) { "Header is named $name. It contains illegal character $char." }
    }

    return name
}