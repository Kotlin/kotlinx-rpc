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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestSourceProtocol

private typealias RequestCompletion = SwiftGrpcCompletion<SwiftGrpcRequestBytes>

/**
 * Supplies encoded requests to Swift pulls.
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

private fun Throwable.toSwiftGrpcError(): NSError =
    swiftGrpcError(message ?: "Kotlin request flow failed")
