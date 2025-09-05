/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.yield
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.rpcClientConfig
import kotlinx.rpc.krpc.rpcServerConfig
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Rpc
interface BackPressure {
    suspend fun plain()

    fun serverStream(num: Int): Flow<Int>

    suspend fun clientStream(flow: Flow<Int>)
}

class BackPressureImpl : BackPressure {
    val plainCounter = atomic(0)
    val serverStreamCounter = atomic(0)
    val clientStreamCounter = atomic(0)
    val entered = CompletableDeferred<Unit>()
    val fence = CompletableDeferred<Unit>()

    suspend fun awaitCounter(value: Int, counter: BackPressureImpl.() -> Int) {
        while (counter() != value) {
            yield()
        }
    }

    override suspend fun plain() {
        plainCounter.incrementAndGet()
    }

    override fun serverStream(num: Int): Flow<Int> {
        return flow {
            repeat(num) {
                serverStreamCounter.incrementAndGet()
                emit(it)
            }
        }
    }

    val consumed = mutableListOf<Int>()
    override suspend fun clientStream(flow: Flow<Int>) {
        flow.collect {
            if (it == 0) {
                entered.complete(Unit)
                fence.await()
            }
            consumed.add(it)
        }
    }
}

class BackPressureTest : BackPressureTestBase() {
    @Test
    fun buffer_size_1_server() = runServerTest(perCallBufferSize = 1)

    @Test
    fun buffer_size_10_server() = runServerTest(perCallBufferSize = 10)

    @Test
    fun buffer_size_1_client() = runClientTest(perCallBufferSize = 1)

    @Test
    fun buffer_size_10_client() = runClientTest(perCallBufferSize = 10)
}

// `+2` explanation:
// 1. the first element is sent and processed by the client
// 2. the second element is sent and is suspended on the client
// because the processing of the first element is not finished yet
// 3. the third element is n from the flow and suspended on the server side
abstract class BackPressureTestBase {
    protected fun runServerTest(
        perCallBufferSize: Int,
        timeout: Duration = 10.seconds,
    ) = runTest(perCallBufferSize, timeout) { service, impl ->
        val flowList = async {
            service.serverStream(1000).map {
                if (it == 0) {
                    impl.entered.complete(Unit)
                    impl.fence.await()
                }
            }.toList()
        }

        impl.entered.await()
        impl.awaitCounter(perCallBufferSize + 2) { serverStreamCounter.value }

        repeat(1000) {
            service.plain()
        }

        impl.awaitCounter(1000) { plainCounter.value }

        assertEquals(perCallBufferSize + 2, impl.serverStreamCounter.value)
        impl.fence.complete(Unit)
        impl.awaitCounter(1000) { serverStreamCounter.value }
        assertEquals(1000, flowList.await().size)
    }

    protected fun runClientTest(
        perCallBufferSize: Int,
        timeout: Duration = 10.seconds,
    ) = runTest(perCallBufferSize, timeout) { service, impl ->
        val flowList = async {
            service.clientStream(flow {
                repeat(1000) {
                    impl.clientStreamCounter.incrementAndGet()
                    emit(it)
                }
            })
        }

        impl.entered.await()
        impl.awaitCounter(perCallBufferSize + 2) { clientStreamCounter.value }

        repeat(1000) {
            service.plain()
        }

        impl.awaitCounter(1000) { plainCounter.value }

        assertEquals(0, impl.consumed.size)
        assertEquals(perCallBufferSize + 2, impl.clientStreamCounter.value)
        impl.fence.complete(Unit)
        impl.awaitCounter(1000) { clientStreamCounter.value }
        flowList.await()
        assertEquals(1000, impl.consumed.size)
    }

    protected fun runTest(
        perCallBufferSize: Int,
        timeout: Duration = 10.seconds,
        body: suspend TestScope.(BackPressure, BackPressureImpl) -> Unit,
    ): TestResult = kotlinx.coroutines.test.runTest(timeout = timeout) {
        val transport = LocalTransport(coroutineContext, recordTimestamps = false)
        val clientConfig = rpcClientConfig {
            serialization {
                json()
            }

            connector {
                this.perCallBufferSize = perCallBufferSize
            }
        }
        val client = KrpcTestClient(clientConfig, transport.client)
        val serverConfig = rpcServerConfig {
            serialization {
                json()
            }

            connector {
                this.perCallBufferSize = perCallBufferSize
            }
        }
        val server = KrpcTestServer(serverConfig, transport.server)
        val impl = BackPressureImpl()
        server.registerService<BackPressure> { impl }
        val service = client.withService<BackPressure>()

        try {
            body(service, impl)
        } finally {
            RpcInternalDumpLoggerContainer.set(null)
            client.close()
            server.close()
            client.awaitCompletion()
            server.awaitCompletion()
            transport.coroutineContext.job.cancelAndJoin()
        }
    }
}
