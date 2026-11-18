/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.ByteWindow
import kotlinx.rpc.protobuf.internal.Utf8
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

class Utf8Test {
    @Test
    fun wellFormedStrings() {
        for (value in listOf("", "abc", "ü", "€", "😊", "Grüße, 你好, привет, 🙂")) {
            val bytes = value.encodeToByteArray()
            checkEncode(value, bytes)
            checkDecode(bytes, value)
        }
    }

    @Test
    fun loneSurrogates() {
        val cases = listOf(
            "a\uD800b" to bytes(0x61, 0x3F, 0x62),
            "a\uD800" to bytes(0x61, 0x3F),
            "\uDC00" to bytes(0x3F),
            "\uDC00\uD800" to bytes(0x3F, 0x3F),
            "\uD800\u00E9" to bytes(0x3F, 0xC3, 0xA9),
            "\uD800\uD800\uDC00" to bytes(0x3F, 0xF0, 0x90, 0x80, 0x80),
        )
        for ((value, expected) in cases) {
            checkEncode(value, expected)
        }
    }

    @Test
    fun validBoundaries() {
        val cases = listOf(
            "\u0080" to bytes(0xC2, 0x80),
            "\u07FF" to bytes(0xDF, 0xBF),
            "\u0800" to bytes(0xE0, 0xA0, 0x80),
            "\uD7FF" to bytes(0xED, 0x9F, 0xBF),
            "\uE000" to bytes(0xEE, 0x80, 0x80),
            "\uFFFF" to bytes(0xEF, 0xBF, 0xBF),
            "\uD800\uDC00" to bytes(0xF0, 0x90, 0x80, 0x80),
            "\uDBFF\uDFFF" to bytes(0xF4, 0x8F, 0xBF, 0xBF),
        )
        for ((value, expected) in cases) {
            checkEncode(value, expected)
            checkDecode(expected, value)
        }
    }

    @Test
    fun invalidInputThrows() {
        val cases = listOf(
            bytes(0xFF),
            bytes(0x80),
            bytes(0xC2),
            bytes(0xC0, 0x80),
            bytes(0xC1, 0xBF),
            bytes(0xC3, 0x28),
            bytes(0xE0, 0x80, 0x80),
            bytes(0xE0, 0x9F, 0xBF),
            bytes(0xED, 0xA0, 0x80),
            bytes(0xED, 0xBF, 0xBF),
            bytes(0xE2, 0x82),
            bytes(0xE2, 0x82, 0x28),
            bytes(0xF0, 0x80, 0x80, 0x80),
            bytes(0xF0, 0x90, 0x80),
            bytes(0xF0, 0x90, 0x80, 0x28),
            bytes(0xF4, 0x90, 0x80, 0x80),
            bytes(0xF4, 0x8F, 0xBF, 0xC0),
            bytes(0xF5, 0x80, 0x80, 0x80),
            bytes(0x61, 0x80),
            bytes(0x61, 0xE2, 0x82),
        )
        for (bytes in cases) {
            checkDecodeFails(bytes)
        }
    }

    @Test
    fun allScalarValuesRoundTrip() {
        val value = allScalarValues()
        val bytes = value.encodeToByteArray()
        checkEncode(value, bytes)
        checkDecode(bytes, value)
    }

    @Test
    fun shortSequencesMatchUtf8Definition() {
        val chars = CharArray(4)
        forEachShortSequence { input -> checkAgainstDefinition(input, chars) }
    }

    /**
     * Checks that [Utf8.decode] accepts [input] only if the shortest-form definition of UTF-8 accepts it,
     * and that both give the same code points.
     */
    private fun checkAgainstDefinition(input: IntArray, chars: CharArray) {
        val bytes = ByteArray(input.size) { input[it].toByte() }
        val expected = decodeByDefinition(input)
        val actual = try {
            codePoints(chars.concatToString(0, Utf8.decode(bytes, 0, bytes.size, chars)))
        } catch (_: ProtobufDecodingException) {
            null
        }
        if (expected != actual) {
            fail("Input ${input.joinToString(" ") { it.toString(16) }}: expected $expected, got $actual.")
        }
    }

