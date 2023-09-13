package org.jetbrains.krpc

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.coroutines.CoroutineContext

class RPCClientEngine(private val transport: RPCTransport) : RPCEngine {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default

    private var callIndex: Long = 0

    override suspend fun call(callInfo: RPCCallInfo): Any {
        val callId = callInfo.data::class.qualifiedName + ":" + callIndex++
        val callContext = RPCCallContext(callId)
        val json = prepareJson(callContext)
        val message = serializeRequest(callId, callInfo, json)
        val result = CompletableDeferred<Any>()

        launch {
            val incoming: Flow<RPCMessage> = transport.incoming.filter { it.callId == callId }

            incoming.collect {
                when (it) {
                    is RPCMessage.CallData -> error("Unexpected message")
                    is RPCMessage.CallException -> result.completeExceptionally(Exception("please parse me"))
                    is RPCMessage.CallSuccess -> {
                        val serializerResult = serializer(callInfo.returnType)
                        result.complete(json.decodeFromString(serializerResult, it.data)!!)
                    }

                    is RPCMessage.StreamCancel -> {
                        val flowId = it.flowId
                        val value = callContext.incomingFlows[flowId]!!
                        val flow = value.flow as MutableStateFlow<Any>

                        TODO()
                    }

                    is RPCMessage.StreamFinished -> {
                        val flowId = it.flowId
                        val value = callContext.incomingFlows[flowId]!!
                        val flow = value.flow as MutableStateFlow<Any>

                        TODO()
                    }

                    is RPCMessage.StreamMessage -> {
                        val flowId = it.flowId
                        val value = callContext.incomingFlows[flowId]!!
                        val flow = value.flow as MutableStateFlow<Any>
                        val message = json.decodeFromString(value.elementSerializer, it.data)
                        flow.tryEmit(message)
                    }
                }
            }
        }

        transport.send(message)

        launch {
            handleOutgoingFlows(callContext, json)
        }

        return result.await()
    }

    private suspend fun handleOutgoingFlows(callContext: RPCCallContext, json: Json) {
        for (clientFlow in callContext.outgoingFlows) {
            launch {
                val callId = clientFlow.callId
                val flowId = clientFlow.flowId
                val elementSerializer = clientFlow.elementSerializer
                try {
                    clientFlow.flow.collect {
                        val message =
                            RPCMessage.StreamMessage(callId, flowId, json.encodeToString(elementSerializer, it!!))
                        transport.send(message)
                    }
                } catch (cause: Throwable) {
                    val serializedReason = SerializedException(cause.message ?: "Unknown error")
                    val message = RPCMessage.StreamCancel(callId, flowId, serializedReason)
                    transport.send(message)
                    throw cause
                }

                val message = RPCMessage.StreamFinished(callId, flowId)
                transport.send(message)
            }
        }
    }

    private fun serializeRequest(callId: String, callInfo: RPCCallInfo, json: Json): RPCMessage {
        val serializerData = serializer(callInfo.dataType)
        val jsonData = json.encodeToString(serializerData, callInfo.data)
        return RPCMessage.CallData(callId, callInfo.methodName, jsonData)
    }

    private fun prepareJson(rpcCallContext: RPCCallContext): Json = Json {
        serializersModule = SerializersModule {
            contextual(Flow::class) {
                FlowSerializer(rpcCallContext, it.first() as KSerializer<Any>)
            }
        }
    }
}

