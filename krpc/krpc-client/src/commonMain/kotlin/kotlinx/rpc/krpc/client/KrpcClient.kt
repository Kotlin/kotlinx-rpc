/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.descriptor.RpcCallable
import kotlinx.rpc.descriptor.RpcInvokator
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.getOrNull
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.krpc.*
import kotlinx.rpc.krpc.client.internal.ClientStreamContext
import kotlinx.rpc.krpc.client.internal.ClientStreamSerializer
import kotlinx.rpc.krpc.client.internal.KrpcClientConnector
import kotlinx.rpc.krpc.client.internal.StreamCall
import kotlinx.rpc.krpc.internal.*
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.SerializersModule
import kotlin.collections.first
import kotlin.concurrent.Volatile
import kotlin.coroutines.cancellation.CancellationException
import kotlin.properties.Delegates

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
 * @param config configuration provided for that specific client. Applied to all services that use this client.
 * @param transport [KrpcTransport] instance that will be used to send and receive RPC messages.
 * IMPORTANT: Must be exclusive to this client, otherwise unexpected behavior may occur.
 */
@OptIn(InternalCoroutinesApi::class)
public abstract class KrpcClient : RpcClient, KrpcEndpoint {
    /**
     * Called once to provide [KrpcTransport] for this client
     */
    public abstract suspend fun initializeTransport(): KrpcTransport

    private var isTransportReady: Boolean = false
    private var transport: KrpcTransport by Delegates.notNull()

    /**
     * Called once to provide [KrpcConfig.Client] for this client
     */
    public abstract fun initializeConfig(): KrpcConfig.Client

    private val config: KrpcConfig.Client by lazy {
        initializeConfig()
    }

    @Volatile
    private var clientCancelled = false

    private fun checkTransportReadiness() {
        if (!isTransportReady) {
            error(
                "Internal error, please contact developers for the support. " +
                        "Transport is not initialized, first scope access must come from an RPC request."
            )
        }
    }

    @InternalRpcApi
    public val internalScope: CoroutineScope by lazy {
        checkTransportReadiness()

        val context = SupervisorJob(transport.coroutineContext.job)

        context.job.invokeOnCompletion(onCancelling = true) {
            clientCancelled = true

            sendCancellation(CancellationType.ENDPOINT, null, null, closeTransportAfterSending = true)

            @OptIn(DelicateCoroutinesApi::class)
            GlobalScope.launch {
                requestChannels.values.forEach { it.close(CancellationException("Client cancelled")) }
                requestChannels.clear()
            }
        }

        CoroutineScope(context)
    }

    // we make a child here, so we can send cancellation messages before closing the connection
    private val connector by lazy {
        checkTransportReadiness()

        KrpcClientConnector(config.serialFormatInitializer.build(), transport, config.waitForServices)
    }

    private var connectionId: Long? = null

    @InternalRpcApi
    final override val sender: KrpcMessageSender
        get() = connector

    private val callCounter = atomic(0L)

    private val logger: RpcInternalCommonLogger = RpcInternalCommonLogger.logger(rpcInternalObjectId())

    private val serverSupportedPlugins: CompletableDeferred<Set<KrpcPlugin>> = CompletableDeferred()

    private val requestChannels = RpcInternalConcurrentHashMap<String, Channel<Any?>>()

    @InternalRpcApi
    final override val supportedPlugins: Set<KrpcPlugin>
        get() = serverSupportedPlugins.getOrNull() ?: emptySet()

    // callId to serviceTypeString
    private val cancellingRequests = RpcInternalConcurrentHashMap<String, String>()

