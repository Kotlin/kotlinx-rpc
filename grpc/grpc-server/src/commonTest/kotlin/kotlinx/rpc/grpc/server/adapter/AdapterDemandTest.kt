/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.server.testing.assertNoMessages
import kotlin.test.Test
import kotlin.test.assertEquals

class AdapterDemandTest {
    @Test
    fun unaryAcceptsARequestedMessageAndCompletes() = adapterTest { fixture ->
        val session = fixture.unary { it }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        session.call.assertMessages("request")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun serverStreamingAcceptsARequestedMessageAndCompletes() = adapterTest { fixture ->
        val session = fixture.serverStreaming { flowOf("$it-response") }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        session.call.assertMessages("request-response")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun bidiStreamingContinuesToAcceptRequestedMessages() = adapterTest { fixture ->
        val received = mutableListOf<String>()
        val session = fixture.bidiStreaming { requests ->
            requests.map {
                received += it
                "$it-response"
            }
        }

        val messages = listOf("first", "second", "third")
        for (message in messages) {
            fixture.runAdapterTasks()
            session.deliverRequest(message)
        }
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(messages, received)
        session.call.assertMessages(messages.map { "$it-response" })
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun clientStreamingWithoutCollectionDoesNotRequestMessages() = adapterTest { fixture ->
        val session = fixture.clientStreaming { "response" }

        fixture.runAdapterTasks()

        assertEquals(0, session.call.requestedTotal())
        session.call.assertMessages("response")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    @Test
    fun failureDuringRequestCollectionClosesWithoutWaitingForMoreRequests() = adapterTest { fixture ->
        val session = fixture.clientStreaming { requests ->
            requests.collect { throw IllegalStateException("request rejected") }
            "unreachable response"
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        fixture.runAdapterTasks()

        session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        if (session.call.availableRequestCredits > 0) {
            session.deliverRequest("in-flight request")
            fixture.runAdapterTasks()
        }

        session.call.assertNoMessages()
        session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
    }

    @Test
    fun collectingRequestsTwiceClosesWithUnknown() = adapterTest { fixture ->
        val received = mutableListOf<String>()
        val session = fixture.clientStreaming { requests ->
            requests.toList(received)
            requests.toList()
            "unreachable response"
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("request"), received)
        session.call.assertNoMessages()
        session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
    }
}
