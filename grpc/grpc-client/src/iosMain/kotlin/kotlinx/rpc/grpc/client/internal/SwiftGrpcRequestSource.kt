/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestMessageProtocol
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestSourceProtocol
import kotlin.coroutines.resume

private typealias RequestCompletion = SwiftGrpcCompletion<SwiftGrpcRequestMessageProtocol>

/**
 * Adapts the request Flow of a client-streaming or bidirectional call to grpc-swift's
 * single-outstanding-pull protocol.
 *
 * The source owns one persistent collector and pre-encodes at most one request. Its atomic
 * rendezvous has five states:
 *
 * - [State.Idle]: neither an emission nor a pull is waiting.
 * - [State.WaitingPull]: Swift pulled before the Flow emitted.
 * - [State.Offered]: the collector holds one encoded request in its stack and is suspended until
 *   Swift pulls; only its continuation is stored in the state.
 * - [State.Delivering]: a pull claimed the encoded request while the collector delivers it.
 * - [State.Terminated]: request EOF, failure, or transport cancellation won. It is final and keeps
 *   the failure available to late pulls.
 *
 * State transitions commit before callbacks or continuations run, so those external actions may
 * re-enter this source. At most one pull callback or collector continuation is retained.
 */
internal class KotlinGrpcRequestSource<Request>(
    scope: CoroutineScope,
    requests: Flow<Request>,
    private val encode: (Request) -> SwiftGrpcRequestMessageProtocol,
) : NSObject(), SwiftGrpcRequestSourceProtocol {
    private val sourceJob = SupervisorJob()
    private val sourceScope = CoroutineScope(scope.coroutineContext + sourceJob)
    private val state = atomic<State>(State.Idle)

    init {
        scope.coroutineContext[Job]?.invokeOnCompletion { cancel() }

        // Pre-encode one request, then suspend until exactly one Swift pull claims it.
        sourceScope.launch(
            context = CoroutineName("grpc-swift-request-source"),
            start = CoroutineStart.UNDISPATCHED,
        ) {
            try {
                requests.collect { request ->
                    val message = encode(request)
                    val completion = awaitPull()
                    deliver(completion, message)
                }
                finish()
            } catch (cause: Throwable) {
                // Cancellation is caused either by termination of this source, or by the request
                // flow itself, which is treated as the end of the flow.
                if (cause is CancellationException) finish() else fail(cause)
            }
        }
    }

    internal val originalFailure: Throwable?
        get() = (state.value as? State.Terminated)?.failure

    override fun nextRequestWithCompletion(completion: RequestCompletion) {
        val waitingPull = State.WaitingPull(completion)
        var offered: State.Offered? = null
        var terminalFailure: Throwable? = null
        var terminated = false
        var rejected = false

        while (true) {
            when (val current = state.value) {
                is State.Terminated -> {
                    terminalFailure = current.failure
                    terminated = true
                    break
                }

                is State.WaitingPull, is State.Delivering -> {
                    rejected = true
                    break
                }

                is State.Offered -> if (state.compareAndSet(current, State.Delivering(completion))) {
                    offered = current
                    break
                }

                State.Idle -> if (state.compareAndSet(current, waitingPull)) {
                    return
                }
            }
        }

        when {
            offered != null -> offered.continuation.resume(completion)
            terminated -> completion(null, terminalFailure?.toSwiftGrpcError())
            rejected -> completion(
                null,
                swiftGrpcError("grpc-swift requested more than one message concurrently"),
            )
        }
    }

    override fun cancel() {
        terminate(
            terminal = State.Terminated.Completed,
            stop = { sourceJob.cancel(CancellationException("grpc-swift call stopped consuming requests")) },
            // Cancellation is transport control flow, not a request failure. Completing a pending pull
            // as EOF lets grpc-swift preserve the call's real terminal status (for example a deadline).
            notify = { completion -> completion(null, null) },
        )
    }

    internal suspend fun cancelAndJoin() {
        cancel()
        sourceJob.join()
    }

    private suspend fun awaitPull(): RequestCompletion = suspendCancellableCoroutine { continuation ->
        val offered = State.Offered(continuation)
        continuation.invokeOnCancellation { withdraw(offered) }
        var waitingCompletion: RequestCompletion? = null
        var terminated = false

        while (continuation.isActive) {
            when (val current = state.value) {
                State.Idle -> if (state.compareAndSet(current, offered)) {
                    // Cancellation may have run its handler immediately before publication.
                    if (!continuation.isActive) withdraw(offered)
                    return@suspendCancellableCoroutine
                }

                is State.WaitingPull -> if (state.compareAndSet(current, State.Delivering(current.completion))) {
                    waitingCompletion = current.completion
                    break
                }

                is State.Terminated -> {
                    terminated = true
                    break
                }

                is State.Offered, is State.Delivering -> error(
                    "The request collector made a second offer before the first was delivered"
                )
            }
        }

        if (waitingCompletion != null) continuation.resume(waitingCompletion)
        else if (terminated || !continuation.isActive) continuation.cancel()
    }

    private fun withdraw(offered: State.Offered) {
        state.compareAndSet(offered, State.Idle)
    }

    private fun deliver(completion: RequestCompletion, message: SwiftGrpcRequestMessageProtocol) {
        val current = state.value
        // If termination claimed the pull in the meantime, the message is dropped.
        if (
            current is State.Delivering &&
            current.completion === completion &&
            state.compareAndSet(current, State.Idle)
        ) {
            completion(message, null)
        }
    }

    private fun finish() {
        terminate(
            terminal = State.Terminated.Completed,
            // The collector has finished, so the job completes without allocating a cancellation.
            stop = { sourceJob.complete() },
            notify = { completion -> completion(null, null) },
        )
    }

    private fun fail(cause: Throwable) {
        terminate(
            terminal = State.Terminated(cause),
            stop = { sourceJob.cancel() },
            notify = { completion -> completion(null, cause.toSwiftGrpcError()) },
        )
    }

    private inline fun terminate(
        terminal: State.Terminated,
        stop: () -> Unit,
        notify: (RequestCompletion) -> Unit,
    ) {
        lateinit var claimed: State
        while (true) {
            val current = state.value
            if (current is State.Terminated) return
            if (state.compareAndSet(current, terminal)) {
                claimed = current
                break
            }
        }

        // Tear down first so that invoking the completion cannot re-enter a still-active source.
        stop()
        when (val current = claimed) {
            is State.WaitingPull -> notify(current.completion)
            is State.Offered -> current.continuation.cancel()
            is State.Delivering -> notify(current.completion)
            State.Idle -> Unit
            is State.Terminated -> error("A terminal state cannot win termination twice")
        }
    }

    private sealed interface State {
        data object Idle : State

        class WaitingPull(val completion: RequestCompletion) : State

        class Offered(val continuation: CancellableContinuation<RequestCompletion>) : State

        class Delivering(val completion: RequestCompletion) : State

        /** The final state. A `null` [failure] means the request flow ended or was cancelled. */
        class Terminated(val failure: Throwable?) : State {
            companion object {
                val Completed = Terminated(null)
            }
        }
    }
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
    encode: (Request) -> SwiftGrpcRequestMessageProtocol,
) : NSObject(), SwiftGrpcRequestSourceProtocol {
    private val message = atomic<SwiftGrpcRequestMessageProtocol?>(null)
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
        scope.coroutineContext[Job]?.invokeOnCompletion { cancel() }
    }

    internal val originalFailure: Throwable?
        get() = failure.value

    /**
     * The encoded request if collection has already succeeded, so Swift can send it without a pull.
     * It is set only after the flow ended with exactly one request. This is the fast-lane.
     */
    internal val readyMessage: SwiftGrpcRequestMessageProtocol?
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
internal val SwiftGrpcRequestSourceProtocol.readyMessage: SwiftGrpcRequestMessageProtocol?
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
