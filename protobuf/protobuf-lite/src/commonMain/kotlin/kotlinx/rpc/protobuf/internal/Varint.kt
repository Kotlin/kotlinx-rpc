/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.protobuf.ProtobufDecodingException

/**
 * Varint sizes, encoding, and decoding. The functions have these roles:
 *
 * - `bits`: the varint bits of a field type. It is the only mapping from a field type to its encoding.
 * - `sizeOf`: the size in bytes. No overload takes a plain `Int` or `Long`, so the caller must name the field type.
 * - `encode`: gives the bytes of a [UInt32] or [UInt64] to a function. The caller chooses where the bytes go.
 * - `decode`: reads a varint of at most [MAX_SIZE_64] bytes from a function. The caller chooses where the bytes
 *   come from.
 */
internal object Varint {
    const val MAX_SIZE_32: Int = 5
    const val MAX_SIZE_64: Int = 10

    fun bits(value: Int32): UInt64 = UInt64(value.value.toLong())

    fun bits(value: Int64): UInt64 = UInt64(value.value)

    fun bits(value: SInt32): UInt32 = ZigZag.encode(value)

    fun bits(value: SInt64): UInt64 = ZigZag.encode(value)

    fun sizeOf(value: UInt32): Int = (352 - value.value.countLeadingZeroBits() * 9) ushr 6

    fun sizeOf(value: UInt64): Int = (640 - value.value.countLeadingZeroBits() * 9) ushr 6

    fun sizeOf(value: Int32): Int = sizeOf(bits(value))

    fun sizeOf(value: Int64): Int = sizeOf(bits(value))

    fun sizeOf(value: SInt32): Int = sizeOf(bits(value))

    fun sizeOf(value: SInt64): Int = sizeOf(bits(value))

    /** Gives the bytes of the varint of [value] to [put], from the first to the last. */
    inline fun encode(value: UInt32, put: (Byte) -> Unit) {
        var v = value.value
        while (v and 0x7F.inv() != 0) {
            put(((v and 0x7F) or 0x80).toByte())
            v = v ushr 7
        }
        put(v.toByte())
    }

    /** Gives the bytes of the varint of [value] to [put], from the first to the last. */
    inline fun encode(value: UInt64, put: (Byte) -> Unit) {
        var v = value.value
        while (v and 0x7F.inv() != 0L) {
            put(((v and 0x7FL).toInt() or 0x80).toByte())
            v = v ushr 7
        }
        put(v.toByte())
    }

    /**
     * Reads a varint from [get] and returns its 64 bits.
     *
     * [get] gives each byte one time, from the first to the last. The function calls it at most
     * [MAX_SIZE_64] times, and throws [ProtobufDecodingException] if the varint is longer.
     */
    inline fun decode(get: () -> Byte): Long {
        var result = 0L
        var shift = 0
        while (true) {
            val b = get()
            result = result or ((b.toLong() and 0x7F) shl shift)
            if (b >= 0) {
                return result
            }
            shift += 7
            if (shift == 7 * MAX_SIZE_64) {
                throw ProtobufDecodingException.malformedVarint()
            }
        }
    }
}
