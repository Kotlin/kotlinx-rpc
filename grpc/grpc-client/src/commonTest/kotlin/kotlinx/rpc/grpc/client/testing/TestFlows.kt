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

/** The final state of a request flow collected by the client. */
internal sealed interface RequestFlowCompletion {
    /** The source emitted all its values, and the client collected all of them. */
    data object Completed : RequestFlowCompletion

    /**
     * The collection ended with [cause]: either the source threw, or the client stopped it.
     * A [CancellationException] means the client cancelled the collection, for example because the call failed.
     */
    data class Failed(val cause: Throwable) : RequestFlowCompletion
}

/**
 * A probe that records the collection, downstream emission, completion, failure, and cancellation of one
 * request flow.
 *
 * Pass [flow] to the client instead of the source. The flow forwards every value of the source unchanged and
 * records each collection event. A test uses these records to synchronize with the client's request side:
 * - [awaitCollectionStarted] returns when the client starts the collection.
 * - [awaitNextEmission] returns when the client takes the next value. The probe records an emission after
 *   the downstream `emit` returns. A value that the client never accepts isn't recorded.
 * - [awaitCompletion] and [awaitCancellation] return when the collection ends, normally or with the cause
 *   of the failure.
 *
 * Recording never suspends, so the probe doesn't change the client's backpressure. The client can collect
 * [flow] only once. A second collection fails, which catches a client that re-subscribes to its request source.
 *
 * @param source The request values to forward to the client.
 */
internal class RequestFlowProbe<T>(source: Flow<T>) {
    private val started = CompletableDeferred<Unit>()

    // One-based number of each value the client takes, in order.
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

    /** Waits until the client starts to collect [flow]. */
    internal suspend fun awaitCollectionStarted() {
        started.await()
    }

    /**
     * Waits for the next downstream emission and checks that it is the [expectedOccurrence]-th one.
     *
     * The probe consumes the emissions in order. Call this method for the occurrences 1, 2, 3, ... in sequence.
     * A skipped occurrence fails the check.
     */
    internal suspend fun awaitNextEmission(expectedOccurrence: Int) {
        require(expectedOccurrence > 0) { "emission occurrence must be one-based" }
        val occurrence = emissions.receive()
        check(occurrence == expectedOccurrence) {
            "expected request emission $expectedOccurrence, observed $occurrence"
        }
    }

    /** Waits until the collection of [flow] ends and returns how it ended. */
    internal suspend fun awaitCompletion(): RequestFlowCompletion = completion.await()

    /** Waits for the collection to end and checks that the client cancelled it. */
    internal suspend fun awaitCancellation() {
        val completion = awaitCompletion()
        check(completion is RequestFlowCompletion.Failed && completion.cause is CancellationException) {
            "expected request flow cancellation, observed $completion"
        }
    }
}

/**
 * A one-shot request flow that emits its values only when the test releases them.
 *
 * The flow emits each value of [values] only after the test calls [releaseNext] for it. The test decides
 * exactly when the client gets its next request, for example to check what the client and server do between
 * two requests. A value that the test never releases is never emitted, and the collection stays suspended
 * until the client cancels it. The flow buffers the releases, so an early release doesn't suspend the test.
 *
 * A [RequestFlowProbe] observes the flow, and the other functions delegate to it.
 *
 * ```kotlin
 * val requests = GatedRequestFlow(listOf(first, second))
 * val response = async { testService.streamingInputCall(requests.flow) }
 * requests.awaitCollectionStarted()
 * requests.releaseNext()
 * requests.awaitNextEmission(expectedOccurrence = 1)
 * awaitServerEvent(EventType.REQUEST_MESSAGE_RECEIVED)
 * // The server got the first request. The second one is still gated.
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

    /** Lets the flow emit its next value as soon as the client collects it. */
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
 * Creates a finite cold request flow from a snapshot of the given values.
 *
 * The function copies the values, so later changes to [values] don't affect the flow. Unlike
 * [RequestFlowProbe.flow], the client can collect the result any number of times.
 */
internal fun <T> finiteRequestFlow(values: Iterable<T>): Flow<T> = values.toList().asFlow()
