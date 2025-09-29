/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.ServerCallScope
import kotlinx.rpc.grpc.ServerInterceptor
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.StatusRuntimeException
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@InternalRpcApi
public fun <Request, Response> CoroutineScope.unaryServerMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    responseKType: KType,
    interceptors: List<ServerInterceptor>,
    implementation: suspend (request: Request) -> Response,
): ServerMethodDefinition<Request, Response> {
    val type = descriptor.type
    require(type == MethodType.UNARY) {
        "Expected a unary method descriptor but got $descriptor"
    }

    return serverMethodDefinition(descriptor, responseKType, interceptors) { requests ->
        requests
            .singleOrStatusFlow("request", descriptor)
            .map { implementation(it) }
    }
}

@InternalRpcApi
public fun <Request, Response> CoroutineScope.clientStreamingServerMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    responseKType: KType,
    interceptors: List<ServerInterceptor>,
    implementation: suspend (requests: Flow<Request>) -> Response,
): ServerMethodDefinition<Request, Response> {
    val type = descriptor.type
    require(type == MethodType.CLIENT_STREAMING) {
        "Expected a client streaming method descriptor but got $descriptor"
    }

    return serverMethodDefinition(descriptor, responseKType, interceptors) { requests ->
        flow {
            val response = implementation(requests)
            emit(response)
        }
    }
}

@InternalRpcApi
public fun <Request, Response> CoroutineScope.serverStreamingServerMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    responseKType: KType,
    interceptors: List<ServerInterceptor>,
    implementation: (request: Request) -> Flow<Response>,
): ServerMethodDefinition<Request, Response> {
    val type = descriptor.type
    require(type == MethodType.SERVER_STREAMING) {
        "Expected a server streaming method descriptor but got $descriptor"
    }

    return serverMethodDefinition(descriptor, responseKType, interceptors) { requests ->
        flow {
            requests
                .singleOrStatusFlow("request", descriptor)
                .collect { request ->
                    implementation(request).collect { response ->
                        emit(response)
                    }
                }
        }
    }
}

@InternalRpcApi
public fun <Request, Response> CoroutineScope.bidiStreamingServerMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    responseKType: KType,
    interceptors: List<ServerInterceptor>,
    implementation: (requests: Flow<Request>) -> Flow<Response>,
): ServerMethodDefinition<Request, Response> {
    val type = descriptor.type
    check(type == MethodType.BIDI_STREAMING) {
        "Expected a bidi streaming method descriptor but got $descriptor"
    }

    return serverMethodDefinition(descriptor, responseKType, interceptors, implementation)
}

private fun <Request, Response> CoroutineScope.serverMethodDefinition(
    descriptor: MethodDescriptor<Request, Response>,
    responseKType: KType,
    interceptors: List<ServerInterceptor>,
    implementation: (Flow<Request>) -> Flow<Response>,
): ServerMethodDefinition<Request, Response> =
    serverMethodDefinition(descriptor, serverCallHandler(descriptor, responseKType, interceptors, implementation))

private fun <Request, Response> CoroutineScope.serverCallHandler(
    descriptor: MethodDescriptor<Request, Response>,
    responseKType: KType,
    interceptors: List<ServerInterceptor>,
    implementation: (Flow<Request>) -> Flow<Response>,
): ServerCallHandler<Request, Response> =
    ServerCallHandler { call, headers ->
        serverCallListenerImpl(descriptor, call, responseKType, interceptors, implementation, headers)
    }

