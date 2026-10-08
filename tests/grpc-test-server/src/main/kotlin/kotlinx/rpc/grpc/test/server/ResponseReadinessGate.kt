/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import kxrpc.testing.EventType

/**
 * Creates a gate for the scenario attached to the current grpc-java context.
 *
 * Must be called from the service method itself, because grpc-java rejects the handler setup once that
 * method returns.
 *
 * @param registry Registry containing the selected scenario.
 * @param responseObserver Observer supplied to the generated service method.
 * @param resumeResponses Restarts the dispatcher after a pause ended.
 * @param cancelResponses Drops the dispatcher's queued responses after the client cancelled.
 * @return The gate, or `null` for calls without a scenario or without response readiness.
 */
internal fun ResponseReadinessGate.Companion.createIfConfigured(
    registry: CallScenarioRegistry,
    responseObserver: StreamObserver<*>,
    resumeResponses: () -> Unit,
    cancelResponses: () -> Unit,
): ResponseReadinessGate? {
    val callId = TEST_CALL_ID_CONTEXT_KEY.get() ?: return null
    if (!registry.configuration(callId).flowControl.respectResponseReadiness) return null
    val serverObserver = responseObserver as? ServerCallStreamObserver<*>
        ?: error("grpc-java did not supply a ServerCallStreamObserver for scenario '$callId'")
    return ResponseReadinessGate(
        registry = registry,
        callId = callId,
        responseObserver = serverObserver,
        resumeResponses = resumeResponses,
        cancelResponses = cancelResponses,
    )
}


/**
 * Holds back a call's queued responses while grpc-java reports the client as not ready, for scenarios that enable
 * `flow_control.respect_response_readiness`.
 *
 * The gate lets client tests verify receive-side backpressure. When the consumer of a client under test is slow, the
 * client should stop asking for more responses instead of buffering everything. A regular grpc-java server cannot
 * show whether it does: `onNext` never blocks, so the server keeps sending, and responses the client does not take
 * pile up in the server's buffers. A client that eagerly reads everything then looks the same as one that applies
 * backpressure. With the gate, the server stops sending once the client's backpressure reaches it and records
 * [EventType.RESPONSE_DELIVERY_BLOCKED], which proves exactly that. A test can then let the client consume again,
 * wait for [EventType.RESPONSE_DELIVERY_READY], and check that no response is lost or reordered, or that cancelling
 * a call while it is held back works.
 *
 * To notice the client's backpressure, the gate relies on grpc-java's readiness. The client never signals readiness
 * itself. grpc-java derives it from HTTP/2 flow control: the client refills its receive window only as it consumes
 * messages, so once it stops, the window is used up and response bytes queue on the server. `isReady()` turns
 * `false` when more than a threshold, 32 KiB by default, is queued. Once the queue drains below it again,
 * `isReady()` turns `true` and grpc-java calls the ready handler.
 *
 * grpc-java has no callback for becoming not ready, so the gate polls instead: the response dispatcher of
 * [InteropTestService] calls [canDeliverResponse] before each response message, which checks `isReady()`, and stops
 * while the answer is `false`. The ready callback restarts it through [resumeResponses].
 *
 * Delivery moves through these states:
 *
 * ```
 * FLOWING   --a check finds the client not ready--------------> PAUSED
 * PAUSED    --ready callback, or a later check finds it ready--> FLOWING
 * any state --call closed or cancelled------------------------> ENDED
 * ```
 *
 * Entering `PAUSED` records [EventType.RESPONSE_DELIVERY_BLOCKED] and leaving it records
 * [EventType.RESPONSE_DELIVERY_READY], once per pause however often delivery is attempted during it. As the client
 * is checked only when a response is due, `BLOCKED` means that the server wanted to send and could not. A client
 * that is not ready only briefly between two responses leaves no trace. grpc-java may also call the ready handler
 * when the client is no longer ready by the time it runs, so the next check starts another pause. A test may
 * therefore observe more than one `BLOCKED`/`READY` pair and should await specific occurrences.
 *
 * [canDeliverResponse] runs on the dispatcher's thread, while grpc-java calls the ready, cancel, and close handlers
 * on the call's executor, so [state] is guarded by [lock]. The dispatcher callbacks are invoked outside [lock].
 *
 * @param registry Owns the scenario configuration and trace.
 * @param callId Identifies the configured scenario selected by the data-plane call metadata.
 * @param responseObserver The grpc-java observer whose readiness is respected.
 * @param resumeResponses Restarts the dispatcher after a pause ended.
 * @param cancelResponses Drops the dispatcher's queued responses after the client cancelled.
 */
