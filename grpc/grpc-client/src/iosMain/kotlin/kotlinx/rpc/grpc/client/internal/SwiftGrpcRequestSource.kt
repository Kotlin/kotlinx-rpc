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
import kotlinx.coroutines.launch
import platform.Foundation.NSError
import platform.darwin.NSObject
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestMessageProtocol
import swiftPMImport.org.jetbrains.kotlinx.grpc.grpc.swift.SwiftGrpcRequestSourceProtocol

/** Adapts a Kotlin request flow to grpc-swift's single-outstanding-pull protocol.
 *
 * It uses a similar mechanism to the kotlin/java bridge implementation.
 */
internal class KotlinGrpcRequestSource<Request>(
    scope: CoroutineScope,
    requests: Flow<Request>,
    private val encode: (Request) -> SwiftGrpcRequestMessageProtocol,
) : NSObject(), SwiftGrpcRequestSourceProtocol {
    private val sourceJob = SupervisorJob()
    private val sourceScope = CoroutineScope(scope.coroutineContext + sourceJob)
    private val messages = Channel<Request>(Channel.RENDEZVOUS)

    // Holds the completion handler for the current request.
    // This also acts as guard to allow only one request at a time.
    private val pendingCompletion = atomic<((SwiftGrpcRequestMessageProtocol?, NSError?) -> Unit)?>(null)

    // Keeps track of the first failure that occurred
    private val failure = atomic<Throwable?>(null)

    init {
        scope.coroutineContext[Job]?.invokeOnCompletion { cancel() }
    }

    // The collector collects message from the request flow and sends them
    // to the message channel. As it is a RENDEZVOUS channel, we only
    // collect one request at a time.
    //
    // In case of a failure, the [failure] property is set, if not already set.
    // Then the message channel is closed to trigger completion.
    private val collector: Job = sourceScope.launch(
        context = CoroutineName("grpc-swift-request-source"),
        start = CoroutineStart.UNDISPATCHED,
    ) {
        try {
            requests.collect(messages::send)
            messages.close()
        } catch (cause: Throwable) {
            if (cause !is CancellationException) failure.compareAndSet(null, cause)
            messages.close(cause)
        }
    }

    internal val originalFailure: Throwable?
        get() = failure.value

    override fun nextRequestWithCompletion(
        completion: (SwiftGrpcRequestMessageProtocol?, NSError?) -> Unit,
    ) {
        if (!pendingCompletion.compareAndSet(expect = null, update = completion)) {
            completion(null, swiftGrpcError("grpc-swift requested more than one message concurrently"))
            return
        }

        // Enter the body even if cancellation races with a Swift pull, so that its completion is
        // always attempted.
        // Final request source cancellation should happen using the `cancel()` call from
        // the Swift side; therefore, all cancellation exceptions are caught and not rethrown.
        sourceScope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val result = messages.receiveCatching()
                val request = result.getOrNull()
                when {
                    request != null -> complete(encode(request), null)
                    // No more requests available (channel closed)
                    result.exceptionOrNull() == null -> complete(null, null)
                    else -> {
                        val cause = result.exceptionOrNull()!!
                        if (cause !is CancellationException) setFailure(cause)
                        complete(null, swiftGrpcError(cause.message ?: "Kotlin request flow failed"))
                    }
                }
            } catch (cause: Throwable) {
                if (cause !is CancellationException) setFailure(cause)
                complete(null, swiftGrpcError(cause.message ?: "Kotlin request source failed"))
            }
        }
    }

    override fun cancel() {
        val cause = CancellationException("grpc-swift call stopped consuming requests")
        // Tear down first so that invoking the completion cannot re-enter a still-active source.
        collector.cancel(cause)
        messages.cancel(cause)
        sourceJob.cancel(cause)
        complete(null, swiftGrpcError(cause.message ?: "Kotlin request source was cancelled"))
    }

    private fun complete(message: SwiftGrpcRequestMessageProtocol?, error: NSError?) {
        // Get the completion handler and free the slot to allow the next request.
        val completion = pendingCompletion.getAndSet(null) ?: return
        // Invoke the completion handler.
        completion(message, error)
    }

    private fun setFailure(cause: Throwable) {
        failure.compareAndSet(null, cause)
    }
}
