/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.service

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
}

@Grpc
interface GrpcServiceSerializable {
    suspend fun plainString(value: String): String

    suspend fun serialization(value: SerializableMessage): SerializableMessage
}

class GrpcServiceImpl : GrpcService {
    override suspend fun plainString(value: String): String {
        return "$value $value"
    }

    override suspend fun message(value: Message): Message {
        return Message("${value.value} ${value.value}")
    }
}

class GrpcServiceSerializableImpl : GrpcServiceSerializable {
    override suspend fun plainString(value: String): String {
        return "$value $value"
    }

    override suspend fun serialization(value: SerializableMessage): SerializableMessage {
        return SerializableMessage("${value.value} ${value.value}")
    }
}

class GrpcGeneratedServiceTest {
    @Test
    fun testCodecResolver() = runServiceTest<GrpcService>(
        resolver = stringResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals("test test", service.plainString("test"))
    }

    @Test
    fun testAnnotationCodec() = runServiceTest<GrpcService>(
        resolver = stringResolver,
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

        private val stringResolver = MessageCodecResolver { kType ->
            check(kType.classifier == String::class) { "Unsupported type: $kType" }
            simpleCodec
        }

        val simpleCodec = object : MessageCodec<String> {
            override fun encode(value: String): Source {
                return Buffer().apply { writeString(value) }
            }

            override fun decode(stream: Source): String {
                return stream.readString()
            }
        }
    }
}
