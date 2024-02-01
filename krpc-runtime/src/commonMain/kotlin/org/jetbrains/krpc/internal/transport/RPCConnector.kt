/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.transport

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.*
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.RPCTransportMessage
import org.jetbrains.krpc.internal.InternalKRPCApi
import org.jetbrains.krpc.internal.hex.toHexStringInternal
import org.jetbrains.krpc.internal.logging.CommonLogger
import org.jetbrains.krpc.internal.logging.DumpLoggerContainer
import org.jetbrains.krpc.internal.logging.initialized
import org.jetbrains.krpc.internal.objectId
import org.jetbrains.krpc.internal.serializeException
import org.jetbrains.krpc.internal.unsupportedSerialFormatError
import kotlin.collections.set

@InternalKRPCApi
public interface RPCMessageSender : CoroutineScope {
    public suspend fun sendMessage(message: RPCMessage)
}

private typealias RPCMessageHandler = suspend (RPCMessage) -> Unit

/**
 * Represents a connector for remote procedure call (RPC) communication.
 * This class is responsible for sending and receiving [RPCMessage] over a specified transport.
 *
 * @param SubscriptionKey the type of the subscription key used for message subscriptions.
 * @param serialFormat the serial format used for encoding and decoding [RPCMessage].
 * @param transport the transport used for sending and receiving encoded RPC messages.
 * @param waitForSubscribers a flag indicating whether the connector should wait for subscribers
 * if no service is available to process the message immediately.
 * If false, the endpoint that sent the message will receive a [RPCMessage.CallException]
 * that says that there were no services to process its message.
 * @param isServer flag indication whether this is a server or a client.
 * @param getKey a lambda function that returns the subscription key for a given [RPCMessage].
 */
@InternalKRPCApi
public class RPCConnector<SubscriptionKey>(
    private val serialFormat: SerialFormat,
    private val transport: RPCTransport,
    private val waitForSubscribers: Boolean = true,
    isServer: Boolean,
    private val getKey: RPCMessage.() -> SubscriptionKey,
) : RPCMessageSender, CoroutineScope by transport {
    private val role = if (isServer) SERVER_ROLE else CLIENT_ROLE
    private val logger = CommonLogger.initialized().logger(objectId(role))

    private val mutex = Mutex()

    private val waiting = mutableMapOf<SubscriptionKey, MutableList<RPCMessage>>()
    private val subscriptions = mutableMapOf<SubscriptionKey, RPCMessageHandler>()

    private val dumpLogger by lazy { DumpLoggerContainer.provide() }

    override suspend fun sendMessage(message: RPCMessage) {
        val transportMessage = when (serialFormat) {
            is StringFormat -> {
                RPCTransportMessage.StringMessage(serialFormat.encodeToString(message))
            }

            is BinaryFormat -> {
                RPCTransportMessage.BinaryMessage(serialFormat.encodeToByteArray(message))
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

    public suspend fun subscribeToMessages(key: SubscriptionKey, handler: RPCMessageHandler) {
        mutex.withLock {
            subscriptions[key] = handler
            processWaiters(key, handler)
        }
    }

    init {
        launch {
            while (isActive) {
                processMessage(transport.receive())
            }
        }
    }

    private suspend fun processMessage(transportMessage: RPCTransportMessage) {
        val message: RPCMessage = when {
            serialFormat is StringFormat && transportMessage is RPCTransportMessage.StringMessage -> {
                serialFormat.decodeFromString(transportMessage.value)
            }

            serialFormat is BinaryFormat && transportMessage is RPCTransportMessage.BinaryMessage -> {
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

    private suspend fun processMessage(message: RPCMessage) {
        mutex.withLock {
            val done = tryHandle(message)

            if (!done) {
                if (waitForSubscribers) {
                    waiting.getOrPut(message.getKey()) { mutableListOf() }.add(message)

                    logger.warn {
                        "No registered service of ${message.serviceType} service type " +
                                "was able to process message at the moment. Waiting for new services."
                    }

                    return
                }

                val cause = IllegalStateException(
                    "Failed to process call ${message.callId} for service ${message.serviceType}, " +
                            "${subscriptions.size} attempts failed"
                )

                val callException = RPCMessage.CallException(
                    callId = message.callId,
                    serviceType = message.serviceType,
                    cause = serializeException(cause)
                )

                sendMessage(callException)
            }
        }
    }

    private suspend fun tryHandle(message: RPCMessage, handler: RPCMessageHandler? = null): Boolean {
        val subscriber = handler ?: subscriptions[message.getKey()] ?: return false

        val result = runCatching {
            subscriber(message)
        }

        return when {
            result.isFailure -> {
                logger.error(result.exceptionOrNull()) { "Service failed" }
                false
            }

            else -> {
                true
            }
        }
    }

    private suspend fun processWaiters(key: SubscriptionKey, handler: RPCMessageHandler) {
        if (waiting.isEmpty()) return

        val iterator = waiting[key]?.iterator() ?: return
        while (iterator.hasNext()) {
            val message = iterator.next()

            if (tryHandle(message, handler)) {
                iterator.remove()
            }
        }
    }

    internal companion object {
        const val SEND_PHASE = "Send"
        const val RECEIVE_PHASE = "Receive"

        const val SERVER_ROLE = "Server"
        const val CLIENT_ROLE = "Client"

        private fun RPCTransportMessage.dump(): String {
            return when (this) {
                is RPCTransportMessage.StringMessage -> value
                is RPCTransportMessage.BinaryMessage -> value.toHexStringInternal()
            }
        }
    }
}
