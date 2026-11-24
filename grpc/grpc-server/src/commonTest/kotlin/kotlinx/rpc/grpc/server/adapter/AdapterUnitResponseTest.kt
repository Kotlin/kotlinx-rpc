/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlin.test.Test
import kotlin.test.assertEquals

class AdapterUnitResponseTest {
    @Test
    fun unitResponseTypeSendsUnitAndClosesWithOk() = adapterTest { fixture ->
        val received = mutableListOf<String>()
        val session = fixture.unaryUnit { received += it }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(listOf("request"), received)
        session.call.assertMessages(Unit)
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }
}
