/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.internal.Int32
import kotlinx.rpc.protobuf.internal.Int64
import kotlinx.rpc.protobuf.internal.SInt32
import kotlinx.rpc.protobuf.internal.SInt64
import kotlinx.rpc.protobuf.internal.UInt32
import kotlinx.rpc.protobuf.internal.UInt64
import kotlinx.rpc.protobuf.internal.Utf8
import kotlinx.rpc.protobuf.internal.WireInput
import kotlinx.rpc.protobuf.internal.WireOutput
import kotlinx.rpc.protobuf.internal.ZigZag
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** The chunk sizes that each test uses. The small sizes make every slow path run. */
private val CHUNK_SIZES = listOf(1, 2, 3, 7, 8192)

private const val ROUNDS = 100
private const val MAX_OPERATIONS = 40
private const val CAPACITY = 1 shl 15

/** A write and the read that must give back the written value. */
private class Operation(val write: (WireOutput) -> Unit, val read: (WireInput) -> Unit)

class WireRoundTripTest {
    @Test
    fun randomValuesRoundTripWithAnyChunkSizes() {
        val random = Random(42)
        repeat(ROUNDS) {
            val operations = List(random.nextInt(1, MAX_OPERATIONS + 1)) { randomOperation(random) }
            for (writeChunk in CHUNK_SIZES) {
                val segments = SplitOutputSegments(CAPACITY, writeChunk)
                WireOutput(segments).use { output ->
                    operations.forEach { it.write(output) }
                    output.flush()
                }
                val written = segments.bytes()
                for (readChunk in CHUNK_SIZES) {
                    WireInput(SplitInputSegments(written, readChunk), written.size).use { input ->
                        operations.forEach { it.read(input) }
                        assertTrue(input.isAtLimit)
                    }
                }
            }
        }
    }

    private fun randomOperation(random: Random): Operation = when (random.nextInt(10)) {
        0 -> {
            val value = random.nextLong()
            Operation({ it.writeVarint(Int64(value)) }, { assertEquals(value, it.readVarint64()) })
        }

        1 -> {
            val value = random.nextInt()
            Operation(
                { it.writeVarint(UInt32(value)) },
                { assertEquals(value.toLong() and 0xFFFFFFFFL, it.readVarint64()) },
            )
        }

        2 -> {
            val value = random.nextInt()
            Operation({ it.writeVarint(Int32(value)) }, { assertEquals(value, it.readVarint64().toInt()) })
        }

        3 -> {
            val value = random.nextLong()
            Operation(
                { it.writeVarint(SInt64(value)) },
                { assertEquals(value, ZigZag.decode(UInt64(it.readVarint64())).value) },
            )
        }

        4 -> {
            val value = random.nextInt()
            Operation(
                { it.writeVarint(SInt32(value)) },
                { assertEquals(value, ZigZag.decode(UInt32(it.readVarint64().toInt())).value) },
            )
        }

        5 -> {
            val value = random.nextInt()
            Operation({ it.writeFixed32(value) }, { assertEquals(value, it.readFixed32()) })
        }

        6 -> {
            val value = random.nextLong()
            Operation({ it.writeFixed64(value) }, { assertEquals(value, it.readFixed64()) })
        }

        7 -> {
            val value = random.nextBytes(random.nextInt(300))
            Operation(
                { it.writeBytes(value, 0, value.size) },
                { assertContentEquals(value, it.readBytes(value.size)) },
            )
        }

        8 -> {
            val value = randomString(random)
            val length = Utf8.encodedLength(value)
            Operation({ it.writeUtf8(value, length) }, { assertEquals(value, it.readUtf8(length)) })
        }

        else -> {
            val tag = random.nextInt()
            Operation({ it.writeVarint(UInt32(tag)) }, { assertEquals(tag, it.readTagVarint()) })
        }
    }

    /** A string with scalar values of every UTF-8 length. It has no lone surrogates, so it survives the round trip. */
    private fun randomString(random: Random): String {
        val builder = StringBuilder()
        repeat(random.nextInt(60)) {
            when (random.nextInt(4)) {
                0 -> builder.append(random.nextInt(0x80).toChar())
                1 -> builder.append(random.nextInt(0x80, 0x800).toChar())
                2 -> builder.append(randomBmpChar(random))
                else -> {
                    val codePoint = random.nextInt(0x10000, 0x110000)
                    builder.append((0xD800 + ((codePoint - 0x10000) shr 10)).toChar())
                    builder.append((0xDC00 + ((codePoint - 0x10000) and 0x3FF)).toChar())
                }
            }
        }
        return builder.toString()
    }

    /** A char from `U+0800` to `U+FFFF` that isn't a surrogate. */
    private fun randomBmpChar(random: Random): Char {
        val value = random.nextInt(0x800, 0x10000 - 0x800)
        return (if (value >= 0xD800) value + 0x800 else value).toChar()
    }
}
