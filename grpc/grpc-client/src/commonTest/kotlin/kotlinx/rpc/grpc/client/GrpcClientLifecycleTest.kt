/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import grpc.testing.Empty
import grpc.testing.invoke
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.client.testing.serverBarrier
import kotlinx.rpc.grpc.client.testing.successfulUnaryEvents
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.milliseconds

class GrpcClientLifecycleTest {
    @Test
    fun gracefulShutdownLetsAnActiveCallFinishAndRejectsNewCalls() = grpcClientTest(
        barriers = listOf(serverBarrier(BarrierType.CLOSE_CALL)),
    ) {
        var barrierReleased = false
        try {
            supervisorScope {
                val activeCall = async { testService.emptyCall(Empty {}) }
                awaitServerEvent(EventType.RESPONSE_MESSAGE_SENT)

                shutdownDataClient()
                shutdownDataClient()
                assertNotNull(runCatching { testService.emptyCall(Empty {}) }.exceptionOrNull())

                releaseServerBarrier(BarrierType.CLOSE_CALL)
                barrierReleased = true
                assertEquals(Empty {}, activeCall.await())
                awaitDataClientTermination()

                shutdownDataClientNow()
                shutdownDataClientNow()
                assertServerTrace(successfulUnaryEvents())
            }
        } finally {
            if (!barrierReleased) releaseServerBarrier(BarrierType.CLOSE_CALL)
        }
    }

    @Test
    fun awaitTerminationReturnsAfterItsTimeoutWithoutShuttingDownTheClient() = grpcClientTest {
        awaitDataClientTermination(50.milliseconds)

        assertEquals(Empty {}, testService.emptyCall(Empty {}))
        assertUnaryLifecycle()
    }
}
