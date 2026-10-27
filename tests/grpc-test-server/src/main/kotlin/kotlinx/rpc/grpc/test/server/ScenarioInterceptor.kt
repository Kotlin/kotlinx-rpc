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
import io.grpc.testing.integration.Messages.SimpleRequest
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

/** Request metadata key with which a data-plane call selects its scenario. The value is the call ID. */
internal val TEST_CALL_ID_METADATA_KEY: Metadata.Key<String> =
    Metadata.Key.of(TEST_CALL_ID_METADATA_KEY_NAME, Metadata.ASCII_STRING_MARSHALLER)

/**
 * Context key that gives the call ID of the scenario to service implementations, for example to
 * [ResponseReadinessGate]. [ScenarioInterceptor] sets it only for calls that have [TEST_CALL_ID_METADATA_KEY].
 */
internal val TEST_CALL_ID_CONTEXT_KEY: Context.Key<String> = Context.key(TEST_CALL_ID_METADATA_KEY_NAME)

/**
 * Applies the configured scenario to each data-plane call that has [TEST_CALL_ID_METADATA_KEY]. For such a call, the
 * interceptor does these steps:
 *
 * - Reports the lifecycle of the call to [registry]: headers, messages, half-close, cancellation, and close.
 * - Pauses the call at the barriers of the scenario.
 * - Adds the configured metadata.
 * - Ends the call early if the scenario has a terminal behavior.
 *
 * The interceptor doesn't change calls without the key.
 *
 * The interceptor rejects these calls, and they don't reach the service:
 *
 * - More than one call ID, or a blank call ID: `INVALID_ARGUMENT`.
 * - The call ID of an unknown or discarded scenario: the status of the failed [CallScenarioRegistry.callAccepted].
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
        if (callIds.isEmpty()) {
            return next.startCall(call, headers)
        }

        if (callIds.size != 1 || callIds.single().isBlank()) {
            return reject(
                call,
                "metadata '$TEST_CALL_ID_METADATA_KEY_NAME' doesn't have exactly one non-blank value. " +
                    "Send one call ID in it",
            )
        }

        val callId = callIds.single()
        // CALL_ACCEPTED is the first record, so the trace starts before anything else can happen to the call.
        try {
            registry.callAccepted(callId, headers.toMetadataEntries())
        } catch (error: Exception) {
            return reject(call, error.toGrpcStatus())
        }

        val scenarioCall = ScenarioServerCall(call, registry, registry.configuration(callId))
        val scenarioContext = Context.current().withValue(TEST_CALL_ID_CONTEXT_KEY, callId)
        // grpc-java cancels the context of the call at once on a separate executor, but ServerCall.Listener.onCancel
        // waits for the serialized call executor. A request barrier can block that executor, so this listener records
        // a cancellation while the call waits at the barrier.
        scenarioContext.addListener(
            { scenarioCall.clientCancelled() },
            DIRECT_EXECUTOR,
        )
        if (scenarioCall.closeBeforeStartIfConfigured()) {
            return object : ServerCall.Listener<ReqT>() {}
        }
        val listener = try {
            Contexts.interceptCall(scenarioContext, scenarioCall, headers, next)
        } catch (error: Exception) {
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
     * Applies the scenario to the outbound side of one call: headers, response messages, and close.
     *
     * Each operation waits at its barrier first. Then, while it holds [lifecycleLock], it forwards to the delegate and
     * records the matching event. The wait is outside the lock, so [clientCancelled] can record a cancellation while
     * the call waits at the barrier. The lock makes the order of the trace match the order of the operations on the
     * call.
     *
     * The call ends exactly once, through [completeClose] or [clientCancelled], which set [terminated]. The call
     * ignores every later operation, so the trace has one [EventType.CALL_CLOSED] or [EventType.CLIENT_CANCELLED].
     *
     * If the scenario logic fails, the call closes with the status of the failure, and the exception doesn't
     * propagate.
     */
    private class ScenarioServerCall<ReqT : Any, RespT : Any>(
        delegate: ServerCall<ReqT, RespT>,
        private val registry: CallScenarioRegistry,
        private val configuration: ConfigureScenarioRequest,
    ) : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(delegate) {
        private val callId: String = configuration.callId

        /** The number of response messages sent on this call. It selects the [BarrierType.SEND_RESPONSE] occurrence. */
        private var responseCount: Int = 0
        private var initialHeadersSent: Boolean = false

        /**
         * Set once when the call closes or the client cancels it. Each operation checks it again under
         * [lifecycleLock] after its wait.
         */
        private val terminated = AtomicBoolean()

        /**
         * Held while an operation forwards to the delegate and records its event, and while the call ends, so these
         * steps don't interleave.
         */
        private val lifecycleLock = Any()

        /**
         * Closes the call with the configured status if the terminal stage of the scenario is
         * [TerminalStage.BEFORE_INITIAL_METADATA].
         *
         * @return `true` if the call closed, so the caller must not start the service.
         */
        fun closeBeforeStartIfConfigured(): Boolean {
            if (configuration.terminalStage() != TerminalStage.BEFORE_INITIAL_METADATA) {
                return false
            }
            closeConfiguredTerminal()
            return true
        }

        /**
         * Adds the configured initial metadata. For [TerminalStage.AFTER_INITIAL_METADATA], closes the call after the
         * headers.
         */
        override fun sendHeaders(headers: Metadata) {
            if (terminated.get()) {
                return
            }
            try {
                val outgoingHeaders = headers.withConfigured(configuration.initialMetadataList)
                val traceMetadata = outgoingHeaders.toMetadataEntries()
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_INITIAL_HEADERS, 1)
                val closeAfterHeaders = synchronized(lifecycleLock) {
                    if (terminated.get()) {
                        return
                    }
                    super.sendHeaders(outgoingHeaders)
                    initialHeadersSent = true
                    registry.recordEvent(
                        callId,
                        EventType.INITIAL_HEADERS_SENT,
                        metadata = traceMetadata,
                    )
                    configuration.terminalStage() == TerminalStage.AFTER_INITIAL_METADATA
                }
                if (closeAfterHeaders) {
                    closeConfiguredTerminal()
                }
            } catch (error: Exception) {
                fail(error)
            }
        }

        /**
         * For [TerminalStage.AFTER_RESPONSE_MESSAGES], closes the call after the configured number of response
         * messages.
         */
        override fun sendMessage(message: RespT) {
            if (terminated.get()) {
                return
            }
            val occurrence = responseCount + 1
            try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.SEND_RESPONSE, occurrence)
                val closeAfterMessage = synchronized(lifecycleLock) {
                    if (terminated.get()) {
                        return
                    }
                    super.sendMessage(message)
                    responseCount = occurrence
                    registry.recordEvent(callId, EventType.RESPONSE_MESSAGE_SENT)
                    configuration.terminalStage() == TerminalStage.AFTER_RESPONSE_MESSAGES &&
                        responseCount == configuration.terminalBehavior.responseMessageCount
                }
                if (closeAfterMessage) {
                    closeConfiguredTerminal()
                }
            } catch (error: Exception) {
                fail(error)
            }
        }

        /**
         * Handles the close from the service. If the scenario has a terminal behavior, its status replaces the status
         * of the service.
         *
         * If the service completes before the response count of [TerminalStage.AFTER_RESPONSE_MESSAGES], the call
         * fails with `INTERNAL`, because the scenario doesn't match the call.
         */
        override fun close(status: Status, trailers: Metadata) {
            if (terminated.get()) {
                return
            }
            try {
                val terminalStage = configuration.terminalStage()
                // Without explicit headers, grpc-java sends a trailers-only response, and the configured initial
                // metadata doesn't reach the client.
                if (!initialHeadersSent &&
                    (configuration.initialMetadataCount > 0 || terminalStage == TerminalStage.AFTER_INITIAL_METADATA)
                ) {
                    sendHeaders(Metadata())
                    if (terminated.get()) {
                        return
                    }
                }

                when (terminalStage) {
                    null -> completeClose(status, trailers)
                    TerminalStage.AFTER_SERVICE_COMPLETION -> closeConfiguredTerminal(trailers)
                    TerminalStage.AFTER_RESPONSE_MESSAGES -> fail(
                        Status.INTERNAL.withDescription(
                            "service completed after $responseCount responses, before response_message_count " +
                                "${configuration.terminalBehavior.responseMessageCount}"
                        )
                    )
                    TerminalStage.BEFORE_INITIAL_METADATA,
                    TerminalStage.AFTER_INITIAL_METADATA,
                    -> closeConfiguredTerminal(trailers)
                    TerminalStage.TERMINAL_STAGE_UNSPECIFIED,
                    TerminalStage.UNRECOGNIZED,
                    -> error("validated terminal stage became invalid")
                }
            } catch (error: Exception) {
                fail(error)
            }
        }

        /**
         * Records an inbound event, then waits at [barrier] for the occurrence of that event.
         *
         * @return `true` if the listener must deliver the event to the service. `false` if the call has ended, also
         * during the wait at the barrier.
         */
        fun recordRequestEvent(
            type: EventType,
            barrier: BarrierType,
            requestPayload: ByteString? = null,
        ): Boolean {
            if (terminated.get()) {
                return false
            }
            return try {
                val event = synchronized(lifecycleLock) {
                    if (terminated.get()) {
                        return false
                    }
                    if (type == EventType.REQUEST_MESSAGE_RECEIVED) {
                        registry.recordRequestMessage(callId, requestPayload)
                    } else {
                        registry.recordEvent(callId, type)
                    }
                }
                registry.awaitBarrierIfConfigured(callId, barrier, event.occurrence)
                !terminated.get()
            } catch (error: Exception) {
                fail(error)
                false
            }
        }

        /** Closes the call with the status of [error] after the scenario logic fails. */
        fun fail(error: Exception) {
            fail(error.toGrpcStatus())
        }

        /**
         * Ends the call as cancelled by the client, if the call didn't end already.
         *
         * The context cancellation listener and [ServerCall.Listener.onCancel] both call it. Only the first call
         * records [EventType.CLIENT_CANCELLED].
         */
        fun clientCancelled() {
            synchronized(lifecycleLock) {
                if (!terminated.compareAndSet(false, true)) {
                    return
                }
                runCatching { registry.clientCancelled(callId) }.throwIfError()
            }
        }

        /**
         * Closes with [status] and without the configured trailing metadata, so the client gets a plain server
         * error.
         */
        private fun fail(status: Status) {
            completeClose(status, Metadata(), includeConfiguredTrailers = false)
        }

        /**
         * Closes with the status of the terminal behavior. The trailers are [serviceTrailers] and the configured
         * trailing metadata.
         */
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
         * If the wait at the barrier fails, for example after a timeout, the call closes with the status of that
         * failure. The function records the event also if the close of the delegate throws.
         */
        private fun completeClose(
            status: Status,
            trailers: Metadata,
            includeConfiguredTrailers: Boolean = true,
        ) {
            if (terminated.get()) {
                return
            }
            val (outgoingStatus, outgoingTrailers) = try {
                registry.awaitBarrierIfConfigured(callId, BarrierType.CLOSE_CALL, 1)
                status to if (includeConfiguredTrailers) {
                    trailers.withConfigured(configuration.trailingMetadataList)
                } else {
                    trailers
                }
            } catch (error: Exception) {
                error.toGrpcStatus() to Metadata()
            }
            val traceMetadata = outgoingTrailers.toMetadataEntries()
            val traceStatus = outgoingStatus.toTraceStatus()
            synchronized(lifecycleLock) {
                if (!terminated.compareAndSet(false, true)) {
                    return
                }
                try {
                    super.close(outgoingStatus, outgoingTrailers)
                } finally {
                    recordClosed(traceMetadata, traceStatus)
                }
            }
        }

        private fun recordClosed(metadata: List<MetadataEntry>, status: GrpcStatus) {
            runCatching { registry.callClosed(callId, metadata, status) }.throwIfError()
        }
    }

    /**
     * Applies the scenario to the inbound side of one call. For each request message and for the half-close, the
     * listener does these steps:
     *
     * 1. Records the event.
     * 2. Holds the event at its barrier.
     * 3. Drops the event if the call ended in the meantime.
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
    return if (hasTerminalBehavior()) {
        terminalBehavior.stage
    } else {
        null
    }
}

/** Returns a copy of this metadata with [entries] added at the end, in their order and with duplicate keys. */
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

/** Converts metadata to trace entries in serialization order. Skips HTTP/2 pseudo-headers such as `:path`. */
private fun Metadata.toMetadataEntries(): List<MetadataEntry> {
    val serialized = InternalMetadata.serialize(this)
    return buildList(serialized.size / 2) {
        for (index in serialized.indices step 2) {
            val key = String(serialized[index], US_ASCII)
            if (key.startsWith(':')) {
                continue
            }
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

/** The payload body to record with [EventType.REQUEST_MESSAGE_RECEIVED], or `null` for messages without a payload. */
private fun Any.interopPayload(): ByteString? = when (this) {
    is SimpleRequest -> payload.body
    is StreamingInputCallRequest -> payload.body
    is StreamingOutputCallRequest -> payload.body
    else -> null
}

/** Runs the cancellation listener on the thread that cancels the context, so it doesn't wait for the call executor. */
private val DIRECT_EXECUTOR: Executor = Executor { command -> command.run() }

private fun <T> Result<T>.throwIfError() = exceptionOrNull()?.let {
    if (it is Error) {
        throw it
    }
}
