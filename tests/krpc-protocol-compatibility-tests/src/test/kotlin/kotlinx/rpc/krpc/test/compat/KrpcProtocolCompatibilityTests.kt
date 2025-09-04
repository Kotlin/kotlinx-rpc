/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.compat

import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

class KrpcProtocolCompatibilityTests : KrpcProtocolCompatibilityTestsBase() {
    @TestFactory
    fun unaryCalls() = matrixTest { service, _ ->
        assertEquals(1, service.unary(1))

        List(100) {
            launch {
                assertEquals(it + 1, service.unary(it + 1))
            }
        }.joinAll()

        assertNoErrorsInLogs()
    }

    @TestFactory
    fun serverStreamCalls() = matrixTest { service, _ ->
        assertEquals(1, service.serverStreaming(1).toList().sum())

        List(100) {
            launch {
                assertEquals((it + 1) * (it + 2) / 2, service.serverStreaming(it + 1).toList().sum())
            }
        }.joinAll()

        assertNoErrorsInLogs()
    }

    @TestFactory
    fun clientStreamCalls() = matrixTest { service, _ ->
        assertEquals(1, service.clientStreaming((1..1).asFlow()))

        List(100) {
            launch {
                assertEquals(
                    (it + 1) * (it + 2) / 2,
                    service.clientStreaming((1..it + 1).asFlow()),
                )
            }
        }.joinAll()

        assertNoErrorsInLogs()
    }

    @TestFactory
    fun bidiStreamCalls() = matrixTest { service, _ ->
        assertEquals(
            2,
            service.bidiStreaming((1..1).asFlow(), (1..1).asFlow()).toList().sum()
        )

        List(100) {
            launch {
                assertEquals(
                    (it + 1) * (it + 2),
                    service.bidiStreaming((1..it + 1).asFlow(), (1..it + 1).asFlow()).toList().sum(),
                )
            }
        }.joinAll()

        assertNoErrorsInLogs()
    }

    @TestFactory
    fun requestCancellation() = matrixTest { service, impl ->
        val job = launch {
            service.requestCancellation()
        }

        impl.entered.await()
        job.cancelAndJoin()
        impl.awaitCounter(1) { cancelled }
        assertEquals(0, impl.exitMethod)

        val followup = launch {
            service.requestCancellation()
        }
        impl.fence.complete(Unit)
        followup.join()
        assertEquals(1, impl.exitMethod)

        assertNoErrorsInLogs()
        assertEquals(1, impl.cancelled)
    }

    @TestFactory
    fun serverStreamCancellation() = matrixTest { service, impl ->
        val job = launch {
            service.serverStreamCancellation().collect {}
        }

        impl.entered.await()
        job.cancelAndJoin()
        impl.awaitCounter(1) { cancelled }
        assertEquals(0, impl.exitMethod)

        val followup = async {
            service.serverStreamCancellation().toList()
        }
        impl.fence.complete(Unit)
        assertEquals(listOf(1, 2), followup.await())

        assertNoErrorsInLogs()
        assertEquals(1, impl.cancelled)
    }

    @TestFactory
    fun clientStreamCancellation() = matrixTest { service, impl ->
        val job = launch {
            service.clientStreamCancellation(flow {
                emit(1)
                impl.fence.await()
            })
        }

        impl.entered.await()
        job.cancelAndJoin()
        impl.awaitCounter(1) { cancelled }

        assertNoErrorsInLogs()
    }

    @TestFactory
    fun fastProducer() = matrixTest(timeout = 30.seconds) { service, impl ->
        val async = async {
            service.fastServerProduce(1000).map {
                // long produce
                impl.entered.complete(Unit)
                impl.fence.await()
                it * it
            }.toList()
        }

        impl.entered.await()
        repeat(10_000) {
            assertEquals(1, service.unary(1))
            assertEquals(55, service.serverStreaming(10).toList().sum())
        }

        impl.fence.complete(Unit)
        assertEquals(List(1000) { it * it },async.await())
    }
}
