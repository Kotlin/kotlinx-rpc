/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.grpc.descriptor

import io.grpc.Drainable
import io.grpc.KnownLength
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.internal.MessageFramer
import io.grpc.internal.StatsTraceContext
import io.grpc.internal.WritableBufferAllocator
import io.grpc.internal.WritableBuffer
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class EncodedMessageInputStreamTest {
    private val bytes = byteArrayOf(0, 1, 127, -1)

    @Test
    fun streamHasKnownLengthAndDrainsTheSameBytes() {
        val marshaller = TestMarshaller()
        val stream = descriptor(marshaller).requestMarshaller.stream(bytes)
        assertIs<KnownLength>(stream)
        assertIs<Drainable>(stream)
        assertEquals(bytes.size, stream.available())
        assertEquals(0, marshaller.writes)

        val output = ByteArrayOutputStream()
        assertEquals(bytes.size, stream.drainTo(output))
        assertContentEquals(bytes, output.toByteArray())
        assertEquals(1, marshaller.writes)
        assertEquals(0, stream.available())
        assertEquals(-1, stream.read())
        assertEquals(0, stream.drainTo(output))
    }

    @Test
    fun readFallbackMaterializesOnceAndDrainUsesOnlyUnreadBytes() {
        val marshaller = TestMarshaller()
        val stream = descriptor(marshaller).requestMarshaller.stream(bytes)
        assertEquals(0, stream.read())
        assertEquals(1, marshaller.writes)
        assertEquals(bytes.size - 1, stream.available())

        val output = ByteArrayOutputStream()
        assertEquals(bytes.size - 1, (stream as Drainable).drainTo(output))
        assertContentEquals(bytes.copyOfRange(1, bytes.size), output.toByteArray())
        assertEquals(1, marshaller.writes)
    }

    @Test
    fun parseAcceptsKnownAndUnknownLengthStreams() {
        val marshaller = TestMarshaller()
        val requestMarshaller = descriptor(marshaller).requestMarshaller
        assertContentEquals(bytes, requestMarshaller.parse(requestMarshaller.stream(bytes)))
        assertContentEquals(bytes, requestMarshaller.parse(ByteArrayInputStream(bytes)))
        assertContentEquals(bytes, requestMarshaller.parse(object : InputStream(), KnownLength {
            private val input = ByteArrayInputStream(bytes)

            override fun available(): Int = input.available()

            override fun read(): Int = input.read()

            override fun read(destination: ByteArray, offset: Int, length: Int): Int =
                input.read(destination, offset, minOf(length, 1))
        }))
    }

    @Test
    fun failedOrIncompleteWritesAreRejectedWithMarshallerName() {
        val marshaller = TestMarshaller { error("broken encoder") }
        val stream = descriptor(marshaller).requestMarshaller.stream(bytes) as Drainable
        val failure = assertFailsWith<IllegalStateException> { stream.drainTo(ByteArrayOutputStream()) }
        assertTrue(failure.message.orEmpty().contains(TestMarshaller::class.java.name))
        assertTrue(failure.cause?.message.orEmpty().contains("broken encoder"))

        val incomplete = TestMarshaller { writer -> writer.writeByte(0) }
        val incompleteStream = descriptor(incomplete).requestMarshaller.stream(bytes) as Drainable
        val incompleteFailure = assertFailsWith<IllegalStateException> {
            incompleteStream.drainTo(ByteArrayOutputStream())
        }
        assertTrue(incompleteFailure.cause?.message.orEmpty().contains("1 of 4 bytes"))
    }

    @Test
    fun outputFailureRejectsTheMessage() {
        val stream = descriptor(TestMarshaller()).requestMarshaller.stream(bytes) as Drainable
        val failure = assertFailsWith<IllegalStateException> {
            stream.drainTo(object : ByteArrayOutputStream() {
                override fun write(bytes: ByteArray, offset: Int, length: Int) {
                    throw IOException("output failed")
                }
            })
        }
        assertIs<IOException>(failure.cause)
    }

    @Test
    fun grpcJavaRejectsOversizedMessageBeforeEncoding() {
        val marshaller = TestMarshaller()
        val stream = descriptor(marshaller).requestMarshaller.stream(bytes)
        val framer = MessageFramer(
            { _, _, _, _ -> error("Frame must not be delivered") },
            WritableBufferAllocator { error("Buffer must not be allocated") },
            StatsTraceContext.NOOP,
        )
        framer.setMaxOutboundMessageSize(bytes.size - 1)

        val failure = assertFailsWith<StatusRuntimeException> { framer.writePayload(stream) }
        assertEquals(Status.Code.RESOURCE_EXHAUSTED, failure.status.code)
        assertEquals(0, marshaller.writes)
    }

    @Test
    fun grpcJavaFramesTheSamePayloadBytes() {
        val frames = ByteArrayOutputStream()
        val framer = MessageFramer(
            { frame, _, _, _ -> if (frame != null) frames.write((frame as RecordingBuffer).bytes()) },
            WritableBufferAllocator { RecordingBuffer(it) },
            StatsTraceContext.NOOP,
        )
        framer.writePayload(descriptor(TestMarshaller()).requestMarshaller.stream(bytes))
        framer.flush()

        assertContentEquals(byteArrayOf(0, 0, 0, 0, bytes.size.toByte()) + bytes, frames.toByteArray())
    }

    @Test
    fun grpcJavaTurnsWriteFailureIntoCallFailure() {
        val marshaller = TestMarshaller { error("broken encoder") }
        val framer = MessageFramer(
            { _, _, _, _ -> error("Failed message must not be delivered") },
            WritableBufferAllocator { RecordingBuffer(it) },
            StatsTraceContext.NOOP,
        )
        val failure = assertFailsWith<StatusRuntimeException> {
            framer.writePayload(descriptor(marshaller).requestMarshaller.stream(bytes))
        }
        assertEquals(Status.Code.INTERNAL, failure.status.code)
        assertTrue(failure.cause?.message.orEmpty().contains(TestMarshaller::class.java.name))
        assertEquals(1, marshaller.writes)
    }

    private fun descriptor(marshaller: GrpcMarshaller<ByteArray>) = methodDescriptor(
        fullMethodName = "test/encode",
        requestMarshaller = marshaller,
        responseMarshaller = marshaller,
        type = GrpcMethodType.UNARY,
        schemaDescriptor = null,
        idempotent = false,
        safe = false,
        sampledToLocalTracing = false,
    )

    private class TestMarshaller(
        private val encode: (GrpcMessageWriter) -> Unit = { writer -> writer.write(byteArrayOf(0, 1, 127, -1)) },
    ) : GrpcMarshaller<ByteArray> {
        var writes: Int = 0

        override fun prepare(value: ByteArray, config: GrpcMarshallerConfig?): GrpcEncodedMessage {
            return object : GrpcEncodedMessage {
                override val size: Int = value.size

                override fun writeTo(writer: GrpcMessageWriter) {
                    writes++
                    encode(writer)
                }
            }
        }

        override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): ByteArray =
            reader.readByteArray()
    }

    private class RecordingBuffer(private val capacity: Int) : WritableBuffer {
        private val output = ByteArrayOutputStream()

        override fun write(bytes: ByteArray, offset: Int, length: Int) {
            output.write(bytes, offset, length)
        }

        override fun write(value: Byte) {
            output.write(value.toInt())
        }

        override fun writableBytes(): Int = capacity - output.size()

        override fun readableBytes(): Int = output.size()

        override fun release() {}

        fun bytes(): ByteArray = output.toByteArray()
    }
}
