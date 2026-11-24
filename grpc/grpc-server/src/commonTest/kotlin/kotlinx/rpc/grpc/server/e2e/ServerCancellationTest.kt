/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingInputCallResponse
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.zeros
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerCancellationTest {
    @Test
    fun cancelBeforeTheFirstRequestCancelsTheService() = grpcServerTest {
        val requestGate = registerNewGate("first request")
        val started = CompletableDeferred<Unit>()
        val cancelled = CompletableDeferred<Unit>()
        start {
            streamingInputCall = { requests ->
                try {
                    started.complete(Unit)
                    requests.collect { marks.add("request") }
                    StreamingInputCallResponse {}
                } catch (error: CancellationException) {
                    cancelled.complete(Unit)
                    throw error
                }
            }
        }

        coroutineScope {
            val response = async {
                testService.streamingInputCall(flow {
                    requestGate.pass()
                    emit(StreamingInputCallRequest {})
                })
            }
            withTimeout(AWAIT_TIMEOUT) { started.await() }
            requestGate.awaitReached(AWAIT_TIMEOUT)
            response.cancelAndJoin()
        }

        withTimeout(AWAIT_TIMEOUT) { cancelled.await() }
        assertEquals(emptyList(), marks.events)
        val close = observer.awaitCall(TestServiceMethods.STREAMING_INPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.CANCELLED, close.status.statusCode)
    }

    @Test
    fun clientCancelBetweenResponsesCancelsTheService() = grpcServerTest {
        val secondResponse = registerNewGate("second response")
        val cancelled = CompletableDeferred<Unit>()
        start {
            streamingOutputCall = {
                flow {
                    try {
                        emit(StreamingOutputCallResponse { payload = zeros(1) })
                        secondResponse.pass()
                        emit(StreamingOutputCallResponse { payload = zeros(2) })
                    } catch (error: CancellationException) {
                        cancelled.complete(Unit)
                        throw error
                    }
                }
            }
        }

        val responses = testService.streamingOutputCall(StreamingOutputCallRequest {}).take(1).toList()

        assertEquals(listOf(zeros(1)), responses.map { it.payload })
        withTimeout(AWAIT_TIMEOUT) { cancelled.await() }
        val close = observer.awaitCall(TestServiceMethods.STREAMING_OUTPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.CANCELLED, close.status.statusCode)
    }

    @Test
    fun cancelDuringClientStreamingCancelsTheService() = grpcServerTest {
        val requestGate = registerNewGate("request collection")
        val cancelled = CompletableDeferred<Unit>()
        start {
            streamingInputCall = { requests ->
                try {
                    requests.collect {
                        marks.add("request")
                        requestGate.pass()
                    }
                    StreamingInputCallResponse {}
                } catch (error: CancellationException) {
                    cancelled.complete(Unit)
                    throw error
                }
            }
        }

        coroutineScope {
            val response = async {
                testService.streamingInputCall(flow {
                    emit(StreamingInputCallRequest { payload = zeros(1) })
                    awaitCancellation()
                })
            }
            requestGate.awaitReached(AWAIT_TIMEOUT)
            response.cancelAndJoin()
        }

        withTimeout(AWAIT_TIMEOUT) { cancelled.await() }
        assertEquals(listOf("request"), marks.events)
        val close = observer.awaitCall(TestServiceMethods.STREAMING_INPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.CANCELLED, close.status.statusCode)
    }
}
