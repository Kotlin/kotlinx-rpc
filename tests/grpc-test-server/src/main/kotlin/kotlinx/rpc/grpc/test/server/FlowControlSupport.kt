/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import kxrpc.testing.EventType

/**
 * Applies the reference server's opt-in flow-control behavior to one configured data-plane call.
 *
 * The scenario's `flow_control` enables two independent behaviors:
 *
 * - **Manual inbound demand.** grpc-java normally requests the next request message on its own, so the service reads
 *   as fast as the client sends. With manual demand, automatic requests are disabled and the server reads a message
 *   only after the test grants demand through `GrantInboundDemand`. Unread messages stay in the transport, so a test
 *   can observe how the client behaves when the server stops reading.
 * - **Response readiness.** A grpc-java service may send responses even while the transport reports the client as
 *   not ready, in which case they are buffered. With readiness respected, the response dispatcher asks
 *   [canDeliverResponse] before each response and stops while the client is not ready. grpc-java's ready callback
 *   then lets it continue. Each pause is recorded as an [EventType.RESPONSE_DELIVERY_BLOCKED] event followed by an
 *   [EventType.RESPONSE_DELIVERY_READY] event, so tests can wait for the server to actually be held back.
 *
 * Setup must happen during service-handler initialization because grpc-java freezes the observer afterward.
 *
 * @param registry Owns the scenario configuration, demand endpoint, trace, and terminal state.
 * @param callId Identifies the configured scenario selected by the data-plane call metadata.
 * @param responseObserver The grpc-java observer used for inbound demand and outbound readiness.
 * @param controlsInboundDemand Whether this RPC shape accepts a stream of request messages.
 * @param resumeResponses Reschedules queued response delivery after the transport becomes ready.
 * @param cancelResponses Clears queued response delivery when the client cancels the call.
 */
