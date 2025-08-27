/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.internal.internalRpcError
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.receiveCatching
import kotlinx.serialization.*

@InternalRpcApi
public interface KrpcMessageSender {
    public val transportScope: CoroutineScope

    public suspend fun sendMessage(message: KrpcMessage)

    public fun drainSendQueueAndClose(message: String)
}

internal typealias KrpcMessageSubscription<Message> = suspend (Message) -> Unit

/**
 * Represents a connector for remote procedure call (RPC) communication.
 * This class is responsible for sending and receiving [KrpcMessage] over a specified transport.
 *
 * DO NOT use actual dumper in production!
 */
@InternalRpcApi
public class KrpcConnector(
    private val serialFormat: SerialFormat,
    private val transport: KrpcTransport,
    private val config: KrpcConfig.Connector,
    isServer: Boolean,
) : KrpcMessageSender {
    override val transportScope: CoroutineScope = transport

    private val role = if (isServer) SERVER_ROLE else CLIENT_ROLE
    private val logger = RpcInternalCommonLogger.logger("$role Connector")

    private val receiveHandlers = RpcInternalConcurrentHashMap<HandlerKey<*>, KrpcReceiveHandler>()
    private val keyLocks = RpcInternalConcurrentHashMap<HandlerKey<*>, Mutex>()
    private val serviceSubscriptions =
        RpcInternalConcurrentHashMap<HandlerKey.Service, KrpcMessageSubscription<KrpcCallMessage>>()

    private val sendHandlers = RpcInternalConcurrentHashMap<HandlerKey<*>, KrpcSendHandler>()
    private val sendChannel = Channel<KrpcTransportMessage>(Channel.UNLIMITED)

    private var receiveBufferSize: Int? = null
    private var sendBufferSize: Int? = null

    private var peerSupportsBackPressure = false

    private val dumpLogger by lazy { RpcInternalDumpLoggerContainer.provide() }

    override suspend fun sendMessage(message: KrpcMessage) {
        if (message is KrpcProtocolMessage.Handshake) {
            message.pluginParams[KrpcPluginKey.WINDOW_UPDATE] = "${config.perCallBufferSize}"
        }

        val transportMessage = when (serialFormat) {
            is StringFormat -> {
                KrpcTransportMessage.StringMessage(serialFormat.encodeToString(message))
            }

            is BinaryFormat -> {
                KrpcTransportMessage.BinaryMessage(serialFormat.encodeToByteArray(message))
            }

            else -> {
                unsupportedSerialFormatError(serialFormat)
            }
        }

        if (dumpLogger.isEnabled) {
            dumpLogger.dump(role, SEND_PHASE) { transportMessage.dump() }
        }

        sendTransportMessage(message.handlerKey(), transportMessage)
    }

    private suspend fun sendTransportMessage(key: HandlerKey<*>, message: KrpcTransportMessage) {
        sendHandlers.computeIfAbsent(key) {
            KrpcSendHandler(sendChannel).also {
                // - fallback to unlimited buffer if no buffer size is specified
                // this is for backwards compatibility
                // - use unlimited size for protocol messages
                val initialSize = when (key) {
                    HandlerKey.Protocol, HandlerKey.Generic -> -1
                    else -> sendBufferSize ?: -1
                }

                it.updateWindowSize(initialSize)
            }
        }.sendMessage(message)
    }

    public fun unsubscribeFromMessages(key: HandlerKey<*>, callback: suspend () -> Unit = {}) {
        transportScope.launch(CoroutineName("krpc-connector-unsubscribe-$key")) {
            withLockForKey(key) {
                if (key is HandlerKey.Service) {
                    receiveHandlers.withKeys { keys ->
                        keys
                            .filter { it is HandlerKey.ServiceCall && it.serviceType == key.serviceType }
                            .forEach {
                                cleanForKey(it)
                            }
                    }

                    serviceSubscriptions.remove(key)
                } else {
                    cleanForKey(key)
                }
            }

            callback()
        }
    }

    private fun cleanForKey(key: HandlerKey<*>) {
        sendHandlers.remove(key)?.close(null)
        receiveHandlers.remove(key)?.close(key, null)
        keyLocks.remove(key)
    }

    public suspend fun <Message : KrpcMessage> subscribeToMessages(
        key: HandlerKey<Message>,
        subscription: KrpcMessageSubscription<Message>,
    ): Unit = withLockForKey(key) {
        if (key is HandlerKey.Service) {
            serviceSubscriptions.computeIfAbsent(key) {
                @Suppress("UNCHECKED_CAST")
                subscription as KrpcMessageSubscription<KrpcCallMessage>
            }

            receiveHandlers.withKeys { keys ->
                keys
                    .filter { it is HandlerKey.ServiceCall && it.serviceType == key.serviceType }
                    .forEach {
                        @Suppress("UNCHECKED_CAST")
                        subscribeWithActingHandlerPerTrack(it as HandlerKey<Message>, subscription)
                    }
            }
        } else {
            subscribeWithActingHandlerPerTrack(key, subscription)
        }
    }

    // per track:
    // - call id is a track
    // - generic is a track
    // - protocol is a track
    // - service is **not** a track
    private fun <Message : KrpcMessage> subscribeWithActingHandlerPerTrack(
        key: HandlerKey<Message>,
        subscription: KrpcMessageSubscription<Message>,
    ) {
        val storingHandler = handlerFor(key)

        if (storingHandler !is KrpcStoringReceiveHandler) {
            internalRpcError("Already subscribed to messages with key $key")
        }

        @Suppress("UNCHECKED_CAST")
        val handler = KrpcActingReceiveHandler(
            callHandler = subscription as KrpcMessageSubscription<KrpcMessage>,
            storingHandler = storingHandler,
            key = key,
            sender = this,
            timeout = config.callTimeout,
            broadcastUpdates = peerSupportsBackPressure,
        )

        receiveHandlers[key] = handler
    }

    private fun <Message : KrpcMessage> handlerFor(key: HandlerKey<Message>): KrpcReceiveHandler {
        if (key is HandlerKey.Service) {
            internalRpcError("Wrong key type for a handler: $key")
        }

        return receiveHandlers.computeIfAbsent(key) {
            val storing = KrpcStoringReceiveHandler(
                buffer = KrpcReceiveBuffer {
                    // - fallback to unlimited buffer if no buffer size is specified
                    // this is for backwards compatibility
                    // - use unlimited size for protocol messages
                    when (key) {
                        HandlerKey.Protocol, HandlerKey.Generic -> Channel.UNLIMITED
                        else -> receiveBufferSize ?: Channel.UNLIMITED
                    }
                },
                sender = this,
            )

            if (key is HandlerKey.ServiceCall && role == SERVER_ROLE) {
                val serviceKey = HandlerKey.Service(key.serviceType)
                val subscription = serviceSubscriptions[serviceKey]
                if (subscription != null) {
                    @Suppress("UNCHECKED_CAST")
                    KrpcActingReceiveHandler(
                        callHandler = subscription as KrpcMessageSubscription<KrpcMessage>,
                        storingHandler = storing,
                        key = key,
                        sender = this,
                        timeout = config.callTimeout,
                        broadcastUpdates = peerSupportsBackPressure,
                    )
                } else {
                    storing
                }
            } else {
                storing
            }
        }
    }

    @Suppress("JoinDeclarationAndAssignment")
    private val sendJob: Job

    override fun drainSendQueueAndClose(message: String) {
        // don't close receive handlers, as
        // we don't want to send 'unprocessed messages' messages
        // because peer is closing entirely anyway
        //
        // don't close send handlers, as we want to process them all

        // close for receiving new messages
        sendChannel.close()

        sendJob.invokeOnCompletion {
            transportScope.cancel(message)
        }
    }

    init {
        sendJob = transportScope.launch(CoroutineName("krpc-connector-send-loop")) {
            while (true) {
                transport.send(sendChannel.receiveCatching().getOrNull() ?: break)
            }
        }

        transportScope.launch(CoroutineName("krpc-connector-receive-loop")) {
            while (true) {
                processMessage(transport.receiveCatching().getOrNull() ?: break)
            }
        }
    }

    private fun decodeMessage(transportMessage: KrpcTransportMessage): KrpcMessage? {
        try {
            return when {
                serialFormat is StringFormat && transportMessage is KrpcTransportMessage.StringMessage -> {
                    // otherwise KrpcMessage? is inferred which leads to decode exception
                    serialFormat.decodeFromString<KrpcMessage>(transportMessage.value)
                }

                serialFormat is BinaryFormat && transportMessage is KrpcTransportMessage.BinaryMessage -> {
                    // otherwise KrpcMessage? is inferred which leads to decode exception
                    serialFormat.decodeFromByteArray<KrpcMessage>(transportMessage.value)
                }

                else -> {
                    logger.error { "Unsupported serialization format: ${serialFormat::class} for ${transportMessage::class}" }
                    return null
                }
            }
        } catch (e: SerializationException) {
            logger.error(e) { "Failed to decode transport message" }
            logger.debug { "Failed message: ${transportMessage.dump()}" }
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Decoded message is not a valid ${KrpcMessage::class}" }
            logger.debug { "Invalid message: ${transportMessage.dump()}" }
        }

        return null
    }

    private suspend fun processMessage(transportMessage: KrpcTransportMessage) {
        val message = decodeMessage(transportMessage) ?: return

        if (dumpLogger.isEnabled) {
            dumpLogger.dump(role, RECEIVE_PHASE) { transportMessage.dump() }
        }

        processMessage(message, message.handlerKey())
    }

    private suspend fun processMessage(message: KrpcMessage, key: HandlerKey<*>) {
        when {
            message is KrpcCallMessage -> {
                @Suppress("UNCHECKED_CAST")
                processServiceMessage(message, key as HandlerKey<KrpcCallMessage>)
            }

            message is KrpcGenericMessage && message.pluginParams.orEmpty().contains(KrpcPluginKey.WINDOW_UPDATE) -> {
                processWindow(message)
            }

            message is KrpcProtocolMessage || message is KrpcGenericMessage -> {
                processNonServiceMessage(message, key)
            }

            else -> {
                logger.error { "Received message of unknown processing: $message" }
            }
        }
    }

    private suspend fun processWindow(message: KrpcGenericMessage) {
        when (val result = decodeWindow(message)) {
            is WindowResult.Success -> {
                // must be 'key' from 'result'
                sendHandlers[result.key]?.updateWindowSize(result.update)
            }

            is WindowResult.Failure -> {
                sendMessage(
                    KrpcProtocolMessage.Failure(
                        errorMessage = result.message,
                        connectionId = message.connectionId,
                        failedMessage = message,
                    )
                )
            }
        }
    }

    private suspend fun processNonServiceMessage(message: KrpcMessage, key: HandlerKey<*>) {
        // should be the first message we receive
        if (message is KrpcProtocolMessage.Handshake) {
            if (message.supportedPlugins.contains(KrpcPlugin.BACKPRESSURE)) {
                peerSupportsBackPressure = true

                // If it is a new version peer, it will be set to the buffer size from the config
                receiveBufferSize = config.perCallBufferSize

                val windowParam = message.pluginParams[KrpcPluginKey.WINDOW_UPDATE]
                    ?.toIntOrNull()

                // -1 for an unlimited buffer size for old peers
                sendBufferSize = windowParam ?: -1
            } else {
                // UNLIMITED for backwards compatibility
                receiveBufferSize = Channel.UNLIMITED
                // -1 for an unlimited buffer size for old peers for backwards compatibility
                sendBufferSize = -1
            }

            // update this if present, as this is the only sender that has -1 even for new clients
            sendHandlers[HandlerKey.Protocol]?.updateWindowSize(sendBufferSize ?: -1)
        }

        withLockForKey(key) {
            val handler = handlerFor(key)

            handler.handle(message) { cause ->
                val failure = KrpcProtocolMessage.Failure(
                    connectionId = message.connectionId,
                    errorMessage = "Failed to process $key, error: ${cause?.message}",
                    failedMessage = message,
                )

                sendMessage(failure)
            }
        }.onFailure {
            if (message.isException) {
                return@onFailure
            }

            val failure = KrpcProtocolMessage.Failure(
                connectionId = message.connectionId,
                errorMessage = "Message limit of ${config.perCallBufferSize} is exceeded for $key.",
                failedMessage = message,
            )

            sendMessage(failure)
        }.onClosed {
            // do nothing; it's a service message, meaning that the service is dead
        }
    }

    private suspend fun processServiceMessage(message: KrpcCallMessage, key: HandlerKey<KrpcCallMessage>) {
        val (handler, result) = withLockForKey(key) {
            val handler = receiveHandlers[key]
                ?: if (config.waitTimeout.isPositive()) {
                    handlerFor(key)
                } else {
                    val errorMessage = "No registered service of ${message.serviceType} service type " +
                            "was able to process message ($key) at the moment. " +
                            "Available: keys: [${receiveHandlers.keys.joinToString()}]."

                    sendCallException(message, illegalStateException(errorMessage))
                    return
                }

            val result = handler.handle(message) { initialCause ->
                val cause = illegalStateException(
                    message = "Failed to process call ${message.callId} for service ${message.serviceType}",
                    cause = initialCause,
                )

                sendCallException(message, cause)
            }

            handler to result
        }

        result.onFailure {
            if (message.isException) {
                return@onFailure
            }

            sendCallException(
                message = message,
                cause = illegalStateException("Message limit of ${config.perCallBufferSize} is exceeded for $key"),
            )
        }.onClosed {
            if (message.isException) {
                return@onClosed
            }

            sendCallException(
                message = message,
                cause = illegalStateException("Service $key is dead"),
            )
        }

        if (handler is KrpcStoringReceiveHandler && result.isSuccess) {
            transportScope.launch(CoroutineName("krpc-connector-discard-if-unprocessed-$key")) {
                delay(config.waitTimeout)

                withLockForKey(key) {
                    if (handler.processingStarted || receiveHandlers[key] != handler) {
                        return@launch
                    }

                    receiveHandlers.remove(key)
                    handler.close(
                        key = key,
                        e = illegalStateException(
                            "Waiting limit of ${config.waitTimeout} " +
                                    "is exceeded for unprocessed messages with $key"
                        ),
                    )
                }
            }
        }
    }

    private suspend fun sendCallException(message: KrpcCallMessage, cause: Throwable) {
        val callException = KrpcCallMessage.CallException(
            callId = message.callId,
            serviceType = message.serviceType,
            cause = serializeException(cause),
            connectionId = message.connectionId,
            serviceId = message.serviceId,
        )

        sendMessage(callException)
    }

    private suspend inline fun <T> withLockForKey(
        key: HandlerKey<*>,
        action: () -> T,
    ): T {
        val lockKey = if (key is HandlerKey.ServiceCall && role == SERVER_ROLE) {
            HandlerKey.Service(key.serviceType)
        } else {
            key
        }

        return keyLocks.computeIfAbsent(lockKey) { Mutex() }.withLock(action = action)
    }

    internal companion object {
        const val SEND_PHASE = "Send"
        const val RECEIVE_PHASE = "Receive"

        const val SERVER_ROLE = "Server"
        const val CLIENT_ROLE = "Client"

        @OptIn(ExperimentalStdlibApi::class)
        private fun KrpcTransportMessage.dump(): String {
            return when (this) {
                is KrpcTransportMessage.StringMessage -> value
                is KrpcTransportMessage.BinaryMessage -> value.toHexString()
            }
        }
    }
}

internal val KrpcMessage.isException
    get() = when (this) {
        is KrpcCallMessage.CallException, is KrpcProtocolMessage.Failure -> true
        else -> false
    }
