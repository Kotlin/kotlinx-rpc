/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import com.google.protobuf.kotlin.Empty
import io.grpc.testing.integration.SimpleRequest
import io.grpc.testing.integration.SimpleResponse
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.grpc.server.testing.GrpcServerTestFixture
import kotlinx.rpc.grpc.server.testing.grpcServerTest
import kotlinx.rpc.grpc.server.testing.runFixture
import kotlinx.rpc.test.TestGate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FixtureTeardownTest {
    @Test
    fun gateWithAWaiterFailsTeardownAndReleasesTheWaiter() = runTest {
        withContext(Dispatchers.Default) {
            val fixture = GrpcServerTestFixture()
            val gate = fixture.registerNewGate("left waiting")
            val waiter = launch(start = CoroutineStart.UNDISPATCHED) { gate.pass() }
            assertTrue(gate.hasWaiters)

            val failure = withContext(NonCancellable) { fixture.close(null) }

            assertContains(assertIs<AssertionError>(failure).message.orEmpty(), "left waiting")
            waiter.join()
            assertTrue(gate.isOpen)
            assertFalse(gate.hasWaiters)
        }
    }

    @Test
    fun serverCallIgnoringCancellationFailsTheJobLeakCheck() = grpcServerTest {
        val heldCall = TestGate("ignores cancellation")
        start {
            unaryCall = {
                withContext(NonCancellable) { heldCall.pass() }
                SimpleResponse {}
            }
        }

        coroutineScope {
            val response = async {
                try {
                    runCatching { testService.unaryCall(SimpleRequest {}) }.exceptionOrNull()
                } finally {
                    heldCall.open()
                }
            }
            try {
                heldCall.awaitReached(AWAIT_TIMEOUT)
                server.shutdown()
                markServerStoppedByTest()
                assertFalse(response.isCompleted)

                val failure = withContext(NonCancellable) { close(null) }

                assertContains(assertIs<AssertionError>(failure).message.orEmpty(), "1 server call jobs didn't end")
                assertNotNull(response.await())
                assertTrue(server.isTerminated)
                assertFalse(heldCall.hasWaiters)
            } finally {
                heldCall.open()
            }
        }
    }

    @Test
    fun successfulTestTerminatesTheServer() = runTest {
        withContext(Dispatchers.Default) {
            lateinit var fixture: GrpcServerTestFixture

            runFixture {
                fixture = this
                start()
                testService.emptyCall(Empty {})
            }

            assertTrue(fixture.server.isTerminated)
        }
    }
}
