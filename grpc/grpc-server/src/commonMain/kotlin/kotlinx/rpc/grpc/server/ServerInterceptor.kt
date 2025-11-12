/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.server.internal.GrpcContext
import kotlinx.rpc.grpc.descriptor.MethodDescriptor

/**
 * Th scope of a single incoming gRPC server call observed by a [ServerInterceptor].
 *
 * An interceptor receives this scope instance for every RPC invocation arriving to the server and can:
 * - Inspect the target RPC [method].
 * - Read client-provided [requestHeaders].
 * - Populate [responseHeaders] (sent before the first response message) and [responseTrailers]
 *   (sent when the call completes).
 * - Register a completion callback with [onClose].
 * - Abort the call early with [close].
 * - Continue handling by calling [proceed] with the inbound request [Flow] and optionally transform
 *   the returned response [Flow].
 *
 * @param Request the request message type of the RPC.
 * @param Response the response message type of the RPC.
 */
public interface ServerCallScope<Request, Response> {
    /** Descriptor of the RPC method (name, marshalling, type) being executed. */
    public val method: MethodDescriptor<Request, Response>

    /** Metadata received from the client with the initial request headers. Read-only from the server perspective. */
    public val requestHeaders: GrpcMetadata

    /**
     * Initial response headers to be sent to the client.
     * Interceptors and handlers may add entries before the first response element is emitted
     * (i.e., before proceeding or before producing output), otherwise headers might have already been sent.
     */
    public val responseHeaders: GrpcMetadata

    /**
     * Trailing metadata to be sent with the final status when the call completes.
     * Interceptors can add diagnostics or custom metadata here.
     */
    public val responseTrailers: GrpcMetadata

    /**
     * The [GrpcContext] associated with this call.
     *
     * It can be used by the interceptor to provide call-scoped information about
     * the current call, such as the identity of the caller or the current authentication state.
     */
    public val context: GrpcContext

    /**
     * Register a callback invoked when the call is closed (successfully or exceptionally).
     * Provides the final [kotlinx.rpc.grpc.Status] and the sent [GrpcMetadata] trailers.
     *
     * IMPORTANT: The callback must not throw an exception or use [close].
     * Behavior is undefined and may lead to crashes depending on the platform.
     */
    public fun onClose(block: (Status, GrpcMetadata) -> Unit)

    /**
     * Immediately terminate the call with the given [status] and optional [trailers].
     *
     * This method does not return (declared as [Nothing]). After calling it, no further messages will be processed
     * or sent. Prefer setting [responseHeaders]/[responseTrailers] before closing if you need to include metadata.
     *
     * We made close throw a [kotlinx.rpc.grpc.StatusException] instead of returning, so control flow is explicit and race conditions
     * between interceptors and the service implementation are avoided.
     */
    public fun close(status: Status, trailers: GrpcMetadata = GrpcMetadata()): Nothing

    /**
     * Continue processing by forwarding the request to the next interceptor or the actual service implementation.
     *
     * IMPORTANT:
     * - You must call [proceed] exactly once to actually handle the RPC; otherwise, the call will be short-circuited
     *   and the service method will not be invoked.
     * - You may transform the incoming [request] flow (e.g., validation, logging, metering) before passing it to
     *   [proceed]. You may also transform the resulting response [Flow] before returning it to the framework.
     * - The interceptor must ensure to provide and return a valid number of messages, depending on the method type.
     * - The interceptor must not throw an exception. Use [close] to terminate the call with an error.
     */
    public fun proceed(request: Flow<Request>): Flow<Response>

    /**
     * Convenience for flow builders: proceeds with [request] and emits the resulting response elements into this
     * [FlowCollector]. Useful inside `flow {}` blocks within interceptors.
     *
     * ```
     * val myAuthInterceptor = object : ServerInterceptor {
     *     override fun <Request, Response> ServerCallScope<Request, Response>.intercept(request: Flow<Request>): Flow<Response> =
     *         flow {
     *             val authorized = mySuspendAuth(requestHeaders)
     *             if (!authorized) {
     *                 close(Status(StatusCode.PERMISSION_DENIED, "Not authorized"))
     *             }
     *
     *             proceedUnmodified(request)
     *         }
     *      }
     * ```
     */
    public suspend fun FlowCollector<Response>.proceedUnmodified(request: Flow<Request>) {
        proceed(request).collect {
            emit(it)
        }
    }
}

/**
 * Server-side interceptor for gRPC calls.
 *
 * Implementations can observe and modify server handling in a structured way. The entry point is the
 * [intercept] extension function on [ServerCallScope], which receives the inbound request [Flow] and must
 * call [ServerCallScope.proceed] to forward the call to the next interceptor or the target service method.
 *
 * Common use-cases include:
 * - Authentication/authorization checks and context propagation.
 * - Setting response headers and trailers.
 * - Structured logging and metrics.
 * - Transforming request/response flows (e.g., validation, mapping, throttling).
 *
 * See ServerInterceptorTest for practical usage patterns.
 */
public interface ServerInterceptor {
    /**
     * Intercept a server call.
     *
     * You can:
     * - Inspect [ServerCallScope.method].
     * - Read [ServerCallScope.requestHeaders] and populate [ServerCallScope.responseHeaders]/[ServerCallScope.responseTrailers].
     * - Register [ServerCallScope.onClose] callbacks.
     * - Transform the [request] flow or wrap the resulting response flow.
     * - Append information to the [ServerCallScope.context].
     *
     * IMPORTANT: You must eventually call [ServerCallScope.proceed] to actually invoke the service logic and produce
     * the response [Flow]. If [ServerCallScope.proceed] is omitted, the call will never reach the service.
     */
    public fun <Request, Response> ServerCallScope<Request, Response>.intercept(
        request: Flow<Request>,
    ): Flow<Response>
}