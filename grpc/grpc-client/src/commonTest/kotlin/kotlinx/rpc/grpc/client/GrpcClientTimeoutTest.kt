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
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.testing.finiteRequestFlow
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kxrpc.testing.BarrierType
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class GrpcClientTimeoutTest {
    @Test
    fun unaryCallTimesOutWaitingForInitialHeaders() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = {
                intercept(timeoutInterceptor(1500.milliseconds, callbackEvents))
            },
            barriers = listOf(serverBarrier(BarrierType.SEND_INITIAL_HEADERS)),
        ) {
            try {
                assertGrpcTimeoutStatus {
                    testService.emptyCall(Empty {})
                }
                assertTerminalCallback(callbackEvents, headersExpected = false)
            } finally {
                releaseServerBarrier(BarrierType.SEND_INITIAL_HEADERS)
            }
        }
    }

    @Test
    fun unaryCallTimesOutWaitingForResponse() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = {
                intercept(timeoutInterceptor(1500.milliseconds, callbackEvents))
            },
            barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE)),
        ) {
            try {
                assertGrpcTimeoutStatus {
                    testService.emptyCall(Empty {})
                }
                assertTerminalCallback(callbackEvents, headersExpected = null)
            } finally {
                releaseServerBarrier(BarrierType.SEND_RESPONSE)
            }
        }
    }

    @Test
    fun serverStreamingTimesOutWaitingForNextResponse() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = {
                intercept(timeoutInterceptor(1500.milliseconds, callbackEvents))
            },
            barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
        ) {
            try {
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
                assertTerminalCallback(callbackEvents, headersExpected = true)
            } finally {
                releaseServerBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)
            }
        }
    }

    @Test
    fun clientStreamingTimesOutWaitingForResponse() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = {
                intercept(timeoutInterceptor(1500.milliseconds, callbackEvents))
            },
            barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE)),
        ) {
            try {
                val requests = finiteRequestFlow(listOf(StreamingInputCallRequest {}))
                assertGrpcTimeoutStatus {
                    testService.streamingInputCall(requests)
                }
                assertTerminalCallback(callbackEvents, headersExpected = null)
            } finally {
                releaseServerBarrier(BarrierType.SEND_RESPONSE)
            }
        }
    }

    @Test
    fun bidirectionalStreamingTimesOutWaitingForNextResponse() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = {
                intercept(timeoutInterceptor(1500.milliseconds, callbackEvents))
            },
            barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
        ) {
            try {
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
                assertTerminalCallback(callbackEvents, headersExpected = true)
            } finally {
                releaseServerBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)
            }
        }
    }

    @Test
    fun clientStreamingTimesOutDuringBlockedRequestFlow() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = {
                intercept(timeoutInterceptor(1500.milliseconds, callbackEvents))
            },
            barriers = listOf(serverBarrier(BarrierType.SEND_INITIAL_HEADERS)),
        ) {
            try {
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
                assertTerminalCallback(callbackEvents, headersExpected = false)
            } finally {
                releaseServerBarrier(BarrierType.SEND_INITIAL_HEADERS)
            }
        }
    }

    private companion object {
        suspend fun assertGrpcTimeoutStatus(block: suspend () -> Unit): GrpcStatusException {
            val failure = assertFailsWith<GrpcStatusException> { block() }
            assertEquals(
                GrpcStatusCode.DEADLINE_EXCEEDED,
                failure.status.statusCode,
                "unexpected timeout failure: $failure",
            )
            return failure
        }

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

        fun timeoutInterceptor(
            timeout: kotlin.time.Duration,
            callbackEvents: MutableList<String>,
        ): GrpcClientInterceptor = object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                callOptions.timeout = timeout
                onHeaders { callbackEvents += "headers" }
                onClose { status, _ -> callbackEvents += "close:${status.statusCode.name}" }
                return proceed(request)
            }
        }
    }
}
