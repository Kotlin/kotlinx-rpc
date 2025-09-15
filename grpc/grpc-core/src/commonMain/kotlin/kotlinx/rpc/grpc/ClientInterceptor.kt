/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.internal.GrpcCallOptions
import kotlinx.rpc.grpc.internal.MethodDescriptor

/**
 * Represents a client call scope within a coroutine context, providing access to properties and
 * functions required to manage the lifecycle and behavior of a client-side remote procedure call
 * (RPC) in a coroutine-based environment.
 *
 * @param Request the type of the request message sent to the gRPC server.
 * @param Response the type of the response message received from the gRPC server.
 */
public interface ClientCallScope<Request, Response> {
    public val method: MethodDescriptor<Request, Response>
    public val metadata: GrpcTrailers
    public val callOptions: GrpcCallOptions
    public fun onHeaders(block: (GrpcTrailers) -> Unit)
    public fun onClose(block: (Status, GrpcTrailers) -> Unit)
    public fun cancel(message: String, cause: Throwable? = null)
    public fun proceed(request: Flow<Request>): Flow<Response>
}

public interface ClientInterceptor {

    /**
     * Intercepts and transforms the flow of requests and responses in a client call.
     * An interceptor can throw an exception at any time to cancel the call.
     *
     * @param scope The scope of the client call, providing context and methods for managing
     * the call lifecycle and metadata.
     * @param request A flow of requests to be sent to the server.
     * @return A flow of responses received from the server.
     */
    public fun <Request, Response> intercept(
        scope: ClientCallScope<Request, Response>,
        request: Flow<Request>,
    ): Flow<Response>

}