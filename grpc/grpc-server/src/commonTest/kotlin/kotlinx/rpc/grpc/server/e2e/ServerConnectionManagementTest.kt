/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import com.google.protobuf.kotlin.Empty
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.zeros
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class ServerConnectionManagementTest {
    @Test
    fun connectionAgeCancelsAHeldCallAndAllowsANewCall() = grpcServerTest {
        val responseGate = registerNewGate("aged connection response")
        val cancelled = CompletableDeferred<Unit>()
        start(serverConfig = {
            maxConnectionAge = 5.seconds
            maxConnectionAgeGrace = 5.seconds
        }) {
            unaryCall = { request ->
                if (request.responseSize == 1) {
                    try {
                        responseGate.pass()
                    } catch (error: CancellationException) {
                        cancelled.complete(Unit)
                        throw error
                    }
                }
                SimpleResponse { payload = zeros(request.responseSize) }
            }
        }

        testService.emptyCall(Empty {})

        coroutineScope {
            val response = async {
                assertGrpcStatus(GrpcStatusCode.UNAVAILABLE) {
                    testService.unaryCall(SimpleRequest { responseSize = 1 })
                }
            }
            responseGate.awaitReached(AWAIT_TIMEOUT)
            withTimeout(AWAIT_TIMEOUT + 5.seconds) {
                response.await()
                cancelled.await()
            }
        }
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.CANCELLED, close.status.statusCode)
        assertEquals(zeros(5), testService.unaryCall(SimpleRequest { responseSize = 5 }).payload)
        val secondClose = observer.awaitCall(TestServiceMethods.UNARY_CALL, index = 1).awaitClose()
        assertEquals(GrpcStatusCode.OK, secondClose.status.statusCode)
    }
}
