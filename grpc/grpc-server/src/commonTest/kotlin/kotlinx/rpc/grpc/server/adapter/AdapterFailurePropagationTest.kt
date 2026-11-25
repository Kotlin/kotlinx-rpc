/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.cause
import kotlinx.rpc.grpc.copy
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.server.testing.assertNoMessages
import kotlinx.rpc.grpc.server.testing.assertNotClosed
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class AdapterFailurePropagationTest {
    @Test
    fun serviceErrorDoesNotCancelOtherCalls() = fatalErrorTest(FailureSite.SERVICE)

    @Test
    fun interceptorInvocationErrorDoesNotCancelOtherCalls() = fatalErrorTest(FailureSite.INTERCEPTOR_INVOCATION)

    @Test
    fun interceptorFlowErrorDoesNotCancelOtherCalls() = fatalErrorTest(FailureSite.INTERCEPTOR_FLOW)

    @Test
    fun cancellationBeforeServiceFailureNotifiesOnceAndStopsResponses() = cancellationAndFailureTest(cancelFirst = true)

    @Test
    fun serviceFailureBeforeCancellationPreservesItsStatusAndNotifiesOnce() =
        cancellationAndFailureTest(cancelFirst = false)

    private fun fatalErrorTest(site: FailureSite) = adapterTest { fixture ->
        val failure = TestError()
        val releaseSurvivor = CompletableDeferred<Unit>()
        var survivorStarted = false
        val survivor = fixture.unary {
            survivorStarted = true
            releaseSurvivor.await()
            "$it-response"
        }
        fixture.runAdapterTasks()
        survivor.deliverRequest("survivor")
        survivor.listener.onHalfClose()
        fixture.runAdapterTasks()
        assertTrue(survivorStarted)
        survivor.call.assertNotClosed()

        val closes = mutableListOf<Pair<GrpcStatus, GrpcMetadata>>()
        var serviceInvoked = false
        val failingInterceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = when (site) {
                FailureSite.SERVICE -> proceed(request)
                FailureSite.INTERCEPTOR_INVOCATION -> throw failure
                FailureSite.INTERCEPTOR_FLOW -> flow { throw failure }
            }
        }
        val failing = fixture.unary(listOf(observeCloses(closes), failingInterceptor)) {
            serviceInvoked = true
            throw failure
        }
        fixture.runAdapterTasks()
        if (site == FailureSite.SERVICE) {
            failing.deliverRequest("failed request")
            failing.listener.onHalfClose()
            fixture.runAdapterTasks()
        }

        assertEquals(site == FailureSite.SERVICE, serviceInvoked)
        val close = failing.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        assertSame(failure, close.status.cause)
        assertSame(failure, closes.single().first.cause)
        assertEquals(GrpcStatusCode.UNKNOWN, closes.single().first.statusCode)
        failing.call.assertNoMessages()
        fixture.assertUncaught { assertEquals(listOf<Throwable>(failure), it) }
        survivor.call.assertNoMessages()
        survivor.call.assertNotClosed()
        assertFalse(fixture.isIdle())

        releaseSurvivor.complete(Unit)
        fixture.runAdapterTasks()
        survivor.call.assertMessages("survivor-response")
        survivor.call.assertClosedWith(GrpcStatusCode.OK)

        val next = fixture.unary { it }
        fixture.runAdapterTasks()
        next.deliverRequest("next call")
        next.listener.onHalfClose()
        fixture.runAdapterTasks()
        next.call.assertMessages("next call")
        next.call.assertClosedWith(GrpcStatusCode.OK)
    }

    private fun cancellationAndFailureTest(cancelFirst: Boolean) = adapterTest { fixture ->
        val fail = CompletableDeferred<Unit>()
        val closes = mutableListOf<Pair<GrpcStatus, GrpcMetadata>>()
        var cleanupCount = 0
        val session = fixture.serverStreaming(listOf(observeCloses(closes))) {
            flow {
                try {
                    emit("first")
                    fail.await()
                    throw GrpcStatusCode.ABORTED.asException(
                        "Service failed",
                        GrpcMetadata { append("x-error", "failure trailer") },
                    )
                } finally {
                    cleanupCount++
                }
            }
        }
        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()
        session.call.assertMessages("first")
        session.call.assertNotClosed()

        if (cancelFirst) {
            session.call.cancelled = true
            session.listener.onCancel()
            fail.complete(Unit)
            fixture.runAdapterTasks()
            session.call.assertNotClosed()
            assertEquals(GrpcStatusCode.CANCELLED, closes.single().first.statusCode)
        } else {
            fail.complete(Unit)
            fixture.runAdapterTasks()
            session.call.assertClosedWith(GrpcStatusCode.ABORTED)
            session.call.cancelled = true
            session.listener.onCancel()
            fixture.runAdapterTasks()
            assertEquals(GrpcStatusCode.ABORTED, closes.single().first.statusCode)
            assertEquals("failure trailer", closes.single().second["x-error"])
        }

        session.listener.onComplete()
        fixture.runAdapterTasks()
        assertEquals(1, closes.size)
        assertEquals(1, cleanupCount)
        assertEquals("scope trailer", closes.single().second["x-scope"])
        session.call.assertMessages("first")
        if (cancelFirst) {
            session.call.assertNotClosed()
        } else {
            session.call.assertClosedWith(GrpcStatusCode.ABORTED)
        }
    }

    private fun observeCloses(closes: MutableList<Pair<GrpcStatus, GrpcMetadata>>): GrpcServerInterceptor =
        object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                responseTrailers.append("x-scope", "scope trailer")
                onClose { status, trailers -> closes += status to trailers.copy() }
                return proceed(request)
            }
        }

    private enum class FailureSite { SERVICE, INTERCEPTOR_INVOCATION, INTERCEPTOR_FLOW }

    private class TestError : Error("Fatal test failure")
}
