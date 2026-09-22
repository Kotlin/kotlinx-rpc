/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import grpc.testing.Empty
import grpc.testing.invoke
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.TestResult
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kxrpc.testing.BarrierType
import kxrpc.testing.EventType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GrpcClientTestCleanupTest {
    @Test
    fun cleanupContinuesAfterFailures() = test {
        val firstFailure = CleanupFailure("first")
        val secondFailure = CleanupFailure("second")
        val completedSteps = mutableListOf<String>()
        val cleanup = GrpcClientTestCleanup()

        cleanup.run {
            completedSteps += "first"
            throw firstFailure
        }
        cleanup.run {
            completedSteps += "second"
            throw secondFailure
        }
        cleanup.run { completedSteps += "third" }

        assertEquals(listOf("first", "second", "third"), completedSteps)
        assertSame(firstFailure, cleanup.failure())
        assertEquals(listOf(secondFailure), firstFailure.suppressedExceptions)
    }

    @Test
    fun timedOutCleanupDoesNotPreventFollowingSteps() = test {
        val cleanup = GrpcClientTestCleanup(stepTimeout = 1.milliseconds)
        var followingStepCompleted = false

        cleanup.run { awaitCancellation() }
        cleanup.run { followingStepCompleted = true }

        assertTrue(followingStepCompleted)
        assertIs<TimeoutCancellationException>(cleanup.failure())
    }

    @Test
    fun primaryFailureSuppressesCleanupFailure() {
        val primaryFailure = CleanupFailure("primary")
        val cleanupFailure = CleanupFailure("cleanup")

        assertSame(primaryFailure, combineTestAndCleanupFailures(primaryFailure, cleanupFailure))
        assertEquals(listOf(cleanupFailure), primaryFailure.suppressedExceptions)
    }

    @Test
    fun bodyFailureWithOutstandingBarrierTearsDown() = test {
        val primaryFailure = CleanupFailure("test body")

        val actualFailure = runCatching {
            executeGrpcClientTest(
                barriers = listOf(serverBarrier(BarrierType.SEND_INITIAL_HEADERS)),
            ) {
                supervisorScope {
                    val call = async { testService.emptyCall(Empty {}) }
                    awaitServerEvent(EventType.CALL_ACCEPTED)
                    assertFalse(call.isCompleted)
                    throw primaryFailure
                }
            }
        }.exceptionOrNull()

        assertSame(primaryFailure, actualFailure)
    }

    private fun test(block: suspend () -> Unit): TestResult {
        return runTestWithCoroutinesProbes(timeout = 10.seconds) { block() }
    }
}

private class CleanupFailure(message: String) : IllegalStateException(message)
