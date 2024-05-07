/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.client.withService
import kotlinx.rpc.internal.invokeOnStreamScopeCompletion
import kotlinx.rpc.internal.streamScoped
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
        streamScoped {
            service.outgoingStream(delayedFlow())
            serverInstance().firstIncomingConsumed.await()
        }

        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testStreamScopeAbsentForOutgoingStream() = runCancellationTest {
        assertFailsWith<IllegalStateException> {
            service.outgoingStream(delayedFlow())
        }

        stopAllAndJoin()
    }

    @Test
    fun testStreamScopeAbsentForIncomingStream() = runCancellationTest {
        assertFailsWith<IllegalStateException> {
            service.incomingStream()
        }

        stopAllAndJoin()
    }

    @Test
    fun testStreamScopeIncoming() = runCancellationTest {
        val flow = streamScoped {
            service.incomingStream().apply {
                unskippableDelay(300)
            }
        }

        val consumed = flow.toList()

        assertContentEquals(listOf(0), consumed)

        stopAllAndJoin()
    }

    @Test
    fun testExceptionInStreamScope() = runCancellationTest {
        runCatching {
            streamScoped {
                service.outgoingStream(delayedFlow())
                serverInstance().firstIncomingConsumed.await()
                error("exception in stream scope")
            }
        }

        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testExceptionInRequest() = runCancellationTest {
        streamScoped {
            runCatching {
                service.outgoingStreamWithException(delayedFlow())
            }

            // to be sure that exception canceled the stream and not scope closure
            serverInstance().consumedAll.await()
        }

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testNestedStreamScopesForbidden() {
        runBlocking {
            assertFailsWith<IllegalStateException> {
                streamScoped { streamScoped { } }
            }
        }
    }

    @Test
    fun testExceptionInRequestDoesNotCancelOtherRequests() = runCancellationTest {
        val result = streamScoped {
            val flow = service.incomingStream(delayMillis = 50)

            runCatching {
                service.outgoingStreamWithException(delayedFlow())
            }

            flow.toList()
        }

        serverInstance().consumedAll.await()
        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        assertContentEquals(List(10) { it }, result)

        stopAllAndJoin()
    }

    @Test
    fun testRequestCancellationCancelsStream() = runCancellationTest {
        streamScoped {
            val job = launch {
                service.outgoingStreamWithDelayedResponse(delayedFlow())
            }

            serverInstance().firstIncomingConsumed.await()

            job.cancel("Test request cancelled")
            job.join()
            assertTrue("Job must be canceled") { job.isCancelled }

            // close by request cancel and not scope closure
            serverInstance().consumedAll.await()
        }

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testRequestCancellationCancelsStreamButNotOthers() = runCancellationTest {
        val result = streamScoped {
            val job = launch {
                service.outgoingStreamWithDelayedResponse(delayedFlow(delayMillis = 50))
            }

            val flow = service.incomingStream(delayMillis = 100)

            serverInstance().firstIncomingConsumed.await()

            job.cancel("Test request cancelled")
            job.join()
            assertTrue("Job must be canceled") { job.isCancelled }

            // close by request cancel and not scope closure
            serverInstance().consumedAll.await()

            flow.toList()
        }

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)
        assertContentEquals(List(10) { it }, result)

        stopAllAndJoin()
    }

    @Test
    fun testServiceCancellationCancelsStream() = runCancellationTest {
        streamScoped {
            launch {
                service.outgoingStreamWithDelayedResponse(delayedFlow(delayMillis = 100))
            }

            serverInstance().firstIncomingConsumed.await()

            service.cancel("Test request cancelled")
            service.join()
        }

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testServiceCancellationCancelsStreamButNotOthers() = runCancellationTest {
        val secondServiceResult = streamScoped {
            launch {
                service.outgoingStreamWithDelayedResponse(delayedFlow(delayMillis = 100))
            }

            serverInstance().firstIncomingConsumed.await()

            val secondServiceFlow = client
                .withService<CancellationService>()
                .incomingStream(delayMillis = 50)

            service.cancel("Test request cancelled")
            service.join()

            secondServiceFlow.toList()
        }

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)
        assertContentEquals(List(10) { it }, secondServiceResult)

        stopAllAndJoin()
    }

    @Test
    fun testScopeClosureCancelsAllStreams() = runCancellationTest {
        streamScoped {
            service.outgoingStream(delayedFlow())

            client.withService<CancellationService>().outgoingStream(delayedFlow())

            serverInstance().firstIncomingConsumed.await()

            while (true) {
                if (serverInstances.size == 2) {
                    serverInstances[1].firstIncomingConsumed.await()
                    break
                }

                unskippableDelay(50)
            }
        }

        assertContentEquals(listOf(0), serverInstance().consumedIncomingValues)
        assertContentEquals(listOf(0), serverInstances[1].consumedIncomingValues)

        stopAllAndJoin()
    }

    @Test
    fun testFieldFlowWorksWithNoScope() = runCancellationTest {
        val result = service.fastFieldFlow.toList()

        assertContentEquals(List(10) { it }, result)

        stopAllAndJoin()
    }

    @Test
    fun testServiceCancellationCancelsFieldFlow() = runCancellationTest {
        val flow = service.slowFieldFlow
        val firstCollected = CompletableDeferred<Int>()
        val allCollected = mutableListOf<Int>()

        val job = launch {
            flow.collect {
                if (!firstCollected.isCompleted) {
                    firstCollected.complete(it)
                }

                allCollected.add(it)
            }
        }

        firstCollected.await()

        service.cancel("Service cancelled")

        job.join()
        assertContentEquals(listOf(0), allCollected)
        assertContentEquals(listOf(0), serverInstance().emittedFromSlowField)

        stopAllAndJoin()
    }

    @Test
    fun testInvokeOnStreamScopeCompletionOnServerWithNoStreams() = runCancellationTest {
        streamScoped {
            service.closedStreamScopeCallback()
        }

        serverInstance().streamScopeCallbackResult.await()

        stopAllAndJoin()
    }

    @Test
    fun testInvokeOnStreamScopeCompletionOnServer() = runCancellationTest {
        val result = streamScoped {
            service.closedStreamScopeCallbackWithStream().toList()
        }

        serverInstance().streamScopeCallbackResult.await()

        assertContentEquals(List(5) { it }, result)

        stopAllAndJoin()
    }

    @Test
    fun testInvokeOnStreamScopeCompletionOnClient() = runCancellationTest {
        val streamScopeCompleted = CompletableDeferred<Unit>()

        streamScoped {
            service.closedStreamScopeCallback()

            invokeOnStreamScopeCompletion {
                streamScopeCompleted.complete(Unit)
            }
        }

        streamScopeCompleted.await()

        stopAllAndJoin()
    }

    @Test
    fun testOutgoingHotFlow() = runCancellationTest {
        streamScoped {
            val state = MutableStateFlow(-1)

            service.outgoingHotFlow(state)

            val mirror = serverInstance().hotFlowMirror
            mirror.first { it == -1 } // initial value

            repeat(3) { value ->
                state.value = value
                mirror.first { it == value }
            }
        }

        assertEquals(4, serverInstance().hotFlowConsumedSize.await())

        stopAllAndJoin()
    }

    @Test
    fun testIncomingHotFlow() = runCancellationTest {
        val state = streamScoped {
            val state = service.incomingHotFlow()

            val mirror = serverInstance().hotFlowMirror
            repeat(3) { value ->
                state.first { it == value }
                mirror.value = value
            }

            state.first { it == 3 }

            state
        }

        serverInstance().incomingHotFlowJob.join()
        assertEquals(3, state.value)
        assertEquals(2, serverInstance().hotFlowMirror.value)

        stopAllAndJoin()
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
