/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.append
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
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GrpcMetadataSizeTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    // The bounds are calibrated so the tests only pass when the configured limit is applied:
    // baseline call headers (< ~1 KiB) < METADATA_SIZE_LIMIT < oversized header < default limit (8 KiB).
    // An oversized header above the default limit would be rejected even if the configuration
    // were silently ignored; one below the limit but above baseline would never be rejected.
    private companion object {
        const val METADATA_SIZE_LIMIT = 2048
        const val OVERSIZED_HEADER_LENGTH = 4096
    }

    @Test
    fun `client rejects inbound metadata larger than configured maximum`() {
        val error = assertFailsWith<GrpcStatusException> {
            runGrpcTest(
                clientConfiguration = { maxInboundMetadataSize = METADATA_SIZE_LIMIT },
                serverInterceptors = serverInterceptor {
                    responseHeaders.append("large-metadata", "x".repeat(OVERSIZED_HEADER_LENGTH))
                    proceed(it)
                },
            ) {
                it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Echo" })
            }
        }

        assertMetadataSizeStatus(error)
    }

    @Test
    fun `client accepts inbound metadata within configured maximum`() {
        runGrpcTest(
            clientConfiguration = { maxInboundMetadataSize = METADATA_SIZE_LIMIT },
        ) {
            it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Echo" })
        }
    }

    @Test
    fun `server rejects inbound metadata larger than configured maximum`() {
        val error = assertFailsWith<GrpcStatusException> {
            runGrpcTest(
                serverConfiguration = { maxInboundMetadataSize = METADATA_SIZE_LIMIT },
                clientInterceptors = clientInterceptor {
                    requestHeaders.append("large-metadata", "x".repeat(OVERSIZED_HEADER_LENGTH))
                    proceed(it)
                },
            ) {
                it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Echo" })
            }
        }

        assertMetadataSizeStatus(error)
    }

    @Test
    fun `server accepts inbound metadata within configured maximum`() {
        runGrpcTest(
            serverConfiguration = { maxInboundMetadataSize = METADATA_SIZE_LIMIT },
        ) {
            it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Echo" })
        }
    }

    @Test
    fun `client rejects non-positive maximum inbound metadata size`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { maxInboundMetadataSize = 0 }
        }
    }

    @Test
    fun `server rejects non-positive maximum inbound metadata size`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { maxInboundMetadataSize = 0 }
        }
    }

    private fun assertMetadataSizeStatus(error: GrpcStatusException) {
        assertTrue(error.getStatus().statusCode in setOf(GrpcStatusCode.INTERNAL, GrpcStatusCode.RESOURCE_EXHAUSTED))
    }
}
