/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.serverInterceptor
import kotlinx.rpc.grpc.server.testing.zeros
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.test.EventLog
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerInterceptorTest {
    @Test
    fun interceptorsRunInRegistrationOrderBeforeTheService() = grpcServerTest {
        start(serverConfig = {
            intercept(
                serverInterceptor { marks.add("first") },
                serverInterceptor { marks.add("second") },
                serverInterceptor { marks.add("third") },
            )
        }) {
            unaryCall = {
                marks.add("service")
                SimpleResponse {}
            }
        }

        testService.unaryCall(SimpleRequest {})

        assertEquals(listOf("first", "second", "third", "service"), marks.events)
    }

    @Test
    fun proceedPassesTheChangedRequestToTheService() = grpcServerTest {
        val interceptor = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = proceed(request.map { SimpleRequest { responseSize = 23 } as Request })
        }
        start(serverConfig = { intercept(interceptor) })

        val response = testService.unaryCall(SimpleRequest { responseSize = 1 })

        assertEquals(zeros(23), response.payload)
    }

    @Test
    fun closeBeforeProceedSkipsTheService() = grpcServerTest {
        start(serverConfig = {
            intercept(serverInterceptor { close(GrpcStatus(GrpcStatusCode.PERMISSION_DENIED, "Call rejected")) })
        }) {
            unaryCall = {
                marks.add("service")
                SimpleResponse {}
            }
        }

        assertGrpcStatus(GrpcStatusCode.PERMISSION_DENIED) { testService.unaryCall(SimpleRequest {}) }

        assertEquals(emptyList(), marks.events)
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.PERMISSION_DENIED, close.status.statusCode)
    }

    @Test
    fun interceptorExceptionClosesWithUnknown() = grpcServerTest {
        start(serverConfig = { intercept(serverInterceptor { error("Interceptor failed") }) })

        assertGrpcStatus(GrpcStatusCode.UNKNOWN) { testService.unaryCall(SimpleRequest {}) }

        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.UNKNOWN, close.status.statusCode)
    }

    @Test
    fun onCloseRunsOnceWithTheFinalStatusAndTrailers() = grpcServerTest {
        val closes = EventLog<Pair<GrpcStatusCode, String?>>()
        start(serverConfig = {
            intercept(serverInterceptor {
                responseTrailers.append("x-final", "trailer")
                onClose { status, trailers -> closes.add(status.statusCode to trailers["x-final"]) }
            })
        }) {
            unaryCall = { throw GrpcStatusCode.ABORTED.asException("Call aborted") }
        }

        val error = assertGrpcStatus(GrpcStatusCode.ABORTED) { testService.unaryCall(SimpleRequest {}) }
        closes.await(AWAIT_TIMEOUT) { true }

        assertEquals("Call aborted", error.status.description)
        assertEquals(listOf(GrpcStatusCode.ABORTED to "trailer"), closes.events)
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.ABORTED, close.status.statusCode)
        assertEquals("trailer", close.trailers["x-final"])
    }
}
