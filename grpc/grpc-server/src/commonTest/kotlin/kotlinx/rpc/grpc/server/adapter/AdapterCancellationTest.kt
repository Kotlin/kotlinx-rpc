/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
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
import kotlin.test.assertTrue

class AdapterCancellationTest {
    @Test
    fun cancelWhileServiceSuspendsCancelsTheServiceAndSkipsClose() = adapterTest { fixture ->
        var serviceStarted = false
        var serviceCancelled = false
        val session = fixture.unary {
            serviceStarted = true
            try {
                awaitCancellation()
            } catch (error: CancellationException) {
                serviceCancelled = true
                throw error
            }
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()
        assertTrue(serviceStarted)
        assertFalse(fixture.isIdle())

        session.call.cancelled = true
        session.listener.onCancel()
        fixture.runAdapterTasks()

        assertTrue(serviceCancelled)
        session.call.assertNoMessages()
        session.call.assertNotClosed()
    }

    @Test
    fun cancelWhileWaitingForReadinessSkipsMessageAndClose() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf("$it-response") }
        session.call.ready = false

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()
        session.call.assertNoMessages()
        session.call.assertNotClosed()
        assertFalse(fixture.isIdle())

        session.call.cancelled = true
        session.listener.onCancel()
        fixture.runAdapterTasks()

        session.call.assertNoMessages()
        session.call.assertNotClosed()
    }

    @Test
    fun cancelInsideSendMessageStopsFurtherResponsesAndSkipsClose() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf("first", "second") }
        session.call.onSendMessage = {
            session.call.cancelled = true
            session.listener.onCancel()
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        session.call.assertNotClosed()
        session.call.assertMessages("first")
    }

    @Test
    fun messageAfterCancellationDoesNotThrowOrSendAResponse() = adapterTest { fixture ->
        val session = fixture.bidiStreaming { it }
        fixture.runAdapterTasks()

        session.call.cancelled = true
        session.listener.onCancel()
        fixture.runAdapterTasks()

        session.listener.onMessage("late request")
        fixture.runAdapterTasks()

        session.call.assertNoMessages()
        session.call.assertNotClosed()
    }

    @Test
    fun messageAfterCompletedCallDoesNotSendAnotherResponseOrClose() = adapterTest { fixture ->
        val session = fixture.bidiStreaming { it }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()
        session.call.assertClosedWith(GrpcStatusCode.OK)

        try {
            session.listener.onMessage("late request")
        } catch (_: Exception) {
            // The adapter can reject an invalid callback synchronously.
        }
        fixture.runAdapterTasks()

        session.call.assertClosedWith(GrpcStatusCode.OK)
        session.call.assertMessages("request")
    }

    @Test
    fun cancellationNotifiesOnCloseCallbacksOnceWithCancelled() = adapterTest { fixture ->
        val closes = mutableListOf<Pair<GrpcStatus, GrpcMetadata>>()
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                onClose { status, trailers -> closes += status to trailers }
                return proceed(request)
            }
        }
        val session = fixture.bidiStreaming(listOf(interceptor)) { it }
        fixture.runAdapterTasks()

        session.call.cancelled = true
        session.listener.onCancel()
        session.listener.onCancel()
        fixture.runAdapterTasks()
        session.listener.onComplete()
        fixture.runAdapterTasks()

        assertEquals(GrpcStatusCode.CANCELLED, closes.single().first.statusCode)
        session.call.assertNoMessages()
        session.call.assertNotClosed()
    }
}
