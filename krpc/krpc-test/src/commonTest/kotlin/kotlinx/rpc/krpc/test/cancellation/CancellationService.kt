/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.rpc.annotations.Rpc

@Rpc
interface CancellationService {
    suspend fun longRequest()

    suspend fun callException()

    fun incomingStream(): Flow<Int>

    suspend fun outgoingStream(stream: Flow<Int>)

    suspend fun outgoingStreamAsync(stream: Flow<Int>)

    suspend fun outgoingStreamWithDelayedResponse(stream: Flow<Int>)

    suspend fun outgoingStreamWithException(stream: Flow<Int>)

    fun nonSuspendable(): Flow<Int>
}

class CancellationServiceImpl : CancellationService {
    val waitCounter = atomic(0)

    suspend fun awaitWaitCounter(value: Int) {
        while (waitCounter.value != value) {
           yield()
        }
    }

    val successCounter = atomic(0)

    val consumedIncomingValues = mutableListOf<Int>()
    val firstIncomingConsumed = CompletableDeferred<Int>()
    val consumedAll = CompletableDeferred<Unit>()
    val fence = CompletableDeferred<Unit>()

    override suspend fun longRequest() {
        firstIncomingConsumed.complete(0)
        waitCounter.incrementAndGet()
        fence.await()
        successCounter.incrementAndGet()
    }

    override suspend fun callException() {
        error("callException")
    }

    override fun incomingStream(): Flow<Int> {
        return resumableFlow(fence)
    }

    override suspend fun outgoingStream(stream: Flow<Int>) {
        consume(stream)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun outgoingStreamAsync(stream: Flow<Int>) {
        @Suppress("detekt.GlobalCoroutineUsage")
        GlobalScope.launch {
            consume(stream)
        }
        firstIncomingConsumed.await()
    }

    override suspend fun outgoingStreamWithDelayedResponse(stream: Flow<Int>) {
        consume(stream)

        unskippableDelay(10000)
    }

    override suspend fun outgoingStreamWithException(stream: Flow<Int>) {
        consume(stream)

        unskippableDelay(300)

        // it will not cancel launch collector
        error("exception in request")
    }

    private suspend fun consume(stream: Flow<Int>) {
        try {
            stream.collect {
                if (!firstIncomingConsumed.isCompleted) {
                    firstIncomingConsumed.complete(it)
                }
                consumedIncomingValues.add(it)
            }
        } finally {
            consumedAll.complete(Unit)
        }
    }

    var nonSuspendableSecond = false
    val nonSuspendableFinished = CompletableDeferred<Unit>()

    override fun nonSuspendable(): Flow<Int> {
        return flow {
            try {
                repeat(2) {
                    if (it == 1) {
                        nonSuspendableSecond = true
                    }

                    emit(it)

                    if (it == 0) {
                        fence.await()
                    }
                }
            } catch (e: CancellationException) {
                nonSuspendableFinished.complete(Unit)

                throw e
            }
        }
    }
}

fun resumableFlow(fence: Deferred<Unit>, onEmit: (Int) -> Unit = {}): Flow<Int> = flow {
    repeat(2) {
        onEmit(it)
        emit(it)

        if (it == 0) {
            fence.await()
        }
    }
}
