/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.Buffer
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

// TODO: Move this to the commonTest
@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
class WireCodecTest {

    @Test
    fun testBoolEncodeDecode() {
        val fieldNr = 3
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeBool(fieldNr, true))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readBool()
        assertNotNull(value)
        assertTrue(value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testInt32EncodeDecode() {
        val fieldNr = 5
        val testValue = 42
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeInt32(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readInt32()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testInt64EncodeDecode() {
        val fieldNr = 6
        val testValue = Long.MAX_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeInt64(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readInt64()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testUInt32EncodeDecode() {
        val fieldNr = 7
        val testValue = UInt.MAX_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeUInt32(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readUInt32()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testUInt64EncodeDecode() {
        val fieldNr = 8
        val testValue = ULong.MAX_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeUInt64(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readUInt64()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSInt32EncodeDecode() {
        val fieldNr = 9
        val testValue = Int.MIN_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeSInt32(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readSInt32()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSInt64EncodeDecode() {
        val fieldNr = 10
        val testValue = Long.MIN_VALUE // Min long value
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeSInt64(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readSInt64()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testFixed32EncodeDecode() {
        val fieldNr = 11
        val testValue = UInt.MAX_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeFixed32(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.FIXED32, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readFixed32()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testFixed64EncodeDecode() {
        val fieldNr = 12
        val testValue = ULong.MAX_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeFixed64(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.FIXED64, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readFixed64()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSFixed32EncodeDecode() {
        val fieldNr = 13
        val testValue = Int.MIN_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeSFixed32(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.FIXED32, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readSFixed32()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testSFixed64EncodeDecode() {
        val fieldNr = 14
        val testValue = Long.MIN_VALUE
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeSFixed64(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.FIXED64, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readSFixed64()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testEnumEncodeDecode() {
        val fieldNr = 15
        val testValue = 42
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeEnum(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.VARINT, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readEnum()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testStringEncodeDecode() {
        val fieldNr = 16
        val testValue = "Hello, World!"
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeString(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readString()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testEmptyBufferDecoding() {
        val buffer = Buffer()

        val decoder = WireDecoder(buffer)
        assertNull(decoder.readTag())
        assertNull(decoder.readBool())
        assertNull(decoder.readInt32())
        assertNull(decoder.readInt64())
        assertNull(decoder.readSInt32())
        assertNull(decoder.readSInt64())
        assertNull(decoder.readUInt32())
        assertNull(decoder.readUInt64())
        assertNull(decoder.readString())
        assertNull(decoder.readEnum())

    }

    @Test
    fun testMissingFlush() {
        val fieldNr = 17
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        encoder.writeBool(fieldNr, true)
        // Intentionally not calling flush()

        // The data is not being written to the buffer yet
        val decoder = WireDecoder(buffer)
        assertNull(decoder.readTag())
        decoder.close()
    }

    @Test
    fun testMultipleFieldsEncodeDecode() {
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)

        // Write multiple fields of different types
        assertTrue(encoder.writeBool(1, true))
        assertTrue(encoder.writeInt32(2, 42))
        assertTrue(encoder.writeString(3, "Hello"))
        assertTrue(encoder.writeFixed64(4, 123456789uL))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        // Read and verify each field
        val tag1 = decoder.readTag()
        assertNotNull(tag1)
        assertEquals(1, tag1.fieldNr)
        assertEquals(WireType.VARINT, tag1.wireType)
        val bool = decoder.readBool()
        assertNotNull(bool)
        assertTrue(bool)

        val tag2 = decoder.readTag()
        assertNotNull(tag2)
        assertEquals(2, tag2.fieldNr)
        assertEquals(WireType.VARINT, tag2.wireType)
        val int32 = decoder.readInt32()
        assertNotNull(int32)
        assertEquals(42, int32)

        val tag3 = decoder.readTag()
        assertNotNull(tag3)
        assertEquals(3, tag3.fieldNr)
        assertEquals(WireType.LENGTH_DELIMITED, tag3.wireType)
        val string = decoder.readString()
        assertNotNull(string)
        assertEquals("Hello", string)

        val tag4 = decoder.readTag()
        assertNotNull(tag4)
        assertEquals(4, tag4.fieldNr)
        assertEquals(WireType.FIXED64, tag4.wireType)
        val fixed64 = decoder.readFixed64()
        assertNotNull(fixed64)
        assertEquals(123456789uL, fixed64)

        // No more tags
        assertNull(decoder.readTag())

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testReadAfterClose() {
        val fieldNr = 19
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeBool(fieldNr, true))
        encoder.flush()

        val decoder = WireDecoder(buffer)
        decoder.close()

        // Reading after close should either return null or throw an exception
        try {
            val tag = decoder.readTag()
            assertNull(tag)
        } catch (e: Exception) {
            // Expected exception in some implementations
        }
    }

    @Test
    fun testWriteAfterFlush() {
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeBool(1, true))
        encoder.flush()

        // Writing after flush should still work
        assertTrue(encoder.writeInt32(2, 42))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        // Verify both values were written
        val tag1 = decoder.readTag()
        assertNotNull(tag1)
        assertEquals(1, tag1.fieldNr)
        val bool = decoder.readBool()
        assertNotNull(bool)
        assertTrue(bool)

        val tag2 = decoder.readTag()
        assertNotNull(tag2)
        assertEquals(2, tag2.fieldNr)
        val int32 = decoder.readInt32()
        assertNotNull(int32)
        assertEquals(42, int32)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testUnicodeStringEncodeDecode() {
        val fieldNr = 20
        val testValue = "Hello, 世界! 😊"
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeString(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readString()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testBufferNotExhausted() {
        val fieldNr = 1
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeBool(fieldNr, true))
        assertTrue(encoder.writeBool(fieldNr + 1, true))
        encoder.flush()

        WireDecoder(buffer).use { decoder ->
            decoder.readTag()
            assertNotNull(decoder.readString())
        }
        assertFalse(buffer.exhausted())
    }

    @Test
    fun testBufferUsedByMultipleDecoders() {
        val buffer = Buffer()

        val field1Nr = 1
        val field2Nr = 2
        val field1Str = "a".repeat(1000000)
        val field2Str = "b".repeat(1000000)

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeString(field1Nr, field1Str))
        assertTrue(encoder.writeString(field2Nr, field2Str))
        encoder.flush()

        WireDecoder(buffer).use { decoder ->
            val tag = decoder.readTag()
            assertEquals(field1Nr, tag?.fieldNr)
            assertEquals(field1Str, decoder.readString())
        }
        assertFalse(buffer.exhausted())

        WireDecoder(buffer).use { decoder ->
            val tag = decoder.readTag()
            assertEquals(field2Nr, tag?.fieldNr)
            assertEquals(field2Str, decoder.readString())
        }
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testEmptyString() {
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeString(1, ""))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(1, tag.fieldNr)
        assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

        val str = decoder.readString()
        assertNotNull(str)
        assertEquals("", str)
    }

    @Test
    fun testEmptyByteArray() {
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeBytes(1, ByteArray(0)))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(1, tag.fieldNr)
        assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)

        val bytes = decoder.readBytes()
        assertNotNull(bytes)
        assertEquals(0, bytes.size)
    }

    @Test
    fun testBytesEncodeDecode() {
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)

        val bytes = ByteArray(1000000) { it.toByte() }

        assertTrue(encoder.writeBytes(1, bytes))
        encoder.flush()

        val decoder = WireDecoder(buffer)
        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(1, tag.fieldNr)
        assertEquals(WireType.LENGTH_DELIMITED, tag.wireType)
    }

    @Test
    fun testDoubleEncodeDecode() {
        val fieldNr = 21
        val testValue = 3.14159265359
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeDouble(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.FIXED64, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readDouble()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testFloatEncodeDecode() {
        val fieldNr = 22
        val testValue = 3.14159f
        val buffer = Buffer()

        val encoder = WireEncoder(buffer)
        assertTrue(encoder.writeFloat(fieldNr, testValue))
        encoder.flush()

        val decoder = WireDecoder(buffer)

        val tag = decoder.readTag()
        assertNotNull(tag)
        assertEquals(WireType.FIXED32, tag.wireType)
        assertEquals(fieldNr, tag.fieldNr)

        val value = decoder.readFloat()
        assertNotNull(value)
        assertEquals(testValue, value)

        decoder.close()
        assertTrue(buffer.exhausted())
    }
}
