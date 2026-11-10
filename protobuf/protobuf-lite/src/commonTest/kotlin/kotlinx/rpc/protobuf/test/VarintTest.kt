/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.internal.ByteWindow
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
    fun writeUnsigned() {
        checkWrite(byteArrayOf(0xAC.toByte(), 0x02)) { window -> Varint.write(window, 0, UInt32(300)) }
        checkWrite(byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x0F)) { window ->
            Varint.write(window, 0, UInt32(-1))
        }
        checkWrite(byteArrayOf(0xAC.toByte(), 0x02)) { window -> Varint.write(window, 0, UInt64(300L)) }
        checkWrite(ByteArray(9) { 0xFF.toByte() } + byteArrayOf(0x01)) { window ->
            Varint.write(window, 0, UInt64(-1L))
        }
    }

    @Test
    fun writeBitsOfSignedTypes() {
        val minusOne64 = ByteArray(9) { 0xFF.toByte() } + byteArrayOf(0x01)
        checkWrite(byteArrayOf(0xAC.toByte(), 0x02)) { window -> Varint.write(window, 0, Varint.bits(Int32(300))) }
        checkWrite(minusOne64) { window -> Varint.write(window, 0, Varint.bits(Int32(-1))) }
        checkWrite(minusOne64) { window -> Varint.write(window, 0, Varint.bits(Int64(-1L))) }
        checkWrite(byteArrayOf(0x01)) { window -> Varint.write(window, 0, Varint.bits(SInt32(-1))) }
        checkWrite(byteArrayOf(0x02)) { window -> Varint.write(window, 0, Varint.bits(SInt32(1))) }
        checkWrite(byteArrayOf(0x01)) { window -> Varint.write(window, 0, Varint.bits(SInt64(-1L))) }
        checkWrite(minusOne64) { window -> Varint.write(window, 0, Varint.bits(SInt64(Long.MIN_VALUE))) }
    }

    @Test
    fun nonNegativeInt32HasUInt32Bytes() {
        for (value in listOf(0, 1, 127, 128, 300, 16384, Int.MAX_VALUE)) {
            val expected = ByteArray(16)
            val expectedWindow = ByteWindow()
            expectedWindow.attach(expected, 0)
            val expectedEnd = Varint.write(expectedWindow, 0, UInt32(value))
            checkWrite(expected.copyOf(expectedEnd)) { window -> Varint.write(window, 0, Varint.bits(Int32(value))) }
            assertEquals(Varint.sizeOf(UInt32(value)), Varint.sizeOf(Int32(value)))
        }
    }

    @Test
    fun writeReturnsIndexAfterValue() {
        val array = ByteArray(16)
        val window = ByteWindow()
        window.attach(array, 0)
        assertEquals(3 + Varint.sizeOf(Int32(-1)), Varint.write(window, 3, Varint.bits(Int32(-1))))
        assertEquals(3 + Varint.sizeOf(SInt64(-300L)), Varint.write(window, 3, Varint.bits(SInt64(-300L))))
    }

    /** Checks that [write] writes [expected] from index 0 and returns the index after the last byte. */
    private fun checkWrite(expected: ByteArray, write: (ByteWindow) -> Int) {
        val array = ByteArray(16)
        val window = ByteWindow()
        window.attach(array, 0)
        val next = write(window)
        assertEquals(expected.size, next)
        assertContentEquals(expected, array.copyOf(next))
    }
}