internal class ResponseReadinessGate(
    private val registry: CallScenarioRegistry,
    private val callId: String,
    private val responseObserver: ServerCallStreamObserver<*>,
    private val resumeResponses: () -> Unit,
    private val cancelResponses: () -> Unit,
) {
    private enum class State {
        /** Responses are delivered as long as each check finds the client ready. */
        FLOWING,

        /** A check found the client not ready and [EventType.RESPONSE_DELIVERY_BLOCKED] was recorded. */
        PAUSED,

        /** The call closed or was cancelled, or the registry no longer sees it as active. Nothing is delivered. */
        ENDED,
    }

    private val lock = Any()
    private var state: State = State.FLOWING

    init {
        responseObserver.setOnReadyHandler(::onReady)
        responseObserver.setOnCancelHandler(::onCancel)
        responseObserver.setOnCloseHandler(::onClose)
    }

    /**
     * Checks whether the next queued response message may be sent, pausing delivery if the client is not ready.
     *
     * This is the only place where a client that is not ready is noticed, as grpc-java offers no callback for it.
     * Only response messages are checked. Completing the call is never held back.
     *
     * @return `true` when `isReady()` reports the client as ready. `false` when delivery is paused or the call has
     * ended, after which the dispatcher stops and waits for [resumeResponses].
     */
    fun canDeliverResponse(): Boolean = synchronized(lock) {
        if (state == State.ENDED) return false
        if (!responseObserver.isReady) {
            if (state == State.FLOWING) pauseLocked()
            // Readiness may have changed while the pause was recorded. If so, deliver now rather than rely on a
            // later ready callback.
            if (state == State.ENDED || !responseObserver.isReady) return false
        }
        resumeLocked()
        state != State.ENDED
    }

    /**
     * grpc-java's ready callback: ends the current pause and restarts the dispatcher.
     *
     * grpc-java calls it when the queued bytes drop below the threshold, and once when the stream is set up. Outside
     * a pause, it does nothing.
     */
    private fun onReady() {
        val resumed = synchronized(lock) { resumeLocked() }
        if (resumed) resumeResponses()
    }

    /** Ends delivery and drops the queued responses, as the client will never receive them. */
    private fun onCancel() {
        onClose()
        cancelResponses()
    }

    /** Ends delivery once grpc-java has closed the call. */
    private fun onClose() {
        synchronized(lock) { state = State.ENDED }
    }

    /** Enters [State.PAUSED] from [State.FLOWING]. Must be called holding [lock]. */
    private fun pauseLocked() {
        state = State.PAUSED
        if (!recordEvent(EventType.RESPONSE_DELIVERY_BLOCKED)) state = State.ENDED
    }

    /**
     * Leaves [State.PAUSED] for [State.FLOWING], if delivery is paused. Must be called holding [lock].
     *
     * @return `true` when a pause ended, so the dispatcher must be restarted.
     */
    private fun resumeLocked(): Boolean {
        if (state != State.PAUSED) return false
        state = State.FLOWING
        if (!recordEvent(EventType.RESPONSE_DELIVERY_READY)) state = State.ENDED
        return state == State.FLOWING
    }

    /** Records [type], returning `false` when the registry has already recorded the end of the call. */
    private fun recordEvent(type: EventType): Boolean = registry.recordFlowControlEvent(callId, type) != null

    companion object
}
