/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.internal.GrpcCallOptions
import kotlinx.rpc.grpc.internal.MethodDescriptor

public interface ServerCallScope<Request, Response> {
    public val method: MethodDescriptor<Request, Response>
    public val responseHeaders: GrpcTrailers
    public fun onCancel(block: () -> Unit)
    public fun onComplete(block: () -> Unit)
    public fun proceed(request: Flow<Request>): Flow<Response>
}

public interface ServerInterceptor {
    public fun <Request, Response> intercept(
        scope: ServerCallScope<Request, Response>,
        requestHeaders: GrpcTrailers,
        request: Flow<Request>,
    ): Flow<Response>
}