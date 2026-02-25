/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.serialization

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.marshaller.MarshallerConfig
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.rpc.grpc.marshaller.MessageMarshallerResolver
import kotlinx.rpc.grpc.marshaller.WithMarshaller
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@WithMarshaller(CustomResolverMessage.Companion::class)
class CustomResolverMessage(val value: String) {
    companion object Companion : MessageMarshaller<CustomResolverMessage> {
        override fun encode(value: CustomResolverMessage, config: MarshallerConfig?): Source {
            return Buffer().apply { writeString(value.value) }
        }

        override fun decode(source: Source, config: MarshallerConfig?): CustomResolverMessage {
            return CustomResolverMessage(source.readString())
        }
    }
}


@Grpc
interface GrpcService {
    suspend fun plainString(value: String): String

    suspend fun message(value: CustomResolverMessage): CustomResolverMessage

    suspend fun krpc173()

    suspend fun clientStreaming(flow: Flow<String>): String

    fun serverStreaming(value: String): Flow<String>

    fun bidiStreaming(flow: Flow<String>): Flow<String>
}

class GrpcServiceImpl : GrpcService {
    override suspend fun plainString(value: String): String {
        return "$value $value"
    }

    override suspend fun message(value: CustomResolverMessage): CustomResolverMessage {
        return CustomResolverMessage("${value.value} ${value.value}")
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
    fun testMarshallerResolver() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals("test test", service.plainString("test"))
    }

    @Test
    fun testAnnotationMarshaller() = runServiceTest<GrpcService>(
        resolver = simpleResolver,
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals("test test", service.message(CustomResolverMessage("test")).value)
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
        private val simpleResolver = MessageMarshallerResolver { kType ->
            when (kType.classifier) {
                Unit::class -> unitMarshaller
                String::class -> stringMarshaller
                else -> null
            }
        }

        val stringMarshaller = object : MessageMarshaller<String> {
            override fun encode(value: String, config: MarshallerConfig?): Source {
                return Buffer().apply { writeString(value) }
            }

            override fun decode(source: Source, config: MarshallerConfig?): String {
                return source.readString()
            }
        }

        val unitMarshaller = object : MessageMarshaller<Unit> {
            override fun encode(value: Unit, config: MarshallerConfig?): Source {
                return Buffer()
            }

            override fun decode(stream: Source, config: MarshallerConfig?) {
                check(stream.exhausted())
            }
        }
    }
}
