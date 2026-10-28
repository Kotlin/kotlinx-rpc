/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestSourceProtocol

private typealias RequestCompletion = SwiftGrpcCompletion<SwiftGrpcRequestBytes>

/**
 * Collects streaming requests and supplies encoded messages to Swift pulls.
 *
 * The [scope] must belong to one RPC call.
 */
internal class KotlinGrpcRequestSource<Request>(
    scope: CoroutineScope,
    requests: Flow<Request>,
    encode: (Request) -> SwiftGrpcRequestBytes,
) : NSObject(), SwiftGrpcRequestSourceProtocol {

    /** A `null` [failure] means the request flow ended or was cancelled. */
    private class Termination(val failure: Throwable?)

    private val termination = atomic<Termination?>(null)

    // The channel owns pending completions. After receive(), the collector owns the completion.
    // The channel or collector responsible for a pending request must invoke its callback exactly once.
    private val pulls = Channel(capacity = 1, onUndeliveredElement = ::completeTerminated)

    private val collector = scope.launch(
        context = CoroutineName("grpc-swift-request-source"),
        start = CoroutineStart.UNDISPATCHED,
    ) {
        try {
            requests.collect { request ->
                // Encode request, wait for Swift pull and invoke its completion callback.
                val message = encode(request)
                val completion = pulls.receive()
                completion(message, null)
            }
            terminate(null)
        } catch (cause: Exception) {
            // Request-flow cancellation is EOF. Transport cancellation keeps the call's terminal status.
            terminate(cause.takeUnless { it is CancellationException })
        }
    }

    init {
        // Complete pending pulls even when an error escapes or collection never starts.
        collector.invokeOnCompletion { cause ->
            terminate(cause.takeUnless { it is CancellationException })
        }
    }

    internal val originalFailure: Throwable?
        get() = termination.value?.failure

    override fun nextRequestWithCompletion(completion: RequestCompletion) {
        val result = pulls.trySend(completion)
        when {
            result.isClosed -> completeTerminated(completion)
            result.isFailure -> completion(
                null,
                swiftGrpcError("grpc-swift requested more than one message concurrently"),
            )
        }
    }

    override fun cancel() {
        // Cancellation is transport control flow, not a request failure. Completing a pending pull
        // as EOF lets grpc-swift preserve the call's real terminal status (for example a deadline).
        terminate(null)
        collector.cancel(CancellationException("grpc-swift call stopped consuming requests"))
    }

    internal suspend fun cancelAndJoin() {
        cancel()
        collector.join()
    }

    private fun terminate(failure: Throwable?) {
        // Pulls re-entering from a completion observe the cancelled channel and complete at once.
        if (termination.compareAndSet(null, Termination(failure))) {
            pulls.cancel()
        }
    }

    private fun completeTerminated(completion: RequestCompletion) {
        // Late pulls get the stored failure or EOF.
        completion(null, termination.value?.failure?.toSwiftGrpcError())
    }
}

/**
 * Adapts the request flow of a unary or server-streaming call, which grpc-swift pulls exactly once.
 *
 * Collection starts immediately and completes only after the flow has ended, so the pull receives
 * a request only if the flow emitted exactly one. The pull's completion runs once collection has
 * finished: with the encoded request, with the flow's failure, or as EOF if the source was
 * cancelled first.
 *
 * The [scope] must belong to one RPC call. An uncaught error cancels that call.
 */
internal class KotlinGrpcSingleRequestSource<Request>(
    scope: CoroutineScope,
    requests: Flow<Request>,
    encode: (Request) -> SwiftGrpcRequestBytes,
) : NSObject(), SwiftGrpcRequestSourceProtocol {
    // Cancellation can withdraw a ready request, but must keep an earlier failure.
    private val outcome = atomic<Outcome?>(null)
    private val pulled = atomic(false)

    private val job = scope.launch(
        context = CoroutineName("grpc-swift-single-request-source"),
        start = CoroutineStart.UNDISPATCHED,
    ) {
        try {
            val message = encode(requests.single())
            outcome.compareAndSet(null, Outcome.Ready(message))
        } catch (_: CancellationException) {
            // Cancellation, by this source or by the request flow itself, ends the requests without one.
        } catch (cause: Exception) {
            outcome.compareAndSet(null, Outcome.Failed(cause))
        }
    }

    init {
        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                cancelOutcome()
            } else {
                outcome.compareAndSet(null, if (cause == null) Outcome.Cancelled else Outcome.Failed(cause))
            }
        }
        // A ready request can outlive collection, but must not outlive its call.
        scope.coroutineContext[Job]?.invokeOnCompletion {
            val current = outcome.value
            if (current is Outcome.Ready) {
                outcome.compareAndSet(current, Outcome.Cancelled)
            }
        }
    }

    internal val originalFailure: Throwable?
        get() = (outcome.value as? Outcome.Failed)?.cause

    /** The encoded request, available only after the flow ends with exactly one value. */
    internal val readyMessage: SwiftGrpcRequestBytes?
        get() = (outcome.value as? Outcome.Ready)?.message

    override fun nextRequestWithCompletion(completion: RequestCompletion) {
        if (!pulled.compareAndSet(expect = false, update = true)) {
            completion(
                null,
                swiftGrpcError("grpc-swift requested more than one message for a single-request call")
            )
            return
        }

        job.invokeOnCompletion {
            when (val current = outcome.value) {
                is Outcome.Ready -> completion(current.message, null)
                is Outcome.Failed -> completion(null, current.cause.toSwiftGrpcError())
                Outcome.Cancelled, null -> completion(null, null)
            }
        }
    }

    override fun cancel() {
        job.cancel(CancellationException("grpc-swift call stopped consuming requests"))
        cancelOutcome()
    }

    private fun cancelOutcome() {
        while (true) {
            val current = outcome.value
            if (current is Outcome.Failed || current == Outcome.Cancelled) {
                break
            }
            if (outcome.compareAndSet(current, Outcome.Cancelled)) {
                break
            }
        }
    }

    internal suspend fun cancelAndJoin() {
        cancel()
        job.join()
    }

    private sealed interface Outcome {
        class Ready(val message: SwiftGrpcRequestBytes) : Outcome

        class Failed(val cause: Throwable) : Outcome

        data object Cancelled : Outcome
    }
}

/** The exception that failed the request flow, if it failed. */
internal val SwiftGrpcRequestSourceProtocol.originalFailure: Throwable?
    get() = when (this) {
        is KotlinGrpcRequestSource<*> -> originalFailure
        is KotlinGrpcSingleRequestSource<*> -> originalFailure
        else -> null
    }

/** The already validated request of a single-request source, if it is ready before the call starts. */
internal val SwiftGrpcRequestSourceProtocol.readyMessage: SwiftGrpcRequestBytes?
    get() = (this as? KotlinGrpcSingleRequestSource<*>)?.readyMessage

/** Cancels request collection and waits until it has stopped. */
internal suspend fun SwiftGrpcRequestSourceProtocol.cancelAndJoin() {
    when (this) {
        is KotlinGrpcRequestSource<*> -> cancelAndJoin()
        is KotlinGrpcSingleRequestSource<*> -> cancelAndJoin()
        else -> cancel()
    }
}

private fun Throwable.toSwiftGrpcError(): NSError =
    swiftGrpcError(message ?: "Kotlin request flow failed")
