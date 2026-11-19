/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import io.grpc.testing.integration.invoke
import kotlin.reflect.typeOf
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.internal.serviceDescriptor
import kotlinx.rpc.grpc.server.GrpcMutableHandlerRegistry
import kotlinx.rpc.grpc.server.internal.unaryServerMethodDefinition
import kotlinx.rpc.grpc.server.serverServiceDefinition
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.rawMethod
import kotlinx.rpc.grpc.server.testing.zeros

class ServerMessageLimitsTest {
    @Test
    fun defaultMessageLimit() = messageLimitTest(limit = 4 * 1024 * 1024)

    @Test
    fun configuredMessageLimit() = messageLimitTest(limit = 1024, configureLimit = true)

    @Test
    fun oneByteMessageLimit() = messageLimitTest(limit = 1, configureLimit = true)

    // Netty rejects zero when a connection opens and leaves the failed transport unterminated.
    @Ignore
    @Test
    fun zeroMessageLimit() = messageLimitTest(limit = 0, configureLimit = true)

    @Test
    fun oversizedMessageFailsWithoutBreakingTheConnection() = grpcServerTest {
        val responseGate = registerNewGate("in-flight response")
        start(serverConfig = { maxInboundMessageSize = 1024 }) {
            unaryCall = { request ->
                if (request.responseSize == 9) {
                    responseGate.pass()
                }
                SimpleResponse { payload = zeros(request.responseSize) }
            }
        }

        coroutineScope {
            val heldResponse = async { testService.unaryCall(SimpleRequest { responseSize = 9 }) }
            responseGate.awaitReached(AWAIT_TIMEOUT)

            assertGrpcStatus(GrpcStatusCode.RESOURCE_EXHAUSTED) {
                testService.unaryCall(SimpleRequest { payload = zeros(2048) })
            }

            assertEquals(zeros(5), testService.unaryCall(SimpleRequest { responseSize = 5 }).payload)
            responseGate.open()
            assertEquals(zeros(9), withTimeout(AWAIT_TIMEOUT) { heldResponse.await() }.payload)
        }
    }

    private fun messageLimitTest(limit: Int, configureLimit: Boolean = false) = grpcServerTest {
        coroutineScope {
            val method = rawMethod("configuration.Service/Echo", GrpcMethodType.UNARY)
            val registry = GrpcMutableHandlerRegistry()
            registry.addService(serverServiceDefinition(
                serviceDescriptor("configuration.Service", listOf(method), null),
                listOf(unaryServerMethodDefinition(method, typeOf<ByteArray>(), emptyList()) { it }),
            ))
            start(
                serverConfig = {
                    fallbackHandlerRegistry = registry
                    if (configureLimit) {
                        maxInboundMessageSize = limit
                    }
                },
                clientConfig = { maxInboundMessageSize = Int.MAX_VALUE },
            )

            // Raw bytes exclude protobuf overhead and the five-byte gRPC frame header.
            for (size in listOf(0, (limit - 1).coerceAtLeast(0), limit).distinct()) {
                val request = ByteArray(size) { it.toByte() }
                assertContentEquals(request, client.unaryRpc(method, request), "message size: $size")
            }
            assertGrpcStatus(GrpcStatusCode.RESOURCE_EXHAUSTED) {
                client.unaryRpc(method, ByteArray(limit + 1))
            }
            assertContentEquals(ByteArray(0), client.unaryRpc(method, ByteArray(0)))
        }
    }
}
