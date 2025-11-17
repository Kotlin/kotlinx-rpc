/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import io.grpc.CallOptions
import kotlinx.rpc.grpc.GrpcCompression
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

internal fun GrpcCallOptions.toJvm(coroutineContext: CoroutineContext): CallOptions {
    var default = CallOptions.DEFAULT
    if (timeout != null) {
        default = default.withDeadlineAfter(timeout!!.inWholeMilliseconds, TimeUnit.MILLISECONDS)
    }
    if (compression !is GrpcCompression.None) {
        default = default.withCompression(compression.name)
    }
    if (callCredentials !is EmptyCallCredentials) {
        default = default.withCallCredentials(callCredentials.toJvm(coroutineContext))
    }
    return default
}