package org.jetbrains.krpc

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.companionObject

val MY_SERVICE_METHODS: MutableMap<String, KType> = mutableMapOf()
val SERVICES_METHODS: MutableMap<KClass<*>, MutableMap<String, KType>> = mutableMapOf()

inline fun <reified T : RPC> rpcBackendOf(service: T, transport: RPCTransport): RPCBackend<T> =
    RPCBackend(service, transport, T::class)

class RPCBackend<T>(val service: T, val transport: RPCTransport, serviceClass: KClass<*>) : CoroutineScope {
    private val methods: Map<String, KCallable<*>> = serviceClass
        .members
        .filter { it.name != "toString" && it.name != "hashCode" && it.name != "equals" }
        .map { it.name to it }.toMap()

    private val methodTypes: MutableMap<String, KType> = SERVICES_METHODS[serviceClass]!!

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    private val calls: MutableMap<String, Job> = ConcurrentHashMap()
    private val callContexts = ConcurrentHashMap<String, RPCCallContext>()

    fun start() {
        launch {
            transport.incoming.collect {
                val callId = it.callId
                println("Received message $it")

                when (it) {
                    is RPCMessage.CallData -> {
                        handleCall(callId, it)
                    }

                    is RPCMessage.CallException -> {
                        calls[callId]?.cancel()
                    }

                    is RPCMessage.CallSuccess -> error("Unexpected success message")
                    is RPCMessage.StreamCancel -> TODO()
                    is RPCMessage.StreamFinished -> TODO()
                    is RPCMessage.StreamMessage -> {
                        val callContext = callContexts[callId] ?: error("Unknown call $callId")
                        val flowId = it.flowId
                        val value = callContext.incomingFlows[flowId] ?: error("Unknown flow $flowId")
                        val flow = value.flow as MutableStateFlow<Any>
                        val json = prepareJson(callContext)
                        val message = json.decodeFromString(value.elementSerializer, it.data)
                        flow.tryEmit(message)
                    }
                }
            }
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun handleCall(callId: String, callData: RPCMessage.CallData) {
        val callContext = RPCCallContext(callId)
        val json = prepareJson(callContext)
        callContexts[callId] = callContext
        val methodName = callData.method
        val method = methods[methodName] ?: error("Unknown method $methodName")
        val type = methodTypes[methodName] ?: error("Unknown method $methodName")
        val args = json.decodeFromString(serializer(type), callData.data) as MethodParameters
        val argsArray: Array<Any?> = args.asArray()

        calls[callId] = launch {
            val result = try {
                val returnType = method.returnType
                val value = method.callSuspend(service, *argsArray)
                val jsonResult = json.encodeToString(serializer(returnType), value)
                RPCMessage.CallSuccess(callId, jsonResult)
            } catch (cause: Throwable) {
                val serializedCause = SerializedException(cause.message ?: "Unknown error")
                RPCMessage.CallException(callId, serializedCause)
            }
            transport.send(result)
            handleOutgoingFlows(callContext, json)
        }.apply {
            invokeOnCompletion(onCancelling = true) {
                calls.remove(callId)
            }
        }
    }

    private suspend fun handleOutgoingFlows(callContext: RPCCallContext, json: Json) {
        for (clientFlow in callContext.outgoingFlows) {
            launch {
                val callId = clientFlow.callId
                val flowId = clientFlow.flowId
                val elementSerializer = clientFlow.elementSerializer
                try {
                    clientFlow.flow.collect {
                        val data = json.encodeToString(elementSerializer, it!!)
                        val message = RPCMessage.StreamMessage(callId, flowId, data)
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

    private fun prepareJson(rpcCallContext: RPCCallContext): Json = Json {
        serializersModule = SerializersModule {
            contextual(Flow::class) {
                FlowSerializer(rpcCallContext, it.first() as KSerializer<Any>)
            }
        }
    }

    fun stop() {
        coroutineContext.cancel()
    }
}

//suspend fun callSuspend(callable: KCallable<*>, args: Array<Any?>): Any? = suspendCancellableCoroutine {
//    try {
//        callable.call(*args, it)
//    } catch (cause: Throwable) {
//        it.resumeWithException(cause)
//    }
//}