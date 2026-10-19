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
import kxrpc.testing.MalformedResponseCount
import kxrpc.testing.ScenarioDiagnostics
import kxrpc.testing.TerminalStage

/**
 * Server-side state of the scenarios that [GrpcClientControlService] configures.
 *
 * A test configures a scenario under a call ID. Then it starts a data-plane call with that call ID in the
 * `kxrpc-test-call-id` metadata. [ScenarioInterceptor] reports the lifecycle of the call to the registry, which:
 *
 * - Records the lifecycle as the call trace.
 * - Blocks the call at each configured barrier until the test releases the barrier.
 * - Forwards the inbound demand that the test grants to the call, for scenarios with manual inbound demand (see
 *   [InboundDemandEndpoint]).
 *
 * Each blocking operation fails after [waitTimeout], so a broken test can't block the server.
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
        require(request.malformedResponseCount != MalformedResponseCount.UNRECOGNIZED) {
            "malformed_response_count has an unknown value. Use a value from client_control.proto"
        }

        val barriers = request.barriersList.map { barrier -> barrierKey(barrier.type, barrier.occurrence) }.toSet()

        require(barriers.size == request.barriersCount) {
            "scenario '${request.callId}' has duplicate barriers. Configure each barrier key once"
        }

        check(scenarios.putIfAbsent(request.callId, ScenarioState(request, barriers)) == null) {
            "scenario '${request.callId}' is already configured"
        }
    }

    fun configuration(callId: String): ConfigureScenarioRequest = scenario(callId).configuration()

    /**
     * Throws [ScenarioNotFoundException] if the scenario with the given [callId] does not exist.
     */
    fun checkScenarioExists(callId: String) {
        if (!scenarios.containsKey(callId))  {
            throw ScenarioNotFoundException(callId)
        }
    }

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
            "flow-control event type must be RESPONSE_DELIVERY_BLOCKED or RESPONSE_DELIVERY_READY"
        }
        return scenario(callId).recordEventIfCallActive(type)
    }

    fun callAccepted(
        callId: String,
        metadata: List<MetadataEntry> = emptyList(),
    ): CallEvent = scenario(callId).callAccepted(metadata)

    /**
     * Records [EventType.CALL_CLOSED] for the active call and ends it.
     *
     * If the call uses manual inbound demand, this function detaches and closes its [InboundDemandEndpoint] and sets
     * the pending demand to zero. A later [grantInboundDemand] then fails because no call is active.
     */
    fun callClosed(
        callId: String,
        metadata: List<MetadataEntry> = emptyList(),
        status: GrpcStatus? = null,
    ): CallEvent {
        val transition = scenario(callId).callClosed(metadata, status)
        transition.inboundDemandEndpoint?.close()
        return transition.event
    }

    /**
     * Records [EventType.CLIENT_CANCELLED] for the active call and ends it. Ends the inbound demand of the call as
     * [callClosed] does.
     */
    fun clientCancelled(callId: String): CallEvent {
        val transition = scenario(callId).clientCancelled()
        transition.inboundDemandEndpoint?.close()
        return transition.event
    }

    /**
     * Attaches the active call of a scenario with manual inbound demand to the control plane, so that
     * [grantInboundDemand] reaches the call. [InboundDemandEndpoint] lists all the steps.
     *
     * Call it once for each call, from the service method, after the automatic requests of grpc-java are off.
     * This function forwards the demand that the test granted before this point to [requestMessages] at once, outside
     * the scenario lock. If that fails, for example because the call ended, it detaches and closes the endpoint and
     * throws the error again.
     *
     * @param requestMessages Requests the given number of additional request messages from grpc-java, that is
     * `ServerCallStreamObserver.request(n)`.
     * @throws IllegalStateException if the scenario doesn't enable manual inbound demand, has no active call, or
     * already has an attached endpoint.
     */
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

    /**
     * Lets the active call of a scenario with manual inbound demand read [messageCount] more request messages.
     *
     * The test can grant demand before the call reaches the service method. So the demand accumulates in the
     * scenario until the call registers through [registerInboundDemand], and the registration forwards it to
     * grpc-java. After that, each grant goes to grpc-java directly.
     *
     * @throws IllegalArgumentException if [messageCount] isn't positive.
     * @throws IllegalStateException if the scenario doesn't enable manual inbound demand, has no active call, or
     * the accumulated demand overflows `Int`.
     */
    fun grantInboundDemand(callId: String, messageCount: Int) {
        require(messageCount > 0) { "inbound demand message_count must be positive" }
        scenario(callId).grantInboundDemand(messageCount)
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

    /** Like [awaitBarrier], but returns at once when the scenario has no such barrier. */
    fun awaitBarrierIfConfigured(callId: String, type: BarrierType, occurrence: Int) {
        scenario(callId).awaitBarrierIfConfigured(barrierKey(type, occurrence), waitTimeout)
    }

    fun getTrace(callId: String): CallTrace = scenario(callId).getTrace()

    fun getDiagnostics(callId: String): ScenarioDiagnostics = scenario(callId).getDiagnostics()

    /**
     * Removes the scenario and closes its attached [InboundDemandEndpoint]. Threads that wait in the scenario fail
     * with [ScenarioDiscardedException].
     */
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
    IllegalStateException("scenario '$callId' isn't configured. Call ConfigureScenario first")

internal class ScenarioDiscardedException(callId: String) :
    IllegalStateException("scenario '$callId' was discarded")

internal class ScenarioWaitTimeoutException(callId: String, awaited: String) :
    IllegalStateException("the wait for $awaited in scenario '$callId' timed out")

/**
 * The [occurrence]-th time that a data-plane call of a scenario reaches a [type] point.
 * For example, `(SEND_RESPONSE, 2)` blocks the second response message.
 *
 * [CallScenarioRegistry] checks that [occurrence] is one-based.
 */
private data class BarrierKey(val type: BarrierType, val occurrence: Int) {
    override fun toString(): String = "barrier $type occurrence $occurrence"
}

/**
 * Result of the end of a call: a close or a client cancellation.
 *
 * @property event The recorded [EventType.CALL_CLOSED] or [EventType.CLIENT_CANCELLED] event.
 * @property inboundDemandEndpoint The demand endpoint that the ended call registered, now detached. The caller closes
 * it outside the scenario lock (see [InboundDemandEndpoint]).
 */
private data class TerminalTransition(
    val event: CallEvent,
    val inboundDemandEndpoint: InboundDemandEndpoint?,
)

/**
 * Connects the control-plane demand grants to the inbound demand of one data-plane call.
 *
 * Only scenarios with manual inbound demand use it. The inbound demand of a call has these steps:
 *
 * 1. The server accepts the call. The call has no endpoint yet, so the grants from `AllowInboundReads` accumulate in
 *    the scenario.
 * 2. The service method calls [enableManualInboundDemandIfConfigured]. It turns off the automatic requests of
 *    grpc-java and attaches an endpoint that calls `request(n)` through [CallScenarioRegistry.registerInboundDemand].
 *    The registry forwards the accumulated demand to the endpoint.
 * 3. The registry forwards each later grant to the endpoint directly.
 * 4. When the call closes, the client cancels it, or the test discards the scenario, the registry detaches and
 *    closes the endpoint.
 *
 * @param requestMessages Requests the given number of additional request messages from grpc-java.
 */
private class InboundDemandEndpoint(
    private val requestMessages: (Int) -> Unit,
) {
    private val active = AtomicBoolean(true)

    /**
     * Requests [messageCount] more request messages from grpc-java.
     *
     * @throws IllegalStateException after [close].
     */
    fun grant(messageCount: Int) {
        check(active.get()) { "inbound demand is no longer attached to an active call" }
        requestMessages(messageCount)
    }

    /** Makes each later [grant] fail. */
    fun close() {
        active.set(false)
    }
}

/**
 * Mutable state of one configured scenario. Its data-plane calls and the control plane share it.
 *
 * Code reads and writes the state only while it holds [lock]. Each change signals [changed], so that these blocked
 * threads check their condition again:
 *
 * - Data-plane threads that wait at barriers.
 * - Control-plane threads that wait for events.
 * - Tests that wait for a waiter count.
 *
 * The caller of a function whose name ends in `Locked` must hold [lock].
 *
 * After [discard], later operations on the scenario fail with [ScenarioDiscardedException]. Threads that already
 * wait in the scenario wake up and fail the same way.
 *
 * @param scenarioConfiguration The validated request that configured this scenario.
 * @param configuredBarriers The barriers from [scenarioConfiguration], deduplicated and validated.
 */
private class ScenarioState(
    private val scenarioConfiguration: ConfigureScenarioRequest,
    private val configuredBarriers: Set<BarrierKey>,
) {
    private val callId: String = scenarioConfiguration.callId
    private val lock = ReentrantLock()
    private val changed = lock.newCondition()

    /** The call trace in recording order. The sequence of an event is its one-based index in this list. */
    private val events = mutableListOf<CallEvent>()

    /** Number of recorded events of each type. Gives each new event its one-based occurrence. */
    private val eventOccurrences = mutableMapOf<EventType, Int>()

    /** Barriers that the control plane released. A release is permanent, so a call that arrives later doesn't wait. */
    private val releasedBarriers = mutableSetOf<BarrierKey>()
    private var discarded = false

    /** Number of threads that wait in [await], at a barrier or for an event. */
    private var waiterCount = 0

    /** Part of [waiterCount]: the threads that wait in control-plane [awaitEvent] calls. */
    private var controlWaiterCount = 0

    /** Number of accepted data-plane calls that didn't close or cancel yet. */
    private var activeCallCount = 0

    /**
     * Demand endpoint of the active call, if the call uses manual inbound demand and registered an endpoint.
     * [registerInboundDemand] sets it, and [detachInboundDemandLocked] clears it when the call ends.
     */
    private var inboundDemandEndpoint: InboundDemandEndpoint? = null

    /**
     * Demand that the test grants while [inboundDemandEndpoint] is null. [registerInboundDemand] gives it to the
     * endpoint. [detachInboundDemandLocked] sets it to zero when the call ends, so it never goes to another call.
     */
    private var pendingInboundDemand: Int = 0

    /** Running totals that [recordEventLocked] checks against the trace size limits of the companion object. */
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

    /**
     * Attaches [endpoint] to the active call and sets [pendingInboundDemand] to zero.
     *
     * @return The demand granted before registration. The caller forwards it to [endpoint] outside [lock].
     */
    fun registerInboundDemand(endpoint: InboundDemandEndpoint): Int = lock.withLock {
        checkNotDiscarded()
        check(scenarioConfiguration.flowControl.manualInboundDemand) {
            "scenario '$callId' doesn't enable manual inbound demand"
        }
        check(activeCallCount > 0) { "scenario '$callId' has no active call for inbound demand" }
        check(inboundDemandEndpoint == null) {
            "scenario '$callId' already has an inbound demand endpoint"
        }
        inboundDemandEndpoint = endpoint
        pendingInboundDemand.also { pendingInboundDemand = 0 }
    }

    /**
     * Detaches [endpoint] when the forwarding of its pending demand failed. Does nothing if the endpoint is already
     * detached, for example because the call ended.
     */
    fun unregisterInboundDemand(endpoint: InboundDemandEndpoint) = lock.withLock {
        if (inboundDemandEndpoint === endpoint) {
            inboundDemandEndpoint = null
        }
    }

    /**
     * Forwards [messageCount] to the attached [inboundDemandEndpoint], or adds it to [pendingInboundDemand] when
     * [inboundDemandEndpoint] is null. Unlike [registerInboundDemand], this function forwards the demand while it
     * holds [lock].
     */
    fun grantInboundDemand(messageCount: Int) = lock.withLock {
        checkNotDiscarded()
        check(scenarioConfiguration.flowControl.manualInboundDemand) {
            "scenario '$callId' doesn't enable manual inbound demand. Set flow_control.manual_inbound_demand"
        }
        check(activeCallCount > 0) { "scenario '$callId' has no active call for inbound demand" }
        inboundDemandEndpoint?.grant(messageCount) ?: run {
            check(pendingInboundDemand <= Int.MAX_VALUE - messageCount) {
                "pending inbound demand of scenario '$callId' overflows Int. Grant less demand"
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

    /**
     * Records [type] if a call is active. Otherwise returns `null`, so that [ResponseReadinessGate] treats the call
     * as ended.
     */
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
            "scenario '$callId' exceeds the maximum trace size of $MAX_TRACE_EVENTS events. " +
                "Use fewer calls or messages in the scenario"
        }
        if (requestPayload != null) {
            check(tracedRequestPayloadBytes + requestPayload.size() <= MAX_TRACE_PAYLOAD_BYTES) {
                "scenario '$callId' exceeds the maximum traced request payload size of " +
                    "$MAX_TRACE_PAYLOAD_BYTES bytes. Send smaller request payloads"
            }
            tracedRequestPayloadBytes += requestPayload.size()
        }
        val metadataBytes = metadata.sumOf { entry -> entry.key.length.toLong() + entry.value.size() }
        check(tracedMetadataBytes + metadataBytes <= MAX_TRACE_METADATA_BYTES) {
            "scenario '$callId' exceeds the maximum traced metadata size of " +
                "$MAX_TRACE_METADATA_BYTES bytes. Send less metadata"
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
        check(releasedBarriers.add(key)) { "$key of scenario '$callId' is already released. Release each barrier once" }
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
        check(key in configuredBarriers) {
            "$key isn't configured for scenario '$callId'. Add it to the barriers of ConfigureScenarioRequest"
        }
    }

    private fun awaitReleasedLocked(key: BarrierKey, timeout: Duration) {
        await(timeout, key.toString(), controlWaiter = false) {
            if (key in releasedBarriers) Unit else null
        }
    }

    fun getTrace(): CallTrace = lock.withLock {
        checkNotDiscarded()
        CallTrace.newBuilder()
            .setCallId(callId)
            .addAllEvents(events)
            .build()
    }

    fun getDiagnostics(): ScenarioDiagnostics = lock.withLock {
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

    /**
     * Marks the scenario as discarded and wakes all waiting threads, which then fail.
     *
     * @return The detached demand endpoint, which the caller closes.
     */
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

    /**
     * Blocks until [result] returns a value that isn't `null`, then returns that value.
     * The caller must hold [lock]. The thread releases [lock] while it waits on [changed].
     *
     * Fails with [ScenarioWaitTimeoutException] after [timeout], or with [ScenarioDiscardedException] after [discard].
     * During the wait, [waiterCount] counts the thread. For a [controlWaiter] call, [controlWaiterCount] counts it too.
     *
     * @param awaited Text for the awaited condition in the timeout message.
     */
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

    /**
     * Detaches the [inboundDemandEndpoint] and sets [pendingInboundDemand] to zero, when the call ends or on
     * [discard].
     *
     * @return The detached endpoint. The caller closes it outside [lock].
     */
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
