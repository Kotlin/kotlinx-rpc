/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.internal.serviceScopeOrNull
import kotlinx.rpc.internal.utils.InternalRPCApi
import kotlinx.rpc.internal.utils.SupervisedCompletableDeferred
import kotlinx.rpc.internal.utils.getOrNull
import kotlinx.rpc.internal.utils.map.ConcurrentHashMap
import kotlinx.rpc.krpc.*
import kotlinx.rpc.krpc.client.internal.RPCClientConnector
import kotlinx.rpc.krpc.internal.*
import kotlinx.rpc.krpc.internal.logging.CommonLogger
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * Default implementation of [RpcClient].
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
) : RPCServiceHandler(), RpcClient, RPCEndpoint {
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

    override val logger: CommonLogger = CommonLogger.logger(objectId())

    private val serverSupportedPlugins: CompletableDeferred<Set<RPCPlugin>> = CompletableDeferred()

    @InternalRPCApi
    final override val supportedPlugins: Set<RPCPlugin>
        get() = serverSupportedPlugins.getOrNull() ?: emptySet()

    private var clientCancelled = false

    // callId to serviceTypeString
    private val cancellingRequests = ConcurrentHashMap<String, String>()

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

    override fun <T> callAsync(
        serviceScope: CoroutineScope,
        call: RpcCall,
    ): Deferred<T> {
        val callable = call.descriptor.getCallable(call.callableName)
            ?: error("Unexpected callable '${call.callableName}' for ${call.descriptor.fqName} service")

        val deferred = SupervisedCompletableDeferred<T>(serviceScope.coroutineContext.job)

        /**
         * Launched on the service scope (receiver)
         * Moreover, this scope has [StreamScope] that is used to handle field streams.
         * [StreamScope] is provided to a service via [provideStubContext].
         */
        serviceScope.launch {
            val rpcCall = call(call, callable, deferred)

            deferred.invokeOnCompletion { cause ->
                if (cause == null) {
                    rpcCall.streamContext.valueOrNull?.launchIf({ incomingHotFlowsAvailable }) {
                        handleIncomingHotFlows(it)
                    }
                }
            }
        }

        return deferred
    }

    final override suspend fun <T> call(call: RpcCall): T {
        val callable = call.descriptor.getCallable(call.callableName)
            ?: error("Unexpected callable '${call.callableName}' for ${call.descriptor.fqName} service")

        val callCompletableResult = SupervisedCompletableDeferred<T>()
        val rpcCall = call(call, callable, callCompletableResult)
        val result = callCompletableResult.await()

        // incomingHotFlowsAvailable value is known after await
        rpcCall.streamContext.valueOrNull?.launchIf({ incomingHotFlowsAvailable }) {
            handleIncomingHotFlows(it)
        }

        return result
    }

    private suspend fun <T> call(
        call: RpcCall,
        callable: RpcCallable<*>,
        callResult: CompletableDeferred<T>,
    ): RPCCallStreamContextFormatAndId {
        val wrappedCallResult = RequestCompletableDeferred(callResult)
        val rpcCall = prepareAndExecuteCall(call, callable, wrappedCallResult)

        rpcCall.streamContext.valueOrNull?.launchIf({ outgoingStreamsAvailable }) {
            handleOutgoingStreams(it, rpcCall.serialFormat, call.descriptor.fqName)
        }

        val handle = serviceScopeOrNull()?.run {
            serviceCoroutineScope.coroutineContext.job.invokeOnCompletion(onCancelling = true) { cause ->
                // service can only be canceled, it can't complete successfully
                callResult.completeExceptionally(CancellationException(cause))

                rpcCall.streamContext.valueOrNull?.cancel("Service cancelled", cause)
            }
        }

        callResult.invokeOnCompletion { cause ->
            if (cause != null) {
                cancellingRequests[rpcCall.callId] = call.descriptor.fqName

                rpcCall.streamContext.valueOrNull?.cancel("Request failed", cause)

                if (!wrappedCallResult.callExceptionOccurred) {
                    sendCancellation(CancellationType.REQUEST, call.serviceId.toString(), rpcCall.callId)
                }

                handle?.dispose()
            } else {
                val streamScope = rpcCall.streamContext.valueOrNull?.streamScope

                if (streamScope == null) {
                    handle?.dispose()

                    connector.unsubscribeFromMessages(call.descriptor.fqName, rpcCall.callId)
                }

                streamScope?.onScopeCompletion(rpcCall.callId) {
                    handle?.dispose()

                    cancellingRequests[rpcCall.callId] = call.descriptor.fqName

                    sendCancellation(CancellationType.REQUEST, call.serviceId.toString(), rpcCall.callId)
                }
            }
        }

        return rpcCall
    }

    private suspend fun prepareAndExecuteCall(
        call: RpcCall,
        callable: RpcCallable<*>,
        callResult: RequestCompletableDeferred<*>,
    ): RPCCallStreamContextFormatAndId {
        // we should wait for the handshake to finish
        awaitHandshakeCompletion()

        val id = callCounter.incrementAndGet()

        val dataTypeString = callable.dataType.toString()

        val callId = "$connectionId:$dataTypeString:$id"

        logger.trace { "start a call[$callId] ${callable.name}" }

        val fallbackScope = serviceScopeOrNull()
            ?.serviceCoroutineScope
            ?.let { streamScopeOrNull(it) }

        val streamContext = LazyRPCStreamContext(streamScopeOrNull(), fallbackScope) {
            RPCStreamContext(callId, config, connectionId, call.serviceId, it)
        }
        val serialFormat = prepareSerialFormat(streamContext)
        val firstMessage = serializeRequest(callId, call, callable, serialFormat)

        @Suppress("UNCHECKED_CAST")
        executeCall(
            callId = callId,
            streamContext = streamContext,
            call = call,
            callable = callable,
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
        call: RpcCall,
        callable: RpcCallable<*>,
        firstMessage: RPCCallMessage,
        serialFormat: SerialFormat,
        callResult: RequestCompletableDeferred<Any?>,
    ) {
        connector.subscribeToCallResponse(call.descriptor.fqName, callId) { message ->
            if (cancellingRequests.containsKey(callId)) {
                return@subscribeToCallResponse
            }

            handleMessage(message, streamContext, callable, serialFormat, callResult)
        }

        connector.sendMessage(firstMessage)
    }

    private suspend fun handleMessage(
        message: RPCCallMessage,
        streamContext: LazyRPCStreamContext,
        callable: RpcCallable<*>,
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
                    val serializerResult = serialFormat.serializersModule.rpcSerializerForType(callable.returnType)

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
    final override suspend fun handleCancellation(message: RPCGenericMessage) {
        when (val type = message.cancellationType()) {
            CancellationType.ENDPOINT -> {
                cancel("Closing client after server cancellation") // we cancel this client
            }

            CancellationType.CANCELLATION_ACK -> {
                val callId = message[RPCPluginKey.CANCELLATION_ID]
                    ?: error("Expected CANCELLATION_ID for cancellation of type 'request'")

                val serviceTypeString = cancellingRequests.remove(callId) ?: return
                connector.unsubscribeFromMessages(serviceTypeString, callId)
            }

            else -> {
                logger.warn {
                    "Unsupported ${RPCPluginKey.CANCELLATION_TYPE} $type for client, " +
                            "only 'endpoint' type may be sent by a server"
                }
            }
        }
    }

    private fun serializeRequest(
        callId: String,
        call: RpcCall,
        callable: RpcCallable<*>,
        serialFormat: SerialFormat,
    ): RPCCallMessage {
        val serializerData = serialFormat.serializersModule.rpcSerializerForType(callable.dataType)
        return when (serialFormat) {
            is StringFormat -> {
                val stringValue = serialFormat.encodeToString(serializerData, call.data)
                RPCCallMessage.CallDataString(
                    callId = callId,
                    serviceType = call.descriptor.fqName,
                    callableName = call.callableName,
                    callType = callable.toMessageCallType(),
                    data = stringValue,
                    connectionId = connectionId,
                    serviceId = call.serviceId,
                )
            }

            is BinaryFormat -> {
                val binaryValue = serialFormat.encodeToByteArray(serializerData, call.data)
                RPCCallMessage.CallDataBinary(
                    callId = callId,
                    serviceType = call.descriptor.fqName,
                    callableName = call.callableName,
                    callType = callable.toMessageCallType(),
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
