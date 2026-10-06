/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.test

import kotlinx.io.Buffer
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.StreamingGrpcMarshaller
import kotlinx.rpc.grpc.marshaller.decodeFromByteArray
import kotlinx.rpc.grpc.marshaller.decodeFromSource
import kotlinx.rpc.grpc.marshaller.encodeToBuffer
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.BufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageReader
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageWriter
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class GrpcMessageTest {
    @Test
    fun byteArrayWriterChecksBoundsAndTracksState() {
        val writer = ByteArrayMessageWriter(ByteArray(3))

        assertEquals(0, writer.written)
        assertEquals(3, writer.remaining)
        assertFalse(writer.isComplete)
        assertFalse(writer.isFailed)

        writer.writeByte(1)
        writer.write(byteArrayOf(2, 3))

        assertContentEquals(byteArrayOf(1, 2, 3), writer.array)
        assertEquals(3, writer.written)
        assertEquals(0, writer.remaining)
        assertTrue(writer.isComplete)
        assertFalse(writer.isFailed)

        assertFailsWith<IllegalStateException> { writer.writeByte(4) }
        assertTrue(writer.isFailed)
    }

    @Test
    fun bufferWriterChecksSourceAndArrayBounds() {
        val invalidRangeWriter = BufferMessageWriter(Buffer(), 1)
        assertFailsWith<IndexOutOfBoundsException> {
            invalidRangeWriter.write(byteArrayOf(1), startIndex = 0, endIndex = 2)
        }
        assertTrue(invalidRangeWriter.isFailed)

        val shortArrayWriter = ByteArrayMessageWriter(ByteArray(1))
        assertFailsWith<IndexOutOfBoundsException> {
            shortArrayWriter.write(byteArrayOf(1, 2))
        }
        assertTrue(shortArrayWriter.isFailed)

        val shortSourceWriter = BufferMessageWriter(Buffer(), 2)
        val source = Buffer().apply { writeByte(1) }
        assertFailsWith<IndexOutOfBoundsException> { shortSourceWriter.write(source, 2) }
        assertTrue(shortSourceWriter.isFailed)
    }

    @Test
    fun encodedMessagesAreSingleUse() {
        val arrayMessage = GrpcEncodedMessage.of(byteArrayOf(1, 2, 3))
        val arrayWriter = ByteArrayMessageWriter(ByteArray(arrayMessage.size))
        arrayMessage.writeTo(arrayWriter)
        assertContentEquals(byteArrayOf(1, 2, 3), arrayWriter.array)
        assertFailsWith<IllegalStateException> {
            arrayMessage.writeTo(ByteArrayMessageWriter(ByteArray(arrayMessage.size)))
        }

        val bufferMessage = GrpcEncodedMessage.of(Buffer().apply { write(byteArrayOf(4, 5)) })
        val destination = Buffer()
        bufferMessage.writeTo(BufferMessageWriter(destination, bufferMessage.size))
        assertContentEquals(byteArrayOf(4, 5), destination.readByteArray())
        assertFailsWith<IllegalStateException> {
            bufferMessage.writeTo(BufferMessageWriter(Buffer(), bufferMessage.size))
        }
    }

    @Test
    fun readersCheckBounds() {
        val arrayReader = ByteArrayMessageReader(byteArrayOf(1, 2, 3))
        assertEquals(1, arrayReader.readByte())
        assertContentEquals(byteArrayOf(2, 3), arrayReader.readByteArray())
        assertEquals(0, arrayReader.remaining)
        assertFailsWith<IndexOutOfBoundsException> { arrayReader.readByte() }

        val bufferReader = BufferMessageReader(
            Buffer().apply { write(byteArrayOf(1, 2)) },
            size = 2,
        )
        assertFailsWith<IndexOutOfBoundsException> {
            bufferReader.readTo(ByteArray(1), startIndex = -1)
        }
        assertEquals(2, bufferReader.remaining)
        assertFailsWith<IndexOutOfBoundsException> { bufferReader.readByteArray(3) }
    }

    @Test
    fun bufferReaderSharesItsSource() {
        val buffer = Buffer().apply { write(byteArrayOf(1, 2, 3)) }
        val reader = BufferMessageReader(buffer, size = 3)

        assertSame(buffer, reader.asSource())
        assertEquals(1, reader.asSource().readByte())
        assertEquals(0, reader.remaining)
    }

    @Test
    fun byteArrayReaderCopiesItsSourceOnce() {
        val bytes = byteArrayOf(1, 2, 3)
        val reader = ByteArrayMessageReader(bytes)
        assertEquals(1, reader.readByte())

        val source = assertIs<Buffer>(reader.asSource())
        bytes[1] = 9

        assertSame(source, reader.asSource())
        assertContentEquals(byteArrayOf(2, 3), source.readByteArray())
        assertEquals(0, reader.remaining)
    }

    @Test
    fun callerHelpersRoundTrip() {
        val value = "hello"

        val bytes = StringMarshaller.encodeToByteArray(value)
        assertEquals(value, StringMarshaller.decodeFromByteArray(bytes))

        val buffer = StringMarshaller.encodeToBuffer(value)
        assertEquals(value, StringMarshaller.decodeFromSource(buffer))

        val source = StringMarshaller.encodeToBuffer(value).peek()
        assertEquals(value, StringMarshaller.decodeFromSource(source))
    }

    @Test
    fun streamingMarshallerBridgesSinkAndSourceImplementations() {
        val value = "hello streaming marshaller"

        val bytes = StreamingStringMarshaller.encodeToByteArray(value)

        assertContentEquals(value.encodeToByteArray(), bytes)
        assertEquals(value, StreamingStringMarshaller.decodeFromByteArray(bytes))
    }

    @Test
    fun emptyMessagesAreSupported() {
        val message = GrpcEncodedMessage.of(byteArrayOf())
        val writer = ByteArrayMessageWriter(ByteArray(0))

        assertEquals(0, message.size)
        assertTrue(writer.isComplete)
        message.writeTo(writer)
        assertTrue(writer.isComplete)

        val reader = ByteArrayMessageReader(byteArrayOf())
        assertContentEquals(byteArrayOf(), reader.readByteArray())
        assertEquals(0, reader.remaining)
        assertTrue(reader.asSource().exhausted())
    }
}

private object StringMarshaller : GrpcMarshaller<String> {
    override fun prepare(value: String, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        GrpcEncodedMessage.of(value.encodeToByteArray())

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): String =
        reader.readByteArray().decodeToString()
}

private object StreamingStringMarshaller : StreamingGrpcMarshaller<String>() {
    override fun encode(value: String, sink: Sink, config: GrpcMarshallerConfig?) {
        sink.write(Buffer().apply { writeString(value) }, value.encodeToByteArray().size.toLong())
    }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): String = source.readString()
}
