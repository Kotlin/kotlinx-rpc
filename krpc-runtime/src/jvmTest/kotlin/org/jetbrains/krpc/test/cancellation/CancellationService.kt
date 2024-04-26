/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.test.cancellation

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.internal.invokeOnStreamScopeCompletion
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates
import kotlin.test.assertIs

interface CancellationService : RPC {
    suspend fun serverDelay(millis: Long)

    suspend fun callException()

    suspend fun incomingStream(times: Int = 10, delayMillis: Long = 200): Flow<Int>

    suspend fun outgoingStream(stream: Flow<Int>)

    suspend fun outgoingStreamWithDelayedResponse(stream: Flow<Int>)

    suspend fun outgoingStreamWithException(stream: Flow<Int>)

    suspend fun outgoingHotFlow(stream: StateFlow<Int>)

    suspend fun incomingHotFlow(): StateFlow<Int>

    val fastFieldFlow: Flow<Int>

    val slowFieldFlow: Flow<Int>

    suspend fun closedStreamScopeCallback()

    suspend fun closedStreamScopeCallbackWithStream(): Flow<Int>
}

class CancellationServiceImpl(override val coroutineContext: CoroutineContext) : CancellationService {
    val delayCounter = atomic(0)
    val consumedIncomingValues = mutableListOf<Int>()
    val firstIncomingConsumed = CompletableDeferred<Int>()
    val consumedAll = CompletableDeferred<Unit>()

    override suspend fun serverDelay(millis: Long) {
        delay(millis)
        delayCounter.incrementAndGet()
    }

    override suspend fun callException() {
        error("callException")
    }

    override suspend fun incomingStream(times: Int, delayMillis: Long): Flow<Int> {
        return delayedFlow(times, delayMillis)
    }

    override suspend fun outgoingStream(stream: Flow<Int>) {
        consume(stream)
    }

    override suspend fun outgoingStreamWithDelayedResponse(stream: Flow<Int>) {
        consume(stream)

        unskippableDelay(2000)
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

    override val fastFieldFlow: Flow<Int> = delayedFlow(delayMillis = -1)

    val emittedFromSlowField = mutableListOf<Int>()

    override val slowFieldFlow: Flow<Int> = delayedFlow {
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

        return delayedFlow(times = 5, delayMillis = 50)
    }

    private fun consume(stream: Flow<Int>) {
        launch {
            stream.collect {
                if (!firstIncomingConsumed.isCompleted) {
                    firstIncomingConsumed.complete(it)
                }
                consumedIncomingValues.add(it)
            }

            consumedAll.complete(Unit)
        }
    }
}

fun delayedFlow(times: Int = 10, delayMillis: Long = 200, onEmit: (Int) -> Unit = {}): Flow<Int> = flow {
    repeat(times) {
        unskippableDelay(delayMillis)

        onEmit(it)
        emit(it)
    }
}
