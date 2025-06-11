/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server.internal

import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.internal.*
import kotlinx.serialization.SerialFormat

internal sealed interface MessageKey {
    data class Service(val serviceTypeString: String): MessageKey

    data object Protocol: MessageKey

    data object Generic: MessageKey
}

internal class KrpcServerConnector private constructor(
    private val connector: KrpcConnector<MessageKey>,
): KrpcMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transport: KrpcTransport,
        waitForServices: Boolean = false,
    ) : this(
        KrpcConnector(serialFormat, transport, waitForServices, isServer = true) {
            when (this) {
                is KrpcCallMessage -> MessageKey.Service(serviceType)
                is KrpcProtocolMessage -> MessageKey.Protocol
                is KrpcGenericMessage -> MessageKey.Generic
            }
        }
    )

    fun unsubscribeFromServiceMessages(serviceTypeString: String, callback: () -> Unit = {}) {
        connector.unsubscribeFromMessages(MessageKey.Service(serviceTypeString), callback)
    }

    suspend fun subscribeToServiceMessages(
        serviceTypeString: String,
        subscription: suspend (KrpcCallMessage) -> Unit,
    ) {
        connector.subscribeToMessages(MessageKey.Service(serviceTypeString)) {
            subscription(it as KrpcCallMessage)
        }
    }

    suspend fun subscribeToProtocolMessages(subscription: suspend (KrpcProtocolMessage) -> Unit) {
        connector.subscribeToMessages(MessageKey.Protocol) {
            subscription(it as KrpcProtocolMessage)
        }
    }

    @Suppress("unused")
    suspend fun subscribeToGenericMessages(subscription: suspend (KrpcGenericMessage) -> Unit) {
        connector.subscribeToMessages(MessageKey.Generic) {
            subscription(it as KrpcGenericMessage)
        }
    }
}
