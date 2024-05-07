/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.server.internal

import kotlinx.coroutines.*
import kotlinx.rpc.RPC
import kotlinx.rpc.RPCConfig
import kotlinx.rpc.internal.*
import kotlinx.rpc.internal.logging.CommonLogger
import kotlinx.rpc.internal.logging.initialized
import kotlinx.rpc.internal.map.ConcurrentHashMap
import kotlinx.rpc.internal.transport.RPCCallMessage
import kotlinx.rpc.internal.transport.RPCMessageSender
import kotlinx.rpc.internal.transport.RPCServiceHandler
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.callSuspend

internal class RPCServerService<T : RPC>(
    private val service: T,
    private val serviceKClass: KClass<T>,
    override val config: RPCConfig.Server,
    private val connector: RPCServerConnector,
    coroutineContext: CoroutineContext,
) : RPCServiceHandler(), CoroutineScope {
    private val serviceTypeString = serviceKClass.qualifiedClassName
    override val logger = CommonLogger.initialized().logger(objectId(serviceTypeString))
    override val sender: RPCMessageSender get() = connector
    private val scope: CoroutineScope = this

    override val coroutineContext: CoroutineContext = coroutineContext.withServerStreamScope()

    private val methods: Map<String, KCallable<*>>
    private val fields: Map<String, KCallable<*>>

    private val requestMap = ConcurrentHashMap<String, RPCRequest>()

    init {
        val (fieldsMap, methodsMap) = serviceKClass.members
            .filter { it.name != "toString" && it.name != "hashCode" && it.name != "equals" }
            .partition { it is KProperty<*> }

        fields = fieldsMap.associateBy { it.name }
        methods = methodsMap.associateBy { it.name }

        coroutineContext.job.invokeOnCompletion {
            logger.trace { "Service completed with $it" }
        }
    }

    suspend fun accept(message: RPCCallMessage) {
        val result = runCatching {
            processMessage(message)
        }

        if (result.isFailure) {
            val exception = result.exceptionOrNull()
                ?: error("Expected exception value")

            cancelRequest(
                callId = message.callId,
                message = "Cancelled after failed to process message: $message, error message: ${exception.message}",
                cause = exception,
            )

            if (exception is CancellationException) {
                return
            }

            val error = serializeException(exception)
            val errorMessage = RPCCallMessage.CallException(
                callId = message.callId,
                serviceType = message.serviceType,
                cause = error,
                connectionId = message.connectionId,
            )

            sender.sendMessage(errorMessage)
        }
    }

    private suspend fun processMessage(message: RPCCallMessage) {
        logger.trace { "Incoming message $message" }

        when (message) {
            is RPCCallMessage.CallData -> {
                handleCall(message)
            }

            is RPCCallMessage.CallException -> {
                cancelRequest(
                    callId = message.callId,
                    message = "Cancelled after RPCCallMessage.CallException received",
                    cause = message.cause.deserialize(),
                )
            }

            is RPCCallMessage.CallSuccess -> {
                error("Unexpected success message: $message")
            }

            is RPCCallMessage.StreamCancel -> {
                // if no stream is present, it probably was already canceled
                getAndAwaitStreamContext(message)
                    ?.cancelStream(message)
            }

            is RPCCallMessage.StreamFinished -> {
                // if no stream is present, it probably was already finished
                getAndAwaitStreamContext(message)
                    ?.closeStream(message)
            }

            is RPCCallMessage.StreamMessage -> {
                requestMap[message.callId]?.streamContext?.apply {
                    awaitInitialized().send(message, prepareSerialFormat(this))
                } ?: error("Invalid request call id: ${message.callId}")
            }
        }
    }

    private suspend fun getAndAwaitStreamContext(message: RPCCallMessage): RPCStreamContext? {
        return requestMap[message.callId]?.streamContext?.awaitInitialized()
    }

    @Suppress("detekt.ThrowsCount", "detekt.LongMethod")
    private fun handleCall(callData: RPCCallMessage.CallData) {
        val callId = callData.callId

        val streamContext = LazyRPCStreamContext(streamScopeOrNull(scope)) {
            RPCStreamContext(callId, config, callData.connectionId, callData.serviceId, it)
        }
        val serialFormat = prepareSerialFormat(streamContext)

        val isMethod = when (callData.callType) {
            RPCCallMessage.CallType.Method -> true
            RPCCallMessage.CallType.Field -> false
            else -> callData.callableName
                .endsWith("\$method") // compatibility with beta-4.2 clients
        }

        val callableName = callData.callableName
            .substringBefore('$') // compatibility with beta-4.2 clients

        val callable = (if (isMethod) methods else fields)[callableName]

        if (callable == null) {
            val callType = if (isMethod) "method" else "field"
            throw NoSuchMethodException("Service $serviceTypeString has no $callType $callableName")
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

        var failure: Throwable? = null

        val requestJob = launch(start = CoroutineStart.LAZY) {
            val result = try {
                @Suppress("detekt.SpreadOperator")
                val value = when {
                    isMethod -> callScoped(callId) {
                        callable.callSuspend(service, *argsArray!!)
                    }

                    else -> callable.call(service)
                }

                val returnType = callable.returnType
                val returnSerializer = serialFormat.serializersModule.rpcSerializerForType(returnType)
                when (serialFormat) {
                    is StringFormat -> {
                        val stringValue = serialFormat.encodeToString(returnSerializer, value)
                        RPCCallMessage.CallSuccessString(
                            callId = callData.callId,
                            serviceType = serviceTypeString,
                            data = stringValue,
                            connectionId = callData.connectionId,
                            serviceId = callData.serviceId,
                        )
                    }

                    is BinaryFormat -> {
                        val binaryValue = serialFormat.encodeToByteArray(returnSerializer, value)
                        RPCCallMessage.CallSuccessBinary(
                            callId = callData.callId,
                            serviceType = serviceTypeString,
                            data = binaryValue,
                            connectionId = callData.connectionId,
                            serviceId = callData.serviceId,
                        )
                    }

                    else -> {
                        unsupportedSerialFormatError(serialFormat)
                    }
                }
            } catch (cause: InvocationTargetException) {
                if (cause.cause is CancellationException) {
                    throw cause.cause!!
                }

                failure = cause.cause ?: cause
                val serializedCause = serializeException(cause.cause ?: cause)
                RPCCallMessage.CallException(
                    callId = callId,
                    serviceType = serviceTypeString,
                    cause = serializedCause,
                    connectionId = callData.connectionId,
                    serviceId = callData.serviceId,
                )
            } catch (cause: CancellationException) {
                throw cause
            } catch (@Suppress("detekt.TooGenericExceptionCaught") cause: Throwable) {
                failure = cause

                val serializedCause = serializeException(cause)
                RPCCallMessage.CallException(
                    callId = callId,
                    serviceType = serviceTypeString,
                    cause = serializedCause,
                    connectionId = callData.connectionId,
                    serviceId = callData.serviceId,
                )
            }

            sender.sendMessage(result)

            if (failure == null) {
                streamContext.valueOrNull?.apply {
                    launchIf({ incomingHotFlowsAvailable }) {
                        handleIncomingHotFlows(it)
                    }

                    launchIf({ outgoingStreamsAvailable }) {
                        handleOutgoingStreams(it, serialFormat, serviceTypeString)
                    }
                } ?: run {
                    cancelRequest(callId, fromJob = true)
                }
            } else {
                cancelRequest(callId, "Server request failed", failure, fromJob = true)
            }
        }

        requestMap[callId] = RPCRequest(requestJob, streamContext)

        requestJob.start()
    }

    fun cancelRequest(callId: String, message: String? = null, cause: Throwable? = null, fromJob: Boolean = false) {
        requestMap.remove(callId)?.cancelAndClose(callId, message, cause, fromJob)
    }
}

private class RPCRequest(val handlerJob: Job, val streamContext: LazyRPCStreamContext) {
    fun cancelAndClose(
        callId: String,
        message: String? = null,
        cause: Throwable? = null,
        fromJob: Boolean = false,
    ) {
        if (!handlerJob.isCompleted && !fromJob) {
            when {
                message != null && cause != null -> handlerJob.cancel(message, cause)
                message != null -> handlerJob.cancel(message)
                else -> handlerJob.cancel()
            }
        }

        val ctx = streamContext.valueOrNull
        if (ctx == null) {
            streamContext.streamScopeOrNull?.cancelRequestScopeById(callId, message ?: "Scope cancelled", cause)
        } else {
            ctx.cancel(message ?: "Request cancelled", cause)
        }
    }
}
