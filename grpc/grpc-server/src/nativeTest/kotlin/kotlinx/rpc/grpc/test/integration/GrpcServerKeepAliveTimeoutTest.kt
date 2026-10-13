/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, InternalNativeRpcApi::class)

package kotlinx.rpc.grpc.test.integration

import cnames.structs.grpc_channel
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.internal.CallbackTag
import kotlinx.rpc.grpc.internal.CompletionQueue
import kotlinx.rpc.grpc.internal.GrpcArg
import kotlinx.rpc.grpc.internal.cinterop.GPR_CLOCK_REALTIME
import kotlinx.rpc.grpc.internal.cinterop.GPR_TIMESPAN
import kotlinx.rpc.grpc.internal.cinterop.gpr_now
import kotlinx.rpc.grpc.internal.cinterop.gpr_time_add
import kotlinx.rpc.grpc.internal.cinterop.gpr_time_from_millis
import kotlinx.rpc.grpc.internal.cinterop.gpr_timespec
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_check_connectivity_state
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_create
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_credentials_release
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_destroy
import kotlinx.rpc.grpc.internal.cinterop.grpc_channel_watch_connectivity_state
import kotlinx.rpc.grpc.internal.cinterop.grpc_connectivity_state
import kotlinx.rpc.grpc.internal.cinterop.grpc_insecure_credentials_create
import kotlinx.rpc.grpc.internal.cinterop.grpc_tracer_set_enabled
import kotlinx.rpc.grpc.internal.shim.InternalNativeRpcApi
import kotlinx.rpc.grpc.internal.toRaw
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.test.captureStdErr
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class GrpcServerKeepAliveTimeoutTest {
    @Test
    fun `server closes unresponsive peers and keeps responsive peers connected`() = runBlocking {
        // C-core timers use real time, so this test uses runBlocking instead of the coroutine test scheduler.
        val logs = captureServerLogs {
            val server = GrpcServer(0) {
                keepAlive {
                    time = 10.seconds
                    timeout = 1.seconds
                }
            }
            try {
                server.start()
                coroutineScope {
                    listOf(false, true).map { acknowledgePings ->
                        async { exercisePeer(server.port, acknowledgePings) }
                    }.awaitAll()
                }
            } finally {
                withContext(NonCancellable) {
                    server.shutdownNow()
                    server.awaitTermination(30.seconds)
                }
            }
        }
        assertContains(logs, "Ping timeout. Closing transport.")
        val pingedConnections = Regex("""SERVER\[(.*?)]: Ping \d+ sent""")
            .findAll(logs).map { it.groupValues[1] }.toSet()
        assertEquals(2, pingedConnections.size, "The server must ping both peers")
    }

    private suspend fun exercisePeer(serverPort: Int, acknowledgePings: Boolean) {
        val queue = CompletionQueue()
        var channel: CPointer<grpc_channel>? = null
        try {
            val credentials = checkNotNull(grpc_insecure_credentials_create())
            try {
                channel = memScoped {
                    // C-core can suppress PING ACKs and still complete the HTTP/2 handshake.
                    // https://github.com/grpc/grpc/blob/v1.81.1/src/core/ext/transport/chttp2/transport/chttp2_transport.cc#L452
                    val args = listOf(
                        GrpcArg.Integer("grpc.http2.ack_pings", if (acknowledgePings) 1 else 0),
                        GrpcArg.Integer("grpc.http2.bdp_probe", 0),
                        GrpcArg.Integer("grpc.keepalive_time_ms", Int.MAX_VALUE),
                    ).toRaw(this)
                    checkNotNull(grpc_channel_create("127.0.0.1:$serverPort", credentials, args.ptr))
                }
            } finally {
                grpc_channel_credentials_release(credentials)
            }
            val peer = checkNotNull(channel)
            withTimeout(30.seconds) {
                var state = grpc_channel_check_connectivity_state(peer, 1)
                while (state != grpc_connectivity_state.GRPC_CHANNEL_READY) {
                    assertTrue(awaitStateChange(peer, queue, state, 30.seconds), "Peer failed to connect")
                    state = grpc_channel_check_connectivity_state(peer, 0)
                }
            }
            val disconnected = awaitStateChange(peer, queue, grpc_connectivity_state.GRPC_CHANNEL_READY, 15.seconds)
            assertEquals(!acknowledgePings, disconnected, "Unexpected connection state for acknowledgePings=$acknowledgePings")
        } finally {
            withContext(NonCancellable) {
                channel?.let { grpc_channel_destroy(it) }
                val closed = CompletableDeferred<Unit>()
                queue.shutdown().onComplete { closed.complete(Unit) }
                closed.await()
            }
        }
    }

    private suspend fun awaitStateChange(
        channel: CPointer<grpc_channel>,
        queue: CompletionQueue,
        state: grpc_connectivity_state,
        timeout: Duration,
    ): Boolean {
        val result = CompletableDeferred<Boolean>()
        val deadline: CValue<gpr_timespec> = gpr_time_add(
            gpr_now(GPR_CLOCK_REALTIME), gpr_time_from_millis(timeout.inWholeMilliseconds, GPR_TIMESPAN),
        )
        grpc_channel_watch_connectivity_state(
            channel, state, deadline, queue.raw, CallbackTag.anonymous { result.complete(it) },
        )
        return result.await()
    }

    private suspend fun captureServerLogs(block: suspend () -> Unit): String = captureStdErr {
        try {
            grpc_tracer_set_enabled("http", 1)
            grpc_tracer_set_enabled("http2_ping", 1)
            block()
        } finally {
            grpc_tracer_set_enabled("http", 0)
            grpc_tracer_set_enabled("http2_ping", 0)
        }
    }
}
