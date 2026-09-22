/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.rpc.grpc.client.testing.ExpectedServerEvent
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kotlinx.rpc.grpc.statusCode
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GrpcClientCancellationTest {
    @Test
    fun uncollectedColdResponseFlowDoesNotStartCall() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(cancellationInterceptor(callbackEvents)) },
            configureScenario = false,
        ) {
            testService.streamingOutputCall(streamingRequest(3))

            assertEquals(emptyList(), callbackEvents)
        }
    }

    @Test
    fun callerCancellationStopsUnaryBeforeInitialHeaders() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(cancellationInterceptor(callbackEvents)) },
            barriers = listOf(serverBarrier(BarrierType.SEND_INITIAL_HEADERS)),
        ) {
            try {
                coroutineScope {
                    val call = async { testService.emptyCall(Empty {}) }
                    awaitServerEvent(EventType.CLIENT_HALF_CLOSED)

                    call.cancelAndJoin()

                    assertTrue(call.isCancelled)
                    assertServerCancelledTrace(cancelledUnaryBeforeHeadersEvents())
                    assertCancellationCallbacks(callbackEvents, headersExpected = false)
                }
            } finally {
                releaseServerBarrier(BarrierType.SEND_INITIAL_HEADERS)
            }
        }
    }

    @Test
    fun earlyServerStreamingCompletionCancelsTheRemoteCall() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
    ) {
        try {
            val responses = testService.streamingOutputCall(streamingRequest(5, 7))
                .take(1)
                .toList()

            assertEquals(1, responses.size)
            assertEquals(5, responses.single().payload.body.size)
            assertServerCancelledTrace(cancelledServerStreamingEvents(responseCount = 1))
        } finally {
            releaseServerBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)
        }
    }

    @Test
    fun callerCancellationStopsOutstandingClientStreamingRequestPull() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(cancellationInterceptor(callbackEvents)) },
            barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE)),
        ) {
            try {
                coroutineScope {
                    val requests = RequestFlowProbe(
                        flow {
                            emit(StreamingInputCallRequest {})
                            awaitCancellation()
                        }
                    )
                    val call = async { testService.streamingInputCall(requests.flow) }
                    requests.awaitCollectionStarted()
                    awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED)

                    call.cancelAndJoin()

                    requests.awaitCancellation()
                    assertTrue(call.isCancelled)
                    assertServerCancelledTraceOneOf(
                        listOf(
                            cancelledClientStreamingEvents(requestCount = 1, clientHalfClosed = false),
                            cancelledClientStreamingEvents(requestCount = 1, clientHalfClosed = true),
                        )
                    )
                    assertCancellationCallbacks(callbackEvents, headersExpected = false)
                }
            } finally {
                releaseServerBarrier(BarrierType.SEND_RESPONSE)
            }
        }
    }

    @Test
    fun earlyBidirectionalCompletionCancelsProducerAndRemoteCall() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
    ) {
        try {
            val requests = RequestFlowProbe(
                flow {
                    emit(streamingRequest(11))
                    awaitCancellation()
                }
            )

            val responses = testService.fullDuplexCall(requests.flow)
                .take(1)
                .toList()

            assertEquals(1, responses.size)
            assertEquals(11, responses.single().payload.body.size)
            requests.awaitCancellation()
            assertServerCancelledTraceOneOf(
                listOf(
                    cancelledBidirectionalEvents(responseCount = 1, clientHalfClosed = false),
                    cancelledBidirectionalEvents(responseCount = 1, clientHalfClosed = true),
                )
            )
        } finally {
            releaseServerBarrier(BarrierType.CLOSE_CALL)
        }
    }

    @Test
    fun callerCancellationAfterFinalResponseWinsAgainstBlockedClose() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(cancellationInterceptor(callbackEvents)) },
            barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
        ) {
            try {
                coroutineScope {
                    val responseObserved = CompletableDeferred<Unit>()
                    val call = launch {
                        testService.streamingOutputCall(streamingRequest(13))
                            .onEach { responseObserved.complete(Unit) }
                            .toList()
                    }
                    responseObserved.await()
                    awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT)

                    call.cancelAndJoin()

                    assertTrue(call.isCancelled)
                    assertServerCancelledTrace(cancelledServerStreamingEvents(responseCount = 1))
                    assertCancellationCallbacks(callbackEvents, headersExpected = true)
                }
            } finally {
                releaseServerBarrier(BarrierType.CLOSE_CALL)
            }
        }
    }

    @Test
    fun cancellationFromOnHeadersCancelsTheRemoteCall() {
        val headersObserved = CompletableDeferred<Unit>()
        grpcClientTest(
            clientConfig = {
                intercept(
                    object : GrpcClientInterceptor {
                        override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                            request: Flow<Request>,
                        ): Flow<Response> {
                            onHeaders { headersObserved.complete(Unit) }
                            return proceed(request)
                        }
                    }
                )
            },
            barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
        ) {
            try {
                coroutineScope {
                    val call = async {
                        testService.streamingOutputCall(streamingRequest(5, 7)).toList()
                    }
                    headersObserved.await()
                    call.cancelAndJoin()
                    assertTrue(call.isCancelled)
                }
                assertServerCancelledTrace(cancelledServerStreamingEvents(responseCount = 1))
            } finally {
                releaseServerBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)
            }
        }
    }

    @Test
    fun cancellationAfterTerminalCloseDoesNotEmitAnotherTerminalEvent() = grpcClientTest {
        coroutineScope {
            val call = launch {
                testService.streamingOutputCall(streamingRequest(17)).toList()
            }
            call.join()
            assertServerTrace(
                listOf(
                    ExpectedServerEvent(EventType.CALL_ACCEPTED),
                    ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
                    ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
                    ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT),
                    ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT),
                    ExpectedServerEvent(EventType.CALL_CLOSED),
                )
            )

            call.cancelAndJoin()

            val terminalEvents = serverTrace().events.filter {
                it.type == EventType.CALL_CLOSED || it.type == EventType.CLIENT_CANCELLED
            }
            assertEquals(listOf(EventType.CALL_CLOSED), terminalEvents.map { it.type })
        }
    }

    @Test
    fun shutdownNowCancelsEveryActiveCallShape() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_INITIAL_HEADERS)),
    ) {
        try {
            supervisorScope {
                val clientRequests = RequestFlowProbe(hangingClientStreamingRequests())
                val bidiRequests = RequestFlowProbe(hangingBidirectionalRequests())
                val calls: List<Deferred<*>> = listOf(
                    async { testService.emptyCall(Empty {}) },
                    async { testService.streamingOutputCall(streamingRequest(19)).toList() },
                    async { testService.streamingInputCall(clientRequests.flow) },
                    async { testService.fullDuplexCall(bidiRequests.flow).toList() },
                )

                awaitServerEvent(EventType.CALL_ACCEPTED, occurrence = 4U)
                clientRequests.awaitCollectionStarted()
                bidiRequests.awaitCollectionStarted()
                shutdownDataClientNow()

                calls.forEach { call ->
                    assertNotNull(runCatching { call.await() }.exceptionOrNull())
                }
                clientRequests.awaitCancellation()
                bidiRequests.awaitCancellation()
                awaitServerEvent(EventType.CLIENT_CANCELLED, occurrence = 4U)
                releaseServerBarrier(BarrierType.SEND_INITIAL_HEADERS)

                val trace = serverTrace()
                assertEquals(4, trace.events.count { it.type == EventType.CALL_ACCEPTED })
                assertEquals(4, trace.events.count { it.type == EventType.CLIENT_CANCELLED })
                assertEquals(0, trace.events.count { it.type == EventType.CALL_CLOSED })
            }
        } finally {
            val outstanding = serverDiagnostics().outstandingBarriers
            if (outstanding.any { it.type == BarrierType.SEND_INITIAL_HEADERS }) {
                releaseServerBarrier(BarrierType.SEND_INITIAL_HEADERS)
            }
        }
    }

    private companion object {
        fun cancellationInterceptor(callbackEvents: MutableList<String>): GrpcClientInterceptor {
            return object : GrpcClientInterceptor {
                override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                    request: Flow<Request>,
                ): Flow<Response> {
                    onHeaders { callbackEvents += "headers" }
                    onClose { status, _ -> callbackEvents += "close:${status.statusCode.name}" }
                    return proceed(request)
                }
            }
        }

        fun assertCancellationCallbacks(
            callbackEvents: List<String>,
            headersExpected: Boolean,
        ) {
            val expectedPrefix = if (headersExpected) listOf("headers") else emptyList()
            assertTrue(
                callbackEvents == expectedPrefix || callbackEvents == expectedPrefix + "close:CANCELLED",
                "unexpected cancellation callbacks: $callbackEvents",
            )
        }

        fun streamingRequest(vararg responseSizes: Int): StreamingOutputCallRequest {
            return StreamingOutputCallRequest {
                responseParameters = responseSizes.map { size -> ResponseParameters { this.size = size } }
            }
        }

        fun hangingClientStreamingRequests(): Flow<StreamingInputCallRequest> = flow {
            emit(StreamingInputCallRequest {})
            awaitCancellation()
        }

        fun hangingBidirectionalRequests(): Flow<StreamingOutputCallRequest> = flow {
            emit(streamingRequest(23))
            awaitCancellation()
        }

        fun cancelledUnaryBeforeHeadersEvents(): List<ExpectedServerEvent> = listOf(
            ExpectedServerEvent(EventType.CALL_ACCEPTED),
            ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
            ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
            ExpectedServerEvent(EventType.CLIENT_CANCELLED),
        )

        fun cancelledServerStreamingEvents(responseCount: Int): List<ExpectedServerEvent> = buildList {
            add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
            add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED))
            add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
            add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
            repeat(responseCount) { index ->
                add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT, (index + 1).toUInt()))
            }
            add(ExpectedServerEvent(EventType.CLIENT_CANCELLED))
        }

        fun cancelledClientStreamingEvents(
            requestCount: Int,
            clientHalfClosed: Boolean,
        ): List<ExpectedServerEvent> = buildList {
            add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
            repeat(requestCount) { index ->
                add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, (index + 1).toUInt()))
            }
            if (clientHalfClosed) add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
            add(ExpectedServerEvent(EventType.CLIENT_CANCELLED))
        }

        fun cancelledBidirectionalEvents(
            responseCount: Int,
            clientHalfClosed: Boolean,
        ): List<ExpectedServerEvent> = buildList {
            add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
            add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED))
            add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
            repeat(responseCount) { index ->
                add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT, (index + 1).toUInt()))
            }
            if (clientHalfClosed) add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
            add(ExpectedServerEvent(EventType.CLIENT_CANCELLED))
        }
    }
}
