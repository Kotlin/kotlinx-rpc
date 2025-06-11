/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client.internal

import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.internal.*
import kotlinx.serialization.SerialFormat

internal sealed interface CallSubscriptionId {
    data class Service(
        val serviceTypeString: String,
        val callId: String,
    ) : CallSubscriptionId

    data object Protocol : CallSubscriptionId

    data object Generic : CallSubscriptionId
}

internal class KrpcClientConnector private constructor(
    private val connector: KrpcConnector<CallSubscriptionId>
) : KrpcMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transport: KrpcTransport,
        waitForServices: Boolean = false,
    ) : this(
        KrpcConnector(serialFormat, transport, waitForServices, isServer = false) {
            when (this) {
                is KrpcCallMessage -> CallSubscriptionId.Service(serviceType, callId)
                is KrpcProtocolMessage -> CallSubscriptionId.Protocol
                is KrpcGenericMessage -> CallSubscriptionId.Generic
            }
        }
    )

    fun unsubscribeFromMessages(serviceTypeString: String, callId: String, callback: () -> Unit = {}) {
        connector.unsubscribeFromMessages(CallSubscriptionId.Service(serviceTypeString, callId), callback)
    }

    suspend fun subscribeToCallResponse(
        serviceTypeString: String,
        callId: String,
        subscription: suspend (KrpcCallMessage) -> Unit,
    ) {
        connector.subscribeToMessages(CallSubscriptionId.Service(serviceTypeString, callId)) {
            subscription(it as KrpcCallMessage)
        }
    }

    suspend fun subscribeToProtocolMessages(subscription: suspend (KrpcProtocolMessage) -> Unit) {
        connector.subscribeToMessages(CallSubscriptionId.Protocol) {
            subscription(it as KrpcProtocolMessage)
        }
    }

    @Suppress("unused")
    suspend fun subscribeToGenericMessages(subscription: suspend (KrpcGenericMessage) -> Unit) {
        connector.subscribeToMessages(CallSubscriptionId.Generic) {
            subscription(it as KrpcGenericMessage)
        }
    }
}
