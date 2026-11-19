/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.cause
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Characterizes contract violations. These outcomes aren't API guarantees. */
class AdapterInvalidInterceptorUsageTest {
    @Test
    fun proceedingTwiceWithTheSameRequestsFailsOnTheSecondCollection() = repeatedCollectionTest(proceedTwice = true)

    @Test
    fun collectingTheProceededFlowTwiceFailsOnTheSecondCollection() = repeatedCollectionTest(proceedTwice = false)

    @Test
    fun proceedingTwiceWithReplacementRequestsCanInvokeTheServiceTwice() = adapterTest { fixture ->
        val invocations = mutableListOf<String>()
        val interceptor = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                val first = proceed(flowOf("first" as Request))
                val second = proceed(flowOf("second" as Request))
                return flow {
                    emitAll(first)
                    emitAll(second)
                }
            }
        }
        val session = fixture.unary(listOf(interceptor)) {
            invocations += it
            "$it-response"
        }

        fixture.runAdapterTasks()

        assertEquals(listOf("first", "second"), invocations)
        assertEquals(0, session.call.requestedTotal())
        session.call.assertMessages("first-response", "second-response")
        session.call.assertClosedWith(GrpcStatusCode.OK)
    }

    private fun repeatedCollectionTest(proceedTwice: Boolean) = adapterTest { fixture ->
        var invocationCount = 0
        val interceptor = object : GrpcServerInterceptor {
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                val first = proceed(request)
                val second = if (proceedTwice) {
                    proceed(request)
                } else {
                    first
                }
                return flow {
                    emitAll(first)
                    emitAll(second)
                }
            }
        }
        val session = fixture.unary(listOf(interceptor)) {
            invocationCount++
            "$it-response"
        }
        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(1, invocationCount)
        session.call.assertMessages("request-response")
        val close = session.call.assertClosedWith(GrpcStatusCode.UNKNOWN)
        val failure = assertIs<IllegalStateException>(close.status.cause)
        assertEquals("requests flow can only be collected once", failure.message)
    }
}
