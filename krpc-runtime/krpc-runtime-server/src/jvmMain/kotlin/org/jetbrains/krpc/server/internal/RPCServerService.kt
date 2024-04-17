/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

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
import org.jetbrains.krpc.internal.transport.RPCCallMessage
import org.jetbrains.krpc.internal.transport.RPCEndpointBase
import org.jetbrains.krpc.internal.transport.RPCMessageSender
import org.jetbrains.krpc.internal.transport.RPCPlugin
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
    override val sender: RPCMessageSender,
    @Suppress("detekt.UnusedPrivateProperty")
    private val clientPlugins: (connectionId: Long?) -> Set<RPCPlugin>,
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

    suspend fun accept(message: RPCCallMessage) {
        val result = runCatching {
            val callId = message.callId
            logger.trace { "Incoming message $message" }

            when (message) {
                is RPCCallMessage.CallData -> {
                    handleCall(message)
                }

                is RPCCallMessage.CallException -> {
                    calls[callId]?.cancel()
                }

                is RPCCallMessage.CallSuccess -> {
                    error("Unexpected success message")
                }

                is RPCCallMessage.StreamCancel -> {
                    val streamContext = streamContexts[callId] ?: error("Unknown call $callId")
                    streamContext.awaitInitialized().cancelStream(message)
                }

                is RPCCallMessage.StreamFinished -> {
                    val streamContext = streamContexts[callId] ?: error("Unknown call $callId")
                    streamContext.awaitInitialized().closeStream(message)
                }

                is RPCCallMessage.StreamMessage -> {
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
    private fun handleCall(callData: RPCCallMessage.CallData) {
        val streamContext = LazyRPCStreamContext {
            RPCStreamContext(callData.callId, config, callData.connectionId, callData.serviceId)
        }
        val serialFormat = prepareSerialFormat(streamContext)
        streamContexts[callData.callId] = streamContext

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
            val cause = NoSuchMethodException("Service $serviceTypeString has no $callType $callableName")
            val message = RPCCallMessage.CallException(
                callId = callData.callId,
                serviceType = serviceTypeString,
                cause = serializeException(cause),
                connectionId = callData.connectionId,
                serviceId = callData.serviceId,
            )
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

        calls[callData.callId] = launch {
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
                val serializedCause = serializeException(cause.cause ?: cause)
                RPCCallMessage.CallException(
                    callId = callData.callId,
                    serviceType = serviceTypeString,
                    cause = serializedCause,
                    connectionId = callData.connectionId,
                    serviceId = callData.serviceId,
                )
            } catch (@Suppress("detekt.TooGenericExceptionCaught") cause: Throwable) {
                val serializedCause = serializeException(cause)
                RPCCallMessage.CallException(
                    callId = callData.callId,
                    serviceType = serviceTypeString,
                    cause = serializedCause,
                    connectionId = callData.connectionId,
                    serviceId = callData.serviceId,
                )
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
                calls.remove(callData.callId)
                streamContext.valueOrNull?.close()
            }
        }
    }
}
