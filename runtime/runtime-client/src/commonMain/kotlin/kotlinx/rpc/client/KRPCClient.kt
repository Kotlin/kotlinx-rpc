/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.client

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.rpc.*
import kotlinx.rpc.client.internal.FieldDataObject
import kotlinx.rpc.client.internal.RPCClientConnector
import kotlinx.rpc.client.internal.RPCFlow
import kotlinx.rpc.internal.*
import kotlinx.rpc.internal.logging.CommonLogger
import kotlinx.rpc.internal.logging.initialized
import kotlinx.rpc.internal.transport.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlin.coroutines.CoroutineContext
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
@OptIn(InternalCoroutinesApi::class)
public abstract class KRPCClient(
    final override val config: RPCConfig.Client,
    transport: RPCTransport,
) : RPCServiceHandler(), RPCClient, RPCEndpoint {
    // we make a child here, so we can send cancellation messages before closing the connection
    final override val coroutineContext: CoroutineContext = SupervisorJob(transport.coroutineContext.job)

    private val connector by lazy {
        RPCClientConnector(config.serialFormatInitializer.build(), transport, config.waitForServices)
    }

    private var connectionId: Long? = null

    @InternalRPCApi
    final override val sender: RPCMessageSender
        get() = connector

    private val callCounter = atomic(0L)

    override val logger: CommonLogger = CommonLogger.initialized().logger(objectId())

    private val serverSupportedPlugins: CompletableDeferred<Set<RPCPlugin>> = CompletableDeferred()

    @InternalRPCApi
    final override val supportedPlugins: Set<RPCPlugin>
        get() = serverSupportedPlugins.getOrNull() ?: emptySet()

    private var clientCancelled = false

    init {
        coroutineContext.job.invokeOnCompletion(onCancelling = true) {
            clientCancelled = true

            sendCancellation(CancellationType.ENDPOINT, null, null, closeTransportAfterSending = true)
        }

        launch {
            connector.subscribeToGenericMessages(::handleGenericMessage)
        }
    }

    @OptIn(InternalCoroutinesApi::class)
    @InternalRPCApi
    final override fun provideStubContext(serviceId: Long): CoroutineContext {
        val childContext = SupervisorJob(coroutineContext.job).withClientStreamScope()

        childContext.job.invokeOnCompletion(onCancelling = true) {
            if (!clientCancelled) {
                // cancellation only by serviceId
                sendCancellation(CancellationType.SERVICE, serviceId.toString(), null)
            }
        }

        return childContext
    }

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

    private fun handleProtocolMessage(message: RPCProtocolMessage) {
        when (message) {
            is RPCProtocolMessage.Handshake -> {
                connectionId = message.connectionId

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

    final override fun <T> registerPlainFlowField(serviceScope: CoroutineScope, field: RPCField): Flow<T> {
        return RPCFlow.Plain<T>(field.serviceTypeString, serviceScope.coroutineContext.job).also { rpcFlow ->
            serviceScope.initializeFlowField(rpcFlow, field)
        }
    }

    final override fun <T> registerSharedFlowField(serviceScope: CoroutineScope, field: RPCField): SharedFlow<T> {
        return RPCFlow.Shared<T>(field.serviceTypeString, serviceScope.coroutineContext.job).also { rpcFlow ->
            serviceScope.initializeFlowField(rpcFlow, field)
        }
    }

    final override fun <T> registerStateFlowField(serviceScope: CoroutineScope, field: RPCField): StateFlow<T> {
        return RPCFlow.State<T>(field.serviceTypeString, serviceScope.coroutineContext.job).also { rpcFlow ->
            serviceScope.initializeFlowField(rpcFlow, field)
        }
    }

    private fun <T, FlowT : Flow<T>> CoroutineScope.initializeFlowField(rpcFlow: RPCFlow<T, FlowT>, field: RPCField) {
        val call = RPCCall(
            serviceTypeString = field.serviceTypeString,
            serviceId = field.serviceId,
            callableName = field.name,
            type = RPCCall.Type.Field,
            data = FieldDataObject,
            dataType = typeOf<FieldDataObject>(),
            returnType = field.type,
        )

        /**
         * Launched on the service scope (receiver)
         * Moreover, this scope has [StreamScope] that is used to handle field streams.
         * [StreamScope] is provided to a service via [provideStubContext].
         */
        launch {
            val rpcCall = call(call, rpcFlow.deferred)

            rpcFlow.deferred.invokeOnCompletion { cause ->
                if (cause == null) {
                    rpcCall.streamContext.valueOrNull?.launchIf({ incomingHotFlowsAvailable }) {
                        handleIncomingHotFlows(it)
                    }
                }
            }
        }
    }

    final override suspend fun <T> call(call: RPCCall): T {
        val callCompletableResult = SupervisedCompletableDeferred<T>()
        val rpcCall = call(call, callCompletableResult)
        val result = callCompletableResult.await()

        // incomingHotFlowsAvailable value is known after await
        rpcCall.streamContext.valueOrNull?.launchIf({ incomingHotFlowsAvailable }) {
            handleIncomingHotFlows(it)
        }

        return result
    }

    private suspend fun <T> call(call: RPCCall, callResult: CompletableDeferred<T>): RPCCallStreamContextFormatAndId {
        val wrappedCallResult = RequestCompletableDeferred(callResult)
        val rpcCall = prepareAndExecuteCall(call, wrappedCallResult)

        rpcCall.streamContext.valueOrNull?.launchIf({ outgoingStreamsAvailable }) {
            handleOutgoingStreams(it, rpcCall.serialFormat, call.serviceTypeString)
        }

        callResult.invokeOnCompletion { cause ->
            if (cause != null) {
                connector.unsubscribeFromMessages(call.serviceTypeString, rpcCall.callId)

                rpcCall.streamContext.valueOrNull?.cancel("Request failed", cause)

                if (!wrappedCallResult.callExceptionOccurred) {
                    sendCancellation(CancellationType.REQUEST, call.serviceId.toString(), rpcCall.callId)
                }
            } else {
                val streamScope = rpcCall.streamContext.valueOrNull?.streamScope

                streamScope?.onScopeCompletion(rpcCall.callId) {
                    connector.unsubscribeFromMessages(call.serviceTypeString, rpcCall.callId)

                    sendCancellation(CancellationType.REQUEST, call.serviceId.toString(), rpcCall.callId)
                }
            }
        }

        return rpcCall
    }

    private suspend fun prepareAndExecuteCall(
        callInfo: RPCCall,
        callResult: RequestCompletableDeferred<*>,
    ): RPCCallStreamContextFormatAndId {
        // we should wait for the handshake to finish
        awaitHandshakeCompletion()

        val id = callCounter.incrementAndGet()

        val dataTypeString = callInfo.dataType.toString()

        val callId = "$connectionId:$dataTypeString:$id"

        logger.trace { "start a call[$callId] ${callInfo.callableName}" }

        val fallbackScope = serviceScopeOrNull()
            ?.serviceCoroutineScope
            ?.let { streamScopeOrNull(it) }

        val streamContext = LazyRPCStreamContext(streamScopeOrNull(), fallbackScope) {
            RPCStreamContext(callId, config, connectionId, callInfo.serviceId, it)
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
            callResult = callResult as RequestCompletableDeferred<Any?>
        )

        return RPCCallStreamContextFormatAndId(streamContext, serialFormat, callId)
    }

    private data class RPCCallStreamContextFormatAndId(
        val streamContext: LazyRPCStreamContext,
        val serialFormat: SerialFormat,
        val callId: String,
    )

    private suspend fun executeCall(
        callId: String,
        streamContext: LazyRPCStreamContext,
        call: RPCCall,
        firstMessage: RPCCallMessage,
        serialFormat: SerialFormat,
        callResult: RequestCompletableDeferred<Any?>,
    ) {
        connector.subscribeToCallResponse(call.serviceTypeString, callId) { message ->
            handleMessage(message, streamContext, call, serialFormat, callResult)
        }

        connector.sendMessage(firstMessage)
    }

    private suspend fun handleMessage(
        message: RPCCallMessage,
        streamContext: LazyRPCStreamContext,
        callInfo: RPCCall,
        serialFormat: SerialFormat,
        callResult: RequestCompletableDeferred<Any?>,
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

                callResult.callExceptionOccurred = true
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

    @InternalRPCApi
    final override fun handleCancellation(message: RPCGenericMessage) {
        when (val type = message.cancellationType()) {
            CancellationType.ENDPOINT -> {
                cancel("Closing client after server cancellation") // we cancel this client
            }

            else -> {
                error(
                    "Unsupported ${RPCPluginKey.CANCELLATION_TYPE} $type for client, " +
                            "only 'endpoint' type may be sent by a server"
                )
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

private class RequestCompletableDeferred<T>(delegate: CompletableDeferred<T>) : CompletableDeferred<T> by delegate {
    var callExceptionOccurred: Boolean = false
}
