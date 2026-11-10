/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageWriter
import kotlinx.rpc.protobuf.ProtobufEncodingException
import kotlinx.rpc.protobuf.internal.ArrayOutputSegments
import kotlinx.rpc.protobuf.internal.BufferOutputSegments
import kotlinx.rpc.protobuf.internal.Int32
import kotlinx.rpc.protobuf.internal.Int64
import kotlinx.rpc.protobuf.internal.SInt32
import kotlinx.rpc.protobuf.internal.SInt64
import kotlinx.rpc.protobuf.internal.UNKNOWN_SIZE
import kotlinx.rpc.protobuf.internal.UInt32
import kotlinx.rpc.protobuf.internal.UInt64
import kotlinx.rpc.protobuf.internal.Utf8
import kotlinx.rpc.protobuf.internal.Varint
import kotlinx.rpc.protobuf.internal.WireOutput
import kotlinx.rpc.protobuf.internal.WriterOutputSegments
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** The chunk sizes that each test uses. The small sizes make every slow path run. */
private val CHUNK_SIZES = listOf(1, 2, 3, 7, 8192)

private const val CAPACITY = 256

private val STRING = "Grüße, 你好, 🙂"

class WireOutputTest {
    @Test
    fun writeFunctionsGiveTheSameBytes() {
        val expected = expectedBytes()
        for (chunk in CHUNK_SIZES) {
            val segments = SplitOutputSegments(CAPACITY, chunk)
            val output = WireOutput(segments)
            output.writeVarint(UInt32(300))
            output.writeVarint(UInt64(-1L))
            output.writeVarint(Int32(-1))
            output.writeVarint(Int32(300))
            output.writeVarint(Int64(-1L))
            output.writeVarint(SInt32(-1))
            output.writeVarint(SInt64(Long.MIN_VALUE))
            output.writeFixed32(0x01020304)
            output.writeFixed64(0x0102030405060708L)
            output.writeByte(0x42)
            output.writeUtf8(STRING, Utf8.encodedLength(STRING))
            output.flush()
            assertEquals(expected.size, output.written)
            assertContentEquals(expected, segments.bytes())
        }
    }

    @Test
    fun writePastCapacityThrows() {
        for (chunk in CHUNK_SIZES) {
            val output = WireOutput(SplitOutputSegments(3, chunk))
            assertFailsWith<ProtobufEncodingException> { output.writeFixed32(0x01020304) }
        }
    }

    @Test
    fun flushContinuesAfterFlushedBytes() {
        val array = ByteArray(8)
        val output = WireOutput(ArrayOutputSegments(array, 0, 8))
        output.writeVarint(UInt32(300))
        output.flush()
        assertEquals(2, output.written)

        output.writeFixed32(0x01020304)
        output.flush()
        assertEquals(6, output.written)
        assertContentEquals(bytes(0xAC, 0x02, 4, 3, 2, 1, 0, 0), array)
    }

    @Test
    fun bufferOutputSegmentsWithKnownSize() {
        val input = ByteArray(9000) { (it % 251).toByte() }
        val buffer = Buffer()
        val output = WireOutput(BufferOutputSegments(buffer, input.size))
        output.writeBytes(input, 0, input.size)
        output.flush()
        assertEquals(input.size, output.written)
        assertContentEquals(input, buffer.readByteArray())

        val full = Buffer()
        val fullOutput = WireOutput(BufferOutputSegments(full, 2))
        fullOutput.writeVarint(UInt32(300))
        fullOutput.flush()
        assertFailsWith<ProtobufEncodingException> { fullOutput.writeByte(0) }
    }

    @Test
    fun bufferOutputSegmentsWithUnknownSize() {
        val input = ByteArray(20_000) { (it % 251).toByte() }
        val buffer = Buffer()
        val output = WireOutput(BufferOutputSegments(buffer, UNKNOWN_SIZE))
        output.writeBytes(input, 0, input.size)
        output.flush()
        assertEquals(input.size, output.written)
        assertContentEquals(input, buffer.readByteArray())
    }

    @Test
    fun writeBufferDoesntChangeSource() {
        val source = bufferOf(bytes(1, 2, 3, 4), 2)
        val segments = SplitOutputSegments(CAPACITY, 8192)
        val output = WireOutput(segments)
        output.writeBuffer(source)
        output.flush()
        assertEquals(4L, source.size)
        assertContentEquals(bytes(1, 2, 3, 4), segments.bytes())
        assertContentEquals(bytes(1, 2, 3, 4), source.readByteArray())
    }

    @Test
    fun closedOutputThrows() {
        val output = WireOutput(SplitOutputSegments(CAPACITY, 4))
        output.writeByte(1)
        output.close()
        output.close()
        assertFailsWith<IllegalStateException> { output.writeByte(0) }
        assertFailsWith<IllegalStateException> { output.writeBytes(bytes(0), 0, 0) }
        assertFailsWith<IllegalStateException> { output.flush() }
    }

