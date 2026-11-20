/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.ServerCallEvent
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AdapterRequestCountTest {
    @Test
    fun unaryWithoutARequestClosesWithInternal() = adapterTest { fixture ->
        val session = fixture.unary { it }

        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.events.filterIsInstance<ServerCallEvent.Closed>().single()
        assertContains(close.status.description.orEmpty(), "received none")
        assertEquals(listOf("close(INTERNAL)"), session.call.outbound())
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-715
    fun unaryWithTwoRequestsClosesWithInternalWithoutAResponse() = adapterTest { fixture ->
        var serviceInvoked = false
        val session = fixture.unary {
            serviceInvoked = true
            "$it-response"
        }

        fixture.runAdapterTasks()
        session.deliverRequest("first")
        fixture.runAdapterTasks()
        session.deliverRequest("second")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.events.filterIsInstance<ServerCallEvent.Closed>().single()
        assertContains(close.status.description.orEmpty(), "received two")
        assertEquals(listOf("close(INTERNAL)"), session.call.outbound())
        assertFalse(serviceInvoked)
    }

    @Test
    fun serverStreamingWithoutARequestClosesWithInternal() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf(it) }

        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.events.filterIsInstance<ServerCallEvent.Closed>().single()
        assertContains(close.status.description.orEmpty(), "received none")
        assertEquals(listOf("close(INTERNAL)"), session.call.outbound())
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-715
    fun serverStreamingWithTwoRequestsClosesWithInternalWithoutAResponse() = adapterTest { fixture ->
        var serviceInvoked = false
        val session = fixture.serverStreaming {
            serviceInvoked = true
            flowOf("$it-response")
        }

        fixture.runAdapterTasks()
        session.deliverRequest("first")
        fixture.runAdapterTasks()
        session.deliverRequest("second")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.events.filterIsInstance<ServerCallEvent.Closed>().single()
        assertContains(close.status.description.orEmpty(), "received two")
        assertEquals(listOf("close(INTERNAL)"), session.call.outbound())
        assertFalse(serviceInvoked)
    }

    @Test
    fun unaryWithTwoRequestsClosesWithInternal() = adapterTest { fixture ->
        val session = fixture.unary { "$it-response" }

        fixture.runAdapterTasks()
        session.deliverRequest("first")
        fixture.runAdapterTasks()
        session.deliverRequest("second")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.INTERNAL)
        assertContains(close.status.description.orEmpty(), "received two")
    }

    @Test
    fun serverStreamingWithTwoRequestsClosesWithInternal() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf("$it-response") }

        fixture.runAdapterTasks()
        session.deliverRequest("first")
        fixture.runAdapterTasks()
        session.deliverRequest("second")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        val close = session.call.assertClosedWith(GrpcStatusCode.INTERNAL)
        assertContains(close.status.description.orEmpty(), "received two")
    }
}
