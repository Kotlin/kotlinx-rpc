/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal.protowire

internal enum class WireType {
    VARINT, // 0
    FIXED64, // 1
    LENGTH_DELIMITED, // 2
    START_GROUP, // 3
    END_GROUP, // 4
    FIXED32, // 5
}

internal data class KTag(val fieldNr: Int, val wireType: WireType) {


    companion object {
        // Number of bits in a tag which identify the wire type.
        const val K_TAG_TYPE_BITS: UInt = 3u;
        // Mask for those bits. (just 0b111)
        val K_TAG_TYPE_MASK: UInt = (1u shl K_TAG_TYPE_BITS.toInt()) - 1u
    }
}

internal fun KTag.Companion.from(rawKTag: UInt): KTag? {
    val type = rawKTag and K_TAG_TYPE_MASK
    val field = rawKTag shr K_TAG_TYPE_BITS.toInt()
    if (type >= WireType.entries.size.toUInt()) {
        return null
    }
    return KTag(field.toInt(), WireType.entries[type.toInt()])
}