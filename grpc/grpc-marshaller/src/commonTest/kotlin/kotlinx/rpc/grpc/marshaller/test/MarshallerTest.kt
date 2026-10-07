/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.test

import kotlinx.rpc.grpc.marshaller.encodeToBuffer
import kotlinx.rpc.grpc.marshaller.decodeFromSource
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.WithGrpcMarshaller
import kotlinx.rpc.grpc.marshaller.decodeFromByteArray
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

@WithGrpcMarshaller(MyGrpcMarshaller::class)
data class MyMessage(
    val value: String
)

class MyGrpcMarshallerConfig(
    val appendHello: Boolean
): GrpcMarshallerConfig

object MyGrpcMarshaller: GrpcMarshaller<MyMessage> {
    override fun prepare(value: MyMessage, config: GrpcMarshallerConfig?): GrpcEncodedMessage {
        val appendHello = (config as? MyGrpcMarshallerConfig)?.appendHello ?: false
        val text = value.value + if (appendHello) "Hello" else ""
        return GrpcEncodedMessage.of(text.encodeToByteArray())
    }

    override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): MyMessage =
        MyMessage(reader.readByteArray().decodeToString())
}


class MarshallerTest {

    @Test
    fun `test custom marshaller`() {
        val msg = MyMessage("test")
        val encoded = grpcMarshallerOf<MyMessage>(typeOf<MyMessage>()).encodeToBuffer(msg)
        val decoded = grpcMarshallerOf<MyMessage>().decodeFromSource(encoded)
        assertEquals(msg, decoded)
    }

    @Test
    fun `test custom marshaller with default config`() {
        val msg = MyMessage("test")
        val config = MyGrpcMarshallerConfig(true)

        val firstEncoded = grpcMarshallerOf<MyMessage>(config).encodeToBuffer(msg)
        val secondEncoded = grpcMarshallerOf<MyMessage>().encodeToBuffer(msg)

        val firstDecoded = grpcMarshallerOf<MyMessage>().decodeFromSource(firstEncoded)
        val secondDecoded = grpcMarshallerOf<MyMessage>().decodeFromSource(secondEncoded)

        assertEquals(msg.value + "Hello", firstDecoded.value)
        assertEquals(msg, secondDecoded)
    }

    @Test
    fun `test custom marshaller with overwritten config`() {
        val msg = MyMessage("test")
        val config = MyGrpcMarshallerConfig(true)

        val marshaller = grpcMarshallerOf<MyMessage>(config)
        val firstEncoded = marshaller.encodeToBuffer(msg, MyGrpcMarshallerConfig(false))
        val secondEncoded = marshaller.encodeToBuffer(msg)

        val firstDecoded = marshaller.decodeFromSource(firstEncoded)
        val secondDecoded = marshaller.decodeFromSource(secondEncoded)

        assertEquals(msg.value, firstDecoded.value)
        assertEquals(msg.value + "Hello", secondDecoded.value)
    }

    @Test
    fun `decorator forwards size and configuration`() {
        val decorator = object : GrpcMarshaller<MyMessage> {
            override fun prepare(value: MyMessage, config: GrpcMarshallerConfig?): GrpcEncodedMessage {
                val message = MyGrpcMarshaller.prepare(value, config)
                return object : GrpcEncodedMessage {
                    override val size: Int = message.size
                    override fun writeTo(writer: GrpcMessageWriter) = message.writeTo(writer)
                }
            }

            override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): MyMessage =
                MyGrpcMarshaller.decode(reader, config)
        }

        val value = MyMessage("test")
        val config = MyGrpcMarshallerConfig(true)
        assertEquals(9, decorator.prepare(value, config).size)
        assertEquals("testHello", decorator.encodeToByteArray(value, config).decodeToString())
        val encoded = "testHello".encodeToByteArray()
        assertEquals(MyMessage("testHello"), decorator.decodeFromByteArray(encoded, config))
    }

}
