/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.codec.WithCodec
import kotlin.test.Test
import kotlin.test.assertContentEquals
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

@Grpc
interface GrpcService {
    suspend fun plainString(value: String): String

    suspend fun message(value: Message): Message

    suspend fun krpc173()

    suspend fun clientStreaming(flow: Flow<String>): String

    fun serverStreaming(value: String): Flow<String>

    fun bidiStreaming(flow: Flow<String>): Flow<String>
}

class GrpcServiceImpl : GrpcService {
    override suspend fun plainString(value: String): String {
        return "$value $value"
    }

    override suspend fun message(value: Message): Message {
        return Message("${value.value} ${value.value}")
    }

    override suspend fun krpc173() {
        doWork()
    }

    override suspend fun clientStreaming(flow: Flow<String>): String {
        return flow.toList().joinToString(" ")
    }

    override fun serverStreaming(value: String): Flow<String> {
        return flowOf(value, value)
    }

    override fun bidiStreaming(flow: Flow<String>): Flow<String> {
        return flow.map { "$it $it" }
    }
}

class CustomResolverGrpcServiceTest : BaseGrpcServiceTest() {
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
    fun krpc173() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals(Unit, service.krpc173())
    }

    @Test
    fun clientStreaming() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals("test test", service.clientStreaming(flowOf("test", "test")))
    }

    @Test
    fun serverStreaming() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals(listOf("test", "test"), service.serverStreaming("test").toList())
    }

    @Test
    fun bidiStreaming() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertContentEquals(
            expected = listOf("test test", "test test"),
            actual = service.bidiStreaming(flowOf("test", "test")).toList(),
        )
    }

    companion object {
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
                return Buffer()
            }

            override fun decode(stream: Source) {
                check(stream.exhausted())
            }
        }
    }
}
