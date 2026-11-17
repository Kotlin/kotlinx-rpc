/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.server.testing.FakeServerCall
import kotlinx.rpc.grpc.server.testing.rawMethod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AdapterTestFixtureTest {
    @Test
    fun sendMessageBeforeHeadersRecordsAViolationAndThrows() {
        val call = FakeServerCall(rawMethod("harness.Test/Unary", GrpcMethodType.UNARY))

        val failure = assertFailsWith<IllegalStateException> { call.sendMessage(ByteArray(0)) }

        assertEquals("sendMessage before sendHeaders", failure.message)
        assertEquals(listOf("sendMessage before sendHeaders"), call.violations())
    }

    @Test
    fun unaryCallSendsHeadersResponseAndOkClose() = adapterTest { fixture ->
        val session = fixture.unary { "$it-response" }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("sendHeaders", "sendMessage", "close(OK)"), session.call.outbound())
        session.call.assertMessages("request-response")
    }
}
