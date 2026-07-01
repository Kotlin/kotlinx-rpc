/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlin.coroutines.coroutineContext
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Regression test for KRPC-461 / grpc-kotlin #318.
 *
 * Deterministically reproduces the cancellation race in [emitResponses]: a server-originated status
 * sits in the already-closed response channel while the surrounding coroutine is cancelled. The
 * for-loop reads the closed channel through a non-suspending fast path that does not observe
 * cancellation, so without the cancellation guard the server status would be rethrown instead of a
 * [CancellationException], leaking it to operators like `retry()`/`catch()`.
 */
class EmitResponsesCancellationTest {

    @Test
    fun emitResponses_honors_cancellation_over_a_closed_channel_server_status() = runTest {
        val responses = Channel<Int>(1)
        // A server-originated status delivered through onClose (cause == null on the wire) and stored
        // as the channel's close-cause: exactly what the cancelled for-loop must not leak.
        responses.close(GrpcStatusException(GrpcStatus(GrpcStatusCode.INTERNAL, "server boom")))

        var observed: Throwable? = null

        val scopeJob = Job(coroutineContext[Job])
        val scope = CoroutineScope(coroutineContext + scopeJob)
        // Cancel the collector before it reads the (already closed) channel.
        scopeJob.cancel()

        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val sink = FlowCollector<Int> { /* values are dropped; the channel is empty */ }
            try {
                sink.emitResponses(responses, requestNext = {}, onError = {})
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
