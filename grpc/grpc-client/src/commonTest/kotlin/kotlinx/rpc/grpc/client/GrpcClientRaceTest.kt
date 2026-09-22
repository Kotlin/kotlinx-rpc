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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.supervisorScope
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.client.testing.ExpectedServerEvent
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kxrpc.testing.GrpcStatus as ScenarioGrpcStatus
import kxrpc.testing.TerminalBehavior
import kxrpc.testing.TerminalStage
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Deterministically exercises both orderings of client lifecycle races. */
class GrpcClientRaceTest {
    @Test
    fun cancellationWinsBeforeHeaders() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_INITIAL_HEADERS)),
    ) {
        try {
            supervisorScope {
                val call = async { testService.emptyCall(Empty {}) }
                awaitServerEvent(EventType.CLIENT_HALF_CLOSED)

                call.cancelAndJoin()

                assertTrue(call.isCancelled)
                assertServerCancelledTrace(unaryEventsBeforeHeaders() + clientCancelled())
            }
        } finally {
            releaseServerBarrier(BarrierType.SEND_INITIAL_HEADERS)
        }
    }

    @Test
    fun headersWinBeforeCancellation() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE)),
    ) {
        try {
            supervisorScope {
                val call = async { testService.streamingOutputCall(streamingRequest(3)).toList() }
                awaitServerEvent(EventType.INITIAL_HEADERS_SENT)

                call.cancelAndJoin()

                assertTrue(call.isCancelled)
                assertServerCancelledTrace(
                    unaryEventsBeforeHeaders() +
                        ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT) +
                        clientCancelled()
                )
            }
        } finally {
            releaseServerBarrier(BarrierType.SEND_RESPONSE)
        }
    }

    @Test
    fun cancellationWinsBeforeResponse() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE)),
    ) {
        try {
            supervisorScope {
                val call = async { testService.streamingOutputCall(streamingRequest(5)).toList() }
                awaitServerEvent(EventType.INITIAL_HEADERS_SENT)

                call.cancelAndJoin()

                assertServerCancelledTrace(
                    unaryEventsBeforeHeaders() +
                        ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT) +
                        clientCancelled()
                )
            }
        } finally {
            releaseServerBarrier(BarrierType.SEND_RESPONSE)
        }
    }

    @Test
    fun responseWinsBeforeCancellation() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
    ) {
        try {
            supervisorScope {
                val responseObserved = CompletableDeferred<Unit>()
                val call = async {
                    testService.streamingOutputCall(streamingRequest(7))
                        .onEach { responseObserved.complete(Unit) }
                        .toList()
                }
                responseObserved.await()

                call.cancelAndJoin()

                assertServerCancelledTrace(serverStreamingEvents(responseCount = 1) + clientCancelled())
            }
        } finally {
            releaseServerBarrier(BarrierType.CLOSE_CALL)
        }
    }

    @Test
    fun cancellationWinsBeforeServerError() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
        scenario = { terminalBehavior = dataLossAfterFirstResponse() },
    ) {
        try {
            supervisorScope {
                val call = async { testService.streamingOutputCall(streamingRequest(11)).toList() }
                awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT)

                call.cancelAndJoin()

                assertServerCancelledTrace(serverStreamingEvents(responseCount = 1) + clientCancelled())
            }
        } finally {
            releaseServerBarrier(BarrierType.CLOSE_CALL)
        }
    }

    @Test
    fun serverErrorWinsBeforeCancellation() {
        val closeObserved = CompletableDeferred<GrpcStatusCode>()
        grpcClientTest(
            clientConfig = {
                intercept(callbackObserver(onClose = { status -> closeObserved.complete(status) }))
            },
            barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
            scenario = { terminalBehavior = dataLossAfterFirstResponse() },
        ) {
            supervisorScope {
                val call = async { testService.streamingOutputCall(streamingRequest(13)).toList() }
                awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT)

                releaseServerBarrier(BarrierType.CLOSE_CALL)
                assertEquals(GrpcStatusCode.DATA_LOSS, closeObserved.await())
                assertNotNull(runCatching { call.await() }.exceptionOrNull())
                call.cancelAndJoin()

                assertServerTrace(serverStreamingEvents(responseCount = 1) + callClosed())
            }
        }
    }

    @Test
    fun cancellationWinsBeforeRequestEof() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.DELIVER_REQUEST_MESSAGE)),
    ) {
        try {
            supervisorScope {
                val requests = RequestFlowProbe(
                    flow {
                        emit(StreamingInputCallRequest {})
                        awaitCancellation()
                    }
                )
                val call = async { testService.streamingInputCall(requests.flow) }
                awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED)

                call.cancelAndJoin()

                requests.awaitCancellation()
                assertServerCancelledTrace(
                    listOf(
                        ExpectedServerEvent(EventType.CALL_ACCEPTED),
                        ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
                        ExpectedServerEvent(EventType.CLIENT_CANCELLED),
                    )
                )
            }
        } finally {
            releaseServerBarrier(BarrierType.DELIVER_REQUEST_MESSAGE)
        }
    }

    @Test
    fun requestEofWinsBeforeCancellation() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE)),
    ) {
        try {
            supervisorScope {
                val requests = RequestFlowProbe(flow { emit(StreamingInputCallRequest {}) })
                val call = async { testService.streamingInputCall(requests.flow) }
                awaitServerEvent(EventType.CLIENT_HALF_CLOSED)

                call.cancelAndJoin()

                assertServerCancelledTrace(
                    listOf(
                        ExpectedServerEvent(EventType.CALL_ACCEPTED),
                        ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
                        ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
                        ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT),
                        ExpectedServerEvent(EventType.CLIENT_CANCELLED),
                    )
                )
            }
        } finally {
            releaseServerBarrier(BarrierType.SEND_RESPONSE)
        }
    }

    @Test
    fun collectorCompletionWinsBeforeNextResponse() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
    ) {
        try {
            val responses = testService.streamingOutputCall(streamingRequest(17, 19))
                .take(1)
                .toList()

            assertEquals(1, responses.size)
            assertServerCancelledTrace(serverStreamingEvents(responseCount = 1) + clientCancelled())
        } finally {
            releaseServerBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)
        }
    }

    @Test
    fun nextResponseWinsBeforeCollectorCompletion() {
        val firstResponseObserved = CompletableDeferred<Unit>()
        val releaseCollector = CompletableDeferred<Unit>()
        grpcClientTest(
            barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
        ) {
            try {
                supervisorScope {
                    val call = async {
                        testService.streamingOutputCall(streamingRequest(23, 29))
                            .onEach {
                                firstResponseObserved.complete(Unit)
                                releaseCollector.await()
                            }
                            .take(1)
                            .toList()
                    }
                    firstResponseObserved.await()
                    awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT, occurrence = 2U)

                    releaseCollector.complete(Unit)
                    assertEquals(1, call.await().size)

                    assertServerCancelledTrace(serverStreamingEvents(responseCount = 2) + clientCancelled())
                }
            } finally {
                releaseCollector.complete(Unit)
                releaseServerBarrier(BarrierType.CLOSE_CALL)
            }
        }
    }

    @Test
    fun shutdownWinsBeforeCallStart() {
        val interceptorEntered = CompletableDeferred<Unit>()
        val releaseCallStart = CompletableDeferred<Unit>()
        grpcClientTest(
            clientConfig = { intercept(callStartBarrier(interceptorEntered, releaseCallStart)) },
            configureScenario = false,
        ) {
            supervisorScope {
                val call = async { testService.emptyCall(Empty {}) }
                interceptorEntered.await()

                shutdownDataClientNow()
                releaseCallStart.complete(Unit)

                assertNotNull(runCatching { call.await() }.exceptionOrNull())
            }
        }
    }

    @Test
    fun callStartWinsBeforeShutdown() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_INITIAL_HEADERS)),
    ) {
        try {
            supervisorScope {
                val call = async { testService.emptyCall(Empty {}) }
                awaitServerEvent(EventType.CALL_ACCEPTED)

                shutdownDataClientNow()

                assertNotNull(runCatching { call.await() }.exceptionOrNull())
                assertServerCancelledTrace(unaryEventsBeforeHeaders() + clientCancelled())
            }
        } finally {
            releaseServerBarrier(BarrierType.SEND_INITIAL_HEADERS)
        }
    }

    @Test
    fun shutdownWinsBeforeCallCompletion() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
    ) {
        try {
            supervisorScope {
                val call = async { testService.emptyCall(Empty {}) }
                awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT)

                shutdownDataClientNow()

                assertNotNull(runCatching { call.await() }.exceptionOrNull())
                assertServerCancelledTrace(serverStreamingEvents(responseCount = 1) + clientCancelled())
            }
        } finally {
            releaseServerBarrier(BarrierType.CLOSE_CALL)
        }
    }

    @Test
    fun callCompletionWinsBeforeShutdown() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
    ) {
        supervisorScope {
            val call = async { testService.emptyCall(Empty {}) }
            awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT)

            releaseServerBarrier(BarrierType.CLOSE_CALL)
            assertEquals(Empty {}, call.await())
            shutdownDataClientNow()

            assertServerTrace(serverStreamingEvents(responseCount = 1) + callClosed())
        }
    }

    private companion object {
        fun callbackObserver(
            onHeaders: () -> Unit = {},
            onClose: (GrpcStatusCode) -> Unit = {},
        ): GrpcClientInterceptor = object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                onHeaders { onHeaders() }
                onClose { status, _ -> onClose(status.statusCode) }
                return proceed(request)
            }
        }

        fun callStartBarrier(
            entered: CompletableDeferred<Unit>,
            release: CompletableDeferred<Unit>,
        ): GrpcClientInterceptor = object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = flow {
                entered.complete(Unit)
                release.await()
                emitAll(proceed(request))
            }
        }

        fun streamingRequest(vararg responseSizes: Int): StreamingOutputCallRequest {
            return StreamingOutputCallRequest {
                responseParameters = responseSizes.map { size -> ResponseParameters { this.size = size } }
            }
        }

        fun dataLossAfterFirstResponse(): TerminalBehavior = TerminalBehavior {
            status = ScenarioGrpcStatus {
                code = GrpcStatusCode.DATA_LOSS.value
                description = "race server error"
            }
            stage = TerminalStage.AFTER_RESPONSE_MESSAGES
            responseMessageCount = 1U
        }

        fun unaryEventsBeforeHeaders(): List<ExpectedServerEvent> = listOf(
            ExpectedServerEvent(EventType.CALL_ACCEPTED),
            ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
            ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
        )

        fun serverStreamingEvents(responseCount: Int): List<ExpectedServerEvent> = buildList {
            addAll(unaryEventsBeforeHeaders())
            add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
            repeat(responseCount) { index ->
                add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT, (index + 1).toUInt()))
            }
        }

        fun clientCancelled(): ExpectedServerEvent = ExpectedServerEvent(EventType.CLIENT_CANCELLED)

        fun callClosed(): ExpectedServerEvent = ExpectedServerEvent(EventType.CALL_CLOSED)
    }
}
