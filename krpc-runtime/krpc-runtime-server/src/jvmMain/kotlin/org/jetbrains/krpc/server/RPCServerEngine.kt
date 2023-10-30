package org.jetbrains.krpc.server

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.*
import org.jetbrains.krpc.internal.map.ConcurrentHashMap
import org.jetbrains.krpc.server.internal.rpcServiceMethodSerializationTypeOf
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.callSuspend

/**
 * Server engine for the provided RPC [service].
 * Handles incoming messages from [transport] and routes them to the provided [service].
 * Handles requests made via [service] and routes them to the [transport]
 *
 * Server engine is also in change of serialization, exception handling and stream management.
 */
class RPCServerEngine<T : RPC>(
    private val service: T,
    private val transport: RPCTransport,
    private val serviceKClass: KClass<T>,
    private val config: RPCConfig.Server = RPCConfig.Server.Default,
) : CoroutineScope {
    private val serviceTypeString = serviceKClass.toString()
    private val logger = KotlinLogging.logger("RPCServerEngine[$serviceTypeString][0x${hashCode().toString(16)}]")

    private val methods: Map<String, KCallable<*>>
    private val fields: Map<String, KCallable<*>>

    override val coroutineContext: CoroutineContext = transport.coroutineContext + Job(transport.coroutineContext.job)

    private val calls = ConcurrentHashMap<String, Job>()

    private val flowContexts = ConcurrentHashMap<String, LazyRPCStreamContext>()

    init {
        val (fieldsMap, methodsMap) = serviceKClass.members
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
                        flowContext.initialize().cancelStream(message)
                    }

                    is RPCMessage.StreamFinished -> {
                        val flowContext = flowContexts[callId] ?: error("Unknown call $callId")
                        flowContext.initialize().closeStream(message)
                    }

                    is RPCMessage.StreamMessage -> {
                        val flowContext = flowContexts[callId] ?: error("Unknown call $callId")
                        flowContext.initialize().send(message, prepareSerialFormat(flowContext))
                    }
                }

                return@runCatching true
            }

            if (result.isFailure) {
                logger.error { result.exceptionOrNull()?.stackTrace?.toString() }
            }

            result.getOrNull() == true
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun handleCall(callId: String, callData: RPCMessage.CallData) {
        val flowContext = LazyRPCStreamContext { RPCStreamContext(callId, config) }
        val serialFormat = prepareSerialFormat(flowContext)
        flowContexts[callId] = flowContext
        val callableName = callData.callableName

        val (isMethod, actualName) = when {
            callableName.endsWith("\$method") -> true to callableName.removeSuffix("\$method")
            callableName.endsWith("\$field") -> false to callableName.removeSuffix("\$field")
            else -> true to callableName // compatibility with old clients
        }

        val callable = (if (isMethod) methods else fields)[actualName]

        if (callable == null) {
            val callType = if (isMethod) "method" else "field"
            val cause = NoSuchMethodException("Service $serviceTypeString has no $callType $actualName")
            val message = RPCMessage.CallException(callId, serviceTypeString, serializeException(cause))
            launch {
                transport.send(message)
            }
            return
        }

        val argsArray = if (isMethod) {
            val type = rpcServiceMethodSerializationTypeOf(serviceKClass, actualName)
                ?: error("Unknown method $actualName")

            val serializerModule = serialFormat.serializersModule
            val paramsSerializer = serializerModule.rpcSerializerForType(type)
            val args = when (serialFormat) {
                is StringFormat -> serialFormat.decodeFromString(paramsSerializer, callData.data) as RPCMethodClassArguments
                else -> error("binary not supported for RPCMessage.CallSuccess")
            }

            args.asArray()
        } else null

        calls[callId] = launch {
            val result = try {
                val value = when {
                    isMethod -> callable.callSuspend(service, *argsArray!!)
                    else -> callable.call(service)
                }

                val returnType = callable.returnType
                val returnSerializer = serialFormat.serializersModule.rpcSerializerForType(returnType)
                val encodedResult = when (serialFormat) {
                    is StringFormat -> serialFormat.encodeToString(returnSerializer, value)
                    else -> error("binary not supported for RPCMessage.CallSuccess")
                }
                RPCMessage.CallSuccess(callId, serviceTypeString, encodedResult)
            } catch (cause: InvocationTargetException) {
                val serializedCause = serializeException(cause.cause ?: cause)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            } catch (cause: Throwable) {
                val serializedCause = serializeException(cause)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            }
            transport.send(result)

            launch {
                handleOutgoingFlows(flowContext, serialFormat)
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

    private suspend fun handleOutgoingFlows(flowContext: LazyRPCStreamContext, serialFormat: SerialFormat) {
        val mutex = Mutex()
        for (clientStream in flowContext.awaitInitialized().outgoingStreams) {
            launch {
                val callId = clientStream.callId
                val streamId = clientStream.streamId
                val elementSerializer = clientStream.elementSerializer
                try {
                    when (clientStream.kind) {
                        StreamKind.Flow, StreamKind.SharedFlow, StreamKind.StateFlow -> {
                            val stream = clientStream.stream as Flow<*>
                            stream.collect {
                                // because we can send new message for the new flow,
                                // which is not published with `transport.send(message)`
                                mutex.withLock {
                                    val data = when (serialFormat) {
                                        is StringFormat -> serialFormat.encodeToString(elementSerializer, it)
                                        else -> error("binary not supported for RPCMessage.CallSuccess")
                                    }
                                    val message = RPCMessage.StreamMessage(callId, serviceTypeString, streamId, data)
                                    transport.send(message)
                                }
                            }
                        }
                    }
                } catch (cause: Throwable) {
                    val serializedReason = serializeException(cause)
                    val message = RPCMessage.StreamCancel(callId, serviceTypeString, streamId, serializedReason)
                    transport.send(message)
                    throw cause
                }

                val message = RPCMessage.StreamFinished(callId, serviceTypeString, streamId)
                transport.send(message)
            }
        }
    }

    private suspend fun handleIncomingHotFlows(flowContext: LazyRPCStreamContext) {
        for (hotFlow in flowContext.awaitInitialized().incomingHotFlows) {
            launch {
                /** Start consuming incoming requests, see [RPCIncomingHotFlow.emit] */
                hotFlow.emit(null)
            }
        }
    }

    private fun prepareSerialFormat(rpcFlowContext: LazyRPCStreamContext): SerialFormat {
        val module = SerializersModule {
            contextual(Flow::class) {
                @Suppress("UNCHECKED_CAST")
                StreamSerializer.Flow(rpcFlowContext.initialize(), it.first() as KSerializer<Any?>)
            }

            contextual(SharedFlow::class) {
                @Suppress("UNCHECKED_CAST")
                StreamSerializer.SharedFlow(rpcFlowContext.initialize(), it.first() as KSerializer<Any?>)
            }

            contextual(StateFlow::class) {
                @Suppress("UNCHECKED_CAST")
                StreamSerializer.StateFlow(rpcFlowContext.initialize(), it.first() as KSerializer<Any?>)
            }
        }

        return config.serialFormatInitializer.applySerializersModuleAndGet(module)
    }

    fun stop() {
        coroutineContext.cancel()
    }
}
