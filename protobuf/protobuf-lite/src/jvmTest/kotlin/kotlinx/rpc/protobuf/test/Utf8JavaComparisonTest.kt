/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import com.google.protobuf.CodedInputStream
import com.google.protobuf.CodedOutputStream
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.Utf8
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.fail

/**
 * Compares [Utf8] with the UTF-8 code of protobuf-java.
 *
 * `com.google.protobuf.Utf8` isn't public, so the test uses it through `CodedInputStream.readStringRequireUtf8`
 * and `CodedOutputStream.writeStringNoTag`.
 */
class Utf8JavaComparisonTest {
    @Test
    fun decodeShortSequences() {
        forEachShortSequence { input -> checkDecode(ByteArray(input.size) { input[it].toByte() }) }
    }

    @Test
    fun decodeRandomInputs() {
        val random = Random(42)
        // Bytes at the edges of the lead and continuation byte ranges. They give many inputs with multibyte sequences.
        val edges = intArrayOf(
            0x00, 0x41, 0x7F, 0x80, 0x8F, 0x90, 0x9F, 0xA0, 0xBF, 0xC0, 0xC1, 0xC2,
            0xDF, 0xE0, 0xE1, 0xEC, 0xED, 0xEE, 0xEF, 0xF0, 0xF1, 0xF3, 0xF4, 0xF5, 0xFF,
        )
        repeat(200_000) {
            val bytes = ByteArray(random.nextInt(0, 13)) {
                if (random.nextInt(4) == 0) {
                    random.nextInt(0x100).toByte()
                } else {
                    edges[random.nextInt(edges.size)].toByte()
                }
            }
            checkDecode(bytes)
        }
    }

    @Test
    fun decodeAllScalarValues() {
        checkDecode(allScalarValues().encodeToByteArray())
    }

    @Test
    fun encodeRandomStrings() {
        val random = Random(42)
        // Chars at the edges of the 1, 2, and 3 byte ranges, and the surrogates.
        val edges = intArrayOf(
            0x61, 0x7F, 0x80, 0x7FF, 0x800, 0xD7FF, 0xD800, 0xDBFF, 0xDC00, 0xDFFF, 0xE000, 0xFFFF,
        )
        repeat(200_000) {
            val value = buildString {
                repeat(random.nextInt(0, 9)) {
                    val code = if (random.nextBoolean()) edges[random.nextInt(edges.size)] else random.nextInt(0x10000)
                    append(code.toChar())
                }
            }
            checkEncode(value)
        }
    }

    @Test
    fun encodeAllScalarValues() {
        checkEncode(allScalarValues())
    }

    private fun checkDecode(bytes: ByteArray) {
        val expected = try {
            javaDecode(bytes)
        } catch (_: InvalidProtocolBufferException) {
            null
        }
        val actual = try {
            val chars = CharArray(bytes.size)
            chars.concatToString(0, Utf8.decode(bytes, 0, bytes.size, chars))
        } catch (_: ProtobufDecodingException) {
            null
        }
        if (expected != actual) {
            fail("Input ${hex(bytes)}: protobuf-java gives ${expected?.let(::codeUnits)}, Utf8 gives ${actual?.let(::codeUnits)}.")
        }
    }

    private fun checkEncode(value: String) {
        val expected = javaEncode(value)
        val actual = utf8Bytes(value)
        if (Utf8.encodedLength(value) != expected.size || !expected.contentEquals(actual)) {
            fail("String ${codeUnits(value)}: protobuf-java gives ${hex(expected)}, Utf8 gives ${hex(actual)}.")
        }
    }

    /** Decodes [bytes] as the value of a `string` field. */
    private fun javaDecode(bytes: ByteArray): String {
        val field = ByteArray(CodedOutputStream.computeByteArraySizeNoTag(bytes))
        val output = CodedOutputStream.newInstance(field)
        output.writeByteArrayNoTag(bytes)
        output.checkNoSpaceLeft()
        return CodedInputStream.newInstance(field).readStringRequireUtf8()
    }

    /**
     * Encodes [value] as the value of a `string` field and returns the bytes without the length prefix.
     *
     * The field array has the size that `computeStringSizeNoTag` gives, so `checkNoSpaceLeft` also checks the
     * size that protobuf-java computes.
     */
    private fun javaEncode(value: String): ByteArray {
        val field = ByteArray(CodedOutputStream.computeStringSizeNoTag(value))
        val output = CodedOutputStream.newInstance(field)
        output.writeStringNoTag(value)
        output.checkNoSpaceLeft()
        return CodedInputStream.newInstance(field).readByteArray()
    }

    private fun hex(bytes: ByteArray): String = bytes.joinToString(" ") { (it.toInt() and 0xFF).toString(16) }

    private fun codeUnits(value: String): String = value.map { it.code.toString(16) }.toString()
}
