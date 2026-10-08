/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.testing

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

/** Terminal state observed while a request source is being collected by the client. */
internal sealed interface RequestFlowCompletion {
    /** The source emitted all its values and the client collected them to the end. */
    data object Completed : RequestFlowCompletion

    /**
     * The collection ended with [cause]: either the source itself threw, or the client stopped collecting.
     * A [CancellationException] means the client cancelled the collection, for example because the call failed.
     */
    data class Failed(val cause: Throwable) : RequestFlowCompletion
}

/**
 * Records collection, downstream emission, completion, failure, and cancellation of one request flow.
 *
 * Pass [flow] to the client instead of the source; it forwards every value of the source unchanged and records
 * what happens along the way, so a test can synchronize with the client's request side:
 * - [awaitCollectionStarted] returns once the client has started collecting.
 * - [awaitNextEmission] returns once the client has taken the next value. The emission is recorded after the
 *   downstream `emit` returns, so it is not recorded for a value the client never accepted.
 * - [awaitCompletion] and [awaitCancellation] return once the collection has ended, either normally or
 *   with the cause the collection failed with.
 *
 * Recording never suspends, so the probe does not change the client's backpressure. [flow] can be collected only
 * once; a second collection fails, which catches a client that re-subscribes to its request source.
 *
 * @param source The request values to forward to the client.
 */
internal class RequestFlowProbe<T>(source: Flow<T>) {
    private val started = CompletableDeferred<Unit>()

    // One-based number of every value the client has taken, in order.
    private val emissions = Channel<Int>(Channel.UNLIMITED)
    private val completion = CompletableDeferred<RequestFlowCompletion>()

    /** The instrumented request flow to pass to the client. Supports a single collection. */
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

    /** Waits until the client has started collecting [flow]. */
    internal suspend fun awaitCollectionStarted() {
        started.await()
    }

    /**
     * Waits for the next value to be emitted downstream and checks that it is the [expectedOccurrence]-th one.
     *
     * Emissions are consumed in order, so calling this for occurrences 1, 2, 3, ... walks through the values the
     * client took one at a time; skipping an occurrence fails the check.
     */
    internal suspend fun awaitNextEmission(expectedOccurrence: Int) {
        require(expectedOccurrence > 0) { "emission occurrence must be one-based" }
        val occurrence = emissions.receive()
        check(occurrence == expectedOccurrence) {
            "expected request emission $expectedOccurrence, observed $occurrence"
        }
    }

    /** Waits until the collection of [flow] has ended and returns how it ended. */
    internal suspend fun awaitCompletion(): RequestFlowCompletion = completion.await()

    /** Waits for the collection to end and checks that it was cancelled. */
    internal suspend fun awaitCancellation() {
        val completion = awaitCompletion()
        check(completion is RequestFlowCompletion.Failed && completion.cause is CancellationException) {
            "expected request flow cancellation, observed $completion"
        }
    }
}

/**
 * One-shot request flow whose individual values are released explicitly by the test.
 *
 * Each value of [values] is emitted only after the test calls [releaseNext] for it, so the test decides exactly
 * when the client gets its next request, for example to check what the client and server do between two
 * requests. Values which are never released are never emitted, and the collection stays suspended until the
 * client cancels it. Releases are buffered, so releasing ahead of the client does not suspend.
 *
 * The flow is observed by a [RequestFlowProbe], and the other functions delegate to it.
 *
 * ```kotlin
 * val requests = GatedRequestFlow(listOf(first, second))
 * val response = async { testService.streamingInputCall(requests.flow) }
 * requests.awaitCollectionStarted()
 * requests.releaseNext()
 * requests.awaitNextEmission(expectedOccurrence = 1)
 * awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED)
 * // The server got the first request; the second one is still held back.
 * ```
 *
 * @param values The request values, emitted in order.
 */
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

    /** The gated request flow to pass to the client. Supports a single collection. */
    internal val flow: Flow<T> get() = probe.flow

    /** See [RequestFlowProbe.awaitCollectionStarted]. */
    internal suspend fun awaitCollectionStarted() {
        probe.awaitCollectionStarted()
    }

    /** Allows the flow to emit its next value as soon as the client collects it. */
    internal suspend fun releaseNext() {
        permits.send(Unit)
    }

    /** See [RequestFlowProbe.awaitNextEmission]. */
    internal suspend fun awaitNextEmission(expectedOccurrence: Int) {
        probe.awaitNextEmission(expectedOccurrence)
    }

    /** See [RequestFlowProbe.awaitCompletion]. */
    internal suspend fun awaitCompletion(): RequestFlowCompletion = probe.awaitCompletion()

    /** See [RequestFlowProbe.awaitCancellation]. */
    internal suspend fun awaitCancellation() {
        probe.awaitCancellation()
    }
}

/**
 * Creates a finite cold request flow from a snapshot of the supplied values.
 *
 * The values are copied, so later changes to [values] do not affect the flow. Unlike [RequestFlowProbe.flow],
 * the result can be collected any number of times.
 */
internal fun <T> finiteRequestFlow(values: Iterable<T>): Flow<T> = values.toList().asFlow()
