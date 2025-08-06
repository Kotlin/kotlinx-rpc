/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.service

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.codec.WithCodec
import kotlinx.rpc.grpc.codec.kotlinx.serialization.asCodecResolver
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@WithCodec(Message.Companion::class)
class Message(val value: String) {
    companion object : MessageCodec<Message> {
        override fun encode(value: Message): Source {
            return Buffer().apply { writeString(value.value) }
        }
        override fun decode(stream: Source): Message {
            return Message(stream.readString())
        }
    }
}

@Serializable
class SerializableMessage(val value: String)

@Grpc
interface GrpcService {
    suspend fun plainString(value: String): String

    suspend fun message(value: Message): Message

    suspend fun krpc173(unit: Unit)
}

@Grpc
interface GrpcServiceSerializable {
    suspend fun plainString(value: String): String

    suspend fun serialization(value: SerializableMessage): SerializableMessage

    suspend fun krpc173(unit: Unit)
}

private suspend fun doWork(): String {
    delay(1)
    return "qwerty"
}

class GrpcServiceImpl : GrpcService {
    override suspend fun plainString(value: String): String {
        return "$value $value"
    }

    override suspend fun message(value: Message): Message {
        return Message("${value.value} ${value.value}")
    }

    override suspend fun krpc173(unit: Unit) {
        doWork()
    }
}

class GrpcServiceSerializableImpl : GrpcServiceSerializable {
    override suspend fun plainString(value: String): String {
        return "$value $value"
    }

    override suspend fun serialization(value: SerializableMessage): SerializableMessage {
        return SerializableMessage("${value.value} ${value.value}")
    }

    override suspend fun krpc173(unit: Unit) {
        doWork()
    }
}

class GrpcGeneratedServiceTest {
    @Test
    fun testCodecResolver() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals("test test", service.plainString("test"))
    }

    @Test
    fun testAnnotationCodec() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals("test test", service.message(Message("test")).value)
    }

    @Test
    fun testSerializationCodec() = runServiceTest<GrpcServiceSerializable>(
        resolver = Json.asCodecResolver(),
        impl = GrpcServiceSerializableImpl(),
    ) { service ->
        assertEquals("test test", service.plainString("test"))

        assertEquals("test test", service.serialization(SerializableMessage("test")).value)
    }

    @Test
    fun testKrpc173Plain() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals(Unit, service.krpc173(Unit))
    }

    @Test
    fun testKrpc173Serialization() = runServiceTest<GrpcServiceSerializable>(
        resolver = Json.asCodecResolver(),
        impl = GrpcServiceSerializableImpl(),
    ) { service ->
        assertEquals(Unit, service.krpc173(Unit))
    }

    private inline fun <@Grpc reified Service : Any> runServiceTest(
        resolver: MessageCodecResolver,
        impl: Service,
        crossinline block: suspend (Service) -> Unit,
    ) = runTest {
        val server = GrpcServer(
            port = PORT,
            messageCodecResolver = resolver,
            parentContext = coroutineContext,
            builder = {
                registerService<Service> { impl }
            }
        )

        server.start()

        val client = GrpcClient("localhost", PORT, messageCodecResolver = resolver) {
            usePlaintext()
        }

        val service = client.withService<Service>()

        block(service)

        client.shutdown()
        client.awaitTermination()
        server.shutdown()
        server.awaitTermination()
    }

    companion object {
        private const val PORT = 8082

        private val simpleResolver = MessageCodecResolver { kType ->
            when (kType.classifier) {
                Unit::class -> unitCodec
                String::class -> stringCodec
                else -> error("Unsupported type: $kType")
            }
        }

        val stringCodec = object : MessageCodec<String> {
            override fun encode(value: String): Source {
                return Buffer().apply { writeString(value) }
            }

            override fun decode(stream: Source): String {
                return stream.readString()
            }
        }

        val unitCodec = object : MessageCodec<Unit> {
            override fun encode(value: Unit): Source {
                return Buffer().apply { writeString("Unit") }
            }

            override fun decode(stream: Source) {
                assertEquals("Unit", stream.readString())
            }
        }
    }
}
