/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.selects.select
import kotlinx.rpc.internal.InternalRPCApi
import kotlinx.rpc.internal.getDeferred
import kotlinx.rpc.internal.getOrNull
import kotlinx.rpc.internal.map.ConcurrentHashMap
import kotlinx.rpc.internal.set
import kotlinx.rpc.krpc.RPCConfig
import kotlinx.rpc.krpc.StreamScope
import kotlinx.rpc.krpc.noStreamScopeError
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlin.coroutines.CoroutineContext

@InternalRPCApi
public class LazyRPCStreamContext(
    public val streamScopeOrNull: StreamScope?,
    private val fallbackScope: StreamScope? = null,
    private val initializer: (StreamScope) -> RPCStreamContext,
) {
    private val deferred = CompletableDeferred<RPCStreamContext>()
    private val lazyValue by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        if (streamScopeOrNull == null && (STREAM_SCOPES_ENABLED || fallbackScope == null)) {
            noStreamScopeError()
        }

        // null pointer is impossible
        val streamScope = streamScopeOrNull ?: fallbackScope!!
        initializer(streamScope).also { deferred.complete(it) }
    }

    public suspend fun awaitInitialized(): RPCStreamContext = deferred.await()

    public val valueOrNull: RPCStreamContext? get() = if (deferred.isCompleted) lazyValue else null

    public fun initialize(): RPCStreamContext = lazyValue
}

