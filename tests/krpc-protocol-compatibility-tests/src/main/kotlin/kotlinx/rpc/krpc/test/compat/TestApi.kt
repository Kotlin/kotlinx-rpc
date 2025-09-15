/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.compat

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.yield

interface CompatTransport : CoroutineScope {
    suspend fun send(message: String)
    suspend fun receive(): String
}

class TestConfig(
    val perCallBufferSize: Int,
)

interface CompatService {
    suspend fun unary(n: Int): Int
    fun serverStreaming(num: Int): Flow<Int>
    suspend fun clientStreaming(n: Flow<Int>): Int
    fun bidiStreaming(flow: Flow<Int>): Flow<Int>

    suspend fun requestCancellation()
    fun serverStreamCancellation(): Flow<Int>
    suspend fun clientStreamCancellation(n: Flow<Int>)

    fun fastServerProduce(n: Int): Flow<Int>
}

interface CompatServiceImpl {
    val exitMethod: Int
    val cancelled: Int
    val entered: CompletableDeferred<Unit>
    val fence: CompletableDeferred<Unit>

    suspend fun awaitCounter(num: Int, counter: CompatServiceImpl.() -> Int) {
        while (counter() != num) {
            yield()
        }
    }
}

interface Starter {
    suspend fun startClient(transport: CompatTransport, config: TestConfig): CompatService
    suspend fun stopClient()
    suspend fun startServer(transport: CompatTransport, config: TestConfig): CompatServiceImpl
    suspend fun stopServer()
}
