/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.cause
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.server.testing.assertNoMessages
import kotlinx.rpc.grpc.server.testing.assertNotClosed
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class AdapterExceptionBoundaryTest {
    @Test
    fun interceptorInvocationFailureClosesWithUnknownAndNotifiesOnClose() = adapterTest { fixture ->
        val closes = mutableListOf<GrpcStatus>()
        var serviceInvoked = false
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                onClose { status, _ -> closes += status }
                error("interceptor invocation failed")
            }
        }
        val session = fixture.unary(listOf(interceptor)) {
            serviceInvoked = true
            it
        }

        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals("interceptor invocation failed", assertIs<IllegalStateException>(close.status.cause).message)
        assertEquals(GrpcStatusCode.UNKNOWN, closes.single().statusCode)
        assertFalse(serviceInvoked)
        session.call.assertNoMessages()
    }

    @Test
    fun interceptorResponseFlowFailurePreservesStatusAndTrailers() = adapterTest { fixture ->
        var serviceInvoked = false
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                responseTrailers.append("x-scope", "scope value")
                return flow {
                    throw GrpcStatusCode.PERMISSION_DENIED.asException(
                        "interceptor flow rejected",
                        GrpcMetadata { append("x-failure", "failure value") },
                    )
                }
            }
        }
        val session = fixture.unary(listOf(interceptor)) {
            serviceInvoked = true
            it
        }

        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.PERMISSION_DENIED)
        assertEquals("interceptor flow rejected", close.status.description)
        assertEquals("scope value", close.trailers["x-scope"])
        assertEquals("failure value", close.trailers["x-failure"])
        assertFalse(serviceInvoked)
        session.call.assertNoMessages()
    }

    @Test
    fun requestTransformationFailureStopsDeliveryAndPreservesEarlierResponses() = adapterTest { fixture ->
        val received = mutableListOf<String>()
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = proceed(request.map {
                if (it == "rejected") {
                    throw GrpcStatusCode.INVALID_ARGUMENT.asException("request transformation failed")
                }
                it
            })
        }
        val session = fixture.bidiStreaming(listOf(interceptor)) { requests ->
            requests.map {
                received += it
                "$it-response"
            }
        }

        fixture.runAdapterTasks()
        session.deliverRequest("accepted")
        fixture.runAdapterTasks()
        session.deliverRequest("rejected")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.INVALID_ARGUMENT)
        assertEquals("request transformation failed", close.status.description)
        assertEquals(listOf("accepted"), received)
        session.call.assertMessages("accepted-response")
    }

    @Test
    fun responseTransformationFailureStopsSendingAndClosesWithUnknown() = adapterTest { fixture ->
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = proceed(request).map {
                check(it != "rejected") { "response transformation failed" }
                it
            }
        }
        val session = fixture.serverStreaming(listOf(interceptor)) { flowOf("accepted", "rejected", "later") }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals("response transformation failed", assertIs<IllegalStateException>(close.status.cause).message)
        session.call.assertMessages("accepted")
    }

    @Test
    fun requestFailureClosesWithUnknownWithoutInvokingTheService() = adapterTest { fixture ->
        var serviceInvoked = false
        val session = fixture.unary {
            serviceInvoked = true
            it
        }
        session.call.beforeRequest = { error("request failed") }

        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals("request failed", assertIs<IllegalStateException>(close.status.cause).message)
        assertFalse(serviceInvoked)
        session.call.assertNoMessages()
    }

    @Test
    fun headersFailureBeforeTheFirstResponseClosesWithUnknown() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf("first", "later") }
        session.call.beforeSendHeaders = { error("headers failed") }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals("headers failed", assertIs<IllegalStateException>(close.status.cause).message)
        session.call.assertNoMessages()
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-717
    fun headersFailureForAnEmptyResponseStreamClosesWithUnknown() = adapterTest { fixture ->
        val closes = mutableListOf<GrpcStatus>()
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                onClose { status, _ -> closes += status }
                return proceed(request)
            }
        }
        val session = fixture.serverStreaming(listOf(interceptor)) { emptyFlow() }
        session.call.beforeSendHeaders = { error("empty stream headers failed") }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals("empty stream headers failed", assertIs<IllegalStateException>(close.status.cause).message)
        assertEquals(GrpcStatusCode.UNKNOWN, closes.single().statusCode)
        session.call.assertNoMessages()
    }

    @Test
    fun messageSendFailureStopsSendingAndClosesWithUnknown() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf("first", "later") }
        session.call.beforeSendMessage = { error("message send failed") }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals("message send failed", assertIs<IllegalStateException>(close.status.cause).message)
        session.call.assertNoMessages()
    }

    @Test
    fun closeFailureIsReportedWithoutRetryingOrLeakingTheCallJob() = adapterTest { fixture ->
        val session = fixture.unary { "$it-response" }
        var closeAttempts = 0
        session.call.beforeClose = { _, _ ->
            closeAttempts++
            error("close failed")
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()
        fixture.runAdapterTasks()

        assertEquals(1, closeAttempts)
        fixture.assertUncaught { errors ->
            assertEquals("close failed", assertIs<IllegalStateException>(errors.single()).message)
        }
        session.call.assertMessages("request-response")
        session.call.assertNotClosed()
    }
}
