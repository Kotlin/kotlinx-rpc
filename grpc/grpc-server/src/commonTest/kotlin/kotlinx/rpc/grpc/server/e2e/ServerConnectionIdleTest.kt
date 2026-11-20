/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.e2e

import com.google.protobuf.kotlin.Empty
import com.google.protobuf.kotlin.invoke
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.withHttp2Peer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class ServerConnectionIdleTest {
    @Test
    fun idleConnectionClosesAndTheServerAcceptsANewConnection() = grpcServerTest {
        start(serverConfig = { maxConnectionIdle = 2.seconds })

        withHttp2Peer(server.port) {
            val connectedAt = TimeSource.Monotonic.markNow()
            withTimeout(AWAIT_TIMEOUT) { awaitClosed() }
            assertTrue(connectedAt.elapsedNow() >= 1.seconds, "The server closed before the configured idle timeout")
        }

        assertEquals(Empty {}, testService.emptyCall(Empty {}))
    }
}
