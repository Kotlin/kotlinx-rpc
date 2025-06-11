/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlin.coroutines.CoroutineContext

@Rpc
interface CancellationService : RemoteService {
    suspend fun longRequest()

    suspend fun serverDelay(millis: Long)

    suspend fun callException()

    fun incomingStream(): Flow<Int>

    suspend fun outgoingStream(stream: Flow<Int>)

    suspend fun outgoingStreamWithDelayedResponse(stream: Flow<Int>)

    suspend fun outgoingStreamWithException(stream: Flow<Int>)

    fun nonSuspendable(): Flow<Int>
}

class CancellationServiceImpl(override val coroutineContext: CoroutineContext) : CancellationService {
    val delayCounter = atomic(0)
    val consumedIncomingValues = mutableListOf<Int>()
    val firstIncomingConsumed = CompletableDeferred<Int>()
    val consumedAll = CompletableDeferred<Unit>()
    val fence = CompletableDeferred<Unit>()

    override suspend fun longRequest() {
        firstIncomingConsumed.complete(0)
        fence.await()
    }

    override suspend fun serverDelay(millis: Long) {
        delay(millis)
        delayCounter.incrementAndGet()
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

    private fun consume(stream: Flow<Int>) {
        launch {
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
