/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing.harness

import com.google.protobuf.kotlin.Empty
import com.google.protobuf.kotlin.invoke
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.server.testing.GrpcServerTestFixture
import kotlinx.rpc.grpc.server.testing.runFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FixtureLifecycleTest {
    @Test
    fun launchFailureFailsTheTestAndStopsTheServer() = runTest {
        withContext(Dispatchers.Default) {
            val failure = IllegalStateException("Fixture child failed")
            lateinit var fixture: GrpcServerTestFixture

            val actual = assertFailsWith<IllegalStateException> {
                runFixture {
                    fixture = this
                    start()
                    coroutineScope {
                        launch { throw failure }
                        awaitCancellation()
                    }
                }
            }

            assertEquals(failure.message, actual.message)
            assertTrue(fixture.server.isTerminated)
        }
    }

    @Test
    fun asyncFailureFailsTheTestWithoutAwait() = runTest {
        withContext(Dispatchers.Default) {
            val failure = IllegalStateException("Fixture async failed")

            val actual = assertFailsWith<IllegalStateException> {
                runFixture {
                    coroutineScope {
                        async { throw failure }
                        awaitCancellation()
                    }
                }
            }

            assertEquals(failure.message, actual.message)
        }
    }

    @Test
    fun testScopeCancellationStopsChildrenAndTheServer() = runTest {
        withContext(Dispatchers.Default) {
            val childStopped = CompletableDeferred<Unit>()
            lateinit var fixture: GrpcServerTestFixture

            assertFailsWith<CancellationException> {
                runFixture {
                    fixture = this
                    start()
                    coroutineScope {
                        launch(start = CoroutineStart.UNDISPATCHED) {
                            try {
                                awaitCancellation()
                            } finally {
                                childStopped.complete(Unit)
                            }
                        }
                        currentCoroutineContext().cancel()
                        awaitCancellation()
                    }
                }
            }

            assertTrue(childStopped.isCompleted)
            assertTrue(fixture.server.isTerminated)
        }
    }

    @Test
    fun explicitScopeCompletesChildrenBeforeServerTeardown() = runTest {
        withContext(Dispatchers.Default) {
            val childCompleted = CompletableDeferred<Unit>()
            lateinit var fixture: GrpcServerTestFixture

            runFixture {
                fixture = this
                start()
                coroutineScope {
                    launch {
                        testService.emptyCall(Empty {})
                        assertFalse(server.isShutdown)
                        childCompleted.complete(Unit)
                    }
                }
                assertTrue(childCompleted.isCompleted)
            }

            assertTrue(fixture.server.isTerminated)
        }
    }

    @Test
    fun explicitSupervisionContainsExpectedChildFailures() = runTest {
        withContext(Dispatchers.Default) {
            val failure = IllegalStateException("Expected child failure")

            runFixture {
                supervisorScope {
                    val response = async { throw failure }
                    assertEquals(failure.message, assertFailsWith<IllegalStateException> { response.await() }.message)
                }
            }
        }
    }
}
