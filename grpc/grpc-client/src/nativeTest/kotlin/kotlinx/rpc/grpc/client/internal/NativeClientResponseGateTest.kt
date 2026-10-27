/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.rpc.grpc.GrpcMetadata
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
        harness.close()

        assertEquals(listOf("headers:initial", "message:first", "request-next", "close"), harness.events)
    }

    @Test
    fun messageBeforeEmptyHeadersIsHeldWithItsDemand() {
        val harness = GateHarness()

        harness.message("first")
        assertEquals(emptyList(), harness.events)

        harness.headers()
        harness.close()

        assertEquals(listOf("headers", "message:first", "request-next", "close"), harness.events)
    }

    @Test
    fun emptyHeadersAreDeliveredBeforeAnyMessage() {
        val harness = GateHarness()

        harness.headers()
        assertEquals(listOf("headers"), harness.events)

        harness.message("first")
        harness.close()

        assertEquals(listOf("headers", "message:first", "request-next", "close"), harness.events)
    }

    @Test
    fun messageWithoutHeadersIsDeliveredAfterEmptyHeadersOnClose() {
        val harness = GateHarness()

        harness.message("first")
        harness.close()

        assertEquals(listOf("headers", "message:first", "request-next", "close"), harness.events)
    }

    @Test
    fun closeWithoutHeadersOrMessagesDeliversNoHeaders() {
        val harness = GateHarness()

        harness.close()

        assertEquals(listOf("close"), harness.events)
    }

    /**
     * Drives a [NativeClientResponseGate] the way [NativeClientCall] does, without a real C-core call, and records
     * everything the gate hands on in [events]: `headers` (or `headers:<marker>`), `message:<value>`,
     * `request-next` when the gate asks for the next message, and `close` for the terminal callback.
     */
    private class GateHarness {
        val events: MutableList<String> = mutableListOf()

        private val gate = NativeClientResponseGate<String>(
            onHeaders = { headers ->
                val marker = headers.getAll(ORDER_KEY).singleOrNull()
                events += if (marker == null) "headers" else "headers:$marker"
            },
            onMessage = { message -> events += "message:$message" },
        )

        /** Completes the initial-metadata operation. Without [marker], the headers are empty. */
        fun headers(marker: String? = null) {
            val headers = GrpcMetadata()
            if (marker != null) headers.append(ORDER_KEY, marker)
            gate.initialMetadataReceived(headers)
        }

        /** Completes a receive operation with [message]. */
        fun message(message: String) {
            gate.messageReceived(message) { events += "request-next" }
        }

        /** Closes the call: delivers the pending message, then records the terminal callback. */
        fun close() {
            gate.deliverPendingMessage()
            events += "close"
        }
    }

    private companion object {
        const val ORDER_KEY: String = "x-callback-order"
    }
}
