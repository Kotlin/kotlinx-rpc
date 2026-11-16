/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.BooleanArrayList
import kotlinx.rpc.protobuf.internal.DoubleArrayList
import kotlinx.rpc.protobuf.internal.FloatArrayList
import kotlinx.rpc.protobuf.internal.IntArrayList
import kotlinx.rpc.protobuf.internal.LongArrayList
import kotlinx.rpc.protobuf.internal.UIntArrayList
import kotlinx.rpc.protobuf.internal.ULongArrayList
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.decodeWire
import kotlinx.rpc.protobuf.internal.encodeWire
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class WireMarshallerTest {

    @Test
    fun testBoolEncodeDecode() {
        val fieldNr = 3
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeBool(fieldNr, true)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readBool()
            assertNotNull(value)
            assertTrue(value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testInt32EncodeDecode() {
        val fieldNr = 5
        val testValue = 42
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeInt32(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readInt32()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testInt64EncodeDecode() {
        val fieldNr = 6
        val testValue = Long.MAX_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeInt64(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readInt64()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testUInt32EncodeDecode() {
        val fieldNr = 7
        val testValue = UInt.MAX_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeUInt32(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readUInt32()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testUInt64EncodeDecode() {
        val fieldNr = 8
        val testValue = ULong.MAX_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeUInt64(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readUInt64()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSInt32EncodeDecode() {
        val fieldNr = 9
        val testValue = Int.MIN_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeSInt32(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readSInt32()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSInt64EncodeDecode() {
        val fieldNr = 10
        val testValue = Long.MIN_VALUE // Min long value
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeSInt64(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readSInt64()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testFixed32EncodeDecode() {
        val fieldNr = 11
        val testValue = UInt.MAX_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeFixed32(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.FIXED32, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readFixed32()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testFixed64EncodeDecode() {
        val fieldNr = 12
        val testValue = ULong.MAX_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeFixed64(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.FIXED64, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readFixed64()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSFixed32EncodeDecode() {
        val fieldNr = 13
        val testValue = Int.MIN_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeSFixed32(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.FIXED32, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readSFixed32()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSFixed64EncodeDecode() {
        val fieldNr = 14
        val testValue = Long.MIN_VALUE
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeSFixed64(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.FIXED64, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readSFixed64()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testEnumEncodeDecode() {
        val fieldNr = 15
        val testValue = 42
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeEnum(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.VARINT, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readEnum()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testStringEncodeDecode() {
        val fieldNr = 16
        val testValue = "Hello, World!"
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeString(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readString()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testEmptyBufferDecoding() {
        val buffer = Buffer()

        decodeWire(buffer) { decoder ->
            assertTrue(decoder.readTag().isEnd)
        }
    }

    @Test
    fun testScopeFlushesAutomatically() {
        val fieldNr = 17
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeBool(fieldNr, true)
        }

        decodeWire(buffer) { decoder ->
            assertEquals(fieldNr, decoder.readTag().fieldNr)
            assertTrue(decoder.readBool())
            assertTrue(decoder.readTag().isEnd)
        }
    }

    @Test
    fun testScopeAppendsToBufferAndReturnsResult() {
        val buffer = Buffer().apply { write(bytes(0x08, 0x01)) }

        val result = encodeWire(buffer) { encoder ->
            encoder.writeInt32(2, 42)
            "encoded"
        }

        assertEquals("encoded", result)
        assertContentEquals(bytes(0x08, 0x01, 0x10, 0x2A), buffer.readByteArray())
    }

    @Test
    fun testEmptyEncodingScope() {
        val buffer = Buffer().apply { write(bytes(0x08, 0x01)) }

        encodeWire(buffer) { }

        assertContentEquals(bytes(0x08, 0x01), buffer.readByteArray())
    }

    @Test
    fun testWriteAfterScope() {
        val buffer = Buffer()
        val escaped = encodeWire(buffer) { encoder ->
            encoder.writeBool(1, true)
            encoder
        }

        assertFailsWith<IllegalStateException> { escaped.writeBool(2, true) }
        assertFailsWith<IllegalStateException> { escaped.flush() }
        assertFailsWith<IllegalStateException> { escaped.writeRawBytes(Buffer()) }
        assertContentEquals(bytes(0x08, 0x01), buffer.readByteArray())
    }

    @Test
    fun testFailedEncodingClosesScope() {
        val buffer = Buffer().apply { write(bytes(0x08, 0x01)) }
        val failure = IllegalArgumentException("encoding failed")
        var escaped: WireEncoder? = null

        val thrown = assertFailsWith<IllegalArgumentException> {
            encodeWire(buffer) { encoder ->
                escaped = encoder
                encoder.writeInt32(2, 42)
                encoder.flush()
                encoder.writeBool(3, true)
                throw failure
            }
        }

        assertSame(failure, thrown)
        assertFailsWith<IllegalStateException> { escaped!!.writeBool(4, true) }
        assertFailsWith<IllegalStateException> { escaped!!.flush() }
        assertFailsWith<IllegalStateException> { escaped!!.writeRawBytes(Buffer()) }
        encodeWire(buffer) { it.writeBool(4, false) }
        assertContentEquals(bytes(0x08, 0x01, 0x10, 0x2A, 0x20, 0x00), buffer.readByteArray())
    }

    @Test
    fun testMultipleFieldsEncodeDecode() {
        val buffer = Buffer()
        encodeWire(buffer) { encoder ->
            encoder.writeBool(1, true)
            encoder.writeInt32(2, 42)
            encoder.writeString(3, "Hello")
            encoder.writeFixed64(4, 123456789uL)
        }

        decodeWire(buffer) { decoder ->
            val tag1 = decoder.readTag()
            assertTrue(!tag1.isEnd)
            assertEquals(1, tag1.fieldNr)
            assertEquals(WireType.VARINT, tag1.wireType)
            val bool = decoder.readBool()
            assertNotNull(bool)
            assertTrue(bool)

            val tag2 = decoder.readTag()
            assertTrue(!tag2.isEnd)
            assertEquals(2, tag2.fieldNr)
            assertEquals(WireType.VARINT, tag2.wireType)
            val int32 = decoder.readInt32()
            assertNotNull(int32)
            assertEquals(42, int32)

            val tag3 = decoder.readTag()
            assertTrue(!tag3.isEnd)
            assertEquals(3, tag3.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag3.wireType)
            val string = decoder.readString()
            assertNotNull(string)
            assertEquals("Hello", string)

            val tag4 = decoder.readTag()
            assertTrue(!tag4.isEnd)
            assertEquals(4, tag4.fieldNr)
            assertEquals(WireType.FIXED64, tag4.wireType)
            val fixed64 = decoder.readFixed64()
            assertNotNull(fixed64)
            assertEquals(123456789uL, fixed64)

            assertTrue(decoder.readTag().isEnd)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testReadAfterScope() {
        val fieldNr = 19
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeBool(fieldNr, true)
        }

        var escaped: WireDecoder? = null
        decodeWire(buffer) { decoder -> escaped = decoder }

        assertFailsWith<ProtobufDecodingException> { escaped!!.readTag() }
    }

    @Test
    fun testWriteAfterFlush() {
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeBool(1, true)
            encoder.flush()

            encoder.writeInt32(2, 42)
        }

        decodeWire(buffer) { decoder ->
            val tag1 = decoder.readTag()
            assertTrue(!tag1.isEnd)
            assertEquals(1, tag1.fieldNr)
            val bool = decoder.readBool()
            assertNotNull(bool)
            assertTrue(bool)

            val tag2 = decoder.readTag()
            assertTrue(!tag2.isEnd)
            assertEquals(2, tag2.fieldNr)
            val int32 = decoder.readInt32()
            assertNotNull(int32)
            assertEquals(42, int32)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testUnicodeStringEncodeDecode() {
        val fieldNr = 20
        val testValue = "Hello, 世界! 😊"
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeString(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readString()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testDecoderConsumesWholeBuffer() {
        val fieldNr = 1
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeBool(fieldNr, true)
            encoder.writeBool(fieldNr + 1, true)
        }

        decodeWire(buffer) { decoder ->
            decoder.readTag()
            assertTrue(decoder.readBool())
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testEachDecodeConsumesItsBuffer() {
        val field1Nr = 1
        val field2Nr = 2
        val field1Str = "a".repeat(1000000)
        val field2Str = "b".repeat(1000000)

        val buffer1 = Buffer()
        val buffer2 = Buffer()
        encodeWire(buffer1) { encoder ->
            encoder.writeString(field1Nr, field1Str)
        }
        encodeWire(buffer2) { encoder ->
            encoder.writeString(field2Nr, field2Str)
        }

        decodeWire(buffer1) { decoder ->
            val tag = decoder.readTag()
            assertEquals(field1Nr, tag.fieldNr)
            assertEquals(field1Str, decoder.readString())
        }
        decodeWire(buffer2) { decoder ->
            val tag = decoder.readTag()
            assertEquals(field2Nr, tag.fieldNr)
            assertEquals(field2Str, decoder.readString())
        }
        assertTrue(buffer1.exhausted())
        assertTrue(buffer2.exhausted())
    }

    @Test
    fun testEmptyString() {
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeString(1, "")
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            val str = decoder.readString()
            assertNotNull(str)
            assertEquals("", str)
        }
    }

    @Test
    fun testEmptyByteArray() {
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeBytes(1, ByteArray(0))
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            val bytes = decoder.readBytes()
            assertNotNull(bytes)
            assertEquals(0, bytes.size)
        }
    }

    @Test
    fun testBytesEncodeDecode() {
        val buffer = Buffer()

        val bytes = ByteArray(1000000) { it.toByte() }
        encodeWire(buffer) { encoder ->
            encoder.writeBytes(1, bytes)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            val actualBytes = decoder.readBytes()
            assertNotNull(actualBytes)
            assertEquals(1000000, actualBytes.size)
            assertTrue(bytes.contentEquals(actualBytes.toByteArray()))
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testDoubleEncodeDecode() {
        val fieldNr = 21
        val testValue = 3.14159265359
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeDouble(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.FIXED64, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readDouble()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testFloatEncodeDecode() {
        val fieldNr = 22
        val testValue = 3.14159f
        val buffer = Buffer()

        encodeWire(buffer) { encoder ->
            encoder.writeFloat(fieldNr, testValue)
        }

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(WireType.FIXED32, tag.wireType)
            assertEquals(fieldNr, tag.fieldNr)

            val value = decoder.readFloat()
            assertNotNull(value)
            assertEquals(testValue, value)
        }
        assertTrue(buffer.exhausted())
    }

    private fun <T, L : MutableList<T>> runPackedFixedTest(
        list: List<T>,
        copyOf: (List<T>) -> L,
        write: WireEncoder.(Int, L) -> Unit,
        read: WireDecoder.(L) -> Unit,
    ) {
        val buf = Buffer()
        encodeWire(buf) { encoder ->
            encoder.write(1, copyOf(list))
        }
        decodeWire(buf) { dec ->
            dec.readTag().apply {
                assertEquals(1, fieldNr)
                assertEquals(WireType.LENGTH_DELIMITED, wireType)
            }
            val test = copyOf(emptyList())
            dec.read(test)
            assertEquals(list, test)
        }
        assertTrue(buf.exhausted())
    }

    @Test
    fun testPackedFixed32() = runPackedFixedTest(
        List(1_000_000) { UInt.MAX_VALUE + it.toUInt() },
        { UIntArrayList.copyOf(it) },
        WireEncoder::writePackedFixed32,
        WireDecoder::readPackedFixed32
    )

    @Test
    fun testPackedBool() = runPackedFixedTest(
        List(1_000_000) { it % 2 == 0 },
        { BooleanArrayList.copyOf(it) },
        WireEncoder::writePackedBool,
        WireDecoder::readPackedBool
    )

    @Test
    fun testPackedFixed64() = runPackedFixedTest(
        List(1_000_000) { UInt.MAX_VALUE + it.toULong() },
        { ULongArrayList.copyOf(it) },
        WireEncoder::writePackedFixed64,
        WireDecoder::readPackedFixed64,
    )

    @Test
    fun testPackedSFixed32() = runPackedFixedTest(
        List(1_000_000) { Int.MAX_VALUE + it },
        { IntArrayList.copyOf(it) },
        WireEncoder::writePackedSFixed32,
        WireDecoder::readPackedSFixed32
    )

    @Test
    fun testPackedSFixed64() = runPackedFixedTest(
        List(1_000_000) { Long.MAX_VALUE + it },
        { LongArrayList.copyOf(it) },
        WireEncoder::writePackedSFixed64,
        WireDecoder::readPackedSFixed64
    )

    @Test
    fun testPackedFloat() = runPackedFixedTest(
        List(1_000_000) { it.toFloat() / 3.3f * ((it and 1) * 2 - 1) },
        { FloatArrayList.copyOf(it) },
        WireEncoder::writePackedFloat,
        WireDecoder::readPackedFloat,
    )

    @Test
    fun testPackedDouble() = runPackedFixedTest(
        List(1_000_000) { it.toDouble() / 3.3 * ((it and 1) * 2 - 1) },
        { DoubleArrayList.copyOf(it) },
        WireEncoder::writePackedDouble,
        WireDecoder::readPackedDouble,
    )

    private fun <T, L : MutableList<T>> runPackedVarTest(
        list: List<T>,
        copyOf: (List<T>) -> L,
        sizeFn: (L) -> Int,
        write: WireEncoder.(Int, L, Int) -> Unit,
        read: WireDecoder.(L) -> Unit,
    ) {
        val buf = Buffer()
        val primitive = copyOf(list)
        encodeWire(buf) { encoder ->
            encoder.write(1, primitive, sizeFn(primitive))
        }
        decodeWire(buf) { dec ->
            dec.readTag().apply {
                assertEquals(1, fieldNr)
                assertEquals(WireType.LENGTH_DELIMITED, wireType)
            }
            val test = copyOf(emptyList())
            dec.read(test)
            assertEquals(list, test)
        }
        assertTrue(buf.exhausted())
    }

    @Test
    fun testPackedInt32() = runPackedVarTest(
        List(1_000_000) { Int.MAX_VALUE + it },
        { IntArrayList.copyOf(it) },
        WireSize::packedInt32,
        WireEncoder::writePackedInt32,
        WireDecoder::readPackedInt32
    )

    @Test
    fun testPackedInt64() = runPackedVarTest(
        List(1_000_000) { Long.MAX_VALUE + it.toLong() },
        { LongArrayList.copyOf(it) },
        WireSize::packedInt64,
        WireEncoder::writePackedInt64,
        WireDecoder::readPackedInt64
    )

    @Test
    fun testPackedUInt32() = runPackedVarTest(
        List(1_000_000) { UInt.MAX_VALUE + it.toUInt() },
        { UIntArrayList.copyOf(it) },
        WireSize::packedUInt32,
        WireEncoder::writePackedUInt32,
        WireDecoder::readPackedUInt32
    )

    @Test
    fun testPackedUInt64() = runPackedVarTest(
        List(1_000_000) { ULong.MAX_VALUE + it.toULong() },
        { ULongArrayList.copyOf(it) },
        WireSize::packedUInt64,
        WireEncoder::writePackedUInt64,
        WireDecoder::readPackedUInt64
    )

    @Test
    fun testPackedSInt32() = runPackedVarTest(
        List(1_000_000) { Int.MAX_VALUE + it },
        { IntArrayList.copyOf(it) },
        WireSize::packedSInt32,
        WireEncoder::writePackedSInt32,
        WireDecoder::readPackedSInt32
    )

    @Test
    fun testPackedSInt64() = runPackedVarTest(
        List(1_000_000) { Long.MAX_VALUE + it.toLong() },
        { LongArrayList.copyOf(it) },
        WireSize::packedSInt64,
        WireEncoder::writePackedSInt64,
        WireDecoder::readPackedSInt64
    )

    @Test
    fun testPackedEnum() = runPackedVarTest(
        List(1_000_000) { it },
        { IntArrayList.copyOf(it) },
        WireSize::packedEnum,
        WireEncoder::writePackedEnum,
        { destination -> readPackedEnum(destination) { number -> number } }
    )

    @Test
    fun testPackedEmptyPayloadAppendsNothing() {
        val buf = Buffer()
        encodeWire(buf) { encoder ->
            encoder.writePackedInt32(fieldNr = 1, value = IntArrayList.copyOf(listOf(1, 2)), fieldSize = 2)
            encoder.writePackedInt32(fieldNr = 1, value = IntArrayList(), fieldSize = 0)
        }
        decodeWire(buf) { dec ->
            dec.readTag()
            val test = IntArrayList.copyOf(listOf(9))
            dec.readPackedInt32(test)
            dec.readTag()
            dec.readPackedInt32(test)
            assertEquals(listOf(9, 1, 2), test)
        }
        assertTrue(buf.exhausted())
    }

    @Test
    fun testPackedFixedEmptyPayloadAppendsNothing() {
        val buf = Buffer()
        encodeWire(buf) { encoder ->
            encoder.writePackedFixed32(fieldNr = 1, value = UIntArrayList.copyOf(listOf(7u)))
            encoder.writePackedFixed32(fieldNr = 1, value = UIntArrayList())
        }
        decodeWire(buf) { dec ->
            dec.readTag()
            val test = UIntArrayList.copyOf(listOf(9u))
            dec.readPackedFixed32(test)
            dec.readTag()
            dec.readPackedFixed32(test)
            assertEquals(listOf(9u, 7u), test)
        }
        assertTrue(buf.exhausted())
    }

    @Test
    fun testPackedFixedLengthNotMultipleOfElementSize() {
        val buffer = Buffer()
        // Tag 0x0A: field 1, wire type 2. Length 3 is not a multiple of the 4-byte element.
        buffer.write(byteArrayOf(0x0A, 3, 1, 2, 3))
        assertFailsWith<ProtobufDecodingException> {
            decodeWire(buffer) {
                it.readTag()
                it.readPackedFixed32(UIntArrayList())
            }
        }
    }

    @Test
    fun testPackedVarintTruncated() {
        val buffer = Buffer()
        // Tag 0x0A: field 1, wire type 2. The 2-byte payload has no terminal varint byte.
        buffer.write(byteArrayOf(0x0A, 2, 0x80.toByte(), 0x80.toByte()))
        assertFailsWith<ProtobufDecodingException> {
            decodeWire(buffer) {
                it.readTag()
                it.readPackedInt32(IntArrayList())
            }
        }
    }

    @Test
    fun testPackedLengthBeyondInput() {
        val buffer = Buffer()
        // Tag 0x0A: field 1, wire type 2. Length 10, but only 2 payload bytes follow.
        buffer.write(byteArrayOf(0x0A, 10, 1, 2))
        assertFailsWith<ProtobufDecodingException> {
            decodeWire(buffer) {
                it.readTag()
                it.readPackedInt32(IntArrayList())
            }
        }
    }


    @Test
    fun testInvalidTagZero() {
        val buffer = Buffer()
        buffer.writeByte(0)

        assertFailsWith<ProtobufDecodingException> {
            decodeWire(buffer) { it.readTag() }
        }
    }

    @Test
    fun testInvalidTagVarintMoreThanTenBytes() {
        // 11 bytes: 10 continuation bytes (0x80) + 1 terminator (0x01)
        // This exceeds the protobuf varint maximum of 10 bytes.
        val buffer = Buffer()
        repeat(10) { buffer.writeByte(0x80.toByte()) }
        buffer.writeByte(0x01)

        assertFailsWith<ProtobufDecodingException> {
            decodeWire(buffer) { it.readTag() }
        }
    }

    @Test
    fun testInvalidTagOverlongEncoding() {
        // Field number 1, wire type 0 (VARINT) = tag value 8.
        // Minimal encoding: single byte 0x08.
        // Overlong encoding: two bytes 0x88 0x00 (value 8 in 2 varint bytes).
        val buffer = Buffer()
        buffer.writeByte(0x88.toByte())
        buffer.writeByte(0x00)

        assertFailsWith<ProtobufDecodingException> {
            decodeWire(buffer) { it.readTag() }
        }
    }

    /**
     * Writes a raw protobuf LENGTH_DELIMITED field with the given [data] bytes
     * at field number [fieldNr] into the buffer.
     */
    private fun Buffer.writeRawStringField(fieldNr: Int, data: ByteArray) {
        // Tag: (fieldNr << 3) | 2 (wire type LENGTH_DELIMITED)
        writeVarint32((fieldNr shl 3) or 2)
        // Length prefix
        writeVarint32(data.size)
        // Raw data
        write(data)
    }

    private fun Buffer.writeVarint32(value: Int) {
        var v = value
        while (v and 0x7F.inv() != 0) {
            writeByte((v and 0x7F or 0x80).toByte())
            v = v ushr 7
        }
        writeByte(v.toByte())
    }

    @Test
    fun testReadStringRejectsInvalidUtf8SingleByte() {
        // 0xFF is never valid in UTF-8
        val buffer = Buffer()
        buffer.writeRawStringField(1, byteArrayOf(0xFF.toByte()))

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            assertFailsWith<ProtobufDecodingException> {
                decoder.readString()
            }
        }
    }

    @Test
    fun testReadStringRejectsInvalidUtf8Surrogate() {
        // 0xED 0xA0 0x80 encodes U+D800 (surrogate half, invalid in UTF-8)
        val buffer = Buffer()
        buffer.writeRawStringField(1, byteArrayOf(0xED.toByte(), 0xA0.toByte(), 0x80.toByte()))

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            assertFailsWith<ProtobufDecodingException> {
                decoder.readString()
            }
        }
    }

    @Test
    fun testReadStringRejectsOverlongEncoding() {
        // 0xC0 0x80 is an overlong encoding of U+0000 (must use single byte 0x00)
        val buffer = Buffer()
        buffer.writeRawStringField(1, byteArrayOf(0xC0.toByte(), 0x80.toByte()))

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            assertFailsWith<ProtobufDecodingException> {
                decoder.readString()
            }
        }
    }

    @Test
    fun testReadStringRejectsTruncatedMultiByteSequence() {
        // 0xE2 0x82 is a truncated 3-byte sequence (missing third continuation byte)
        val buffer = Buffer()
        buffer.writeRawStringField(1, byteArrayOf(0xE2.toByte(), 0x82.toByte()))

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            assertFailsWith<ProtobufDecodingException> {
                decoder.readString()
            }
        }
    }

    @Test
    fun testReadStringAcceptsValidUtf8() {
        // Valid multi-byte UTF-8: U+00E9 (é) = 0xC3 0xA9, U+1F600 (😀) = 0xF0 0x9F 0x98 0x80
        val validUtf8 = byteArrayOf(
            0xC3.toByte(), 0xA9.toByte(),                         // é
            0xF0.toByte(), 0x9F.toByte(), 0x98.toByte(), 0x80.toByte()  // 😀
        )
        val buffer = Buffer()
        buffer.writeRawStringField(1, validUtf8)

        decodeWire(buffer) { decoder ->
            val tag = decoder.readTag()
            assertTrue(!tag.isEnd)
            assertEquals(1, tag.fieldNr)
            assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

            val str = decoder.readString()
            assertEquals("é😀", str)
        }
        assertTrue(buffer.exhausted())
    }

}
