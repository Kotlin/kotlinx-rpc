/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.*
import io.grpc.testing.integration.*
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.testing.assertGrpcStatus
import kotlinx.rpc.grpc.client.testing.assertGrpcStatusCode
import kotlinx.rpc.grpc.client.testing.failedUnaryBeforeResponseEvents
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
        assertServerTrace(failedUnaryBeforeResponseEvents())
    }

    @Test
    fun standardInteropUnimplementedMethod() = grpcClientTest {
        assertGrpcStatusCode(GrpcStatusCode.UNIMPLEMENTED) {
            testService.unimplementedCall(Empty {})
        }
        assertServerTrace(failedUnaryBeforeResponseEvents())
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
    }
}
