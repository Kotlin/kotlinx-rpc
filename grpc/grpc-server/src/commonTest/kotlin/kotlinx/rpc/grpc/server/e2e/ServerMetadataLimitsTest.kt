/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.rawMethod
import kotlinx.rpc.grpc.server.testing.zeros

class ServerMetadataLimitsTest {
    @Test
    fun defaultMetadataLimitAcceptsSmallHeadersAndRejectsLargeHeaders() = grpcServerTest {
        start()
        val method = rawMethod(TestServiceMethods.EMPTY_CALL, GrpcMethodType.UNARY)

        // Leave room for the protocol headers that the client adds.
        assertContentEquals(ByteArray(0), client.unaryRpc(
            method,
            ByteArray(0),
            headers = GrpcMetadata { append("x-padding", "x".repeat(4096)) },
        ))

        assertGrpcStatus(GrpcStatusCode.INTERNAL) {
            client.unaryRpc(method, ByteArray(0), headers = GrpcMetadata { append("x-padding", "x".repeat(16384)) })
        }
    }

    @Test
    fun oversizedMetadataFailsWithoutBreakingTheConnection() = grpcServerTest {
        val responseGate = registerNewGate("in-flight response")
        start(serverConfig = { maxInboundMetadataSize = 1024 }) {
            unaryCall = { request ->
                if (request.responseSize == 9) {
                    responseGate.pass()
                }
                SimpleResponse { payload = zeros(request.responseSize) }
            }
        }

        val method = rawMethod(TestServiceMethods.EMPTY_CALL, GrpcMethodType.UNARY)
        val acceptedHeaders = GrpcMetadata { append("x-padding", "x".repeat(512)) }
        coroutineScope {
            val heldResponse = async { testService.unaryCall(SimpleRequest { responseSize = 9 }) }
            responseGate.awaitReached(AWAIT_TIMEOUT)

            assertContentEquals(ByteArray(0), client.unaryRpc(method, ByteArray(0), headers = acceptedHeaders))

            assertGrpcStatus(GrpcStatusCode.INTERNAL) {
                client.unaryRpc(
                    method,
                    ByteArray(0),
                    headers = GrpcMetadata { append("x-large", "a".repeat(2048)) },
                )
            }

            assertContentEquals(ByteArray(0), client.unaryRpc(method, ByteArray(0), headers = acceptedHeaders))
            responseGate.open()
            assertEquals(zeros(9), withTimeout(AWAIT_TIMEOUT) { heldResponse.await() }.payload)
        }
    }
}
