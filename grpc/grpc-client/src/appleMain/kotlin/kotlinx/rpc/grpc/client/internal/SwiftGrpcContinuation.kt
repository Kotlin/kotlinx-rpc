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
    domain = "kotlinx.rpc.grpc.swift",
    code = 1,
    userInfo = mapOf("NSLocalizedDescription" to description),
)

/** Completion callback type for gRPC Obj-C API. */
internal typealias SwiftGrpcCompletion<Value> = (value: Value?, error: NSError?) -> Unit

/**
 * Suspends until an Objective-C completion handler supplies a result.
 *
 * A null value without an error can mark the end of a stream.
 * [onCancellation] must finish the pending operation. The continuation ignores late results after cancellation.
 *
 * If the completion handler supplies an error, the continuation resumes with an [SwiftGrpcInteropException].
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
    } catch (cause: Exception) {
        if (continuation.isActive) {
            continuation.resumeWithException(cause)
        }
    }
}
