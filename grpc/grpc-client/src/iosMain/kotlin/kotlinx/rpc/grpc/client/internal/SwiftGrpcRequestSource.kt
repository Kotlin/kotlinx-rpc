/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.atomicfu.atomic
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestBytes
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.client.SwiftGrpcRequestSourceProtocol
import kotlin.coroutines.cancellation.CancellationException

private typealias RequestCompletion = SwiftGrpcCompletion<SwiftGrpcRequestBytes>

/**
 * Takes a flow of [SwiftGrpcRequestBytes] encoded requests and turns it into a [SwiftGrpcRequestSourceProtocol].
 *
 * Use the [SwiftGrpcRequestSourceProtocol] only inside the [block] lambda.
 *
 * This function returns only after the request collection ends. The consumer of the request source must call
 * `cancel`, or pull until the source reports the end of the stream. A consumer that does neither suspends this
 * function forever.
 */
internal suspend fun <T> withRequestSource(
    requests: Flow<SwiftGrpcRequestBytes>,
    block: suspend (SwiftGrpcRequestSourceProtocol) -> T
) = coroutineScope {

    // The termination state of the request source
    class Termination(val failure: Throwable?)
    val termination = atomic<Termination?>(null)

    // The request source adds pull requests (completion callbacks) to the pull queue.
    // There must only be one pull request at a time.
    val pulls = Channel<RequestCompletion>(capacity = 1, onUndeliveredElement = { completion ->
        // On cancellation of the pull (normal termination), we complete all pending requests.
        completion(null, termination.value?.failure?.toSwiftGrpcError())
    })

    fun terminate(failure: Throwable?) {
        if (termination.compareAndSet(null, Termination(failure))) {
            pulls.cancel()
        }
    }

    val collector = launch(
        context = CoroutineName("grpc-swift-request-source"),
        start = CoroutineStart.UNDISPATCHED
    ) {
        try {
            requests.collect { request ->
                val completion = pulls.receive()
                completion(request, null)
            }
            terminate(null)
        } catch(cause: Throwable) {
            // We must catch throwable to define the termination state,
            // as requests come from the Swift runtime.
            terminate(cause.takeUnless { it is CancellationException })
            throw cause
        }
    }

    val requestSource = object : NSObject(), SwiftGrpcRequestSourceProtocol {
        override fun cancel() {
            terminate(null)
            collector.cancel()
        }

        override fun nextRequestWithCompletion(completion: RequestCompletion) {
            val result = pulls.trySend(completion)
            when {
                // If the channel is closed, we know that the request source terminated
                result.isClosed -> completion(
                    null,
                    termination.value?.failure?.toSwiftGrpcError()
                )
                // If the channel failed without being closed, the 1 pull slot is already filled,
                // and the request source consumer violently pulled before receiving the previous
                // completion.
                result.isFailure -> completion(
                    null,
                    swiftGrpcError("grpc-swift requested more than one message concurrently")
                )
            }
        }
    }

    block(requestSource)
}

private fun Throwable.toSwiftGrpcError(): NSError =
    swiftGrpcError(message ?: "Kotlin request flow failed")
