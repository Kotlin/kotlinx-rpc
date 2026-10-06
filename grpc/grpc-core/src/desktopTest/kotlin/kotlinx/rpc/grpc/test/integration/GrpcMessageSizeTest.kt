/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GrpcMessageSizeTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `client defaults to a four MiB inbound message limit`() {
        val error = assertFailsWith<GrpcStatusException> {
            runGrpcTest(
                serverConfiguration = { maxInboundMessageSize = 8 * 1024 * 1024 },
            ) {
                // The serialized response includes overhead in addition to this four MiB string.
                it.withService<EchoService>().UnaryEcho(EchoRequest { message = "x".repeat(4 * 1024 * 1024) })
            }
        }
        assertEquals(GrpcStatusCode.RESOURCE_EXHAUSTED, error.getStatus().statusCode)
    }

    @Test
    fun `server defaults to a four MiB inbound message limit`() {
        val error = assertFailsWith<GrpcStatusException> {
            runGrpcTest(
                clientConfiguration = { maxInboundMessageSize = 8 * 1024 * 1024 },
            ) {
                it.withService<EchoService>().UnaryEcho(EchoRequest { message = "x".repeat(4 * 1024 * 1024) })
            }
        }
        assertEquals(GrpcStatusCode.RESOURCE_EXHAUSTED, error.getStatus().statusCode)
    }

    @Test
    fun `client rejects inbound messages larger than configured maximum`() {
        val error = assertFailsWith<GrpcStatusException> {
            runGrpcTest(clientConfiguration = { maxInboundMessageSize = 128 }) {
                it.withService<EchoService>().UnaryEcho(EchoRequest { message = "x".repeat(1024) })
            }
        }

        assertEquals(GrpcStatusCode.RESOURCE_EXHAUSTED, error.getStatus().statusCode)
    }

    @Test
    fun `server rejects inbound messages larger than configured maximum`() {
        val error = assertFailsWith<GrpcStatusException> {
            runGrpcTest(serverConfiguration = { maxInboundMessageSize = 128 }) {
                it.withService<EchoService>().UnaryEcho(EchoRequest { message = "x".repeat(1024) })
            }
        }

        assertEquals(GrpcStatusCode.RESOURCE_EXHAUSTED, error.getStatus().statusCode)
    }

    @Test
    fun `client rejects negative maximum inbound message size`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { maxInboundMessageSize = -1 }
        }
    }

    @Test
    fun `server rejects negative maximum inbound message size`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { maxInboundMessageSize = -1 }
        }
    }
}
