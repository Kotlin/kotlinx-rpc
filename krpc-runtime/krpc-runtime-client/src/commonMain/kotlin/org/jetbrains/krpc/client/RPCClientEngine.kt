package org.jetbrains.krpc.client

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.*
import org.jetbrains.krpc.internal.InternalKRPCApi
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KType

private val CLIENT_ENGINE_ID = atomic(initial = 0L)

@OptIn(InternalCoroutinesApi::class)
internal class RPCClientEngine(
    private val transport: RPCTransport,
    private val serviceType: KType,
) : RPCEngine {
    private val callCounter = atomic(0L)
    private val engineId: Long = CLIENT_ENGINE_ID.incrementAndGet()
    private val logger = KotlinLogging.logger("RPCClientEngine[0x${hashCode().toString(16)}]")
    private val serviceTypeString = serviceType.toString()

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default

    init {
        coroutineContext.job.invokeOnCompletion {
            logger.trace { "Job completed with $it" }
        }
    }

    @OptIn(InternalKRPCApi::class)
    override suspend fun call(callInfo: RPCCallInfo): Any? {
        val id = callCounter.incrementAndGet()
        val callId = "$engineId:${callInfo.dataType}:$id"

        logger.trace { "start a call[$callId] ${callInfo.methodName}" }

        val callContext = RPCCallContext(callId)
        val json = prepareJson(callContext)
        val firstMessage = serializeRequest(callId, callInfo, json)
        val deferred = CompletableDeferred<Any?>()

        launch {
            transport.subscribe { message ->
                if (message.callId != callId) return@subscribe false

                when (message) {
                    is RPCMessage.CallData -> error("Unexpected message")
                    is RPCMessage.CallException -> {
                        val cause = runCatching {
                            message.cause.deserialize()
                        }

                        val result = if (cause.isFailure) {
                            cause.exceptionOrNull()!!
                        } else {
                            cause.getOrNull()!!
                        }

                        deferred.completeExceptionally(result)
                    }

                    is RPCMessage.CallSuccess -> {
                        val value = runCatching {
                            val serializerResult = json.serializersModule.contextualForFlow(callInfo.returnType)
                            json.decodeFromString(serializerResult, message.data)
                        }

                        deferred.completeWith(value)
                    }

                    is RPCMessage.StreamCancel -> {
                        callContext.cancelFlow(message)
                    }

                    is RPCMessage.StreamFinished -> {
                        callContext.closeFlow(message)
                    }

                    is RPCMessage.StreamMessage -> {
                        callContext.send(message, json)
                    }
                }

                return@subscribe true
            }
        }

        coroutineContext.job.invokeOnCompletion {
            callContext.close()
        }

        transport.send(firstMessage)

        launch {
            handleOutgoingFlows(callContext, json)
        }

        return deferred.await()
    }

    @OptIn(InternalKRPCApi::class)
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
                            val message = RPCMessage.StreamMessage(callId, serviceTypeString, flowId, data)
                            transport.send(message)
                        }
                    }
                } catch (cause: Throwable) {
                    val serializedReason = serializeException(cause)
                    val message = RPCMessage.StreamCancel(callId, serviceTypeString, flowId, serializedReason)
                    transport.send(message)
                    throw cause
                }

                val message = RPCMessage.StreamFinished(callId, serviceTypeString, flowId)
                transport.send(message)
            }
        }
    }

    private fun serializeRequest(callId: String, callInfo: RPCCallInfo, json: StringFormat): RPCMessage {
        val serializerData = json.serializersModule.contextualForFlow(callInfo.dataType)
        val jsonData = json.encodeToString(serializerData, callInfo.data)
        return RPCMessage.CallData(callId, serviceTypeString, callInfo.methodName, jsonData)
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
