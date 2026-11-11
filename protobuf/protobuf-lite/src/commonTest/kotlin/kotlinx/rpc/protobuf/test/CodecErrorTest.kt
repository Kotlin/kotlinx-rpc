/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.decodeFromByteArray
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class CodecErrorTest {
    @Test
    fun eachReaderKindHasTheSameOutcome() {
        for (case in decodeErrorCases()) {
            val expected = case.decode(ByteArrayMessageReader(case.bytes))
            if (case.fails) {
                assertIs<ProtobufDecodingException>(expected.exceptionOrNull(), case.name)
            } else {
                assertNull(expected.exceptionOrNull(), case.name)
            }
            for (kind in readerKinds) {
                assertSameOutcome(expected, case.decode(kind.create(case.bytes)), "${case.name}, ${kind.name}")
            }
        }
    }

    @Test
    fun nestedUnknownGroupsDecodeWithAHigherRecursionLimit() {
        val marshaller = grpcMarshallerOf<UnknownFieldsSubset>()
        val bytes = nestedUnknownGroups()
        val decoded = marshaller.decodeFromByteArray(bytes, ProtoConfig { recursionLimit = NESTED_GROUPS })
        assertContentEquals(bytes, marshaller.encodeToByteArray(decoded))
    }

    @Test
    fun loneSurrogateEncodesAsQuestionMark() {
        val marshaller = grpcMarshallerOf<AllPrimitives>()
        val message = AllPrimitives { string = "a${0xD800.toChar()}b" }
        val bytes = marshaller.encodeToByteArray(message)
        assertContentEquals(marshaller.encodeToByteArray(AllPrimitives { string = "a?b" }), bytes)
        assertEquals(bytes.size, marshaller.prepare(message).size)
    }

    @Test
    fun stringSizeMatchesWrittenBytes() {
        val random = Random(42)
        repeat(1000) {
            val chars = CharArray(random.nextInt(0, 51)) {
                if (random.nextBoolean()) {
                    random.nextInt(0xD800, 0xE000).toChar()
                } else {
                    random.nextInt(0, 0x10000).toChar()
                }
            }
            val value = chars.concatToString()
            val buffer = Buffer()
            WireEncoder(buffer).apply {
                writeString(1, value)
                flush()
            }
            val length = WireSize.string(value)
            val expected = WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(length) + length
            assertEquals(expected.toLong(), buffer.size, value.map { it.code.toString(16) }.toString())
        }
    }
}

/** An input for the decode tests, and the marshaller that decodes it. */
internal class DecodeErrorCase(
    val name: String,
    val bytes: ByteArray,
    /** `true` if the decode must throw [ProtobufDecodingException], `false` if it must complete. */
    val fails: Boolean,
    private val marshaller: GrpcMarshaller<*>,
    private val config: ProtoConfig? = null,
) {
    fun decode(reader: GrpcMessageReader): Result<Any?> = runCatching { marshaller.decode(reader, config) }
}

/** Checks that two decodes completed with equal messages, or that both threw an exception of the same class. */
internal fun assertSameOutcome(expected: Result<Any?>, actual: Result<Any?>, message: String) {
    val expectedFailure = expected.exceptionOrNull()
    val actualFailure = actual.exceptionOrNull()
    assertEquals(expectedFailure?.let { it::class }, actualFailure?.let { it::class }, message)
    if (expectedFailure == null) {
        assertEquals(expected.getOrNull(), actual.getOrNull(), message)
    }
}

private const val NESTED_GROUPS = 200

/** Unknown groups of field 1, nested [NESTED_GROUPS] times. */
private fun nestedUnknownGroups(): ByteArray = ByteArray(2 * NESTED_GROUPS) {
    if (it < NESTED_GROUPS) {
        0x0B
    } else {
        0x0C
    }
}

/** Malformed inputs, and each prefix of an encoded [AllPrimitives]. */
internal fun decodeErrorCases(): List<DecodeErrorCase> = buildList {
    val allPrimitives = grpcMarshallerOf<AllPrimitives>()
    val encoded = codecCases().first().bytes
    val boundaries = fieldBoundaries(encoded)
    for (length in encoded.indices) {
        add(DecodeErrorCase("prefix of $length bytes", encoded.copyOf(length), length !in boundaries, allPrimitives))
    }
    val malformed = listOf(
        "11-byte varint" to bytes(0x08, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF),
        "invalid UTF-8" to bytes(0x72, 0x01, 0xFF),
        "tag out of range" to bytes(0xFF, 0xFF, 0xFF, 0xFF, 0x1F),
        "length past the end" to bytes(0x0A, 0x7F),
        "negative length" to bytes(0x0A, 0xFF, 0xFF, 0xFF, 0xFF, 0x0F),
    )
    for ((name, input) in malformed) {
        add(DecodeErrorCase(name, input, true, allPrimitives))
    }

    val subset = grpcMarshallerOf<UnknownFieldsSubset>()
    add(DecodeErrorCase("nested unknown groups", nestedUnknownGroups(), true, subset))
    val discard = ProtoConfig { discardUnknownFields = true }
    add(DecodeErrorCase("nested unknown groups, discarded", nestedUnknownGroups(), true, subset, discard))

    // Field 2 is `listFixed32Packed`, so the length must be a multiple of 4.
    val packed = bytes(0x12, 0x05, 1, 2, 3, 4, 5)
    add(DecodeErrorCase("packed fixed32 of 5 bytes", packed, true, grpcMarshallerOf<Repeated>()))
}

/** The prefix lengths of [bytes] that end between two fields. [bytes] must have no groups. */
private fun fieldBoundaries(bytes: ByteArray): Set<Int> {
    var position = 0
    fun readVarint(): Long {
        var result = 0L
        var shift = 0
        while (true) {
            val byte = bytes[position++]
            result = result or ((byte.toLong() and 0x7F) shl shift)
            if (byte >= 0) {
                return result
            }
            shift += 7
        }
    }

    val result = mutableSetOf(0)
    while (position < bytes.size) {
        when (val wireType = readVarint().toInt() and 7) {
            0 -> {
                readVarint()
            }
            1 -> {
                position += 8
            }
            2 -> {
                val length = readVarint().toInt()
                position += length
            }
            5 -> {
                position += 4
            }
            else -> {
                error("Unexpected wire type $wireType")
            }
        }
        result.add(position)
    }
    return result
}
