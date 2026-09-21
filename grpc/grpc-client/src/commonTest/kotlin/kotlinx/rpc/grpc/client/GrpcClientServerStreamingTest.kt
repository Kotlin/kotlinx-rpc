/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.testing.integration.EchoStatus
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.testing.assertGrpcStatus
import kotlinx.rpc.grpc.client.testing.failedServerStreamingBeforeResponseEvents
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kotlinx.rpc.grpc.client.testing.successfulServerStreamingEvents
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals

class GrpcClientServerStreamingTest {
    @Test
    fun emptyResponseStream() = grpcClientTest {
        val responses = testService.streamingOutputCall(streamingRequest()).toList()

        assertEquals(emptyList(), responses)
        assertServerTrace(successfulServerStreamingEvents(responseCount = 0))
    }

    @Test
    fun singleResponse() = grpcClientTest {
        val responses = testService.streamingOutputCall(streamingRequest(SINGLE_RESPONSE_SIZE)).toList()

        assertResponsePayloads(
            expected = listOf(ByteArray(SINGLE_RESPONSE_SIZE)),
            actual = responses.map { it.payload.body.toByteArray() },
        )
        assertCompressableResponses(responses.map { it.payload.type })
        assertServerTrace(successfulServerStreamingEvents(responseCount = 1))
    }

    @Test
    fun standardInteropServerStreaming() = grpcClientTest {
        val responses = testService.streamingOutputCall(
            streamingRequest(
                responseSizes = STANDARD_INTEROP_RESPONSE_SIZES,
                requestPayloadSize = STANDARD_INTEROP_REQUEST_SIZE,
            )
        ).toList()

        assertResponsePayloads(
            expected = STANDARD_INTEROP_RESPONSE_SIZES.map(::ByteArray),
            actual = responses.map { it.payload.body.toByteArray() },
        )
        assertCompressableResponses(responses.map { it.payload.type })
        assertServerTrace(successfulServerStreamingEvents(STANDARD_INTEROP_RESPONSE_SIZES.size))
    }

    @Test
    fun individualResponseOccurrenceRemainsGated() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.SEND_RESPONSE, occurrence = 2U)),
    ) {
        coroutineScope {
            val responses = async {
                testService.streamingOutputCall(streamingRequest(3, 5, 7)).toList()
            }

            awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT, occurrence = 1U)
            assertServerEventAbsentUntil(
                event = EventType.RESPONSE_MESSAGE_SENT,
                barrier = BarrierType.SEND_RESPONSE,
                occurrence = 2U,
            )

            val completedResponses = responses.await()
            assertResponsePayloads(
                expected = listOf(ByteArray(3), ByteArray(5), ByteArray(7)),
                actual = completedResponses.map { it.payload.body.toByteArray() },
            )
            assertCompressableResponses(completedResponses.map { it.payload.type })
            assertServerTrace(successfulServerStreamingEvents(responseCount = 3))
        }
    }

    @Test
    fun requestedStatusFailsBeforeFirstResponse() = grpcClientTest {
        val expectedMessage = "server-streaming requested failure"

        assertGrpcStatus(GrpcStatusCode.DATA_LOSS, expectedMessage) {
            testService.streamingOutputCall(
                streamingRequest(
                    responseSizes = listOf(13),
                    responseStatus = EchoStatus {
                        code = GrpcStatusCode.DATA_LOSS.value
                        message = expectedMessage
                    },
                )
            ).toList()
        }
        assertServerTrace(failedServerStreamingBeforeResponseEvents())
    }

    private companion object {
        const val SINGLE_RESPONSE_SIZE: Int = 7
        const val STANDARD_INTEROP_REQUEST_SIZE: Int = 27_182
        val STANDARD_INTEROP_RESPONSE_SIZES: List<Int> = listOf(31_415, 9, 2_653, 58_979)

        fun streamingRequest(vararg responseSizes: Int): StreamingOutputCallRequest {
            return streamingRequest(responseSizes.toList())
        }

        fun streamingRequest(
            responseSizes: List<Int>,
            requestPayloadSize: Int = 0,
            responseStatus: EchoStatus? = null,
        ): StreamingOutputCallRequest {
            return StreamingOutputCallRequest {
                responseType = PayloadType.COMPRESSABLE
                responseParameters = responseSizes.map { size ->
                    ResponseParameters { this.size = size }
                }
                if (requestPayloadSize > 0) {
                    payload = Payload {
                        type = PayloadType.COMPRESSABLE
                        body = ByteString(*ByteArray(requestPayloadSize))
                    }
                }
                responseStatus?.let { this.responseStatus = it }
            }
        }

        fun assertCompressableResponses(types: List<PayloadType>) {
            assertEquals(List(types.size) { PayloadType.COMPRESSABLE }, types)
        }
    }
}
