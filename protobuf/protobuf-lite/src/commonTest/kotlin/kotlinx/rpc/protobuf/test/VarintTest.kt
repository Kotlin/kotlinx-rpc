/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.Int32
import kotlinx.rpc.protobuf.internal.Int64
import kotlinx.rpc.protobuf.internal.SInt32
import kotlinx.rpc.protobuf.internal.SInt64
import kotlinx.rpc.protobuf.internal.UInt32
import kotlinx.rpc.protobuf.internal.UInt64
import kotlinx.rpc.protobuf.internal.Varint
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VarintTest {
    @Test
    fun sizeOfUInt32() {
        assertEquals(1, Varint.sizeOf(UInt32(0)))
        assertEquals(1, Varint.sizeOf(UInt32(1)))
        assertEquals(1, Varint.sizeOf(UInt32(127)))
        assertEquals(2, Varint.sizeOf(UInt32(128)))
        assertEquals(2, Varint.sizeOf(UInt32(16383)))
        assertEquals(3, Varint.sizeOf(UInt32(16384)))
        assertEquals(5, Varint.sizeOf(UInt32(Int.MAX_VALUE)))
        assertEquals(5, Varint.sizeOf(UInt32(-1)))
    }

    @Test
    fun sizeOfUInt64() {
        assertEquals(1, Varint.sizeOf(UInt64(0L)))
        assertEquals(1, Varint.sizeOf(UInt64(127L)))
        assertEquals(2, Varint.sizeOf(UInt64(128L)))
        assertEquals(9, Varint.sizeOf(UInt64(Long.MAX_VALUE)))
        assertEquals(10, Varint.sizeOf(UInt64(-1L)))
    }

    @Test
    fun sizeOfSignedTypes() {
        assertEquals(1, Varint.sizeOf(Int32(0)))
        assertEquals(5, Varint.sizeOf(Int32(Int.MAX_VALUE)))
        assertEquals(Varint.MAX_SIZE_64, Varint.sizeOf(Int32(-1)))
        assertEquals(Varint.MAX_SIZE_64, Varint.sizeOf(Int32(Int.MIN_VALUE)))
        assertEquals(Varint.MAX_SIZE_64, Varint.sizeOf(Int64(-1L)))
        assertEquals(1, Varint.sizeOf(SInt32(-1)))
        assertEquals(5, Varint.sizeOf(SInt32(Int.MIN_VALUE)))
        assertEquals(1, Varint.sizeOf(SInt64(-1L)))
        assertEquals(Varint.MAX_SIZE_64, Varint.sizeOf(SInt64(Long.MIN_VALUE)))
    }

    @Test
    fun encodeUnsigned() {
        assertContentEquals(bytes(0xAC, 0x02), varintBytes(UInt32(300)))
        assertContentEquals(bytes(0xFF, 0xFF, 0xFF, 0xFF, 0x0F), varintBytes(UInt32(-1)))
        assertContentEquals(bytes(0xAC, 0x02), varintBytes(UInt64(300L)))
        assertContentEquals(ByteArray(9) { 0xFF.toByte() } + bytes(0x01), varintBytes(UInt64(-1L)))
    }

    @Test
    fun encodeBitsOfSignedTypes() {
        val minusOne64 = ByteArray(9) { 0xFF.toByte() } + bytes(0x01)
        assertContentEquals(bytes(0xAC, 0x02), varintBytes(Varint.bits(Int32(300))))
        assertContentEquals(minusOne64, varintBytes(Varint.bits(Int32(-1))))
        assertContentEquals(minusOne64, varintBytes(Varint.bits(Int64(-1L))))
        assertContentEquals(bytes(0x01), varintBytes(Varint.bits(SInt32(-1))))
        assertContentEquals(bytes(0x02), varintBytes(Varint.bits(SInt32(1))))
        assertContentEquals(bytes(0x01), varintBytes(Varint.bits(SInt64(-1L))))
        assertContentEquals(minusOne64, varintBytes(Varint.bits(SInt64(Long.MIN_VALUE))))
    }

    @Test
    fun nonNegativeInt32HasUInt32Bytes() {
        for (value in listOf(0, 1, 127, 128, 300, 16384, Int.MAX_VALUE)) {
            assertContentEquals(varintBytes(UInt32(value)), varintBytes(Varint.bits(Int32(value))))
            assertEquals(Varint.sizeOf(UInt32(value)), Varint.sizeOf(Int32(value)))
        }
    }

    @Test
    fun encodedSizeIsSizeOf() {
        for (value in listOf(0, 1, 127, 128, 300, 16383, 16384, Int.MAX_VALUE, -1, Int.MIN_VALUE)) {
            assertEquals(Varint.sizeOf(UInt32(value)), varintBytes(UInt32(value)).size)
            assertEquals(Varint.sizeOf(Int32(value)), varintBytes(Varint.bits(Int32(value))).size)
            assertEquals(Varint.sizeOf(SInt32(value)), varintBytes(Varint.bits(SInt32(value))).size)
        }
        for (value in listOf(0L, 127L, 128L, -300L, Long.MAX_VALUE, -1L, Long.MIN_VALUE)) {
            assertEquals(Varint.sizeOf(UInt64(value)), varintBytes(UInt64(value)).size)
            assertEquals(Varint.sizeOf(Int64(value)), varintBytes(Varint.bits(Int64(value))).size)
            assertEquals(Varint.sizeOf(SInt64(value)), varintBytes(Varint.bits(SInt64(value))).size)
        }
    }

    @Test
    fun decode() {
        assertEquals(0L, decode(0x00))
        assertEquals(127L, decode(0x7F))
        assertEquals(300L, decode(0xAC, 0x02))
        assertEquals(0xFFFFFFFFL, decode(0xFF, 0xFF, 0xFF, 0xFF, 0x0F))
        assertEquals(-1L, decode(0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01))
        assertEquals(Long.MIN_VALUE, decode(0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x80, 0x01))
    }

    @Test
    fun decodeInvertsEncode() {
        for (value in listOf(0, 1, 127, 128, 300, 16384, Int.MAX_VALUE, -1, Int.MIN_VALUE)) {
            var i = 0
            val encoded = varintBytes(UInt32(value))
            assertEquals(value.toLong() and 0xFFFFFFFFL, Varint.decode { encoded[i++] })
            assertEquals(encoded.size, i)
        }
        for (value in listOf(0L, 1L, 127L, 128L, 300L, Long.MAX_VALUE, -1L, Long.MIN_VALUE)) {
            var i = 0
            val encoded = varintBytes(UInt64(value))
            assertEquals(value, Varint.decode { encoded[i++] })
            assertEquals(encoded.size, i)
        }
    }

    @Test
    fun decodeStopsAfterTenBytes() {
        var calls = 0
        assertFailsWith<ProtobufDecodingException> {
            Varint.decode {
                calls++
                0x80.toByte()
            }
        }
        assertEquals(Varint.MAX_SIZE_64, calls)
    }

    private fun decode(vararg input: Int): Long {
        var i = 0
        return Varint.decode { input[i++].toByte() }
    }
}
