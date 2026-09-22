/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import io.grpc.Context
import io.grpc.Contexts
import io.grpc.ForwardingServerCall
import io.grpc.ForwardingServerCallListener
import io.grpc.InternalMetadata
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import io.grpc.Status
import io.grpc.testing.integration.Messages.StreamingInputCallRequest
import io.grpc.testing.integration.Messages.StreamingOutputCallRequest
import java.nio.charset.StandardCharsets.US_ASCII
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import kxrpc.testing.BarrierType
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GrpcStatus
import kxrpc.testing.MetadataEntry
import kxrpc.testing.TerminalStage

internal const val TEST_CALL_ID_METADATA_KEY_NAME: String = "kxrpc-test-call-id"
internal val TEST_CALL_ID_METADATA_KEY: Metadata.Key<String> =
    Metadata.Key.of(TEST_CALL_ID_METADATA_KEY_NAME, Metadata.ASCII_STRING_MARSHALLER)
internal val TEST_CALL_ID_CONTEXT_KEY: Context.Key<String> = Context.key(TEST_CALL_ID_METADATA_KEY_NAME)

internal class InteropMetadataInterceptor(
    private val registry: CallScenarioRegistry,
) : ServerInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): ServerCall.Listener<ReqT> {
        val callIds = headers.getAll(TEST_CALL_ID_METADATA_KEY)?.toList().orEmpty()
        if (callIds.isEmpty()) return next.startCall(call, headers)
        if (callIds.size != 1 || callIds.single().isBlank()) {
            return reject(call, "metadata '$TEST_CALL_ID_METADATA_KEY_NAME' must contain one non-blank value")
        }

        val callId = callIds.single()
        try {
            registry.callAccepted(callId, headers.toMetadataEntries())
        } catch (error: Throwable) {
            return reject(call, error.toGrpcStatus())
        }

        val scenarioCall = ScenarioServerCall(call, registry, registry.configuration(callId))
        val scenarioContext = Context.current().withValue(TEST_CALL_ID_CONTEXT_KEY, callId)
        scenarioContext.addListener(
            Context.CancellationListener { scenarioCall.clientCancelled() },
            DIRECT_EXECUTOR,
        )
        if (scenarioCall.closeBeforeStartIfConfigured()) {
            return object : ServerCall.Listener<ReqT>() {}
        }
        val listener = try {
            Contexts.interceptCall(scenarioContext, scenarioCall, headers, next)
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
        private val configuration: ConfigureScenarioRequest,
    ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {
        private val callId: String = configuration.callId
        private var responseCount: Int = 0
        private var initialHeadersSent: Boolean = false
        private val terminated = AtomicBoolean()

        fun closeBeforeStartIfConfigured(): Boolean {
            if (configuration.terminalStage() != TerminalStage.BEFORE_INITIAL_METADATA) return false
            closeConfiguredTerminal()
            return true
        }

        override fun sendHeaders(headers: Metadata) {
            if (terminated.get()) return
            try {
                val outgoingHeaders = headers.withConfigured(configuration.initialMetadataList)
                val traceMetadata = outgoingHeaders.toMetadataEntries()
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_INITIAL_HEADERS, 1)
                if (terminated.get()) return
                super.sendHeaders(outgoingHeaders)
                initialHeadersSent = true
                registry.recordEvent(
                    callId,
                    EventType.INITIAL_HEADERS_SENT,
                    metadata = traceMetadata,
                )
                if (configuration.terminalStage() == TerminalStage.AFTER_INITIAL_METADATA) {
                    closeConfiguredTerminal()
                }
            } catch (error: Throwable) {
                fail(error)
            }
        }

        override fun sendMessage(message: RespT) {
            if (terminated.get()) return
            val occurrence = responseCount + 1
            try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_RESPONSE, occurrence)
                if (terminated.get()) return
                super.sendMessage(message)
                responseCount = occurrence
                registry.recordEvent(callId, EventType.RESPONSE_MESSAGE_SENT)
                if (configuration.terminalStage() == TerminalStage.AFTER_RESPONSE_MESSAGES &&
                    responseCount == configuration.terminalBehavior.responseMessageCount
                ) {
                    closeConfiguredTerminal()
                }
            } catch (error: Throwable) {
                fail(error)
            }
        }

        override fun close(status: Status, trailers: Metadata) {
            if (terminated.get()) return
            try {
                val terminalStage = configuration.terminalStage()
                if (!initialHeadersSent &&
                    (configuration.initialMetadataCount > 0 || terminalStage == TerminalStage.AFTER_INITIAL_METADATA)
                ) {
                    sendHeaders(Metadata())
                    if (terminated.get()) return
                }

                when (terminalStage) {
                    null -> completeClose(status, trailers)
                    TerminalStage.AFTER_SERVICE_COMPLETION -> closeConfiguredTerminal(trailers)
                    TerminalStage.AFTER_RESPONSE_MESSAGES -> fail(
                        Status.INTERNAL.withDescription(
                            "service completed after $responseCount responses before configured response " +
                                configuration.terminalBehavior.responseMessageCount
                        )
                    )
                    TerminalStage.BEFORE_INITIAL_METADATA,
                    TerminalStage.AFTER_INITIAL_METADATA,
                    -> closeConfiguredTerminal(trailers)
                    TerminalStage.TERMINAL_STAGE_UNSPECIFIED,
                    TerminalStage.UNRECOGNIZED,
                    -> error("validated terminal stage became invalid")
                }
            } catch (error: Throwable) {
                fail(error)
            }
        }

        fun recordRequestEvent(
            type: EventType,
            barrier: BarrierType,
            requestPayload: ByteString? = null,
        ): Boolean {
            if (terminated.get()) return false
            return try {
                val event = if (type == EventType.REQUEST_MESSAGE_RECEIVED) {
                    registry.recordRequestMessage(callId, requestPayload)
                } else {
                    registry.recordEvent(callId, type)
                }
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

        fun clientCancelled() {
            if (!terminated.compareAndSet(false, true)) return
            runCatching { registry.clientCancelled(callId) }
        }

        private fun fail(status: Status) {
            completeClose(status, Metadata(), includeConfiguredTrailers = false)
        }

        private fun closeConfiguredTerminal(serviceTrailers: Metadata = Metadata()) {
            val terminal = configuration.terminalBehavior
            val status = Status.fromCodeValue(terminal.status.code)
                .withDescription(terminal.status.description)
            completeClose(status, serviceTrailers)
        }

        private fun completeClose(
            status: Status,
            trailers: Metadata,
            includeConfiguredTrailers: Boolean = true,
        ) {
            if (terminated.get()) return
            val (outgoingStatus, outgoingTrailers) = try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.CLOSE_CALL, 1)
                status to if (includeConfiguredTrailers) {
                    trailers.withConfigured(configuration.trailingMetadataList)
                } else {
                    trailers.withConfigured(emptyList())
                }
            } catch (error: Throwable) {
                error.toGrpcStatus() to Metadata()
            }
            if (!terminated.compareAndSet(false, true)) return
            val traceMetadata = outgoingTrailers.toMetadataEntries()
            val traceStatus = outgoingStatus.toTraceStatus()
            try {
                super.close(outgoingStatus, outgoingTrailers)
            } finally {
                recordClosed(traceMetadata, traceStatus)
            }
        }

        private fun recordClosed(metadata: List<MetadataEntry>, status: GrpcStatus) {
            runCatching { registry.callClosed(callId, metadata, status) }
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
                    message.interopPayload(),
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

        override fun onCancel() {
            call.clientCancelled()
            super.onCancel()
        }
    }

}

