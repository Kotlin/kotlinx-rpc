/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.client.testing.GrpcClientTestFixture
import kotlinx.rpc.grpc.client.testing.RequestFlowCompletion
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kxrpc.testing.EventType
import kxrpc.testing.FlowControlBehavior
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GrpcClientBackpressureTest {
    @Test
    fun requestProducerRemainsBoundedAndAdvancesWithInboundDemand() = grpcClientTest(
        scenario = { flowControl = createManualInboundDemand() },
    ) {
        val producerExceededLimit = CompletableDeferred<Unit>()
        val request = streamingInputRequest()
        val requests = RequestFlowProbe(
            flow {
                repeat(REQUEST_COUNT) { index ->
                    emit(request)
                    if (index + 1 > MAX_REQUEST_SOURCE_LEAD) producerExceededLimit.complete(Unit)
                }
            }
        )

        coroutineScope {
            val response = async {
                testService.streamingInputCall(requests.flow)
            }
            requests.awaitCollectionStarted()
            awaitServerEvent(EventType.CALL_ACCEPTED)

            grantInboundDemand()
            awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED)
            assertFalse(
                producerExceededLimit.isCompleted,
                "request source advanced more than $MAX_REQUEST_SOURCE_LEAD messages ahead of server demand",
            )
            assertEquals(1, serverRequestCount())
            assertFalse(response.isCompleted, "client-streaming response completed before all demand was granted")

            grantInboundDemand()
            awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, occurrence = 2U)
            assertFalse(
                producerExceededLimit.isCompleted,
                "request source advanced more than $MAX_REQUEST_SOURCE_LEAD messages after incremental demand",
            )
            assertEquals(2, serverRequestCount())

            response.cancelAndJoin()
            requests.awaitCancellation()
            assertServerObservedCancellation()
        }
    }

    @Test
    fun blockedResponsesResumeWhenTheCollectorAdvances() = grpcClientTest(
        scenario = { flowControl = responseReadiness() },
    ) {
        val permits = Channel<Unit>(Channel.UNLIMITED)
        var responseCount = 0

        coroutineScope {
            val collection = launch {
                largeResponseStream()
                    .onEach {
                        permits.receive()
                        responseCount++
                    }
                    .collect()
            }

            awaitServerEvent(EventType.RESPONSE_DELIVERY_BLOCKED)
            assertTrue(responseCount < RESPONSE_COUNT, "all responses bypassed a blocked collector")

            repeat(RESPONSE_COUNT) { permits.send(Unit) }
            awaitServerEvent(EventType.RESPONSE_DELIVERY_READY)
            collection.join()

            assertEquals(RESPONSE_COUNT, responseCount)
            assertEquals(RESPONSE_COUNT, serverResponseCount())
        }
    }

    @Test
    fun bidirectionalRequestDemandAdvancesWhileResponsesAreBlocked() = grpcClientTest(
        scenario = {
            flowControl = FlowControlBehavior {
                manualInboundDemand = true
                respectResponseReadiness = true
            }
        },
    ) {
        val permits = Channel<Unit>(Channel.UNLIMITED)
        val requests = RequestFlowProbe(bidirectionalRequests(BIDI_REQUEST_COUNT))
        var responseCount = 0

        coroutineScope {
            val collection = launch {
                testService.fullDuplexCall(requests.flow)
                    .onEach {
                        permits.receive()
                        responseCount++
                    }
                    .collect()
            }
            requests.awaitCollectionStarted()
            awaitServerEvent(EventType.CALL_ACCEPTED)

            grantInboundDemand(BIDI_INITIAL_DEMAND.toUInt())
            awaitServerEvent(
                EventType.REQUEST_MESSAGE_RECEIVED,
                occurrence = BIDI_INITIAL_DEMAND.toUInt(),
            )
            awaitServerEvent(EventType.RESPONSE_DELIVERY_BLOCKED)

            grantInboundDemand((BIDI_REQUEST_COUNT - BIDI_INITIAL_DEMAND).toUInt())
            awaitServerEvent(
                EventType.REQUEST_MESSAGE_RECEIVED,
                occurrence = BIDI_REQUEST_COUNT.toUInt(),
            )
            assertEquals(BIDI_REQUEST_COUNT, serverRequestCount())
            assertTrue(responseCount < BIDI_REQUEST_COUNT, "responses did not remain independently blocked")

            repeat(BIDI_REQUEST_COUNT) { permits.send(Unit) }
            awaitServerEvent(EventType.RESPONSE_DELIVERY_READY)
            collection.join()

            assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
            assertEquals(BIDI_REQUEST_COUNT, responseCount)
            assertEquals(BIDI_REQUEST_COUNT, serverResponseCount())
        }
    }

    @Test
    fun cancellationWhileResponseDeliveryIsBlockedCleansUpTheCall() = grpcClientTest(
        scenario = { flowControl = responseReadiness() },
    ) {
        coroutineScope {
            val collectorBlocker = CompletableDeferred<Unit>()
            val collection = launch {
                largeResponseStream().collect { collectorBlocker.await() }
            }

            awaitServerEvent(EventType.RESPONSE_DELIVERY_BLOCKED)
            collection.cancelAndJoin()

            assertServerObservedCancellation()
            assertEquals(0U, serverDiagnostics().activeCallCount)
        }
    }

    @Test
    fun unrelatedUnaryCallProgressesWhileStreamingResponsesAreBlocked() = grpcClientTest(
        scenario = { flowControl = responseReadiness() },
    ) {
        coroutineScope {
            val collectorBlocker = CompletableDeferred<Unit>()
            val blockedCollection = launch {
                largeResponseStream().collect { collectorBlocker.await() }
            }

            awaitServerEvent(EventType.RESPONSE_DELIVERY_BLOCKED)
            val unaryResponse = testService.emptyCall(Empty {})

            assertEquals(Empty {}, unaryResponse)
            awaitServerEvent(EventType.CALL_CLOSED)
            assertEquals(2, serverTrace().events.count { it.type == EventType.CALL_ACCEPTED })

            blockedCollection.cancelAndJoin()
            awaitServerEvent(EventType.CLIENT_CANCELLED)
            val terminalEvents = serverTrace().events.count {
                it.type == EventType.CALL_CLOSED || it.type == EventType.CLIENT_CANCELLED
            }
            assertEquals(2, terminalEvents)
        }
    }

    private suspend fun GrpcClientTestFixture.serverRequestCount(): Int {
        return serverTrace().events.count { it.type == EventType.REQUEST_MESSAGE_RECEIVED }
    }

    private suspend fun GrpcClientTestFixture.serverResponseCount(): Int {
        return serverTrace().events.count { it.type == EventType.RESPONSE_MESSAGE_SENT }
    }

    private fun GrpcClientTestFixture.largeResponseStream(): Flow<StreamingOutputCallResponse> {
        return testService.streamingOutputCall(
            StreamingOutputCallRequest {
                responseParameters = List(RESPONSE_COUNT) {
                    ResponseParameters { size = RESPONSE_PAYLOAD_SIZE }
                }
            }
        )
    }

    private companion object {
        const val REQUEST_COUNT: Int = 4_096
        const val REQUEST_PAYLOAD_SIZE: Int = 16 * 1_024
        const val MAX_REQUEST_SOURCE_LEAD: Int = 2_048
        const val RESPONSE_COUNT: Int = 128
        const val RESPONSE_PAYLOAD_SIZE: Int = 64 * 1_024
        const val BIDI_INITIAL_DEMAND: Int = 64
        const val BIDI_REQUEST_COUNT: Int = 128

        fun createManualInboundDemand(): FlowControlBehavior = FlowControlBehavior {
            manualInboundDemand = true
        }

        fun responseReadiness(): FlowControlBehavior = FlowControlBehavior {
            respectResponseReadiness = true
        }

        fun streamingInputRequest(): StreamingInputCallRequest {
            return StreamingInputCallRequest {
                payload = Payload { body = ByteString(*ByteArray(REQUEST_PAYLOAD_SIZE)) }
            }
        }

        fun bidirectionalRequests(count: Int): Flow<StreamingOutputCallRequest> = flow {
            repeat(count) {
                emit(
                    StreamingOutputCallRequest {
                        responseParameters = listOf(ResponseParameters { size = RESPONSE_PAYLOAD_SIZE })
                    }
                )
            }
        }
    }
}
