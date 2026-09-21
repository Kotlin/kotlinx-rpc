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
import kxrpc.testing.MalformedResponse
import kxrpc.testing.MalformedResponseCardinality
import kxrpc.testing.MalformedResponseRequest
import kxrpc.testing.MalformedResponseServiceGrpc

/** Raw grpc-java handlers for response cardinalities forbidden by generated service adapters. */
internal class MalformedResponseTestService(
    private val registry: CallScenarioRegistry,
) : BindableService {
    override fun bindService(): ServerServiceDefinition {
        val unaryMethod = MalformedResponseServiceGrpc.getUnaryCallMethod()
            .toBuilder()
            .setType(MethodDescriptor.MethodType.SERVER_STREAMING)
            .build()
        val streamingInputMethod = MalformedResponseServiceGrpc.getStreamingInputCallMethod()
            .toBuilder()
            .setType(MethodDescriptor.MethodType.BIDI_STREAMING)
            .build()

        return ServerServiceDefinition.builder(MalformedResponseServiceGrpc.SERVICE_NAME)
            .addMethod(unaryMethod, handler(clientStreaming = false))
            .addMethod(streamingInputMethod, handler(clientStreaming = true))
            .build()
    }

    private fun handler(
        clientStreaming: Boolean,
    ): ServerCallHandler<MalformedResponseRequest, MalformedResponse> {
        return ServerCallHandler { call, headers -> startCall(call, headers, clientStreaming) }
    }

    private fun startCall(
        call: ServerCall<MalformedResponseRequest, MalformedResponse>,
        headers: Metadata,
        clientStreaming: Boolean,
    ): ServerCall.Listener<MalformedResponseRequest> {
        val callId = headers.get(TEST_CALL_ID_METADATA_KEY)
        if (callId == null) {
            call.close(
                Status.INVALID_ARGUMENT.withDescription(
                    "metadata '$TEST_CALL_ID_METADATA_KEY_NAME' is required for malformed response calls"
                ),
                Metadata(),
            )
            return object : ServerCall.Listener<MalformedResponseRequest>() {}
        }

        val cardinality = registry.configuration(callId).malformedResponseCardinality
        if (cardinality == MalformedResponseCardinality.MALFORMED_RESPONSE_CARDINALITY_UNSPECIFIED) {
            call.close(
                Status.INVALID_ARGUMENT.withDescription("malformed response cardinality must be configured"),
                Metadata(),
            )
            return object : ServerCall.Listener<MalformedResponseRequest>() {}
        }

        call.request(if (clientStreaming) 1 else 2)
        return MalformedResponseCallListener(call, cardinality, clientStreaming)
    }

    private class MalformedResponseCallListener(
        private val call: ServerCall<MalformedResponseRequest, MalformedResponse>,
        private val cardinality: MalformedResponseCardinality,
        private val clientStreaming: Boolean,
    ) : ServerCall.Listener<MalformedResponseRequest>() {
        private var requestCount: Int = 0
        private var responsePayload = MalformedResponse.getDefaultInstance()

        override fun onMessage(message: MalformedResponseRequest) {
            requestCount++
            responsePayload = MalformedResponse.newBuilder()
                .setPayload(message.payload)
                .build()
            if (clientStreaming) call.request(1)
        }

        override fun onHalfClose() {
            if (!clientStreaming && requestCount != 1) {
                call.close(
                    Status.INVALID_ARGUMENT.withDescription(
                        "unary malformed response call requires exactly one request"
                    ),
                    Metadata(),
                )
                return
            }

            call.sendHeaders(Metadata())
            when (cardinality) {
                MalformedResponseCardinality.OMIT_RESPONSE -> {
                    // Deliberately close an OK call without a response message.
                }
                MalformedResponseCardinality.DUPLICATE_RESPONSE -> {
                    call.sendMessage(responsePayload)
                    call.sendMessage(responsePayload)
                }
                MalformedResponseCardinality.MALFORMED_RESPONSE_CARDINALITY_UNSPECIFIED,
                MalformedResponseCardinality.UNRECOGNIZED,
                -> {
                    error("validated malformed response cardinality became invalid")
                }
            }
            call.close(Status.OK, Metadata())
        }
    }
}
