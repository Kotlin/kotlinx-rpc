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
import kotlinx.rpc.grpc.server.testing.clientInterceptor
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class ServerDeadlineTest {
    @Test
    fun deadlineCancelsTheServiceAndReturnsDeadlineExceeded() = grpcServerTest {
        val responseGate = registerNewGate("unary response")
        val cancelled = CompletableDeferred<Unit>()
        start(clientConfig = {
            intercept(clientInterceptor {
                if (method.fullMethodName == TestServiceMethods.UNARY_CALL) {
                    callOptions.timeout = 5.seconds
                }
            })
        }) {
            unaryCall = {
                try {
                    responseGate.pass()
                    SimpleResponse {}
                } catch (error: CancellationException) {
                    cancelled.complete(Unit)
                    throw error
                }
            }
        }

        testService.emptyCall(Empty {})

        coroutineScope {
            val response = async {
                assertGrpcStatus(GrpcStatusCode.DEADLINE_EXCEEDED) { testService.unaryCall(SimpleRequest {}) }
            }
            responseGate.awaitReached(AWAIT_TIMEOUT)
            withTimeout(AWAIT_TIMEOUT) {
                response.await()
                cancelled.await()
            }
        }
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.CANCELLED, close.status.statusCode)
    }
}
