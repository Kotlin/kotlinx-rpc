/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.client.internal.GrpcCallOptions
import kotlinx.rpc.grpc.descriptor.MethodDescriptor

/**
 * The scope of a single outgoing gRPC client call observed by a [ClientInterceptor].
 *
 * An interceptor receives this scope instance for every call and can:
 * - Inspect the RPC [method] being invoked.
 * - Read or populate [requestHeaders] before the request is sent.
 * - Read [callOptions] that affect transport-level behavior.
 * - Register callbacks with [onHeaders] and [onClose] to observe response metadata and final status.
 * - Cancel the call early via [cancel].
 * - Continue the call by calling [proceed] with a (possibly transformed) request [Flow].
 * - Transform the response by modifying the returned [Flow].
 *
 * ```kt
 *  val interceptor = object : ClientInterceptor {
 *      override fun <Request, Response> ClientCallScope<Request, Response>.intercept(
 *          request: Flow<Request>
 *      ): Flow<Response> {
 *          // Example: add a header before proceeding
 *          requestHeaders[MyKeys.Authorization] = token
 *
 *          // Example: modify call options
 *          callOptions.timeout = 5.seconds
 *
 *          // Example: observe response metadata
 *          onHeaders { headers -> /* inspect headers */ }
 *          onClose { status, trailers -> /* log status/trailers */ }
 *
 *          // IMPORTANT: proceed forwards the call to the next interceptor/transport.
 *          // If you do not call proceed, no request will be sent and the call is short-circuited.
 *          return proceed(request)
 *      }
 *  }
 * ```
 *
 * @param Request the request message type of the RPC.
 * @param Response the response message type of the RPC.
 */
public interface ClientCallScope<Request, Response> {
    /** Descriptor of the RPC method (name, marshalling, type) being invoked. */
    public val method: MethodDescriptor<Request, Response>

    /**
     * Outgoing request headers for this call.
     *
     * Interceptors may read and mutate this metadata
     * before calling [proceed] so the headers are sent to the server. Headers added after
     * the call has already been proceeded may not be reflected on the wire.
     */
    public val requestHeaders: GrpcMetadata

    /**
     * Transport/engine options used for this call (deadlines, compression, etc.).
     * Modifying this object is only possible before the call is proceeded.
     */
    public val callOptions: GrpcCallOptions

    /**
     * Register a callback invoked when the initial response headers are received.
     * Typical gRPC semantics guarantee headers are delivered at most once per call
     * and before the first message is received.
     */
    public fun onHeaders(block: (responseHeaders: GrpcMetadata) -> Unit)

    /**
     * Register a callback invoked when the call completes, successfully or not.
     * The final `status` and trailing `responseTrailers` are provided.
     */
    public fun onClose(block: (status: Status, responseTrailers: GrpcMetadata) -> Unit)

    /**
     * Cancel the call locally, providing a human-readable [message] and an optional [cause].
     * This method won't return and abort all further processing.
     *
     * We made cancel throw a [kotlinx.rpc.grpc.StatusException] instead of returning, so control flow is explicit and
     * race conditions between interceptors and the transport layer are avoided.
     */
    public fun cancel(message: String, cause: Throwable? = null): Nothing

    /**
     * Continue the invocation by forwarding it to the next interceptor or to the underlying transport.
     *
     * This function is the heart of an interceptor:
     * - It must be called to actually perform the RPC. If you never call [proceed], the request is not sent
     *   and the call is effectively short-circuited by the interceptor.
     * - You may transform the [request] flow before passing it to [proceed] (e.g., logging, retry orchestration,
     *   compression, metrics). The returned [Flow] yields response messages and can also be transformed
     *   before being returned to the caller.
     * - Call [proceed] at most once per intercepted call. Calling it multiple times or after cancellation
     *   is not supported.
     */
    public fun proceed(request: Flow<Request>): Flow<Response>
}

/**
 * Client-side interceptor for gRPC calls.
 *
 * Implementations can observe and modify client calls in a structured way. The primary entry point is the
 * [intercept] extension function on [ClientCallScope], which receives the inbound request [Flow] and must
 * call [ClientCallScope.proceed] to forward the call.
 *
 * Common use-cases include:
 * - Adding authentication or custom headers.
 * - Implementing logging/metrics.
 * - Observing headers/trailers and final status.
 * - Transforming request/response flows (e.g., mapping, buffering, throttling).
 */
public interface ClientInterceptor {
    /**
     * Intercept a client call.
     *
     * You can:
     * - Inspect [ClientCallScope.method] and [ClientCallScope.callOptions].
     * - Read or populate [ClientCallScope.requestHeaders].
     * - Register [ClientCallScope.onHeaders] and [ClientCallScope.onClose] callbacks.
     * - Transform the [request] flow or wrap the resulting response flow.
     *
     * IMPORTANT: [ClientCallScope.proceed] must eventually be called to actually execute the RPC and obtain
     * the response [Flow]. If [ClientCallScope.proceed] is omitted, the call will not reach the server.
     */
    public fun <Request, Response> ClientCallScope<Request, Response>.intercept(
        request: Flow<Request>,
    ): Flow<Response>

}