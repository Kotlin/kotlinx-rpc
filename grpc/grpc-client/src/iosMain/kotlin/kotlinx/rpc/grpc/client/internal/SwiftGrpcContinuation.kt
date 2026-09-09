/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** An infrastructure failure reported by an Objective-C completion handler. */
internal class SwiftGrpcInteropException(
    val error: NSError,
) : RuntimeException(error.localizedDescription)

internal fun swiftGrpcError(description: String): NSError = NSError(
    domain = "org.jetbrains.kotlinx.rpc.grpc.swift",
    code = 1,
    userInfo = mapOf("NSLocalizedDescription" to description),
)

/**
 * Completion handler type for Objective-C APIs that use a completion handler.
 */
internal typealias SwiftGrpcCompletion<Value> = (value: Value?, error: NSError?) -> Unit

/**
 * Adapts an Objective-C completion-handler API to a cancellable Kotlin suspension without
 * blocking a thread.
 *
 * A null value with no error is a successful result and can represent the end of a stream. The
 * cancellation action must cause the underlying operation to finish its outstanding completion;
 * late completions are safely ignored by the cancellable continuation.
 */
internal suspend fun <Value : Any> awaitSwiftGrpcCompletion(
    onCancellation: () -> Unit,
    register: (completion: SwiftGrpcCompletion<Value>) -> Unit,
): Value? = suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation {
        onCancellation()
    }

    try {
        register { value, error ->
            if (error != null) {
                continuation.resumeWithException(SwiftGrpcInteropException(error))
            } else {
                continuation.resume(value)
            }
        }
    } catch (cause: Throwable) {
        if (continuation.isActive) {
            continuation.resumeWithException(cause)
        }
    }
}
