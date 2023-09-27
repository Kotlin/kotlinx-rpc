package org.jetbrains.krpc

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class RPCCallContext(
    val callId: String,
) {
    private var flowId = 0
    private val incomingFlows = ConcurrentMap<String, FlowInfo>()
    private val incomingChannels = ConcurrentMap<String, Channel<Any>>()

    val outgoingFlows: Channel<FlowInfo> = Channel(capacity = Channel.UNLIMITED)

    fun registerFlow(flow: Flow<*>, elementSerializer: KSerializer<Any>): String {
        val id = "flow:${flowId++}"
        outgoingFlows.trySend(FlowInfo(callId, id, flow, elementSerializer))
        return id
    }

    fun prepareFlow(flowId: String, elementSerializer: KSerializer<Any>): Flow<*> {
        val incoming: Channel<Any> = Channel(Channel.UNLIMITED)
        incomingChannels[flowId] = incoming

        val flow = flow {
            for (message in incoming) {
                when (message) {
                    is FlowCancel -> throw message.cause.cause.deserialize()
                    is FlowEnd -> return@flow
                    else -> emit(message)
                }
            }
        }
        incomingFlows[flowId] = FlowInfo(callId, flowId, flow, elementSerializer)
        return flow
    }

    suspend fun closeFlow(message: RPCMessage.StreamFinished) {
        incomingChannels[message.flowId]!!.send(FlowEnd)
    }

    suspend fun cancelFlow(message: RPCMessage.StreamCancel) {
        incomingChannels[message.flowId]!!.send(FlowCancel(message))
    }

    suspend fun send(message: RPCMessage.StreamMessage, json: Json) {
        val info = incomingFlows[message.flowId] ?: error("Unknown flow ${message.flowId}")
        val result = json.decodeFromString(info.elementSerializer, message.data)
        incomingChannels[message.flowId]!!.send(result)
    }

    fun close() {
        for (channel in incomingChannels.values) {
            channel.close()
        }

        incomingChannels.clear()
        incomingFlows.clear()
    }
}

private object FlowEnd

private class FlowCancel(
    val cause: RPCMessage.StreamCancel
)