private fun <Request, Response> CoroutineScope.serverCallListenerImpl(
    descriptor: MethodDescriptor<Request, Response>,
    handler: ServerCall<Request, Response>,
    responseKType: KType,
    interceptors: List<ServerInterceptor>,
    implementation: (Flow<Request>) -> Flow<Response>,
    requestHeaders: GrpcMetadata,
): ServerCall.Listener<Request> {
    val ready = Ready { handler.isReady() }
    val requestsChannel = Channel<Request>(1)

    val requestsStarted = AtomicBoolean(false) // enforces read-once

    val requests = flow {
        check(requestsStarted.value.compareAndSet(expect = false, update = true)) {
            "requests flow can only be collected once"
        }

        try {
            handler.request(1)
            for (request in requestsChannel) {
                emit(request)
                handler.request(1)
            }
        } catch (e: Exception) {
            requestsChannel.cancel(
                CancellationException("Exception thrown while collecting requests", e)
            )
            handler.request(1) // make sure we don't cause backpressure
            throw e
        }
    }

    val context = GrpcContextElement.current()
    val serverCallScope = ServerCallScopeImpl(
        method = descriptor,
        interceptors = interceptors,
        implementation = implementation,
        requestHeaders = requestHeaders,
        serverCall = handler,
        context = context.grpcContext,
    )

    val rpcJob = launch(context) {
        val mutex = Mutex()
        val headersSent = AtomicBoolean(false) // enforces only sending headers once
        val failure = runCatching {
            serverCallScope.proceed(requests).collect { response ->
                @Suppress("UNCHECKED_CAST")
                // fix for KRPC-173
                val value = if (responseKType == unitKType) Unit as Response else response

                // once we have a response message, check if we've sent headers yet - if not, do so
                if (headersSent.value.compareAndSet(expect = false, update = true)) {
                    mutex.withLock {
                        handler.sendHeaders(GrpcMetadata())
                    }
                }
                ready.suspendUntilReady()
                mutex.withLock {
                    handler.sendMessage(value)
                }
            }
        }.exceptionOrNull()
        // check headers again once we're done collecting the response flow - if we received
        // no elements or threw an exception, then we wouldn't have sent them
        if (failure == null && headersSent.value.compareAndSet(expect = false, update = true)) {
            mutex.withLock {
                handler.sendHeaders(GrpcMetadata())
            }
        }

        val closeStatus = when (failure) {
            null -> Status(StatusCode.OK)
            is CancellationException -> Status(StatusCode.CANCELLED, cause = failure)
            is StatusException -> failure.getStatus()
            is StatusRuntimeException -> failure.getStatus()
            else -> Status(StatusCode.UNKNOWN, cause = failure)
        }

        val trailers = failure?.let {
            when (it) {
                is StatusException -> {
                    it.getTrailers()
                }

                is StatusRuntimeException -> {
                    it.getTrailers()
                }

                else -> {
                    null
                }
            }
        } ?: GrpcMetadata()

        mutex.withLock {
            handler.close(closeStatus, trailers)
            serverCallScope.onCloseFuture.complete(Pair(closeStatus, trailers))
        }
    }

    return serverCallListener(
        state = ServerCallListenerState(),
        onCancel = {
            rpcJob.cancel("Cancellation received from client")
        },
        onMessage = { state, message: Request ->
            if (state.isReceiving) {
                val result = requestsChannel.trySend(message)
                state.isReceiving = result.isSuccess
                result.onFailure { ex ->
                    if (ex !is CancellationException) {
                        throw StatusException(
                            Status(StatusCode.INTERNAL, "onMessage should never be called until requests is ready"),
                        )
                    }
                }
            }

            if (!state.isReceiving) {
                handler.request(1) // do not exert backpressure
            }
        },
        onHalfClose = {
            requestsChannel.close()
        },
        onReady = {
            ready.onReady()
        },
        onComplete = { }
    )
}

private class AtomicBoolean(initialValue: Boolean) {
    val value = atomic(initialValue)
}

private class ServerCallListenerState {
    var isReceiving = true
}

private val unitKType = typeOf<Unit>()


private class ServerCallScopeImpl<Request, Response>(
    override val method: MethodDescriptor<Request, Response>,
    val interceptors: List<ServerInterceptor>,
    val implementation: (Flow<Request>) -> Flow<Response>,
    override val requestHeaders: GrpcMetadata,
    val serverCall: ServerCall<Request, Response>,
    override val context: GrpcContext,
) : ServerCallScope<Request, Response> {

    override val responseHeaders: GrpcMetadata = GrpcMetadata()
    override val responseTrailers: GrpcMetadata = GrpcMetadata()

    // keeps track of already processed interceptors
    var interceptorIndex = 0
    val onCloseFuture = CallbackFuture<Pair<Status, GrpcMetadata>>()

    override fun onClose(block: (Status, GrpcMetadata) -> Unit) {
        onCloseFuture.onComplete { block(it.first, it.second) }
    }

    override fun close(status: Status, trailers: GrpcMetadata): Nothing {
        // this will be cached by the rpcImpl() runCatching{} and turns it into a close()
        throw StatusException(status, trailers)
    }

    override fun proceed(request: Flow<Request>): Flow<Response> {
        return if (interceptorIndex < interceptors.size) {
            with(interceptors[interceptorIndex++]) {
                intercept(request)
            }
        } else {
            implementation(request)
        }
    }
}