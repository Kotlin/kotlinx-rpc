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
import kotlinx.coroutines.channels.getOrElse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestMessageProtocol
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestSourceProtocol

private typealias RequestCompletion = SwiftGrpcCompletion<SwiftGrpcRequestMessageProtocol>

/** Adapts a Kotlin request flow to grpc-swift's single-outstanding-pull protocol.
 *
 * It uses a similar mechanism to the kotlin/java bridge implementation: a collector offers one
 * request at a time through a rendezvous channel, and every Swift pull receives one of them.
 *
 * Pulls and termination are coordinated by a single atomic [State]:
 *
 * - [State.Idle]: no pull is outstanding. A pull moves to [State.Pending].
 * - [State.Pending]: Swift waits for its completion. Delivering a request returns to [State.Idle].
 * - [State.Terminated]: the end of the request flow, its failure, or cancellation won. It is final.
 *
 * Only one transition into [State.Terminated] succeeds. Its winner stops [sourceJob] and then
 * completes the pull it claimed, if any. All later terminal events, including repeated [cancel]
 * calls, are no-ops. Completions are invoked only after their transition is committed, so they may
 * re-enter this source.
 */
internal class KotlinGrpcRequestSource<Request>(
    scope: CoroutineScope,
    requests: Flow<Request>,
    private val encode: (Request) -> SwiftGrpcRequestMessageProtocol,
) : NSObject(), SwiftGrpcRequestSourceProtocol {
    private val sourceJob = SupervisorJob()
    private val sourceScope = CoroutineScope(scope.coroutineContext + sourceJob)
    private val messages = Channel<Request>(Channel.RENDEZVOUS)
    private val state = atomic<State>(State.Idle)

    init {
        scope.coroutineContext[Job]?.invokeOnCompletion { cancel() }

        // As the channel is a RENDEZVOUS channel, only one request is collected at a time.
        sourceScope.launch(
            context = CoroutineName("grpc-swift-request-source"),
            start = CoroutineStart.UNDISPATCHED,
        ) {
            try {
                requests.collect(messages::send)
                messages.close()
            } catch (cause: Throwable) {
                // Cancellation is caused either by termination of this source, or by the request
                // flow itself, which is treated as the end of the flow.
                if (cause is CancellationException) messages.close() else fail(cause)
            }
        }
    }

    internal val originalFailure: Throwable?
        get() = (state.value as? State.Terminated)?.failure

    override fun nextRequestWithCompletion(completion: RequestCompletion) {
        val pending = State.Pending(completion)
        while (true) {
            when (val current = state.value) {
                is State.Terminated -> {
                    completion(null, current.failure?.toSwiftGrpcError())
                    return
                }

                is State.Pending -> {
                    completion(null, swiftGrpcError("grpc-swift requested more than one message concurrently"))
                    return
                }

                State.Idle -> if (state.compareAndSet(current, pending)) {
                    sourceScope.launch(start = CoroutineStart.UNDISPATCHED) { pull() }
                    return
                }
            }
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

    private suspend fun pull() {
        val result = try {
            messages.receiveCatching()
        } catch (_: CancellationException) {
            // Only a terminal transition cancels the source, and it already completed this pull.
            return
        }

        val request = result.getOrElse {
            // The collector closes the channel only after it has finished, and it fails the source
            // itself. So a closed channel is the regular end of the request flow.
            finish()
            return
        }

        val message = try {
            encode(request)
        } catch (cause: Throwable) {
            fail(cause)
            return
        }

        deliver(message)
    }

    private fun deliver(message: SwiftGrpcRequestMessageProtocol) {
        val current = state.value
        // If termination claimed the pull in the meantime, the message is dropped.
        if (current is State.Pending && state.compareAndSet(current, State.Idle)) {
            current.completion(message, null)
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
        while (true) {
            val current = state.value
            if (current is State.Terminated) return
            if (state.compareAndSet(current, terminal)) {
                // Tear down first so that invoking the completion cannot re-enter a still-active source.
                stop()
                if (current is State.Pending) notify(current.completion)
                return
            }
        }
    }

    private fun Throwable.toSwiftGrpcError(): NSError =
        swiftGrpcError(message ?: "Kotlin request flow failed")

    private sealed interface State {
        data object Idle : State

        class Pending(val completion: RequestCompletion) : State

        /** The final state. A `null` [failure] means the request flow ended or was cancelled. */
        class Terminated(val failure: Throwable?) : State {
            companion object {
                val Completed = Terminated(null)
            }
        }
    }
}
