/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.Http2TestPeer
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.withHttp2Peer
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class ServerKeepaliveTest {
    @Test
    fun keepaliveAcceptsAnAcknowledgementAndClosesAnUnresponsiveConnection() = grpcServerTest {
        start(serverConfig = {
            keepAlive {
                time = 10.seconds
                timeout = 5.seconds
            }
        })
        withHttp2Peer(server.port) {
            withTimeout(20.seconds) { acknowledgePing(awaitPing()) }
            val acknowledgedAt = TimeSource.Monotonic.markNow()
            withTimeout(20.seconds) { awaitPing() }
            assertTrue(acknowledgedAt.elapsedNow() >= 9.seconds, "The next PING arrived before the configured interval")

            assertClosesAfterPingTimeout(5.seconds)
        }
    }

    @Test
    fun keepaliveUsesDefaultTimeoutWhenOnlyTheIntervalIsConfigured() = grpcServerTest {
        start(serverConfig = { keepAlive { time = 10.seconds } })

        withHttp2Peer(server.port) {
            withTimeout(20.seconds) { awaitPing() }
            assertClosesAfterPingTimeout(20.seconds)
        }
    }

    private suspend fun Http2TestPeer.assertClosesAfterPingTimeout(timeout: Duration) {
        val unansweredAt = TimeSource.Monotonic.markNow()
        withTimeout(timeout + AWAIT_TIMEOUT) { awaitClosed(acknowledgePings = false) }
        assertTrue(
            unansweredAt.elapsedNow() >= timeout - 1.seconds,
            "The server closed before the PING timeout",
        )
    }
}
