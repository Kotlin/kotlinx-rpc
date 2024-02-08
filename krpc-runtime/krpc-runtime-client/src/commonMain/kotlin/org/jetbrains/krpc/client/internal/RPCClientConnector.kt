/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client.internal

import kotlinx.serialization.SerialFormat
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.transport.RPCCallMessage
import org.jetbrains.krpc.internal.transport.RPCConnector
import org.jetbrains.krpc.internal.transport.RPCMessageSender
import org.jetbrains.krpc.internal.transport.RPCProtocolMessage

internal sealed interface CallSubscriptionId {
    data class Service(
        val serviceTypeString: String,
        val callId: String,
    ) : CallSubscriptionId

    @Suppress("ConvertObjectToDataObject") // not supported in 1.8.22 or earlier
    object Protocol : CallSubscriptionId
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
            }
        }
    )

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
}
