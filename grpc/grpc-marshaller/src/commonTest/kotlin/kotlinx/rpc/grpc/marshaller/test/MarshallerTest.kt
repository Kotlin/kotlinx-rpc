/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.test

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.WithGrpcMarshaller
import kotlinx.rpc.grpc.marshaller.marshallerOf
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
    override fun encode(value: MyMessage, config: GrpcMarshallerConfig?): Source = Buffer().apply {
        val appendHello = (config as? MyGrpcMarshallerConfig)?.appendHello ?: false
        writeString(value.value + if (appendHello) "Hello" else "")
    }

    override fun decode(source: Source, config: GrpcMarshallerConfig?): MyMessage {
        return MyMessage(source.readString())
    }
}


class MarshallerTest {

    @Test
    fun `test custom marshaller`() {
        val msg = MyMessage("test")
        val encoded = marshallerOf<MyMessage>(typeOf<MyMessage>()).encode(msg)
        val decoded = marshallerOf<MyMessage>().decode(encoded)
        assertEquals(msg, decoded)
    }

    @Test
    fun `test custom marshaller with default config`() {
        val msg = MyMessage("test")
        val config = MyGrpcMarshallerConfig(true)

        val firstEncoded = marshallerOf<MyMessage>(config).encode(msg)
        val secondEncoded = marshallerOf<MyMessage>().encode(msg)

        val firstDecoded = marshallerOf<MyMessage>().decode(firstEncoded)
        val secondDecoded = marshallerOf<MyMessage>().decode(secondEncoded)

        assertEquals(msg.value + "Hello", firstDecoded.value)
        assertEquals(msg, secondDecoded)
    }

    @Test
    fun `test custom marshaller with overwritten config`() {
        val msg = MyMessage("test")
        val config = MyGrpcMarshallerConfig(true)

        val marshaller = marshallerOf<MyMessage>(config)
        val firstEncoded = marshaller.encode(msg, MyGrpcMarshallerConfig(false))
        val secondEncoded = marshaller.encode(msg)

        val firstDecoded = marshaller.decode(firstEncoded)
        val secondDecoded = marshaller.decode(secondEncoded)

        assertEquals(msg.value, firstDecoded.value)
        assertEquals(msg.value + "Hello", secondDecoded.value)
    }

}
