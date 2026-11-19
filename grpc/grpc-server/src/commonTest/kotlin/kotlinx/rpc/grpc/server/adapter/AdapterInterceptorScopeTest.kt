/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.server.testing.assertNoMessages
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.trailers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class AdapterInterceptorScopeTest {
    @Test
    fun middleInterceptorResponseSkipsTheRestOfTheChainAndTheService() = adapterTest { fixture ->
        val marks = mutableListOf<String>()
        val middle = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                marks += "middle"
                return flowOf("interceptor response" as Response)
            }
        }
        val interceptors = listOf(recordingInterceptor("first", marks), middle, recordingInterceptor("last", marks))
        val session = fixture.unary(interceptors) {
            marks += "service"
            it
        }

        fixture.runAdapterTasks()

        assertEquals(listOf("first", "middle"), marks)
        session.call.assertMessages("interceptor response")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun middleInterceptorCloseSkipsTheRestOfTheChainAndTheService() = adapterTest { fixture ->
        val marks = mutableListOf<String>()
        val middle = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                marks += "middle"
                close(
                    GrpcStatus(GrpcStatusCode.PERMISSION_DENIED, "access denied"),
                    GrpcMetadata { append("x-rejection", "middle") },
                )
            }
        }
        val interceptors = listOf(recordingInterceptor("first", marks), middle, recordingInterceptor("last", marks))
        val session = fixture.unary(interceptors) {
            marks += "service"
            it
        }

        fixture.runAdapterTasks()

        assertEquals(listOf("first", "middle"), marks)
        val close = session.call.assertClosedWith(GrpcStatusCode.PERMISSION_DENIED)
        assertEquals("access denied", close.status.description)
        assertEquals("middle", close.trailers["x-rejection"])
        session.call.assertNoMessages()
    }

    @Test
    fun outerInterceptorCanRecoverFromInnerFailureBeforeTheResponseFlowReturns() = adapterTest { fixture ->
        val failures = mutableListOf<GrpcStatusException>()
        var serviceInvoked = false
        val outer = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                return try {
                    proceed(request)
                } catch (error: GrpcStatusException) {
                    failures += error
                    flowOf("recovered" as Response)
                }
            }
        }
        val inner = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                throw GrpcStatusCode.ABORTED.asException(
                    "inner rejected",
                    GrpcMetadata { append("x-inner", "rejection") },
                )
            }
        }
        val session = fixture.unary(listOf(outer, inner)) {
            serviceInvoked = true
            it
        }

        fixture.runAdapterTasks()

        val failure = failures.single()
        assertEquals(GrpcStatusCode.ABORTED, failure.status.statusCode)
        assertEquals("inner rejected", failure.status.description)
        assertEquals("rejection", failure.trailers?.get("x-inner"))
        assertFalse(serviceInvoked)
        session.call.assertMessages("recovered")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun outerInterceptorCanRecoverFromInnerResponseFlowFailure() = adapterTest { fixture ->
        val failures = mutableListOf<Throwable>()
        var serviceInvoked = false
        val outer = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = proceed(request).catch { error ->
                failures += error
                emit("recovered" as Response)
            }
        }
        val inner = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = flow {
                throw GrpcStatusCode.ABORTED.asException(
                    "inner flow rejected",
                    GrpcMetadata { append("x-inner", "flow rejection") },
                )
            }
        }
        val session = fixture.unary(listOf(outer, inner)) {
            serviceInvoked = true
            it
        }

        fixture.runAdapterTasks()

        val failure = assertIs<GrpcStatusException>(failures.single())
        assertEquals(GrpcStatusCode.ABORTED, failure.status.statusCode)
        assertEquals("inner flow rejected", failure.status.description)
        assertEquals("flow rejection", failure.trailers?.get("x-inner"))
        assertFalse(serviceInvoked)
        session.call.assertMessages("recovered")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun onCloseCallbacksAcrossInterceptorsRunOnceWithTheFinalStatusAndTrailers() = adapterTest { fixture ->
        val names = listOf("first", "second", "third")
        val closes = names.associateWith { mutableListOf<Pair<GrpcStatus, GrpcMetadata>>() }
        val interceptors = names.map { name ->
            object : GrpcServerInterceptor {
                override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                    request: Flow<Request>,
                ): Flow<Response> {
                    responseTrailers.append("x-$name", "$name value")
                    onClose { status, trailers -> closes.getValue(name) += status to trailers }
                    return proceed(request)
                }
            }
        }
        val session = fixture.unary(interceptors) {
            throw GrpcStatusCode.FAILED_PRECONDITION.asException(
                "precondition failed",
                GrpcMetadata { append("x-exception", "exception value") },
            )
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()
        session.listener.onComplete()
        session.listener.onComplete()
        fixture.runAdapterTasks()

        for (name in names) {
            val (status, trailers) = closes.getValue(name).single()
            assertEquals(GrpcStatusCode.FAILED_PRECONDITION, status.statusCode)
            assertEquals("precondition failed", status.description)
            for (contributor in names) {
                assertEquals("$contributor value", trailers["x-$contributor"])
            }
            assertEquals("exception value", trailers["x-exception"])
        }
        session.call.assertClosedWith(GrpcStatusCode.FAILED_PRECONDITION)
    }

    private fun recordingInterceptor(name: String, marks: MutableList<String>): GrpcServerInterceptor {
        return object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                marks += name
                return proceed(request)
            }
        }
    }
}
