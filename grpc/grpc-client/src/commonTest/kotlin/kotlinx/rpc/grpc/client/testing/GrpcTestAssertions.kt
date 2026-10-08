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
import kotlin.test.assertTrue

/**
 * One expected occurrence in the reference server's ordered trace.
 *
 * Matches a recorded `CallEvent` by [type] and [occurrence] only; its sequence number, metadata, and payload are
 * checked separately where needed. Lists of these describe whole call lifecycles, see the `successful*Events`
 * and `failed*Events` builders below.
 *
 * @property type The event type, for example [EventType.RESPONSE_MESSAGE_SENT].
 * @property occurrence One-based occurrence of [type] within the call, for example `2U` for the second response.
 */
internal data class ExpectedServerEvent(
    val type: EventType,
    val occurrence: UInt = 1U,
)

/**
 * Creates a configured one-based reference-server barrier.
 *
 * Passed to [grpcClientTest] or [GrpcClientTestFixture.configureScenario], the barrier pauses the server right
 * before the [occurrence]-th step of [type] until the test calls [GrpcClientTestFixture.releaseServerBarrier]
 * with the same type and occurrence. For example, `serverBarrier(BarrierType.SEND_RESPONSE, 2U)` holds the call
 * after its first response.
 */
internal fun serverBarrier(type: BarrierType, occurrence: UInt = 1U): Barrier {
    require(occurrence > 0U) { "barrier occurrence must be one-based" }
    return Barrier {
        this.type = type
        this.occurrence = occurrence
    }
}

/** Renders an ordered server trace for failure output, one `sequence: type occurrence n` line per event. */
internal fun CallTrace.render(): String {
    if (events.isEmpty()) return "  <empty>"
    return events.joinToString(separator = "\n") { event ->
        "  ${event.sequence}: ${event.type} occurrence ${event.occurrence}"
    }
}

/**
 * Renders the server-owned state which must be empty during fixture teardown: open data-plane calls, blocked
 * control-plane waiters, and unreleased barriers.
 */
internal fun ScenarioDiagnostics.render(): String {
    val barriers = outstandingBarriers.joinToString(prefix = "[", postfix = "]") { "${it.type}#${it.occurrence}" }
    return "call_id='$callId', active_calls=$activeCallCount, control_waiters=$controlWaiterCount, " +
        "outstanding_barriers=$barriers"
}

/**
 * Asserts exact trace order, occurrences, and monotonic sequence numbers.
 *
 * The trace must belong to [expectedCallId] and its events must match [expectedEvents] exactly, with nothing
 * missing or extra. Sequence numbers must run from 1 without gaps, which also verifies that the server did not
 * drop an event from the returned trace. Failure messages contain the rendered trace.
 */
internal fun CallTrace.assertEvents(
    expectedCallId: String,
    expectedEvents: List<ExpectedServerEvent>,
) {
    val failureMessage = failureMessage(expectedCallId)
    assertEquals(expectedCallId, callId, failureMessage)
    assertEquals(expectedEvents, events.map { ExpectedServerEvent(it.type, it.occurrence) }, failureMessage)
    assertEquals(events.indices.map { (it + 1).toULong() }, events.map { it.sequence }, failureMessage)
}

/**
 * Asserts that the exact trace is one of a small set of documented orderings.
 *
 * Use this when the order of some events is legitimately racy, for example when a client cancellation and a
 * server step happen concurrently. Each alternative is checked like [assertEvents]; the caller should explain
 * why every alternative is valid.
 */
internal fun CallTrace.assertEventsOneOf(
    expectedCallId: String,
    expectedAlternatives: List<List<ExpectedServerEvent>>,
) {
    require(expectedAlternatives.isNotEmpty()) { "expected event alternatives must not be empty" }
    val failureMessage = failureMessage(expectedCallId)
    val actual = events.map { ExpectedServerEvent(it.type, it.occurrence) }
    assertEquals(expectedCallId, callId, failureMessage)
    assertTrue(
        actual in expectedAlternatives,
        "$failureMessage\nexpected one of $expectedAlternatives, actual $actual",
    )
    assertEquals(events.indices.map { (it + 1).toULong() }, events.map { it.sequence }, failureMessage)
}

/**
 * Asserts an exact lifecycle prefix followed by either server close or peer cancellation.
 *
 * All events but the last must match [expectedBeforeTerminal] exactly, and the last one must be the first
 * occurrence of either [EventType.CALL_CLOSED] or [EventType.CLIENT_CANCELLED]. Use this when the client
 * cancels a call which the server may already be closing, so either side can end it first.
 */
internal fun CallTrace.assertEventsWithEitherTerminal(
    expectedCallId: String,
    expectedBeforeTerminal: List<ExpectedServerEvent>,
) {
    val failureMessage = failureMessage(expectedCallId)
    assertEquals(expectedCallId, callId, failureMessage)
    assertEquals(
        expectedBeforeTerminal,
        events.dropLast(1).map { ExpectedServerEvent(it.type, it.occurrence) },
        failureMessage,
    )
    val terminal = events.lastOrNull()
    assertTrue(
        terminal?.type == EventType.CALL_CLOSED || terminal?.type == EventType.CLIENT_CANCELLED,
        failureMessage,
    )
    assertEquals(1U, terminal.occurrence, failureMessage)
    assertEquals(events.indices.map { (it + 1).toULong() }, events.map { it.sequence }, failureMessage)
}

/**
 * Asserts that a scenario has no server-owned work left after a call terminates.
 *
 * No data-plane call may still be open, no configured barrier may still be unreleased, and no `AwaitEvent`
 * control call may still be blocked. [trace] is only used for the failure message.
 */
