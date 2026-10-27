/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import kxrpc.testing.EventType

/**
 * Creates a gate for the scenario that [TEST_CALL_ID_CONTEXT_KEY] selects in the current grpc-java context.
 *
 * Call it from the service method itself, because grpc-java rejects the handler setup after that method returns.
 *
 * @param responseObserver The observer that grpc-java gives to the service method.
 * @return The gate, or `null` if the call has no scenario or the scenario doesn't enable response readiness.
 */
internal fun ResponseReadinessGate.Companion.createIfConfigured(
    registry: CallScenarioRegistry,
    responseObserver: StreamObserver<*>,
    resumeResponses: () -> Unit,
    cancelResponses: () -> Unit,
): ResponseReadinessGate? {
    val callId = TEST_CALL_ID_CONTEXT_KEY.get() ?: return null
    if (!registry.configuration(callId).flowControl.respectResponseReadiness) {
        return null
    }
    val serverObserver = responseObserver as? ServerCallStreamObserver<*>
        ?: error("the response observer of scenario '$callId' isn't a ServerCallStreamObserver")
    return ResponseReadinessGate(
        registry = registry,
        callId = callId,
        responseObserver = serverObserver,
        resumeResponses = resumeResponses,
        cancelResponses = cancelResponses,
    )
}


/**
 * Holds back the queued responses of a call while grpc-java reports that the client isn't ready, for scenarios that
 * enable `flow_control.respect_response_readiness`.
 *
 * A regular server can't show whether the client under test has receive-side backpressure. `onNext` never blocks,
 * so the server buffers the responses that the client doesn't take.
 *
 * grpc-java has no "not ready" callback, so the dispatcher of [InteropTestService] calls [canDeliverResponse] before
 * each response message. The ready handler restarts the dispatcher through [resumeResponses]. Consequences for the
 * trace:
 *
 * - The gate checks only when a response is due. A short "not ready" time between two responses isn't in the trace.
 * - grpc-java can call the ready handler after the client isn't ready again. A test can see more than one
 *   `BLOCKED`/`READY` pair and must wait for the occurrence that it expects.
 *
 * [canDeliverResponse] and the grpc-java handlers can run on different threads, so code reads and writes [state] only
 * while it holds [lock]. The dispatcher holds its own monitor when it calls [canDeliverResponse], so the gate calls
 * [resumeResponses] and [cancelResponses] outside [lock].
 *
 * @param responseObserver The observer whose readiness the gate checks. The gate sets its ready, cancel, and close
 * handlers.
 * @param resumeResponses Restarts the dispatcher after a pause.
 * @param cancelResponses Drops the queued responses of the dispatcher after the client cancels.
 */
internal class ResponseReadinessGate(
    private val registry: CallScenarioRegistry,
    private val callId: String,
    private val responseObserver: ServerCallStreamObserver<*>,
    private val resumeResponses: () -> Unit,
    private val cancelResponses: () -> Unit,
) {
    private enum class State {
        /** The dispatcher sends a response each time a check finds the client ready. */
        FLOWING,

        /** A check found the client not ready, and the gate recorded [EventType.RESPONSE_DELIVERY_BLOCKED]. */
        PAUSED,

        /**
         * The call closed, the client cancelled it, or the registry has no active call for it. The gate holds back
         * every response.
         */
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
     * Returns whether the dispatcher can send the next queued response message. If the client isn't ready, the gate
     * pauses delivery.
     *
     * Call it only before a response message, so the gate never holds back the completion of the call.
     *
     * @return `true` if `isReady()` reports the client as ready. `false` if the gate paused delivery or the call has
     * ended. After `false`, the dispatcher stops and waits for [resumeResponses].
     */
    fun canDeliverResponse(): Boolean = synchronized(lock) {
        if (state == State.ENDED) {
            return false
        }
        if (!responseObserver.isReady) {
            if (state == State.FLOWING) {
                pauseUnsafe()
            }
            // Readiness can change while the gate records the pause. If the client is ready again, the gate lets
            // the response through now and doesn't wait for a later ready callback.
            if (state == State.ENDED || !responseObserver.isReady) {
                return false
            }
        }
        resumeUnsafe()
        state != State.ENDED
    }

    /**
     * The ready handler. Ends the current pause and restarts the dispatcher.
     *
     * grpc-java also calls it once when the stream starts. Outside a pause, it does nothing.
     */
    private fun onReady() {
        val resumed = synchronized(lock) { resumeUnsafe() }
        if (resumed) {
            resumeResponses()
        }
    }

    /** Ends delivery and drops the queued responses of the dispatcher. */
    private fun onCancel() {
        onClose()
        cancelResponses()
    }

    /** Ends delivery after grpc-java closes the call. */
    private fun onClose() {
        synchronized(lock) { state = State.ENDED }
    }

    /** Changes the state from [State.FLOWING] to [State.PAUSED]. The caller must hold [lock]. */
    private fun pauseUnsafe() {
        state = State.PAUSED
        if (!recordEvent(EventType.RESPONSE_DELIVERY_BLOCKED)) {
            state = State.ENDED
        }
    }

    /**
     * Changes the state from [State.PAUSED] to [State.FLOWING]. Does nothing outside a pause. The caller must hold
     * [lock].
     *
     * @return `true` if a pause ended, so the caller must restart the dispatcher.
     */
    private fun resumeUnsafe(): Boolean {
        if (state != State.PAUSED) {
            return false
        }
        state = State.FLOWING
        if (!recordEvent(EventType.RESPONSE_DELIVERY_READY)) {
            state = State.ENDED
        }
        return state == State.FLOWING
    }

    /** Records [type]. Returns `false` if the registry has already recorded the end of the call. */
    private fun recordEvent(type: EventType): Boolean = registry.recordFlowControlEvent(callId, type) != null

    companion object
}
