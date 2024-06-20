/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.client.internal

import kotlinx.rpc.RPCTransport
import kotlinx.rpc.internal.transport.*
import kotlinx.serialization.SerialFormat

internal sealed interface CallSubscriptionId {
    data class Service(
        val serviceTypeString: String,
        val callId: String,
    ) : CallSubscriptionId

    @Suppress("ConvertObjectToDataObject") // not supported in 1.8.22 or earlier
    object Protocol : CallSubscriptionId

    @Suppress("ConvertObjectToDataObject") // not supported in 1.8.22 or earlier
    object Generic : CallSubscriptionId
}

internal class RPCClientConnector private constructor(
    private val connector: RPCConnector<CallSubscriptionId>
) : RPCMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transport: RPCTransport,
        waitForServices: Boolean = false,
    ) : this(
        RPCConnector(serialFormat, transport, waitForServices, isServer = false) {
            when (this) {
                is RPCCallMessage -> CallSubscriptionId.Service(serviceType, callId)
                is RPCProtocolMessage -> CallSubscriptionId.Protocol
                is RPCGenericMessage -> CallSubscriptionId.Generic
            }
        }
    )

    @Suppress("unused")
    fun unsubscribeFromMessages(serviceTypeString: String, callId: String) {
        connector.unsubscribeFromMessages(CallSubscriptionId.Service(serviceTypeString, callId))
    }

    suspend fun subscribeToCallResponse(
        serviceTypeString: String,
        callId: String,
        subscription: suspend (RPCCallMessage) -> Unit,
    ) {
        connector.subscribeToMessages(CallSubscriptionId.Service(serviceTypeString, callId)) {
            subscription(it as RPCCallMessage)
        }
    }

    suspend fun subscribeToProtocolMessages(subscription: suspend (RPCProtocolMessage) -> Unit) {
        connector.subscribeToMessages(CallSubscriptionId.Protocol) {
            subscription(it as RPCProtocolMessage)
        }
    }

    @Suppress("unused")
    suspend fun subscribeToGenericMessages(subscription: suspend (RPCGenericMessage) -> Unit) {
        connector.subscribeToMessages(CallSubscriptionId.Generic) {
            subscription(it as RPCGenericMessage)
        }
    }
}