    @Test
    fun writerOutputSegmentsOverByteArrayWriter() {
        val expected = bytes(0xAC, 0x02, 4, 3, 2, 1)
        val writer = ByteArrayMessageWriter(ByteArray(expected.size))
        val output = WireOutput(WriterOutputSegments(writer))
        output.writeVarint(UInt32(300))
        output.writeFixed32(0x01020304)
        output.flush()
        assertEquals(0, writer.remaining)
        assertContentEquals(expected, writer.array)
    }

    @Test
    fun arrayOutputSegmentsWriteOnlyTheirRange() {
        val array = ByteArray(8) { 9 }
        val output = WireOutput(ArrayOutputSegments(array, 2, 6))
        output.writeFixed32(0x01020304)
        assertFailsWith<ProtobufEncodingException> { output.writeByte(0) }
        assertContentEquals(bytes(9, 9, 4, 3, 2, 1, 9, 9), array)
    }

    @Test
    fun arrayOutputSegmentsOverflowAfterFlush() {
        val array = ByteArray(8) { 9 }
        val output = WireOutput(ArrayOutputSegments(array, 2, 6))
        output.writeByte(1)
        output.writeByte(2)
        output.flush()
        output.writeByte(3)
        output.writeByte(4)
        assertFailsWith<ProtobufEncodingException> { output.writeByte(5) }
        assertContentEquals(bytes(9, 9, 1, 2, 3, 4, 9, 9), array)
    }

    @Test
    fun writerOutputSegmentsWriteMoreThanOneScratchArray() {
        val input = ByteArray(20_000) { (it % 251).toByte() }
        val writer = ByteArrayMessageWriter(ByteArray(input.size))
        val output = WireOutput(WriterOutputSegments(writer))
        output.writeBytes(input, 0, input.size)
        output.flush()
        assertEquals(0, writer.remaining)
        assertContentEquals(input, writer.array)
    }

    @Test
    fun writerOutputSegmentsOverflowThrows() {
        val output = WireOutput(WriterOutputSegments(ByteArrayMessageWriter(ByteArray(3))))
        assertFailsWith<ProtobufEncodingException> { output.writeFixed32(1) }
    }

    @Test
    fun valuesThatDontFitThrow() {
        for (chunk in CHUNK_SIZES) {
            assertFailsWith<ProtobufEncodingException> {
                WireOutput(SplitOutputSegments(3, chunk)).writeVarint(UInt64(-1L))
            }
            assertFailsWith<ProtobufEncodingException> {
                WireOutput(SplitOutputSegments(3, chunk)).writeVarint(Int32(-1))
            }
            assertFailsWith<ProtobufEncodingException> {
                WireOutput(SplitOutputSegments(3, chunk)).writeFixed64(1L)
            }
            assertFailsWith<ProtobufEncodingException> {
                WireOutput(SplitOutputSegments(3, chunk)).writeBytes(ByteArray(4), 0, 4)
            }
            assertFailsWith<ProtobufEncodingException> {
                WireOutput(SplitOutputSegments(3, chunk)).writeUtf8("\u20AC\u20AC", Utf8.encodedLength("\u20AC\u20AC"))
            }
        }
    }

    @Test
    fun bufferOutputSegmentsContinueAfterFlush() {
        for (size in listOf(6, UNKNOWN_SIZE)) {
            val buffer = Buffer()
            val output = WireOutput(BufferOutputSegments(buffer, size))
            output.writeBytes(bytes(1, 2, 3), 0, 3)
            output.flush()
            output.writeBytes(bytes(4, 5, 6), 0, 3)
            output.flush()
            assertEquals(6, output.written)
            assertContentEquals(bytes(1, 2, 3, 4, 5, 6), buffer.readByteArray())
        }
    }

    @Test
    fun writeBufferAcrossSegments() {
        val source = bufferOf(bytes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 3)
        for (chunk in CHUNK_SIZES) {
            val segments = SplitOutputSegments(CAPACITY, chunk)
            val output = WireOutput(segments)
            output.writeBuffer(source)
            output.flush()
            assertContentEquals(bytes(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), segments.bytes())
        }
        assertEquals(10L, source.size)
    }

    /** Builds the bytes that the writes in [writeFunctionsGiveTheSameBytes] must give, with the kernels. */
    private fun expectedBytes(): ByteArray {
        val fixed32 = ByteArray(4) { (0x01020304 ushr (8 * it)).toByte() }
        val fixed64 = ByteArray(8) { (0x0102030405060708L ushr (8 * it)).toByte() }
        return varintBytes(UInt32(300)) +
            varintBytes(UInt64(-1L)) +
            varintBytes(Varint.bits(Int32(-1))) +
            varintBytes(Varint.bits(Int32(300))) +
            varintBytes(Varint.bits(Int64(-1L))) +
            varintBytes(Varint.bits(SInt32(-1))) +
            varintBytes(Varint.bits(SInt64(Long.MIN_VALUE))) +
            fixed32 +
            fixed64 +
            bytes(0x42) +
            utf8Bytes(STRING)
    }
}
