/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.test

import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageWriter
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

class GrpcMarshallerHelpersTest {
    @Test
    fun encodeToByteArrayWritesIntoTheReturnedArray() {
        val value = byteArrayOf(1, 2, 3)
        val expectedConfig = object : GrpcMarshallerConfig {}
        var prepareCalls = 0
        var sizeReads = 0
        var writeCalls = 0
        var destination: ByteArray? = null
        val marshaller = object : GrpcMarshaller<ByteArray> {
            override fun prepare(value: ByteArray, config: GrpcMarshallerConfig?): GrpcEncodedMessage {
                prepareCalls++
                assertSame(expectedConfig, config)
                return object : GrpcEncodedMessage {
                    override val size: Int
                        get() {
                            sizeReads++
                            return value.size
                        }

                    override fun writeTo(writer: GrpcMessageWriter) {
                        writeCalls++
                        val arrayWriter = assertIs<ByteArrayMessageWriter>(writer)
                        destination = arrayWriter.array
                        arrayWriter.writeDirect { array, startIndex, endIndex ->
                            assertEquals(value.size, endIndex - startIndex)
                            value.copyInto(array, startIndex)
                        }
                    }
                }
            }

            override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): ByteArray =
                reader.readByteArray()
        }

        val result = marshaller.encodeToByteArray(value, expectedConfig)

        assertSame(destination, result)
        assertContentEquals(value, result)
        assertEquals(1, prepareCalls)
        assertEquals(1, sizeReads)
        assertEquals(1, writeCalls)
    }

    @Test
    fun encodeToByteArraySupportsEmptyMessages() {
        val marshaller = WritingMarshaller(0) { writer -> writer.write(byteArrayOf()) }

        assertContentEquals(byteArrayOf(), marshaller.encodeToByteArray(Unit))
    }

    @Test
    fun encodeToByteArrayRejectsNegativeSizesBeforeWriting() {
        var writeCalls = 0
        val marshaller = WritingMarshaller(-1) { writeCalls++ }

        assertFailsWith<IllegalArgumentException> { marshaller.encodeToByteArray(Unit) }
        assertEquals(0, writeCalls)
    }

    @Test
    fun encodeToByteArrayRejectsIncompleteWrites() {
        val marshaller = WritingMarshaller(2) { it.writeByte(1) }

        assertFailsWith<IllegalStateException> { marshaller.encodeToByteArray(Unit) }
    }

    @Test
    fun encodeToByteArrayRejectsOverflow() {
        val marshaller = WritingMarshaller(1) { it.write(byteArrayOf(1, 2)) }

        assertFailsWith<IndexOutOfBoundsException> { marshaller.encodeToByteArray(Unit) }
    }

    @Test
    fun encodeToByteArrayRejectsSwallowedWriteFailures() {
        val marshaller = WritingMarshaller(1) { writer ->
            writer.writeByte(1)
            assertFailsWith<IllegalStateException> { writer.writeByte(2) }
        }

        assertFailsWith<IllegalStateException> { marshaller.encodeToByteArray(Unit) }
    }
}

private class WritingMarshaller(
    private val messageSize: Int,
    private val write: (GrpcMessageWriter) -> Unit,
) : GrpcMarshaller<Unit> {
    override fun prepare(value: Unit, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
        object : GrpcEncodedMessage {
            override val size: Int = messageSize

            override fun writeTo(writer: GrpcMessageWriter) = write(writer)
        }

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): Unit = Unit
}
