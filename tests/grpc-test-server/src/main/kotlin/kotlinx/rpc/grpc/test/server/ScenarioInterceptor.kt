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

/** Name of the request metadata through which a data-plane call selects its scenario. */
internal const val TEST_CALL_ID_METADATA_KEY_NAME: String = "kxrpc-test-call-id"

/** Request metadata key carrying the `call_id` of the scenario a data-plane call opts into. */
internal val TEST_CALL_ID_METADATA_KEY: Metadata.Key<String> =
    Metadata.Key.of(TEST_CALL_ID_METADATA_KEY_NAME, Metadata.ASCII_STRING_MARSHALLER)

/**
 * Context key exposing the scenario's `call_id` to service implementations, for example to [FlowControlSupport].
 * Set only for calls that carry [TEST_CALL_ID_METADATA_KEY].
 */
internal val TEST_CALL_ID_CONTEXT_KEY: Context.Key<String> = Context.key(TEST_CALL_ID_METADATA_KEY_NAME)

/**
 * Applies a configured scenario to every data-plane call that carries [TEST_CALL_ID_METADATA_KEY].
 *
 * The call's lifecycle (headers, messages, half-close, cancellation, close) is reported to [registry],
 * paused at the scenario's barriers, extended with its configured metadata, and terminated early when it
 * has a terminal behavior. Calls without the key pass through unchanged.
 *
 * A call with more than one or a blank call id is rejected with `INVALID_ARGUMENT`, and a call for an unknown or
 * discarded scenario with the status of the failed [CallScenarioRegistry.callAccepted]. Neither reaches the service.
 */
