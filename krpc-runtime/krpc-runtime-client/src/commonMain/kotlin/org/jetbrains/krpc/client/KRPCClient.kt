/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.completeWith
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import org.jetbrains.krpc.*
import org.jetbrains.krpc.client.internal.FieldDataObject
import org.jetbrains.krpc.client.internal.RPCClientConnector
import org.jetbrains.krpc.client.internal.RPCFlow
import org.jetbrains.krpc.internal.*
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.DumpLogger
import org.jetbrains.krpc.internal.logging.DumpLoggerNoop
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.transport.*
import kotlin.reflect.typeOf

private val CONNECTION_ID = atomic(initial = 0L)

/**
 * Default implementation of [RPCClient].
 * Takes care of tracking requests and responses,
 * serializing data, tracking streams, processing exceptions and other protocol responsibilities.
 * Leaves out the delivery of encoded messages to the specific implementations.
 *
 *  * Simple example, how this client may be implemented:
 *  * ```kotlin
 *  * class MyTransport : RPCTransport { /*...*/ }
 *  *
 *  * class MyClient(
 *  *     config: RPCConfig.Client,
 *  *     override val coroutineContext: CoroutineContext,
 *  * ): KRPCClient(config), RPCTransport by MyTransport()
 *
 * @property config configuration provided for that specific client. Applied to all services that use this client.
 */
public abstract class KRPCClient(
    final override val config: RPCConfig.Client
) : RPCEndpointBase(), RPCClient, RPCTransport {
    @InternalKRPCApi
    protected open val dumpLogger: DumpLogger = DumpLoggerNoop

    private val connector by lazy {
        RPCClientConnector(config.serialFormatInitializer.build(), this, config.waitForServices, dumpLogger)
    }

    private val connectionId: Long = CONNECTION_ID.incrementAndGet()

    override val sender: RPCMessageSender
        get() = connector

    private val callCounter = atomic(0L)

    override val logger: CommonLogger = CommonLogger.initialized().logger(objectId(connectionId.toString()))

    private val serverSupportedPlugins: CompletableDeferred<Set<RPCPlugin>> = CompletableDeferred()

    private val initHandshake by lazy {
        launch {
            connector.sendMessage(RPCProtocolMessage.Handshake(connectionId, RPCPlugin.ALL))

            connector.subscribeToProtocolMessages(::handleProtocolMessage)
        }
    }

    private fun handleProtocolMessage(message: RPCProtocolMessage) {
        when (message) {
            is RPCProtocolMessage.Handshake -> {
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

    override fun <T> registerPlainFlowField(field: RPCField): Flow<T> {
        return RPCFlow.Plain<T>(field.serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, field)
        }
    }

    override fun <T> registerSharedFlowField(field: RPCField): SharedFlow<T> {
        return RPCFlow.Shared<T>(field.serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, field)
        }
    }

    override fun <T> registerStateFlowField(field: RPCField): StateFlow<T> {
        return RPCFlow.State<T>(field.serviceTypeString).also { rpcFlow ->
            initializeFlowField(rpcFlow, field)
        }
    }

    private fun <T, FlowT : Flow<T>> initializeFlowField(rpcFlow: RPCFlow<T, FlowT>, field: RPCField) {
        val call = RPCCall(
            serviceTypeString = field.serviceTypeString,
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

    override suspend fun <T> call(call: RPCCall): T {
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
        initHandshake.join()
        serverSupportedPlugins.await()

        val id = callCounter.incrementAndGet()
        val callId = "$connectionId:${callInfo.dataType}:$id"

        logger.trace { "start a call[$callId] ${callInfo.callableName}" }

        val streamContext = LazyRPCStreamContext { RPCStreamContext(callId, config, connectionId) }
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
        firstMessage: RPCMessage,
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
        message: RPCMessage,
        streamContext: LazyRPCStreamContext,
        callInfo: RPCCall,
        serialFormat: SerialFormat,
        callResult: CompletableDeferred<Any?>,
    ) {
        when (message) {
            is RPCMessage.CallData -> {
                error("Unexpected message")
            }

            is RPCMessage.CallException -> {
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

            is RPCMessage.CallSuccess -> {
                val value = runCatching {
                    val serializerResult = serialFormat.serializersModule.rpcSerializerForType(callInfo.returnType)

                    decodeMessageData(serialFormat, serializerResult, message)
                }

                callResult.completeWith(value)
            }

            is RPCMessage.StreamCancel -> {
                streamContext.awaitInitialized().cancelStream(message)
            }

            is RPCMessage.StreamFinished -> {
                streamContext.awaitInitialized().closeStream(message)
            }

            is RPCMessage.StreamMessage -> {
                streamContext.awaitInitialized().send(message, serialFormat)
            }
        }
    }

    private fun serializeRequest(callId: String, call: RPCCall, serialFormat: SerialFormat): RPCMessage {
        val serializerData = serialFormat.serializersModule.rpcSerializerForType(call.dataType)
        return when (serialFormat) {
            is StringFormat -> {
                val stringValue = serialFormat.encodeToString(serializerData, call.data)
                RPCMessage.CallDataString(
                    callId = callId,
                    serviceType = call.serviceTypeString,
                    callableName = call.callableName,
                    callType = call.type.toMessageCallType(),
                    data = stringValue,
                    connectionId = connectionId,
                )
            }

            is BinaryFormat -> {
                val binaryValue = serialFormat.encodeToByteArray(serializerData, call.data)
                RPCMessage.CallDataBinary(
                    callId = callId,
                    serviceType = call.serviceTypeString,
                    callableName = call.callableName,
                    callType = call.type.toMessageCallType(),
                    data = binaryValue,
                    connectionId = connectionId,
                )
            }

            else -> {
                unsupportedSerialFormatError(serialFormat)
            }
        }
    }
}
