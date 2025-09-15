/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.stress

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.test.BaseServiceTest
import kotlinx.rpc.test.runTestWithCoroutinesProbes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class StressTest : BaseStressTest() {
    // ~30 sec, 300_000 messages
    @Test
    fun `unary, buffer 100, launches 3_000 x 100`() = testUnary(100, 120.seconds, 3_000, 100)

    // ~10 sec, 100_000 messages
    @Test
    fun `unary, buffer 1, launches 10_000 x 10`() = testUnary(1, 120.seconds, 10_000, 10)

    // ~51 min, 500_000 messages
    @Test
    fun `unary, 1000 buffer, launches 50_000 x 10`() = testUnary(1000, 180.seconds, 50_000, 10)


    // ~15 sec, 4_000_000 messages
    @Test
    fun `server streaming, buffer 30 buf, launches 200 x 10`() = testServerStreaming(30, 120.seconds, 200, 10)

    // ~19 sec, 4_000_000 messages
    @Test
    fun `server streaming, buffer 1, launches 200 x 10`() = testServerStreaming(1, 120.seconds, 200, 10)

    // ~14 sec, 4_000_000 messages
    @Test
    fun `server streaming, buffer 2000, launches 200 x 10`() = testServerStreaming(2000, 180.seconds, 200, 10)


    // ~15 sec, 4_000_000 messages
    @Test
    fun `client streaming, buffer 30, launches 200 x 10`() = testClientStreaming(30, 120.seconds, 200, 10)

    // ~19 sec, 4_000_000 messages
    @Test
    fun `client streaming, buffer 1, launches 200 x 10`() = testClientStreaming(1, 120.seconds, 200, 10)

    // ~15 sec, 4_000_000 messages
    @Test
    fun `client streaming, buffer 2000, launches 200 x 10`() = testClientStreaming(2000, 180.seconds, 200, 10)


    // ~23 sec, 4_500_000 messages
    @Test
    fun `bidi streaming, buffer 30, launches 150 x 10`() = testBidiStreaming(30, 120.seconds, 150, 10)

    // ~24 sec, 4_500_000 messages
    @Test
    fun `bidi streaming, buffer 1, launches 150 x 10`() = testBidiStreaming(1, 120.seconds, 150, 10)

    // ~20 sec, 4_500_000 messages
    @Test
    fun `bidi streaming, buffer 2000, launches 150 x 10`() = testBidiStreaming(2000, 120.seconds, 150, 10)
}


abstract class BaseStressTest : BaseServiceTest() {
    // (launches * iterationsPerLaunch) ^ 2 * 2 messages
    protected fun testBidiStreaming(
        perCallBufferSize: Int,
        timeout: Duration,
        launches: Int,
        iterationsPerLaunch: Int,
    ) = runTest(perCallBufferSize, timeout) { counter ->
        List(launches) { id ->
            val i = id + 1
            launch {
                repeat(iterationsPerLaunch) { iter ->
                    val j = iter + 1
                    assertEquals(
                        expected = (1 + i * j) * (i * j) / 2,
                        actual = service.bidiStreaming((1..i * j).asFlow()).toList().sum(),
                    )
                    counter.total.incrementAndGet()
                }
                counter.launches.incrementAndGet()
            }
        }.joinAll()

        assertEquals(launches * iterationsPerLaunch, impl.bidiStreamingInvocations.value)
    }

    // (launches * iterationsPerLaunch) ^ 2 messages
    protected fun testClientStreaming(
        perCallBufferSize: Int,
        timeout: Duration,
        launches: Int,
        iterationsPerLaunch: Int,
    ) = runTest(perCallBufferSize, timeout) { counter ->
        List(launches) { id ->
            val i = id + 1
            launch {
                repeat(iterationsPerLaunch) { iter ->
                    val j = iter + 1
                    assertEquals(
                        expected = (1 + i * j) * (i * j) / 2,
                        actual = service.clientStreaming((1..i * j).asFlow()),
                    )
                    counter.total.incrementAndGet()
                }
                counter.launches.incrementAndGet()
            }
        }.joinAll()

        assertEquals(launches * iterationsPerLaunch, impl.clientStreamingInvocations.value)
    }

    // (launches * iterationsPerLaunch) ^ 2 messages
    protected fun testServerStreaming(
        perCallBufferSize: Int,
        timeout: Duration,
        launches: Int,
        iterationsPerLaunch: Int,
    ) = runTest(perCallBufferSize, timeout) { counter ->
        List(launches) { id ->
            val i = id + 1
            launch {
                repeat(iterationsPerLaunch) { iter ->
                    val j = iter + 1
                    assertEquals(
                        expected = (1 + i * j) * (i * j) / 2,
                        actual = service.serverStreaming(i * j).toList().sum(),
                        message = "i=$i, j=$j",
                    )
                    counter.total.incrementAndGet()
                }
                counter.launches.incrementAndGet()
            }
        }.joinAll()

        assertEquals(launches * iterationsPerLaunch, impl.serverStreamingInvocations.value)
    }

    // (launches * iterationsPerLaunch) messages
    protected fun testUnary(
        perCallBufferSize: Int,
        timeout: Duration,
        launches: Int,
        iterationsPerLaunch: Int,
    ) = runTest(perCallBufferSize, timeout) { counter ->
        List(launches) { id ->
            launch {
                repeat(iterationsPerLaunch) { iter ->
                    assertEquals(id * iter, service.unary(id * iter))
                    counter.total.incrementAndGet()
                }
                counter.launches.incrementAndGet()
            }
        }.joinAll()

        assertEquals(launches * iterationsPerLaunch, impl.unaryInvocations.value)
    }

    class Counter {
        val launches = atomic(0)
        val total = atomic(0)
    }

    private fun runTest(
        perCallBufferSize: Int = 100,
        timeout: Duration = 120.seconds,
        body: suspend Env.(Counter) -> Unit,
    ) = runTestWithCoroutinesProbes(timeout = timeout) {
        RpcInternalDumpLoggerContainer.set(null)
        runServiceTest(coroutineContext, perCallBufferSize) {
            val counter = Counter()
            val counterJob = launch {
                while (true) {
                    withContext(Dispatchers.Default) {
                        delay(5.seconds)
                        println("Launches: ${counter.launches.value}, total: ${counter.total.value}")
                    }
                }
            }

            body(this, counter)

            counterJob.cancelAndJoin()
        }
    }
}
