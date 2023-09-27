package org.jetbrains.krpc.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.*
import kotlin.coroutines.CoroutineContext

class RPCClientEngine(private val transport: RPCTransport) : RPCEngine {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default

    private var callIndex: Long = 0

    override suspend fun call(callInfo: RPCCallInfo): Any? {
        val callId = callInfo.dataType.toString() + ":" + callIndex++
        val callContext = RPCCallContext(callId)
        val json = prepareJson(callContext)
        val message = serializeRequest(callId, callInfo, json)
        val result = CompletableDeferred<Any?>()

        launch {
            val incoming: Flow<RPCMessage> = transport.incoming.filter { it.callId == callId }

            incoming.collect {
                when (it) {
                    is RPCMessage.CallData -> error("Unexpected message")
                    is RPCMessage.CallException -> {
                        val cause = it.cause.deserialize()
                        result.completeExceptionally(cause)
                    }

                    is RPCMessage.CallSuccess -> {
                        val serializerResult = json.serializersModule.contextualForFlow(callInfo.returnType)
                        val value = json.decodeFromString(serializerResult, it.data)
                        result.complete(value)
                    }

                    is RPCMessage.StreamCancel -> {
                        callContext.cancelFlow(it)
                    }

                    is RPCMessage.StreamFinished -> {
                        callContext.closeFlow(it)
                    }

                    is RPCMessage.StreamMessage -> {
                        callContext.send(it, json)
                    }
                }
            }
        }

        coroutineContext.job.invokeOnCompletion {
            callContext.close()
        }

        transport.send(message)

        launch {
            handleOutgoingFlows(callContext, json)
        }

        return result.await()
    }

    private suspend fun handleOutgoingFlows(callContext: RPCCallContext, json: Json) {
        val mutex = Mutex()
        for (clientFlow in callContext.outgoingFlows) {
            launch {
                val callId = clientFlow.callId
                val flowId = clientFlow.flowId
                val elementSerializer = clientFlow.elementSerializer
                try {
                    clientFlow.flow.collect {
                        mutex.withLock {
                            val data = json.encodeToString(elementSerializer, it!!)
                            val message = RPCMessage.StreamMessage(callId, flowId, data)
                            transport.send(message)
                        }
                    }
                } catch (cause: Throwable) {
                    val serializedReason = serializeException(cause)
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
        val serializerData = json.serializersModule.contextualForFlow(callInfo.dataType)
        val jsonData = json.encodeToString(serializerData, callInfo.data)
        return RPCMessage.CallData(callId, callInfo.methodName, jsonData)
    }

    private fun prepareJson(rpcCallContext: RPCCallContext): Json = Json {
        serializersModule = SerializersModule {
            contextual(Flow::class) {
                @Suppress("UNCHECKED_CAST")
                FlowSerializer(rpcCallContext, it.first() as KSerializer<Any>)
            }
        }
    }
}