@InternalRPCApi
public class RPCStreamContext(
    private val callId: String,
    private val config: RPCConfig,
    private val connectionId: Long?,
    private val serviceId: Long?,
    public val streamScope: StreamScope,
) {
    private companion object {
        private const val STREAM_ID_PREFIX = "stream:"
    }
    private val closed = CompletableDeferred<Unit>()

    // thread-safe set
    private val closedStreams = ConcurrentHashMap<String, CompletableDeferred<Unit>>()

    @InternalRPCApi
    public inline fun launchIf(
        condition: RPCStreamContext.() -> Boolean,
        noinline block: suspend CoroutineScope.(RPCStreamContext) -> Unit,
    ) {
        if (condition(this)) {
            launch(block)
        }
    }

    public fun launch(block: suspend CoroutineScope.(RPCStreamContext) -> Unit) {
        streamScope.launch(callId) {
            block(this@RPCStreamContext)
        }
    }

    public fun cancel(message: String, cause: Throwable?): Job? {
        return streamScope.cancelRequestScopeById(callId, message, cause)
    }

    init {
        streamScope.onScopeCompletion(callId) { cause ->
            close(cause)
        }
    }

    private val streamIdCounter = atomic(0L)

    public val incomingHotFlowsAvailable: Boolean get() = incomingHotFlowsInitialized

    public val outgoingStreamsAvailable: Boolean get() = outgoingStreamsInitialized

    private var incomingStreamsInitialized: Boolean = false
    private val incomingStreams by lazy {
        incomingStreamsInitialized = true
        ConcurrentHashMap<String, CompletableDeferred<RPCStreamCall>>()
    }

    private var incomingChannelsInitialized: Boolean = false
    private val incomingChannels by lazy {
        incomingChannelsInitialized = true
        ConcurrentHashMap<String, CompletableDeferred<Channel<Any?>?>>()
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
        outgoingStreams.trySend(
            RPCStreamCall(
                callId = callId,
                streamId = id,
                stream = stream,
                kind = streamKind,
                elementSerializer = elementSerializer,
                connectionId = connectionId,
                serviceId = serviceId,
            )
        )
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

        val stream = streamOf<StreamT>(streamId, streamKind, stateFlowInitialValue, incoming)
        incomingStreams[streamId] = RPCStreamCall(
            callId = callId,
            streamId = streamId,
            stream = stream,
            kind = streamKind,
            elementSerializer = elementSerializer,
            connectionId = connectionId,
            serviceId = serviceId,
        )
        return stream
    }

    @Suppress("UNCHECKED_CAST")
    private fun <StreamT : Any> streamOf(
        streamId: String,
        streamKind: StreamKind,
        stateFlowInitialValue: Any?,
        incoming: Channel<Any?>,
    ): StreamT {
        suspend fun consumeFlow(collector: FlowCollector<Any?>, onError: (Throwable) -> Unit) {
            fun onClose() {
                incoming.cancel()

                closedStreams[streamId] = Unit
                incomingChannels.remove(streamId)?.complete(null)
                incomingStreams.remove(streamId)
            }

            for (message in incoming) {
                when (message) {
                    is StreamCancel -> {
                        onClose()
                        onError(message.cause ?: streamCanceled())
                    }

                    is StreamEnd -> {
                        onClose()
                        if (streamKind != StreamKind.Flow) {
                            onError(streamCanceled())
                        }

                        return
                    }

                    else -> {
                        collector.emit(message)
                    }
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

    public suspend fun closeStream(message: RPCCallMessage.StreamFinished) {
        incomingChannelOf(message.streamId)?.send(StreamEnd)
    }

    public suspend fun cancelStream(message: RPCCallMessage.StreamCancel) {
        incomingChannelOf(message.streamId)?.send(StreamCancel(message.cause.deserialize()))
    }

    public suspend fun send(message: RPCCallMessage.StreamMessage, serialFormat: SerialFormat) {
        val info: RPCStreamCall? = select {
            incomingStreams.getDeferred(message.streamId).onAwait { it }
            closedStreams.getDeferred(message.streamId).onAwait { null }
            closed.onAwait { null }
        }
        if (info == null) return
        val result = decodeMessageData(serialFormat, info.elementSerializer, message)
        val channel = incomingChannelOf(message.streamId)
        channel?.send(result)
    }

    private suspend fun incomingChannelOf(streamId: String): Channel<Any?>? {
        return select {
            incomingChannels.getDeferred(streamId).onAwait { it }
            closedStreams.getDeferred(streamId).onAwait { null }
            closed.onAwait { null }
        }
    }

    private fun close(cause: Throwable?) {
        if (closed.isCompleted) {
            return
        }

        closed.complete(Unit)

        if (incomingChannelsInitialized) {
            for (channel in incomingChannels.values) {
                if (!channel.isCompleted) {
                    continue
                }

                @OptIn(ExperimentalCoroutinesApi::class)
                channel.getCompleted()?.apply {
                    trySend(StreamEnd)

                    // close for sending, but not for receiving our cancel message, if possible.
                    close(cause)
                }
            }

            incomingChannels.clear()
        }

        if (incomingStreamsInitialized) {
            incomingStreams.values
                .mapNotNull { it.getOrNull()?.stream }
                .filterIsInstance<RPCIncomingHotFlow>()
                .forEach { stream ->
                    stream.subscriptionContexts.forEach {
                        it.cancel(CancellationException("Stream closed", cause))
                    }
                }

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

private fun streamCanceled() = NoSuchElementException("Stream canceled")

private object StreamEnd

private class StreamCancel(val cause: Throwable? = null)

private abstract class RPCIncomingHotFlow(
    private val rawFlow: MutableSharedFlow<Any?>,
    private val consume: suspend (FlowCollector<Any?>, onError: (Throwable) -> Unit) -> Unit,
) : MutableSharedFlow<Any?> {
    val subscriptionContexts by lazy { mutableSetOf<CoroutineContext>() }

    override suspend fun collect(collector: FlowCollector<Any?>): Nothing {
        val context = currentCoroutineContext()

        if (context.isActive) {
            subscriptionContexts.add(context)

            context.job.invokeOnCompletion {
                subscriptionContexts.remove(context)
            }
        }

        try {
            rawFlow.collect(collector)
        } finally {
            subscriptionContexts.remove(context)
        }
    }

    // value can be ignored, as actual values are coming from the rawFlow
    override suspend fun emit(value: Any?) {
        consume(rawFlow) { e ->
            subscriptionContexts.forEach { it.cancel(CancellationException(e.message, e)) }

            subscriptionContexts.clear()
        }
    }
}
