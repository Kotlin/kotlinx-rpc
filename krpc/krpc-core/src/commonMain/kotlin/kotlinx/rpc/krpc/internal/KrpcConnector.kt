/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
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
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.receiveCatching
import kotlinx.serialization.*

@InternalRpcApi
public interface KrpcMessageSender {
    public val transportScope: CoroutineScope

    public suspend fun sendMessage(message: KrpcMessage)
}

internal typealias KrpcMessageSubscription = suspend (KrpcMessage) -> Unit

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

    private val receiveHandlers = RpcInternalConcurrentHashMap<HandlerKey, KrpcReceiveHandler>()
    private val keyLocks = RpcInternalConcurrentHashMap<HandlerKey, Mutex>()
    private val serviceSubscriptions = RpcInternalConcurrentHashMap<HandlerKey.Service, KrpcMessageSubscription>()

    private val sendHandlers = RpcInternalConcurrentHashMap<HandlerKey, KrpcSendHandler>()
    private val sendChannel = Channel<KrpcTransportMessage>(Channel.UNLIMITED)

    private var bufferSize: Int? = null

    private val dumpLogger by lazy { RpcInternalDumpLoggerContainer.provide() }

    override suspend fun sendMessage(message: KrpcMessage) {
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

        sendTransportMessage(message.handlerKey, transportMessage)
    }

    private suspend fun sendTransportMessage(key: HandlerKey, message: KrpcTransportMessage) {
        sendHandlers.computeIfAbsent(key) { KrpcSendHandler(sendChannel) }.sendMessage(message)
    }

    public fun unsubscribeFromMessages(key: HandlerKey, callback: () -> Unit = {}) {
        transportScope.launch(CoroutineName("krpc-connector-unsubscribe-$key")) {
            withLockForKey(key) {
                if (key is HandlerKey.Service) {
                    receiveHandlers.keys
                        .filter { it is HandlerKey.ServiceCall && it.serviceType == key.serviceType }
                        .forEach {
                            cleanForKey(it)
                        }

                    serviceSubscriptions.remove(key)
                } else {
                    cleanForKey(key)
                }
            }
        }.invokeOnCompletion {
            callback()
        }
    }

    private fun cleanForKey(key: HandlerKey) {
        sendHandlers.remove(key)?.close(null)
        receiveHandlers.remove(key)?.close(key, null)
        keyLocks.remove(key)
    }

    public suspend fun subscribeToMessages(
        key: HandlerKey,
        subscription: KrpcMessageSubscription,
    ): Unit = withLockForKey(key) {
        if (key is HandlerKey.Service) {
            serviceSubscriptions.computeIfAbsent(key) { subscription }

            receiveHandlers.keys
                .filter { it is HandlerKey.ServiceCall && it.serviceType == key.serviceType }
                .forEach {
                    subscribeToMessagesPerCallId(it, subscription)
                }
        } else {
            subscribeToMessagesPerCallId(key, subscription)
        }
    }

    private fun subscribeToMessagesPerCallId(key: HandlerKey, subscription: KrpcMessageSubscription) {
        val storingHandler = handlerFor(key)

        if (storingHandler !is KrpcStoringReceiveHandler) {
            internalRpcError("Already subscribed to messages with key $key")
        }

        @Suppress("UNCHECKED_CAST")
        val handler = KrpcActingReceiveHandler(
            callHandler = subscription,
            storingHandler = storingHandler,
            key = key,
            sender = this,
            timeout = config.callTimeout,
        )

        receiveHandlers[key] = handler
    }

    private fun handlerFor(key: HandlerKey): KrpcReceiveHandler {
        if (key is HandlerKey.Service) {
            internalRpcError("Wrong key type for a handler: $key")
        }

        return receiveHandlers.computeIfAbsent(key) {
            val storing = KrpcStoringReceiveHandler(
                buffer = KrpcReceiveBuffer {
                    // fallback to unlimited buffer if no buffer size is specified
                    // this is for backwards compatibility
                    bufferSize ?: Channel.UNLIMITED
                },
                sender = this,
            )

            if (key is HandlerKey.ServiceCall && role == SERVER_ROLE) {
                val serviceKey = HandlerKey.Service(key.serviceType)
                val subscription = serviceSubscriptions[serviceKey]
                if (subscription != null) {
                    KrpcActingReceiveHandler(
                        callHandler = subscription,
                        storingHandler = storing,
                        key = key,
                        sender = this,
                        timeout = config.callTimeout,
                    )
                } else {
                    storing
                }
            } else {
                storing
            }
        }
    }

    init {
        transportScope.launch(CoroutineName("krpc-connector-send-loop")) {
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

    private suspend fun processMessage(transportMessage: KrpcTransportMessage) {
        val message: KrpcMessage = when {
            serialFormat is StringFormat && transportMessage is KrpcTransportMessage.StringMessage -> {
                serialFormat.decodeFromString(transportMessage.value)
            }

            serialFormat is BinaryFormat && transportMessage is KrpcTransportMessage.BinaryMessage -> {
                serialFormat.decodeFromByteArray(transportMessage.value)
            }

            else -> {
                return
            }
        }

        if (dumpLogger.isEnabled) {
            dumpLogger.dump(role, RECEIVE_PHASE) { transportMessage.dump() }
        }

        processMessage(message, message.handlerKey)
    }

    private suspend fun processMessage(message: KrpcMessage, key: HandlerKey) {
        when {
            message is KrpcCallMessage -> {
                processServiceMessage(message, key)
            }

            message is KrpcGenericMessage &&  message.pluginParams.orEmpty().contains(KrpcPluginKey.WINDOW_UPDATE)-> {
                processWindow(message)
            }

            message is KrpcProtocolMessage || message is KrpcGenericMessage -> {
                processNonServiceMessage(message, key)
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

    private suspend fun processNonServiceMessage(message: KrpcMessage, key: HandlerKey) {
        // should the first message we receive
        if (message is KrpcProtocolMessage.Handshake) {
            bufferSize = if (message.supportedPlugins.contains(KrpcPlugin.BACKPRESSURE)) {
                // If it is a new client, it will be set to the buffer size from the config
                config.perCallBufferSize
            } else {
                // UNLIMITED for backwards compatibility
                Channel.UNLIMITED
            }
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

    private suspend fun processServiceMessage(message: KrpcCallMessage, key: HandlerKey) {
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
        key: HandlerKey,
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

internal val KrpcMessage.isException get() = when (this) {
    is KrpcCallMessage.CallException, is KrpcProtocolMessage.Failure -> true
    else -> false
}
