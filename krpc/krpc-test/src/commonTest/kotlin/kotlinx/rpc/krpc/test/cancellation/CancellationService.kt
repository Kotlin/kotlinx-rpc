/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.test.WaitCounter

@Rpc
interface CancellationService {
    suspend fun longRequest()

    suspend fun callException()

    suspend fun serverCancellation()

    fun incomingStream(): Flow<Int>

    fun cancellationInIncomingStream(): Flow<Int>

    suspend fun cancellationInOutgoingStream(cancelled: Flow<Int>)

    suspend fun outgoingStream(stream: Flow<Int>)

    suspend fun outgoingStreamAsync(stream: Flow<Int>)

    suspend fun outgoingStreamWithDelayedResponse(stream: Flow<Int>)

    fun nonSuspendable(): Flow<Int>
}

class CancellationServiceImpl : CancellationService {
    val waitCounter = WaitCounter()
    val successCounter = WaitCounter()
    val cancellationsCounter = WaitCounter()

    val consumedIncomingValues = mutableListOf<Int>()
    val firstIncomingConsumed = CompletableDeferred<Int>()
    val consumedAll = CompletableDeferred<Unit>()
    val fence = CompletableDeferred<Unit>()

    override suspend fun longRequest() {
        try {
            firstIncomingConsumed.complete(0)
            waitCounter.increment()
            fence.await()
            successCounter.increment()
        } catch (e: CancellationException) {
            cancellationsCounter.increment()
            throw e
        }
    }

    override suspend fun serverCancellation() {
        throw CancellationException("serverCancellation")
    }

    override suspend fun callException() {
        error("callException")
    }

    override fun incomingStream(): Flow<Int> {
        waitCounter.increment()
        return resumableFlow(fence)
    }

    override fun cancellationInIncomingStream(): Flow<Int> {
        return flow {
            emit(1)
            throw CancellationException("cancellationInIncomingStream")
        }
    }

    override suspend fun cancellationInOutgoingStream(cancelled: Flow<Int>) {
        supervisorScope {
            launch {
                try {
                    cancelled.collect {
                        if (it == 1) {
                            firstIncomingConsumed.complete(it)
                        }
                    }
                } catch (e: CancellationException) {
                    cancellationsCounter.increment()
                    throw e
                }
            }
        }
    }

    override suspend fun outgoingStream(stream: Flow<Int>) {
        try {
            waitCounter.increment()
            consume(stream)
        } catch (e: CancellationException) {
            cancellationsCounter.increment()
            throw e
        }
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
        try {
            waitCounter.increment()
            consume(stream)

            fence.await()
        } catch (e: CancellationException) {
            cancellationsCounter.increment()
            throw e
        }
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
