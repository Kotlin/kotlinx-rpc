/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.codec
import kotlinx.rpc.grpc.test.AllPrimitives
import kotlinx.rpc.grpc.test.Enum
import kotlinx.rpc.grpc.test.UnknownFieldsAll
import kotlinx.rpc.grpc.test.UnknownFieldsSubset
import kotlinx.rpc.grpc.test.asInternal
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.grpc.test.presence
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


@Grpc(protoServiceName = "Service")
private interface ClientTestService {
    suspend fun serverTest(message: UnknownFieldsAll): UnknownFieldsAll
    suspend fun clientTest(message: UnknownFieldsAll): UnknownFieldsSubset
}

@Grpc(protoServiceName = "Service")
private interface ServerTestService {
    suspend fun serverTest(message: UnknownFieldsSubset): UnknownFieldsSubset
    suspend fun clientTest(message: UnknownFieldsAll): UnknownFieldsAll
}

private object ServerServiceImpl : ServerTestService {
    override suspend fun serverTest(message: UnknownFieldsSubset): UnknownFieldsSubset = message
    override suspend fun clientTest(message: UnknownFieldsAll): UnknownFieldsAll = message
}

class GrpcCodecConfigTest : GrpcTestBase() {

    override fun RpcServer.registerServices() {
        registerService<ServerTestService> { ServerServiceImpl }
    }

    private val unknownFieldsAllMessage = UnknownFieldsAll {
        field1 = 123
        intMissing = 456
        allPrimitivesMissing = AllPrimitives {
            int32 = 7
        }
        enumMissing = Enum.ONE
        testOneof = UnknownFieldsAll.TestOneof.OneofString("oneof value")
    }


    @Test
    fun `test protobuf discardUnknownFields codec config in server config`() = runGrpcTest(
        serverConfiguration = { codecConfig = ProtobufConfig(discardUnknownFields = true) }
    ) { client ->
        val service = client.withService<ClientTestService>()
        val message = unknownFieldsAllMessage
        val response = service.serverTest(message)

        // the server should have discarded unknown fields
        response.run {
            assertEquals(message.field1, field1)
            assertFalse(response.presence.hasIntMissing)
            assertFalse(response.presence.hasAllPrimitivesMissing)
            assertFalse(response.presence.hasEnumMissing)
            assertNull(response.testOneof)
        }
    }

    @Test
    fun `test unknown fields presents by default - server`() = runGrpcTest { client ->
        val service = client.withService<ClientTestService>()
        val message = unknownFieldsAllMessage
        val response = service.serverTest(message)

        // the server should have preserved unknown fields
        assertEquals(message, response)
    }

    @Test
    fun `test protobuf discardUnknownFields codec config in client config`() = runGrpcTest(
        clientConfiguration = { codecConfig = ProtobufConfig(discardUnknownFields = true) }
    ) { client ->
        val service = client.withService<ClientTestService>()
        val message = unknownFieldsAllMessage
        val response = service.clientTest(message)

        // the client should have discarded unknown fields
        assertEquals(0, response.asInternal()._unknownFields.size)
    }


    @Test
    fun `test unknown fields presents by default - client`() = runGrpcTest { client ->
        val service = client.withService<ClientTestService>()
        val message = unknownFieldsAllMessage
        val response = service.clientTest(message)

        // the server should have preserved unknown fields
        assertTrue(response.asInternal()._unknownFields.size > 0)

        // encode and decode as UnknownFieldsAll
        val codec = codec<UnknownFieldsAll>()
        val decoded = codec.decode(codec.encode(message))
        // should have preserved all fields
        assertEquals(message, decoded)
    }


}