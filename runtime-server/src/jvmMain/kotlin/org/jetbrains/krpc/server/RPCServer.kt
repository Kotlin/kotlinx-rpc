package org.jetbrains.krpc.server

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.jetbrains.krpc.*
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspend
import kotlin.reflect.typeOf

inline fun <reified T : RPC> rpcBackendOf(
    service: T,
    transport: RPCTransport,
    noinline serviceMethodsGetter: (String) -> KType?
): RPCServer<T> {
    return rpcBackendOf(service, typeOf<T>(), transport, serviceMethodsGetter)
}

fun <T : RPC> rpcBackendOf(
    service: T,
    serviceType: KType,
    transport: RPCTransport,
    serviceMethodsGetter: (String) -> KType?
): RPCServer<T> {
    return RPCServer(service, transport, serviceType, serviceMethodsGetter)
}

class RPCServer<T>(
    val service: T,
    val transport: RPCTransport,
    serviceType: KType,
    val serviceMethodsGetter: (String) -> KType?
) : CoroutineScope {
    private val methods: Map<String, KCallable<*>> = (serviceType.classifier as KClass<*>)
        .members
        .filter { it.name != "toString" && it.name != "hashCode" && it.name != "equals" }
        .map { it.name to it }.toMap()

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    private val calls: MutableMap<String, Job> = ConcurrentHashMap()
    private val callContexts = ConcurrentHashMap<String, RPCCallContext>()

    fun start(): Job = launch {
        transport.incoming.collect {
            val callId = it.callId

            when (it) {
                is RPCMessage.CallData -> {
                    handleCall(callId, it)
                }

                is RPCMessage.CallException -> {
                    calls[callId]?.cancel()
                }

                is RPCMessage.CallSuccess -> error("Unexpected success message")
                is RPCMessage.StreamCancel -> {
                    val callContext = callContexts[callId] ?: error("Unknown call $callId")
                    callContext.cancelFlow(it)
                }

                is RPCMessage.StreamFinished -> {
                    val callContext = callContexts[callId] ?: error("Unknown call $callId")
                    callContext.closeFlow(it)
                }

                is RPCMessage.StreamMessage -> {
                    val callContext = callContexts[callId] ?: error("Unknown call $callId")
                    callContext.send(it, prepareJson(callContext))
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
        val type = serviceMethodsGetter(methodName) ?: error("Unknown method $methodName")
        val serializerModule = json.serializersModule
        val paramsSerializer = serializerModule.contextualForFlow(type)
        val args = json.decodeFromString(paramsSerializer, callData.data) as RPCMethodClassArguments
        val argsArray: Array<Any?> = args.asArray()

        calls[callId] = launch {
            val result = try {
                val returnType = method.returnType
                val value = method.callSuspend(service, *argsArray)
                val returnSerializer = json.serializersModule.contextualForFlow(returnType)
                val jsonResult = json.encodeToString(returnSerializer, value)
                RPCMessage.CallSuccess(callId, jsonResult)
            } catch (cause: InvocationTargetException) {
                val serializedCause = serializeException(cause.cause!!)
                RPCMessage.CallException(callId, serializedCause)
            } catch (cause: Throwable) {
                val serializedCause = serializeException(cause)
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
        val mutex = Mutex()
        for (clientFlow in callContext.outgoingFlows) {
            launch {
                val callId = clientFlow.callId
                val flowId = clientFlow.flowId
                val elementSerializer = clientFlow.elementSerializer
                try {
                    clientFlow.flow.collect {
                        // because we can send new message for the new flow,
                        // which is not published with `transport.send(message)`
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

    private fun prepareJson(rpcCallContext: RPCCallContext): Json = Json {
        serializersModule = SerializersModule {
            polymorphic(Flow::class) {
                defaultDeserializer { TODO() }
            }
            contextual(Flow::class) {
                FlowSerializer(rpcCallContext, it.first() as KSerializer<Any>)
            }
        }
    }

    fun stop() {
        coroutineContext.cancel()
    }
}
