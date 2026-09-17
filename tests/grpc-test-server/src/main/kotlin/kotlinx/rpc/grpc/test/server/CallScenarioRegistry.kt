/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.server

import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.CallEvent
import kxrpc.testing.CallTrace
import kxrpc.testing.ConfigureScenarioRequest
import kxrpc.testing.EventType
import kxrpc.testing.ScenarioDiagnostics

internal class CallScenarioRegistry(
    private val waitTimeout: Duration = Duration.ofSeconds(10),
) {
    private val scenarios = ConcurrentHashMap<String, ScenarioState>()

    internal fun configure(request: ConfigureScenarioRequest) {
        require(request.callId.isNotBlank()) { "call_id must not be blank" }

        val barriers = request.barriersList.map { barrier ->
            requireKnownBarrier(barrier.type)
            require(barrier.occurrence > 0) { "barrier occurrence must be one-based" }
            BarrierKey(barrier.type.number, barrier.occurrence)
        }.toSet()

        require(barriers.size == request.barriersCount) {
            "scenario '${request.callId}' contains duplicate barriers"
        }

        check(scenarios.putIfAbsent(request.callId, ScenarioState(request.callId, barriers)) == null) {
            "scenario '${request.callId}' is already configured"
        }
    }

    internal fun recordEvent(callId: String, type: EventType): CallEvent {
        requireKnownEvent(type)
        return scenario(callId).recordEvent(type)
    }

    internal fun callAccepted(callId: String): CallEvent = scenario(callId).callAccepted()

    internal fun callClosed(callId: String): CallEvent = scenario(callId).callClosed()

    internal fun awaitEvent(callId: String, type: EventType, occurrence: Int): CallEvent {
        requireKnownEvent(type)
        require(occurrence > 0) { "event occurrence must be one-based" }
        return scenario(callId).awaitEvent(type, occurrence, waitTimeout)
    }

    internal fun releaseBarrier(callId: String, type: BarrierType, occurrence: Int) {
        requireKnownBarrier(type)
        require(occurrence > 0) { "barrier occurrence must be one-based" }
        scenario(callId).releaseBarrier(BarrierKey(type.number, occurrence))
    }

    internal fun awaitBarrier(callId: String, type: BarrierType, occurrence: Int) {
        requireKnownBarrier(type)
        require(occurrence > 0) { "barrier occurrence must be one-based" }
        scenario(callId).awaitBarrier(BarrierKey(type.number, occurrence), waitTimeout)
    }

    internal fun awaitBarrierIfConfigured(callId: String, type: BarrierType, occurrence: Int) {
        requireKnownBarrier(type)
        require(occurrence > 0) { "barrier occurrence must be one-based" }
        scenario(callId).awaitBarrierIfConfigured(BarrierKey(type.number, occurrence), waitTimeout)
    }

    internal fun trace(callId: String): CallTrace = scenario(callId).trace()

    internal fun diagnostics(callId: String): ScenarioDiagnostics = scenario(callId).diagnostics()

    internal fun discard(callId: String) {
        val scenario = scenarios.remove(callId) ?: throw ScenarioNotFoundException(callId)
        scenario.discard()
    }

    internal fun activeScenarioCount(): Int = scenarios.size

    internal fun awaitWaiterCount(callId: String, count: Int) {
        require(count >= 0) { "waiter count must not be negative" }
        scenario(callId).awaitWaiterCount(count, waitTimeout)
    }

    private fun scenario(callId: String): ScenarioState {
        return scenarios[callId] ?: throw ScenarioNotFoundException(callId)
    }

    private fun requireKnownBarrier(type: BarrierType) {
        require(type != BarrierType.BARRIER_TYPE_UNSPECIFIED && type != BarrierType.UNRECOGNIZED) {
            "barrier type must be specified"
        }
    }

    private fun requireKnownEvent(type: EventType) {
        require(type != EventType.EVENT_TYPE_UNSPECIFIED && type != EventType.UNRECOGNIZED) {
            "event type must be specified"
        }
    }
}

