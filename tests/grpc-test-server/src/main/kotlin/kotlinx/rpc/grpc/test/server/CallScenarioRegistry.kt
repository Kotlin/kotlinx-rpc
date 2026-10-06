/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import com.google.protobuf.ByteString
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.CallEvent
import kxrpc.testing.CallTrace
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.GrpcStatus
import kxrpc.testing.MetadataEntry
import kxrpc.testing.MalformedResponseCardinality
import kxrpc.testing.ScenarioDiagnostics
import kxrpc.testing.TerminalStage

/**
 * Server-side state of the scenarios configured through [GrpcClientControlService].
 *
 * A test configures a scenario under a `call_id`, then starts a data-plane call carrying that id in
 * the `kxrpc-test-call-id` metadata. [InteropMetadataInterceptor] reports the call's lifecycle here,
 * which records it as an ordered trace and blocks it at configured barriers until the test releases
 * them. All blocking operations give up after [waitTimeout] so a broken test cannot hang the server.
 */
internal class CallScenarioRegistry(
    private val waitTimeout: Duration = Duration.ofSeconds(10),
) {
    private val scenarios = ConcurrentHashMap<String, ScenarioState>()

    fun configure(request: ConfigureScenarioRequest) {
        require(request.callId.isNotBlank()) { "call_id must not be blank" }
        validateMetadata(request.initialMetadataList)
        validateMetadata(request.trailingMetadataList)
        validateTerminalBehavior(request)
        require(request.malformedResponseCardinality != MalformedResponseCardinality.UNRECOGNIZED) {
            "malformed response cardinality must be recognized"
        }

        val barriers = request.barriersList.map { barrier -> barrierKey(barrier.type, barrier.occurrence) }.toSet()

        require(barriers.size == request.barriersCount) {
            "scenario '${request.callId}' contains duplicate barriers"
        }

        check(scenarios.putIfAbsent(request.callId, ScenarioState(request, barriers)) == null) {
            "scenario '${request.callId}' is already configured"
        }
    }

    fun configuration(callId: String): ConfigureScenarioRequest = scenario(callId).configuration()

    fun recordEvent(
        callId: String,
        type: EventType,
        metadata: List<MetadataEntry> = emptyList(),
        status: GrpcStatus? = null,
    ): CallEvent {
        requireKnownEvent(type)
        return scenario(callId).recordEvent(type, metadata = metadata, status = status)
    }

    fun recordRequestMessage(callId: String, payload: ByteString?): CallEvent {
        return scenario(callId).recordEvent(EventType.REQUEST_MESSAGE_RECEIVED, payload)
    }

    fun recordFlowControlEvent(callId: String, type: EventType): CallEvent? {
        require(type == EventType.RESPONSE_DELIVERY_BLOCKED || type == EventType.RESPONSE_DELIVERY_READY) {
            "flow-control event type must describe response delivery readiness"
        }
        return scenario(callId).recordEventIfCallActive(type)
    }

    fun callAccepted(
        callId: String,
        metadata: List<MetadataEntry> = emptyList(),
    ): CallEvent = scenario(callId).callAccepted(metadata)

    fun callClosed(
        callId: String,
        metadata: List<MetadataEntry> = emptyList(),
        status: GrpcStatus? = null,
    ): CallEvent {
        val transition = scenario(callId).callClosed(metadata, status)
        transition.inboundDemandEndpoint?.close()
        return transition.event
    }

    fun clientCancelled(callId: String): CallEvent {
        val transition = scenario(callId).clientCancelled()
        transition.inboundDemandEndpoint?.close()
        return transition.event
    }

    fun registerInboundDemand(callId: String, requestMessages: (Int) -> Unit) {
        val scenario = scenario(callId)
        val endpoint = InboundDemandEndpoint(requestMessages)
        val pendingDemand = scenario.registerInboundDemand(endpoint)
        try {
            if (pendingDemand > 0) endpoint.grant(pendingDemand)
        } catch (error: Throwable) {
            scenario.unregisterInboundDemand(endpoint)
            endpoint.close()
            throw error
        }
    }

    fun grantInboundDemand(callId: String, messageCount: Int) {
        require(messageCount > 0) { "inbound demand message_count must be positive" }
        val endpoint = scenario(callId).grantInboundDemand(messageCount)
        endpoint?.grant(messageCount)
    }

    fun awaitEvent(callId: String, type: EventType, occurrence: Int): CallEvent {
        requireKnownEvent(type)
        require(occurrence > 0) { "event occurrence must be one-based" }
        return scenario(callId).awaitEvent(type, occurrence, waitTimeout)
    }

    fun releaseBarrier(callId: String, type: BarrierType, occurrence: Int) {
        scenario(callId).releaseBarrier(barrierKey(type, occurrence))
    }

    fun awaitBarrier(callId: String, type: BarrierType, occurrence: Int) {
        scenario(callId).awaitBarrier(barrierKey(type, occurrence), waitTimeout)
    }

    /** Like [awaitBarrier], but returns immediately when the scenario has no such barrier. */
    fun awaitBarrierIfConfigured(callId: String, type: BarrierType, occurrence: Int) {
        scenario(callId).awaitBarrierIfConfigured(barrierKey(type, occurrence), waitTimeout)
    }

    fun trace(callId: String): CallTrace = scenario(callId).trace()

    fun diagnostics(callId: String): ScenarioDiagnostics = scenario(callId).diagnostics()

    fun discard(callId: String) {
        val scenario = scenarios.remove(callId) ?: throw ScenarioNotFoundException(callId)
        scenario.discard()?.close()
    }

    fun activeScenarioCount(): Int = scenarios.size

    fun awaitWaiterCount(callId: String, count: Int) {
        require(count >= 0) { "waiter count must not be negative" }
        scenario(callId).awaitWaiterCount(count, waitTimeout)
    }

    private fun scenario(callId: String): ScenarioState {
        return scenarios[callId] ?: throw ScenarioNotFoundException(callId)
    }

    private fun barrierKey(type: BarrierType, occurrence: Int): BarrierKey {
        require(type != BarrierType.BARRIER_TYPE_UNSPECIFIED && type != BarrierType.UNRECOGNIZED) {
            "barrier type must be specified"
        }
        require(occurrence > 0) { "barrier occurrence must be one-based" }
        return BarrierKey(type, occurrence)
    }

    private fun requireKnownEvent(type: EventType) {
        require(type != EventType.EVENT_TYPE_UNSPECIFIED && type != EventType.UNRECOGNIZED) {
            "event type must be specified"
        }
    }

    private fun validateMetadata(entries: List<MetadataEntry>) {
        entries.forEach { entry ->
            require(METADATA_KEY.matches(entry.key)) {
                "metadata key '${entry.key}' must contain only lowercase ASCII letters, digits, '-', '_', or '.'"
            }
        }
    }

    private fun validateTerminalBehavior(request: ConfigureScenarioRequest) {
        if (!request.hasTerminalBehavior()) return

        val terminal = request.terminalBehavior
        require(terminal.hasStatus()) { "terminal behavior must specify a status" }
        require(terminal.status.code in 0..MAX_GRPC_STATUS_CODE) {
            "terminal status code must be a canonical gRPC status code"
        }
        require(terminal.stage != TerminalStage.TERMINAL_STAGE_UNSPECIFIED &&
            terminal.stage != TerminalStage.UNRECOGNIZED
        ) {
            "terminal stage must be specified"
        }
        if (terminal.stage == TerminalStage.AFTER_RESPONSE_MESSAGES) {
            require(terminal.responseMessageCount > 0) {
                "AFTER_RESPONSE_MESSAGES requires a positive response_message_count"
            }
        } else {
            require(terminal.responseMessageCount == 0) {
                "response_message_count is valid only for AFTER_RESPONSE_MESSAGES"
            }
        }
    }

    private companion object {
        val METADATA_KEY: Regex = Regex("[0-9a-z_.-]+")
        const val MAX_GRPC_STATUS_CODE: Int = 16
    }
}

