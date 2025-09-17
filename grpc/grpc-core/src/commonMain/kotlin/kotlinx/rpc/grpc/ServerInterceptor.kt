/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.rpc.grpc.internal.MethodDescriptor

public interface ServerCallScope<Request, Response> {
    public val method: MethodDescriptor<Request, Response>
    public val requestHeaders: GrpcMetadata
    public val responseHeaders: GrpcMetadata
    public val responseTrailers: GrpcMetadata

    public fun onClose(block: (Status, GrpcMetadata) -> Unit)
    public fun close(status: Status, trailers: GrpcMetadata = GrpcMetadata()): Nothing
    public fun proceed(request: Flow<Request>): Flow<Response>

    public suspend fun FlowCollector<Response>.proceedFlow(request: Flow<Request>) {
        proceed(request).collect {
            emit(it)
        }
    }
}

public interface ServerInterceptor {
    public fun <Request, Response> ServerCallScope<Request, Response>.intercept(
        request: Flow<Request>,
    ): Flow<Response>
}