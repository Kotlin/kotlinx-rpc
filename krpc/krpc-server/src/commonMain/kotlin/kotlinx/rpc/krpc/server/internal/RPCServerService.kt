/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server.internal

import kotlinx.coroutines.*
import kotlinx.rpc.RemoteService
import kotlinx.rpc.descriptor.RpcInvokator
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.internal.utils.map.ConcurrentHashMap
import kotlinx.rpc.krpc.RPCConfig
import kotlinx.rpc.krpc.callScoped
import kotlinx.rpc.krpc.internal.*
import kotlinx.rpc.krpc.internal.logging.CommonLogger
import kotlinx.rpc.krpc.streamScopeOrNull
import kotlinx.rpc.krpc.withServerStreamScope
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.StringFormat
import kotlin.coroutines.CoroutineContext

internal class RPCServerService<T : RemoteService>(
    private val service: T,
    private val descriptor: RpcServiceDescriptor<T>,
    override val config: RPCConfig.Server,
    private val connector: RPCServerConnector,
    coroutineContext: CoroutineContext,
) : RPCServiceHandler(), CoroutineScope {
    override val logger = CommonLogger.logger(objectId(descriptor.fqName))
    override val sender: RPCMessageSender get() = connector
    private val scope: CoroutineScope = this

    override val coroutineContext: CoroutineContext = coroutineContext.withServerStreamScope()

    private val requestMap = ConcurrentHashMap<String, RPCRequest>()

    init {
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

        val callable = descriptor.getCallable(callableName)

        if (callable == null || callable.invokator is RpcInvokator.Method && !isMethod) {
            val callType = if (isMethod) "method" else "field"
            error("Service ${descriptor.fqName} has no $callType '$callableName'")
        }

        val data = if (isMethod) {
            val serializerModule = serialFormat.serializersModule
            val paramsSerializer = serializerModule.rpcSerializerForType(callable.dataType)
            decodeMessageData(serialFormat, paramsSerializer, callData)
        } else {
            null
        }

        var failure: Throwable? = null

        val requestJob = launch(start = CoroutineStart.LAZY) {
            val result = try {
                @Suppress("detekt.SpreadOperator")
                val value = when (val invokator = callable.invokator) {
                    is RpcInvokator.Method -> callScoped(callId) {
                        invokator.call(service, data)
                    }

                    is RpcInvokator.Field -> {
                        invokator.call(service)
                    }
                }

                val returnType = callable.returnType
                val returnSerializer = serialFormat.serializersModule.rpcSerializerForType(returnType)
                when (serialFormat) {
                    is StringFormat -> {
                        val stringValue = serialFormat.encodeToString(returnSerializer, value)
                        RPCCallMessage.CallSuccessString(
                            callId = callData.callId,
                            serviceType = descriptor.fqName,
                            data = stringValue,
                            connectionId = callData.connectionId,
                            serviceId = callData.serviceId,
                        )
                    }

                    is BinaryFormat -> {
                        val binaryValue = serialFormat.encodeToByteArray(returnSerializer, value)
                        RPCCallMessage.CallSuccessBinary(
                            callId = callData.callId,
                            serviceType = descriptor.fqName,
                            data = binaryValue,
                            connectionId = callData.connectionId,
                            serviceId = callData.serviceId,
                        )
                    }

                    else -> {
                        unsupportedSerialFormatError(serialFormat)
                    }
                }
            } catch (cause: CancellationException) {
                throw cause
            } catch (@Suppress("detekt.TooGenericExceptionCaught") cause: Throwable) {
                failure = cause

                val serializedCause = serializeException(cause)
                RPCCallMessage.CallException(
                    callId = callId,
                    serviceType = descriptor.fqName,
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
                        handleOutgoingStreams(it, serialFormat, descriptor.fqName)
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

    suspend fun cancelRequest(
        callId: String,
        message: String? = null,
        cause: Throwable? = null,
        fromJob: Boolean = false,
    ) {
        requestMap.remove(callId)?.cancelAndClose(callId, message, cause, fromJob)

        // acknowledge the cancellation
        sender.sendMessage(
            RPCGenericMessage(
                connectionId = null,
                pluginParams = mapOf(
                    RPCPluginKey.GENERIC_MESSAGE_TYPE to RPCGenericMessage.CANCELLATION_TYPE,
                    RPCPluginKey.CANCELLATION_TYPE to CancellationType.CANCELLATION_ACK.toString(),
                    RPCPluginKey.CANCELLATION_ID to callId,
                )
            )
        )
    }
}

private class RPCRequest(val handlerJob: Job, val streamContext: LazyRPCStreamContext) {
    suspend fun cancelAndClose(
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

            handlerJob.join()
        }

        val ctx = streamContext.valueOrNull
        if (ctx == null) {
            streamContext.streamScopeOrNull
                ?.cancelRequestScopeById(callId, message ?: "Scope cancelled", cause)
                ?.join()
        } else {
            ctx.cancel(message ?: "Request cancelled", cause)?.join()
        }
    }
}
