/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.zeros
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ServerShutdownTest {
    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-719
    fun shutdownLetsAnExistingCallCompleteWithOk() = grpcServerTest {
        val responseGate = registerNewGate("unary response")
        start {
            unaryCall = {
                responseGate.pass()
                SimpleResponse { payload = zeros(5) }
            }
        }

        coroutineScope {
            val response = async { testService.unaryCall(SimpleRequest {}) }
            responseGate.awaitReached(AWAIT_TIMEOUT)

            markServerStoppedByTest()
            server.shutdown()
            assertTrue(server.isShutdown)
            assertFalse(server.isTerminated)
            responseGate.open()

            assertEquals(zeros(5), withTimeout(AWAIT_TIMEOUT) { response.await() }.payload)
            val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
            assertEquals(GrpcStatusCode.OK, close.status.statusCode)
            server.awaitTermination(AWAIT_TIMEOUT)
            assertTrue(server.isTerminated)
        }
    }

    @Test
    fun shutdownNowCancelsAnExistingCall() = grpcServerTest {
        val responseGate = registerNewGate("unary response")
        val cancelled = CompletableDeferred<Unit>()
        start {
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

        coroutineScope {
            val response = async {
                assertFailsWith<GrpcStatusException> { testService.unaryCall(SimpleRequest {}) }
            }
            responseGate.awaitReached(AWAIT_TIMEOUT)

            markServerStoppedByTest()
            server.shutdownNow()

            withTimeout(AWAIT_TIMEOUT) {
                cancelled.await()
                response.await()
            }
            val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
            assertEquals(GrpcStatusCode.CANCELLED, close.status.statusCode)
            server.awaitTermination(AWAIT_TIMEOUT)
            assertTrue(server.isTerminated)
        }
    }

    @Test
    fun shutdownRejectsNewCallsWithUnavailable() = grpcServerTest {
        start()
        testService.unaryCall(SimpleRequest {})

        markServerStoppedByTest()
        server.shutdown()
        server.awaitTermination(AWAIT_TIMEOUT)

        assertGrpcStatus(GrpcStatusCode.UNAVAILABLE) { testService.unaryCall(SimpleRequest {}) }
        assertTrue(server.isTerminated)
        assertEquals(1, observer.calls.size)
    }
}
