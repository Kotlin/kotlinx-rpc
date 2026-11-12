/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.protobuf.ProtobufEncodingException
import kotlinx.rpc.protobuf.internal.ArrayOutputSegments
import kotlinx.rpc.protobuf.internal.BufferOutputSegments
import kotlinx.rpc.protobuf.internal.UInt32
import kotlinx.rpc.protobuf.internal.UNKNOWN_SIZE
import kotlinx.rpc.protobuf.internal.WireOutput
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WireStringTest {
    @Test
    fun lengthPrefixesAcrossSegments() {
        val lengths = listOf(
            0, 1, 31, 32, 42, 43, 63, 64, 127, 128, 500, 2730, 2731,
            5461, 5462, 8192, 16383, 16384,
        )
        val patterns = listOf(
            "a" to bytes(0x61),
            "ü" to bytes(0xC3, 0xBC),
            "你" to bytes(0xE4, 0xBD, 0xA0),
            "🙂" to bytes(0xF0, 0x9F, 0x99, 0x82),
            "\uD800\uDC00" to bytes(0xF0, 0x90, 0x80, 0x80),
            "\uD800" to bytes(0x3F),
            "\uDC00" to bytes(0x3F),
        )
        for (length in lengths) {
            for ((pattern, encoded) in patterns) {
                val value = pattern.repeat(length)
                val payload = ByteArray(encoded.size * length) { encoded[it % encoded.size] }
                val expected = bytes(7) + varintBytes(UInt32(payload.size)) + payload + bytes(42)
                checkArray(value, expected, value.length * 3 + 20)
                checkArray(value, expected, expected.size)
                for (chunk in listOf(1, 2, 3, 7, 128, 8192)) {
                    val segments = SplitOutputSegments(expected.size, chunk)
                    WireOutput(segments).use { writeString(it, value, expected.size) }
                    assertContentEquals(expected, segments.bytes())
                }
                for (size in listOf(expected.size, UNKNOWN_SIZE)) {
                    val buffer = Buffer()
                    WireOutput(BufferOutputSegments(buffer, size)).use { writeString(it, value, expected.size) }
                    assertContentEquals(expected, buffer.readByteArray())
                }
            }
        }
    }

    @Test
    fun stringsThatDontFitThrow() {
        for (value in listOf("", "abc", "ü", "你好", "🙂", "a".repeat(128))) {
            val size = value.encodeToByteArray().size
            val capacity = varintBytes(UInt32(size)).size + size - 1
            for (chunk in listOf(1, 2, 7, 8192)) {
                WireOutput(SplitOutputSegments(capacity, chunk)).use { output ->
                    assertFailsWith<ProtobufEncodingException> { output.writeStringLengthDelimited(value) }
                }
            }
        }
    }

    @Test
    fun stringPrefixesContinueAfterFlush() {
        val array = ByteArray(64)
        WireOutput(ArrayOutputSegments(array, 2, array.size - 2)).use { output ->
            output.writeStringLengthDelimited("abc")
            output.flush()
            output.writeStringLengthDelimited("你好")
            output.flush()
            output.writeStringLengthDelimited("")
            output.flush()
            val expected = bytes(3, 0x61, 0x62, 0x63, 6, 0xE4, 0xBD, 0xA0, 0xE5, 0xA5, 0xBD, 0)
            assertEquals(expected.size, output.written)
            assertContentEquals(expected, array.copyOfRange(2, 2 + output.written))
        }
    }

    private fun checkArray(value: String, expected: ByteArray, capacity: Int) {
        val array = ByteArray(capacity + 4) { 9 }
        WireOutput(ArrayOutputSegments(array, 2, capacity + 2)).use { writeString(it, value, expected.size) }
        assertContentEquals(expected, array.copyOfRange(2, 2 + expected.size))
        assertContentEquals(bytes(9, 9), array.copyOfRange(0, 2))
        assertContentEquals(bytes(9, 9), array.copyOfRange(capacity + 2, array.size))
    }

    private fun writeString(output: WireOutput, value: String, expectedSize: Int) {
        output.writeByte(7)
        output.writeStringLengthDelimited(value)
        output.writeByte(42)
        output.flush()
        assertEquals(expectedSize, output.written)
    }
}
