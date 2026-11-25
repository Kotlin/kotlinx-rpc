/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingInputCallResponse
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.asException
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.serverInterceptor
import kotlinx.rpc.grpc.server.testing.zeros
import kotlinx.rpc.grpc.statusCode

class ServerCallKindsTest {
    @Test
    fun unaryCallReturnsTheRequestedPayloadAndClosesWithOk() = grpcServerTest {
        start()

        val response = testService.unaryCall(SimpleRequest { responseSize = 17 })

        assertEquals(zeros(17), response.payload)
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    fun serverStreamingReturnsTheRequestedPayloadsAndClosesWithOk() = grpcServerTest {
        start()
        val request = StreamingOutputCallRequest {
            responseParameters = listOf(ResponseParameters { size = 3 }, ResponseParameters { size = 7 })
        }

        val responses = testService.streamingOutputCall(request).toList()

        assertEquals(listOf(zeros(3), zeros(7)), responses.map { it.payload })
        val close = observer.awaitCall(TestServiceMethods.STREAMING_OUTPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    fun clientStreamingAggregatesThePayloadSizesAndClosesWithOk() = grpcServerTest {
        start()
        val requests = flowOf(
            StreamingInputCallRequest { payload = zeros(3) },
            StreamingInputCallRequest { payload = zeros(7) },
        )

        val response = testService.streamingInputCall(requests)

        assertEquals(10, response.aggregatedPayloadSize)
        val close = observer.awaitCall(TestServiceMethods.STREAMING_INPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    fun fullDuplexRespondsBeforeTheNextRequestAndClosesWithOk() = grpcServerTest {
        start()
        val firstResponse = CompletableDeferred<Unit>()
        val requests = flow {
            emit(StreamingOutputCallRequest { responseParameters = listOf(ResponseParameters { size = 3 }) })
            withTimeout(AWAIT_TIMEOUT) { firstResponse.await() }
            emit(StreamingOutputCallRequest { responseParameters = listOf(ResponseParameters { size = 7 }) })
        }

        val responses = withTimeout(AWAIT_TIMEOUT) {
            testService.fullDuplexCall(requests).onEach { firstResponse.complete(Unit) }.toList()
        }

        assertEquals(listOf(zeros(3), zeros(7)), responses.map { it.payload })
        val close = observer.awaitCall(TestServiceMethods.FULL_DUPLEX_CALL).awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    fun earlyClientStreamingResponseStopsTheRequestProducer() = grpcServerTest {
        val producerStopped = CompletableDeferred<Unit>()
        start {
            streamingInputCall = { requests ->
                val request = requests.first()
                StreamingInputCallResponse { aggregatedPayloadSize = request.payload.body.size }
            }
        }

        val response = withTimeout(AWAIT_TIMEOUT) {
            testService.streamingInputCall(flow {
                try {
                    emit(StreamingInputCallRequest { payload = zeros(7) })
                    awaitCancellation()
                } finally {
                    producerStopped.complete(Unit)
                }
            })
        }

        assertEquals(7, response.aggregatedPayloadSize)
        withTimeout(AWAIT_TIMEOUT) { producerStopped.await() }
        val close = observer.awaitCall(TestServiceMethods.STREAMING_INPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    fun responseBeforeStreamFailureReachesTheClientWithTheFinalStatusAndTrailers() = grpcServerTest {
        val firstResponse = CompletableDeferred<Unit>()
        start(serverConfig = {
            intercept(serverInterceptor { responseTrailers.append("x-scope", "scope trailer") })
        }) {
            streamingOutputCall = {
                flow {
                    emit(StreamingOutputCallResponse { payload = zeros(3) })
                    withTimeout(AWAIT_TIMEOUT) { firstResponse.await() }
                    throw GrpcStatusCode.ABORTED.asException(
                        "Stream aborted",
                        GrpcMetadata { append("x-error", "error trailer") },
                    )
                }
            }
        }
        val responses = mutableListOf<StreamingOutputCallResponse>()

        val error = assertGrpcStatus(GrpcStatusCode.ABORTED) {
            testService.streamingOutputCall(StreamingOutputCallRequest {}).collect {
                responses += it
                firstResponse.complete(Unit)
            }
        }

        assertEquals(listOf(zeros(3)), responses.map { it.payload })
        assertEquals("Stream aborted", error.status.description)
        assertEquals("scope trailer", error.trailers?.get("x-scope"))
        assertEquals("error trailer", error.trailers?.get("x-error"))
        val close = observer.awaitCall(TestServiceMethods.STREAMING_OUTPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.ABORTED, close.status.statusCode)
        assertEquals("scope trailer", close.trailers["x-scope"])
        assertEquals("error trailer", close.trailers["x-error"])
    }

    @Test
    fun emptyResponseStreamClosesWithOk() = grpcServerTest {
        start()

        assertEquals(emptyList(), testService.streamingOutputCall(StreamingOutputCallRequest {}).toList())

        val close = observer.awaitCall(TestServiceMethods.STREAMING_OUTPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.OK, close.status.statusCode)
    }

    @Test
    fun serviceExceptionPreservesItsStatusAndDescription() = grpcServerTest {
        start {
            unaryCall = { throw GrpcStatus(GrpcStatusCode.PERMISSION_DENIED, "Call rejected").asException() }
        }

        val error = assertGrpcStatus(GrpcStatusCode.PERMISSION_DENIED) {
            testService.unaryCall(SimpleRequest {})
        }

        assertEquals("Call rejected", error.status.description)
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.PERMISSION_DENIED, close.status.statusCode)
        assertEquals("Call rejected", close.status.description)
    }
}
