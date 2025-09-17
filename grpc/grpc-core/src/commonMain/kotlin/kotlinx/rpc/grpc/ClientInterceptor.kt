/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.internal.GrpcCallOptions
import kotlinx.rpc.grpc.internal.MethodDescriptor

public interface ClientCallScope<Request, Response> {
    public val method: MethodDescriptor<Request, Response>
    public val requestHeaders: GrpcMetadata
    public val callOptions: GrpcCallOptions
    public fun onHeaders(block: (responseHeaders: GrpcMetadata) -> Unit)
    public fun onClose(block: (closeStatus: Status, responseTrailers: GrpcMetadata) -> Unit)
    public fun cancel(message: String, cause: Throwable? = null): Nothing
    public fun proceed(request: Flow<Request>): Flow<Response>
}

public interface ClientInterceptor {

    /**
     * Intercepts and transforms the flow of requests and responses in a client call.
     * An interceptor can throw an exception at any time to cancel the call.
     *
     * The interceptor must ensure that it emits an expected number of values.
     * E.g. if the intercepted method is a unary call, the interceptor's returned flow must emit exactly one value.
     *
     * @param this The scope of the client call, providing context and methods for managing
     * the call lifecycle and metadata.
     * @param request A flow of requests to be sent to the server.
     * @return A flow of responses received from the server.
     */
    public fun <Request, Response> ClientCallScope<Request, Response>.intercept(
        request: Flow<Request>,
    ): Flow<Response>

}