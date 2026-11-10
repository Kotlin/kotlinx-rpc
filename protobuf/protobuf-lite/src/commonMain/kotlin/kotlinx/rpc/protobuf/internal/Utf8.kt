/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.ProtobufEncodingException

/**
 * UTF-8 for protobuf strings.
 *
 * Encoding writes `?` for a lone surrogate, as protobuf-java does. Decoding accepts only well-formed UTF-8
 * (Unicode table 3-7) and throws [ProtobufDecodingException] for other input.
 *
 * A caller of `decode` gives a `chars` array with at least `count` elements: each UTF-8 sequence gives at
 * most one char for each byte.
 */
internal object Utf8 {
    /**
     * The number of bytes that [value] needs in UTF-8.
     *
     * @throws ProtobufEncodingException if the encoded string has more than [Int.MAX_VALUE] bytes.
     */
    fun encodedLength(value: String): Int {
        // A string can have up to 3 bytes for each char, so the count can be larger than an Int.
        var length = 0L
        var i = 0
        while (i < value.length) {
            val c = value[i]
            if (c.isHighSurrogate() && i + 1 < value.length && value[i + 1].isLowSurrogate()) {
                length += 4
                i += 2
            } else {
                length += when {
                    c < '\u0080' -> 1
                    c < '\u0800' -> 2
                    c.isSurrogate() -> 1
                    else -> 3
                }
                i++
            }
        }
        if (length > Int.MAX_VALUE) {
            throw ProtobufEncodingException(
                "A protobuf string can have at most ${Int.MAX_VALUE} bytes in UTF-8, but the string has $length.",
            )
        }
        return length.toInt()
    }

    /** Writes [value] to [window] from [index] and returns the next index. */
    fun encode(value: String, window: ByteWindow, index: Int): Int =
        encode(value, index) { i, byte -> window.setByte(i, byte) }

    /** Writes [value] to [destination] from [offset] and returns the next index. */
    fun encode(value: String, destination: ByteArray, offset: Int): Int =
        encode(value, offset) { i, byte -> destination[i] = byte }

    /** Reads [count] bytes from [window] at [index] into [chars] and returns the char count. */
    fun decode(window: ByteWindow, index: Int, count: Int, chars: CharArray): Int =
        decode(count, chars) { i -> window.getByte(index + i) }

    /** Reads [count] bytes from [bytes] at [offset] into [chars] and returns the char count. */
    fun decode(bytes: ByteArray, offset: Int, count: Int, chars: CharArray): Int =
        decode(count, chars) { i -> bytes[offset + i] }

    private inline fun encode(value: String, start: Int, put: (index: Int, byte: Byte) -> Unit): Int {
        var i = start
        var charIndex = 0
        while (charIndex < value.length) {
            val c = value[charIndex]
            if (c.isHighSurrogate() && charIndex + 1 < value.length && value[charIndex + 1].isLowSurrogate()) {
                val cp = 0x10000 + ((c.code - 0xD800) shl 10) + (value[charIndex + 1].code - 0xDC00)
                put(i++, (0xF0 or (cp shr 18)).toByte())
                put(i++, (0x80 or ((cp shr 12) and 0x3F)).toByte())
                put(i++, (0x80 or ((cp shr 6) and 0x3F)).toByte())
                put(i++, (0x80 or (cp and 0x3F)).toByte())
                charIndex += 2
            } else {
                if (c.isSurrogate()) {
                    put(i++, '?'.code.toByte())
                } else if (c < '\u0080') {
                    put(i++, c.code.toByte())
                } else if (c < '\u0800') {
                    put(i++, (0xC0 or (c.code shr 6)).toByte())
                    put(i++, (0x80 or (c.code and 0x3F)).toByte())
                } else {
                    put(i++, (0xE0 or (c.code shr 12)).toByte())
                    put(i++, (0x80 or ((c.code shr 6) and 0x3F)).toByte())
                    put(i++, (0x80 or (c.code and 0x3F)).toByte())
                }
                charIndex++
            }
        }
        return i
    }

    private inline fun decode(count: Int, chars: CharArray, get: (index: Int) -> Byte): Int {
        var i = 0
        var charCount = 0
        while (i < count) {
            val b0 = get(i).toInt() and 0xFF
            if (b0 < 0x80) {
                chars[charCount++] = b0.toChar()
                i++
                continue
            }
            val length = when (b0) {
                in 0xC2..0xDF -> 2
                in 0xE0..0xEF -> 3
                in 0xF0..0xF4 -> 4
                else -> throw ProtobufDecodingException.invalidUtf8()
            }
            if (length > count - i) {
                throw ProtobufDecodingException.invalidUtf8()
            }
            val b1 = get(i + 1).toInt() and 0xFF
            // The second byte range of Unicode table 3-7 for the lead byte.
            val secondByteValid = when (b0) {
                0xE0 -> b1 in 0xA0..0xBF
                0xED -> b1 in 0x80..0x9F
                0xF0 -> b1 in 0x90..0xBF
                0xF4 -> b1 in 0x80..0x8F
                else -> b1 in 0x80..0xBF
            }
            if (!secondByteValid) {
                throw ProtobufDecodingException.invalidUtf8()
            }
            for (j in 2 until length) {
                if ((get(i + j).toInt() and 0xFF) !in 0x80..0xBF) {
                    throw ProtobufDecodingException.invalidUtf8()
                }
            }
            when (length) {
                2 -> {
                    chars[charCount++] = (((b0 and 0x1F) shl 6) or (b1 and 0x3F)).toChar()
                }
                3 -> {
                    val b2 = get(i + 2).toInt() and 0xFF
                    chars[charCount++] = (((b0 and 0x0F) shl 12) or ((b1 and 0x3F) shl 6) or (b2 and 0x3F)).toChar()
                }
                4 -> {
                    val b2 = get(i + 2).toInt() and 0xFF
                    val b3 = get(i + 3).toInt() and 0xFF
                    val cp = ((b0 and 0x07) shl 18) or ((b1 and 0x3F) shl 12) or
                        ((b2 and 0x3F) shl 6) or (b3 and 0x3F)
                    chars[charCount++] = (0xD800 + ((cp - 0x10000) shr 10)).toChar()
                    chars[charCount++] = (0xDC00 + ((cp - 0x10000) and 0x3FF)).toChar()
                }
            }
            i += length
        }
        return charCount
    }
}
