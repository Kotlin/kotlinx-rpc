package org.jetbrains.krpc.server.internal

import kotlinx.coroutines.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.internal.*
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.map.ConcurrentHashMap
import org.jetbrains.krpc.internal.transport.RPCMessage
import org.jetbrains.krpc.internal.transport.RPCMessageSender
import org.jetbrains.krpc.internal.transport.RPCEndpointBase
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.callSuspend

internal class RPCServerService<T : RPC>(
    private val service: T,
    private val serviceKClass: KClass<T>,
    override val config: RPCConfig.Server = RPCConfig.Server.Default,
    override val sender: RPCMessageSender,
) : RPCEndpointBase(), CoroutineScope {
    private val serviceTypeString = serviceKClass.qualifiedClassName
    override val logger = CommonLogger.initialized().logger(objectId(serviceTypeString))

    private val methods: Map<String, KCallable<*>>
    private val fields: Map<String, KCallable<*>>

    override val coroutineContext: CoroutineContext = sender.coroutineContext + Job(sender.coroutineContext.job)

    private val calls = ConcurrentHashMap<String, Job>()

    private val streamContexts = ConcurrentHashMap<String, LazyRPCStreamContext>()

    init {
        val (fieldsMap, methodsMap) = serviceKClass.members
            .filter { it.name != "toString" && it.name != "hashCode" && it.name != "equals" }
            .partition { it is KProperty<*> }

        fields = fieldsMap.associateBy { it.name }
        methods = methodsMap.associateBy { it.name }

        service.coroutineContext.job.invokeOnCompletion {
            logger.trace { "Service completed with $it" }
        }
    }

    suspend fun accept(message: RPCMessage) {
        val result = runCatching {
            val callId = message.callId
            logger.trace { "Incoming message $message" }

            when (message) {
                is RPCMessage.CallData -> {
                    handleCall(callId, message)
                }

                is RPCMessage.CallException -> {
                    calls[callId]?.cancel()
                }

                is RPCMessage.CallSuccess -> {
                    error("Unexpected success message")
                }

                is RPCMessage.StreamCancel -> {
                    val streamContext = streamContexts[callId] ?: error("Unknown call $callId")
                    streamContext.awaitInitialized().cancelStream(message)
                }

                is RPCMessage.StreamFinished -> {
                    val streamContext = streamContexts[callId] ?: error("Unknown call $callId")
                    streamContext.awaitInitialized().closeStream(message)
                }

                is RPCMessage.StreamMessage -> {
                    val streamContext = streamContexts[callId] ?: error("Unknown call $callId")
                    streamContext.awaitInitialized().send(message, prepareSerialFormat(streamContext))
                }
            }

            return@runCatching true
        }

        if (result.isFailure) {
            logger.error { result.exceptionOrNull()?.stackTrace?.toString() }
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun handleCall(callId: String, callData: RPCMessage.CallData) {
        val streamContext = LazyRPCStreamContext { RPCStreamContext(callId, config) }
        val serialFormat = prepareSerialFormat(streamContext)
        streamContexts[callId] = streamContext

        val isMethod = when (callData.callType) {
            RPCMessage.CallType.Method -> true
            RPCMessage.CallType.Field -> false
            else -> true // compatibility with old clients
        }

        val callableName = callData.callableName

        val callable = (if (isMethod) methods else fields)[callableName]

        if (callable == null) {
            val callType = if (isMethod) "method" else "field"
            val cause = NoSuchMethodException("Service $serviceTypeString has no $callType $callableName")
            val message = RPCMessage.CallException(callId, serviceTypeString, serializeException(cause))
            launch {
                sender.sendMessage(message)
            }
            return
        }

        val argsArray = if (isMethod) {
            val type = rpcServiceMethodSerializationTypeOf(serviceKClass, callableName)
                ?: error("Unknown method $callableName")

            val serializerModule = serialFormat.serializersModule
            val paramsSerializer = serializerModule.rpcSerializerForType(type)
            val args = decodeMessageData(serialFormat, paramsSerializer, callData) as RPCMethodClassArguments

            args.asArray()
        } else {
            null
        }

        calls[callId] = launch {
            val result = try {
                @Suppress("detekt.SpreadOperator")
                val value = when {
                    isMethod -> callable.callSuspend(service, *argsArray!!)
                    else -> callable.call(service)
                }

                val returnType = callable.returnType
                val returnSerializer = serialFormat.serializersModule.rpcSerializerForType(returnType)
                when (serialFormat) {
                    is StringFormat -> {
                        val stringValue = serialFormat.encodeToString(returnSerializer, value)
                        RPCMessage.CallSuccessString(callId, serviceTypeString, stringValue)
                    }

                    is BinaryFormat -> {
                        val binaryValue = serialFormat.encodeToByteArray(returnSerializer, value)
                        RPCMessage.CallSuccessBinary(callId, serviceTypeString, binaryValue)
                    }

                    else -> {
                        unsupportedSerialFormatError(serialFormat)
                    }
                }
            } catch (cause: InvocationTargetException) {
                val serializedCause = serializeException(cause.cause ?: cause)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            } catch (@Suppress("detekt.TooGenericExceptionCaught") cause: Throwable) {
                val serializedCause = serializeException(cause)
                RPCMessage.CallException(callId, serviceTypeString, serializedCause)
            }
            sender.sendMessage(result)

            launch {
                handleOutgoingStreams(this, streamContext, serialFormat, serviceTypeString)
            }

            launch {
                handleIncomingHotFlows(this, streamContext)
            }
        }.apply {
            invokeOnCompletion(onCancelling = true) {
                calls.remove(callId)
                streamContext.valueOrNull?.close()
            }
        }
    }
}
