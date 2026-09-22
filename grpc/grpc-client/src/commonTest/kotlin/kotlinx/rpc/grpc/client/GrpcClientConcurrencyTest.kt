/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.supervisorScope
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GrpcClientConcurrencyTest {
    @Test
    fun manyUnaryCallsKeepResponsesAndTerminalCallbacksIsolated() {
        val closeEvents = Channel<GrpcStatusCode>(Channel.UNLIMITED)
        grpcClientTest(
            clientConfig = { intercept(terminalObserver(closeEvents)) },
        ) {
            val responses = supervisorScope {
                List(UNARY_CALL_COUNT) { index ->
                    async {
                        testService.unaryCall(
                            SimpleRequest {
                                responseSize = index + 1
                                payload = payload(index + 1)
                            }
                        )
                    }
                }.awaitAll()
            }

            assertEquals(
                (1..UNARY_CALL_COUNT).toList(),
                responses.map { it.payload.body.size },
            )
            repeat(UNARY_CALL_COUNT) {
                assertEquals(GrpcStatusCode.OK, closeEvents.receive())
            }
            assertTrue(closeEvents.tryReceive().isFailure, "a unary call emitted a duplicate close callback")

            val trace = serverTrace()
            assertEquals(UNARY_CALL_COUNT, trace.events.count { it.type == EventType.CALL_ACCEPTED })
            assertEquals(UNARY_CALL_COUNT, trace.events.count { it.type == EventType.CALL_CLOSED })
            assertEquals(0, trace.events.count { it.type == EventType.CLIENT_CANCELLED })
        }
    }

    @Test
    fun mixedRpcShapesShareOneChannelWithoutCrossCallContamination() = grpcClientTest {
        supervisorScope {
            val unary = async {
                testService.unaryCall(SimpleRequest { responseSize = 11 })
            }
            val serverStreaming = async {
                testService.streamingOutputCall(streamingOutputRequest(13, 17)).toList()
            }
            val clientStreaming = async {
                testService.streamingInputCall(
                    flowOf(streamingInputRequest(19), streamingInputRequest(23))
                )
            }
            val bidirectional = async {
                testService.fullDuplexCall(
                    flowOf(streamingOutputRequest(29), streamingOutputRequest(31))
                ).toList()
            }

            assertEquals(11, unary.await().payload.body.size)
            assertEquals(listOf(13, 17), serverStreaming.await().map { it.payload.body.size })
            assertEquals(42, clientStreaming.await().aggregatedPayloadSize)
            assertEquals(listOf(29, 31), bidirectional.await().map { it.payload.body.size })
        }

        val trace = serverTrace()
        assertEquals(4, trace.events.count { it.type == EventType.CALL_ACCEPTED })
        assertEquals(4, trace.events.count { it.type == EventType.CALL_CLOSED })
        assertEquals(0, trace.events.count { it.type == EventType.CLIENT_CANCELLED })
        assertEquals(6, trace.events.count { it.type == EventType.REQUEST_MESSAGE_RECEIVED })
        assertEquals(6, trace.events.count { it.type == EventType.RESPONSE_MESSAGE_SENT })
    }

    @Test
    fun cancellingOneCallDoesNotPoisonConcurrentSuccessOrFailure() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE)),
    ) {
        var barrierReleased = false
        try {
            supervisorScope {
                val cancelled = async {
                    testService.streamingOutputCall(streamingOutputRequest(37)).toList()
                }
                awaitServerEvent(EventType.INITIAL_HEADERS_SENT)

                val successful = async { testService.emptyCall(Empty {}) }
                val failed = async {
                    runCatching { unimplementedService.unimplementedCall(Empty {}) }.exceptionOrNull()
                }
                awaitServerEvent(EventType.CALL_ACCEPTED, occurrence = 2U)

                cancelled.cancelAndJoin()
                awaitServerEvent(EventType.CLIENT_CANCELLED)

                releaseServerBarrier(BarrierType.SEND_RESPONSE)
                barrierReleased = true
                assertEquals(Empty {}, successful.await())
                assertEquals(
                    GrpcStatusCode.UNIMPLEMENTED,
                    assertIs<GrpcStatusException>(failed.await()).status.statusCode,
                )
            }

            val trace = serverTrace()
            assertEquals(2, trace.events.count { it.type == EventType.CALL_ACCEPTED })
            assertEquals(1, trace.events.count { it.type == EventType.CLIENT_CANCELLED })
            assertEquals(1, trace.events.count { it.type == EventType.CALL_CLOSED })
            assertEquals(1, trace.events.count { it.type == EventType.RESPONSE_MESSAGE_SENT })
        } finally {
            if (!barrierReleased) releaseServerBarrier(BarrierType.SEND_RESPONSE)
        }
    }

    @Test
    fun connectionLossTerminatesEveryConcurrentCallExactlyOnce() {
        val closeEvents = Channel<GrpcStatusCode>(Channel.UNLIMITED)
        grpcClientTest(
            clientConfig = { intercept(terminalObserver(closeEvents)) },
        ) {
            val disposableService = startDisposableTestService()
            supervisorScope {
                val requestProbes = List(CONNECTION_LOSS_CALL_COUNT) {
                    RequestFlowProbe(
                        flow {
                            emit(streamingInputRequest(it + 1))
                            awaitCancellation()
                        }
                    )
                }
                val calls = requestProbes.map { requests ->
                    async {
                        runCatching { disposableService.streamingInputCall(requests.flow) }.exceptionOrNull()
                    }
                }

                awaitServerEvent(EventType.CALL_ACCEPTED, CONNECTION_LOSS_CALL_COUNT.toUInt())
                awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, CONNECTION_LOSS_CALL_COUNT.toUInt())
                stopDisposableEndpoint()

                val terminalStatuses = calls.map { call ->
                    assertIs<GrpcStatusException>(call.await()).status.statusCode
                }
                assertTrue(
                    terminalStatuses.all { it == GrpcStatusCode.UNAVAILABLE },
                    "connection loss did not report UNAVAILABLE: $terminalStatuses",
                )
                assertEquals(
                    1,
                    terminalStatuses.distinct().size,
                    "calls observed inconsistent connection-loss statuses",
                )
                requestProbes.forEach { it.awaitCancellation() }

                repeat(CONNECTION_LOSS_CALL_COUNT) {
                    assertEquals(terminalStatuses.singleStatus(), closeEvents.receive())
                }
            }
            assertTrue(closeEvents.tryReceive().isFailure, "connection loss emitted a duplicate close callback")

            val trace = serverTrace()
            val terminalCount = trace.events.count {
                it.type == EventType.CALL_CLOSED || it.type == EventType.CLIENT_CANCELLED
            }
            assertEquals(CONNECTION_LOSS_CALL_COUNT, trace.events.count { it.type == EventType.CALL_ACCEPTED })
            assertEquals(CONNECTION_LOSS_CALL_COUNT, terminalCount)
            assertEquals(0U, serverDiagnostics().activeCallCount)
        }
    }

    private companion object {
        const val UNARY_CALL_COUNT: Int = 16
        const val CONNECTION_LOSS_CALL_COUNT: Int = 4

        fun terminalObserver(events: Channel<GrpcStatusCode>): GrpcClientInterceptor {
            return object : GrpcClientInterceptor {
                override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                    request: Flow<Request>,
                ): Flow<Response> {
                    onClose { status, _ ->
                        check(events.trySend(status.statusCode).isSuccess) { "could not record close callback" }
                    }
                    return proceed(request)
                }
            }
        }

        fun payload(size: Int): Payload = Payload {
            body = ByteString(*ByteArray(size) { size.toByte() })
        }

        fun streamingInputRequest(size: Int): StreamingInputCallRequest = StreamingInputCallRequest {
            payload = payload(size)
        }

        fun streamingOutputRequest(vararg responseSizes: Int): StreamingOutputCallRequest {
            return StreamingOutputCallRequest {
                responseParameters = responseSizes.map { size -> ResponseParameters { this.size = size } }
            }
        }

        fun List<GrpcStatusCode>.singleStatus(): GrpcStatusCode = distinct().single()
    }
}
