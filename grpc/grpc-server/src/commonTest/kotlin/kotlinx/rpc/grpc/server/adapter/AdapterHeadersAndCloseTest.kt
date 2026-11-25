/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.server.testing.ServerCallEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class AdapterHeadersAndCloseTest {
    @Test
    fun emptyResponseStreamSendsHeadersAndOkClose() = adapterTest { fixture ->
        val session = fixture.serverStreaming { emptyFlow() }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("sendHeaders", "close(OK)"), session.call.outbound())
    }

    @Test
    fun failureBeforeFirstResponseSendsOnlyClose() = adapterTest { fixture ->
        val session = fixture.unary { throw GrpcStatusCode.INVALID_ARGUMENT.asException("invalid request") }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("close(INVALID_ARGUMENT)"), session.call.outbound())
    }

    @Test
    fun failureAfterOneResponseSendsHeadersMessageAndErrorClose() = adapterTest { fixture ->
        val session = fixture.serverStreaming {
            flow {
                emit("response")
                throw GrpcStatusCode.ABORTED.asException("stream aborted")
            }
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("sendHeaders", "sendMessage", "close(ABORTED)"), session.call.outbound())
    }

    @Test
    fun unarySuccessSendsCustomHeadersMessageAndOkClose() = adapterTest { fixture ->
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                responseHeaders.append("x-response", "header value")
                return proceed(request)
            }
        }
        val session = fixture.unary(listOf(interceptor)) { "$it-response" }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val headers = session.call.events.filterIsInstance<ServerCallEvent.HeadersSent>().single().headers
        assertEquals("header value", headers["x-response"])
        session.call.assertMessages("request-response")
        assertEquals(listOf("sendHeaders", "sendMessage", "close(OK)"), session.call.outbound())
    }

    @Test
    fun alreadyCancelledCallSkipsClose() = adapterTest { fixture ->
        val session = fixture.unary { throw GrpcStatusCode.RESOURCE_EXHAUSTED.asException() }
        session.call.cancelled = true

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(emptyList(), session.call.outbound())
    }
}
