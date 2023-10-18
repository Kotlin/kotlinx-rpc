package org.jetbrains.krpc.server

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.*
import org.jetbrains.krpc.internal.InternalKRPCApi
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspend

class RPCServerEngine<T : RPC>(
    val service: T,
    val transport: RPCTransport,
    val serviceType: KType,
) : CoroutineScope {
    private val serviceTypeString = serviceType.toString()
    private val logger = KotlinLogging.logger("RPCServer[0x${hashCode().toString(16)}]")

    private val methods: Map<String, KCallable<*>> =
        (serviceType.classifier as KClass<*>).members.filter { it.name != "toString" && it.name != "hashCode" && it.name != "equals" }
            .associateBy { it.name }

    override val coroutineContext: CoroutineContext = transport.coroutineContext + Job(transport.coroutineContext.job)

    private val calls: MutableMap<String, Job> = ConcurrentHashMap()

    private val callContexts = ConcurrentHashMap<String, RPCCallContext>()

    init {
        service.coroutineContext.job.invokeOnCompletion {
            logger.trace { "Service completed with $it" }
        }

        launch {
            run()
        }
    }

    internal suspend fun run(): Unit = withContext(coroutineContext) {
        transport.subscribe { message ->
            if (message.serviceType != serviceTypeString) return@subscribe false
            val callId = message.callId
            logger.trace { "Incoming message $message" }

            when (message) {
                is RPCMessage.CallData -> {
                    handleCall(callId, message)
                }

                is RPCMessage.CallException -> {
                    calls[callId]?.cancel()
                }

                is RPCMessage.CallSuccess -> error("Unexpected success message")
                is RPCMessage.StreamCancel -> {
                    val callContext = callContexts[callId] ?: error("Unknown call $callId")
                    callContext.cancelFlow(message)
                }

                is RPCMessage.StreamFinished -> {
                    val callContext = callContexts[callId] ?: error("Unknown call $callId")
                    callContext.closeFlow(message)
                }

                is RPCMessage.StreamMessage -> {
                    val callContext = callContexts[callId] ?: error("Unknown call $callId")
                    callContext.send(message, prepareJson(callContext))
                }
            }

            return@subscribe true
        }
    }

    @OptIn(InternalCoroutinesApi::class, InternalKRPCApi::class)
    private fun handleCall(callId: String, callData: RPCMessage.CallData) {
        val callContext = RPCCallContext(callId)
        val json = prepareJson(callContext)
        callContexts[callId] = callContext
        val methodName = callData.method
        val method = methods[methodName]

        if (method == null) {
            val cause = NoSuchMethodException("Service ${serviceType.classifier} has no method $methodName")
            val message = RPCMessage.CallException(callId, serviceTypeString, serializeException(cause))
            launch {
                transport.send(message)
            }
            return
        }

        val type = rpcServiceMethodSerializationTypeOf(serviceType, methodName) ?: error("Unknown method $methodName")
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
                RPCMessage.CallSuccess(callId, serviceTypeString, jsonResult)
            } catch (cause: InvocationTargetException) {
                val serializedCause = serializeException(cause.cause!!)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            } catch (cause: Throwable) {
                val serializedCause = serializeException(cause)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            }
            transport.send(result)
            handleOutgoingFlows(callContext, json)
        }.apply {
            invokeOnCompletion(onCancelling = true) {
                calls.remove(callId)
            }
        }
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
                        // because we can send new message for the new flow,
                        // which is not published with `transport.send(message)`
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

    private fun prepareJson(rpcCallContext: RPCCallContext): Json = Json {
        serializersModule = SerializersModule {
            contextual(Flow::class) {
                @Suppress("UNCHECKED_CAST") FlowSerializer(rpcCallContext, it.first() as KSerializer<Any>)
            }
        }
    }

    fun stop() {
        coroutineContext.cancel()
    }
}
