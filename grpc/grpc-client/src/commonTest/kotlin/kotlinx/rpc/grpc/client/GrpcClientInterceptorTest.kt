/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.toList
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kxrpc.testing.EventType
import kxrpc.testing.MetadataEntry
import kxrpc.testing.invoke
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotSame

class GrpcClientInterceptorTest {
    @Test
    fun multipleInterceptorsNestInRegistrationOrder() {
        val events = mutableListOf<String>()
        grpcClientTest(
            clientConfig = {
                // Entry follows registration order, while collection/response/completion unwind
                // through the nested flows like ordinary middleware.
                intercept(
                    interceptor { request ->
                        events += "outer-enter"
                        flow {
                            events += "outer-collect"
                            proceed(request.map { events += "outer-request"; it }).collect {
                                events += "outer-response"
                                emit(it)
                            }
                            events += "outer-complete"
                        }
                    },
                    interceptor { request ->
                        events += "inner-enter"
                        flow {
                            events += "inner-collect"
                            proceed(request.map { events += "inner-request"; it }).collect {
                                events += "inner-response"
                                emit(it)
                            }
                            events += "inner-complete"
                        }
                    },
                )
            },
        ) {
            assertEquals(Empty {}, testService.emptyCall(Empty {}))
            assertEquals(
                listOf(
                    "outer-enter",
                    "outer-collect",
                    "inner-enter",
                    "inner-collect",
                    "outer-request",
                    "inner-request",
                    "inner-response",
                    "outer-response",
                    "inner-complete",
                    "outer-complete",
                ),
                events,
            )
            assertUnaryLifecycle()
        }
    }