internal class ScenarioNotFoundException(callId: String) :
    IllegalStateException("scenario '$callId' is not configured")

internal class ScenarioDiscardedException(callId: String) :
    IllegalStateException("scenario '$callId' was discarded")

internal class ScenarioWaitTimeoutException(callId: String, awaited: String) :
    IllegalStateException("timed out waiting for $awaited in scenario '$callId'")

private data class BarrierKey(val type: BarrierType, val occurrence: Int) {
    override fun toString(): String = "barrier $type occurrence $occurrence"
}

private data class TerminalTransition(
    val event: CallEvent,
    val inboundDemandEndpoint: InboundDemandEndpoint?,
)

private class InboundDemandEndpoint(
    private val requestMessages: (Int) -> Unit,
) {
    private val active = AtomicBoolean(true)

    fun grant(messageCount: Int) {
        check(active.get()) { "inbound demand is no longer attached to an active call" }
        requestMessages(messageCount)
    }

    fun close() {
        active.set(false)
    }
}

private class ScenarioState(
    private val scenarioConfiguration: ConfigureScenarioRequest,
    private val configuredBarriers: Set<BarrierKey>,
) {
    private val callId: String = scenarioConfiguration.callId
    private val lock = ReentrantLock()
    private val changed = lock.newCondition()
    private val events = mutableListOf<CallEvent>()
    private val eventOccurrences = mutableMapOf<EventType, Int>()
    private val releasedBarriers = mutableSetOf<BarrierKey>()
    private var discarded = false
    private var waiterCount = 0
    private var controlWaiterCount = 0
    private var activeCallCount = 0
    private var inboundDemandEndpoint: InboundDemandEndpoint? = null
    private var pendingInboundDemand: Int = 0
    private var tracedRequestPayloadBytes = 0L
    private var tracedMetadataBytes = 0L

    fun configuration(): ConfigureScenarioRequest = scenarioConfiguration

    fun callAccepted(metadata: List<MetadataEntry>): CallEvent = lock.withLock {
        checkNotDiscarded()
        val event = recordEventLocked(EventType.CALL_ACCEPTED, metadata = metadata)
        activeCallCount++
        event
    }

    fun callClosed(metadata: List<MetadataEntry>, status: GrpcStatus?): TerminalTransition = lock.withLock {
        checkNotDiscarded()
        check(activeCallCount > 0) { "scenario '$callId' has no active call to close" }
        val event = recordEventLocked(EventType.CALL_CLOSED, metadata = metadata, status = status)
        activeCallCount--
        TerminalTransition(event, detachInboundDemandLocked())
    }

    fun clientCancelled(): TerminalTransition = lock.withLock {
        checkNotDiscarded()
        check(activeCallCount > 0) { "scenario '$callId' has no active call to cancel" }
        val event = recordEventLocked(EventType.CLIENT_CANCELLED)
        activeCallCount--
        TerminalTransition(event, detachInboundDemandLocked())
    }

    fun registerInboundDemand(endpoint: InboundDemandEndpoint): Int = lock.withLock {
        checkNotDiscarded()
        check(scenarioConfiguration.flowControl.manualInboundDemand) {
            "scenario '$callId' does not enable manual inbound demand"
        }
        check(activeCallCount > 0) { "scenario '$callId' has no active call for inbound demand" }
        check(inboundDemandEndpoint == null) {
            "scenario '$callId' already has an inbound-demand handler"
        }
        inboundDemandEndpoint = endpoint
        pendingInboundDemand.also { pendingInboundDemand = 0 }
    }

    fun unregisterInboundDemand(endpoint: InboundDemandEndpoint) = lock.withLock {
        if (inboundDemandEndpoint === endpoint) {
            inboundDemandEndpoint = null
        }
    }

    fun grantInboundDemand(messageCount: Int): InboundDemandEndpoint? = lock.withLock {
        checkNotDiscarded()
        check(scenarioConfiguration.flowControl.manualInboundDemand) {
            "scenario '$callId' does not enable manual inbound demand"
        }
        check(activeCallCount > 0) { "scenario '$callId' has no active call for inbound demand" }
        inboundDemandEndpoint ?: run {
            check(pendingInboundDemand <= Int.MAX_VALUE - messageCount) {
                "scenario '$callId' accumulated too much pending inbound demand"
            }
            pendingInboundDemand += messageCount
            null
        }
    }

    fun recordEvent(
        type: EventType,
        requestPayload: ByteString? = null,
        metadata: List<MetadataEntry> = emptyList(),
        status: GrpcStatus? = null,
    ): CallEvent = lock.withLock {
        checkNotDiscarded()
        recordEventLocked(type, requestPayload, metadata, status)
    }

    fun recordEventIfCallActive(type: EventType): CallEvent? = lock.withLock {
        checkNotDiscarded()
        if (activeCallCount == 0) null else recordEventLocked(type)
    }

    private fun recordEventLocked(
        type: EventType,
        requestPayload: ByteString? = null,
        metadata: List<MetadataEntry> = emptyList(),
        status: GrpcStatus? = null,
    ): CallEvent {
        check(events.size < MAX_TRACE_EVENTS) {
            "scenario '$callId' exceeded the maximum trace size of $MAX_TRACE_EVENTS events"
        }
        if (requestPayload != null) {
            check(tracedRequestPayloadBytes + requestPayload.size() <= MAX_TRACE_PAYLOAD_BYTES) {
                "scenario '$callId' exceeded the maximum traced request payload size of " +
                    "$MAX_TRACE_PAYLOAD_BYTES bytes"
            }
            tracedRequestPayloadBytes += requestPayload.size()
        }
        val metadataBytes = metadata.sumOf { entry -> entry.key.length.toLong() + entry.value.size() }
        check(tracedMetadataBytes + metadataBytes <= MAX_TRACE_METADATA_BYTES) {
            "scenario '$callId' exceeded the maximum traced metadata size of " +
                "$MAX_TRACE_METADATA_BYTES bytes"
        }
        tracedMetadataBytes += metadataBytes
        val occurrence = (eventOccurrences[type] ?: 0) + 1
        eventOccurrences[type] = occurrence

        val eventBuilder = CallEvent.newBuilder()
            .setSequence(events.size.toLong() + 1L)
            .setType(type)
            .setOccurrence(occurrence)
        requestPayload?.let(eventBuilder::setRequestPayload)
        eventBuilder.addAllMetadata(metadata)
        status?.let(eventBuilder::setStatus)
        val event = eventBuilder.build()
        events += event
        changed.signalAll()
        return event
    }

    fun awaitEvent(type: EventType, occurrence: Int, timeout: Duration): CallEvent = lock.withLock {
        await(timeout, "event $type occurrence $occurrence", controlWaiter = true) {
            events.firstOrNull { it.type == type && it.occurrence == occurrence }
        }
    }

    fun releaseBarrier(key: BarrierKey) = lock.withLock {
        checkNotDiscarded()
        checkConfigured(key)
        check(releasedBarriers.add(key)) { "$key was already released for scenario '$callId'" }
        changed.signalAll()
    }

    fun awaitBarrier(key: BarrierKey, timeout: Duration) = lock.withLock {
        checkConfigured(key)
        awaitReleasedLocked(key, timeout)
    }

    fun awaitBarrierIfConfigured(key: BarrierKey, timeout: Duration) = lock.withLock {
        if (key in configuredBarriers) awaitReleasedLocked(key, timeout)
    }

    private fun checkConfigured(key: BarrierKey) {
        check(key in configuredBarriers) { "$key is not configured for scenario '$callId'" }
    }

    private fun awaitReleasedLocked(key: BarrierKey, timeout: Duration) {
        await(timeout, key.toString(), controlWaiter = false) {
            if (key in releasedBarriers) Unit else null
        }
    }

    fun trace(): CallTrace = lock.withLock {
        checkNotDiscarded()
        CallTrace.newBuilder()
            .setCallId(callId)
            .addAllEvents(events)
            .build()
    }

    fun diagnostics(): ScenarioDiagnostics = lock.withLock {
        checkNotDiscarded()
        val outstandingBarriers = (configuredBarriers - releasedBarriers)
            .sortedWith(compareBy({ it.type.number }, BarrierKey::occurrence))
            .map { key ->
                Barrier.newBuilder()
                    .setType(key.type)
                    .setOccurrence(key.occurrence)
                    .build()
            }

        ScenarioDiagnostics.newBuilder()
            .setCallId(callId)
            .setActiveCallCount(activeCallCount)
            .addAllOutstandingBarriers(outstandingBarriers)
            .setControlWaiterCount(controlWaiterCount)
            .build()
    }

    fun discard(): InboundDemandEndpoint? = lock.withLock {
        discarded = true
        val endpoint = detachInboundDemandLocked()
        changed.signalAll()
        endpoint
    }

    fun awaitWaiterCount(count: Int, timeout: Duration) = lock.withLock {
        var remainingNanos = timeout.toNanos()
        while (waiterCount != count) {
            checkNotDiscarded()
            if (remainingNanos <= 0L) {
                throw ScenarioWaitTimeoutException(callId, "waiter count $count")
            }
            remainingNanos = changed.awaitNanos(remainingNanos)
        }
    }

    private fun <T : Any> await(
        timeout: Duration,
        awaited: String,
        controlWaiter: Boolean,
        result: () -> T?,
    ): T {
        waiterCount++
        if (controlWaiter) controlWaiterCount++
        changed.signalAll()
        try {
            var remainingNanos = timeout.toNanos()
            while (true) {
                checkNotDiscarded()
                result()?.let { return it }
                if (remainingNanos <= 0L) {
                    throw ScenarioWaitTimeoutException(callId, awaited)
                }
                remainingNanos = changed.awaitNanos(remainingNanos)
            }
        } finally {
            waiterCount--
            if (controlWaiter) controlWaiterCount--
            changed.signalAll()
        }
    }

    private fun checkNotDiscarded() {
        if (discarded) throw ScenarioDiscardedException(callId)
    }

    private fun detachInboundDemandLocked(): InboundDemandEndpoint? {
        pendingInboundDemand = 0
        return inboundDemandEndpoint.also { inboundDemandEndpoint = null }
    }

    private companion object {
        const val MAX_TRACE_EVENTS: Int = 1_024
        const val MAX_TRACE_PAYLOAD_BYTES: Long = 64L * 1_024 * 1_024
        const val MAX_TRACE_METADATA_BYTES: Long = 1L * 1_024 * 1_024
    }
}
