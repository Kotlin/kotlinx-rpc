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
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.getAllBinary
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.trailers
import kotlinx.rpc.grpc.client.testing.ExpectedServerEvent
import kotlinx.rpc.grpc.client.testing.assertGrpcStatus
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.successfulUnaryEvents
import kxrpc.testing.EventType
import kxrpc.testing.GrpcStatus as ScenarioGrpcStatus
import kxrpc.testing.MetadataEntry
import kxrpc.testing.TerminalBehavior
import kxrpc.testing.TerminalStage
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class GrpcClientMetadataAndStatusTest {
    @Test
    fun metadataAndCallbacksPreserveWireSemantics() {
        val initialBinary = byteArrayOf(0, 1, -1, 42)
        val trailingBinary = ByteArray(1_024) { index -> index.toByte() }
        val requestBinary = byteArrayOf(-1, 0, 1, 127)
        val callback = CallbackRecorder()

        grpcClientTest(
            clientConfig = { intercept(metadataInterceptor(callback, requestBinary)) },
            scenario = {
                // Make one call exercise duplicate/empty ASCII values, binary values, and a
                // non-OK close while keeping initial headers and trailers distinguishable.
                initialMetadata = listOf(
                    metadataEntry(RESPONSE_ASCII_KEY, "first"),
                    metadataEntry(RESPONSE_ASCII_KEY, ""),
                    metadataEntry(RESPONSE_BINARY_KEY, initialBinary),
                )
                trailingMetadata = listOf(
                    metadataEntry(TRAILER_ASCII_KEY, "done"),
                    metadataEntry(TRAILER_ASCII_KEY, ""),
                    metadataEntry(TRAILER_BINARY_KEY, trailingBinary),
                )
                terminalBehavior = terminalBehavior(
                    code = GrpcStatusCode.DATA_LOSS,
                    description = METADATA_FAILURE_DESCRIPTION,
                    stage = TerminalStage.AFTER_SERVICE_COMPLETION,
                )
            },
        ) {
            val failure = assertGrpcStatus(GrpcStatusCode.DATA_LOSS, METADATA_FAILURE_DESCRIPTION) {
                testService.emptyCall(Empty {})
            }

            // Client callbacks must retain wire order and fire exactly once despite the error.
            assertEquals(listOf("headers", "message", "close"), callback.events)
            assertEquals(1, callback.headerCount)
            assertEquals(1, callback.closeCount)
            assertEquals(GrpcStatusCode.DATA_LOSS, callback.status?.statusCode)

            val headers = assertNotNull(callback.headers)
            assertEquals(listOf("first", ""), headers.getAll(RESPONSE_ASCII_KEY.uppercase()))
            assertContentEquals(initialBinary, headers.getAllBinary(RESPONSE_BINARY_KEY).single())
            assertFalse(headers.keys().any { it.startsWith(':') }, "HTTP/2 pseudo-header escaped into metadata")

            val callbackTrailers = assertNotNull(callback.trailers)
            assertEquals(listOf("done", ""), callbackTrailers.getAll(TRAILER_ASCII_KEY))
            assertContentEquals(trailingBinary, callbackTrailers.getAllBinary(TRAILER_BINARY_KEY).single())
            val exceptionTrailers = assertNotNull(failure.trailers)
            assertEquals(listOf("done", ""), exceptionTrailers.getAll(TRAILER_ASCII_KEY))
            assertContentEquals(trailingBinary, exceptionTrailers.getAllBinary(TRAILER_BINARY_KEY).single())

            val acceptedMetadata = serverTrace().events
                .single { it.type == EventType.CALL_ACCEPTED }
                .metadata
                .filter { it.key.startsWith("x-request-") }
            // Verify what reached the wire: lowercase keys, preserved duplicates/binary data,
            // and the documented ASCII replacement of the non-ASCII request value.
            assertEquals(
                listOf(
                    metadataEntry(REQUEST_ASCII_KEY, "alpha"),
                    metadataEntry(REQUEST_ASCII_KEY, ""),
                    metadataEntry(REQUEST_REPLACED_ASCII_KEY, "caf?"),
                    metadataEntry(REQUEST_BINARY_KEY, requestBinary),
                ),
                acceptedMetadata,
            )
            assertServerTrace(successfulUnaryEvents())
        }
    }

    @Test
    fun nonOkStatusAfterResponsesPreservesMessagesThenFails() {
        val callback = CallbackRecorder()
        grpcClientTest(
            clientConfig = { intercept(recordingInterceptor(callback)) },
            scenario = {
                // Request three responses but terminate after the second has reached the wire.
                trailingMetadata = listOf(metadataEntry(FAILURE_POINT_KEY, "two"))
                terminalBehavior = terminalBehavior(
                    code = GrpcStatusCode.RESOURCE_EXHAUSTED,
                    description = "after two responses",
                    stage = TerminalStage.AFTER_RESPONSE_MESSAGES,
                    responseCount = 2U,
                )
            },
        ) {
            val responses = mutableListOf<Int>()
            val failure = assertGrpcStatus(GrpcStatusCode.RESOURCE_EXHAUSTED, "after two responses") {
                testService.streamingOutputCall(
                    StreamingOutputCallRequest {
                        responseType = PayloadType.COMPRESSABLE
                        responseParameters = listOf(3, 5, 8).map { size ->
                            ResponseParameters { this.size = size }
                        }
                    }
                ).onEach { responses += it.payload.body.size }.toList()
            }

            // Already-delivered messages remain observable before collection fails on close.
            assertEquals(listOf(3, 5), responses)
            assertEquals(listOf("headers", "message", "message", "close"), callback.events)
            assertEquals(1, callback.headerCount)
            assertEquals(1, callback.closeCount)
            assertEquals("two", assertNotNull(failure.trailers).getAll(FAILURE_POINT_KEY).single())
            assertServerTrace(
                listOf(
                    ExpectedServerEvent(EventType.CALL_ACCEPTED),
                    ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
                    ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
                    ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT),
                    ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT),
                    ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT, occurrence = 2U),
                    ExpectedServerEvent(EventType.CALL_CLOSED),
                )
            )
        }
    }

    @Test fun cancelledStatus() = assertTerminalStatus(GrpcStatusCode.CANCELLED)
    @Test fun unknownStatus() = assertTerminalStatus(GrpcStatusCode.UNKNOWN)
    @Test fun invalidArgumentStatus() = assertTerminalStatus(GrpcStatusCode.INVALID_ARGUMENT)
    @Test fun deadlineExceededStatus() = assertTerminalStatus(GrpcStatusCode.DEADLINE_EXCEEDED)
    @Test fun notFoundStatus() = assertTerminalStatus(GrpcStatusCode.NOT_FOUND)
    @Test fun alreadyExistsStatus() = assertTerminalStatus(GrpcStatusCode.ALREADY_EXISTS)
    @Test fun permissionDeniedStatus() = assertTerminalStatus(GrpcStatusCode.PERMISSION_DENIED)
    @Test fun resourceExhaustedStatus() = assertTerminalStatus(GrpcStatusCode.RESOURCE_EXHAUSTED)
    @Test fun failedPreconditionStatus() = assertTerminalStatus(GrpcStatusCode.FAILED_PRECONDITION)
    @Test fun abortedStatus() = assertTerminalStatus(GrpcStatusCode.ABORTED)
    @Test fun outOfRangeStatus() = assertTerminalStatus(GrpcStatusCode.OUT_OF_RANGE)
    @Test fun unimplementedStatus() = assertTerminalStatus(GrpcStatusCode.UNIMPLEMENTED)
    @Test fun internalStatus() = assertTerminalStatus(GrpcStatusCode.INTERNAL)
    @Test fun unavailableStatus() = assertTerminalStatus(GrpcStatusCode.UNAVAILABLE)
    @Test fun dataLossStatus() = assertTerminalStatus(GrpcStatusCode.DATA_LOSS)
    @Test fun unauthenticatedStatus() = assertTerminalStatus(GrpcStatusCode.UNAUTHENTICATED)

    private fun assertTerminalStatus(code: GrpcStatusCode) = grpcClientTest(
        scenario = {
            // Close before headers so every canonical error code is tested as a trailers-only response.
            trailingMetadata = listOf(metadataEntry(STATUS_TRAILER_KEY, code.name))
            terminalBehavior = terminalBehavior(
                code = code,
                description = "configured ${code.name}",
                stage = TerminalStage.BEFORE_INITIAL_METADATA,
            )
        },
    ) {
        val failure = assertGrpcStatus(code, "configured ${code.name}") {
            testService.emptyCall(Empty {})
        }
        assertEquals(code.name, assertNotNull(failure.trailers).getAll(STATUS_TRAILER_KEY).single())
        val trace = serverTrace()
        assertEquals(
            listOf(EventType.CALL_ACCEPTED, EventType.CALL_CLOSED),
            trace.events.map { it.type },
        )
        val closed = trace.events.last()
        assertEquals(code.value, closed.status.code)
        assertEquals("configured ${code.name}", closed.status.description)
    }

    private companion object {
        const val REQUEST_ASCII_KEY: String = "x-request-value"
        const val REQUEST_REPLACED_ASCII_KEY: String = "x-request-replaced"
        const val REQUEST_BINARY_KEY: String = "x-request-bin"
        const val RESPONSE_ASCII_KEY: String = "x-response-value"
        const val RESPONSE_BINARY_KEY: String = "x-response-bin"
        const val TRAILER_ASCII_KEY: String = "x-trailer-value"
        const val TRAILER_BINARY_KEY: String = "x-trailer-bin"
        const val FAILURE_POINT_KEY: String = "x-failure-point"
        const val STATUS_TRAILER_KEY: String = "x-status-name"
        const val METADATA_FAILURE_DESCRIPTION: String = "metadata terminal status"

        fun metadataInterceptor(callback: CallbackRecorder, requestBinary: ByteArray): GrpcClientInterceptor {
            return recordingInterceptor(callback) {
                requestHeaders.append(REQUEST_ASCII_KEY.uppercase(), "alpha")
                requestHeaders.append(REQUEST_ASCII_KEY, "")
                requestHeaders.append(REQUEST_REPLACED_ASCII_KEY, "café")
                requestHeaders.appendBinary(REQUEST_BINARY_KEY.uppercase(), requestBinary)
            }
        }

        fun recordingInterceptor(
            callback: CallbackRecorder,
            configure: GrpcClientCallScope<*, *>.() -> Unit = {},
        ): GrpcClientInterceptor = object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                configure()
                onHeaders { headers ->
                    callback.headerCount++
                    callback.headers = headers
                    callback.events += "headers"
                }
                onClose { status, trailers ->
                    callback.closeCount++
                    callback.status = status
                    callback.trailers = trailers
                    callback.events += "close"
                }
                return proceed(request).onEach { callback.events += "message" }
            }
        }

        fun metadataEntry(key: String, value: String): MetadataEntry = metadataEntry(key, value.encodeToByteArray())

        fun metadataEntry(key: String, value: ByteArray): MetadataEntry = MetadataEntry {
            this.key = key
            this.value = ByteString(*value)
        }

        fun terminalBehavior(
            code: GrpcStatusCode,
            description: String,
            stage: TerminalStage,
            responseCount: UInt = 0U,
        ): TerminalBehavior = TerminalBehavior {
            status = ScenarioGrpcStatus {
                this.code = code.value
                this.description = description
            }
            this.stage = stage
            responseMessageCount = responseCount
        }
    }
}

private class CallbackRecorder {
    val events: MutableList<String> = mutableListOf()
    var headerCount: Int = 0
    var closeCount: Int = 0
    var headers: GrpcMetadata? = null
    var status: GrpcStatus? = null
    var trailers: GrpcMetadata? = null
}
