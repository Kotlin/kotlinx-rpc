/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.test.cancellation

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.invokeOnStreamScopeCompletion
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates
import kotlin.test.assertIs

@Rpc
interface CancellationService : RemoteService {
    suspend fun longRequest()

    suspend fun serverDelay(millis: Long)

    suspend fun callException()

    suspend fun incomingStream(): Flow<Int>

    suspend fun outgoingStream(stream: Flow<Int>)

    suspend fun outgoingStreamWithDelayedResponse(stream: Flow<Int>)

    suspend fun outgoingStreamWithException(stream: Flow<Int>)

    suspend fun outgoingHotFlow(stream: StateFlow<Int>)

    suspend fun incomingHotFlow(): StateFlow<Int>

    val fastFieldFlow: Flow<Int>

    val slowFieldFlow: Flow<Int>

    suspend fun closedStreamScopeCallback()

    suspend fun closedStreamScopeCallbackWithStream(): Flow<Int>

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

    override suspend fun incomingStream(): Flow<Int> {
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

    val hotFlowMirror = MutableStateFlow(-1)
    val hotFlowConsumedSize = CompletableDeferred<Int>()

    override suspend fun outgoingHotFlow(stream: StateFlow<Int>) {
        launch {
            var cnt = 0

            val cancellation = runCatching {
                stream.collect {
                    cnt++
                    hotFlowMirror.emit(it)
                }
            }

            val result = runCatching {
                assertIs<CancellationException>(cancellation.exceptionOrNull(), "Cancellation should be thrown")

                cnt
            }

            hotFlowConsumedSize.completeWith(result)
        }
    }

    var incomingHotFlowJob by Delegates.notNull<Job>()

    override suspend fun incomingHotFlow(): StateFlow<Int> {
        val state = MutableStateFlow(-1)

        incomingHotFlowJob = launch {
            repeat(Int.MAX_VALUE) { value ->
                state.value = value

                hotFlowMirror.first { it == value }
            }
        }

        invokeOnStreamScopeCompletion {
            incomingHotFlowJob.cancel()
        }

        return state
    }

    override val fastFieldFlow: Flow<Int> = resumableFlow(fence)

    val emittedFromSlowField = mutableListOf<Int>()

    override val slowFieldFlow: Flow<Int> = resumableFlow(fence) {
        emittedFromSlowField.add(it)
    }

    val streamScopeCallbackResult = CompletableDeferred<Throwable?>()

    override suspend fun closedStreamScopeCallback() {
        invokeOnStreamScopeCompletion { cause ->
            streamScopeCallbackResult.complete(cause)
        }
    }

    override suspend fun closedStreamScopeCallbackWithStream(): Flow<Int> {
        invokeOnStreamScopeCompletion { cause ->
            streamScopeCallbackResult.complete(cause)
        }

        return resumableFlow(fence)
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