    /**
     * Decodes [input] by the definition of UTF-8, not by the lead byte table that [Utf8] uses. Returns `null`
     * if a sequence doesn't meet these conditions:
     *
     * - It has the number of continuation bytes that its lead byte gives.
     * - It is the shortest form of its code point.
     * - The code point isn't a surrogate and isn't above U+10FFFF.
     */
    private fun decodeByDefinition(input: IntArray): List<Int>? {
        val result = ArrayList<Int>()
        var i = 0
        while (i < input.size) {
            val b0 = input[i]
            val length = when {
                b0 < 0x80 -> 1
                b0 and 0xE0 == 0xC0 -> 2
                b0 and 0xF0 == 0xE0 -> 3
                b0 and 0xF8 == 0xF0 -> 4
                else -> return null
            }
            if (i + length > input.size) {
                return null
            }
            var codePoint = if (length == 1) { b0 } else { b0 and (0xFF ushr (length + 1)) }
            for (j in 1 until length) {
                if (input[i + j] and 0xC0 != 0x80) {
                    return null
                }
                codePoint = (codePoint shl 6) or (input[i + j] and 0x3F)
            }
            val minimum = intArrayOf(0, 0, 0x80, 0x800, 0x10000)[length]
            if (codePoint !in minimum..0x10FFFF || codePoint in 0xD800..0xDFFF) {
                return null
            }
            result.add(codePoint)
            i += length
        }
        return result
    }

    private fun codePoints(value: String): List<Int> {
        val result = ArrayList<Int>()
        var i = 0
        while (i < value.length) {
            if (value[i].isHighSurrogate()) {
                result.add(0x10000 + ((value[i].code - 0xD800) shl 10) + (value[i + 1].code - 0xDC00))
                i += 2
            } else {
                result.add(value[i].code)
                i++
            }
        }
        return result
    }

    @Test
    fun decodeReadsEachByteOnceAndNeverPastCount() {
        forEachShortSequence { input ->
            var calls = 0
            var rejected = false
            try {
                // A read past the input fails with an IndexOutOfBoundsException.
                Utf8.decode(input.size, CharArray(input.size)) { input[calls++].toByte() }
            } catch (_: ProtobufDecodingException) {
                rejected = true
            }
            assertTrue(calls <= input.size, "Input ${input.toList()}: $calls reads.")
            assertTrue(rejected || calls == input.size, "Input ${input.toList()}: $calls reads, accepted.")
        }
    }

    /** Checks [Utf8.encodedLength] and [Utf8.encode]. */
    private fun checkEncode(value: String, expected: ByteArray) {
        assertEquals(expected.size, Utf8.encodedLength(value))
        assertContentEquals(expected, utf8Bytes(value))
    }

    /** Checks both `decode` functions, at index 0 and at index 3. */
    private fun checkDecode(bytes: ByteArray, expected: String) {
        for (start in listOf(0, 3)) {
            val chars = CharArray(bytes.size)
            val array = ByteArray(start) + bytes
            assertEquals(expected, chars.concatToString(0, Utf8.decode(array, start, bytes.size, chars)))

            val window = windowOver(bytes, start)
            assertEquals(expected, chars.concatToString(0, Utf8.decode(window, start, bytes.size, chars)))
        }
    }

    /** Checks that both `decode` functions throw, at index 0 and at index 3. */
    private fun checkDecodeFails(bytes: ByteArray) {
        for (start in listOf(0, 3)) {
            val array = ByteArray(start) + bytes
            assertFailsWith<ProtobufDecodingException> {
                Utf8.decode(array, start, bytes.size, CharArray(bytes.size))
            }
            val window = windowOver(bytes, start)
            assertFailsWith<ProtobufDecodingException> {
                Utf8.decode(window, start, bytes.size, CharArray(bytes.size))
            }
        }
    }

    /**
     * Returns a window that starts at array offset 2 and has [bytes] from index [start]. It has one byte more,
     * because `attach` needs a non-empty segment.
     */
    private fun windowOver(bytes: ByteArray, start: Int): ByteWindow {
        val array = ByteArray(2 + start) + bytes + ByteArray(1)
        val window = ByteWindow()
        window.attach(array, 2)
        return window
    }

    private fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { values[it].toByte() }
}
