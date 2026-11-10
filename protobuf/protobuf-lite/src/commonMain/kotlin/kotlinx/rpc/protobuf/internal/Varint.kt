/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

/**
 * Varint sizes and encoding.
 *
 * The functions have these roles:
 *
 * - `bits`: the varint bits of a field type. It is the only mapping from a field type to its encoding.
 * - `sizeOf`: one overload for each varint type in `VarintTypes.kt`. No overload takes a plain `Int` or
 *   `Long`, so the caller must name the field type.
 * - `write`: writes [UInt32] or [UInt64] bits from `index` and returns the next index. The caller makes
 *   sure that the window has `sizeOf(value)` free bytes from `index`.
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

    fun write(window: ByteWindow, index: Int, value: UInt32): Int {
        var i = index
        var v = value.value
        while (v and 0x7F.inv() != 0) {
            window.setByte(i++, ((v and 0x7F) or 0x80).toByte())
            v = v ushr 7
        }
        window.setByte(i++, v.toByte())
        return i
    }

    fun write(window: ByteWindow, index: Int, value: UInt64): Int {
        var i = index
        var v = value.value
        while (v and 0x7F.inv() != 0L) {
            window.setByte(i++, ((v and 0x7FL).toInt() or 0x80).toByte())
            v = v ushr 7
        }
        window.setByte(i++, v.toByte())
        return i
    }
}
