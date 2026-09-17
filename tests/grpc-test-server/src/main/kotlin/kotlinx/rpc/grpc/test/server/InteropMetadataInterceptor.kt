/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.ForwardingServerCall
import io.grpc.ForwardingServerCallListener
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType

internal const val TEST_CALL_ID_METADATA_KEY_NAME: String = "kxrpc-test-call-id"

internal class InteropMetadataInterceptor(
    private val registry: CallScenarioRegistry,
) : ServerInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): ServerCall.Listener<ReqT> {
        val callIds = headers.getAll(CALL_ID_KEY)?.toList().orEmpty()
        if (callIds.isEmpty()) return next.startCall(call, headers)
        if (callIds.size != 1 || callIds.single().isBlank()) {
            return reject(call, "metadata '$TEST_CALL_ID_METADATA_KEY_NAME' must contain one non-blank value")
        }

        val callId = callIds.single()
        try {
            registry.callAccepted(callId)
        } catch (error: Throwable) {
            return reject(call, error.toGrpcStatus())
        }

        val scenarioCall = ScenarioServerCall(call, registry, callId)
        val listener = try {
            next.startCall(scenarioCall, headers)
        } catch (error: Throwable) {
            scenarioCall.fail(error)
            return object : ServerCall.Listener<ReqT>() {}
        }
        return ScenarioServerCallListener(listener, scenarioCall)
    }

    private fun <ReqT : Any, RespT : Any> reject(
        call: ServerCall<ReqT, RespT>,
        description: String,
    ): ServerCall.Listener<ReqT> {
        return reject(call, Status.INVALID_ARGUMENT.withDescription(description))
    }

    private fun <ReqT : Any, RespT : Any> reject(
        call: ServerCall<ReqT, RespT>,
        status: Status,
    ): ServerCall.Listener<ReqT> {
        call.close(status, Metadata())
        return object : ServerCall.Listener<ReqT>() {}
    }

    private class ScenarioServerCall<ReqT : Any, RespT : Any>(
        delegate: ServerCall<ReqT, RespT>,
        private val registry: CallScenarioRegistry,
        private val callId: String,
    ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {
        private var responseCount: Int = 0
        private var closed: Boolean = false

        override fun sendHeaders(headers: Metadata) {
            if (closed) return
            try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_INITIAL_HEADERS, 1)
                super.sendHeaders(headers)
                registry.recordEvent(callId, EventType.INITIAL_HEADERS_SENT)
            } catch (error: Throwable) {
                fail(error)
            }
        }

        override fun sendMessage(message: RespT) {
            if (closed) return
            val occurrence = responseCount + 1
            try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_RESPONSE, occurrence)
                super.sendMessage(message)
                responseCount = occurrence
                registry.recordEvent(callId, EventType.RESPONSE_MESSAGE_SENT)
            } catch (error: Throwable) {
                fail(error)
            }
        }

        override fun close(status: Status, trailers: Metadata) {
            if (closed) return
            try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.CLOSE_CALL, 1)
            } catch (error: Throwable) {
                fail(error)
                return
            }

            closed = true
            super.close(status, trailers)
            recordClosed()
        }

        fun recordRequestEvent(type: EventType, barrier: BarrierType): Boolean {
            if (closed) return false
            return try {
                val event = registry.recordEvent(callId, type)
                registry.awaitBarrierIfConfigured(callId, barrier, event.occurrence)
                true
            } catch (error: Throwable) {
                fail(error)
                false
            }
        }

        fun fail(error: Throwable) {
            fail(error.toGrpcStatus())
        }

        private fun fail(status: Status) {
            if (closed) return
            closed = true
            super.close(status, Metadata())
            recordClosed()
        }

        private fun recordClosed() {
            runCatching { registry.callClosed(callId) }
        }
    }

    private class ScenarioServerCallListener<ReqT : Any, RespT : Any>(
        delegate: ServerCall.Listener<ReqT>,
        private val call: ScenarioServerCall<ReqT, RespT>,
    ) : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
        override fun onMessage(message: ReqT) {
            if (call.recordRequestEvent(
                    EventType.REQUEST_MESSAGE_RECEIVED,
                    BarrierType.DELIVER_REQUEST_MESSAGE,
                )
            ) {
                super.onMessage(message)
            }
        }

        override fun onHalfClose() {
            if (call.recordRequestEvent(
                    EventType.CLIENT_HALF_CLOSED,
                    BarrierType.DELIVER_CLIENT_HALF_CLOSE,
                )
            ) {
                super.onHalfClose()
            }
        }
    }

    private companion object {
        val CALL_ID_KEY: Metadata.Key<String> =
            Metadata.Key.of(TEST_CALL_ID_METADATA_KEY_NAME, Metadata.ASCII_STRING_MARSHALLER)
    }
}
