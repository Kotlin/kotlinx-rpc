/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.withService
import kotlin.test.*

class CancellationTest {
    @Test
    fun testCancelRequestScope() = runCancellationTest {
        val cancellingRequestJob = launch {
            service.serverDelay(100)
        }

        val aliveRequestJob = launch {
            service.serverDelay(300)
        }

        cancellingRequestJob.cancelAndJoin()
        aliveRequestJob.join()

        assertFalse(aliveRequestJob.isCancelled, "Expected aliveRequestJob not to be cancelled")
        assertTrue(cancellingRequestJob.isCancelled, "Expected cancellingRequestJob to be cancelled")
        assertEquals(1, serverInstances.single().delayCounter.value, "Expected one request to be cancelled")

        checkAlive()
        stopAllAndJoin()
    }

    @Test
    fun testCancelServiceScope() = runCancellationTest {
        val firstRequestJob = launch {
            service.serverDelay(300)
        }

        val secondRequestJob = launch {
            service.serverDelay(300)
        }

        unskippableDelay(150) // wait for requests to reach server
        service.cancel()
        firstRequestJob.join()
        secondRequestJob.join()

        assertTrue(firstRequestJob.isCancelled, "Expected firstRequestJob to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")

        assertEquals(0, serverInstances.single().delayCounter.value, "Expected both requests to be cancelled")

        checkAlive(serviceAlive = false)
        checkAlive(expectAlive = false, serverInstances.single().join(), "server instance")
        stopAllAndJoin()
    }

    @Test
    fun testCallException() = runCancellationTest {
        val requestJob = launch {
            service.serverDelay(300)
        }

        val exceptionRequestJob = launch {
            try {
                service.callException()
            } catch (@Suppress("SwallowedException") e: Throwable) {
                throw CancellationException(e.message)
            }

            fail("Expected exception to be thrown")
        }

        exceptionRequestJob.join()
        requestJob.join()

        assertFalse(requestJob.isCancelled, "Expected requestJob not to be cancelled")
        assertTrue(exceptionRequestJob.isCancelled, "Expected exception in callException call")

        assertEquals(1, serverInstances.single().delayCounter.value, "Error should not cancel parallel request")

        checkAlive()
        stopAllAndJoin()
    }

    @Test
    fun testCancelServiceWithOtherService() = runCancellationTest {
        val firstRequestJob = launch {
            service.serverDelay(300)
        }

        val secondService = client.withService<CancellationService>()

        val secondRequestJob = launch {
            secondService.serverDelay(300)
        }

        unskippableDelay(150) // wait for requests to reach server
        secondService.cancel()
        firstRequestJob.join()
        secondRequestJob.join()

        assertFalse(firstRequestJob.isCancelled, "Expected firstRequestJob not to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")

        assertEquals(2, serverInstances.size, "Expected two service instances on a server")
        assertEquals(1, serverInstances.sumOf { it.delayCounter.value }, "Expected one request to succeed")

        val secondServiceInstance = serverInstances.single { it.delayCounter.value == 0 }.join()
        checkAlive(expectAlive = false, secondServiceInstance, "server instance")
        checkAlive()
        stopAllAndJoin()
    }

    @Test
    fun testCancelClient() = runCancellationTest {
        val firstRequestJob = launch {
            service.serverDelay(300)
        }

        val secondService = client.withService<CancellationService>()

        val secondRequestJob = launch {
            secondService.serverDelay(300)
        }

        unskippableDelay(150) // wait for requests to reach server
        client.cancel()
        firstRequestJob.join()
        secondRequestJob.join()

        assertTrue(firstRequestJob.isCancelled, "Expected firstRequestJob to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")

        assertEquals(0, serverInstances.sumOf { it.delayCounter.value }, "Expected no requests to succeed")

        client.join()
        server.join()

        checkAlive(serviceAlive = false, clientAlive = false, serverAlive = false)
        checkAlive(expectAlive = false, secondService, "second service")
        serverInstances.forEachIndexed { i, impl ->
            checkAlive(expectAlive = false, impl.join(), "server instance $i")
        }
        stopAllAndJoin()
    }

    @Test
    fun testCancelServer() = runCancellationTest {
        val firstRequestJob = launch {
            service.serverDelay(300)
        }

        val secondService = client.withService<CancellationService>()

        val secondRequestJob = launch {
            secondService.serverDelay(300)
        }

        unskippableDelay(150) // wait for requests to reach server
        server.cancel()
        firstRequestJob.join()
        secondRequestJob.join()

        assertTrue(firstRequestJob.isCancelled, "Expected firstRequestJob to be cancelled")
        assertTrue(secondRequestJob.isCancelled, "Expected secondRequestJob to be cancelled")

        assertEquals(0, serverInstances.sumOf { it.delayCounter.value }, "Expected no requests to succeed")

        client.join()
        server.join()

        checkAlive(serviceAlive = false, clientAlive = false, serverAlive = false)
        checkAlive(expectAlive = false, secondService, "second service")
        serverInstances.forEachIndexed { i, impl ->
            checkAlive(expectAlive = false, impl.join(), "server instance $i")
        }
        stopAllAndJoin()
    }

