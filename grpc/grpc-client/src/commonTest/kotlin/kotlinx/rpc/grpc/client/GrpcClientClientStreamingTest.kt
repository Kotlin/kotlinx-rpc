/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.client.testing.GatedRequestFlow
import kotlinx.rpc.grpc.client.testing.RequestFlowCompletion
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.deterministicBytes
import kotlinx.rpc.grpc.client.testing.failedClientStreamingDuringRequestsEvents
import kotlinx.rpc.grpc.client.testing.finiteRequestFlow
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kotlinx.rpc.grpc.client.testing.successfulClientStreamingEvents
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

class GrpcClientClientStreamingTest {
    @Test
    fun emptyRequestStream() = grpcClientTest {
        val requests = RequestFlowProbe(finiteRequestFlow(emptyList<StreamingInputCallRequest>()))

        val response = testService.streamingInputCall(requests.flow)

        assertEquals(0, response.aggregatedPayloadSize)
        assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
        assertServerRequestPayloads(emptyList())
        assertServerTrace(successfulClientStreamingEvents(requestCount = 0))
    }

    @Test
    fun singleRequest() = grpcClientTest {
        val payload = deterministicBytes(SINGLE_REQUEST_SIZE)
        val requests = RequestFlowProbe(finiteRequestFlow(listOf(streamingRequest(payload))))

        val response = testService.streamingInputCall(requests.flow)

        assertEquals(payload.size, response.aggregatedPayloadSize)
        assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
        assertServerRequestPayloads(listOf(payload))
        assertServerTrace(successfulClientStreamingEvents(requestCount = 1))
    }

    @Test
    fun standardInteropClientStreaming() = grpcClientTest {
        val payloads = STANDARD_INTEROP_REQUEST_SIZES.mapIndexed { index, size ->
            deterministicBytes(size, seed = index + 1)
        }
        val requests = RequestFlowProbe(finiteRequestFlow(payloads.map(::streamingRequest)))

        val response = testService.streamingInputCall(requests.flow)

        assertEquals(STANDARD_INTEROP_AGGREGATED_SIZE, response.aggregatedPayloadSize)
        assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
        assertServerRequestPayloads(payloads)
        assertServerTrace(successfulClientStreamingEvents(STANDARD_INTEROP_REQUEST_SIZES.size))
    }

    @Test
    fun individualRequestOccurrenceRemainsGated() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.DELIVER_REQUEST_MESSAGE, occurrence = 2U)),
    ) {
        val payloads = listOf(3, 5, 7).mapIndexed { index, size -> deterministicBytes(size, index + 1) }
        val requests = GatedRequestFlow(payloads.map(::streamingRequest))

        coroutineScope {
            val response = async { testService.streamingInputCall(requests.flow) }
            requests.awaitCollectionStarted()

            requests.releaseNext()
            requests.awaitNextEmission(expectedOccurrence = 1)
            awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, occurrence = 1U)

            requests.releaseNext()
            requests.awaitNextEmission(expectedOccurrence = 2)
            awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, occurrence = 2U)
            assertFalse(response.isCompleted, "response completed while the second request remained gated")
            releaseServerBarrier(BarrierType.DELIVER_REQUEST_MESSAGE, occurrence = 2U)

            requests.releaseNext()
            requests.awaitNextEmission(expectedOccurrence = 3)

            assertEquals(payloads.sumOf(ByteArray::size), response.await().aggregatedPayloadSize)
            assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
            assertServerRequestPayloads(payloads)
            assertServerTrace(successfulClientStreamingEvents(requestCount = payloads.size))
        }
    }

    @Test
    fun responseWaitsForClientHalfClose() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.DELIVER_CLIENT_HALF_CLOSE)),
    ) {
        coroutineScope {
            val response = async {
                testService.streamingInputCall(finiteRequestFlow(listOf(streamingRequest(ByteArray(11)))))
            }

            awaitServerEvent(EventType.CLIENT_HALF_CLOSED)
            assertFalse(response.isCompleted, "response completed before the server received client half-close")
            assertServerEventAbsentUntil(
                event = EventType.RESPONSE_MESSAGE_SENT,
                barrier = BarrierType.DELIVER_CLIENT_HALF_CLOSE,
            )

            assertEquals(11, response.await().aggregatedPayloadSize)
            assertServerRequestPayloads(listOf(ByteArray(11)))
            assertServerTrace(successfulClientStreamingEvents(requestCount = 1))
        }
    }

    @Test
    fun requestFlowFailurePropagatesWithoutHalfClose() = grpcClientTest {
        val expectedFailure = IllegalStateException("client-streaming request source failed")
        val request = streamingRequest(deterministicBytes(13))
        val requests = RequestFlowProbe(
            flow {
                emit(request)
                awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED)
                throw expectedFailure
            }
        )

        val actualFailure = assertFailsWith<IllegalStateException> {
            testService.streamingInputCall(requests.flow)
        }

        assertEquals(expectedFailure.message, actualFailure.message)
        val completion = assertIs<RequestFlowCompletion.Failed>(requests.awaitCompletion())
        assertSame(expectedFailure, completion.cause)
        assertServerRequestPayloads(listOf(request.payload.body.toByteArray()))
        assertServerTraceWithEitherTerminal(
            failedClientStreamingDuringRequestsEvents(requestCount = 1).dropLast(1)
        )
    }

    private companion object {
        const val SINGLE_REQUEST_SIZE: Int = 7
        val STANDARD_INTEROP_REQUEST_SIZES: List<Int> = listOf(27_182, 8, 1_828, 45_904)
        val STANDARD_INTEROP_AGGREGATED_SIZE: Int = STANDARD_INTEROP_REQUEST_SIZES.sum()

        fun streamingRequest(payloadBytes: ByteArray): StreamingInputCallRequest {
            return StreamingInputCallRequest {
                payload = Payload {
                    type = PayloadType.COMPRESSABLE
                    body = ByteString(*payloadBytes)
                }
            }
        }
    }
}
