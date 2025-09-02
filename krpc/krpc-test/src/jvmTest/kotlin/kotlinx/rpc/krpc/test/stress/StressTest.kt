/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.stress

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.test.KrpcTestClient
import kotlinx.rpc.krpc.test.KrpcTestServer
import kotlinx.rpc.krpc.test.LocalTransport
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Rpc
interface StressService {
    suspend fun unary(n: Int): Int
    fun serverStreaming(num: Int): Flow<Int>
    suspend fun clientStreaming(n: Flow<Int>): Int
    fun bidiStreaming(flow1: Flow<Int>, flow2: Flow<Int>): Flow<Int>
}

class StressServiceImpl : StressService {
    val unaryInvocations = atomic(0)
    val serverStreamingInvocations = atomic(0)
    val clientStreamingInvocations = atomic(0)
    val bidiStreamingInvocations = atomic(0)

    override suspend fun unary(n: Int): Int {
        unaryInvocations.incrementAndGet()
        return n
    }

    override fun serverStreaming(num: Int): Flow<Int> {
        serverStreamingInvocations.incrementAndGet()
        return (1..num).asFlow()
    }

    override suspend fun clientStreaming(n: Flow<Int>): Int {
        clientStreamingInvocations.incrementAndGet()
        return n.toList().sum()
    }

    override fun bidiStreaming(flow1: Flow<Int>, flow2: Flow<Int>): Flow<Int> {
        bidiStreamingInvocations.incrementAndGet()
        return flow1.zip(flow2) { a, b -> a + b }
    }
}

//
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


    // ~30 sec, 4_500_000 messages
    @Test
    fun `bidi streaming, buffer 30, launches 150 x 10`() = testBidiStreaming(30, 120.seconds, 150, 10)

    // ~33 sec, 4_500_000 messages
    @Test
    fun `bidi streaming, buffer 1, launches 150 x 10`() = testBidiStreaming(1, 120.seconds, 150, 10)

    // ~29 sec, 4_500_000 messages
    @Test
    fun `bidi streaming, buffer 2000, launches 150 x 10`() = testBidiStreaming(2000, 120.seconds, 150, 10)
}


abstract class BaseStressTest {
    // (launches * iterationsPerLaunch) ^ 2 * 2 messages
    protected fun testBidiStreaming(
        perCallBufferSize: Int,
        timeout: Duration,
        launches: Int,
        iterationsPerLaunch: Int,
    ) = runTest(perCallBufferSize, timeout) { service, impl ->
        List(launches) { id ->
            val i = id + 1
            launch {
                repeat(iterationsPerLaunch) { iter ->
                    val j = iter + 1
                    assertEquals(
                        expected = (1 + i * j) * (i * j),
                        actual = service.bidiStreaming(
                            (1..i * j).asFlow(),
                            (1..i * j).asFlow(),
                        ).toList().sum(),
                    )
                }
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
    ) = runTest(perCallBufferSize, timeout) { service, impl ->
        List(launches) { id ->
            val i = id + 1
            launch {
                repeat(iterationsPerLaunch) { iter ->
                    val j = iter + 1
                    assertEquals(
                        expected = (1 + i * j) * (i * j) / 2,
                        actual = service.clientStreaming((1..i * j).asFlow()),
                    )
                }
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
    ) = runTest(perCallBufferSize, timeout) { service, impl ->
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
                }
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
    ) = runTest(perCallBufferSize, timeout) { service, impl ->
        List(launches) { id ->
            launch {
                repeat(iterationsPerLaunch) { iter ->
                    assertEquals(id * iter, service.unary(id * iter))
                }
            }
        }.joinAll()

        assertEquals(launches * iterationsPerLaunch, impl.unaryInvocations.value)
    }

    private fun runTest(
        perCallBufferSize: Int = 100,
        timeout: Duration = 120.seconds,
        body: suspend TestScope.(StressService, StressServiceImpl) -> Unit,
    ) = kotlinx.coroutines.test.runTest(timeout = timeout) {
        val transport = LocalTransport(coroutineContext, recordTimestamps = false)

        val clientConfig = rpcClientConfig {
            serialization {
                json()
            }

            connector {
                this.perCallBufferSize = perCallBufferSize
            }
        }

        val serverConfig = rpcServerConfig {
            serialization {
                json()
            }

            connector {
                this.perCallBufferSize = perCallBufferSize
            }
        }

        val client = KrpcTestClient(clientConfig, transport.client)
        val service = client.withService<StressService>()

        val server = KrpcTestServer(serverConfig, transport.server)
        val impl = StressServiceImpl()
        server.registerService<StressService> { impl }

        body(service, impl)

        client.close()
        server.close()
        client.awaitCompletion()
        server.awaitCompletion()
        transport.coroutineContext.cancelAndJoin()
    }
}