private fun ConfigureScenarioRequest.terminalStage(): TerminalStage? {
    return if (hasTerminalBehavior()) terminalBehavior.stage else null
}

private fun Metadata.withConfigured(entries: List<MetadataEntry>): Metadata {
    return Metadata().also { result ->
        result.merge(this)
        entries.forEach { entry -> result.put(entry) }
    }
}

private fun Metadata.put(entry: MetadataEntry) {
    val value = entry.value.toByteArray()
    if (entry.key.endsWith(Metadata.BINARY_HEADER_SUFFIX)) {
        val key = Metadata.Key.of(entry.key, Metadata.BINARY_BYTE_MARSHALLER)
        put(key, value)
    } else {
        val key = Metadata.Key.of(entry.key, Metadata.ASCII_STRING_MARSHALLER)
        put(key, String(value, US_ASCII))
    }
}

private fun Metadata.toMetadataEntries(): List<MetadataEntry> {
    val serialized = InternalMetadata.serialize(this)
    return buildList(serialized.size / 2) {
        for (index in serialized.indices step 2) {
            val key = String(serialized[index], US_ASCII)
            if (key.startsWith(':')) continue
            add(
                MetadataEntry.newBuilder()
                    .setKey(key)
                    .setValue(ByteString.copyFrom(serialized[index + 1]))
                    .build()
            )
        }
    }
}

private fun Status.toTraceStatus(): GrpcStatus {
    return GrpcStatus.newBuilder()
        .setCode(code.value())
        .setDescription(description.orEmpty())
        .build()
}

private fun Any.interopPayload(): ByteString? = when (this) {
    is StreamingInputCallRequest -> payload.body
    is StreamingOutputCallRequest -> payload.body
    else -> null
}

private val DIRECT_EXECUTOR: Executor = Executor { command -> command.run() }
