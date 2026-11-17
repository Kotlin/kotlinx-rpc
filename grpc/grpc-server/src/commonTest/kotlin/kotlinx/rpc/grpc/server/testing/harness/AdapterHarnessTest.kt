/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.server.testing.AdapterHarness
import kotlinx.rpc.grpc.server.testing.FakeServerCall
import kotlinx.rpc.grpc.server.testing.ServerCallEvent
import kotlinx.rpc.grpc.server.testing.assertNoViolations
import kotlinx.rpc.grpc.server.testing.rawMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AdapterHarnessTest {
    @Test
    fun sendMessageBeforeHeadersRecordsAViolationAndThrows() {
        val call = FakeServerCall(rawMethod("harness.Test/Unary", GrpcMethodType.UNARY))

        val failure = assertFailsWith<IllegalStateException> { call.sendMessage(ByteArray(0)) }

        assertEquals("sendMessage before sendHeaders", failure.message)
        assertEquals(listOf("sendMessage before sendHeaders"), call.violations())
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun unaryCallSendsHeadersResponseAndOkClose() = runTest {
        val harness = AdapterHarness(this)
        val session = harness.unary { "$it-response" }

        session.listener.onMessage("request")
        session.listener.onHalfClose()
        runCurrent()

        assertEquals(listOf("sendHeaders", "sendMessage", "close(OK)"), session.call.outbound())
        val response = session.call.events.filterIsInstance<ServerCallEvent.MessageSent>().single()
        assertEquals("request-response", response.message)
        assertTrue(harness.isIdle())
        session.call.assertNoViolations()
        assertEquals(emptyList(), harness.uncaught)
    }
}
