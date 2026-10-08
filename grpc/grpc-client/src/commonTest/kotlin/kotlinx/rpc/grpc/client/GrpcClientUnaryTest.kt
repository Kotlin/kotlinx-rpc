/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import io.grpc.testing.integration.EchoStatus
import io.grpc.testing.integration.Payload
import io.grpc.testing.integration.PayloadType
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.yield
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.testing.assertGrpcStatus
import kotlinx.rpc.grpc.client.testing.assertGrpcStatusCode
import kotlinx.rpc.grpc.client.testing.failedBeforeResponseEvents
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class GrpcClientUnaryTest {
    @Test
    fun emptyUnary() = grpcClientTest {
        assertEquals(Empty {}, testService.emptyCall(Empty {}))
        assertUnaryLifecycle()
    }

    @Test
    fun canonicalUnaryPayload() = grpcClientTest {
        val response = testService.unaryCall(
            SimpleRequest {
                responseType = PayloadType.COMPRESSABLE
                responseSize = LARGE_RESPONSE_SIZE
                payload = Payload {
                    type = PayloadType.COMPRESSABLE
                    body = ByteString(*ByteArray(LARGE_REQUEST_SIZE))
                }
            }
        )

        assertEquals(PayloadType.COMPRESSABLE, response.payload.type)
        assertContentEquals(ByteArray(LARGE_RESPONSE_SIZE), response.payload.body.toByteArray())
        assertUnaryLifecycle()
    }

    @Test
    fun unaryRequestProducedAfterCallStart() = grpcClientTest(
        clientConfig = { intercept(suspendingRequestInterceptor()) },
    ) {
        // A request that isn't ready when the call starts must still be sent exactly once.
        assertEquals(Empty {}, testService.emptyCall(Empty {}))
        assertUnaryLifecycle()
    }

    @Test
    fun standardInteropRequestedStatus() = grpcClientTest {
        val expectedMessage = "unary requested failure"

        assertGrpcStatus(GrpcStatusCode.DATA_LOSS, expectedMessage) {
            testService.unaryCall(
                SimpleRequest {
                    responseStatus = EchoStatus {
                        code = GrpcStatusCode.DATA_LOSS.value
                        message = expectedMessage
                    }
                }
            )
        }
        assertServerTrace(failedBeforeResponseEvents())
    }

    @Test
    fun standardInteropUnimplementedMethod() = grpcClientTest {
        assertGrpcStatusCode(GrpcStatusCode.UNIMPLEMENTED) {
            testService.unimplementedCall(Empty {})
        }
        assertServerTrace(failedBeforeResponseEvents())
    }

    @Test
    fun standardInteropUnimplementedService() = grpcClientTest(configureScenario = false) {
        assertGrpcStatusCode(GrpcStatusCode.UNIMPLEMENTED) {
            unimplementedService.unimplementedCall(Empty {})
        }
    }

    private companion object {
        const val LARGE_REQUEST_SIZE: Int = 271_828
        const val LARGE_RESPONSE_SIZE: Int = 314_159

        fun suspendingRequestInterceptor(): GrpcClientInterceptor = object : GrpcClientInterceptor {
            override fun <Request, Response> GrpcClientCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> = proceed(
                flow {
                    yield()
                    request.collect { emit(it) }
                }
            )
        }
    }
}
