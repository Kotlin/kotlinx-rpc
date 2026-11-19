/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.adapter

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.server.GrpcServerCallScope
import kotlinx.rpc.grpc.server.GrpcServerInterceptor
import kotlinx.rpc.grpc.server.testing.adapterTest
import kotlinx.rpc.grpc.server.testing.assertClosedWith
import kotlinx.rpc.grpc.server.testing.assertMessages
import kotlinx.rpc.grpc.statusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class AdapterServiceRecoveryTest {
    @Test
    fun interceptorRecoversFromAUnaryServiceFailure() = recoveryTest(FailureSite.UNARY)

    @Test
    fun interceptorRecoversWhenTheServiceThrowsBeforeReturningItsFlow() = recoveryTest(FailureSite.FLOW_CREATION)

    @Test
    fun interceptorRecoversAfterTheServiceEmitsAResponse() = recoveryTest(FailureSite.FLOW_COLLECTION)

    private fun recoveryTest(site: FailureSite) = adapterTest { fixture ->
        val failure = GrpcStatusCode.ABORTED.asException(
            "Service failed",
            GrpcMetadata { append("x-error", "unrecovered failure") },
        )
        val failures = mutableListOf<Throwable>()
        val closes = mutableListOf<GrpcStatusCode>()
        var invocationCount = 0
        var cleanupCount = 0
        val recovery = object : GrpcServerInterceptor {
            @Suppress("UNCHECKED_CAST")
            override fun <Request, Response> GrpcServerCallScope<Request, Response>.intercept(
                request: Flow<Request>,
            ): Flow<Response> {
                responseTrailers.append("x-recovery", "recovered")
                onClose { status, _ -> closes += status.statusCode }
                return proceed(request).catch { error ->
                    if (error !is GrpcStatusException) {
                        throw error
                    }
                    failures += error
                    emit("recovered" as Response)
                }
            }
        }
        val session = when (site) {
            FailureSite.UNARY -> fixture.unary(listOf(recovery)) {
                invocationCount++
                try {
                    throw failure
                } finally {
                    cleanupCount++
                }
            }
            FailureSite.FLOW_CREATION -> fixture.serverStreaming(listOf(recovery)) {
                invocationCount++
                try {
                    throw failure
                } finally {
                    cleanupCount++
                }
            }
            FailureSite.FLOW_COLLECTION -> fixture.serverStreaming(listOf(recovery)) {
                flow {
                    invocationCount++
                    try {
                        emit("first")
                        throw failure
                    } finally {
                        cleanupCount++
                    }
                }
            }
        }

        fixture.runAdapterTasks()
        session.deliverRequest("request")
        session.listener.onHalfClose()
        fixture.runAdapterTasks()

        assertEquals(1, invocationCount)
        assertEquals(1, cleanupCount)
        assertSame(failure, failures.single())
        val expected = if (site == FailureSite.FLOW_COLLECTION) {
            listOf("first", "recovered")
        } else {
            listOf("recovered")
        }
        session.call.assertMessages(expected)
        val close = session.call.assertClosedWith(GrpcStatusCode.OK)
        assertEquals("recovered", close.trailers["x-recovery"])
        assertNull(close.trailers["x-error"])
        assertEquals(listOf(GrpcStatusCode.OK), closes)
    }

    private enum class FailureSite { UNARY, FLOW_CREATION, FLOW_COLLECTION }
}
