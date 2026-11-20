/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.server.testing.AWAIT_TIMEOUT
import kotlinx.rpc.test.TestGate
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class TestGateTest {
    @Test
    fun passSuspendsUntilTheGateOpens() = runTest {
        withContext(Dispatchers.Default) {
            val gate = TestGate("response")
            val passage = async(start = CoroutineStart.UNDISPATCHED) { gate.pass() }

            gate.awaitReached(AWAIT_TIMEOUT)
            assertFalse(gate.isOpen)
            assertTrue(gate.hasWaiters)
            assertFalse(passage.isCompleted)

            gate.open()
            passage.await()

            assertTrue(gate.isOpen)
            assertFalse(gate.hasWaiters)
        }
    }

    @Test
    fun passAfterOpenCompletesImmediately() = runTest {
        withContext(Dispatchers.Default) {
            val gate = TestGate("already open")
            gate.open()

            val passage = async(start = CoroutineStart.UNDISPATCHED) { gate.pass() }

            assertTrue(passage.isCompleted)
            passage.await()
            assertFalse(gate.hasWaiters)
        }
    }

    @Test
    fun cancellingTheWaitingCoroutineClearsHasWaiters() = runTest {
        withContext(Dispatchers.Default) {
            val gate = TestGate("cancelled")
            val waiter = launch(start = CoroutineStart.UNDISPATCHED) { gate.pass() }
            assertTrue(gate.hasWaiters)

            waiter.cancelAndJoin()

            assertFalse(gate.hasWaiters)
            assertFalse(gate.isOpen)
        }
    }

    @Test
    fun awaitReachedTimesOutAndIdentifiesTheGate() = runTest {
        withContext(Dispatchers.Default) {
            val gate = TestGate("unreached")

            val failure = assertFailsWith<AssertionError> { gate.awaitReached(1.milliseconds) }

            assertContains(failure.message.orEmpty(), "unreached")
            assertFalse(gate.hasWaiters)
        }
    }
}
