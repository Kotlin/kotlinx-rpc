/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.testing.integration.EchoStatus
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.testing.RequestFlowCompletion
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.assertGrpcStatus
import kotlinx.rpc.grpc.client.testing.deterministicBytes
import kotlinx.rpc.grpc.client.testing.failedBidirectionalDuringRequestsEvents
import kotlinx.rpc.grpc.client.testing.finiteRequestFlow
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kotlinx.rpc.grpc.client.testing.successfulFullDuplexEvents
import kotlinx.rpc.grpc.client.testing.successfulHalfDuplexEvents
import kotlinx.rpc.grpc.client.testing.successfulPingPongEvents
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame

class GrpcClientBidirectionalStreamingTest {
    @Test
    fun emptyFullDuplexStream() = grpcClientTest {
        val requests = RequestFlowProbe(finiteRequestFlow(emptyList<StreamingOutputCallRequest>()))

        val responses = testService.fullDuplexCall(requests.flow).toList()

        assertEquals(emptyList(), responses)
        assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
        assertServerRequestPayloads(emptyList())
        assertServerTrace(successfulPingPongEvents(exchangeCount = 0))
    }

    @Test
    fun standardInteropFullDuplexPingPong() = grpcClientTest {
        val requestPayloads = canonicalRequestPayloads()
        val requests = requestChannel()

        coroutineScope {
            val responses = collectResponses(testService.fullDuplexCall(requests.probe.flow))
            requests.probe.awaitCollectionStarted()

            val received = requestPayloads.indices.map { index ->
                requests.channel.send(streamingRequest(requestPayloads[index], CANONICAL_RESPONSE_SIZES[index]))
                requests.probe.awaitNextEmission(expectedOccurrence = index + 1)
                responses.values.receive()
            }
            requests.channel.close()
            responses.collection.join()

            assertResponsePayloads(
                expected = CANONICAL_RESPONSE_SIZES.map(::ByteArray),
                actual = received.map { it.payload.body.toByteArray() },
            )
            assertCompressableResponses(received)
            assertEquals(RequestFlowCompletion.Completed, requests.probe.awaitCompletion())
            assertServerRequestPayloads(requestPayloads)
            assertServerTrace(successfulPingPongEvents(exchangeCount = requestPayloads.size))
        }
    }

    @Test
    fun fullDuplexSupportsMultipleResponsesPerRequest() = grpcClientTest {
        val requestPayloads = listOf(deterministicBytes(11, seed = 1), deterministicBytes(13, seed = 2))
        val requests = requestChannel()

        coroutineScope {
            val responses = collectResponses(testService.fullDuplexCall(requests.probe.flow))
            requests.probe.awaitCollectionStarted()

            requests.channel.send(streamingRequest(requestPayloads[0], 3, 5))
            requests.probe.awaitNextEmission(expectedOccurrence = 1)
            val firstResponses = listOf(responses.values.receive(), responses.values.receive())

            requests.channel.send(streamingRequest(requestPayloads[1], 7))
            requests.probe.awaitNextEmission(expectedOccurrence = 2)
            val lastResponse = responses.values.receive()
            requests.channel.close()
            responses.collection.join()

            assertResponsePayloads(
                expected = listOf(ByteArray(3), ByteArray(5), ByteArray(7)),
                actual = (firstResponses + lastResponse).map { it.payload.body.toByteArray() },
            )
            assertCompressableResponses(firstResponses + lastResponse)
            assertEquals(RequestFlowCompletion.Completed, requests.probe.awaitCompletion())
            assertServerRequestPayloads(requestPayloads)
            assertServerTrace(successfulFullDuplexEvents(responsesAfterRequest = listOf(2, 1)))
        }
    }

