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
 * It disables grpc-java's automatic inbound demand so tests can grant requests explicitly, and pauses queued responses
 * while the client is unready. Readiness transitions are recorded in [CallScenarioRegistry] for deterministic tests.
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
    private val responseReadinessEnabled: Boolean =
        registry.configuration(callId).flowControl.respectResponseReadiness && resumeResponses != null
    private val readinessLock = Any()
    private var responseDeliveryBlocked: Boolean = false
    private var terminated: Boolean = false

    init {
        val behavior = registry.configuration(callId).flowControl
        if (behavior.manualInboundDemand && controlsInboundDemand) {
            responseObserver.disableAutoRequest()
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
     * @return `true` when readiness control is disabled or the transport currently accepts another response; `false`
     * when delivery must remain suspended or the call has terminated.
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
                if (registry.recordFlowControlEvent(callId, EventType.RESPONSE_DELIVERY_BLOCKED) == null) {
                    terminated = true
                    responseDeliveryBlocked = false
                    return false
                }
            }
            if (responseObserver.isReady) {
                recordReadyIfBlocked()
                return true
            }
            return false
        }
    }

    private fun onResponseReady() {
        val resumed = synchronized(readinessLock) {
            if (terminated) return@synchronized false
            recordReadyIfBlocked()
        }
        if (resumed) resumeResponses?.invoke()
    }

    private fun onCallCancelled() {
        onCallClosed()
        cancelResponses?.invoke()
    }

    private fun onCallClosed() {
        synchronized(readinessLock) {
            terminated = true
            responseDeliveryBlocked = false
        }
    }

    private fun recordReadyIfBlocked(): Boolean {
        if (!responseDeliveryBlocked) return false
        responseDeliveryBlocked = false
        if (registry.recordFlowControlEvent(callId, EventType.RESPONSE_DELIVERY_READY) == null) {
            terminated = true
            return false
        }
        return true
    }

    internal companion object {
        /**
         * Creates support for the scenario attached to the current grpc-java context.
         *
         * Returns `null` for ordinary calls and scenarios without applicable flow control. This must be called during the
         * initial service method while grpc-java still permits observer configuration.
         *
         * @param registry Registry containing the selected scenario.
         * @param responseObserver Observer supplied to the generated service method.
         * @param controlsInboundDemand Whether the method accepts streaming requests.
         * @param resumeResponses Callback that resumes a readiness-aware response dispatcher.
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