    @Test
    fun testStreamScopeOutgoing() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()

        service.outgoingStream(resumableFlow(fence))
        serverInstance().firstIncomingConsumed.await()

        fence.complete(Unit)
        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testStreamScopeIncoming() = runCancellationTest {
        val first: Int
        val flow = service.incomingStream().apply { first = first() }

        serverInstance().fence.complete(Unit)
        val consumed = flow.toList()

        assertEquals(0, first)
        assertContentEquals(emptyList(), consumed)

        stopAllAndJoin()
    }

    @Test
    fun testExceptionInStreamScope() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()

        runCatching {
            service.outgoingStream(resumableFlow(fence))
            serverInstance().firstIncomingConsumed.await()
            error("exception in stream scope")
        }

        fence.complete(Unit)

        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testExceptionInRequest() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()

        runCatching {
            service.outgoingStreamWithException(resumableFlow(fence))
        }

        // to be sure that exception canceled the stream and not scope closure
        serverInstance().consumedAll.await()

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testExceptionInRequestDoesNotCancelOtherRequests() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()

        val flow = service.incomingStream()

        runCatching {
            service.outgoingStreamWithException(resumableFlow(fence))
        }

        fence.complete(Unit)
        serverInstance().fence.complete(Unit)

        val result = flow.toList()

        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        assertContentEquals(List(2) { it }, result)

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
    fun testServiceCancellationCancelsStream() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()
        launch {
            service.outgoingStream(resumableFlow(fence))
        }

        serverInstance().firstIncomingConsumed.await()

        service.cancel("Test request cancelled")
        service.join()

        serverInstance().consumedAll.await()

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testServiceCancellationCancelsStreamButNotOthers() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()
        launch {
            service.outgoingStream(resumableFlow(fence))
        }

        serverInstance().firstIncomingConsumed.await()

        val secondServiceFlow = client
            .withService<CancellationService>()
            .incomingStream()

        service.cancel("Test request cancelled")
        service.join()

        serverInstances[1].fence.complete(Unit)

        val secondServiceResult = secondServiceFlow.toList()

        serverInstance().consumedAll.await()

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)
        assertContentEquals(List(2) { it }, secondServiceResult)

        stopAllAndJoin()
    }

    @Test
    fun testScopeClosureCancelsAllStreams() = runCancellationTest {
        val fence = CompletableDeferred<Unit>()

        service.outgoingStream(resumableFlow(fence))

        client.withService<CancellationService>().outgoingStream(resumableFlow(fence))

        serverInstance().firstIncomingConsumed.await()

        while (true) {
            if (serverInstances.size == 2) {
                serverInstances[1].firstIncomingConsumed.await()
                break
            }

            unskippableDelay(50)
        }

        serverInstances.forEach { it.consumedAll.await() }

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)
        assertContentEquals(listOf(0), serverInstances[1].consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testCancelledClientCancelsFlows() = runCancellationTest {
        val flow = service.incomingStream()

        assertEquals(0, flow.first())
        client.cancel()
        val rest = flow.toList()

        assertTrue("Rest must be empty, as flow was closed") { rest.isEmpty() }

        stopAllAndJoin()
    }

    @Test
    fun testCancelledClientCancelsRequest() = runCancellationTest {
        launch {
            serverInstance().firstIncomingConsumed.await()
            client.cancel("Cancelled by test")
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
    fun testCancelledServiceCancelsRequest() = runCancellationTest {
        launch {
            serverInstance().firstIncomingConsumed.await()
            service.cancel("Cancelled by test")
        }

        try {
            service.longRequest()
            fail("Expected the request to fail to cancellation")
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
        serviceAlive: Boolean = true,
        clientAlive: Boolean = true,
        serverAlive: Boolean = true,
        transportAlive: Boolean = true,
    ) {
        checkAlive(serviceAlive, service, "service")
        checkAlive(clientAlive, client, "client")
        checkAlive(serverAlive, server, "server")
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

    @Suppress("SuspendFunctionOnCoroutineScope")
    private suspend fun CoroutineScope.join() = apply { coroutineContext.job.join() }

    private suspend fun CancellationToolkit.stopAllAndJoin() = transport.coroutineContext.job.cancelAndJoin()
}