    @Test
    fun fullDuplexProducerAdvancesWhileResponsesArePending() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
    ) {
        val requestPayloads = listOf(deterministicBytes(17, seed = 1), deterministicBytes(19, seed = 2))
        val requests = requestChannel()

        coroutineScope {
            val responses = collectResponses(testService.fullDuplexCall(requests.probe.flow))
            requests.probe.awaitCollectionStarted()

            requests.channel.send(streamingRequest(requestPayloads[0], 23, 29))
            requests.probe.awaitNextEmission(expectedOccurrence = 1)
            val firstResponse = responses.values.receive()

            requests.channel.send(streamingRequest(requestPayloads[1], 31))
            requests.probe.awaitNextEmission(expectedOccurrence = 2)
            awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, occurrence = 2U)
            assertServerEventAbsentUntil(
                event = EventType.RESPONSE_MESSAGE_SENT,
                barrier = BarrierType.SEND_RESPONSE,
                occurrence = 2U,
            )

            val remainingResponses = listOf(responses.values.receive(), responses.values.receive())
            requests.channel.close()
            responses.collection.join()
            val completedResponses = listOf(firstResponse) + remainingResponses
            assertResponsePayloads(
                expected = listOf(ByteArray(23), ByteArray(29), ByteArray(31)),
                actual = completedResponses.map { it.payload.body.toByteArray() },
            )
            assertCompressableResponses(completedResponses)
            assertEquals(RequestFlowCompletion.Completed, requests.probe.awaitCompletion())
            assertServerRequestPayloads(requestPayloads)
            assertServerTrace(
                successfulFullDuplexEvents(
                    responsesAfterRequest = listOf(1, 2),
                )
            )
        }
    }

    @Test
    fun fullDuplexDeliversPendingResponseAfterClientHalfClose() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
    ) {
        val requestPayloads = listOf(deterministicBytes(43, seed = 1), deterministicBytes(47, seed = 2))
        val requests = requestChannel()

        coroutineScope {
            val responses = collectResponses(testService.fullDuplexCall(requests.probe.flow))
            requests.probe.awaitCollectionStarted()

            requests.channel.send(streamingRequest(requestPayloads[0], 53))
            requests.probe.awaitNextEmission(expectedOccurrence = 1)
            val firstResponse = responses.values.receive()

            requests.channel.send(streamingRequest(requestPayloads[1], 59))
            requests.probe.awaitNextEmission(expectedOccurrence = 2)
            requests.channel.close()
            awaitServerEvent(EventType.CLIENT_HALF_CLOSED)
            assertServerEventAbsentUntil(
                event = EventType.RESPONSE_MESSAGE_SENT,
                barrier = BarrierType.SEND_RESPONSE,
                occurrence = 2U,
            )

            val secondResponse = responses.values.receive()
            responses.collection.join()
            assertResponsePayloads(
                expected = listOf(ByteArray(53), ByteArray(59)),
                actual = listOf(firstResponse, secondResponse).map { it.payload.body.toByteArray() },
            )
            assertCompressableResponses(listOf(firstResponse, secondResponse))
            assertEquals(RequestFlowCompletion.Completed, requests.probe.awaitCompletion())
            assertServerRequestPayloads(requestPayloads)
            assertServerTrace(
                successfulFullDuplexEvents(
                    responsesAfterRequest = listOf(1, 0),
                    responsesAfterHalfClose = 1,
                )
            )
        }
    }

    @Test
    fun fullDuplexRequestedStatusFailsBeforeResponse() = grpcClientTest {
        val expectedMessage = "full-duplex requested failure"
        val request = streamingRequest(
            payloadBytes = deterministicBytes(31),
            responseStatus = EchoStatus {
                code = GrpcStatusCode.DATA_LOSS.value
                message = expectedMessage
            },
        )
        val requests = RequestFlowProbe(finiteRequestFlow(listOf(request)))

        assertGrpcStatus(GrpcStatusCode.DATA_LOSS, expectedMessage) {
            testService.fullDuplexCall(requests.flow).toList()
        }

        assertServerRequestPayloads(listOf(request.payload.body.toByteArray()))
        assertServerTrace(failedBidirectionalDuringRequestsEvents(requestCount = 1))
    }

    @Test
    fun emptyHalfDuplexStream() = grpcClientTest {
        val requests = RequestFlowProbe(finiteRequestFlow(emptyList<StreamingOutputCallRequest>()))

        val responses = testService.halfDuplexCall(requests.flow).toList()

        assertEquals(emptyList(), responses)
        assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
        assertServerRequestPayloads(emptyList())
        assertServerTrace(successfulHalfDuplexEvents(requestCount = 0, responseCount = 0))
    }

    @Test
    fun halfDuplexBuffersCanonicalResponsesUntilClientHalfClose() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.DELIVER_CLIENT_HALF_CLOSE)),
    ) {
        val requestPayloads = canonicalRequestPayloads()
        val requests = RequestFlowProbe(
            finiteRequestFlow(
                requestPayloads.mapIndexed { index, payload ->
                    streamingRequest(payload, CANONICAL_RESPONSE_SIZES[index])
                }
            )
        )

        coroutineScope {
            val responses = async { testService.halfDuplexCall(requests.flow).toList() }

            awaitServerEvent(EventType.CLIENT_HALF_CLOSED)
            assertFalse(responses.isCompleted, "half-duplex responses completed before client half-close delivery")
            assertServerEventAbsentUntil(
                event = EventType.RESPONSE_MESSAGE_SENT,
                barrier = BarrierType.DELIVER_CLIENT_HALF_CLOSE,
            )

            val completedResponses = responses.await()
            assertResponsePayloads(
                expected = CANONICAL_RESPONSE_SIZES.map(::ByteArray),
                actual = completedResponses.map { it.payload.body.toByteArray() },
            )
            assertCompressableResponses(completedResponses)
            assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
            assertServerRequestPayloads(requestPayloads)
            assertServerTrace(
                successfulHalfDuplexEvents(
                    requestCount = requestPayloads.size,
                    responseCount = CANONICAL_RESPONSE_SIZES.size,
                )
            )
        }
    }

    @Test
    fun halfDuplexRequestFailurePropagatesWithoutResponses() = grpcClientTest {
        val expectedFailure = IllegalStateException("half-duplex request source failed")
        val request = streamingRequest(deterministicBytes(37), 41)
        val requests = RequestFlowProbe(
            flow {
                emit(request)
                awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED)
                throw expectedFailure
            }
        )

        val actualFailure = kotlin.test.assertFailsWith<IllegalStateException> {
            testService.halfDuplexCall(requests.flow).toList()
        }

        assertEquals(expectedFailure.message, actualFailure.message)
        val completion = assertIs<RequestFlowCompletion.Failed>(requests.awaitCompletion())
        assertSame(expectedFailure, completion.cause)
        assertServerRequestPayloads(listOf(request.payload.body.toByteArray()))
        assertServerTraceWithEitherTerminal(
            failedBidirectionalDuringRequestsEvents(requestCount = 1).dropLast(1)
        )
    }

    private companion object {
        val CANONICAL_REQUEST_SIZES: List<Int> = listOf(27_182, 8, 1_828, 45_904)
        val CANONICAL_RESPONSE_SIZES: List<Int> = listOf(31_415, 9, 2_653, 58_979)

        fun canonicalRequestPayloads(): List<ByteArray> {
            return CANONICAL_REQUEST_SIZES.mapIndexed { index, size ->
                deterministicBytes(size, seed = index + 1)
            }
        }

        fun streamingRequest(
            payloadBytes: ByteArray,
            vararg responseSizes: Int,
            responseStatus: EchoStatus? = null,
        ): StreamingOutputCallRequest {
            return StreamingOutputCallRequest {
                responseType = PayloadType.COMPRESSABLE
                responseParameters = responseSizes.map { size ->
                    ResponseParameters { this.size = size }
                }
                payload = Payload {
                    type = PayloadType.COMPRESSABLE
                    body = ByteString(*payloadBytes)
                }
                responseStatus?.let { this.responseStatus = it }
            }
        }

        fun requestChannel(): RequestChannel {
            val channel = Channel<StreamingOutputCallRequest>(Channel.UNLIMITED)
            return RequestChannel(channel, RequestFlowProbe(flow {
                for (request in channel) emit(request)
            }))
        }

        fun kotlinx.coroutines.CoroutineScope.collectResponses(
            source: kotlinx.coroutines.flow.Flow<StreamingOutputCallResponse>,
        ): ResponseCollection {
            val values = Channel<StreamingOutputCallResponse>(Channel.UNLIMITED)
            val collection = launch {
                source.collect { values.send(it) }
                values.close()
            }
            return ResponseCollection(values, collection)
        }

        fun assertCompressableResponses(responses: List<StreamingOutputCallResponse>) {
            assertEquals(
                List(responses.size) { PayloadType.COMPRESSABLE },
                responses.map { it.payload.type },
            )
        }
    }

    private data class RequestChannel(
        val channel: Channel<StreamingOutputCallRequest>,
        val probe: RequestFlowProbe<StreamingOutputCallRequest>,
    )

    private data class ResponseCollection(
        val values: Channel<StreamingOutputCallResponse>,
        val collection: kotlinx.coroutines.Job,
    )
}
