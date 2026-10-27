/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.asException
import kotlin.coroutines.coroutineContext
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Regression test for KRPC-461 and grpc-kotlin #318.
 *
 * A server status is queued when the coroutine is already cancelled. The collector throws that
 * status without a suspension, so [emitEvents] has to throw the cancellation instead. Otherwise
 * operators such as `retry()` and `catch()` get the status.
 */
class EmitEventsCancellationTest {

    @Test
    fun emitEvents_honors_cancellation_over_a_queued_server_status() = runTest {
        val events = Channel<GrpcClientCallEvents<Int>>(1)
        events.trySend(
            GrpcClientCallEvents.Closed(
                GrpcStatus(GrpcStatusCode.INTERNAL, "server boom"),
                GrpcMetadata(),
            )
        )
        events.close()

        var observed: Throwable? = null

        val scopeJob = Job(coroutineContext[Job])
        val scope = CoroutineScope(coroutineContext + scopeJob)
        scopeJob.cancel()

        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val sink = FlowCollector<GrpcClientCallEvents<Int>> { event ->
                if (event is GrpcClientCallEvents.Closed) {
                    throw event.status.asException(event.trailers)
                }
            }

            try {
                sink.emitEvents(events, requestNext = {}, onError = {})
            } catch (e: Throwable) {
                observed = e
            }
        }.join()

        assertTrue(
            observed is CancellationException,
            "A cancelled collection must fail with CancellationException, not a leaked server " +
                "status (KRPC-461 / grpc-kotlin #318). Observed: $observed",
        )
    }
}