internal class ScenarioInterceptor(
    private val registry: CallScenarioRegistry,
) : ServerInterceptor {
    override fun <ReqT : Any, RespT : Any> interceptCall(
        call: ServerCall<ReqT, RespT>,
        headers: Metadata,
        next: ServerCallHandler<ReqT, RespT>,
    ): ServerCall.Listener<ReqT> {
        val callIds = headers.getAll(TEST_CALL_ID_METADATA_KEY)?.toList().orEmpty()
        // If no call id is present, pass through unchanged.
        if (callIds.isEmpty()) return next.startCall(call, headers)

        if (callIds.size != 1 || callIds.single().isBlank()) {
            return reject(call, "metadata '$TEST_CALL_ID_METADATA_KEY_NAME' must contain one non-blank value")
        }

        val callId = callIds.single()
        // Records CALL_ACCEPTED first, so the trace starts before anything else can happen to the call.
        try {
            registry.callAccepted(callId, headers.toMetadataEntries())
        } catch (error: Throwable) {
            return reject(call, error.toGrpcStatus())
        }

        val scenarioCall = ScenarioServerCall(call, registry, registry.configuration(callId))
        // Create a scenario context for the given call id.
        val scenarioContext = Context.current().withValue(TEST_CALL_ID_CONTEXT_KEY, callId)
        // grpc-java cancels the call's context promptly on a separate executor, while ServerCall.Listener.onCancel
        // waits on the serialized call executor. That executor may be blocked at a request barrier, so this listener
        // is what records a cancellation while the call is paused.
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

    /**
     * Applies the scenario to the server's outbound side of one call: headers, response messages, and close.
     *
     * Each operation first waits at its barrier, then forwards to the delegate and records the matching event while
     * holding [lifecycleLock]. Waiting outside the lock lets a cancellation be recorded while the call is paused; the
     * lock makes the trace order match the order in which the call was actually driven.
     *
     * The call ends exactly once, through [completeClose] or [clientCancelled], which set [terminated]. Every later
     * operation is ignored, so the trace contains a single [EventType.CALL_CLOSED] or [EventType.CLIENT_CANCELLED].
     * Failures inside the scenario logic close the call with the failure's status instead of propagating.
     */
    private class ScenarioServerCall<ReqT : Any, RespT : Any>(
        delegate: ServerCall<ReqT, RespT>,
        private val registry: CallScenarioRegistry,
        private val configuration: ConfigureScenarioRequest,
    ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {
        private val callId: String = configuration.callId

        /** Response messages sent on this call, which selects the [BarrierType.SEND_RESPONSE] occurrence. */
        private var responseCount: Int = 0
        private var initialHeadersSent: Boolean = false

        /** Set once when the call closes or is cancelled. Checked again under [lifecycleLock] after each wait. */
        private val terminated = AtomicBoolean()

        /** Serializes forwarding to the delegate with recording the matching event and with termination. */
        private val lifecycleLock = Any()

        /**
         * Closes the call with the configured status if the scenario terminates [TerminalStage.BEFORE_INITIAL_METADATA].
         *
         * @return `true` when the call was closed and the service must not be started.
         */
        fun closeBeforeStartIfConfigured(): Boolean {
            if (configuration.terminalStage() != TerminalStage.BEFORE_INITIAL_METADATA) return false
            closeConfiguredTerminal()
            return true
        }

        /** Adds the configured initial metadata, and closes the call afterward for [TerminalStage.AFTER_INITIAL_METADATA]. */
        override fun sendHeaders(headers: Metadata) {
            if (terminated.get()) return
            try {
                val outgoingHeaders = headers.withConfigured(configuration.initialMetadataList)
                val traceMetadata = outgoingHeaders.toMetadataEntries()
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_INITIAL_HEADERS, 1)
                val closeAfterHeaders = synchronized(lifecycleLock) {
                    if (terminated.get()) return
                    super.sendHeaders(outgoingHeaders)
                    initialHeadersSent = true
                    registry.recordEvent(
                        callId,
                        EventType.INITIAL_HEADERS_SENT,
                        metadata = traceMetadata,
                    )
                    configuration.terminalStage() == TerminalStage.AFTER_INITIAL_METADATA
                }
                if (closeAfterHeaders) closeConfiguredTerminal()
            } catch (error: Throwable) {
                fail(error)
            }
        }

        /** Closes the call after the configured response for [TerminalStage.AFTER_RESPONSE_MESSAGES]. */
        override fun sendMessage(message: RespT) {
            if (terminated.get()) return
            val occurrence = responseCount + 1
            try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_RESPONSE, occurrence)
                val closeAfterMessage = synchronized(lifecycleLock) {
                    if (terminated.get()) return
                    super.sendMessage(message)
                    responseCount = occurrence
                    registry.recordEvent(callId, EventType.RESPONSE_MESSAGE_SENT)
                    configuration.terminalStage() == TerminalStage.AFTER_RESPONSE_MESSAGES &&
                        responseCount == configuration.terminalBehavior.responseMessageCount
                }
                if (closeAfterMessage) closeConfiguredTerminal()
            } catch (error: Throwable) {
                fail(error)
            }
        }

        /**
         * Handles the service's own close, replacing its status when the scenario has a terminal behavior.
         *
         * A service that completes before the configured [TerminalStage.AFTER_RESPONSE_MESSAGES] count is reported
         * as an `INTERNAL` error, since the scenario does not match the call.
         */
        override fun close(status: Status, trailers: Metadata) {
            if (terminated.get()) return
            try {
                val terminalStage = configuration.terminalStage()
                // Sends headers explicitly, as grpc-java would otherwise close trailers-only and the configured
                // initial metadata would never reach the client.
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

        /**
         * Records an inbound event, then waits at the [barrier] for that event's occurrence.
         *
         * @return `true` when the event should be delivered to the service; `false` when the call has ended,
         * including while it was paused at the barrier.
         */
        fun recordRequestEvent(
            type: EventType,
            barrier: BarrierType,
            requestPayload: ByteString? = null,
        ): Boolean {
            if (terminated.get()) return false
            return try {
                val event = synchronized(lifecycleLock) {
                    if (terminated.get()) return false
                    if (type == EventType.REQUEST_MESSAGE_RECEIVED) {
                        registry.recordRequestMessage(callId, requestPayload)
                    } else {
                        registry.recordEvent(callId, type)
                    }
                }
                registry.awaitBarrierIfConfigured(callId, barrier, event.occurrence)
                !terminated.get()
            } catch (error: Throwable) {
                fail(error)
                false
            }
        }

        /** Closes the call with the status of [error], used when applying the scenario fails. */
        fun fail(error: Throwable) {
            fail(error.toGrpcStatus())
        }

        /**
         * Ends the call as cancelled by the client, unless it already ended.
         *
         * Called from both the context cancellation listener and [ServerCall.Listener.onCancel]; only the first one
         * records [EventType.CLIENT_CANCELLED].
         */
        fun clientCancelled() {
            synchronized(lifecycleLock) {
                if (!terminated.compareAndSet(false, true)) return
                runCatching { registry.clientCancelled(callId) }
            }
        }

        /** Closes with [status] without the configured trailing metadata, so the client sees a plain server error. */
        private fun fail(status: Status) {
            completeClose(status, Metadata(), includeConfiguredTrailers = false)
        }

        /** Closes with the status of the scenario's terminal behavior, keeping the trailers the service provided. */
        private fun closeConfiguredTerminal(serviceTrailers: Metadata = Metadata()) {
            val terminal = configuration.terminalBehavior
            val status = Status.fromCodeValue(terminal.status.code)
                .withDescription(terminal.status.description)
            completeClose(status, serviceTrailers)
        }

        /**
         * Waits at the [BarrierType.CLOSE_CALL] barrier, then closes the delegate and records
         * [EventType.CALL_CLOSED] with the status and trailers as sent.
         *
         * If the barrier wait fails, for example by timing out, the call closes with that failure's status instead.
         * The event is recorded even if closing the delegate throws.
         */
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
            val traceMetadata = outgoingTrailers.toMetadataEntries()
            val traceStatus = outgoingStatus.toTraceStatus()
            synchronized(lifecycleLock) {
                if (!terminated.compareAndSet(false, true)) return
                try {
                    super.close(outgoingStatus, outgoingTrailers)
                } finally {
                    recordClosed(traceMetadata, traceStatus)
                }
            }
        }

        private fun recordClosed(metadata: List<MetadataEntry>, status: GrpcStatus) {
            runCatching { registry.callClosed(callId, metadata, status) }
        }
    }

    /**
     * Applies the scenario to the inbound side of one call: records request messages and the half-close, holds them
     * at their barriers, and drops them if the call ended in the meantime.
     */
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

/** Returns a copy of this metadata with [entries] appended, keeping their order and duplicate keys. */
private fun Metadata.withConfigured(entries: List<MetadataEntry>): Metadata {
    return Metadata().also { result ->
        result.merge(this)
        entries.forEach { entry -> result.put(entry) }
    }
}

/** Adds [entry] as binary metadata for `-bin` keys and as ASCII metadata otherwise. */
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

/** Converts metadata to trace entries in serialization order, skipping HTTP/2 pseudo-headers such as `:path`. */
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

/** Payload body recorded with [EventType.REQUEST_MESSAGE_RECEIVED], or `null` for messages without one. */
private fun Any.interopPayload(): ByteString? = when (this) {
    is StreamingInputCallRequest -> payload.body
    is StreamingOutputCallRequest -> payload.body
    else -> null
}

/** Runs the cancellation listener on the thread that cancels the context, without waiting for the call executor. */
private val DIRECT_EXECUTOR: Executor = Executor { command -> command.run() }
