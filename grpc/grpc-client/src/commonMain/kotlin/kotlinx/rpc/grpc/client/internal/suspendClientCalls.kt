/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.client.ClientCallScope
import kotlinx.rpc.grpc.client.EmptyCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.grpc.descriptor.MethodType
import kotlinx.rpc.grpc.descriptor.methodType
import kotlinx.rpc.grpc.internal.CallbackFuture
import kotlinx.rpc.grpc.internal.Ready
import kotlinx.rpc.grpc.internal.singleOrStatus
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.internal.utils.InternalRpcApi

// heavily inspired by
// https://github.com/grpc/grpc-kotlin/blob/master/stub/src/main/java/io/grpc/kotlin/ClientCalls.kt

@InternalRpcApi
public suspend fun <Request, Response> GrpcClient.unaryRpc(
    descriptor: MethodDescriptor<Request, Response>,
    request: Request,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Response {
    val type = descriptor.methodType
    require(type == MethodType.UNARY) {
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
    descriptor: MethodDescriptor<Request, Response>,
    request: Request,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Flow<Response> {
    val type = descriptor.methodType
    require(type == MethodType.SERVER_STREAMING) {
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
    descriptor: MethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Response {
    val type = descriptor.methodType
    require(type == MethodType.CLIENT_STREAMING) {
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
    descriptor: MethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcCallOptions(),
    headers: GrpcMetadata = GrpcMetadata(),
): Flow<Response> {
    val type = descriptor.methodType
    check(type == MethodType.BIDI_STREAMING) {
        "Expected a bidirectional streaming method, but got $type"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = requests
    )
}

private sealed interface ClientRequest<Request> {
    suspend fun sendTo(
        clientCall: ClientCall<Request, *>,
        ready: Ready,
    )

    class Unary<Request>(private val request: Request) : ClientRequest<Request> {
        override suspend fun sendTo(
            clientCall: ClientCall<Request, *>,
            ready: Ready,
        ) {
            clientCall.sendMessage(request)
        }
    }

    class Flowing<Request>(private val requestFlow: Flow<Request>) : ClientRequest<Request> {
        override suspend fun sendTo(
            clientCall: ClientCall<Request, *>,
            ready: Ready,
        ) {
            ready.suspendUntilReady()
            requestFlow.collect { request ->
                clientCall.sendMessage(request)
                ready.suspendUntilReady()
            }
        }
    }
}

private fun <Request, Response> GrpcClient.rpcImpl(
    descriptor: MethodDescriptor<Request, Response>,
    callOptions: GrpcCallOptions,
    headers: GrpcMetadata,
    request: Flow<Request>,
): Flow<Response> {
    val clientCallScope = ClientCallScopeImpl(
        client = this,
        method = descriptor,
        requestHeaders = headers,
        callOptions = callOptions,
    )
    return clientCallScope.proceed(request)
}

private class ClientCallScopeImpl<Request, Response>(
    val client: GrpcClient,
    override val method: MethodDescriptor<Request, Response>,
    override val requestHeaders: GrpcMetadata,
    override val callOptions: GrpcCallOptions,
) : ClientCallScope<Request, Response> {
    val interceptors = client.interceptors
    val onHeadersFuture = CallbackFuture<GrpcMetadata>()
    val onCloseFuture = CallbackFuture<Pair<Status, GrpcMetadata>>()

    var interceptorIndex = 0

    override fun onHeaders(block: (GrpcMetadata) -> Unit) {
        onHeadersFuture.onComplete { block(it) }
    }

    override fun onClose(block: (Status, GrpcMetadata) -> Unit) {
        onCloseFuture.onComplete { block(it.first, it.second) }
    }

    override fun cancel(message: String, cause: Throwable?): Nothing {
        throw StatusException(Status(StatusCode.CANCELLED, message, cause))
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

    private fun doCall(request: Flow<Request>): Flow<Response> = flow {
        coroutineScope {

            val call = client.channel.platformApi.createCall(method, callOptions)

            /*
             * We maintain a buffer of size 1, so onMessage never has to block: it only gets called after
             * we request a response from the server, which only happens when responses is empty and
             * there is room in the buffer.
             */
            val responses = Channel<Response>(1)
            val ready = Ready { call.isReady() }

            call.start(channelResponseListener(call, responses, ready), requestHeaders)

            suspend fun Flow<Request>.send() {
                if (method.methodType == MethodType.UNARY || method.methodType == MethodType.SERVER_STREAMING) {
                    call.sendMessage(single())
                } else {
                    ready.suspendUntilReady()
                    this.collect { request ->
                        call.sendMessage(request)
                        ready.suspendUntilReady()
                    }
                }
            }

            val fullMethodName = method.getFullMethodName()
            val sender = launch(CoroutineName("grpc-send-message-$fullMethodName")) {
                try {
                    request.send()
                    call.halfClose()
                } catch (ex: Exception) {
                    call.cancel("Collection of requests completed exceptionally", ex)
                    throw ex // propagate failure upward
                }
            }

            try {
                call.request(1)
                for (response in responses) {
                    emit(response)
                    call.request(1)
                }
            } catch (e: Exception) {
                withContext(NonCancellable) {
                    sender.cancel("Collection of responses completed exceptionally", e)
                    sender.join()
                    // we want the sender to be done cancelling before we cancel the handler, or it might try
                    // sending to a dead call, which results in ugly exception messages
                    call.cancel("Collection of responses completed exceptionally", e)
                }
                throw e
            }

            if (!sender.isCompleted) {
                sender.cancel("Collection of responses completed before collection of requests")
            }
        }
    }

    private fun <Response> channelResponseListener(
        call: ClientCall<*, Response>,
        responses: Channel<Response>,
        ready: Ready,
    ) = clientCallListener(
        onHeaders = {
            try {
                onHeadersFuture.complete(it)
            } catch (e: StatusException) {
                // if a client interceptor called cancel, we throw a StatusException.
                // as the JVM implementation treats them differently, we need to catch them here.
                call.cancel(e.message, e.cause)
            }
        },
        onMessage = { message: Response ->
            responses.trySend(message).onFailure { e ->
                throw e ?: AssertionError("onMessage should never be called until responses is ready")
            }
        },
        onClose = { status: Status, trailers: GrpcMetadata ->
            var cause = when {
                status.statusCode == StatusCode.OK -> null
                status.getCause() is CancellationException -> status.getCause()
                else -> StatusException(status, trailers)
            }

            try {
                onCloseFuture.complete(status to trailers)
            } catch (exception: Throwable) {
                cause = exception
                if (exception !is StatusException) {
                    val status = Status(StatusCode.CANCELLED, "Interceptor threw an error", exception)
                    cause = StatusException(status)
                }
            }

            responses.close(cause = cause)
        },
        onReady = {
            ready.onReady()
        },
    )
}
