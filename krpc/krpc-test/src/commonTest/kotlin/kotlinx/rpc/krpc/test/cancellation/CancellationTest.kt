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
import kotlin.test.*

class CancellationTest {
    @Test
    fun testCancelRequestScope() = runCancellationTest {
        val cancellingRequestJob = launch {
            service.longRequest()
        }

        val aliveRequestJob = launch {
            service.longRequest()
        }

        cancellingRequestJob.cancelAndJoin()
        serverInstance().fence.complete(Unit)
        aliveRequestJob.join()

        assertFalse(aliveRequestJob.isCancelled, "Expected aliveRequestJob not to be cancelled")
        assertTrue(cancellingRequestJob.isCancelled, "Expected cancellingRequestJob to be cancelled")
        assertEquals(1, serverInstances.single().waitCounter.value, "Expected one request to be cancelled")

        checkAlive()
        stopAllAndJoin()
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

        assertEquals(1, serverInstances.single().waitCounter.value, "Error should not cancel parallel request")

        checkAlive()
        stopAllAndJoin()
    }

    @Test
    fun testCancelClient() = runCancellationTest {
        val firstRequestJob = launch {
            service.longRequest()
        }

        val secondService = client.withService<CancellationService>()

        val secondRequestJob = launch {
            secondService.longRequest()
        }

        serverInstance().awaitWaitCounter(2)
        client.close()
        firstRequestJob.join()
        secondRequestJob.join()

        assertTrue(firstRequestJob.isCancelled, "Expected firstRequestJob to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")

        assertEquals(0, serverInstances.sumOf { it.successCounter.value }, "Expected no requests to succeed")

        client.awaitCompletion()
        server.awaitCompletion()

        checkAlive(clientAlive = false, serverAlive = false)
        stopAllAndJoin()
    }

    @Test
    fun testCancelServer() = runCancellationTest {
        val firstRequestJob = launch {
            service.longRequest()
        }

        val secondService = client.withService<CancellationService>()

        val secondRequestJob = launch {
            secondService.longRequest()
        }

        serverInstance().awaitWaitCounter(2) // wait for requests to reach server
        server.close()
        firstRequestJob.join()
        secondRequestJob.join()

        assertTrue(firstRequestJob.isCancelled, "Expected firstRequestJob to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")

        assertEquals(0, serverInstances.sumOf { it.successCounter.value }, "Expected no requests to succeed")

        client.awaitCompletion()
        server.awaitCompletion()

        checkAlive(clientAlive = false, serverAlive = false)
        stopAllAndJoin()
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