    /**
     * Starts the handshake process and awaits for completion.
     * If the handshake was completed before, nothing happens.
     */
    private suspend fun initializeAndAwaitHandshakeCompletion() {
        transport = initializeTransport()
        isTransportReady = true

        connector.subscribeToGenericMessages(::handleGenericMessage)
        connector.subscribeToProtocolMessages(::handleProtocolMessage)

        connector.sendMessage(KrpcProtocolMessage.Handshake(KrpcPlugin.ALL))

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

    final override suspend fun <T> call(call: RpcCall): T {
        return callServerStreaming<T>(call).first()
    }

    @Suppress("detekt.CyclomaticComplexMethod")
    final override fun <T> callServerStreaming(call: RpcCall): Flow<T> {
        return flow {
            if (clientCancelled) {
                error("Client cancelled")
            }

            initializeAndAwaitHandshakeCompletion()

            val id = callCounter.incrementAndGet()
            val callable = call.descriptor.getCallable(call.callableName)
                ?: error("Unexpected callable '${call.callableName}' for ${call.descriptor.fqName} service")

            val callId = "$connectionId:${callable.name}:$id"

            val channel = Channel<T>()

            try {
                @Suppress("UNCHECKED_CAST")
                requestChannels[callId] = channel as Channel<Any?>

                val request = serializeRequest(
                    callId = callId,
                    call = call,
                    callable = callable,
                    serialFormat = serialFormat,
                    pluginParams = mapOf(KrpcPluginKey.NON_SUSPENDING_SERVER_FLOW_MARKER to ""),
                )

                connector.subscribeToCallResponse(call.descriptor.fqName, callId) { message ->
                    if (cancellingRequests.containsKey(callId)) {
                        return@subscribeToCallResponse
                    }

                    handleServerStreamingMessage(message, channel, callable)
                }

                connector.sendMessage(request)

                coroutineScope {
                    val clientStreamsJob = launch(CoroutineName("client-stream-root-${call.serviceId}-$callId")) {
                        supervisorScope {
                            clientStreamContext.streams[callId].orEmpty().forEach {
                                launch(CoroutineName("client-stream-${call.serviceId}-$callId-${it.streamId}")) {
                                    handleOutgoingStream(it, serialFormat, call.descriptor.fqName)
                                }
                            }
                        }
                    }

                    try {
                        consumeAndEmitServerMessages(channel)
                    } finally {
                        clientStreamsJob.cancelAndJoin()
                        clientStreamContext.streams.remove(callId)
                    }
                }
            } catch (e: CancellationException) {
                if (!clientCancelled) {
                    cancellingRequests[callId] = call.descriptor.fqName

                    sendCancellation(CancellationType.REQUEST, call.descriptor.fqName, callId)

                    connector.unsubscribeFromMessages(call.descriptor.fqName, callId) {
                        cancellingRequests.remove(callId)
                    }
                }

                throw e
            } finally {
                channel.close()
                requestChannels.remove(callId)
            }
        }
    }

    private suspend fun <T> FlowCollector<T>.consumeAndEmitServerMessages(channel: Channel<T>) {
        while (true) {
            val element = channel.receiveCatching()
            if (element.isClosed) {
                val ex = element.exceptionOrNull() ?: break
                throw ex
            }

            if (!element.isFailure) {
                emit(element.getOrThrow())
            }
        }
    }

    private suspend fun <T, @Rpc R : Any> handleServerStreamingMessage(
        message: KrpcCallMessage,
        channel: Channel<T>,
        callable: RpcCallable<R>,
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

                channel.close(result)
            }

            is KrpcCallMessage.CallSuccess, is KrpcCallMessage.StreamMessage -> {
                val value = runCatching {
                    val serializerResult = serialFormat.serializersModule
                        .rpcSerializerForType(callable.returnType)

                    decodeMessageData(serialFormat, serializerResult, message)
                }

                @Suppress("UNCHECKED_CAST")
                channel.send(value.getOrNull() as T)
            }

            is KrpcCallMessage.StreamFinished -> {
                channel.close()
            }

            is KrpcCallMessage.StreamCancel -> {
                val cause = message.cause.deserialize()
                channel.close(cause)
            }
        }
    }

    @InternalRpcApi
    final override suspend fun handleCancellation(message: KrpcGenericMessage) {
        when (val type = message.cancellationType()) {
            CancellationType.ENDPOINT -> {
                internalScope.cancel("Closing client after server cancellation") // we cancel this client
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
                val stringValue = clientStreamContext.scoped(callId, call.serviceId) {
                    serialFormat.encodeToString(serializerData, call.data)
                }

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
                val binaryValue = clientStreamContext.scoped(callId, call.serviceId) {
                    serialFormat.encodeToByteArray(serializerData, call.data)
                }

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

    private suspend fun handleOutgoingStream(
        outgoingStream: StreamCall,
        serialFormat: SerialFormat,
        serviceTypeString: String,
    ) {
        try {
            collectAndSendOutgoingStream(
                serialFormat = serialFormat,
                flow = outgoingStream.stream,
                outgoingStream = outgoingStream,
                serviceTypeString = serviceTypeString,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (@Suppress("detekt.TooGenericExceptionCaught") cause: Throwable) {
            val serializedReason = serializeException(cause)
            val message = KrpcCallMessage.StreamCancel(
                callId = outgoingStream.callId,
                serviceType = serviceTypeString,
                streamId = outgoingStream.streamId,
                cause = serializedReason,
                connectionId = outgoingStream.connectionId,
                serviceId = outgoingStream.serviceId,
            )
            sender.sendMessage(message)

            throw cause
        }

        val message = KrpcCallMessage.StreamFinished(
            callId = outgoingStream.callId,
            serviceType = serviceTypeString,
            streamId = outgoingStream.streamId,
            connectionId = outgoingStream.connectionId,
            serviceId = outgoingStream.serviceId,
        )

        sender.sendMessage(message)
    }

    private suspend fun collectAndSendOutgoingStream(
        serialFormat: SerialFormat,
        flow: Flow<*>,
        serviceTypeString: String,
        outgoingStream: StreamCall,
    ) {
        flow.collect {
            val message = when (serialFormat) {
                is StringFormat -> {
                    val stringData = serialFormat.encodeToString(outgoingStream.elementSerializer, it)
                    KrpcCallMessage.StreamMessageString(
                        callId = outgoingStream.callId,
                        serviceType = serviceTypeString,
                        streamId = outgoingStream.streamId,
                        data = stringData,
                        connectionId = outgoingStream.connectionId,
                        serviceId = outgoingStream.serviceId,
                    )
                }

                is BinaryFormat -> {
                    val binaryData = serialFormat.encodeToByteArray(outgoingStream.elementSerializer, it)
                    KrpcCallMessage.StreamMessageBinary(
                        callId = outgoingStream.callId,
                        serviceType = serviceTypeString,
                        streamId = outgoingStream.streamId,
                        data = binaryData,
                        connectionId = outgoingStream.connectionId,
                        serviceId = outgoingStream.serviceId,
                    )
                }

                else -> {
                    unsupportedSerialFormatError(serialFormat)
                }
            }

            sender.sendMessage(message)
        }
    }

    private val clientStreamContext: ClientStreamContext = ClientStreamContext(connectionId = connectionId)

    private val serialFormat: SerialFormat by lazy {
        val module = SerializersModule {
            contextual(Flow::class) {
                @Suppress("UNCHECKED_CAST")
                ClientStreamSerializer(clientStreamContext, it.first() as KSerializer<Any?>)
            }
        }

        config.serialFormatInitializer.applySerializersModuleAndBuild(module)
    }

    private fun RpcCallable<*>.toMessageCallType(): KrpcCallMessage.CallType {
        return when (invokator) {
            is RpcInvokator.Method -> KrpcCallMessage.CallType.Method
        }
    }
}

/**
 * Represents an initialized RPC client that wraps a predefined configuration and transport.
 */
public abstract class InitializedKrpcClient(
    private val config: KrpcConfig.Client,
    private val transport: KrpcTransport,
): KrpcClient() {
    final override suspend fun initializeTransport(): KrpcTransport {
        return transport
    }

    final override fun initializeConfig(): KrpcConfig.Client {
        return config
    }
}
