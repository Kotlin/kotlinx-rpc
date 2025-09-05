/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.client.internal

import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.internal.*
import kotlinx.serialization.SerialFormat

internal class KrpcClientConnector private constructor(
    private val connector: KrpcConnector
) : KrpcMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transport: KrpcTransport,
        config: KrpcConfig.Connector,
    ) : this(
        KrpcConnector(serialFormat, transport, config, isServer = false)
    )

    suspend fun unsubscribeFromMessages(serviceTypeString: String, callId: String) {
        connector.unsubscribeFromMessages(HandlerKey.ServiceCall(serviceTypeString, callId))
    }

    suspend fun subscribeToCallResponse(
        serviceTypeString: String,
        callId: String,
        subscription: suspend (KrpcCallMessage) -> Unit,
    ) {
        connector.subscribeToMessages(HandlerKey.ServiceCall(serviceTypeString, callId)) {
            subscription(it)
        }
    }

    suspend fun subscribeToProtocolMessages(subscription: suspend (KrpcProtocolMessage) -> Unit) {
        connector.subscribeToMessages(HandlerKey.Protocol) {
            subscription(it)
        }
    }

    suspend fun subscribeToGenericMessages(subscription: suspend (KrpcGenericMessage) -> Unit) {
        connector.subscribeToMessages(HandlerKey.Generic) {
            subscription(it)
        }
    }
}
