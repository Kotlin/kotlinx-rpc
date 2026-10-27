/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.BindableService
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerServiceDefinition
import io.grpc.Status
import kxrpc.testing.EchoRequest
import kxrpc.testing.EchoResponse
import kxrpc.testing.InvalidResponseCount
import kxrpc.testing.InvalidResponseCountServiceGrpc

/**
 * Raw grpc-java handlers for response cardinalities that the generated service adapters don't allow.
 *
 * Client tests use them to check how the client handles an invalid number of response messages.
 *
 * The server binds `UnaryCall` as server-streaming and `StreamingInputCall` as bidirectional streaming. So a call
 * that the proto declares as unary or client-streaming can complete with zero or two response messages. The
 * `invalid_response_count` of the scenario selects the count.
 */
internal class InvalidResponseCountTestService(
    private val registry: CallScenarioRegistry,
) : BindableService {
    override fun bindService(): ServerServiceDefinition {
        val unaryMethod = InvalidResponseCountServiceGrpc.getUnaryCallMethod()
            .toBuilder()
            .setType(MethodDescriptor.MethodType.SERVER_STREAMING)
            .build()
        val streamingInputMethod = InvalidResponseCountServiceGrpc.getStreamingInputCallMethod()
            .toBuilder()
            .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
            .build()

        return ServerServiceDefinition.builder(InvalidResponseCountServiceGrpc.SERVICE_NAME)
            .addMethod(unaryMethod, handler(clientStreaming = false))
            .addMethod(streamingInputMethod, handler(clientStreaming = true))
            .build()
    }

    private fun handler(
        clientStreaming: Boolean,
    ): ServerCallHandler<EchoRequest, EchoResponse> {
        return ServerCallHandler { call, headers -> startCall(call, headers, clientStreaming) }
    }

    private fun startCall(
        call: ServerCall<EchoRequest, EchoResponse>,
        headers: Metadata,
        clientStreaming: Boolean,
    ): ServerCall.Listener<EchoRequest> {
        val callId = headers.get(TEST_CALL_ID_METADATA_KEY)
        if (callId == null) {
            call.closeInvalid(
                "the call has no '$TEST_CALL_ID_METADATA_KEY_NAME' metadata. Set it to the call ID of a scenario"
            )
            return object : ServerCall.Listener<EchoRequest>() {}
        }

        val count = registry.configuration(callId).invalidResponseCount
        if (count == InvalidResponseCount.INVALID_RESPONSE_COUNT_UNSPECIFIED) {
            call.closeInvalid("the scenario doesn't set invalid_response_count. Set it in ConfigureScenario")
            return object : ServerCall.Listener<EchoRequest>() {}
        }

        // For a unary call, the server requests one message more than expected, so it can find a second request.
        call.request(
            if (clientStreaming) {
                1
            } else {
                2
            }
        )
        return InvalidResponseCountCallListener(call, count, clientStreaming)
    }

    private class InvalidResponseCountCallListener(
        private val call: ServerCall<EchoRequest, EchoResponse>,
        private val count: InvalidResponseCount,
        private val clientStreaming: Boolean,
    ) : ServerCall.Listener<EchoRequest>() {
        private var requestCount: Int = 0
        private var responsePayload = EchoResponse.getDefaultInstance()

        override fun onMessage(message: EchoRequest) {
            requestCount++
            responsePayload = EchoResponse.newBuilder()
                .setPayload(message.payload)
                .build()
            if (clientStreaming) {
                call.request(1)
            }
        }

        override fun onHalfClose() {
            if (!clientStreaming && requestCount != 1) {
                call.closeInvalid(
                    "the unary call has $requestCount request messages. Send exactly one"
                )
                return
            }

            call.sendHeaders(Metadata())
            when (count) {
                InvalidResponseCount.OMIT_RESPONSE -> {
                    // No response message. The call closes with OK below.
                }
                InvalidResponseCount.DUPLICATE_RESPONSE -> {
                    call.sendMessage(responsePayload)
                    call.sendMessage(responsePayload)
                }
                InvalidResponseCount.INVALID_RESPONSE_COUNT_UNSPECIFIED,
                InvalidResponseCount.UNRECOGNIZED -> {
                    error("invalid_response_count changed after validation")
                }
            }
            call.close(Status.OK, Metadata())
        }
    }
}

private fun ServerCall<*, *>.closeInvalid(description: String) {
    close(Status.INVALID_ARGUMENT.withDescription(description), Metadata())
}
