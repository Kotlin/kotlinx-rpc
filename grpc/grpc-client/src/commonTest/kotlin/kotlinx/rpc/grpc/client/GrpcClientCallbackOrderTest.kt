/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.assertGrpcStatusCode
import kotlinx.rpc.grpc.client.testing.successfulServerStreamingEvents
import kotlinx.rpc.grpc.client.testing.successfulUnaryEvents
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.statusCode
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.MalformedResponseCardinality
import kxrpc.testing.MalformedResponseRequest
import kxrpc.testing.MetadataEntry
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Locks in the [ClientCall.Listener] contract as observed through interceptors on every platform:
 * `onHeaders` is delivered at most once and always before the first message, and `onClose` is terminal.
 */
class GrpcClientCallbackOrderTest {
    @Test
    fun okWithoutCustomInitialMetadataStillDeliversHeaders() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(orderObserver(callbackEvents)) },
        ) {
            testService.emptyCall(Empty {})

            // Even without server-configured headers, a successful call must still deliver onHeaders,
            // and it must arrive before the single unary response message.
            assertEquals(listOf("headers", "message", "close:OK"), callbackEvents)
            assertServerTrace(successfulUnaryEvents())
        }
    }

    @Test
    fun okWithoutMessageStillDeliversHeadersBeforeClose() {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(orderObserver(callbackEvents)) },
            scenario = { malformedResponseCardinality = MalformedResponseCardinality.OMIT_RESPONSE },
        ) {
            // The raw server closes with OK after sending empty initial metadata but no message.
            // Unary cardinality validation subsequently converts the missing response to INTERNAL.
            assertGrpcStatusCode(GrpcStatusCode.INTERNAL) {
                malformedResponseService.unaryCall(MalformedResponseRequest {})
            }

            assertEquals(listOf("headers", "close:OK"), callbackEvents)
        }
    }

    @Test
    fun serverStreamingHeadersPrecedeEveryMessage() {
        assertServerStreamingOrder(
            scenario = { initialMetadata = listOf(metadataEntry(ORDER_KEY, "initial")) },
            expectedHeadersEvent = "headers:initial",
        )
    }

    @Test
    fun serverStreamingHeadersPrecedeEveryMessageWithoutCustomInitialMetadata() {
        assertServerStreamingOrder(
            scenario = {},
            expectedHeadersEvent = "headers",
        )
    }

    private fun assertServerStreamingOrder(
        scenario: ConfigureScenarioRequest.Builder.() -> Unit,
        expectedHeadersEvent: String,
    ) {
        val callbackEvents = mutableListOf<String>()
        grpcClientTest(
            clientConfig = { intercept(orderObserver(callbackEvents)) },
            scenario = scenario,
        ) {
            val responses = testService.streamingOutputCall(streamingRequest(RESPONSE_SIZES)).toList()

            assertEquals(RESPONSE_SIZES, responses.map { it.payload.body.size })
            assertEquals(
                listOf(expectedHeadersEvent) + List(RESPONSE_SIZES.size) { "message" } + "close:OK",
                callbackEvents,
            )
            assertServerTrace(successfulServerStreamingEvents(RESPONSE_SIZES.size))
        }
    }

    private fun orderObserver(callbackEvents: MutableList<String>): GrpcClientInterceptor =
        object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                onHeaders { headers ->
                    val marker = headers.getAll(ORDER_KEY).singleOrNull()
                    callbackEvents += if (marker == null) "headers" else "headers:$marker"
                }
                onClose { status, _ -> callbackEvents += "close:${status.statusCode.name}" }
                return proceed(request).onEach { callbackEvents += "message" }
            }
        }

    private companion object {
        const val ORDER_KEY: String = "x-callback-order"
        val RESPONSE_SIZES: List<Int> = listOf(3, 5, 7)

        fun metadataEntry(key: String, value: String): MetadataEntry = MetadataEntry {
            this.key = key
            this.value = ByteString(*value.encodeToByteArray())
        }

        fun streamingRequest(responseSizes: List<Int>): StreamingOutputCallRequest {
            return StreamingOutputCallRequest {
                responseType = PayloadType.COMPRESSABLE
                responseParameters = responseSizes.map { size ->
                    ResponseParameters { this.size = size }
                }
            }
        }
    }
}
