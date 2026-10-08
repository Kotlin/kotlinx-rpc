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
 * One expected event occurrence in the ordered trace of the reference server.
 *
 * Matches a recorded `CallEvent` by [type] and [occurrence] only. Tests check the sequence number, metadata, and
 * payload separately where necessary. The `successful*Events` and `failed*Events` builders below give lists for
 * complete call lifecycles.
 *
 * @property type The event type, for example [EventType.RESPONSE_MESSAGE_SENT].
 * @property occurrence One-based occurrence of [type] in the call, for example `2U` for the second response.
 */
internal data class ExpectedServerEvent(
    val type: EventType,
    val occurrence: UInt = 1U,
)

/**
 * Creates a one-based reference-server barrier for [grpcClientTest] or [GrpcClientTestFixture.configureScenario].
 *
 * The barrier stops the server immediately before the [occurrence]-th step of [type]. The server continues when
 * the test calls [GrpcClientTestFixture.releaseServerBarrier] with the same type and occurrence. For example,
 * `serverBarrier(BarrierType.SEND_RESPONSE, 2U)` holds the call after its first response.
 */
internal fun serverBarrier(type: BarrierType, occurrence: UInt = 1U): Barrier {
    require(occurrence > 0U) { "barrier occurrence must be one-based" }
    return Barrier {
        this.type = type
        this.occurrence = occurrence
    }
}

/** Renders the server trace for failure output, with one `sequence: type occurrence n` line for each event. */
internal fun CallTrace.render(): String {
    if (events.isEmpty()) return "  <empty>"
    return events.joinToString(separator = "\n") { event ->
        "  ${event.sequence}: ${event.type} occurrence ${event.occurrence}"
    }
}

/**
 * Renders the server-side state that must be empty at fixture teardown: open data-plane calls, blocked
 * control-plane waiters, and unreleased barriers.
 */
internal fun ScenarioDiagnostics.render(): String {
    val barriers = outstandingBarriers.joinToString(prefix = "[", postfix = "]") { "${it.type}#${it.occurrence}" }
    return "call_id='$callId', active_calls=$activeCallCount, control_waiters=$controlWaiterCount, " +
        "outstanding_barriers=$barriers"
}

/**
 * Asserts that the trace of [expectedCallId] has exactly [expectedEvents], in order.
 *
 * The sequence numbers must start at 1 and have no gaps, so the check also finds an event that is missing from
 * the returned trace. The failure messages have the rendered trace.
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
 * Asserts that the trace matches one of [expectedAlternatives], each checked like [assertEvents].
 *
 * Use it when the order of some events is a race, for example when a client cancellation and a server step
 * happen at the same time. The caller must tell in a comment why each alternative is valid.
 */
internal fun CallTrace.assertEventsOneOf(
    expectedCallId: String,
    expectedAlternatives: List<List<ExpectedServerEvent>>,
) {
    require(expectedAlternatives.isNotEmpty()) { "expectedAlternatives must have at least one event list" }
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
 * Asserts that the trace is [expectedBeforeTerminal] followed by a server close or a client cancellation.
 *
 * The last event must be the first occurrence of [EventType.CALL_CLOSED] or [EventType.CLIENT_CANCELLED]. Use it
 * when the client cancels a call while the server closes it, so either side can end the call first.
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
 * Asserts that the server holds no work for the scenario after its calls end.
 *
 * The checks:
 * - No data-plane call is open.
 * - No configured barrier is unreleased.
 * - No `AwaitEvent` control call is blocked.
 *
 * The function uses [trace] only for the failure message.
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
 * Runs [block] and asserts that it throws a [GrpcStatusException] with status [code] and the exact
 * [description].
 *
 * A `null` [description] requires a status without a description. The assertion fails if [block] completes
 * normally or throws a different exception.
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
 * Like [assertGrpcStatus], but ignores the description.
 *
 * Use it for a status that gets its description from the gRPC implementation and not from the test, such as
 * `UNIMPLEMENTED` or `DEADLINE_EXCEEDED`. The wording of such a description is different in each
 * implementation.
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
        "expected gRPC status $code, actual failure: ${failure?.let { "${it::class.simpleName}: ${it.message}" }}",
    )
    assertEquals(code, exception.status.statusCode)
    return exception
}

/**
 * A successful unary call: one request, half-close, headers, one response, close.
 *
 * The server records the same lifecycle for a client-streaming call with one request.
 */
internal fun successfulUnaryEvents(): List<ExpectedServerEvent> = successfulClientStreamingEvents(requestCount = 1)

/**
 * A successful server-streaming call: one request, half-close, headers, [responseCount] responses, close.
 *
 * The server sends the initial headers only with the first response. If [responseCount] is `0`, the server closes
 * with a trailers-only response, so the trace has no [EventType.INITIAL_HEADERS_SENT].
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
 * A unary or server-streaming call that failed after the server received the request, before any response.
 *
 * The server closes with a trailers-only response, so the trace has no [EventType.INITIAL_HEADERS_SENT].
 */
internal fun failedBeforeResponseEvents(): List<ExpectedServerEvent> = listOf(
    ExpectedServerEvent(EventType.CALL_ACCEPTED),
    ExpectedServerEvent(EventType.REQUEST_MESSAGE_RECEIVED),
    ExpectedServerEvent(EventType.CLIENT_HALF_CLOSED),
    ExpectedServerEvent(EventType.CALL_CLOSED),
)

/** A successful client-streaming call: [requestCount] requests, half-close, headers, one response, close. */
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
 * The server closes the call without headers or responses while the client still sends.
 */
internal fun failedDuringRequestsEvents(requestCount: Int): List<ExpectedServerEvent> = buildList {
    require(requestCount >= 0) { "request count must not be negative" }
    add(ExpectedServerEvent(EventType.CALL_ACCEPTED))
    addOccurrences(EventType.REQUEST_MESSAGE_RECEIVED, requestCount)
    add(ExpectedServerEvent(EventType.CALL_CLOSED))
}

/**
 * A successful bidirectional call where the server answers each request with one response before the next
 * request.
 *
 * Response `n` follows request `n`, the initial headers come before the first response, and the half-close
 * follows the last exchange. If [exchangeCount] is `0`, the call has only the half-close and the close.
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
 * A successful bidirectional call where the server answers request `i` with `responsesAfterRequest[i]` responses,
 * and sends [responsesAfterHalfClose] more responses after the client half-close.
 *
 * The response occurrences count across the whole call. The initial headers come immediately before the first
 * response, wherever it is. For example, `successfulFullDuplexEvents(listOf(0, 2), 1)` expects:
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
 * A successful bidirectional call where the server answers only after it receives all requests and the
 * half-close.
 *
 * The events are [requestCount] requests, half-close, headers, [responseCount] responses, close. If
 * [responseCount] is `0`, the trace has no headers.
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

/** Failure message of the trace assertions: the expected and actual call id and the rendered trace. */
private fun CallTrace.failureMessage(expectedCallId: String): String {
    return "expected call_id='$expectedCallId', actual call_id='$callId', server trace:\n${render()}"
}
