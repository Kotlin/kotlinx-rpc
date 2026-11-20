/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import io.grpc.testing.integration.StreamingOutputCallResponse
import io.grpc.testing.integration.invoke
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.client.internal.bidirectionalStreamingRpc
import kotlinx.rpc.grpc.client.internal.clientStreamingRpc
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.server.testing.TestServiceMethods
import kotlinx.rpc.grpc.server.testing.assertGrpcStatus
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.rawMethod
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ServerProtocolViolationTest {
    @Test
    fun unaryWithoutARequestFailsWithInternal() = grpcServerTest {
        start()

        val error = assertGrpcStatus(GrpcStatusCode.INTERNAL) {
            client.clientStreamingRpc(
                rawMethod(TestServiceMethods.UNARY_CALL, GrpcMethodType.CLIENT_STREAMING),
                emptyFlow(),
            )
        }

        assertContains(error.status.description.orEmpty(), "received none")
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.INTERNAL, close.status.statusCode)
    }

    @Test
    fun serverStreamingWithoutARequestFailsWithInternal() = grpcServerTest {
        start()

        val error = assertGrpcStatus(GrpcStatusCode.INTERNAL) {
            client.clientStreamingRpc(
                rawMethod(TestServiceMethods.STREAMING_OUTPUT_CALL, GrpcMethodType.CLIENT_STREAMING),
                emptyFlow(),
            )
        }

        assertContains(error.status.description.orEmpty(), "received none")
        val close = observer.awaitCall(TestServiceMethods.STREAMING_OUTPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.INTERNAL, close.status.statusCode)
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-715
    fun unaryWithTwoRequestsFailsBeforeSendingAResponse() = grpcServerTest {
        start()
        val responses = mutableListOf<ByteArray>()

        val error = assertGrpcStatus(GrpcStatusCode.INTERNAL) {
            client.bidirectionalStreamingRpc(
                rawMethod(TestServiceMethods.UNARY_CALL, GrpcMethodType.BIDI_STREAMING),
                flowOf(ByteArray(0), ByteArray(0)),
            ).collect { responses += it }
        }

        assertContains(error.status.description.orEmpty(), "received two")
        assertEquals(emptyList(), responses)
        val close = observer.awaitCall(TestServiceMethods.UNARY_CALL).awaitClose()
        assertEquals(GrpcStatusCode.INTERNAL, close.status.statusCode)
    }

    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KXRPC-715
    fun serverStreamingWithTwoRequestsFailsBeforeSendingAResponse() = grpcServerTest {
        start { streamingOutputCall = { flowOf(StreamingOutputCallResponse {}) } }
        val responses = mutableListOf<ByteArray>()

        val error = assertGrpcStatus(GrpcStatusCode.INTERNAL) {
            client.bidirectionalStreamingRpc(
                rawMethod(TestServiceMethods.STREAMING_OUTPUT_CALL, GrpcMethodType.BIDI_STREAMING),
                flowOf(ByteArray(0), ByteArray(0)),
            ).collect { responses += it }
        }

        assertContains(error.status.description.orEmpty(), "received two")
        assertEquals(emptyList(), responses)
        val close = observer.awaitCall(TestServiceMethods.STREAMING_OUTPUT_CALL).awaitClose()
        assertEquals(GrpcStatusCode.INTERNAL, close.status.statusCode)
    }
}
