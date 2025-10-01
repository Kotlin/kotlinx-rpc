/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.compat.service

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.test.compat.CompatServiceImpl
import kotlinx.rpc.test.WaitCounter
import kotlin.coroutines.cancellation.CancellationException

@Rpc
interface TestService {
    suspend fun unary(n: Int): Int
    fun serverStreaming(num: Int): Flow<Int>
    suspend fun clientStreaming(n: Flow<Int>): Int
    fun bidiStreaming(flow: Flow<Int>): Flow<Int>
    suspend fun requestCancellation()
    fun serverStreamCancellation(): Flow<Int>
    suspend fun clientStreamCancellation(n: Flow<Int>)

    fun fastServerProduce(n: Int): Flow<Int>
}

class TestServiceImpl : TestService, CompatServiceImpl {
    override suspend fun unary(n: Int): Int {
        return n
    }

    override fun serverStreaming(num: Int): Flow<Int> {
        return (1..num).asFlow()
    }

    override suspend fun clientStreaming(n: Flow<Int>): Int {
        return n.toList().sum()
    }

    override fun bidiStreaming(flow: Flow<Int>): Flow<Int> {
        return flow
    }

    override val exitMethod: WaitCounter = WaitCounter()
    override val cancelled: WaitCounter = WaitCounter()

    override val entered: CompletableDeferred<Unit> = CompletableDeferred()
    override val fence: CompletableDeferred<Unit> = CompletableDeferred()

    override suspend fun requestCancellation() {
        try {
            entered.complete(Unit)
            fence.await()
            exitMethod.increment()
        } catch (e: CancellationException) {
            cancelled.increment()
            throw e
        }
    }

    override fun serverStreamCancellation(): Flow<Int> {
        return flow {
            try {
                emit(1)
                entered.complete(Unit)
                fence.await()
                emit(2)
            } catch (e: CancellationException) {
                cancelled.increment()
                throw e
            }
        }
    }

    override suspend fun clientStreamCancellation(n: Flow<Int>) {
        try {
            n.collect {
                println("[clientStreamCancellation] collected $it")
                if (it != 0) {
                    entered.complete(Unit)
                }
            }
        } catch (e: CancellationException) {
            println("[clientStreamCancellation] cancelled on server")
            cancelled.increment()
            throw e
        } catch (e: Throwable) {
            println("[clientStreamCancellation] caught $e")
            throw e
        } finally {
            println("[clientStreamCancellation] finally")
        }
    }

    override fun fastServerProduce(n: Int): Flow<Int> {
        return flow {
            repeat(n) {
                emit(it)
            }
        }
    }
}
