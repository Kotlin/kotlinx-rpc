/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.StatusRuntimeException
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.cause
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertNoMessages
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class AdapterStatusMappingTest {
    @Test
    fun serviceCancellationClosesWithCancelled() = adapterTest { fixture ->
        val session = fixture.unary { throw CancellationException("service cancelled") }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        session.call.assertClosedWith(GrpcStatusCode.CANCELLED)
        session.call.assertNoMessages()
    }

    @Test
    fun statusExceptionPreservesStatusAndMergesTrailers() = adapterTest { fixture ->
        val status = GrpcStatus(GrpcStatusCode.PERMISSION_DENIED, "permission denied")
        val trailers = GrpcMetadata {
            append("x-exception", "exception value")
            append("x-shared", "exception")
        }
        val session = fixture.unary(listOf(TrailerInterceptor)) { throw GrpcStatusException(status, trailers) }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.PERMISSION_DENIED)
        assertEquals("permission denied", close.status.description)
        assertEquals("scope value", close.trailers["x-scope"])
        assertEquals("exception value", close.trailers["x-exception"])
        assertEquals(listOf("scope", "exception"), close.trailers.getAll("x-shared"))
        session.call.assertNoMessages()
    }

    @Test
    fun runtimeStatusExceptionPreservesStatusAndMergesTrailers() = adapterTest { fixture ->
        val trailers = GrpcMetadata {
            append("x-exception", "runtime value")
            append("x-shared", "runtime")
        }
        val session = fixture.unary(listOf(TrailerInterceptor)) {
            throw StatusRuntimeException(GrpcStatusCode.DATA_LOSS, "data lost", trailers)
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.DATA_LOSS)
        assertEquals("data lost", close.status.description)
        assertEquals("scope value", close.trailers["x-scope"])
        assertEquals("runtime value", close.trailers["x-exception"])
        assertEquals(listOf("scope", "runtime"), close.trailers.getAll("x-shared"))
        session.call.assertNoMessages()
    }

    @Test
    fun serviceExceptionClosesWithUnknown() = adapterTest { fixture ->
        val failure = IllegalStateException("service failed")
        val session = fixture.unary { throw failure }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals(failure.message, assertIs<IllegalStateException>(close.status.cause).message)
        session.call.assertNoMessages()
    }

    @Test
    fun serviceErrorClosesWithUnknownAndPropagatesToTheExceptionHandler() = adapterTest { fixture ->
        val failure = ServiceError()
        val session = fixture.unary { throw failure }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertEquals(failure.message, assertIs<ServiceError>(close.status.cause).message)
        session.call.assertNoMessages()
        fixture.assertUncaught { errors ->
            assertEquals(listOf<Throwable>(failure), errors)
        }
    }

    @Test
    fun interceptorClosePreservesStatusAndTrailers() = adapterTest { fixture ->
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                close(
                    GrpcStatus(GrpcStatusCode.UNAUTHENTICATED, "credentials missing"),
                    GrpcMetadata { append("x-close", "close value") },
                )
            }
        }
        var serviceInvoked = false
        val session = fixture.unary(listOf(interceptor)) {
            serviceInvoked = true
            it
        }

        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.UNAUTHENTICATED)
        assertEquals("credentials missing", close.status.description)
        assertEquals("close value", close.trailers["x-close"])
        assertFalse(serviceInvoked)
        session.call.assertNoMessages()
    }

    private object TrailerInterceptor : GrpcServerInterceptor {
        override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
            request: Flow<Request>,
        ): Flow<Response> {
            responseTrailers.append("x-scope", "scope value")
            responseTrailers.append("x-shared", "scope")
            return proceed(request)
        }
    }

    private class ServiceError : Error("service error")
}
