/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.io.UnsafeIoApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.ArrayInputSegments
import kotlinx.rpc.protobuf.internal.BufferInputSegments
import kotlinx.rpc.protobuf.internal.ReaderInputSegments
import kotlinx.rpc.protobuf.internal.UInt64
import kotlinx.rpc.protobuf.internal.WireInput
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** The chunk sizes that each test uses. The small sizes make every slow path run. */
private val CHUNK_SIZES = listOf(1, 2, 3, 7, 8192)

class WireInputTest {
    @Test
    fun varints() {
        val values = listOf(0L, 1L, 127L, 128L, 300L, Long.MAX_VALUE, -1L, Long.MIN_VALUE)
        val input = varintBytes(values)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                for (value in values) {
                    assertEquals(value, it.readVarint64())
                }
                assertTrue(it.isAtLimit)
            }
        }
    }

    @Test
    fun malformedVarintThrows() {
        val input = ByteArray(10) { 0x80.toByte() } + byteArrayOf(0x01)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                assertFailsWith<ProtobufDecodingException> { it.readVarint64() }
            }
        }
    }

    @Test
    fun tagVarints() {
        for (chunk in CHUNK_SIZES) {
            wireInput(bytes(0x08), chunk) {
                assertEquals(8, it.readTagVarint())
            }
            wireInput(bytes(0x88, 0x00), chunk) {
                val failure = assertFailsWith<ProtobufDecodingException> { it.readTagVarint() }
                assertEquals("Overlong varint encoding for tag", failure.message)
            }
            wireInput(bytes(0xFF, 0xFF, 0xFF, 0xFF, 0x0F), chunk) {
                assertEquals(-1, it.readTagVarint())
            }
            wireInput(bytes(0xFF, 0xFF, 0xFF, 0xFF, 0x1F), chunk) {
                assertFailsWith<ProtobufDecodingException> { it.readTagVarint() }
            }
            wireInput(bytes(0x80, 0x80, 0x80, 0x80, 0x80, 0x01), chunk) {
                assertFailsWith<ProtobufDecodingException> { it.readTagVarint() }
            }
        }
    }

    @Test
    fun fixed32AndFixed64() {
        val input = bytes(4, 3, 2, 1, 8, 7, 6, 5, 4, 3, 2, 1)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                assertEquals(0x01020304, it.readFixed32())
                assertEquals(0x0102030405060708L, it.readFixed64())
                assertTrue(it.isAtLimit)
            }
        }
    }

    @Test
    fun bytesAndUtf8AcrossSegments() {
        val string = "a😊b"
        val input = string.encodeToByteArray()
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                assertEquals(string, it.readUtf8(input.size))
                assertEquals("", it.readUtf8(0))
            }
            wireInput(input, chunk) {
                assertContentEquals(input, it.readBytes(input.size))
            }
        }
    }

    @Test
    fun pushAndPopLimit() {
        val input = bytes(1, 2, 3, 4, 5)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                assertFailsWith<ProtobufDecodingException> { it.pushLimit(-1) }
                assertFailsWith<ProtobufDecodingException> { it.pushLimit(6) }
                val oldLimit = it.pushLimit(3)
                assertEquals(1, it.readByte())
                assertEquals(2, it.readByte())
                assertEquals(3, it.readByte())
                assertTrue(it.isAtLimit)
                assertEquals(0, it.bytesUntilLimit)
                assertFailsWith<ProtobufDecodingException> { it.readByte() }
                it.popLimit(oldLimit)
                assertEquals(2, it.bytesUntilLimit)
                assertEquals(4, it.readByte())
                assertEquals(5, it.readByte())
                assertTrue(it.isAtLimit)
            }
        }
    }

    @Test
    fun invalidCountsThrow() {
        val input = bytes(1, 2, 3)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                assertFailsWith<ProtobufDecodingException> { it.readBytes(-1) }
                assertFailsWith<ProtobufDecodingException> { it.readBytes(Int.MAX_VALUE) }
                assertFailsWith<ProtobufDecodingException> { it.skip(-1) }
            }
        }
    }

    @Test
    fun inputShorterThanSizeThrows() {
        val input = bytes(1, 2, 3)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk, size = 5) {
                assertEquals(1, it.readByte())
                assertEquals(2, it.readByte())
                assertEquals(3, it.readByte())
                assertFailsWith<ProtobufDecodingException> { it.readByte() }
            }
        }
    }

    @Test
    fun skipMovesOverBytes() {
        val input = bytes(1, 2, 3, 4, 5)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                it.skip(2)
                assertEquals(3, it.readByte())
                it.skip(2)
                assertTrue(it.isAtLimit)
            }
        }
    }

    @Test
    fun readAfterCloseThrows() {
        val input = WireInput(SplitInputSegments(bytes(1), 1), 1)
        input.close()
        input.close()
        assertFailsWith<IllegalStateException> { input.readByte() }
    }

    @Test
    fun bufferInputSegments() {
        // A varint 300, a fixed32 0x01020304, and the string "Grüße".
        val input = bytes(0xAC, 0x02, 4, 3, 2, 1, 0x47, 0x72, 0xC3, 0xBC, 0xC3, 0x9F, 0x65)
        for (chunk in CHUNK_SIZES) {
            val buffer = bufferOf(input, chunk)
            UnsafeBufferOperations.iterate(buffer) { context, head ->
                WireInput(BufferInputSegments(context, head), input.size).use { wireInput ->
                    assertEquals(300L, wireInput.readVarint64())
                    assertEquals(0x01020304, wireInput.readFixed32())
                    assertEquals("Grüße", wireInput.readUtf8(7))
                    assertTrue(wireInput.isAtLimit)
                }
            }
        }
    }

    @Test
    fun bufferInputSegmentsSkipEmptySegment() {
        val buffer = Buffer()
        UnsafeBufferOperations.moveToTail(buffer, bytes(0xAC, 0x02))
        UnsafeBufferOperations.moveToTail(buffer, ByteArray(1), 0, 0)
        UnsafeBufferOperations.moveToTail(buffer, bytes(4, 3, 2, 1))
        UnsafeBufferOperations.iterate(buffer) { context, head ->
            WireInput(BufferInputSegments(context, head), 6).use { input ->
                assertEquals(300L, input.readVarint64())
                assertEquals(0x01020304, input.readFixed32())
                assertTrue(input.isAtLimit)
            }
        }
    }

    @Test
    fun readsPastLimitThrow() {
        // A varint of 4 bytes, then 12 more bytes.
        val varint = bytes(0x80, 0x80, 0x80, 0x01) + ByteArray(12) { 1 }
        assertTruncatedAtLimit(varint, 3) { it.readVarint64() }
        assertTruncatedAtLimit(varint, 3) { it.readTagVarint() }
        val data = ByteArray(16) { 1 }
        assertTruncatedAtLimit(data, 3) { it.readFixed32() }
        assertTruncatedAtLimit(data, 7) { it.readFixed64() }
        assertTruncatedAtLimit(data, 3) { it.readBytes(4) }
        assertTruncatedAtLimit(data, 3) { it.readUtf8(4) }
        assertTruncatedAtLimit(data, 3) { it.skip(4) }
    }

    @Test
    fun readsThatEndAtLimitSucceed() {
        val data = "abcdefgh".encodeToByteArray() + ByteArray(8) { 1 }
        for (chunk in CHUNK_SIZES) {
            wireInput(data, chunk) {
                it.pushLimit(4)
                assertEquals(0x64636261, it.readFixed32())
                assertTrue(it.isAtLimit)
            }
            wireInput(data, chunk) {
                it.pushLimit(8)
                assertEquals(0x6867666564636261L, it.readFixed64())
                assertTrue(it.isAtLimit)
            }
            wireInput(data, chunk) {
                it.pushLimit(4)
                assertEquals("abcd", it.readUtf8(4))
                assertTrue(it.isAtLimit)
            }
            wireInput(data, chunk) {
                it.pushLimit(4)
                assertContentEquals("abcd".encodeToByteArray(), it.readBytes(4))
                assertTrue(it.isAtLimit)
            }
            wireInput(data, chunk) {
                it.pushLimit(4)
                it.skip(4)
                assertTrue(it.isAtLimit)
            }
        }
    }

    @Test
    fun nestedLimits() {
        val input = bytes(1, 2, 3, 4, 5, 6, 7, 8)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                val outer = it.pushLimit(6)
                assertEquals(1, it.readByte())
                val inner = it.pushLimit(2)
                assertFailsWith<ProtobufDecodingException> { it.pushLimit(3) }
                assertEquals(2, it.readByte())
                assertEquals(3, it.readByte())
                assertTrue(it.isAtLimit)
                it.popLimit(inner)
                assertEquals(3, it.bytesUntilLimit)
                it.skip(3)
                assertTrue(it.isAtLimit)
                assertFailsWith<ProtobufDecodingException> { it.readByte() }
                it.popLimit(outer)
                assertEquals(7, it.readByte())
                assertEquals(8, it.readByte())
            }
        }
    }

    @Test
    fun invalidUtf8AcrossSegmentsIsRejected() {
        // The 3-byte sequence E2 82 has no third byte inside the string. The decoder must not read the 'b' after it.
        val input = bytes(0x61, 0xE2, 0x82, 0x62, 0x63)
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                val failure = assertFailsWith<ProtobufDecodingException> { it.readUtf8(3) }
                assertEquals(ProtobufDecodingException.invalidUtf8().message, failure.message)
            }
        }
    }

    @Test
    fun arrayInputSegmentsReadTheRange() {
        val array = bytes(9, 9, 1, 2, 3, 4, 9, 9)
        WireInput(ArrayInputSegments(array, 2, 6), 4).use {
            assertContentEquals(bytes(1, 2, 3, 4), it.readBytes(4))
            assertTrue(it.isAtLimit)
            assertFailsWith<ProtobufDecodingException> { it.readByte() }
        }
        WireInput(ArrayInputSegments(array, 3, 3), 0).use {
            assertTrue(it.isAtLimit)
            assertFailsWith<ProtobufDecodingException> { it.readByte() }
        }
    }

    @Test
    fun readerInputSegmentsReadAllSizes() {
        for (size in listOf(0, 1, 8191, 8192, 8193, 20_000)) {
            val input = ByteArray(size) { (it % 251).toByte() }
            WireInput(ReaderInputSegments(ByteArrayMessageReader(input)), size).use {
                assertContentEquals(input, it.readBytes(size))
                assertTrue(it.isAtLimit)
                assertFailsWith<ProtobufDecodingException> { it.readByte() }
            }
        }
    }

    @Test
    fun readerInputSegmentsReadAcrossTheScratchArray() {
        val input = ByteArray(8200) { (it % 251).toByte() }
        var expected = 0L
        for (i in 0 until 8) {
            expected = expected or ((input[8188 + i].toLong() and 0xFF) shl (8 * i))
        }
        WireInput(ReaderInputSegments(ByteArrayMessageReader(input)), input.size).use {
            it.skip(8188)
            assertEquals(expected, it.readFixed64())
            assertEquals(4, it.bytesUntilLimit)
        }
    }

    private fun <R> wireInput(
        input: ByteArray,
        chunk: Int,
        size: Int = input.size,
        block: (WireInput) -> R,
    ): R {
        return WireInput(SplitInputSegments(input, chunk), size).use(block)
    }

    private fun assertTruncatedAtLimit(input: ByteArray, limit: Int, read: (WireInput) -> Unit) {
        for (chunk in CHUNK_SIZES) {
            wireInput(input, chunk) {
                it.pushLimit(limit)
                val failure = assertFailsWith<ProtobufDecodingException> { read(it) }
                assertEquals(ProtobufDecodingException.truncatedMessage().message, failure.message)
            }
        }
    }

    private fun varintBytes(values: List<Long>): ByteArray =
        values.fold(ByteArray(0)) { result, value -> result + varintBytes(UInt64(value)) }
}
