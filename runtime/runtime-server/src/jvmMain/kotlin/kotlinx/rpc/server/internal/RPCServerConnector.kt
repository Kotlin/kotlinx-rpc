/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.server.internal

import kotlinx.rpc.RPCTransport
import kotlinx.rpc.internal.transport.*
import kotlinx.serialization.SerialFormat

internal sealed interface MessageKey {
    data class Service(val serviceTypeString: String): MessageKey

    @Suppress("ConvertObjectToDataObject") // not supported in 1.8.22 or earlier
    object Protocol: MessageKey

    @Suppress("ConvertObjectToDataObject") // not supported in 1.8.22 or earlier
    object Generic: MessageKey
}

internal class RPCServerConnector private constructor(
    private val connector: RPCConnector<MessageKey>,
): RPCMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transport: RPCTransport,
        waitForServices: Boolean = false,
    ) : this(
        RPCConnector(serialFormat, transport, waitForServices, isServer = true) {
            when (this) {
                is RPCCallMessage -> MessageKey.Service(serviceType)
                is RPCProtocolMessage -> MessageKey.Protocol
                is RPCGenericMessage -> MessageKey.Generic
            }
        }
    )

    suspend fun subscribeToServiceMessages(
        serviceTypeString: String,
        subscription: suspend (RPCCallMessage) -> Unit,
    ) {
        connector.subscribeToMessages(MessageKey.Service(serviceTypeString)) {
            subscription(it as RPCCallMessage)
        }
    }

    suspend fun subscribeToProtocolMessages(subscription: suspend (RPCProtocolMessage) -> Unit) {
        connector.subscribeToMessages(MessageKey.Protocol) {
            subscription(it as RPCProtocolMessage)
        }
    }

    @Suppress("unused")
    suspend fun subscribeToGenericMessages(subscription: suspend (RPCGenericMessage) -> Unit) {
        connector.subscribeToMessages(MessageKey.Generic) {
            subscription(it as RPCGenericMessage)
        }
    }
}
