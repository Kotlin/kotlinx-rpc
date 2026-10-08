/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.testing.GrpcClientTestFixture
import kotlinx.rpc.grpc.client.testing.finiteRequestFlow
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class GrpcClientTimeoutTest {
    @Test
    fun unaryCallTimesOutWaitingForInitialHeaders() = timeoutTest(
        barrier = serverBarrier(BarrierType.SEND_INITIAL_HEADERS),
    ) { callbackEvents ->
        assertGrpcTimeoutStatus {
            testService.emptyCall(Empty {})
        }
        assertServerObservedCancellation()
        assertTerminalCallback(callbackEvents, headersExpected = false)
    }

    @Test
    fun unaryCallTimesOutWaitingForResponse() = timeoutTest(
        barrier = serverBarrier(BarrierType.SEND_RESPONSE),
    ) { callbackEvents ->
        assertGrpcTimeoutStatus {
            testService.emptyCall(Empty {})
        }
        assertServerObservedCancellation()
        assertTerminalCallback(callbackEvents, headersExpected = null)
    }

    @Test
    fun serverStreamingTimesOutWaitingForNextResponse() = timeoutTest(
        barrier = serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U),
    ) { callbackEvents ->
        val responseSizes = mutableListOf<Int>()
        val request = StreamingOutputCallRequest {
            responseType = PayloadType.COMPRESSABLE
            responseParameters = listOf(
                ResponseParameters { size = 5 },
                ResponseParameters { size = 5 },
            )
        }
        assertGrpcTimeoutStatus {
            testService.streamingOutputCall(request)
                .onEach { responseSizes += it.payload.body.size }
                .toList()
        }
        assertEquals(listOf(5), responseSizes)
        assertServerObservedCancellation()
        assertTerminalCallback(callbackEvents, headersExpected = true)
    }

    @Test
    fun serverStreamingTimesOutWhileCollectorIsSlow() = timeoutTest(
        barrier = serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 3U),
    ) { callbackEvents ->
        val responseSizes = mutableListOf<Int>()
        val request = StreamingOutputCallRequest {
            responseType = PayloadType.COMPRESSABLE
            responseParameters = List(3) { ResponseParameters { size = 5 } }
        }
        assertGrpcTimeoutStatus {
            testService.streamingOutputCall(request)
                .onEach {
                    responseSizes += it.payload.body.size
                    if (responseSizes.size == 1) {
                        // Stall the collector until the second response was sent and the
                        // deadline expired, so the client holds an undelivered response.
                        awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT, occurrence = 2U)
                        awaitServerEvent(EventType.CLIENT_CANCELLED)
                    }
                }
                .toList()
        }
        assertEquals(5, responseSizes.first())
        assertServerObservedCancellation()
        assertTerminalCallback(callbackEvents, headersExpected = true)
    }

    @Test
    fun clientStreamingTimesOutWaitingForResponse() = timeoutTest(
        barrier = serverBarrier(BarrierType.SEND_RESPONSE),
    ) { callbackEvents ->
        val requests = finiteRequestFlow(listOf(StreamingInputCallRequest {}))
        assertGrpcTimeoutStatus {
            testService.streamingInputCall(requests)
        }
        assertServerObservedCancellation()
        assertTerminalCallback(callbackEvents, headersExpected = null)
    }

    @Test
    fun bidirectionalStreamingTimesOutWaitingForNextResponse() = timeoutTest(
        barrier = serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U),
    ) { callbackEvents ->
        val responseSizes = mutableListOf<Int>()
        val requests = listOf(
            StreamingOutputCallRequest {
                responseParameters = listOf(ResponseParameters { size = 5 })
            },
            StreamingOutputCallRequest {
                responseParameters = listOf(ResponseParameters { size = 5 })
            },
        ).asFlow()
        assertGrpcTimeoutStatus {
            testService.fullDuplexCall(requests)
                .onEach { responseSizes += it.payload.body.size }
                .toList()
        }
        assertEquals(listOf(5), responseSizes)
        assertServerObservedCancellation()
        assertTerminalCallback(callbackEvents, headersExpected = true)
    }

    @Test
    fun clientStreamingTimesOutDuringBlockedRequestFlow() = timeoutTest(
        barrier = serverBarrier(BarrierType.SEND_INITIAL_HEADERS),
    ) { callbackEvents ->
        var requestFinallyExecuted = false
        val hangingRequestFlow: Flow<StreamingInputCallRequest> = flow {
            try {
                emit(StreamingInputCallRequest {})
                awaitCancellation()
            } finally {
                requestFinallyExecuted = true
            }
        }
        assertGrpcTimeoutStatus {
            testService.streamingInputCall(hangingRequestFlow)
        }
        assertTrue(requestFinallyExecuted, "deadline did not cancel the blocked request producer")
        assertServerObservedCancellation()
        assertTerminalCallback(callbackEvents, headersExpected = false)
    }

    @Test
    fun unaryTimesOutDuringBlockedRequestFlow(): TestResult {
        var requestFinallyExecuted = false
        val hangingRequestFlow: Flow<Nothing> = flow {
            try {
                awaitCancellation()
            } finally {
                requestFinallyExecuted = true
            }
        }
        return timeoutTest(
            clientConfig = { intercept(replacingRequestInterceptor(hangingRequestFlow)) },
        ) { callbackEvents ->
            // The call starts before its request is ready, so the deadline also bounds the request.
            assertGrpcTimeoutStatus {
                testService.emptyCall(Empty {})
            }
            assertTrue(requestFinallyExecuted, "deadline did not cancel the blocked request producer")
            val trace = assertServerObservedCancellation()
            assertEquals(0, trace.events.count { it.type == EventType.REQUEST_MESSAGE_RECEIVED })
            assertTerminalCallback(callbackEvents, headersExpected = false)
        }
    }

    private companion object {
        val CALL_TIMEOUT: Duration = 1500.milliseconds

        /**
         * Runs [block] with a client whose calls time out after [CALL_TIMEOUT] and record their
         * `onHeaders`/`onClose` callbacks in the list passed to [block].
         *
         * [barrier] holds the server where the call is expected to time out. It is released after
         * [block], so that the cancelled call's scenario can finish.
         */
        fun timeoutTest(
            barrier: Barrier? = null,
            clientConfig: GrpcClientConfiguration.() -> Unit = {},
            block: suspend GrpcClientTestFixture.(callbackEvents: List<String>) -> Unit,
        ): TestResult {
            val callbackEvents = mutableListOf<String>()
            return grpcClientTest(
                clientConfig = {
                    intercept(timeoutInterceptor(callbackEvents))
                    clientConfig()
                },
                barriers = listOfNotNull(barrier),
            ) {
                try {
                    block(callbackEvents)
                } finally {
                    if (barrier != null) releaseServerBarrier(barrier.type, barrier.occurrence)
                }
            }
        }

        suspend fun assertGrpcTimeoutStatus(block: suspend () -> Unit): GrpcStatusException {
            val failure = assertFailsWith<GrpcStatusException> { block() }
            assertEquals(
                GrpcStatusCode.DEADLINE_EXCEEDED,
                failure.status.statusCode,
                "unexpected timeout failure: $failure",
            )
            return failure
        }

        /** `headersExpected = null` accepts both outcomes, for timeouts that race with the server sending headers. */
        fun assertTerminalCallback(
            callbackEvents: List<String>,
            headersExpected: Boolean?,
        ) {
            val closeOnly = listOf("close:DEADLINE_EXCEEDED")
            val headersThenClose = listOf("headers", "close:DEADLINE_EXCEEDED")
            when (headersExpected) {
                true -> assertEquals(headersThenClose, callbackEvents)
                false -> assertEquals(closeOnly, callbackEvents)
                null -> assertTrue(callbackEvents == closeOnly || callbackEvents == headersThenClose)
            }
        }

        fun replacingRequestInterceptor(requests: Flow<Nothing>): GrpcClientInterceptor =
            object : GrpcClientInterceptor {
                override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                    request: Flow<Request>,
                ): Flow<Response> = proceed(requests)
            }

        fun timeoutInterceptor(callbackEvents: MutableList<String>): GrpcClientInterceptor =
            object : GrpcClientInterceptor {
                override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                    request: Flow<Request>,
                ): Flow<Response> {
                    callOptions.timeout = CALL_TIMEOUT
                    onHeaders { callbackEvents += "headers" }
                    onClose { status, _ -> callbackEvents += "close:${status.statusCode.name}" }
                    return proceed(request)
                }
            }
    }
}
