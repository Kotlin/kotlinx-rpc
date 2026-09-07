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
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodType
import kotlinx.rpc.grpc.internal.CallbackFuture
import kotlinx.rpc.grpc.internal.singleOrStatus
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
    val clientCallScope = ClientCallScopeImpl(
        client = this@rpcImpl,
        method = descriptor,
        requestHeaders = headers,
        callOptions = callOptions,
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
        val events = client.transport.execute(
            method,
            request,
            requestHeaders,
            callOptions,
        )

        return flow {
            events.collect { event ->
                when (event) {
                    // Messages are emitted to the collector directly.
                    is GrpcClientCallEvents.Message -> emit(event.response)

                    is GrpcClientCallEvents.Headers -> {
                        try {
                            onHeadersFuture.complete(event.headers)
                        } catch (exception: Throwable) {
                            // Turn all exceptions into Grpc exceptions.
                            // They are handled by the underlying flow emitter.
                            throw when (exception) {
                                is GrpcStatusException -> exception
                                else -> GrpcStatus(
                                    GrpcStatusCode.CANCELLED,
                                    "Interceptor threw an error",
                                    exception
                                ).asException()
                            }
                        }
                    }

                    is GrpcClientCallEvents.Closed -> {
                        val status = event.status
                        var cause = when {
                            status.statusCode == GrpcStatusCode.OK -> null
                            status.cause is CancellationException -> status.cause
                            else -> status.asException(event.trailers)
                        }

                        try {
                            // In any case, call onClose callbacks first then propagate potential exception.
                            onCloseFuture.complete(event.status to event.trailers)
                        } catch (exception: Throwable) {
                            cause = exception

                            if (exception !is GrpcStatusException) {
                                val status = GrpcStatus(
                                    GrpcStatusCode.CANCELLED,
                                    "Interceptor threw an error",
                                    exception,
                                )
                                cause = GrpcStatusException(status)
                            }
                        }

                        if (cause != null) throw cause
                    }
                }
            }
        }
    }
}
