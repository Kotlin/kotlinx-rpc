/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.internal

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage
import kotlinx.rpc.krpc.internal.logging.RpcInternalCommonLogger
import kotlinx.rpc.krpc.internal.logging.RpcInternalDumpLoggerContainer
import kotlinx.rpc.krpc.receiveCatching
import kotlinx.serialization.*
import kotlin.time.Duration.Companion.seconds

@InternalRpcApi
public interface KrpcMessageSender : CoroutineScope {
    public suspend fun sendMessage(message: KrpcMessage)
}

private typealias KrpcMessageHandler = suspend (KrpcMessage) -> Unit

/**
 * Represents a connector for remote procedure call (RPC) communication.
 * This class is responsible for sending and receiving [KrpcMessage] over a specified transport.
 *
 * @param SubscriptionKey the type of the subscription key used for message subscriptions.
 * @param serialFormat the serial format used for encoding and decoding [KrpcMessage].
 * @param transport the transport used for sending and receiving encoded RPC messages.
 * @param waitForSubscribers a flag indicating whether the connector should wait for subscribers
 * if no service is available to process the message immediately.
 * If false, the endpoint that sent the message will receive a [KrpcCallMessage.CallException]
 * that says that there were no services to process its message.
 * @param isServer flag indication whether this is a server or a client.
 * @param getKey a lambda function that returns the subscription key for a given [KrpcCallMessage].
 * DO NOT use actual dumper in production!
 */
@InternalRpcApi
public class KrpcConnector<SubscriptionKey : Any>(
    private val serialFormat: SerialFormat,
    private val transport: KrpcTransport,
    private val waitForSubscribers: Boolean = true,
    isServer: Boolean,
    private val getKey: KrpcMessage.() -> SubscriptionKey,
) : KrpcMessageSender, CoroutineScope by transport {
    private val role = if (isServer) SERVER_ROLE else CLIENT_ROLE
    private val logger = RpcInternalCommonLogger.logger(rpcInternalObjectId(role))

    private val waiting = RpcInternalConcurrentHashMap<SubscriptionKey, MutableList<KrpcMessage>>()
    private val subscriptions = RpcInternalConcurrentHashMap<SubscriptionKey, KrpcMessageHandler>()
    private val processWaitersLocks = RpcInternalConcurrentHashMap<SubscriptionKey, Mutex>()

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

        transport.send(transportMessage)
    }

    public fun unsubscribeFromMessages(key: SubscriptionKey, callback: () -> Unit = {}) {
        launch(CoroutineName("krpc-connector-unsubscribe-$key")) {
            delay(15.seconds)
            subscriptions.remove(key)
            processWaitersLocks.remove(key)
        }.invokeOnCompletion {
            callback()
        }
    }

    public suspend fun subscribeToMessages(key: SubscriptionKey, handler: KrpcMessageHandler) {
        subscriptions[key] = handler
        processWaiters(key, handler)
    }

    init {
        launch(CoroutineName("krpc-connector-receive-loop")) {
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

        processMessage(message)
    }

    private suspend fun processMessage(message: KrpcMessage) = withLockForKey(message.getKey()) {
        when (message) {
            is KrpcCallMessage -> processServiceMessage(message)
            is KrpcProtocolMessage, is KrpcGenericMessage -> processNonServiceMessage(message)
        }
    }

    private suspend fun processNonServiceMessage(message: KrpcMessage) {
        when (val result = tryHandle(message)) {
            is HandlerResult.Failure -> {
                val failure = KrpcProtocolMessage.Failure(
                    connectionId = message.connectionId,
                    errorMessage = "Failed to process ${message::class.simpleName}, error: ${result.cause?.message}",
                    failedMessage = message,
                )

                sendMessage(failure)
            }

            HandlerResult.NoSubscription -> {
                waiting.computeIfAbsent(message.getKey()) { mutableListOf() }.add(message)
            }

            HandlerResult.Success -> {} // ok
        }
    }

    private suspend fun processServiceMessage(message: KrpcCallMessage) {
        val result = tryHandle(message)

        // todo better exception processing probably
        if (result != HandlerResult.Success) {
            if (waitForSubscribers) {
                waiting.computeIfAbsent(message.getKey()) { mutableListOf() }.add(message)

                val reason = when (result) {
                    is HandlerResult.Failure -> {
                        "Unhandled exception while processing ${result.cause?.message}"
                    }

                    is HandlerResult.NoSubscription -> {
                        "No service with key '${message.getKey()}' and '${message.serviceType}' type was registered. " +
                                "Available: keys: [${subscriptions.keys.joinToString()}]"
                    }

                    else -> {
                        "Unknown"
                    }
                }

                logger.warn((result as? HandlerResult.Failure)?.cause) {
                    "No registered service of ${message.serviceType} service type " +
                            "was able to process message ($message) at the moment. Waiting for new services. " +
                            "Reason: $reason"
                }

                return
            }

            val initialCause = (result as? HandlerResult.Failure)?.cause

            val cause = IllegalStateException(
                "Failed to process call ${message.callId} for service ${message.serviceType}, " +
                        "${subscriptions.values.size} attempts failed",
                initialCause,
            )

            val callException = KrpcCallMessage.CallException(
                callId = message.callId,
                serviceType = message.serviceType,
                cause = serializeException(cause),
                connectionId = message.connectionId,
                serviceId = message.serviceId,
            )

            sendMessage(callException)
        }
    }

    private suspend fun tryHandle(
        message: KrpcMessage,
        handler: KrpcMessageHandler? = null,
    ): HandlerResult {
        val key = message.getKey()
        val subscriber = handler ?: subscriptions[key] ?: return HandlerResult.NoSubscription

        val result = runCatching {
            subscriber(message)
        }

        return when {
            result.isFailure -> {
                val exception = result.exceptionOrNull()
                if (exception !is CancellationException) {
                    logger.error(exception) { "Failed to handle message with key $key" }
                }
                HandlerResult.Failure(exception)
            }

            else -> {
                HandlerResult.Success
            }
        }
    }

    private suspend fun processWaiters(key: SubscriptionKey, handler: KrpcMessageHandler) {
        withLockForKey(key) {
            if (waiting.values.isEmpty()) return

            val iterator = waiting[key]?.iterator() ?: return
            while (iterator.hasNext()) {
                val message = iterator.next()

                val tryHandle = tryHandle(message, handler)
                if (tryHandle == HandlerResult.Success) {
                    iterator.remove()
                }
            }
        }
    }

    private suspend inline fun <T> withLockForKey(key: SubscriptionKey, action: () -> T): T =
        processWaitersLocks.computeIfAbsent(key) { Mutex() }.withLock(action = action)

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

private sealed interface HandlerResult {
    data object Success : HandlerResult

    data object NoSubscription : HandlerResult

    data class Failure(val cause: Throwable?) : HandlerResult
}
