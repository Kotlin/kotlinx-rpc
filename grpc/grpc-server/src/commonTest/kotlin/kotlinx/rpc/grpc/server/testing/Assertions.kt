/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.statusCode
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal suspend fun assertGrpcStatus(code: GrpcStatusCode, block: suspend () -> Unit): GrpcStatusException {
    val error = assertFailsWith<GrpcStatusException> { block() }
    assertEquals(code, error.status.statusCode, "Unexpected status: ${error.status}")
    return error
}

internal fun FakeServerCall<*, *>.assertNoViolations() {
    assertEquals(emptyList(), violations())
}

internal fun FakeServerCall<*, *>.assertClosedWith(code: GrpcStatusCode): ServerCallEvent.Closed {
    val close = events.filterIsInstance<ServerCallEvent.Closed>().single()
    assertEquals(code, close.status.statusCode)
    return close
}

internal fun FakeServerCall<*, *>.assertNoMessages() {
    assertMessages()
}

internal fun <Response> FakeServerCall<*, Response>.assertMessages(vararg expected: Response) {
    assertMessages(expected.toList())
}

internal fun <Response> FakeServerCall<*, Response>.assertMessages(expected: List<Response>) {
    val messages = events.filterIsInstance<ServerCallEvent.MessageSent>().map { it.message }
    assertEquals(expected, messages)
}

internal fun FakeServerCall<*, *>.assertNotClosed() {
    assertEquals(emptyList(), events.filterIsInstance<ServerCallEvent.Closed>())
}