internal class ScenarioNotFoundException(callId: String) :
    IllegalStateException("scenario '$callId' is not configured")

internal class ScenarioDiscardedException(callId: String) :
    IllegalStateException("scenario '$callId' was discarded")

internal class ScenarioWaitTimeoutException(callId: String, awaited: String) :
    IllegalStateException("timed out waiting for $awaited in scenario '$callId'")

private data class BarrierKey(val typeNumber: Int, val occurrence: Int)

private class ScenarioState(
    private val callId: String,
    private val configuredBarriers: Set<BarrierKey>,
) {
    private val lock = ReentrantLock()
    private val changed = lock.newCondition()
    private val events = mutableListOf<CallEvent>()
    private val eventOccurrences = mutableMapOf<Int, Int>()
    private val releasedBarriers = mutableSetOf<BarrierKey>()
    private var discarded = false
    private var waiterCount = 0
    private var controlWaiterCount = 0
    private var activeCallCount = 0

    fun callAccepted(): CallEvent = lock.withLock {
        checkNotDiscarded()
        val event = recordEventLocked(EventType.CALL_ACCEPTED)
        activeCallCount++
        event
    }

    fun callClosed(): CallEvent = lock.withLock {
        checkNotDiscarded()
        check(activeCallCount > 0) { "scenario '$callId' has no active call to close" }
        val event = recordEventLocked(EventType.CALL_CLOSED)
        activeCallCount--
        event
    }

    fun recordEvent(type: EventType): CallEvent = lock.withLock {
        checkNotDiscarded()
        recordEventLocked(type)
    }

    private fun recordEventLocked(type: EventType): CallEvent {
        check(events.size < MAX_TRACE_EVENTS) {
            "scenario '$callId' exceeded the maximum trace size of $MAX_TRACE_EVENTS events"
        }
        val occurrence = (eventOccurrences[type.number] ?: 0) + 1
        eventOccurrences[type.number] = occurrence

        val event = CallEvent.newBuilder()
            .setSequence(events.size.toLong() + 1L)
            .setType(type)
            .setOccurrence(occurrence)
            .build()
        events += event
        changed.signalAll()
        return event
    }

    fun awaitEvent(type: EventType, occurrence: Int, timeout: Duration): CallEvent = lock.withLock {
        await(timeout, "event ${type.number} occurrence $occurrence", controlWaiter = true) {
            events.firstOrNull { it.type.number == type.number && it.occurrence == occurrence }
        }
    }

    fun releaseBarrier(key: BarrierKey) = lock.withLock {
        checkNotDiscarded()
        check(key in configuredBarriers) {
            "barrier ${key.typeNumber} occurrence ${key.occurrence} is not configured for scenario '$callId'"
        }
        check(releasedBarriers.add(key)) {
            "barrier ${key.typeNumber} occurrence ${key.occurrence} was already released for scenario '$callId'"
        }
        changed.signalAll()
    }

    fun awaitBarrier(key: BarrierKey, timeout: Duration) = lock.withLock {
        check(key in configuredBarriers) {
            "barrier ${key.typeNumber} occurrence ${key.occurrence} is not configured for scenario '$callId'"
        }
        await(timeout, "barrier ${key.typeNumber} occurrence ${key.occurrence}", controlWaiter = false) {
            if (key in releasedBarriers) Unit else null
        }
    }

    fun awaitBarrierIfConfigured(key: BarrierKey, timeout: Duration) = lock.withLock {
        if (key in configuredBarriers) {
            await(timeout, "barrier ${key.typeNumber} occurrence ${key.occurrence}", controlWaiter = false) {
                if (key in releasedBarriers) Unit else null
            }
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
            .sortedWith(compareBy(BarrierKey::typeNumber, BarrierKey::occurrence))
            .map { key ->
                Barrier.newBuilder()
                    .setType(BarrierType.forNumber(key.typeNumber))
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

    fun discard() = lock.withLock {
        discarded = true
        changed.signalAll()
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

    private companion object {
        const val MAX_TRACE_EVENTS: Int = 1_024
    }
}
