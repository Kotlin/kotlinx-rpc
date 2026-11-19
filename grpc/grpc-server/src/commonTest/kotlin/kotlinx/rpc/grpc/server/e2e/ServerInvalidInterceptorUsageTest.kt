/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.zeros
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerInvalidInterceptorUsageTest {
    @Test
    fun missingUnaryResponseFailsTheCall() = invalidResponseCountTest(responseCount = 0)

    @Test
    fun extraUnaryResponseFailsTheCall() = invalidResponseCountTest(responseCount = 2)

    private fun invalidResponseCountTest(responseCount: Int) = grpcServerTest {
        val responseFlowStopped = CompletableDeferred<Unit>()
        val interceptor = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = flow {
                try {
                    request.single()
                    repeat(responseCount) { index ->
                        emit(SimpleResponse { payload = zeros(index + 1) } as Response)
                    }
                } finally {
                    responseFlowStopped.complete(Unit)
                }
            }
        }
        start(serverConfig = { intercept(interceptor) }) {
            unaryCall = {
                marks.add("service")
                SimpleResponse {}
            }
        }

        withTimeout(AWAIT_TIMEOUT) {
            assertFailsWith<GrpcStatusException> { testService.unaryCall(SimpleRequest {}) }
            responseFlowStopped.await()
        }

        assertEquals(emptyList(), marks.events)
    }
}
