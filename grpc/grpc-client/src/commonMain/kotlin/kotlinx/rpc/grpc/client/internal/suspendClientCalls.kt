/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.cause
import kotlinx.rpc.grpc.client.GrpcClientCallScope
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.plus
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
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

/**
 * Pumps responses from the [responses] channel into this [FlowCollector], requesting the next
 * message through [requestNext] after every emit and running [onError] cleanup (under
 * [NonCancellable]) if the collection fails.
 *
 * If the surrounding coroutine has been cancelled, this completes with that cancellation instead of
 * rethrowing the channel's close-cause. The for-loop reads an already-closed channel through a
 * non-suspending fast path that does not observe coroutine cancellation, so without this guard a
 * server status sitting in the closed [responses] channel would leak past cancellation to operators
 * such as `retry()`/`catch()` instead of the expected [CancellationException]
 * (KRPC-461, grpc-kotlin #318).
 */
internal suspend fun <Response> FlowCollector<Response>.emitResponses(
    responses: ReceiveChannel<Response>,
    requestNext: () -> Unit,
    onError: suspend (Throwable) -> Unit,
) {
    try {
        requestNext()
        for (response in responses) {
            emit(response)
            requestNext()
        }
    } catch (e: Exception) {
        withContext(NonCancellable) { onError(e) }
        currentCoroutineContext().ensureActive()
        throw e
    }
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

    private fun doCall(request: Flow<Request>): Flow<Response> = flow {
        coroutineScope {
            // attach all call credentials set in the client to the call option ones.
            callOptions.callCredentials += client.callCredentials
            // pass this scope's context, so the suspend functions of call credentials launch in the same context.
            // it will ensure that getRequestMetadata of the callCredential won't orphan if the call is cancelled
            // by the user or by grpc-java
            val call = client.channel.platformApi.createCall(method, callOptions, this.coroutineContext)

            /*
             * We maintain a buffer of size 1, so onMessage never has to block: it only gets called after
             * we request a response from the server, which only happens when responses is empty and
             * there is room in the buffer.
             */
            val responses = Channel<Response>(1)
            val ready = Ready { call.isReady() }
            val fullMethodName = method.getFullMethodName()

            // (KRPC-461 / grpc-kotlin #378) For client/bidi streaming, subscribe to the request flow
            // eagerly — before the RPC is started and independently of readiness — so that the flow's
            // initialization runs deterministically and cannot be skipped or interrupted by an early
            // RPC failure. The collected messages are handed to the sender through a rendezvous
            // channel, which keeps the original backpressure (the flow is pulled only as fast as
            // messages are actually sent). Unary and server-streaming send a single, already
            // materialized request, so there is nothing to subscribe eagerly.
            val requestChannel: Channel<Request>? = when (method.methodType) {
                GrpcMethodType.CLIENT_STREAMING, GrpcMethodType.BIDI_STREAMING -> Channel(Channel.RENDEZVOUS)
                else -> null
            }

            val requestCollector: Job? = if (requestChannel != null) {
                launch(
                    context = CoroutineName("grpc-collect-requests-$fullMethodName"),
                    start = CoroutineStart.UNDISPATCHED,
                ) {
                    try {
                        request.collect { requestChannel.send(it) }
                        requestChannel.close()
                    } catch (e: Throwable) {
                        requestChannel.close(e)
                        throw e
                    }
                }
            } else {
                null
            }

            call.start(channelResponseListener(call, responses, ready), requestHeaders)

            val sender = launch(CoroutineName("grpc-send-message-$fullMethodName")) {
                try {
                    if (requestChannel != null) {
                        for (message in requestChannel) {
                            ready.suspendUntilReady()
                            call.sendMessage(message)
                        }
                    } else {
                        call.sendMessage(request.single())
                    }
                    call.halfClose()
                } catch (ex: Exception) {
                    call.cancel("Collection of requests completed exceptionally", ex)
                    throw ex // propagate failure upward
                }
            }

            emitResponses(
                responses = responses,
                requestNext = { call.request(1) },
                onError = { e ->
                    sender.cancel("Collection of responses completed exceptionally", e)
                    sender.join()
                    requestCollector?.cancel("Collection of responses completed exceptionally", e)
                    requestCollector?.join()
                    // we want the sender to be done cancelling before we cancel the handler, or it might try
                    // sending to a dead call, which results in ugly exception messages
                    call.cancel("Collection of responses completed exceptionally", e)
                },
            )

            if (!sender.isCompleted) {
                sender.cancel("Collection of responses completed before collection of requests")
            }
            if (requestCollector?.isCompleted == false) {
                requestCollector.cancel("Collection of responses completed before collection of requests")
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
            } catch (e: GrpcStatusException) {
                // if a client interceptor called cancel, we throw a GrpcStatusException.
                // as the JVM implementation treats them differently, we need to catch them here.
                call.cancel(e.message, e.cause)
            }
        },
        onMessage = { message: Response ->
            responses.trySend(message).onFailure { e ->
                throw e ?: AssertionError("onMessage should never be called until responses is ready")
            }
        },
        onClose = { status: GrpcStatus, trailers: GrpcMetadata ->
            var cause = when {
                status.statusCode == GrpcStatusCode.OK -> null
                status.cause is CancellationException -> status.cause
                else -> GrpcStatusException(status, trailers)
            }

            try {
                onCloseFuture.complete(status to trailers)
            } catch (exception: Throwable) {
                cause = exception
                if (exception !is GrpcStatusException) {
                    val status = GrpcStatus(GrpcStatusCode.CANCELLED, "Interceptor threw an error", exception)
                    cause = GrpcStatusException(status)
                }
            }

            responses.close(cause = cause)
        },
        onReady = {
            ready.onReady()
        },
    )
}
