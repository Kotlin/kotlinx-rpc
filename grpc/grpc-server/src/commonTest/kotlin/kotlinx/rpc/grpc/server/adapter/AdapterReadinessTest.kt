/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.server.testing.assertNoMessages
import kotlinx.rpc.grpc.server.testing.assertNotClosed
import kotlin.test.Test
import kotlin.test.assertFalse

class AdapterReadinessTest {
    @Test
    fun onReadySendsTheResponseOnlyWhenTheCallIsReady() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf("response") }
        session.call.ready = false

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()
        session.call.assertNoMessages()
        session.call.assertNotClosed()
        assertFalse(fixture.isIdle())

        session.listener.onReady()
        fixture.runAdapterTasks()
        session.call.assertNoMessages()
        session.call.assertNotClosed()
        assertFalse(fixture.isIdle())

        session.call.ready = true
        session.listener.onReady()
        fixture.runAdapterTasks()

        session.call.assertClosedWith(GrpcStatusCode.OK)
        session.call.assertMessages("response")
    }

    @Test
    fun repeatedReadyNotificationsSendEachMessageOnce() = adapterTest { fixture ->
        val secondResponse = CompletableDeferred<Unit>()
        val session = fixture.serverStreaming {
            flow {
                emit("first")
                secondResponse.await()
                emit("second")
            }
        }
        session.call.ready = false

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        session.call.ready = true
        repeat(3) { session.listener.onReady() }
        fixture.runAdapterTasks()
        session.call.assertMessages("first")
        session.call.assertNotClosed()

        repeat(3) { session.listener.onReady() }
        fixture.runAdapterTasks()
        session.call.assertMessages("first")
        session.call.assertNotClosed()

        secondResponse.complete(Unit)
        fixture.runAdapterTasks()

        session.call.assertClosedWith(GrpcStatusCode.OK)
        session.call.assertMessages("first", "second")
    }
}
