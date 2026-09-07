/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client.internal

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcStatus
import kotlinx.rpc.grpc.client.GrpcCallCredentials
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.plus
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodType
import kotlinx.rpc.grpc.internal.Ready

internal class NonIosGrpcClientTransport(
    private val channel: ManagedChannel,
    private val callCredentials: GrpcCallCredentials,
) : GrpcClientTransport {
    override fun <Request, Response> execute(
        method: GrpcMethodDescriptor<Request, Response>,
        requests: Flow<Request>,
        headers: GrpcMetadata,
        callOptions: GrpcCallOptions,
    ): Flow<GrpcClientCallEvents<Response>> = flow {
        coroutineScope {
            callOptions.callCredentials += callCredentials
            val call = channel.platformApi.createCall(method, callOptions, coroutineContext)

            // At most one response is requested at a time. Three slots are enough for the optional
            // headers, that response, and the terminal status to arrive without blocking a callback.
            val events = Channel<GrpcClientCallEvents<Response>>(capacity = 3)
            val ready = Ready { call.isReady() }
            val fullMethodName = method.getFullMethodName()

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
                        requests.collect { requestChannel.send(it) }
                        requestChannel.close()
                    } catch (e: Throwable) {
                        requestChannel.close(e)
                        throw e
                    }
                }
            } else {
                null
            }

            call.start(channelResponseListener(events, ready), headers)

            val sender = launch(CoroutineName("grpc-send-message-$fullMethodName")) {
                try {
                    if (requestChannel != null) {
                        for (message in requestChannel) {
                            ready.suspendUntilReady()
                            call.sendMessage(message)
                        }
                    } else {
                        call.sendMessage(requests.single())
                    }
                    call.halfClose()
                } catch (ex: Exception) {
                    call.cancel("Collection of requests completed exceptionally", ex)
                    throw ex
                }
            }

            emitEvents(
                events = events,
                requestNext = { call.request(1) },
                onError = { e ->
                    sender.cancel("Collection of responses completed exceptionally", e)
                    sender.join()
                    requestCollector?.cancel("Collection of responses completed exceptionally", e)
                    requestCollector?.join()
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
        events: Channel<GrpcClientCallEvents<Response>>,
        ready: Ready,
    ): ClientCall.Listener<Response> = clientCallListener(
        onHeaders = { headers ->
            events.trySend(GrpcClientCallEvents.Headers(headers)).onFailure { cause ->
                throw cause ?: AssertionError("Headers should fit into the call event channel")
            }
        },
        onMessage = { message ->
            events.trySend(GrpcClientCallEvents.Message(message)).onFailure { cause ->
                throw cause ?: AssertionError("A requested message should fit into the call event channel")
            }
        },
        onClose = { status: GrpcStatus, trailers: GrpcMetadata ->
            events.trySend(GrpcClientCallEvents.Closed(status, trailers)).onFailure { cause ->
                throw cause ?: AssertionError("The terminal status should fit into the call event channel")
            }
            events.close()
        },
        onReady = ready::onReady,
    )
}

internal suspend fun <Response> FlowCollector<GrpcClientCallEvents<Response>>.emitEvents(
    events: Channel<GrpcClientCallEvents<Response>>,
    requestNext: () -> Unit,
    onError: suspend (Throwable) -> Unit,
) {
    try {
        requestNext()
        for (event in events) {
            emit(event)
            if (event is GrpcClientCallEvents.Message) {
                requestNext()
            }
        }
    } catch (e: Exception) {
        withContext(NonCancellable) { onError(e) }
        currentCoroutineContext().ensureActive()
        throw e
    }
}

internal actual fun GrpcClientTransport(
    channel: ManagedChannel,
    callCredentials: GrpcCallCredentials,
): GrpcClientTransport = NonIosGrpcClientTransport(channel, callCredentials)
