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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestBytes
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestSourceProtocol

private typealias RequestCompletion = SwiftGrpcCompletion<SwiftGrpcRequestBytes>

/**
 * Adapts the request Flow of a client-streaming or bidirectional call to grpc-swift's
 * single-outstanding-pull protocol.
 *
 * One persistent collector pre-encodes a request and then receives the next Swift pull from
 * [pulls]. grpc-swift writes requests sequentially, so at most one pull is outstanding.
 * If more pulls arrive, one of them waits in [pulls] and the rest are rejected.
 *
 * Each pull completion is owned by exactly one party, which invokes it exactly once:
 * - the caller of [nextRequestWithCompletion], if [pulls] did not accept it,
 * - the collector, once it has received it,
 * - or [pulls] itself, which completes undelivered pulls as terminated when it is cancelled.
 *
 * The first of request EOF, failure, or transport cancellation sets [termination] and cancels
 * [pulls]. Termination is final and keeps the failure available to late pulls.
 */
internal class KotlinGrpcRequestSource<Request>(
    scope: CoroutineScope,
    requests: Flow<Request>,
    encode: (Request) -> SwiftGrpcRequestBytes,
) : NSObject(), SwiftGrpcRequestSourceProtocol {
    // Set before pulls is cancelled, so every pull that observes cancellation also observes it.
    private val termination = atomic<Termination?>(null)

    private val pulls = Channel<RequestCompletion>(capacity = 1, onUndeliveredElement = ::completeTerminated)

    private val collector = CoroutineScope(scope.coroutineContext + SupervisorJob()).launch(
        context = CoroutineName("grpc-swift-request-source"),
        start = CoroutineStart.UNDISPATCHED,
    ) {
        try {
            requests.collect { request ->
                val message = encode(request)
                val completion = try {
                    pulls.receive()
                } catch (cause: Throwable) {
                    message.discard()
                    throw cause
                }
                completion(message, null)
            }
            terminate(null)
        } catch (cause: Throwable) {
            // Cancellation is caused either by termination of this source, or by the request
            // flow itself, which is treated as the end of the flow.
            terminate(cause.takeUnless { it is CancellationException })
        }
    }

    init {
        scope.coroutineContext[Job]?.invokeOnCompletion { cancel() }
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
        if (termination.compareAndSet(null, Termination(failure))) pulls.cancel()
    }

    private fun completeTerminated(completion: RequestCompletion) {
        completion(null, termination.value?.failure?.toSwiftGrpcError())
    }

    /** A `null` [failure] means the request flow ended or was cancelled. */
    private class Termination(val failure: Throwable?)
}

/**
 * Adapts the request flow of a unary or server-streaming call, which grpc-swift pulls exactly once.
 *
 * Collection starts immediately and completes only after the flow has ended, so the pull receives
 * a request only if the flow emitted exactly one. The pull's completion runs once collection has
 * finished: with the encoded request, with the flow's failure, or as EOF if the source was
 * cancelled first.
 */
internal class KotlinGrpcSingleRequestSource<Request>(
    scope: CoroutineScope,
    requests: Flow<Request>,
    encode: (Request) -> SwiftGrpcRequestBytes,
) : NSObject(), SwiftGrpcRequestSourceProtocol {
    private val message = atomic<SwiftGrpcRequestBytes?>(null)
    private val failure = atomic<Throwable?>(null)
    private val pulled = atomic(false)

    private val job = CoroutineScope(scope.coroutineContext + SupervisorJob()).launch(
        context = CoroutineName("grpc-swift-single-request-source"),
        start = CoroutineStart.UNDISPATCHED,
    ) {
        try {
            message.value = encode(requests.single())
        } catch (_: CancellationException) {
            // Cancellation, by this source or by the request flow itself, ends the requests without one.
        } catch (cause: Throwable) {
            failure.value = cause
        }
    }

    init {
        // Cancellation can arrive while synchronous encoding is still finishing. In that case
        // cancel() may observe no message, so release it when the cancelled job completes.
        job.invokeOnCompletion {
            if (job.isCancelled) message.value?.discard()
        }
        scope.coroutineContext[Job]?.invokeOnCompletion { cancel() }
    }

    internal val originalFailure: Throwable?
        get() = failure.value

    /**
     * The encoded request if collection has already succeeded, so Swift can send it without a pull.
     * It is set only after the flow ended with exactly one request. This is the fast-lane.
     */
    internal val readyMessage: SwiftGrpcRequestBytes?
        get() = message.value

    override fun nextRequestWithCompletion(completion: RequestCompletion) {
        if (!pulled.compareAndSet(expect = false, update = true)) {
            completion(
                null,
                swiftGrpcError("grpc-swift requested more than one message for a single-request call")
            )
            return
        }

        // Invoked exactly once, after the request is ready, has failed, or was cancelled.
        job.invokeOnCompletion {
            val cause = failure.value
            if (cause != null) completion(null, cause.toSwiftGrpcError())
            else completion(message.value, null)
        }
    }

    override fun cancel() {
        job.cancel(CancellationException("grpc-swift call stopped consuming requests"))
        message.value?.discard()
    }

    internal suspend fun cancelAndJoin() {
        cancel()
        job.join()
    }
}

// Kotlin/Native doesn't let Objective-C classes implement Kotlin interfaces, so the transport
// reaches the Kotlin-side members of both sources through these extensions.

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