    @Test
    fun interceptorTransformsUnaryRequestAndResponse() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { request ->
                val transformedRequest = request.map { message ->
                    if (message is SimpleRequest) {
                        SimpleRequest {
                            responseType = PayloadType.COMPRESSABLE
                            responseSize = TRANSFORMED_SERVER_RESPONSE_SIZE
                        }
                    } else {
                        message
                    }
                }
                proceed(transformedRequest).map { message ->
                    if (message is SimpleResponse) {
                        SimpleResponse {
                            payload = Payload {
                                type = PayloadType.COMPRESSABLE
                                body = ByteString(*TRANSFORMED_CLIENT_RESPONSE)
                            }
                        }
                    } else {
                        message
                    }
                }
            })
        },
    ) {
        // The request transform controls the server's payload size; the response transform then
        // replaces those wire bytes before exposing the result to the caller.
        val response = testService.unaryCall(SimpleRequest {})

        assertContentEquals(TRANSFORMED_CLIENT_RESPONSE, response.payload.body.toByteArray())
        assertUnaryLifecycle()
    }

    @Test
    fun interceptorAddsHeadersAndObservesCallbacksExactlyOnce() {
        val events = mutableListOf<String>()
        var headerCount = 0
        var closeCount = 0
        grpcClientTest(
            clientConfig = {
                // Exercise outbound metadata and both terminal callbacks in one interceptor.
                intercept(interceptor { request ->
                    requestHeaders.append(INTERCEPTOR_HEADER, "present")
                    onHeaders { headers ->
                        headerCount++
                        assertEquals("initial", headers.getAll(INITIAL_HEADER).single())
                        events += "headers"
                    }
                    onClose { status, trailers ->
                        closeCount++
                        assertEquals(GrpcStatusCode.OK, status.statusCode)
                        assertEquals("trailing", trailers.getAll(TRAILING_HEADER).single())
                        events += "close"
                    }
                    proceed(request).map {
                        events += "message"
                        it
                    }
                })
            },
            scenario = {
                initialMetadata = listOf(metadataEntry(INITIAL_HEADER, "initial"))
                trailingMetadata = listOf(metadataEntry(TRAILING_HEADER, "trailing"))
            },
        ) {
            assertEquals(Empty {}, testService.emptyCall(Empty {}))

            // Callback order and counts must match the single headers/message/close lifecycle.
            assertEquals(listOf("headers", "message", "close"), events)
            assertEquals(1, headerCount)
            assertEquals(1, closeCount)
            val accepted = serverTrace().events.single { it.type == EventType.CALL_ACCEPTED }
            assertEquals(
                listOf(metadataEntry(INTERCEPTOR_HEADER, "present")),
                accepted.metadata.filter { it.key == INTERCEPTOR_HEADER },
            )
            assertUnaryLifecycle()
        }
    }

    @Test
    fun interceptorEntryFailureIsPropagatedWithoutStartingCall() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { throw InterceptorFailure("entry") })
        },
        configureScenario = false,
    ) {
        val failure = assertFailsWith<InterceptorFailure> { testService.emptyCall(Empty {}) }
        assertEquals("entry", failure.message)
    }

    @Test
    fun requestWrapperFailureIsPropagated() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { request ->
                proceed(request.map { throw InterceptorFailure("request") })
            })
        },
        configureScenario = false,
    ) {
        // The request wrapper fails while the transport tries to collect the unary request.
        val failure = assertFailsWith<InterceptorFailure> { testService.emptyCall(Empty {}) }
        assertEquals("request", failure.message)
    }

    @Test
    fun responseWrapperFailureIsPropagated() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { request ->
                proceed(request).map { throw InterceptorFailure("response") }
            })
        },
    ) {
        // The server call succeeds, but the response wrapper replaces its result with the failure.
        val failure = assertFailsWith<InterceptorFailure> { testService.emptyCall(Empty {}) }
        assertEquals("response", failure.message)
    }

    @Test
    fun onHeadersFailureCancelsTheClientResult() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { request ->
                onHeaders { throw InterceptorFailure("headers") }
                proceed(request)
            })
        },
    ) {
        // Callback failures cross the public boundary as CANCELLED while retaining their cause.
        val failure = assertFailsWith<GrpcStatusException> { testService.emptyCall(Empty {}) }
        assertEquals(GrpcStatusCode.CANCELLED, failure.status.statusCode)
        assertEquals("headers", assertIs<InterceptorFailure>(failure.cause).message)
    }

    @Test
    fun onCloseFailureCancelsTheClientResult() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { request ->
                onClose { _, _ -> throw InterceptorFailure("close") }
                proceed(request)
            })
        },
    ) {
        // Even after transport close, an onClose failure becomes the observable client result.
        val failure = assertFailsWith<GrpcStatusException> { testService.emptyCall(Empty {}) }
        assertEquals(GrpcStatusCode.CANCELLED, failure.status.statusCode)
        assertEquals("close", assertIs<InterceptorFailure>(failure.cause).message)
    }

    @Test
    fun shortCircuitWithoutProceedPerformsNoServerCall() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { flowOf(Empty {}) })
        },
        configureScenario = false,
    ) {
        assertEquals(Empty {}, testService.emptyCall(Empty {}))
    }

    @Test
    fun explicitCancelFromInterceptorDoesNotStartCall() = grpcClientTest(
        clientConfig = {
            intercept(interceptor { cancel("cancelled by interceptor", InterceptorFailure("cancel cause")) })
        },
        configureScenario = false,
    ) {
        val failure = assertFailsWith<GrpcStatusException> { testService.emptyCall(Empty {}) }
        assertEquals(GrpcStatusCode.CANCELLED, failure.status.statusCode)
        assertEquals("cancel cause", assertIs<InterceptorFailure>(failure.cause).message)
    }

    @Test
    fun recollectingResponseFlowCreatesFreshCallScopeAndReinvokesInterceptors() {
        val callScopes = mutableListOf<Any>()
        val terminalStatuses = mutableListOf<GrpcStatusCode>()

        grpcClientTest(
            clientConfig = {
                intercept(interceptor { request ->
                    callScopes += this
                    onClose { status, _ -> terminalStatuses += status.statusCode }
                    proceed(request)
                })
            },
        ) {
            val request = StreamingOutputCallRequest {
                responseType = PayloadType.COMPRESSABLE
                responseParameters = listOf(ResponseParameters { size = 3 })
            }
            val flow = testService.streamingOutputCall(request)

            val firstRun = flow.toList()
            assertEquals(1, firstRun.size)

            val secondRun = flow.toList()
            assertEquals(1, secondRun.size)
            assertEquals(2, callScopes.size)
            assertNotSame(callScopes[0], callScopes[1])
            assertEquals(listOf(GrpcStatusCode.OK, GrpcStatusCode.OK), terminalStatuses)

            val trace = serverTrace()
            assertEquals(2, trace.events.count { it.type == EventType.CALL_ACCEPTED })
            assertEquals(2, trace.events.count { it.type == EventType.CALL_CLOSED })
        }
    }

    @Test
    fun retryOperatorOnResponseFlowReinvokesInterceptorsAndRecovers() {
        var attempts = 0
        val callScopes = mutableListOf<Any>()
        val terminalStatuses = mutableListOf<GrpcStatusCode>()
        grpcClientTest(
            clientConfig = {
                intercept(interceptor { request ->
                    attempts++
                    callScopes += this
                    if (attempts == 1) {
                        throw InterceptorFailure("attempt 1 failure")
                    }
                    onClose { status, _ -> terminalStatuses += status.statusCode }
                    proceed(request)
                })
            },
        ) {
            val request = StreamingOutputCallRequest {
                responseType = PayloadType.COMPRESSABLE
                responseParameters = listOf(ResponseParameters { size = 5 })
            }
            val responses = testService.streamingOutputCall(request)
                .retry(1) { it is InterceptorFailure }
                .toList()

            assertEquals(1, responses.size)
            assertEquals(2, attempts)
            assertEquals(2, callScopes.size)
            assertNotSame(callScopes[0], callScopes[1])
            assertEquals(listOf(GrpcStatusCode.OK), terminalStatuses)
            assertUnaryLifecycle()
        }
    }

    private companion object {
        const val TRANSFORMED_SERVER_RESPONSE_SIZE: Int = 19
        val TRANSFORMED_CLIENT_RESPONSE: ByteArray = byteArrayOf(2, 3, 5, 7, 11)
        const val INTERCEPTOR_HEADER: String = "x-interceptor-value"
        const val INITIAL_HEADER: String = "x-interceptor-initial"
        const val TRAILING_HEADER: String = "x-interceptor-trailing"

        fun metadataEntry(key: String, value: String): MetadataEntry = MetadataEntry {
            this.key = key
            this.value = ByteString(*value.encodeToByteArray())
        }

        fun interceptor(
            block: GrpcClientCallScope<Any, Any>.(Flow<Any>) -> Flow<Any>,
        ): GrpcClientInterceptor = object : GrpcClientInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                return with(this as GrpcClientCallScope<Any, Any>) {
                    block(request as Flow<Any>) as Flow<Response>
                }
            }
        }
    }
}

private class InterceptorFailure(message: String) : IllegalStateException(message)
