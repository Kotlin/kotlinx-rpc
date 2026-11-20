/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.asException
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcClientInterceptor
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ServerCallObserverTest {
    @Test
    fun unaryCallClosesWithOk() = grpcServerTest {
        start()

        testService.unaryCall(SimpleRequest {})

        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    fun observedRequestHeadersIncludeTheClientHeader() = grpcServerTest {
        start(clientConfig = {
            intercept(object : GrpcClientInterceptor {
                override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                    request: Flow<Request>,
                ): Flow<Response> {
                    requestHeaders.append("x-harness-header", "observed")
                    return proceed(request)
                }
            })
        })

        testService.unaryCall(SimpleRequest {})

        val call = observer.awaitCall(TestServiceMethods.UNARY_CALL)
        assertEquals("observed", call.requestHeaders["x-harness-header"])
        assertEquals(GrpcStatusCode.OK, call.awaitClose().status.statusCode)
    }

    @Test
    fun awaitCallWithIndexOneReturnsTheSecondCall() = grpcServerTest {
        start()

        testService.unaryCall(SimpleRequest {})
        testService.unaryCall(SimpleRequest {})

        val first = observer.awaitCall(TestServiceMethods.UNARY_CALL)
        val second = observer.awaitCall(TestServiceMethods.UNARY_CALL, index = 1)
        assertNotSame(first, second)
        assertEquals(2, observer.calls.size)
        assertSame(observer.calls[1], second)
        assertEquals(GrpcStatusCode.OK, second.awaitClose().status.statusCode)
    }

    @Test
    fun serviceExceptionPreservesItsStatusInTheObservedClose() = grpcServerTest {
        start {
            unaryCall = {
                throw GrpcStatus(GrpcStatusCode.PERMISSION_DENIED, "service rejected the call").asException()
            }
        }

        assertGrpcStatus(GrpcStatusCode.PERMISSION_DENIED) { testService.unaryCall(SimpleRequest {}) }

        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.PERMISSION_DENIED, close.status.statusCode)
        assertEquals("service rejected the call", close.status.description)
    }
}
