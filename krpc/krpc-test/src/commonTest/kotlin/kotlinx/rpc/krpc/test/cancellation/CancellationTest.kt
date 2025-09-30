/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.withService
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class CancellationTest {
    @Test
    fun testCancelRequestScope() = runCancellationTest {
        val cancellingRequestJob = launch {
            service.longRequest()
        }

        val aliveRequestJob = launch {
            service.longRequest()
        }

        serverInstance().waitCounter.await(2)
        cancellingRequestJob.cancelAndJoin()
        serverInstance().cancellationsCounter.await(1)
        serverInstance().fence.complete(Unit)
        aliveRequestJob.join()

        assertFalse(aliveRequestJob.isCancelled, "Expected aliveRequestJob not to be cancelled")
        assertTrue(cancellingRequestJob.isCancelled, "Expected cancellingRequestJob to be cancelled")
        assertEquals(1, serverInstance().successCounter.value, "Expected one request to be cancelled")

        checkAlive()
        stopAllAndJoin()

        assertEquals(1, serverInstance().successCounter.value, "Expected one request to succeed")
        assertEquals(1, serverInstance().cancellationsCounter.value, "Expected one request to be cancelled")
    }

    @Test
    fun testCallException() = runCancellationTest {
        val requestJob = launch {
            service.longRequest()
        }

        serverInstance().firstIncomingConsumed.await()

        val exceptionRequestJob = launch {
            try {
                service.callException()
            } catch (@Suppress("SwallowedException") e: Throwable) {
                throw CancellationException(e.message)
            }

            fail("Expected exception to be thrown")
        }

        exceptionRequestJob.join()
        serverInstance().fence.complete(Unit)
        requestJob.join()

        assertFalse(requestJob.isCancelled, "Expected requestJob not to be cancelled")
        assertTrue(exceptionRequestJob.isCancelled, "Expected exception in callException call")

        assertEquals(1, serverInstance().successCounter.value, "Error should not cancel parallel request")

        checkAlive()
        stopAllAndJoin()

        assertEquals(0, serverInstance().cancellationsCounter.value, "Expected no requests to be cancelled")
    }

    @Test
    fun testServerRequestCancellation() = runCancellationTest {
        supervisorScope {
            val requestJob = launch {
                service.serverCancellation()
            }

            requestJob.join()

            assertTrue(requestJob.isCancelled, "Expected requestJob to be cancelled")
        }

        checkAlive()
        stopAllAndJoin()
    }

    @Test
    fun testCancellationInServerStream() = runCancellationTest {
        supervisorScope {
            var ex: CancellationException? = null
            val requestJob = launch {
                try {
                    service.cancellationInIncomingStream().toList()
                } catch (e: CancellationException) {
                    ex = e
                    throw e
                }
            }

            requestJob.join()

            assertTrue(requestJob.isCancelled, "Expected requestJob to be cancelled")
            assertNotNull(ex, "Expected requestJob to be cancelled with a CancellationException")
        }

        checkAlive()
        stopAllAndJoin()
    }

    @Test
    fun testCancellationInClientStream() = runCancellationTest {
        supervisorScope {
            val requestJob = launch {
                service.cancellationInOutgoingStream(
                    stream = flow {
                        emit(42)
                        println("[testCancellationInClientStream] emit 42")
                        emit(43)
                        println("[testCancellationInClientStream] emit 43")
                    },
                    cancelled = flow {
                        emit(1)
                        println("[testCancellationInClientStream] emit 1")
                        serverInstance().firstIncomingConsumed.await()
                        println("[testCancellationInClientStream] firstIncomingConsumed")
                        throw CancellationException("cancellationInClientStream")
                    },
                )
            }

            requestJob.join()
            println("[testCancellationInClientStream] Request job finished")
            serverInstance().consumedAll.await()
            println("[testCancellationInClientStream] Server consumed all")

            assertFalse(requestJob.isCancelled, "Expected requestJob not to be cancelled")
            assertContentEquals(listOf(42, 43), serverInstance().consumedIncomingValues)
        }
        println("[testCancellationInClientStream] Scope finished")

        checkAlive()
        stopAllAndJoin()
        println("[testCancellationInClientStream] All done")

        assertEquals(1, serverInstance().cancellationsCounter.value, "Expected 1 request to be cancelled")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testCancelClient() = runCancellationTest {
        val firstRequestJob = launch {
            println("[testCancelClient] firstRequestJob started")
            service.longRequest()
        }

        val secondService = client.withService<CancellationService>()

        val secondRequestJob = launch {
            println("[testCancelClient] secondRequestJob started")
            secondService.longRequest()
        }

        val clientFlowJob = launch {
            service.outgoingStream(flow {
                emit(0)
                println("[testCancelClient] emit 0")
                serverInstance().fence.await()
                println("[testCancelClient] fence awaited")
                emit(1)
            })
        }

        val serverFlowJob = launch {
            try {
                secondService.incomingStream().toList()
                fail("Expected cancellation of the client to cancel the server flow")
            } catch (e : CancellationException) {
                serverInstance().cancellationsCounter.increment()
                throw e
            }
        }

        println("[testCancelClient] Requests sent")
        serverInstance().waitCounter.await(4)
        println("[testCancelClient] Requests reached")
        client.close()
        client.awaitCompletion()
        println("[testCancelClient] Client stopped")
        server.awaitCompletion()
        println("[testCancelClient] Server stopped")
        firstRequestJob.join()
        println("[testCancelClient] First request finished")
        secondRequestJob.join()
        println("[testCancelClient] Second request finished")
        clientFlowJob.join()
        println("[testCancelClient] Client flow finished")

        serverInstance().fence.complete(Unit)
        serverFlowJob.join()
        println("[testCancelClient] Server flow finished")

        serverInstance().cancellationsCounter.await(4)
        println("[testCancelClient] Server cancellations counted")

        assertTrue(firstRequestJob.isCancelled, "Expected firstRequestJob to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")
        assertTrue(clientFlowJob.isCancelled, "Expected clientFlowJob to be cancelled")
        assertTrue(serverFlowJob.isCancelled, "Expected serverFlowJob to be cancelled")

        assertEquals(0, serverInstances.sumOf { it.successCounter.value }, "Expected no requests to succeed")

        checkAlive(clientAlive = false, serverAlive = false)
        stopAllAndJoin()
        println("[testCancelClient] All done")

        assertEquals(4, serverInstance().cancellationsCounter.value, "Expected 4 requests to be cancelled")
    }

    @Test
    fun testCancelServer() = runCancellationTest {
        val firstRequestJob = launch {
            println("[testCancelServer] firstRequestJob started")
            service.longRequest()
        }

        val secondService = client.withService<CancellationService>()

        val secondRequestJob = launch {
            println("[testCancelServer] secondRequestJob started")
            secondService.longRequest()
        }

        val clientFlowJob = launch {
            service.outgoingStream(flow {
                emit(0)
                println("[testCancelServer] emit 0")
                serverInstance().fence.await()
                println("[testCancelServer] fence awaited")
                emit(1)
            })
        }

        val serverFlowJob = launch {
            try {
                secondService.incomingStream().toList()
                fail("Expected cancellation of the client to cancel the server flow")
            } catch (e : CancellationException) {
                serverInstance().cancellationsCounter.increment()
                throw e
            }
        }

        println("[testCancelServer] Requests sent")
        serverInstance().waitCounter.await(4) // wait for requests to reach server
        println("[testCancelServer] Requests reached")
        server.close()
        server.awaitCompletion()
        println("[testCancelServer] Server stopped")
        client.awaitCompletion()
        println("[testCancelServer] Client stopped")
        firstRequestJob.join()
        println("[testCancelServer] First request finished")
        secondRequestJob.join()
        println("[testCancelServer] Second request finished")
        clientFlowJob.join()
        println("[testCancelServer] Client flow finished")

        serverInstance().fence.complete(Unit)
        serverFlowJob.join()
        println("[testCancelServer] Server flow finished")

        serverInstance().cancellationsCounter.await(4)
        println("[testCancelServer] Server cancellations counted")

        assertTrue(firstRequestJob.isCancelled, "Expected firstRequestJob to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")
        assertTrue(clientFlowJob.isCancelled, "Expected clientFlowJob to be cancelled")
        assertTrue(serverFlowJob.isCancelled, "Expected serverFlowJob to be cancelled")

        assertEquals(0, serverInstances.sumOf { it.successCounter.value }, "Expected no requests to succeed")

        checkAlive(clientAlive = false, serverAlive = false)
        stopAllAndJoin()
        println("[testCancelServer] All done")

        assertEquals(4, serverInstance().cancellationsCounter.value, "Expected 4 requests to be cancelled")
    }

    @Test
    fun testStreamOutgoing() = runCancellationTest {
        service.outgoingStream(
            flow {
                repeat(2) {
                    emit(it)

                    if (it == 0) {
                        serverInstance().firstIncomingConsumed.await()
                    }
                }
            }
        )

        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0, 1), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testOutgoingFlowLifetime() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()

        service.outgoingStreamAsync(resumableFlow(fence))

        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testStreamIncoming() = runCancellationTest {
        var first: Int = -1
        val flow = service.incomingStream()

        val consumed = flow.mapNotNull {
            if (it == 0) {
                first = it
                serverInstance().fence.complete(Unit)
                null
            } else {
                it
            }
        }.toList()

        assertEquals(0, first)
        assertContentEquals(listOf(1), consumed)

        stopAllAndJoin()
    }

    @Test
    fun testRequestCancellationCancelsStream() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()

        val job = launch {
            service.outgoingStreamWithDelayedResponse(resumableFlow(fence))
        }

        serverInstance().firstIncomingConsumed.await()

        job.cancel("Test request cancelled")
        job.join()
        assertTrue("Job must be canceled") { job.isCancelled }

        // close by request cancel and not scope closure
        serverInstance().consumedAll.await()

        serverInstance().cancellationsCounter.await(1)

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testRequestCancellationCancelsStreamButNotOthers() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()
        val job = launch {
            service.outgoingStreamWithDelayedResponse(resumableFlow(fence))
        }

        val flow = service.incomingStream()

        serverInstance().firstIncomingConsumed.await()

        job.cancel("Test request cancelled")
        job.join()
        assertTrue("Job must be canceled") { job.isCancelled }
        serverInstance().fence.complete(Unit)

        // close by request cancel and not scope closure
        serverInstance().consumedAll.await()

        serverInstance().cancellationsCounter.await(1)

        val result = flow.toList()

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)
        assertContentEquals(List(2) { it }, result)

        stopAllAndJoin()
    }

    @Test
    fun testClientCancellationCancelsStream() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()
        launch {
            service.outgoingStream(resumableFlow(fence))
        }

        serverInstance().firstIncomingConsumed.await()

        client.close("Test request cancelled")
        client.awaitCompletion()

        serverInstance().consumedAll.await()

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testCancelledClientCancelsFlows() = runCancellationTest {
        val flow = service.incomingStream()
        var caught: Throwable? = null

        flow.catch {
            caught = it
        }.collect {
            if (it == 0) {
                client.close()
            } else {
                fail("Expected the request to fail with cancellation of the client")
            }
        }

        assertNotNull(caught, "Expected cancellation exception")

        stopAllAndJoin()
    }

    @Test
    fun testCancelledClientCancelsRequest() = runCancellationTest {
        launch {
            serverInstance().firstIncomingConsumed.await()
            client.close("Cancelled by test")
        }

        try {
            service.longRequest()
            fail("Expected the request to fail with cancellation of the client")
        } catch (_: CancellationException) {
            // success
        }

        stopAllAndJoin()

        assertEquals(1, serverInstance().cancellationsCounter.value, "Expected 1 request to be cancelled")
    }

    @Test
    fun testCancellingNonSuspendable() = runCancellationTest {
        val flow = service.nonSuspendable()
        val firstDone = CompletableDeferred<Unit>()
        val requestJob = launch {
            flow.collect {
                if (it == 0) {
                    firstDone.complete(Unit)
                }
            }
        }
        firstDone.await()

        requestJob.cancel("Cancelled by test")
        requestJob.join()
        serverInstance().nonSuspendableFinished.await()

        assertEquals(false, serverInstance().nonSuspendableSecond)

        checkAlive()
        stopAllAndJoin()
    }

    @Test
    @Ignore // KRPC-169
    fun testGCNonSuspendable() = runCancellationTest {
        val firstDone = CompletableDeferred<Unit>()
        val latch = CompletableDeferred<Unit>()
        val requestJob = processFlowAndLeaveUnusedForGC(firstDone, latch)

        firstDone.await()
        // here, GC should collect the flow.
        // so far, it works well locally and on TC,
        // so, until it is flaky, we're good
        serverInstance().nonSuspendableFinished.await()

        assertEquals(false, serverInstance().nonSuspendableSecond)
        latch.complete(Unit)
        requestJob.join()

        checkAlive()
        stopAllAndJoin()
    }

    private fun CancellationToolkit.processFlowAndLeaveUnusedForGC(
        firstDone: CompletableDeferred<Unit>,
        latch: CompletableDeferred<Unit>,
    ): Job {
        val flow = service.nonSuspendable()
        val requestJob = launch {
            flow.first()
            firstDone.complete(Unit)
            latch.await()
        }

        return requestJob
    }

    private fun CancellationToolkit.checkAlive(
        clientAlive: Boolean = true,
        serverAlive: Boolean = true,
        transportAlive: Boolean = true,
    ) {
        checkAlive(clientAlive, client.internalScope, "client")
        checkAlive(serverAlive, server.internalScope, "server")
        checkAlive(transportAlive, transport, "transport")
    }

    private fun checkAlive(expectAlive: Boolean, scope: CoroutineScope, name: String) {
        if (expectAlive) {
            assertTrue(scope.isActive, "$name expected to be alive")
        } else {
            assertTrue(scope.isCancelled, "$name expected to be cancelled")
            assertTrue(scope.isCompleted, "$name expected to be completed")
        }
    }

    private val CoroutineScope.isCompleted get() = coroutineContext.job.isCompleted
    private val CoroutineScope.isCancelled get() = coroutineContext.job.isCancelled

    private suspend fun CancellationToolkit.stopAllAndJoin() = transport.coroutineContext.job.cancelAndJoin()
}
