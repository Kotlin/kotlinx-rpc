/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.grpc.client.testing.ExpectedServerEvent
import kotlinx.rpc.grpc.client.testing.GatedRequestFlow
import kotlinx.rpc.grpc.client.testing.RequestFlowCompletion
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.assertEvents
import kotlinx.rpc.grpc.client.testing.assertPayloadSequence
import kotlinx.rpc.grpc.client.testing.deterministicBytes
import kotlinx.rpc.grpc.client.testing.failingRequestFlow
import kotlinx.rpc.grpc.client.testing.render
import kotlinx.rpc.grpc.client.testing.sequencedPayloads
import kotlinx.rpc.grpc.client.testing.successfulClientStreamingEvents
import kotlinx.rpc.grpc.client.testing.successfulHalfDuplexEvents
import kotlinx.rpc.grpc.client.testing.successfulPingPongEvents
import kotlinx.rpc.grpc.client.testing.successfulServerStreamingEvents
import kotlinx.rpc.grpc.client.testing.successfulUnaryEvents
import kotlinx.rpc.grpc.client.testing.testChecksum
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kxrpc.testing.CallEvent
import kxrpc.testing.CallTrace
import kxrpc.testing.EventType
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.time.Duration.Companion.seconds

class GrpcTestHelpersTest {
    @Test
    fun traceAssertionsCoverStreamingOrderAndOccurrences() {
        val expected = successfulPingPongEvents(exchangeCount = 2)
        val trace = trace("trace-call", expected)

        trace.assertEvents("trace-call", expected)
        assertContains(trace.render(), "RESPONSE_MESSAGE_SENT occurrence 2")
        assertEquals(
            listOf(1U, 2U),
            expected.filter { it.type == EventType.REQUEST_MESSAGE_RECEIVED }.map { it.occurrence },
        )
    }

    @Test
    fun lifecycleBuildersCoverEveryPhaseTwoStreamingPattern() {
        val serverStreaming = successfulServerStreamingEvents(responseCount = 3)
        val clientStreaming = successfulClientStreamingEvents(requestCount = 3)
        val halfDuplex = successfulHalfDuplexEvents(requestCount = 2, responseCount = 3)

        assertEquals(
            listOf(1U, 2U, 3U),
            serverStreaming.filter { it.type == EventType.RESPONSE_MESSAGE_SENT }.map { it.occurrence },
        )
        assertEquals(
            listOf(1U, 2U, 3U),
            clientStreaming.filter { it.type == EventType.REQUEST_MESSAGE_RECEIVED }.map { it.occurrence },
        )
        assertEquals(
            EventType.CLIENT_HALF_CLOSED,
            halfDuplex.first { it.type == EventType.CLIENT_HALF_CLOSED }.type,
        )
        assertEquals(
            listOf(EventType.CLIENT_HALF_CLOSED, EventType.INITIAL_HEADERS_SENT),
            halfDuplex.dropWhile { it.type != EventType.CLIENT_HALF_CLOSED }.take(2).map { it.type },
        )
        assertEquals(successfulUnaryEvents(), successfulClientStreamingEvents(requestCount = 1))
    }

    @Test
    fun deterministicPayloadsIncludeSequenceAndChecksumDiagnostics() {
        val payloads = sequencedPayloads(listOf(0, 8, 8))

        assertContentEquals(byteArrayOf(19, 50, 77, 108), deterministicBytes(4, seed = 1))
        assertPayloadSequence(payloads, sequencedPayloads(listOf(0, 8, 8)))
        assertEquals(0xcbf29ce484222325UL, payloads.first().testChecksum())
        assertPayloadSequence(payloads, payloads.map { it.copyOf() })

        val failure = assertFailsWith<AssertionError> {
            assertPayloadSequence(payloads, payloads.reversed(), context = "responses")
        }
        assertContains(failure.message.orEmpty(), "sequence=1")
        assertContains(failure.message.orEmpty(), "checksum=0x")
    }

    @Test
    fun gatedRequestFlowMakesEachEmissionExplicit(): TestResult = test {
        val gated = GatedRequestFlow(listOf("first", "second"))
        val observed = mutableListOf<String>()
        val collection = launch { gated.flow.toList(observed) }

        gated.awaitCollectionStarted()
        assertEquals(emptyList(), observed)
        gated.releaseNext()
        gated.awaitNextEmission(1)
        assertEquals(listOf("first"), observed)
        gated.releaseNext()
        gated.awaitNextEmission(2)
        collection.join()

        assertEquals(listOf("first", "second"), observed)
        assertEquals(RequestFlowCompletion.Completed, gated.awaitCompletion())
    }

    @Test
    fun requestFlowProbeRecordsFailureAndCancellation(): TestResult = test {
        val expectedFailure = IllegalStateException("request failure")
        val failing = RequestFlowProbe(failingRequestFlow(listOf(1, 2), expectedFailure))
        val failureCollection = launch { runCatching { failing.flow.toList() } }
        failureCollection.join()

        val failed = assertIs<RequestFlowCompletion.Failed>(failing.awaitCompletion())
        assertEquals(expectedFailure, failed.cause)

        val gated = GatedRequestFlow(listOf(1, 2))
        val cancelledCollection = launch { gated.flow.toList() }
        gated.awaitCollectionStarted()
        gated.releaseNext()
        gated.awaitNextEmission(1)
        cancelledCollection.cancelAndJoin()

        gated.awaitCancellation()
    }

    private fun trace(callId: String, expected: List<ExpectedServerEvent>): CallTrace {
        return CallTrace {
            this.callId = callId
            events = expected.mapIndexed { index, event ->
                CallEvent {
                    sequence = (index + 1).toULong()
                    type = event.type
                    occurrence = event.occurrence
                }
            }
        }
    }

    private fun test(block: suspend TestScope.() -> Unit): TestResult {
        return runTestWithCoroutinesProbes(timeout = 5.seconds, body = block)
    }
}