internal class FlowControlSupport private constructor(
    private val registry: CallScenarioRegistry,
    private val callId: String,
    private val responseObserver: ServerCallStreamObserver<*>,
    controlsInboundDemand: Boolean,
    private val resumeResponses: (() -> Unit)?,
    private val cancelResponses: (() -> Unit)?,
) {
    /** Readiness only applies to methods that queue their responses, which is what [resumeResponses] signals. */
    private val responseReadinessEnabled: Boolean =
        registry.configuration(callId).flowControl.respectResponseReadiness && resumeResponses != null

    /**
     * Guards the state below. Readiness is checked on the response dispatcher's thread, while grpc-java calls the
     * ready, cancel, and close handlers on the call's executor.
     */
    private val readinessLock = Any()

    /**
     * `true` between a recorded [EventType.RESPONSE_DELIVERY_BLOCKED] and its [EventType.RESPONSE_DELIVERY_READY],
     * so that each pause is recorded once, however often delivery is attempted during it.
     */
    private var responseDeliveryBlocked: Boolean = false

    /**
     * Set once the call has closed or been cancelled, or the registry reports that it is no longer active. After
     * that, no response is delivered and no readiness event is recorded.
     */
    private var terminated: Boolean = false

    init {
        val behavior = registry.configuration(callId).flowControl
        if (behavior.manualInboundDemand && controlsInboundDemand) {
            responseObserver.disableAutoRequest()
            // GrantInboundDemand reaches responseObserver.request(n) through the registry, which also forwards
            // demand granted before this point.
            registry.registerInboundDemand(callId, responseObserver::request)
        }
        if (responseReadinessEnabled) {
            responseObserver.setOnReadyHandler(::onResponseReady)
            responseObserver.setOnCancelHandler(::onCallCancelled)
            responseObserver.setOnCloseHandler(::onCallClosed)
        }
    }

    /**
     * Checks whether the next queued response may be sent.
     *
     * An unready check records [EventType.RESPONSE_DELIVERY_BLOCKED]. The ready callback records
     * [EventType.RESPONSE_DELIVERY_READY] and resumes queued delivery.
     *
     * Callers check this only before response messages. Completing the call is never held back.
     *
     * @return `true` when readiness control is disabled or the transport currently accepts another response; `false`
     * when delivery must remain suspended or the call has terminated. After `false`, the caller stops and waits for
     * [resumeResponses].
     */
    fun canDeliverResponse(): Boolean {
        if (!responseReadinessEnabled) return true
        synchronized(readinessLock) {
            if (terminated) return false
            if (responseObserver.isReady) {
                recordReadyIfBlocked()
                return true
            }
            if (!responseDeliveryBlocked) {
                responseDeliveryBlocked = true
                // A null event means the registry has already recorded the end of the call.
                if (registry.recordFlowControlEvent(callId, EventType.RESPONSE_DELIVERY_BLOCKED) == null) {
                    terminated = true
                    responseDeliveryBlocked = false
                    return false
                }
            }
            // Readiness may have changed while the event was recorded. If so, record the matching READY and deliver
            // now rather than rely on a later ready callback.
            if (responseObserver.isReady) {
                recordReadyIfBlocked()
                return true
            }
            return false
        }
    }

    /** grpc-java's ready callback: ends a recorded pause and lets the dispatcher continue. */
    private fun onResponseReady() {
        val resumed = synchronized(readinessLock) {
            if (terminated) return@synchronized false
            recordReadyIfBlocked()
        }
        if (resumed) resumeResponses?.invoke()
    }

    /** Stops delivery and drops the responses still queued, as the client will never receive them. */
    private fun onCallCancelled() {
        onCallClosed()
        cancelResponses?.invoke()
    }

    /** Stops delivery once grpc-java has closed the call. */
    private fun onCallClosed() {
        synchronized(readinessLock) {
            terminated = true
            responseDeliveryBlocked = false
        }
    }

    /**
     * Records [EventType.RESPONSE_DELIVERY_READY] if a pause is in progress. Must be called holding [readinessLock].
     *
     * @return `true` when a pause ended, so the dispatcher must be resumed; `false` when there was no pause or the
     * call is no longer active.
     */
    private fun recordReadyIfBlocked(): Boolean {
        if (!responseDeliveryBlocked) return false
        responseDeliveryBlocked = false
        if (registry.recordFlowControlEvent(callId, EventType.RESPONSE_DELIVERY_READY) == null) {
            terminated = true
            return false
        }
        return true
    }

    companion object {
        /**
         * Creates support for the scenario attached to the current grpc-java context.
         *
         * Returns `null` for ordinary calls and scenarios without applicable flow control. This must be called during
         * the initial service method while grpc-java still permits observer configuration.
         *
         * For manual inbound demand alone, the caller may discard the result: the setup done on creation is all the
         * call needs.
         *
         * @param registry Registry containing the selected scenario.
         * @param responseObserver Observer supplied to the generated service method.
         * @param controlsInboundDemand Whether the method accepts streaming requests.
         * @param resumeResponses Callback that resumes a readiness-aware response dispatcher. Methods that send no
         * queued responses pass `null`, which disables readiness control for them.
         * @param cancelResponses Callback that clears queued responses after client cancellation.
         * @return Flow-control support for the configured call, or `null` when the call needs no special handling.
         */
        fun create(
            registry: CallScenarioRegistry,
            responseObserver: StreamObserver<*>,
            controlsInboundDemand: Boolean,
            resumeResponses: (() -> Unit)? = null,
            cancelResponses: (() -> Unit)? = null,
        ): FlowControlSupport? {
            val callId = TEST_CALL_ID_CONTEXT_KEY.get() ?: return null
            val behavior = registry.configuration(callId).flowControl
            val needsInboundControl = behavior.manualInboundDemand && controlsInboundDemand
            val needsReadinessControl = behavior.respectResponseReadiness && resumeResponses != null
            if (!needsInboundControl && !needsReadinessControl) return null
            val serverObserver = responseObserver as? ServerCallStreamObserver<*>
                ?: error("grpc-java did not supply a ServerCallStreamObserver for scenario '$callId'")
            return FlowControlSupport(
                registry = registry,
                callId = callId,
                responseObserver = serverObserver,
                controlsInboundDemand = controlsInboundDemand,
                resumeResponses = resumeResponses,
                cancelResponses = cancelResponses,
            )
        }
    }
}
