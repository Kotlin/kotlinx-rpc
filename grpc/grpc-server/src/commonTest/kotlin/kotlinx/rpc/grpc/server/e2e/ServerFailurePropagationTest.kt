/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.ResponseParameters
import io.grpc.testing.integration.StreamingInputCallRequest
import io.grpc.testing.integration.StreamingOutputCallRequest
import io.grpc.testing.integration.StreamingOutputCallResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.serverInterceptor
import kotlinx.rpc.grpc.server.testing.zeros
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerFailurePropagationTest {
    @Test
    fun serviceExceptionStopsTheActiveRequestProducer() = clientStreamingFailureTest(
        failure = IllegalStateException("Service failed"),
        expectedCode = GrpcStatusCode.UNKNOWN,
    )

    @Test
    fun serviceStatusFailureStopsTheActiveRequestProducer() = clientStreamingFailureTest(
        failure = GrpcStatusCode.ABORTED.asException("Service aborted"),
        expectedCode = GrpcStatusCode.ABORTED,
    )

    @Test
    fun serviceCancellationStopsTheActiveRequestProducer() = clientStreamingFailureTest(
        failure = CancellationException("Service cancelled"),
        expectedCode = GrpcStatusCode.CANCELLED,
    )

    @Test
    fun interceptorInvocationFailureStopsTheActiveRequestProducerWithoutInvokingTheService() =
        clientStreamingFailureTest(
            failure = IllegalStateException("Interceptor failed"),
            expectedCode = GrpcStatusCode.UNKNOWN,
            failInInterceptor = true,
        )

    @Test
    fun requestTransformationFailureStopsTheProducerAndPreservesTheEarlierResponse() =
        bidiFailureTest(transformRequests = true)

    @Test
    fun responseTransformationFailureStopsTheProducerAndPreservesTheEarlierResponse() =
        bidiFailureTest(transformRequests = false)

    private fun clientStreamingFailureTest(
        failure: Throwable,
        expectedCode: GrpcStatusCode,
        failInInterceptor: Boolean = false,
    ) = grpcServerTest {
        val producerActive = CompletableDeferred<Unit>()
        val producerStopped = CompletableDeferred<Unit>()
        val producerActiveAtFailure = CompletableDeferred<Boolean>()
        val waitForProducer = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = flow {
                withTimeout(AWAIT_TIMEOUT) { producerActive.await() }
                emitAll(proceed(request))
            }
        }
        start(
            serverConfig = {
                if (failInInterceptor) {
                    intercept(waitForProducer, serverInterceptor {
                        producerActiveAtFailure.complete(!producerStopped.isCompleted)
                        throw failure
                    })
                }
            },
        ) {
            streamingInputCall = { requests ->
                marks.add("service")
                assertEquals(zeros(7), requests.first().payload)
                withTimeout(AWAIT_TIMEOUT) { producerActive.await() }
                producerActiveAtFailure.complete(!producerStopped.isCompleted)
                throw failure
            }
        }

        withTimeout(AWAIT_TIMEOUT) {
            assertGrpcStatus(expectedCode) {
                testService.streamingInputCall(flow {
                    try {
                        emit(StreamingInputCallRequest { payload = zeros(7) })
                        producerActive.complete(Unit)
                        awaitCancellation()
                    } finally {
                        producerStopped.complete(Unit)
                    }
                })
            }
        }

        assertTrue(producerActive.isCompleted)
        assertTrue(withTimeout(AWAIT_TIMEOUT) { producerActiveAtFailure.await() })
        withTimeout(AWAIT_TIMEOUT) { producerStopped.await() }
        val expectedMarks = if (failInInterceptor) {
            emptyList()
        } else {
            listOf("service")
        }
        assertEquals(expectedMarks, marks.events)
    }

    private fun bidiFailureTest(transformRequests: Boolean) = grpcServerTest {
        val firstResponse = CompletableDeferred<Unit>()
        val producerActive = CompletableDeferred<Unit>()
        val producerStopped = CompletableDeferred<Unit>()
        val producerActiveAtFailure = CompletableDeferred<Boolean>()
        val expectedCode = if (transformRequests) {
            GrpcStatusCode.INVALID_ARGUMENT
        } else {
            GrpcStatusCode.UNKNOWN
        }
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                var count = 0
                suspend fun rejectSecondMessage() {
                    if (++count == 2) {
                        withTimeout(AWAIT_TIMEOUT) { producerActive.await() }
                        producerActiveAtFailure.complete(!producerStopped.isCompleted)
                        if (transformRequests) {
                            throw GrpcStatusCode.INVALID_ARGUMENT.asException("Request rejected")
                        }
                        error("Response rejected")
                    }
                }
                return if (transformRequests) {
                    proceed(request.map {
                        rejectSecondMessage()
                        it
                    })
                } else {
                    proceed(request).map {
                        rejectSecondMessage()
                        it
                    }
                }
            }
        }
        start(
            serverConfig = { intercept(interceptor) },
        )
        val responses = mutableListOf<StreamingOutputCallResponse>()

        withTimeout(AWAIT_TIMEOUT) {
            assertGrpcStatus(expectedCode) {
                testService.fullDuplexCall(flow {
                    try {
                        emit(StreamingOutputCallRequest {
                            responseParameters = listOf(ResponseParameters { size = 3 })
                        })
                        firstResponse.await()
                        emit(StreamingOutputCallRequest {
                            responseParameters = listOf(ResponseParameters { size = 7 })
                        })
                        producerActive.complete(Unit)
                        awaitCancellation()
                    } finally {
                        producerStopped.complete(Unit)
                    }
                }).collect {
                    responses += it
                    firstResponse.complete(Unit)
                }
            }
        }

        assertTrue(producerActive.isCompleted)
        assertTrue(withTimeout(AWAIT_TIMEOUT) { producerActiveAtFailure.await() })
        withTimeout(AWAIT_TIMEOUT) { producerStopped.await() }
        assertEquals(listOf(zeros(3)), responses.map { it.payload })
    }
}
