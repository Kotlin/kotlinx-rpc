/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import org.jetbrains.krpc.*
import org.jetbrains.krpc.client.internal.FieldDataObject
import org.jetbrains.krpc.client.internal.RPCClientConnector
import org.jetbrains.krpc.client.internal.RPCFlow
import org.jetbrains.krpc.internal.*
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.transport.*
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates
import kotlin.reflect.typeOf

/**
 * Default implementation of [RPCClient].
 * Takes care of tracking requests and responses,
 * serializing data, tracking streams, processing exceptions, and other protocol responsibilities.
 * Leaves out the delivery of encoded messages to the specific implementations.
 *
 * A simple example of how this client may be implemented:
 * ```kotlin
 * class MyTransport : RPCTransport { /*...*/ }
 *
 * class MyClient(config: RPCConfig.Client): KRPCClient(config, MyTransport())
 * ```
 *
 * @property config configuration provided for that specific client. Applied to all services that use this client.
 * @param transport [RPCTransport] instance that will be used to send and receive RPC messages.
 * IMPORTANT: Must be exclusive to this client, otherwise unexpected behavior may occur.
 */
public abstract class KRPCClient(
    final override val config: RPCConfig.Client,
    transport: RPCTransport,
) : RPCEndpointBase(), RPCClient {
    final override val coroutineContext: CoroutineContext = transport.coroutineContext

    private val connector by lazy {
        RPCClientConnector(config.serialFormatInitializer.build(), transport, config.waitForServices)
    }

    private var connectionId: Long by Delegates.notNull()

    override val sender: RPCMessageSender
        get() = connector

    private val callCounter = atomic(0L)

    override val logger: CommonLogger = CommonLogger.initialized().logger(objectId())

    private val serverSupportedPlugins: CompletableDeferred<Set<RPCPlugin>> = CompletableDeferred()

    private val initHandshake: Job = launch {
        connector.sendMessage(RPCProtocolMessage.Handshake(RPCPlugin.ALL))

        connector.subscribeToProtocolMessages(::handleProtocolMessage)
    }

    /**
     * Starts the handshake process and awaits for completion.
     * If the handshake was completed before, nothing happens.
     */
    private suspend fun awaitHandshakeCompletion() {
        initHandshake.join()
        serverSupportedPlugins.await()
    }

    private suspend fun handleProtocolMessage(message: RPCProtocolMessage) {
        when (message) {
            is RPCProtocolMessage.Handshake -> {
                connectionId = message.connectionId ?: run {
                    val failure = "Server sent null connectionId"

                    connector.sendMessage(RPCProtocolMessage.Failure(failure, failedMessage = message))
                    serverSupportedPlugins.completeExceptionally(IllegalStateException(failure))
                    return
                }

                serverSupportedPlugins.complete(message.supportedPlugins)
            }

            is RPCProtocolMessage.Failure -> {
                logger.error {
                    "Server [${message.connectionId}] failed to process protocol message ${message.failedMessage}: " +
                            message.errorMessage
                }

                serverSupportedPlugins.completeExceptionally(
                    IllegalStateException("Server failed to process protocol message: ${message.failedMessage}")
                )
            }
        }
    }

    final override fun <T> registerPlainFlowField(field: RPCField): Flow<T> {
        return RPCFlow.Plain<T>(field.serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, field)
        }
    }

    final override fun <T> registerSharedFlowField(field: RPCField): SharedFlow<T> {
        return RPCFlow.Shared<T>(field.serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, field)
        }
    }

    final override fun <T> registerStateFlowField(field: RPCField): StateFlow<T> {
        return RPCFlow.State<T>(field.serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, field)
        }
    }

    private fun <T, FlowT : Flow<T>> initializeFlowField(rpcFlow: RPCFlow<T, FlowT>, field: RPCField) {
        val call = RPCCall(
            serviceTypeString = field.serviceTypeString,
            serviceId = field.serviceId,
            callableName = field.name,
            type = RPCCall.Type.Field,
            data = FieldDataObject,
            dataType = typeOf<FieldDataObject>(),
            returnType = field.type,
        )

        launch {
            call(call, rpcFlow.deferred)
        }
    }

    final override suspend fun <T> call(call: RPCCall): T {
        return CompletableDeferred<T>().also { result -> call(call, result) }.await()
    }

    private suspend fun <T> call(call: RPCCall, callResult: CompletableDeferred<T>) {
        val (callContext, serialFormat) = prepareAndExecuteCall(call, callResult)

        launch {
            handleOutgoingStreams(this, callContext, serialFormat, call.serviceTypeString)
        }

        launch {
            handleIncomingHotFlows(this, callContext)
        }
    }

    private suspend fun prepareAndExecuteCall(
        callInfo: RPCCall,
        callResult: CompletableDeferred<*>,
    ): Pair<LazyRPCStreamContext, SerialFormat> {
        // we should init and await for handshake to finish
        awaitHandshakeCompletion()

        val id = callCounter.incrementAndGet()
        val callId = "$connectionId:${callInfo.dataType}:$id"

        logger.trace { "start a call[$callId] ${callInfo.callableName}" }

        val streamContext = LazyRPCStreamContext {
            RPCStreamContext(callId, config, connectionId, callInfo.serviceId)
        }
        val serialFormat = prepareSerialFormat(streamContext)
        val firstMessage = serializeRequest(callId, callInfo, serialFormat)

        @Suppress("UNCHECKED_CAST")
        executeCall(
            callId = callId,
            streamContext = streamContext,
            call = callInfo,
            firstMessage = firstMessage,
            serialFormat = serialFormat,
            callResult = callResult as CompletableDeferred<Any?>
        )

        return streamContext to serialFormat
    }

    private suspend fun executeCall(
        callId: String,
        streamContext: LazyRPCStreamContext,
        call: RPCCall,
        firstMessage: RPCCallMessage,
        serialFormat: SerialFormat,
        callResult: CompletableDeferred<Any?>,
    ) {
        connector.subscribeToCallResponse(call.serviceTypeString, callId) { message ->
            handleMessage(message, streamContext, call, serialFormat, callResult)
        }

        coroutineContext.job.invokeOnCompletion {
            streamContext.valueOrNull?.close()
        }

        connector.sendMessage(firstMessage)
    }

    private suspend fun handleMessage(
        message: RPCCallMessage,
        streamContext: LazyRPCStreamContext,
        callInfo: RPCCall,
        serialFormat: SerialFormat,
        callResult: CompletableDeferred<Any?>,
    ) {
        when (message) {
            is RPCCallMessage.CallData -> {
                error("Unexpected message")
            }

            is RPCCallMessage.CallException -> {
                val cause = runCatching {
                    message.cause.deserialize()
                }

                val result = if (cause.isFailure) {
                    cause.exceptionOrNull()!!
                } else {
                    cause.getOrNull()!!
                }

                callResult.completeExceptionally(result)
            }

            is RPCCallMessage.CallSuccess -> {
                val value = runCatching {
                    val serializerResult = serialFormat.serializersModule.rpcSerializerForType(callInfo.returnType)

                    decodeMessageData(serialFormat, serializerResult, message)
                }

                callResult.completeWith(value)
            }

            is RPCCallMessage.StreamCancel -> {
                streamContext.awaitInitialized().cancelStream(message)
            }

            is RPCCallMessage.StreamFinished -> {
                streamContext.awaitInitialized().closeStream(message)
            }

            is RPCCallMessage.StreamMessage -> {
                streamContext.awaitInitialized().send(message, serialFormat)
            }
        }
    }

    private fun serializeRequest(callId: String, call: RPCCall, serialFormat: SerialFormat): RPCCallMessage {
        val serializerData = serialFormat.serializersModule.rpcSerializerForType(call.dataType)
        return when (serialFormat) {
            is StringFormat -> {
                val stringValue = serialFormat.encodeToString(serializerData, call.data)
                RPCCallMessage.CallDataString(
                    callId = callId,
                    serviceType = call.serviceTypeString,
                    callableName = call.callableName,
                    callType = call.type.toMessageCallType(),
                    data = stringValue,
                    connectionId = connectionId,
                    serviceId = call.serviceId,
                )
            }

            is BinaryFormat -> {
                val binaryValue = serialFormat.encodeToByteArray(serializerData, call.data)
                RPCCallMessage.CallDataBinary(
                    callId = callId,
                    serviceType = call.serviceTypeString,
                    callableName = call.callableName,
                    callType = call.type.toMessageCallType(),
                    data = binaryValue,
                    connectionId = connectionId,
                    serviceId = call.serviceId,
                )
            }

            else -> {
                unsupportedSerialFormatError(serialFormat)
            }
        }
    }
}
