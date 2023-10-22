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
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspend

class RPCServerEngine<T : RPC>(
    private val service: T,
    private val transport: RPCTransport,
    private val serviceType: KType,
    private val config: RPCConfig.Server = RPCConfig.Server.Default,
) : CoroutineScope {
    private val serviceTypeString = serviceType.toString()
    private val logger = KotlinLogging.logger("RPCServer[0x${hashCode().toString(16)}]")

    private val methods: Map<String, KCallable<*>>
    private val fields: Map<String, KCallable<*>>

    override val coroutineContext: CoroutineContext = transport.coroutineContext + Job(transport.coroutineContext.job)

    private val calls: MutableMap<String, Job> = ConcurrentHashMap()

    private val flowContexts = ConcurrentHashMap<String, LazyRPCFlowContext>()

    init {
        val (fieldsMap, methodsMap) = (serviceType.classifier as KClass<*>).members
            .filter { it.name != "toString" && it.name != "hashCode" && it.name != "equals" }
            .partition { it is KProperty<*> }

        fields = fieldsMap.associateBy { it.name }
        methods = methodsMap.associateBy { it.name }

        service.coroutineContext.job.invokeOnCompletion {
            logger.trace { "Service completed with $it" }
        }

        launch {
            run()
        }
    }

    private suspend fun run(): Unit = withContext(coroutineContext) {
        transport.subscribe { message ->
            val result = runCatching {
                if (message.serviceType != serviceTypeString) {
                    return@runCatching false
                }

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
                        val flowContext = flowContexts[callId] ?: error("Unknown call $callId")
                        flowContext.initialize().cancelFlow(message)
                    }

                    is RPCMessage.StreamFinished -> {
//                        logger.info { "Receive $callId ${message.flowId}, flow close" }
                        val flowContext = flowContexts[callId] ?: error("Unknown call $callId")
                        flowContext.initialize().closeFlow(message)
                    }

                    is RPCMessage.StreamMessage -> {
//                        logger.info { "Receive $callId ${message.flowId}, flow message" }
                        val flowContext = flowContexts[callId] ?: error("Unknown call $callId")
                        flowContext.initialize().send(message, prepareJson(flowContext))
                    }
                }

                return@runCatching true
            }

            if (result.isFailure) {
                logger.error { result.exceptionOrNull().toString() }
            }

            result.getOrNull() == true
        }
    }

    @OptIn(InternalCoroutinesApi::class, InternalKRPCApi::class)
    private fun handleCall(callId: String, callData: RPCMessage.CallData) {
        val flowContext = LazyRPCFlowContext { RPCFlowContext(callId, config) }
        val json = prepareJson(flowContext)
        flowContexts[callId] = flowContext
        val callableName = callData.callableName
        val callable = when(callData.callType) {
            RPCMessage.CallType.Method -> methods[callableName]
            RPCMessage.CallType.Field -> fields[callableName]
        }

        if (callable == null) {
            val cause = NoSuchMethodException("Service ${serviceType.classifier} has no ${callData.callType.name.lowercase()} $callableName")
            val message = RPCMessage.CallException(callId, serviceTypeString, serializeException(cause))
            launch {
                transport.send(message)
            }
            return
        }

        val argsArray = if (callData.callType == RPCMessage.CallType.Method) {
            val type = rpcServiceMethodSerializationTypeOf(serviceType, callData.callableName)
                ?: error("Unknown method ${callData.callableName}")

            val serializerModule = json.serializersModule
            val paramsSerializer = serializerModule.contextualForFlow(type)
            val args = json.decodeFromString(paramsSerializer, callData.data) as RPCMethodClassArguments

            args.asArray()
        } else null

        calls[callId] = launch {
            val result = try {
                val value = when (callData.callType) {
                    RPCMessage.CallType.Method -> {
                        callable.callSuspend(service, *argsArray!!)
                    }

                    RPCMessage.CallType.Field -> {
                        callable.call(service)
                    }
                }

                val returnType = callable.returnType
                val returnSerializer = json.serializersModule.contextualForFlow(returnType)
                val jsonResult = json.encodeToString(returnSerializer, value)
                RPCMessage.CallSuccess(callId, serviceTypeString, jsonResult)
            } catch (cause: InvocationTargetException) {
                val serializedCause = serializeException(cause.cause ?: cause)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            } catch (cause: Throwable) {
                val serializedCause = serializeException(cause)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            }
            transport.send(result)
            
            launch {
                handleOutgoingFlows(flowContext, json)
            }

            launch {
                handleIncomingHotFlows(flowContext)
            }
        }.apply {
            invokeOnCompletion(onCancelling = true) {
                calls.remove(callId)
                flowContext.valueOrNull?.close()
            }
        }
    }

    @OptIn(InternalKRPCApi::class)
    private suspend fun handleOutgoingFlows(flowContext: LazyRPCFlowContext, json: Json) {
        val mutex = Mutex()
        for (clientFlow in flowContext.awaitInitialized().outgoingFlows) {
            launch {
                val callId = clientFlow.callId
                val flowId = clientFlow.flowId
                val elementSerializer = clientFlow.elementSerializer
                try {
                    clientFlow.flow.collect {
                        // because we can send new message for the new flow,
                        // which is not published with `transport.send(message)`
                        mutex.withLock {
                            val data = json.encodeToString(elementSerializer, it)
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

    private suspend fun handleIncomingHotFlows(flowContext: LazyRPCFlowContext) {
        for (hotFlow in flowContext.awaitInitialized().incomingHotFlows) {
            launch {
                /** Start consuming incoming requests, see [RPCIncomingHotFlow.emit] */
                hotFlow.emit(null)
            }
        }
    }

    private fun prepareJson(rpcFlowContext: LazyRPCFlowContext): Json = Json {
        serializersModule = SerializersModule {
            contextual(Flow::class) {
                @Suppress("UNCHECKED_CAST")
                FlowSerializer(rpcFlowContext.initialize(), it.first() as KSerializer<Any?>)
            }

            config.serializersModuleExtension?.invoke(this)
        }
    }

    fun stop() {
        coroutineContext.cancel()
    }
}
