/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.description
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kxrpc.testing.Barrier
import kxrpc.testing.BarrierType
import kxrpc.testing.CallTrace
import kxrpc.testing.EventType
import kxrpc.testing.ScenarioDiagnostics
import kxrpc.testing.invoke
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** One expected occurrence in the reference server's ordered trace. */
internal data class ExpectedServerEvent(
    val type: EventType,
    val occurrence: UInt = 1U,
)

/** Creates a configured one-based reference-server barrier. */
internal fun serverBarrier(type: BarrierType, occurrence: UInt = 1U): Barrier {
    require(occurrence > 0U) { "barrier occurrence must be one-based" }
    return Barrier {
        this.type = type
        this.occurrence = occurrence
    }
}

/** Renders an ordered server trace for failure output. */
internal fun CallTrace.render(): String {
    if (events.isEmpty()) return "  <empty>"
    return events.joinToString(separator = "\n") { event ->
        "  ${event.sequence}: ${event.type} occurrence ${event.occurrence}"
    }
}

/** Renders the server-owned state which must be empty during fixture teardown. */
internal fun ScenarioDiagnostics.render(): String = buildString {
    append("call_id='")
    append(callId)
    append("', active_calls=")
    append(activeCallCount)
    append(", control_waiters=")
    append(controlWaiterCount)
    append(", outstanding_barriers=")
    append(
        outstandingBarriers.joinToString(prefix = "[", postfix = "]") { barrier ->
            "${barrier.type}#${barrier.occurrence}"
        }
    )
}

/** Asserts exact trace order, occurrences, and monotonic sequence numbers. */
internal fun CallTrace.assertEvents(
    expectedCallId: String,
    expectedEvents: List<ExpectedServerEvent>,
) {
    val failureMessage = failureMessage(expectedCallId)
    assertEquals(expectedCallId, callId, failureMessage)
    assertEquals(expectedEvents, events.map { ExpectedServerEvent(it.type, it.occurrence) }, failureMessage)
    assertEquals(events.indices.map { (it + 1).toULong() }, events.map { it.sequence }, failureMessage)
}

/** Asserts that a scenario has no server-owned work left after a call terminates. */
internal fun ScenarioDiagnostics.assertNoLeaks(
    expectedCallId: String,
    trace: CallTrace,
) {
    val failureMessage = "${render()}\nserver trace:\n${trace.render()}"
    assertEquals(expectedCallId, callId, failureMessage)
    assertEquals(0U, activeCallCount, failureMessage)
    assertEquals(emptyList<Barrier>(), outstandingBarriers, failureMessage)
    assertEquals(0U, controlWaiterCount, failureMessage)
}

internal fun successfulUnaryEvents(): List<ExpectedServerEvent> = buildList {
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED))
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
    add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT))
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

internal fun successfulServerStreamingEvents(responseCount: Int): List<ExpectedServerEvent> = buildList {
    require(responseCount >= 0) { "response count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED))
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    if (responseCount > 0) add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
    addOccurrences(EventType.RESPONSE_MESSAGE_SENT, responseCount)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

internal fun failedServerStreamingBeforeResponseEvents(): List<ExpectedServerEvent> = listOf(
    ExpectedServerEvent(EventType.CALL_ACCEPTED),
    ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
    ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
    ExpectedServerEvent(EventType.CALL_CLOSED),
)

/** Asserts the exact gRPC status propagated by a failed client operation. */
internal suspend fun assertGrpcStatus(
    code: GrpcStatusCode,
    description: String? = null,
    block: suspend () -> Unit,
): GrpcStatusException {
    val failure = try {
        block()
        null
    } catch (error: Throwable) {
        error
    }
    val exception = assertIs<GrpcStatusException>(failure, "expected gRPC status $code")
    assertEquals(code, exception.status.statusCode)
    assertEquals(description, exception.status.description)
    return exception
}

internal fun successfulClientStreamingEvents(requestCount: Int): List<ExpectedServerEvent> = buildList {
    require(requestCount >= 0) { "request count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    addOccurrences(EventType.REQUEST_MESSAGE_RECEIVED, requestCount)
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
    add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT))
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

internal fun failedClientStreamingDuringRequestsEvents(requestCount: Int): List<ExpectedServerEvent> = buildList {
    require(requestCount >= 0) { "request count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    addOccurrences(EventType.REQUEST_MESSAGE_RECEIVED, requestCount)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

internal fun failedBidirectionalDuringRequestsEvents(requestCount: Int): List<ExpectedServerEvent> = buildList {
    require(requestCount >= 0) { "request count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    addOccurrences(EventType.REQUEST_MESSAGE_RECEIVED, requestCount)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

internal fun successfulPingPongEvents(exchangeCount: Int): List<ExpectedServerEvent> = buildList {
    require(exchangeCount >= 0) { "exchange count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    repeat(exchangeCount) { index ->
        val occurrence = (index + 1).toUInt()
        add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, occurrence))
        if (index == 0) add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
        add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT, occurrence))
    }
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

internal fun successfulFullDuplexEvents(
    responsesAfterRequest: List<Int>,
    responsesAfterHalfClose: Int = 0,
): List<ExpectedServerEvent> = buildList {
    require(responsesAfterRequest.all { it >= 0 }) { "response count must not be negative" }
    require(responsesAfterHalfClose >= 0) { "response count must not be negative" }
    var responseOccurrence = 0
    var headersSent = false

    fun addResponses(count: Int) {
        repeat(count) {
            if (!headersSent) {
                add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
                headersSent = true
            }
            responseOccurrence++
            add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT, responseOccurrence.toUInt()))
        }
    }

    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    responsesAfterRequest.forEachIndexed { index, responseCount ->
        add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED, (index + 1).toUInt()))
        addResponses(responseCount)
    }
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    addResponses(responsesAfterHalfClose)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

internal fun successfulHalfDuplexEvents(
    requestCount: Int,
    responseCount: Int,
): List<ExpectedServerEvent> = buildList {
    require(requestCount >= 0) { "request count must not be negative" }
    require(responseCount >= 0) { "response count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    addOccurrences(EventType.REQUEST_MESSAGE_RECEIVED, requestCount)
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    if (responseCount > 0) add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
    addOccurrences(EventType.RESPONSE_MESSAGE_SENT, responseCount)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

private fun MutableList<ExpectedServerEvent>.addOccurrences(type: EventType, count: Int) {
    repeat(count) { index -> add(ExpectedServerEvent(type, (index + 1).toUInt())) }
}

private fun CallTrace.failureMessage(expectedCallId: String): String {
    return "expected call_id='$expectedCallId', actual call_id='$callId', server trace:\n${render()}"
}
