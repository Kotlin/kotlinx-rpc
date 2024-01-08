/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.internal.map.ConcurrentHashMap
import org.jetbrains.krpc.internal.transport.RPCMessage
import kotlin.coroutines.CoroutineContext

@InternalKRPCApi
public class LazyRPCStreamContext(private val initializer: () -> RPCStreamContext) {
    private val deferred = CompletableDeferred<RPCStreamContext>()
    private val lazyValue by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        initializer().also { deferred.complete(it) }
    }

    public suspend fun awaitInitialized(): RPCStreamContext = deferred.await()

    public val valueOrNull: RPCStreamContext? get() = if (deferred.isCompleted) lazyValue else null

    public fun initialize(): RPCStreamContext = lazyValue
}

@InternalKRPCApi
public class RPCStreamContext(private val callId: String, private val config: RPCConfig) {
    private companion object {
        private const val STREAM_ID_PREFIX = "stream:"
    }

    private val streamIdCounter = atomic(0L)

    private var incomingStreamsInitialized: Boolean = false
    private val incomingStreams by lazy {
        incomingStreamsInitialized = true
        ConcurrentHashMap<String, CompletableDeferred<RPCStreamCall>>()
    }

    private var incomingChannelsInitialized: Boolean = false
    private val incomingChannels by lazy {
        incomingChannelsInitialized = true
        ConcurrentHashMap<String, CompletableDeferred<Channel<Any?>>>()
    }

    private var outgoingStreamsInitialized: Boolean = false
    internal val outgoingStreams: Channel<RPCStreamCall> by lazy {
        outgoingStreamsInitialized = true
        Channel(capacity = Channel.UNLIMITED)
    }

    private var incomingHotFlowsInitialized: Boolean = false
    internal val incomingHotFlows: Channel<FlowCollector<Any?>> by lazy {
        incomingHotFlowsInitialized = true
        Channel(Channel.UNLIMITED)
    }

    internal fun <StreamT : Any> registerOutgoingStream(
        stream: StreamT,
        streamKind: StreamKind,
        elementSerializer: KSerializer<Any?>,
    ): String {
        val id = "$STREAM_ID_PREFIX${streamIdCounter.getAndIncrement()}"
        outgoingStreams.trySend(RPCStreamCall(callId, id, stream, streamKind, elementSerializer))
        return id
    }

    internal fun <StreamT : Any> prepareIncomingStream(
        streamId: String,
        streamKind: StreamKind,
        stateFlowInitialValue: Any?,
        elementSerializer: KSerializer<Any?>,
    ): StreamT {
        val incoming: Channel<Any?> = Channel(Channel.UNLIMITED)
        incomingChannels[streamId] = incoming

        val stream = streamOf<StreamT>(streamKind, stateFlowInitialValue, incoming)
        incomingStreams[streamId] = RPCStreamCall(callId, streamId, stream, streamKind, elementSerializer)
        return stream
    }

    @Suppress("UNCHECKED_CAST")
    private fun <StreamT : Any> streamOf(
        streamKind: StreamKind,
        stateFlowInitialValue: Any?,
        incoming: Channel<Any?>,
    ): StreamT {
        suspend fun consumeFlow(collector: FlowCollector<Any?>, onError: (Throwable) -> Unit) {
            for (message in incoming) {
                when (message) {
                    is StreamCancel -> onError(message.cause.cause.deserialize())
                    is StreamEnd -> return
                    else -> collector.emit(message)
                }
            }
        }

        return when (streamKind) {
            StreamKind.Flow -> {
                flow {
                    consumeFlow(this) { e -> throw e }
                }
            }

            StreamKind.SharedFlow -> {
                val sharedFlow: MutableSharedFlow<Any?> = config.sharedFlowBuilder()

                object : RPCIncomingHotFlow(sharedFlow, ::consumeFlow), MutableSharedFlow<Any?> by sharedFlow {
                    override suspend fun collect(collector: FlowCollector<Any?>): Nothing {
                        super.collect(collector)
                    }

                    override suspend fun emit(value: Any?) {
                        super.emit(value)
                    }
                }.also { incomingHotFlows.trySend(it) }
            }

            StreamKind.StateFlow -> {
                val stateFlow = MutableStateFlow(stateFlowInitialValue)

                object : RPCIncomingHotFlow(stateFlow, ::consumeFlow), MutableStateFlow<Any?> by stateFlow {
                    override suspend fun collect(collector: FlowCollector<Any?>): Nothing {
                        super.collect(collector)
                    }

                    override suspend fun emit(value: Any?) {
                        super.emit(value)
                    }
                }.also { incomingHotFlows.trySend(it) }
            }
        } as StreamT
    }

    public suspend fun closeStream(message: RPCMessage.StreamFinished) {
        incomingChannelOf(message.streamId).send(StreamEnd)
    }

    public suspend fun cancelStream(message: RPCMessage.StreamCancel) {
        incomingChannelOf(message.streamId).send(StreamCancel(message))
    }

    public suspend fun send(message: RPCMessage.StreamMessage, serialFormat: SerialFormat) {
        val info = incomingStreams.getDeferred(message.streamId).await()
        val result = decodeMessageData(serialFormat, info.elementSerializer, message)
        incomingChannelOf(message.streamId).send(result)
    }

    private suspend fun incomingChannelOf(streamId: String): Channel<Any?> {
        return incomingChannels.getDeferred(streamId).await()
    }

    public fun close() {
        if (incomingChannelsInitialized) {
            for (channel in incomingChannels.values) {
                if (channel.isCompleted) {
                    @OptIn(ExperimentalCoroutinesApi::class)
                    channel.getCompleted().close()
                }
            }

            incomingChannels.clear()
        }

        if (incomingStreamsInitialized) {
            incomingStreams.clear()
        }

        if (outgoingStreamsInitialized) {
            outgoingStreams.close()
        }

        if (incomingHotFlowsInitialized) {
            incomingHotFlows.close()
        }
    }
}

private object StreamEnd

private class StreamCancel(
    val cause: RPCMessage.StreamCancel
)

private abstract class RPCIncomingHotFlow(
    private val rawFlow: MutableSharedFlow<Any?>,
    private val consume: suspend (FlowCollector<Any?>, onError: (Throwable) -> Unit) -> Unit,
) : MutableSharedFlow<Any?> {
    private val subscriptionContexts by lazy { mutableListOf<CoroutineContext>() }

    override suspend fun collect(collector: FlowCollector<Any?>): Nothing {
        val context = currentCoroutineContext()

        if (context.isActive) {
            subscriptionContexts.add(context)

            context.job.invokeOnCompletion {
                subscriptionContexts.remove(context)
            }
        }

        rawFlow.collect(collector)
    }

    // value can be ignored, as actual values are coming from the
    override suspend fun emit(value: Any?) {
        consume(rawFlow) { e ->
            subscriptionContexts.forEach { it.cancel(CancellationException(e.message, e)) }
        }
    }
}

private fun <K : Any, V> ConcurrentHashMap<K, CompletableDeferred<V>>.getDeferred(key: K): CompletableDeferred<V> {
    return putIfAbsentAndGet(key, CompletableDeferred())
}

private operator fun <K : Any, V> ConcurrentHashMap<K, CompletableDeferred<V>>.set(key: K, value: V) {
    getDeferred(key).complete(value)
}
