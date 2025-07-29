/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

public enum class WireType {
    VARINT, // 0
    FIXED64, // 1
    LENGTH_DELIMITED, // 2
    START_GROUP, // 3
    END_GROUP, // 4
    FIXED32, // 5
}

public data class KTag(val fieldNr: Int, val wireType: WireType) {

    init {
        check(isValidFieldNr(fieldNr)) { "Invalid field number: $fieldNr" }
    }

    internal companion object {
        // Number of bits in a tag which identify the wire type.
        const val K_TAG_TYPE_BITS: Int = 3;

        // Mask for those bits. (just 0b111)
        val K_TAG_TYPE_MASK: UInt = (1u shl K_TAG_TYPE_BITS) - 1u
    }
}

internal fun KTag.toRawKTag(): UInt {
    return (fieldNr.toUInt() shl KTag.Companion.K_TAG_TYPE_BITS) or wireType.ordinal.toUInt()
}

internal fun KTag.Companion.fromOrNull(rawKTag: UInt): KTag? {
    val type = (rawKTag and K_TAG_TYPE_MASK).toInt()
    val field = (rawKTag shr K_TAG_TYPE_BITS).toInt()
    if (!isValidFieldNr(field)) {
        return null
    }
    if (type >= WireType.entries.size) {
        return null
    }
    return KTag(field, WireType.entries[type])
}

internal fun KTag.Companion.isValidFieldNr(fieldNr: Int): Boolean {
    return 1 <= fieldNr && fieldNr <= 536_870_911
}