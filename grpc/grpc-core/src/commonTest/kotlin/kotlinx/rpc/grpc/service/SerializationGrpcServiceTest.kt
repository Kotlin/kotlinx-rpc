/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.kotlinx.serialization.asCodecResolver
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@Serializable
class SerializableMessage(val value: String)

@Grpc
interface GrpcServiceSerializable {
    suspend fun plainString(value: String): String

    suspend fun serialization(value: SerializableMessage): SerializableMessage

    suspend fun krpc173(unit: Unit)

    suspend fun clientStreaming(flow: Flow<String>): String

    fun serverStreaming(value: String): Flow<String>

    fun bidiStreaming(flow: Flow<String>): Flow<String>
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

class SerializationGrpcServiceTest : BaseGrpcServiceTest() {
    @Test
    fun testSerializationCodec() = runServiceTest<GrpcServiceSerializable>(
        resolver = Json.asCodecResolver(),
        impl = GrpcServiceSerializableImpl(),
    ) { service ->
        assertEquals("test test", service.plainString("test"))

        assertEquals("test test", service.serialization(SerializableMessage("test")).value)
    }

    @Test
    fun krpc173() = runServiceTest<GrpcServiceSerializable>(
        resolver = Json.asCodecResolver(),
        impl = GrpcServiceSerializableImpl(),
    ) { service ->
        assertEquals(Unit, service.krpc173(Unit))
    }

    @Test
    fun clientStreaming() = runServiceTest<GrpcService>(
        resolver = Json.asCodecResolver(),
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals("test test", service.clientStreaming(flowOf("test", "test")))
    }

    @Test
    fun serverStreaming() = runServiceTest<GrpcService>(
        resolver = Json.asCodecResolver(),
        impl = GrpcServiceImpl(),
    ) { service ->
        assertEquals(listOf("test", "test"), service.serverStreaming("test").toList())
    }

    @Test
    fun bidiStreaming() = runServiceTest<GrpcService>(
        resolver = Json.asCodecResolver(),
        impl = GrpcServiceImpl(),
    ) { service ->
        assertContentEquals(
            expected = listOf("test test", "test test"),
            actual = service.bidiStreaming(flowOf("test", "test")).toList(),
        )
    }
}
