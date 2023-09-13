package org.jetbrains.krpc

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.KSerializer

internal class RPCCallContext(
    val callId: String,
) {
    private var flowId = 0
    internal val incomingFlows = mutableMapOf<String, FlowInfo>()
    internal val outgoingFlows = Channel<FlowInfo>(capacity = Channel.UNLIMITED)

    fun registerFlow(flow: Flow<*>, elementSerializer: KSerializer<Any>): String {
        val id = "flow:${flowId++}"
        outgoingFlows.trySend(FlowInfo(callId, id, flow, elementSerializer))
        return id
    }

    fun prepareFlow(id: String, elementSerializer: KSerializer<Any>): Flow<*> {
        val flow = MutableSharedFlow<Any>()
        incomingFlows[id] = FlowInfo(callId, id, flow, elementSerializer)
        return flow
    }
}