/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.rpc.grpc.*
import kotlinx.rpc.internal.utils.InternalRpcApi

// heavily inspired by
// https://github.com/grpc/grpc-kotlin/blob/master/stub/src/main/java/io/grpc/kotlin/ClientCalls.kt

@InternalRpcApi
public suspend fun <Request, Response> GrpcClient.unaryRpc(
    descriptor: MethodDescriptor<Request, Response>,
    request: Request,
    callOptions: GrpcCallOptions = GrpcDefaultCallOptions,
    trailers: GrpcTrailers = GrpcTrailers(),
): Response {
    val type = descriptor.type
    require(type == MethodType.UNARY) {
        "Expected a unary RPC method, but got $descriptor"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        trailers = trailers,
        request = flowOf(request)
    ).singleOrStatus("request", descriptor)
}

@InternalRpcApi
public fun <Request, Response> GrpcClient.serverStreamingRpc(
    descriptor: MethodDescriptor<Request, Response>,
    request: Request,
    callOptions: GrpcCallOptions = GrpcDefaultCallOptions,
    trailers: GrpcTrailers = GrpcTrailers(),
): Flow<Response> {
    val type = descriptor.type
    require(type == MethodType.SERVER_STREAMING) {
        "Expected a server streaming RPC method, but got $type"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        trailers = trailers,
        request = flowOf(request)
    )
}

@InternalRpcApi
public suspend fun <Request, Response> GrpcClient.clientStreamingRpc(
    descriptor: MethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcDefaultCallOptions,
    trailers: GrpcTrailers = GrpcTrailers(),
): Response {
    val type = descriptor.type
    require(type == MethodType.CLIENT_STREAMING) {
        "Expected a client streaming RPC method, but got $type"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        trailers = trailers,
        request = requests
    ).singleOrStatus("response", descriptor)
}

@InternalRpcApi
public fun <Request, Response> GrpcClient.bidirectionalStreamingRpc(
    descriptor: MethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcDefaultCallOptions,
    trailers: GrpcTrailers = GrpcTrailers(),
): Flow<Response> {
    val type = descriptor.type
    check(type == MethodType.BIDI_STREAMING) {
        "Expected a bidirectional streaming method, but got $type"
    }

    return rpcImpl(
        descriptor = descriptor,
        callOptions = callOptions,
        trailers = trailers,
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
    trailers: GrpcTrailers,
    request: Flow<Request>,
): Flow<Response> {
    val clientCallScope = ClientCallScopeImpl(
        client = this,
        method = descriptor,
        metadata = trailers,
        callOptions = callOptions,
    )
    return clientCallScope.proceed(request)
}

// todo really needed?
internal fun <T> Flow<T>.singleOrStatusFlow(
    expected: String,
    descriptor: Any,
): Flow<T> = flow {
    var found = false
    collect {
        if (!found) {
            found = true
            emit(it)
        } else {
            throw StatusException(
                Status(StatusCode.INTERNAL, "Expected one $expected for $descriptor but received two")
            )
        }
    }

    if (!found) {
        throw StatusException(
            Status(StatusCode.INTERNAL, "Expected one $expected for $descriptor but received none")
        )
    }
}

internal suspend fun <T> Flow<T>.singleOrStatus(
    expected: String,
    descriptor: Any,
): T = singleOrStatusFlow(expected, descriptor).single()

internal class Ready(private val isReallyReady: () -> Boolean) {
    // A CONFLATED channel never suspends to send, and two notifications of readiness are equivalent
    // to one
    private val channel = Channel<Unit>(Channel.CONFLATED)

    fun onReady() {
        channel.trySend(Unit).onFailure { e ->
            throw e ?: AssertionError(
                "Should be impossible; a CONFLATED channel should never return false on offer"
            )
        }
    }

    suspend fun suspendUntilReady() {
        while (!isReallyReady()) {
            channel.receive()
        }
    }
}

private class ClientCallScopeImpl<Request, Response>(
    val client: GrpcClient,
    override val method: MethodDescriptor<Request, Response>,
    override val metadata: GrpcTrailers,
    override val callOptions: GrpcCallOptions,
) : ClientCallScope<Request, Response> {

    val call = client.channel.platformApi.newCall(method, callOptions)
    val interceptors = client.interceptors
    val onHeadersFuture = CallbackFuture<GrpcTrailers>()
    val onCloseFuture = CallbackFuture<Pair<Status, GrpcTrailers>>()

    var interceptorIndex = 0

    override fun onHeaders(block: (GrpcTrailers) -> Unit) {
        onHeadersFuture.onComplete { block(it) }
    }

    override fun onClose(block: (Status, GrpcTrailers) -> Unit) {
        onCloseFuture.onComplete { block(it.first, it.second) }
    }

    override fun cancel(message: String, cause: Throwable?) {
        call.cancel(message, cause)
    }

    override fun proceed(request: Flow<Request>): Flow<Response> {
        return if (interceptorIndex < interceptors.size) {
            interceptors[interceptorIndex++]
                .intercept(this, request)
        } else {
            // if the interceptor chain is exhausted, we start the actual call
            doCall(request)
        }
    }

    private fun doCall(request: Flow<Request>): Flow<Response> = flow {
        coroutineScope {

            /*
             * We maintain a buffer of size 1 so onMessage never has to block: it only gets called after
             * we request a response from the server, which only happens when responses is empty and
             * there is room in the buffer.
             */
            val responses = Channel<Response>(1)
            val ready = Ready { call.isReady() }

            call.start(channelResponseListener(responses, ready), metadata)

            suspend fun Flow<Request>.send() {
                if (method.type == MethodType.UNARY || method.type == MethodType.SERVER_STREAMING) {
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
        responses: Channel<Response>,
        ready: Ready,
    ) = clientCallListener(
        onHeaders = { onHeadersFuture.complete(it) },
        onMessage = { message: Response ->
            responses.trySend(message).onFailure { e ->
                throw e ?: AssertionError("onMessage should never be called until responses is ready")
            }
        },
        onClose = { status: Status, trailers: GrpcTrailers ->
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
