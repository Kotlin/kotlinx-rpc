/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.server.testing.runAdapterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

class AdapterTestRunnerTest {
    @Test
    fun unfinishedCallFailsFinalVerification() = runTest {
        val failure = assertFailsWith<AssertionError> {
            runAdapterTest(this) { fixture ->
                fixture.bidiStreaming { it }
                fixture.runAdapterTasks()
            }
        }

        assertContains(failure.message.orEmpty(), "Adapter call jobs are still active")
    }

    @Test
    fun violationInAnEarlierSessionFailsFinalVerification() = runTest {
        val failure = assertFailsWith<AssertionError> {
            runAdapterTest(this) { fixture ->
                val first = fixture.unary { it }
                assertFailsWith<IllegalStateException> { first.call.sendMessage("without headers") }
                val second = fixture.unary { it }

                fixture.runAdapterTasks()
                for (session in listOf(first, second)) {
                    session.deliverRequest("request")
                    session.listener.onHalfClose()
                }
                fixture.runAdapterTasks()
            }
        }

        assertContains(failure.message.orEmpty(), "sendMessage before sendHeaders")
    }

    @Test
    fun unverifiedUncaughtFailureFailsFinalVerification() = runTest {
        val failure = assertFailsWith<AssertionError> {
            runAdapterTest(this) { fixture ->
                val session = fixture.unary { it }
                session.call.beforeClose = { _, _ -> error("unhandled close failure") }

                fixture.runAdapterTasks()
                session.deliverRequest("request")
                session.listener.onHalfClose()
                fixture.runAdapterTasks()
            }
        }

        assertContains(failure.message.orEmpty(), "unhandled close failure")
    }

    @Test
    fun verifyingOneUncaughtFailureDoesNotHideALaterFailure() = runTest {
        val failure = assertFailsWith<AssertionError> {
            runAdapterTest(this) { fixture ->
                val first = fixture.unary { it }
                first.call.beforeClose = { _, _ -> error("expected close failure") }

                fixture.runAdapterTasks()
                first.deliverRequest("first")
                first.listener.onHalfClose()
                fixture.runAdapterTasks()

                fixture.assertUncaught { errors ->
                    assertEquals("expected close failure", assertIs<IllegalStateException>(errors.single()).message)
                }

                val second = fixture.unary { it }
                second.call.beforeClose = { _, _ -> error("unexpected later failure") }

                fixture.runAdapterTasks()
                second.deliverRequest("second")
                second.listener.onHalfClose()
                fixture.runAdapterTasks()
            }
        }

        assertContains(failure.message.orEmpty(), "unexpected later failure")
    }

    @Test
    fun testFailureRemainsPrimaryWhenFinalVerificationAlsoFails() = runTest {
        val originalFailure = TestFailure()

        val failure = assertFailsWith<TestFailure> {
            runAdapterTest(this) { fixture ->
                fixture.unary { it }
                throw originalFailure
            }
        }

        assertSame(originalFailure, failure)
        val verificationFailure = assertIs<AssertionError>(failure.suppressedExceptions.single())
        assertContains(verificationFailure.message.orEmpty(), "Adapter call jobs are still active")
    }

    private class TestFailure : Exception("test body failed")
}
