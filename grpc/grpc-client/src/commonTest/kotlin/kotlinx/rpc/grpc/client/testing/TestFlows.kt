/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

/** Terminal state observed while a request source is being collected by the client. */
internal sealed interface RequestFlowCompletion {
    data object Completed : RequestFlowCompletion
    data class Failed(val cause: Throwable) : RequestFlowCompletion
}

/** Records collection, downstream emission, completion, failure, and cancellation of one request flow. */
internal class RequestFlowProbe<T>(source: Flow<T>) {
    private val started = CompletableDeferred<Unit>()
    private val emissions = Channel<Int>(Channel.UNLIMITED)
    private val completion = CompletableDeferred<RequestFlowCompletion>()

    internal val flow: Flow<T> = flow {
        check(started.complete(Unit)) { "test request flow supports only one collection" }
        var occurrence = 0
        try {
            source.collect { value ->
                emit(value)
                occurrence++
                emissions.send(occurrence)
            }
            completion.complete(RequestFlowCompletion.Completed)
        } catch (error: Throwable) {
            completion.complete(RequestFlowCompletion.Failed(error))
            throw error
        }
    }

    internal suspend fun awaitCollectionStarted() {
        started.await()
    }

    internal suspend fun awaitNextEmission(expectedOccurrence: Int): Int {
        require(expectedOccurrence > 0) { "emission occurrence must be one-based" }
        val occurrence = emissions.receive()
        check(occurrence == expectedOccurrence) {
            "expected request emission $expectedOccurrence, observed $occurrence"
        }
        return occurrence
    }

    internal suspend fun awaitCompletion(): RequestFlowCompletion = completion.await()

    internal suspend fun awaitCancellation(): CancellationException {
        val result = awaitCompletion()
        val failure = result as? RequestFlowCompletion.Failed
            ?: error("expected request flow cancellation, observed normal completion")
        return failure.cause as? CancellationException
            ?: error("expected request flow cancellation, observed ${failure.cause}")
    }
}

/** One-shot request flow whose individual values are released explicitly by the test. */
internal class GatedRequestFlow<T>(values: List<T>) {
    private val permits = Channel<Unit>(Channel.UNLIMITED)
    private val probe = RequestFlowProbe(
        flow {
            for (value in values) {
                permits.receive()
                emit(value)
            }
        }
    )

    internal val flow: Flow<T> get() = probe.flow

    internal suspend fun awaitCollectionStarted() {
        probe.awaitCollectionStarted()
    }

    internal suspend fun releaseNext() {
        permits.send(Unit)
    }

    internal suspend fun awaitNextEmission(expectedOccurrence: Int): Int {
        return probe.awaitNextEmission(expectedOccurrence)
    }

    internal suspend fun awaitCompletion(): RequestFlowCompletion = probe.awaitCompletion()

    internal suspend fun awaitCancellation(): CancellationException = probe.awaitCancellation()
}

/** Creates a finite cold request flow from a snapshot of the supplied values. */
internal fun <T> finiteRequestFlow(values: Iterable<T>): Flow<T> {
    val snapshot = values.toList()
    return flow {
        for (value in snapshot) emit(value)
    }
}

/** Emits the supplied values and then throws the exact requested failure. */
internal fun <T> failingRequestFlow(
    valuesBeforeFailure: Iterable<T>,
    failure: Throwable,
): Flow<T> {
    val snapshot = valuesBeforeFailure.toList()
    return flow {
        for (value in snapshot) emit(value)
        throw failure
    }
}
