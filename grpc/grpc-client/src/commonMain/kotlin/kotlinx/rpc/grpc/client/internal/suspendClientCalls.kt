/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.asException
import kotlinx.rpc.grpc.cause
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.copy
import kotlinx.rpc.grpc.copy
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodType
import kotlinx.rpc.grpc.internal.CallbackFuture
import kotlinx.rpc.grpc.internal.singleOrStatus
import kotlinx.rpc.grpc.internal.singleOrStatusFlow
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.internal.utils.InternalRpcApi

// heavily inspired by
// https://github.com/grpc/grpc-kotlin/blob/master/stub/src/main/java/io/grpc/kotlin/ClientCalls.kt

@InternalRpcApi
public suspend fun <Request, Response> GrpcClient.unaryRpc(
    descriptor: GrpcMethodDescriptor<Request, Response>,
    request: Request,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Response {
    val type = descriptor.methodType
    require(type == GrpcMethodType.UNARY) {
        "Expected a unary RPC method, but got $descriptor"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = flowOf(request)
    ).singleOrStatus("request", descriptor)
}

@InternalRpcApi
public fun <Request, Response> GrpcClient.serverStreamingRpc(
    descriptor: GrpcMethodDescriptor<Request, Response>,
    request: Request,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Flow<Response> {
    val type = descriptor.methodType
    require(type == GrpcMethodType.SERVER_STREAMING) {
        "Expected a server streaming RPC method, but got $type"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = flowOf(request)
    )
}

@InternalRpcApi
public suspend fun <Request, Response> GrpcClient.clientStreamingRpc(
    descriptor: GrpcMethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Response {
    val type = descriptor.methodType
    require(type == GrpcMethodType.CLIENT_STREAMING) {
        "Expected a client streaming RPC method, but got $type"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = requests
    ).singleOrStatus("response", descriptor)
}

@InternalRpcApi
public fun <Request, Response> GrpcClient.bidirectionalStreamingRpc(
    descriptor: GrpcMethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Flow<Response> {
    val type = descriptor.methodType
    check(type == GrpcMethodType.BIDI_STREAMING) {
        "Expected a bidirectional streaming method, but got $type"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = requests
    )
}

private fun <Request, Response> GrpcClient.rpcImpl(
    descriptor: GrpcMethodDescriptor<Request, Response>,
    callOptions: GrpcCallOptions,
    headers: GrpcMetadata,
    request: Flow<Request>,
): Flow<Response> = flow {
    // Interceptors and call credentials mutate the headers and options of their call scope,
    // so every collection works on its own copies and never changes the caller's instances.
    val clientCallScope = ClientCallScopeImpl(
        client = this@rpcImpl,
        method = descriptor,
        requestHeaders = headers.copy(),
        callOptions = callOptions.copy(),
    )
    // We must wrap the proceeded flow, because if users try to use
    // retry or retryWhen on a returned flow, it must produce a new call scope,
    // with new intercept invocations. This wouldn't be the case otherwise, as
    // the inner flow (after interceptor invocation and future completion) would be used,
    // causing unexpected behavior.
    emitAll(clientCallScope.proceed(request))
}

private class ClientCallScopeImpl<Request, Response>(
    val client: GrpcClient,
    override val method: GrpcMethodDescriptor<Request, Response>,
    override val requestHeaders: GrpcMetadata,
    override val callOptions: GrpcCallOptions,
) : GrpcClientCallScope<Request, Response> {
    val interceptors = client.interceptors
    val onHeadersFuture = CallbackFuture<GrpcMetadata>()
    val onCloseFuture = CallbackFuture<Pair<GrpcStatus, GrpcMetadata>>()

    var interceptorIndex = 0

    override fun onHeaders(block: (GrpcMetadata) -> Unit) {
        onHeadersFuture.onComplete { block(it) }
    }

    override fun onClose(block: (GrpcStatus, GrpcMetadata) -> Unit) {
        onCloseFuture.onComplete { block(it.first, it.second) }
    }

    override fun cancel(message: String, cause: Throwable?): Nothing {
        throw GrpcStatusException(GrpcStatus(GrpcStatusCode.CANCELLED, message, cause))
    }

    override fun proceed(request: Flow<Request>): Flow<Response> {
        return if (interceptorIndex < interceptors.size) {
            with(interceptors[interceptorIndex++]) {
                intercept(request)
            }
        } else {
            // if the interceptor chain is exhausted, we start the actual call
            doCall(request)
        }
    }

    private fun doCall(request: Flow<Request>): Flow<Response> {
        // Interceptors can replace the request flow, so this layer checks the request count.
        val validatedRequest = when (method.methodType) {
            GrpcMethodType.UNARY,
            GrpcMethodType.SERVER_STREAMING,
            -> request.singleOrStatusFlow("request", method)
            GrpcMethodType.CLIENT_STREAMING,
            GrpcMethodType.BIDI_STREAMING,
            GrpcMethodType.UNKNOWN,
            -> request
        }
        val events = client.backend.execute(method, validatedRequest, requestHeaders, callOptions)

        return flow {
            events.collect { event ->
                when (event) {
                    is GrpcClientCallEvents.Headers -> {
                        try {
                            onHeadersFuture.complete(event.headers)
                        } catch (exception: Exception) {
                            throw exception.toInterceptorFailure()
                        }
                    }

                    is GrpcClientCallEvents.Message -> emit(event.response)

                    is GrpcClientCallEvents.Closed -> {
                        val status = event.status
                        var cause = when {
                            status.statusCode == GrpcStatusCode.OK -> null
                            status.cause is CancellationException -> status.cause
                            else -> status.asException(event.trailers)
                        }

                        // The onClose callbacks run before the call fails with its status.
                        try {
                            onCloseFuture.complete(status to event.trailers)
                        } catch (exception: Exception) {
                            cause = exception.toInterceptorFailure()
                        }

                        if (cause != null) {
                            throw cause
                        }
                    }
                }
            }
        }
    }
}

/**
 * An interceptor that throws a [GrpcStatusException] fails the call with that status.
 * Any other exception fails the call with [GrpcStatusCode.CANCELLED].
 */
private fun Exception.toInterceptorFailure(): GrpcStatusException =
    this as? GrpcStatusException
        ?: GrpcStatus(GrpcStatusCode.CANCELLED, "Interceptor threw an error", this).asException()
