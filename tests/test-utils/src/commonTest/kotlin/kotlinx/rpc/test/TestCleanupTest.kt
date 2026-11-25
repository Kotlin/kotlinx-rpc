/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TestCleanupTest {
    @Test
    fun cleanupContinuesAfterFailures() = runTest {
        val firstFailure = CleanupFailure("first")
        val secondFailure = CleanupFailure("second")
        val completedSteps = mutableListOf<String>()
        val cleanup = TestCleanup()

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
        assertSame(firstFailure, cleanup.failure())
        assertEquals(listOf(secondFailure), firstFailure.suppressedExceptions)
    }

    @Test
    fun timedOutCleanupDoesNotPreventFollowingSteps() = runTest {
        val cleanup = TestCleanup(stepTimeout = 1.milliseconds)
        var followingStepCompleted = false

        cleanup.run { awaitCancellation() }
        cleanup.run(timeout = 10.seconds) { followingStepCompleted = true }

        assertTrue(followingStepCompleted)
        assertIs<TimeoutCancellationException>(cleanup.failure())
    }

    @Test
    fun namedTimeoutIdentifiesTheStepAndPreservesItsCause() = runTest {
        val cleanup = TestCleanup(stepTimeout = 1.milliseconds)

        cleanup.run("stop server") { awaitCancellation() }

        val failure = assertIs<AssertionError>(cleanup.failure())
        assertContains(failure.message.orEmpty(), "stop server")
        assertIs<TimeoutCancellationException>(failure.cause)
    }

    @Test
    fun successfulCleanupHasNoFailure() = runTest {
        val cleanup = TestCleanup()

        cleanup.run {}

        assertNull(cleanup.failure())
    }

    @Test
    fun primaryFailureSuppressesCleanupFailure() {
        val primaryFailure = CleanupFailure("primary")
        val cleanupFailure = CleanupFailure("cleanup")

        assertSame(primaryFailure, combineFailures(primaryFailure, cleanupFailure))
        assertEquals(listOf(cleanupFailure), primaryFailure.suppressedExceptions)
    }

    @Test
    fun cleanupFailureIsReturnedWhenTheBodySucceeds() {
        val cleanupFailure = CleanupFailure("cleanup")

        assertSame(cleanupFailure, combineFailures(null, cleanupFailure))
    }

    @Test
    fun successfulBodyAndCleanupHaveNoFailure() {
        assertNull(combineFailures(null, null))
    }

    @Test
    fun primaryFailureIsReturnedWhenCleanupSucceeds() {
        val primaryFailure = CleanupFailure("primary")

        assertSame(primaryFailure, combineFailures(primaryFailure, null))
        assertTrue(primaryFailure.suppressedExceptions.isEmpty())
    }

    @Test
    fun combiningTheSameFailureDoesNotSuppressItself() {
        val failure = CleanupFailure("shared")

        assertSame(failure, combineFailures(failure, failure))
        assertTrue(failure.suppressedExceptions.isEmpty())
    }
}

private class CleanupFailure(message: String) : IllegalStateException(message)
