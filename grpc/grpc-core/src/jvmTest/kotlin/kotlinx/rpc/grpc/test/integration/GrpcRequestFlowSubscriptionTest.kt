/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Regression test for KRPC-461 / grpc-kotlin #378.
 *
 * The request flow of a client/bidi streaming call must be subscribed eagerly — its initialization
 * must run as soon as collection starts, decoupled from RPC readiness — so an early failure or
 * cancellation cannot prevent or interrupt it. Previously the request flow was collected by a
 * dispatched coroutine that first waited for readiness, so cancelling before the call became ready
 * could skip subscription entirely.
 */
class GrpcRequestFlowSubscriptionTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `request flow is subscribed eagerly even if the call is cancelled before readiness`() = runGrpcTest { client ->
        val service = client.withService<EchoService>()
        var requestFlowSubscribed = false

        coroutineScope {
            // UNDISPATCHED so the whole call setup runs synchronously up to the first suspension
            // point before we cancel: an eagerly-subscribed request flow has already run its
            // initialization, a lazily/dispatched one has not.
            val job = launch(start = CoroutineStart.UNDISPATCHED) {
                service.bidirectionalStreamingEcho(
                    flow {
                        requestFlowSubscribed = true
                        awaitCancellation()
                    },
                ).collect { }
            }
            job.cancel()
            job.join()
        }

        assertTrue(
            requestFlowSubscribed,
            "The request flow must be subscribed (its initialization must run) eagerly, even " +
                "when the call is cancelled before it becomes ready (KRPC-461 / grpc-kotlin #378).",
        )
    }
}
