/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.internal.serviceScopeOrNull
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.SupervisedCompletableDeferred
import kotlinx.rpc.internal.utils.getOrNull
import kotlinx.rpc.internal.utils.map.ConcurrentHashMap
import kotlinx.rpc.krpc.*
import kotlinx.rpc.krpc.client.internal.KrpcClientConnector
import kotlinx.rpc.krpc.internal.*
import kotlinx.rpc.krpc.internal.logging.CommonLogger
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.error

@Deprecated("Use KrpcClient instead", ReplaceWith("KrpcClient"), level = DeprecationLevel.ERROR)
public typealias KRPCClient = KrpcClient

/**
 * Default implementation of [RpcClient].
 * Takes care of tracking requests and responses,
 * serializing data, tracking streams, processing exceptions, and other protocol responsibilities.
 * Leaves out the delivery of encoded messages to the specific implementations.
 *
 * A simple example of how this client may be implemented:
 * ```kotlin
 * class MyTransport : RpcTransport { /*...*/ }
 *
 * class MyClient(config: RpcConfig.Client): KrpcClient(config, MyTransport())
 * ```
 *
 * @property config configuration provided for that specific client. Applied to all services that use this client.
 * @param transport [KrpcTransport] instance that will be used to send and receive RPC messages.
 * IMPORTANT: Must be exclusive to this client, otherwise unexpected behavior may occur.
 */
@OptIn(InternalCoroutinesApi::class)
public abstract class KrpcClient(
    final override val config: KrpcConfig.Client,
    transport: KrpcTransport,
) : KrpcServiceHandler(), RpcClient, KrpcEndpoint {
    // we make a child here, so we can send cancellation messages before closing the connection
    final override val coroutineContext: CoroutineContext = SupervisorJob(transport.coroutineContext.job)

    private val connector by lazy {
        KrpcClientConnector(config.serialFormatInitializer.build(), transport, config.waitForServices)
    }

    private var connectionId: Long? = null

    @InternalRpcApi
    final override val sender: KrpcMessageSender
        get() = connector

    private val callCounter = atomic(0L)

    override val logger: CommonLogger = CommonLogger.logger(objectId())

    private val serverSupportedPlugins: CompletableDeferred<Set<KrpcPlugin>> = CompletableDeferred()

    @InternalRpcApi
    final override val supportedPlugins: Set<KrpcPlugin>
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
    @InternalRpcApi
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
        connector.sendMessage(KrpcProtocolMessage.Handshake(KrpcPlugin.ALL))

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

    private fun handleProtocolMessage(message: KrpcProtocolMessage) {
        when (message) {
            is KrpcProtocolMessage.Handshake -> {
                connectionId = message.connectionId

                serverSupportedPlugins.complete(message.supportedPlugins)
            }

            is KrpcProtocolMessage.Failure -> {
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

    @Deprecated(
        "This method was primarily used for fields in RPC services, which are now deprecated. " +
                "See https://kotlin.github.io/kotlinx-rpc/strict-mode.html fields guide for more information"
    )
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
    ): RpcCallStreamContextFormatAndId {
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
    ): RpcCallStreamContextFormatAndId {
        // we should wait for the handshake to finish
        awaitHandshakeCompletion()

        val id = callCounter.incrementAndGet()

        val dataTypeString = callable.dataType.toString()

        val callId = "$connectionId:$dataTypeString:$id"

        logger.trace { "start a call[$callId] ${callable.name}" }

        val fallbackScope = serviceScopeOrNull()
            ?.serviceCoroutineScope
            ?.let { streamScopeOrNull(it) }

        val streamContext = LazyKrpcStreamContext(streamScopeOrNull(), fallbackScope) {
            KrpcStreamContext(callId, config, connectionId, call.serviceId, it)
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

        return RpcCallStreamContextFormatAndId(streamContext, serialFormat, callId)
    }

    private data class RpcCallStreamContextFormatAndId(
        val streamContext: LazyKrpcStreamContext,
        val serialFormat: SerialFormat,
        val callId: String,
    )

    private suspend fun executeCall(
        callId: String,
        streamContext: LazyKrpcStreamContext,
        call: RpcCall,
        callable: RpcCallable<*>,
        firstMessage: KrpcCallMessage,
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

    private val nonSuspendingSerialFormat = config.serialFormatInitializer.build()

    override fun <T> callServerStreaming(call: RpcCall): Flow<T> {
        return flow {
            awaitHandshakeCompletion()

            val id = callCounter.incrementAndGet()
            val callable = call.descriptor.getCallable(call.callableName)
                ?: error("Unexpected callable '${call.callableName}' for ${call.descriptor.fqName} service")

            val dataTypeString = callable.dataType.toString()

            val callId = "$connectionId:$dataTypeString:$id"

            val channel = Channel<T>()

            val request = serializeRequest(
                callId = callId,
                call = call,
                callable = callable,
                serialFormat = nonSuspendingSerialFormat,
                pluginParams = mapOf(KrpcPluginKey.NON_SUSPENDING_SERVER_FLOW_MARKER to ""),
            )

            connector.sendMessage(request)

            try {
                connector.subscribeToCallResponse(call.descriptor.fqName, callId) { message ->
                    when (message) {
                        is KrpcCallMessage.CallData -> {
                            error("Unexpected message")
                        }

                        is KrpcCallMessage.CallException -> {
                            val cause = runCatching {
                                message.cause.deserialize()
                            }

                            val result = if (cause.isFailure) {
                                cause.exceptionOrNull()!!
                            } else {
                                cause.getOrNull()!!
                            }

                            channel.close(result)
                        }

                        is KrpcCallMessage.CallSuccess, is KrpcCallMessage.StreamMessage -> {
                            val value = runCatching {
                                val serializerResult =
                                    nonSuspendingSerialFormat.serializersModule.rpcSerializerForType(callable.returnType)

                                decodeMessageData(nonSuspendingSerialFormat, serializerResult, message)
                            }

                            @Suppress("UNCHECKED_CAST")
                            channel.send(value.getOrNull() as T)
                        }

                        is KrpcCallMessage.StreamFinished -> {
                            connector.unsubscribeFromMessages(call.descriptor.fqName, callId)
                            channel.close()
                        }

                        is KrpcCallMessage.StreamCancel -> {
                            connector.unsubscribeFromMessages(call.descriptor.fqName, callId)
                            val cause = message.cause.deserialize()
                            channel.close(cause)
                        }
                    }
                }

                try {
                    while (true) {
                        val element = channel.receiveCatching()
                        if (element.isClosed) {
                            val ex = element.exceptionOrNull() ?: break
                            error(ex)
                        }

                        if (!element.isFailure) {
                            emit(element.getOrThrow())
                        }
                    }
                } catch (_: ClosedReceiveChannelException) {
                    // ignore
                }
            } catch (e: CancellationException) {
                // sendCancellation is not suspending, so no need for NonCancellable
                sendCancellation(CancellationType.REQUEST, call.serviceId.toString(), callId)

                throw e
            }
        }
    }

    private suspend fun handleMessage(
        message: KrpcCallMessage,
        streamContext: LazyKrpcStreamContext,
        callable: RpcCallable<*>,
        serialFormat: SerialFormat,
        callResult: RequestCompletableDeferred<Any?>,
    ) {
        when (message) {
            is KrpcCallMessage.CallData -> {
                error("Unexpected message")
            }

            is KrpcCallMessage.CallException -> {
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

            is KrpcCallMessage.CallSuccess -> {
                val value = runCatching {
                    val serializerResult = serialFormat.serializersModule.rpcSerializerForType(callable.returnType)

                    decodeMessageData(serialFormat, serializerResult, message)
                }

                callResult.completeWith(value)
            }

            is KrpcCallMessage.StreamCancel -> {
                streamContext.awaitInitialized().cancelStream(message)
            }

            is KrpcCallMessage.StreamFinished -> {
                streamContext.awaitInitialized().closeStream(message)
            }

            is KrpcCallMessage.StreamMessage -> {
                streamContext.awaitInitialized().send(message, serialFormat)
            }
        }
    }

    @InternalRpcApi
    final override suspend fun handleCancellation(message: KrpcGenericMessage) {
        when (val type = message.cancellationType()) {
            CancellationType.ENDPOINT -> {
                cancel("Closing client after server cancellation") // we cancel this client
            }

            CancellationType.CANCELLATION_ACK -> {
                val callId = message[KrpcPluginKey.CANCELLATION_ID]
                    ?: error("Expected CANCELLATION_ID for cancellation of type 'request'")

                val serviceTypeString = cancellingRequests.remove(callId) ?: return
                connector.unsubscribeFromMessages(serviceTypeString, callId)
            }

            else -> {
                logger.warn {
                    "Unsupported ${KrpcPluginKey.CANCELLATION_TYPE} $type for client, " +
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
        pluginParams: Map<KrpcPluginKey, String> = emptyMap(),
    ): KrpcCallMessage {
        val serializerData = serialFormat.serializersModule.rpcSerializerForType(callable.dataType)
        return when (serialFormat) {
            is StringFormat -> {
                val stringValue = serialFormat.encodeToString(serializerData, call.data)
                KrpcCallMessage.CallDataString(
                    callId = callId,
                    serviceType = call.descriptor.fqName,
                    callableName = call.callableName,
                    callType = callable.toMessageCallType(),
                    data = stringValue,
                    connectionId = connectionId,
                    serviceId = call.serviceId,
                    pluginParams = pluginParams,
                )
            }

            is BinaryFormat -> {
                val binaryValue = serialFormat.encodeToByteArray(serializerData, call.data)
                KrpcCallMessage.CallDataBinary(
                    callId = callId,
                    serviceType = call.descriptor.fqName,
                    callableName = call.callableName,
                    callType = callable.toMessageCallType(),
                    data = binaryValue,
                    connectionId = connectionId,
                    serviceId = call.serviceId,
                    pluginParams = pluginParams,
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
