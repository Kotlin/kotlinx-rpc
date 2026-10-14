/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.CallOptions
import kotlinx.rpc.grpc.GrpcCompression
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

internal fun GrpcCallOptions.toJvm(coroutineContext: CoroutineContext): CallOptions {
    var default = CallOptions.DEFAULT
    if (isTimeoutExplicitlySet && timeout != Duration.INFINITE) {
        // Only change the java default (infinite) if timeout is explicitly set and not infinite.
        default = default.withDeadlineAfter(timeout.inWholeNanoseconds, TimeUnit.NANOSECONDS)
    }
    if (isWaitForReadyExplicitlySet) {
        default = when (waitForReady) {
            true -> default.withWaitForReady()
            false -> default.withoutWaitForReady()
        }
    }
    if (compression !is GrpcCompression.None) {
        default = default.withCompression(compression.name)
    }
    if (callCredentials !is GrpcEmptyCallCredentials) {
        default = default.withCallCredentials(callCredentials.toJvm(coroutineContext))
    }
    return default
}
