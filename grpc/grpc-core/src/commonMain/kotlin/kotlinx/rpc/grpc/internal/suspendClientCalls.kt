/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.GrpcTrailers
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.code
import kotlinx.rpc.internal.utils.InternalRpcApi

// heavily inspired by
// https://github.com/grpc/grpc-kotlin/blob/master/stub/src/main/java/io/grpc/kotlin/ClientCalls.kt

@InternalRpcApi
public suspend fun <Request, Response> unaryRpc(
    channel: GrpcChannel,
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
        channel = channel,
        descriptor = descriptor,
        callOptions = callOptions,
        headers = trailers,
        request = ClientRequest.Unary(request)
    ).singleOrStatus("request", descriptor)
}

@InternalRpcApi
public fun <Request, Response> serverStreamingRpc(
    channel: GrpcChannel,
    descriptor: MethodDescriptor<Request, Response>,
    request: Request,
    callOptions: GrpcCallOptions = GrpcDefaultCallOptions,
    headers: GrpcTrailers = GrpcTrailers(),
): Flow<Response> {
    val type = descriptor.type
    require(type == MethodType.SERVER_STREAMING) {
        "Expected a server streaming RPC method, but got $type"
    }

    return rpcImpl(
        channel = channel,
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = ClientRequest.Unary(request)
    )
}

@InternalRpcApi
public suspend fun <Request, Response> clientStreamingRpc(
    channel: GrpcChannel,
    descriptor: MethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcDefaultCallOptions,
    headers: GrpcTrailers = GrpcTrailers(),
): Response {
    val type = descriptor.type
    require(type == MethodType.CLIENT_STREAMING) {
        "Expected a client streaming RPC method, but got $type"
    }

    return rpcImpl(
        channel = channel,
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = ClientRequest.Flowing(requests)
    ).singleOrStatus("response", descriptor)
}

@InternalRpcApi
public fun <Request, Response> bidirectionalStreamingRpc(
    channel: GrpcChannel,
    descriptor: MethodDescriptor<Request, Response>,
    requests: Flow<Request>,
    callOptions: GrpcCallOptions = GrpcDefaultCallOptions,
    headers: GrpcTrailers = GrpcTrailers(),
): Flow<Response> {
    val type = descriptor.type
    check(type == MethodType.BIDI_STREAMING) {
        "Expected a bidirectional streaming method, but got $type"
    }

    return rpcImpl(
        channel = channel,
        descriptor = descriptor,
        callOptions = callOptions,
        headers = headers,
        request = ClientRequest.Flowing(requests)
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

private fun <Request, Response> rpcImpl(
    channel: GrpcChannel,
    descriptor: MethodDescriptor<Request, Response>,
    callOptions: GrpcCallOptions,
    headers: GrpcTrailers,
    request: ClientRequest<Request>,
): Flow<Response> = flow {
    coroutineScope {
        val handler = channel.newCall(descriptor, callOptions)

        /*
         * We maintain a buffer of size 1 so onMessage never has to block: it only gets called after
         * we request a response from the server, which only happens when responses is empty and
         * there is room in the buffer.
         */
        val responses = Channel<Response>(1)
        val ready = Ready()

        handler.start(channelResponseListener(responses, ready), headers)

        val fullMethodName = descriptor.getFullMethodName()
        val sender = launch(CoroutineName("grpc-send-message-$fullMethodName")) {
            try {
                request.sendTo(handler, ready)
                handler.halfClose()
            } catch (ex: Exception) {
                handler.cancel("Collection of requests completed exceptionally", ex)
                throw ex // propagate failure upward
            }
        }

        try {
            handler.request(1)
            for (response in responses) {
                emit(response)
                handler.request(1)
            }
        } catch (e: Exception) {
            withContext(NonCancellable) {
                sender.cancel("Collection of responses completed exceptionally", e)
                sender.join()
                // we want the sender to be done cancelling before we cancel the handler, or it might try
                // sending to a dead call, which results in ugly exception messages
                handler.cancel("Collection of responses completed exceptionally", e)
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
) = clientCallListener<Response>(
    onHeaders = {
        // todo check what happens here
    },
    onMessage = { message: Response ->
        responses.trySend(message).onFailure { e ->
            throw e ?: AssertionError("onMessage should never be called until responses is ready")
        }
    },
    onClose = { status: Status, trailers: GrpcTrailers ->
        val cause = when {
            status.code == StatusCode.OK -> null
            status.getCause() is CancellationException -> status.getCause()
            else -> StatusException(status, trailers)
        }

        responses.close(cause = cause)
    },
    onReady = {
        ready.onReady()
    },
)

// todo really needed?
internal fun <T> Flow<T>.singleOrStatusFlow(
    expected: String,
    descriptor: Any
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
    descriptor: Any
): T = singleOrStatusFlow(expected, descriptor).single()

internal class Ready {
    // A CONFLATED channel never suspends to send, and two notifications of readiness are equivalent
    // to one
    private val channel = Channel<Unit>(Channel.CONFLATED)

    fun onReady() {
        channel.trySend(Unit)
    }

    suspend fun suspendUntilReady() {
        channel.receive()
    }
}
