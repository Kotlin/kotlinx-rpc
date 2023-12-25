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
import org.jetbrains.krpc.RPCField
import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.client.internal.FieldDataObject
import org.jetbrains.krpc.client.internal.RPCClientConnector
import org.jetbrains.krpc.client.internal.RPCFlow
import org.jetbrains.krpc.internal.*
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.transport.RPCMessage
import org.jetbrains.krpc.internal.transport.RPCMessageSender
import org.jetbrains.krpc.internal.transport.RPCEndpointBase
import org.jetbrains.krpc.RPCTransport
import kotlin.reflect.typeOf

private val CLIENT_ENGINE_ID = atomic(initial = 0L)

/**
 * Default implementation of [RPCClient].
 * Takes care of tracking requests and responses,
 * serializing data, tracking streams, processing exceptions and other protocol responsibilities.
 * Leaves out the delivery of encoded messages to the specific implementations.
 *
 * @property config configuration provided for that specific client. Applied to all services that use this client.
 */
abstract class KRPCClient(
    final override val config: RPCConfig.Client
) : RPCEndpointBase(), RPCClient, RPCTransport {
    private val connector by lazy {
        RPCClientConnector(config.serialFormatInitializer.build(), this, config.waitForServices)
    }

    override val sender: RPCMessageSender
        get() = connector

    private val callCounter = atomic(0L)
    private val engineId: Long = CLIENT_ENGINE_ID.incrementAndGet()
    override val logger = CommonLogger.initialized().logger(objectId())

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
        val id = callCounter.incrementAndGet()
        val callId = "$engineId:${callInfo.dataType}:$id"

        logger.trace { "start a call[$callId] ${callInfo.callableName}" }

        val streamContext = LazyRPCStreamContext { RPCStreamContext(callId, config) }
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
                    data = stringValue
                )
            }

            is BinaryFormat -> {
                val binaryValue = serialFormat.encodeToByteArray(serializerData, call.data)
                RPCMessage.CallDataBinary(
                    callId = callId,
                    serviceType = call.serviceTypeString,
                    callableName = call.callableName,
                    callType = call.type.toMessageCallType(),
                    data = binaryValue
                )
            }

            else -> {
                unsupportedSerialFormatError(serialFormat)
            }
        }
    }
}
