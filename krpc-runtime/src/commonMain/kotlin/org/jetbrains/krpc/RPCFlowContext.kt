package org.jetbrains.krpc

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.internal.InternalKRPCApi
import kotlin.coroutines.CoroutineContext

class LazyRPCFlowContext(private val initializer: () -> RPCFlowContext) {
    private val deferred = CompletableDeferred<RPCFlowContext>()
    private val lazyValue by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        initializer().also { deferred.complete(it) }
    }

    suspend fun awaitInitialized() = deferred.await()

    val valueOrNull get() = if (deferred.isCompleted) lazyValue else null

    fun initialize(): RPCFlowContext = lazyValue
}

class RPCFlowContext(private val callId: String, private val config: RPCConfig) {
    private val flowId = atomic(0L)

    private var incomingFlowsInitialized: Boolean = false
    private val incomingFlows by lazy {
        incomingFlowsInitialized = true
        ConcurrentMap<String, FlowInfo>()
    }

    private var incomingChannelsInitialized: Boolean = false
    private val incomingChannels by lazy {
        incomingChannelsInitialized = true
        ConcurrentMap<String, Channel<Any?>>()
    }

    private var outgoingFlowsInitialized: Boolean = false
    val outgoingFlows: Channel<FlowInfo> by lazy {
        outgoingFlowsInitialized = true
        Channel(capacity = Channel.UNLIMITED)
    }

    private var incomingHotFlowsInitialized: Boolean = false
    val incomingHotFlows: Channel<FlowCollector<Any?>> by lazy {
        incomingHotFlowsInitialized = true
        Channel(Channel.UNLIMITED)
    }

    fun registerFlow(flow: Flow<*>, elementSerializer: KSerializer<Any?>): String {
        val id = "flow:${flowId.getAndIncrement()}"
        outgoingFlows.trySend(FlowInfo(callId, id, flow, elementSerializer))
        return id
    }

    fun prepareFlow(
        flowId: String,
        flowKind: FlowKind,
        stateFlowInitialValue: Any?,
        elementSerializer: KSerializer<Any?>,
    ): Flow<*> {
        val incoming: Channel<Any?> = Channel(Channel.UNLIMITED)
        incomingChannels[flowId] = incoming

        val flow = flowOf(flowKind, stateFlowInitialValue, incoming)
        incomingFlows[flowId] = FlowInfo(callId, flowId, flow, elementSerializer)
        return flow
    }

    @OptIn(InternalKRPCApi::class)
    private fun flowOf(flowKind: FlowKind, stateFlowInitialValue: Any?, incoming: Channel<Any?>): Flow<Any?> {
        suspend fun consume(collector: FlowCollector<Any?>, onError: (Throwable) -> Unit) {
            for (message in incoming) {
                when (message) {
                    is FlowCancel -> onError(message.cause.cause.deserialize())
                    is FlowEnd -> return
                    else -> collector.emit(message)
                }
            }
        }

        return when (flowKind) {
            FlowKind.Plain -> flow {
                consume(this) { e -> throw e }
            }

            FlowKind.Shared -> {
                val sharedFlow: MutableSharedFlow<Any?> = config.incomingSharedFlowFactory()

                object : RPCIncomingHotFlow(sharedFlow, ::consume), MutableSharedFlow<Any?> by sharedFlow {
                    override suspend fun collect(collector: FlowCollector<Any?>): Nothing {
                        super.collect(collector)
                    }

                    override suspend fun emit(value: Any?) {
                        super.emit(value)
                    }
                }.also { incomingHotFlows.trySend(it) }
            }

            FlowKind.State -> {
                val stateFlow = MutableStateFlow(stateFlowInitialValue)

                object : RPCIncomingHotFlow(stateFlow, ::consume), MutableStateFlow<Any?> by stateFlow {
                    override suspend fun collect(collector: FlowCollector<Any?>): Nothing {
                        super.collect(collector)
                    }

                    override suspend fun emit(value: Any?) {
                        super.emit(value)
                    }
                }.also { incomingHotFlows.trySend(it) }
            }
        }
    }

    suspend fun closeFlow(message: RPCMessage.StreamFinished) {
        incomingChannelOf(message.flowId).send(FlowEnd)
    }

    suspend fun cancelFlow(message: RPCMessage.StreamCancel) {
        incomingChannelOf(message.flowId).send(FlowCancel(message))
    }

    suspend fun send(message: RPCMessage.StreamMessage, json: Json) {
        val info = incomingFlows[message.flowId] ?: error("Unknown flow ${message.flowId}")
        val result = json.decodeFromString(info.elementSerializer, message.data)
        incomingChannelOf(message.flowId).send(result)
    }

    private fun incomingChannelOf(flowId: String): Channel<Any?> {
        return incomingChannels[flowId] ?: error("Expected flow with id \"$flowId\" to be present in incomingChannels")
    }

    fun close() {
        if (incomingChannelsInitialized) {
            for (channel in incomingChannels.values) {
                channel.close()
            }

            incomingChannels.clear()
        }

        if (incomingFlowsInitialized) {
            incomingFlows.clear()
        }

        if (outgoingFlowsInitialized) {
            outgoingFlows.close()
        }

        if (incomingHotFlowsInitialized) {
            incomingHotFlows.close()
        }
    }
}

private object FlowEnd

private class FlowCancel(
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