internal fun ScenarioDiagnostics.assertNoLeaks(
    expectedCallId: String,
    trace: CallTrace,
) {
    val failureMessage = "${render()}\nserver trace:\n${trace.render()}"
    assertEquals(expectedCallId, callId, failureMessage)
    assertEquals(0U, activeCallCount, failureMessage)
    assertEquals(emptyList(), outstandingBarriers, failureMessage)
    assertEquals(0U, controlWaiterCount, failureMessage)
}

/**
 * Asserts the exact gRPC status propagated by a failed client operation.
 *
 * Runs [block] and requires it to throw a [GrpcStatusException] with status [code] and the exact [description];
 * `null` requires the status to have no description. Fails if [block] completes normally or throws anything
 * else.
 *
 * @return The thrown exception, for further checks such as trailers.
 */
internal suspend fun assertGrpcStatus(
    code: GrpcStatusCode,
    description: String? = null,
    block: suspend () -> Unit,
): GrpcStatusException {
    val exception = assertGrpcStatusCode(code, block)
    assertEquals(description, exception.status.description)
    return exception
}

/**
 * Asserts only the gRPC status code when a server-generated description is not portable.
 *
 * Like [assertGrpcStatus], but ignores the description. Use it for statuses whose description is written by a
 * gRPC implementation rather than by the test, such as `UNIMPLEMENTED` or `DEADLINE_EXCEEDED`, because the
 * wording differs between implementations.
 *
 * @return The thrown exception, for further checks.
 */
internal suspend fun assertGrpcStatusCode(
    code: GrpcStatusCode,
    block: suspend () -> Unit,
): GrpcStatusException {
    val failure = try {
        block()
        null
    } catch (error: Throwable) {
        error
    }
    val exception = assertIs<GrpcStatusException>(
        failure,
        "expected gRPC status $code, actual failure was ${failure?.let { "${it::class.simpleName}: ${it.message}" }}",
    )
    assertEquals(code, exception.status.statusCode)
    return exception
}

/**
 * A successful unary call: one request, half-close, headers, one response, close.
 *
 * On the wire this is the same lifecycle as a client-streaming call with a single request.
 */
internal fun successfulUnaryEvents(): List<ExpectedServerEvent> = successfulClientStreamingEvents(requestCount = 1)

/**
 * A successful server-streaming call; initial headers are only sent with the first response.
 *
 * One request and the half-close, followed by [responseCount] responses and the close. Without responses, the
 * server closes with a trailers-only response, so no [EventType.INITIAL_HEADERS_SENT] is recorded.
 */
internal fun successfulServerStreamingEvents(responseCount: Int): List<ExpectedServerEvent> = buildList {
    require(responseCount >= 0) { "response count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    add(ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED))
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    if (responseCount > 0) add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
    addOccurrences(EventType.RESPONSE_MESSAGE_SENT, responseCount)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

/**
 * A unary or server-streaming call that failed after receiving its request, before any response.
 *
 * The server closes with a trailers-only response, so no [EventType.INITIAL_HEADERS_SENT] is recorded.
 */
internal fun failedBeforeResponseEvents(): List<ExpectedServerEvent> = listOf(
    ExpectedServerEvent(EventType.CALL_ACCEPTED),
    ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
    ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
    ExpectedServerEvent(EventType.CALL_CLOSED),
)

/** A successful client-streaming call: [requestCount] requests, half-close, then the single response. */
internal fun successfulClientStreamingEvents(requestCount: Int): List<ExpectedServerEvent> = buildList {
    require(requestCount >= 0) { "request count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    addOccurrences(EventType.REQUEST_MESSAGE_RECEIVED, requestCount)
    add(ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED))
    add(ExpectedServerEvent(EventType.INITIAL_HEADERS_SENT))
    add(ExpectedServerEvent(EventType.RESPONSE_MESSAGE_SENT))
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

/**
 * A client-streaming or bidirectional call that failed after [requestCount] requests, before the half-close.
 *
 * The server closes the call without headers or responses while the client is still sending.
 */
internal fun failedDuringRequestsEvents(requestCount: Int): List<ExpectedServerEvent> = buildList {
    require(requestCount >= 0) { "request count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    addOccurrences(EventType.REQUEST_MESSAGE_RECEIVED, requestCount)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

/**
 * A successful bidirectional call where every request is answered by one response before the next request.
 *
 * Request `n` is followed by response `n`, initial headers precede the first response, and the half-close
 * follows the last exchange. With [exchangeCount] `0` the call only contains the half-close and the close.
 */
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

/**
 * A successful bidirectional call where request `i` is answered by `responsesAfterRequest[i]` responses
 * and [responsesAfterHalfClose] further responses follow the client half-close.
 *
 * Response occurrences are numbered across the whole call, and initial headers are inserted right before the
 * first response, wherever it happens. For example, `successfulFullDuplexEvents(listOf(0, 2), 1)` expects:
 * request 1, request 2, headers, response 1, response 2, half-close, response 3, close.
 */
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

/**
 * A successful bidirectional call that answers only after receiving every request and the half-close.
 *
 * [requestCount] requests and the half-close, then [responseCount] responses and the close. Initial headers are
 * only expected if there is at least one response.
 */
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

/** Appends occurrences `1..count` of [type]. */
private fun MutableList<ExpectedServerEvent>.addOccurrences(type: EventType, count: Int) {
    repeat(count) { index -> add(ExpectedServerEvent(type, (index + 1).toUInt())) }
}

/** Failure message shared by the trace assertions: the expected and actual call id plus the rendered trace. */
private fun CallTrace.failureMessage(expectedCallId: String): String {
    return "expected call_id='$expectedCallId', actual call_id='$callId', server trace:\n${render()}"
}
