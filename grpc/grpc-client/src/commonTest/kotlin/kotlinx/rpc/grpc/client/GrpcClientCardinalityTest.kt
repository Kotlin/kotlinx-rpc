/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import grpc.testing.Empty
import grpc.testing.invoke
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.client.testing.ExpectedServerEvent
import kotlinx.rpc.grpc.client.testing.RequestFlowCompletion
import kotlinx.rpc.grpc.client.testing.RequestFlowProbe
import kotlinx.rpc.grpc.client.testing.assertGrpcStatusCode
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kxrpc.testing.EventType
import kxrpc.testing.MalformedResponseCardinality
import kxrpc.testing.MalformedResponseRequest
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GrpcClientCardinalityTest {
    @Test
    fun unaryRequestCannotBeOmitted() = malformedUnaryRequestTest(omitRequest = true)

    @Test
    fun unaryRequestCannotBeDuplicated() = malformedUnaryRequestTest(omitRequest = false)

    @Test
    fun unaryResponseCannotBeOmitted() = malformedUnaryTest(MalformedResponseCardinality.OMIT_RESPONSE)

    @Test
    fun unaryResponseCannotBeDuplicated() = malformedUnaryTest(MalformedResponseCardinality.DUPLICATE_RESPONSE)

    @Test
    fun clientStreamingResponseCannotBeOmitted() = malformedClientStreamingTest(
        MalformedResponseCardinality.OMIT_RESPONSE,
    )

    @Test
    fun clientStreamingResponseCannotBeDuplicated() = malformedClientStreamingTest(
        MalformedResponseCardinality.DUPLICATE_RESPONSE,
    )

    private fun malformedUnaryTest(cardinality: MalformedResponseCardinality) = grpcClientTest(
        scenario = { malformedResponseCardinality = cardinality },
    ) {
        // The raw server handler bypasses grpc-java's unary safeguards so the client sees
        // zero or two responses on a descriptor that requires exactly one.
        assertGrpcStatusCode(GrpcStatusCode.INTERNAL) {
            malformedResponseService.unaryCall(request(UNARY_PAYLOAD))
        }
        assertServerTrace(expectedMalformedResponseEvents(cardinality, requestCount = 1))
    }

    private fun malformedUnaryRequestTest(omitRequest: Boolean) = grpcClientTest(
        clientConfig = { intercept(requestCardinalityInterceptor(omitRequest)) },
        configureScenario = false,
    ) {
        // Common client validation must reject zero or two unary requests before any RPC starts.
        val failure = assertFailsWith<GrpcStatusException> {
            testService.emptyCall(Empty {})
        }
        assertEquals(GrpcStatusCode.INTERNAL, failure.status.statusCode)
    }

    private fun malformedClientStreamingTest(cardinality: MalformedResponseCardinality) = grpcClientTest(
        scenario = { malformedResponseCardinality = cardinality },
    ) {
        val requests = RequestFlowProbe(flowOf(request(FIRST_PAYLOAD), request(SECOND_PAYLOAD)))

        // Invalid response cardinality must not strand the completed streaming request producer.
        assertGrpcStatusCode(GrpcStatusCode.INTERNAL) {
            malformedResponseService.streamingInputCall(requests.flow)
        }

        assertEquals(RequestFlowCompletion.Completed, requests.awaitCompletion())
        assertServerTrace(expectedMalformedResponseEvents(cardinality, requestCount = 2))
    }

    private companion object {
        val UNARY_PAYLOAD: ByteArray = byteArrayOf(1, 2, 3)
        val FIRST_PAYLOAD: ByteArray = byteArrayOf(5, 8)
        val SECOND_PAYLOAD: ByteArray = byteArrayOf(13, 21, 34)

        fun request(payload: ByteArray): MalformedResponseRequest = MalformedResponseRequest {
            this.payload = ByteString(*payload)
        }

        fun requestCardinalityInterceptor(omitRequest: Boolean): GrpcClientInterceptor {
            return object : GrpcClientInterceptor {
                override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                    request: Flow<Request>,
                ): Flow<Response> {
                    val malformedRequest = if (omitRequest) {
                        emptyFlow()
                    } else {
                        flow {
                            request.collect { message ->
                                emit(message)
                                emit(message)
                            }
                        }
                    }
                    return proceed(malformedRequest)
                }
            }
        }

        fun expectedMalformedResponseEvents(
            cardinality: MalformedResponseCardinality,
            requestCount: Int,
        ): List<ExpectedServerEvent> = buildList {
            add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
            repeat(requestCount) { index ->
                add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, occurrence = (index + 1).toUInt()))
            }
            add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
            add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
            if (cardinality == MalformedResponseCardinality.DUPLICATE_RESPONSE) {
                add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT))
                add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT, occurrence = 2U))
            }
            add(ExpectedServerEvent(EventType.CALL_CLOSED))
        }
    }
}
