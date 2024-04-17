/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.cancellation

import kotlinx.coroutines.*
import org.jetbrains.krpc.client.withService
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
