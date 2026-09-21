/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.getAll
import kotlin.test.Test
import kotlin.test.assertEquals

class NativeClientResponseGateTest {
    @Test
    fun messageBeforeNonEmptyHeadersIsHeldWithItsDemand() {
        val harness = GateHarness()

        harness.message("first")
        assertEquals(emptyList(), harness.events)

        harness.headers("initial")
        harness.close(GrpcStatusCode.INTERNAL)

        assertEquals(
            listOf("headers:initial", "message:first", "request-next", "close:INTERNAL"),
            harness.events,
        )
    }

    @Test
    fun messageBeforeEmptyHeadersProvesAHeadersPhase() {
        val harness = GateHarness()

        harness.message("first")
        assertEquals(emptyList(), harness.events)

        harness.headers()
        harness.close(GrpcStatusCode.INTERNAL)

        assertEquals(
            listOf("headers", "message:first", "request-next", "close:INTERNAL"),
            harness.events,
        )
    }

    @Test
    fun emptyHeadersBeforeNonOkCloseAreSuppressed() {
        val harness = GateHarness()

        harness.headers()
        assertEquals(emptyList(), harness.events)

        harness.close(GrpcStatusCode.UNAVAILABLE)

        assertEquals(listOf("close:UNAVAILABLE"), harness.events)
    }

    @Test
    fun emptyHeadersBeforeOkCloseAreDelivered() {
        val harness = GateHarness()

        harness.headers()
        assertEquals(emptyList(), harness.events)

        harness.close(GrpcStatusCode.OK)

        assertEquals(listOf("headers", "close:OK"), harness.events)
    }

    @Test
    fun nonEmptyHeadersBeforeNonOkCloseAreDelivered() {
        val harness = GateHarness()

        harness.headers("initial")
        harness.close(GrpcStatusCode.INTERNAL)

        assertEquals(listOf("headers:initial", "close:INTERNAL"), harness.events)
    }

    private class GateHarness {
        val events: MutableList<String> = mutableListOf()

        private val gate = NativeClientResponseGate<String>(
            onHeaders = { headers ->
                val marker = headers.getAll(ORDER_KEY).singleOrNull()
                events += if (marker == null) "headers" else "headers:$marker"
            },
            onMessage = { message -> events += "message:$message" },
        )

        fun headers(marker: String? = null) {
            val headers = GrpcMetadata()
            if (marker != null) headers.append(ORDER_KEY, marker)
            gate.initialMetadataReceived(headers)
        }

        fun message(message: String) {
            gate.messageReceived(message) { events += "request-next" }
        }

        fun close(code: GrpcStatusCode) {
            gate.flushBeforeClose(GrpcStatus(code))
            events += "close:${code.name}"
        }
    }

    private companion object {
        const val ORDER_KEY: String = "x-callback-order"
    }
}
