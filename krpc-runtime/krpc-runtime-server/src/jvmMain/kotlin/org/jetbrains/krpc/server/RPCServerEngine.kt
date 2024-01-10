/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.server

import kotlinx.coroutines.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.*
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.map.ConcurrentHashMap
import org.jetbrains.krpc.server.internal.rpcServiceMethodSerializationTypeOf
import java.lang.reflect.InvocationTargetException
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.callSuspend

private const val HEX_RADIX = 16

/**
 * Server engine for the provided RPC [service].
 * Handles incoming messages from [transport] and routes them to the provided [service].
 * Handles requests made via [service] and routes them to the [transport]
 *
 * Server engine is also in change of serialization, exception handling and stream management.
 */
class RPCServerEngine<T : RPC>(
    private val service: T,
    override val transport: RPCTransport,
    private val serviceKClass: KClass<T>,
    override val config: RPCConfig.Server = RPCConfig.Server.Default,
) : RPCEngine(), CoroutineScope {
    override val serviceTypeString = serviceKClass.toString()
    override val logger = CommonLogger.initialized()
        .logger("RPCServerEngine[$serviceTypeString][0x${hashCode().toString(HEX_RADIX)}]")

    private val methods: Map<String, KCallable<*>>
    private val fields: Map<String, KCallable<*>>

    override val coroutineContext: CoroutineContext = transport.coroutineContext + Job(transport.coroutineContext.job)

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

            result.getOrNull() == true
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    private fun handleCall(callId: String, callData: RPCMessage.CallData) {
        val streamContext = LazyRPCStreamContext { RPCStreamContext(callId, config) }
        val serialFormat = prepareSerialFormat(streamContext)
        streamContexts[callId] = streamContext
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
            transport.send(result)

            launch {
                handleOutgoingStreams(this, streamContext, serialFormat)
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

    fun stop() {
        coroutineContext.cancel()
    }
}
