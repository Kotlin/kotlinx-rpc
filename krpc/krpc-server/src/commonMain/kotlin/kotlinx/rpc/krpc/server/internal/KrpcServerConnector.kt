/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.server.internal

import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.internal.*
import kotlinx.serialization.SerialFormat

internal class KrpcServerConnector private constructor(
    private val connector: KrpcConnector,
): KrpcMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transport: KrpcTransport,
        config: KrpcConfig.Connector,
    ) : this(
        KrpcConnector(serialFormat, transport, config, isServer = true)
    )

    fun unsubscribeFromServiceMessages(serviceTypeString: String, callback: suspend () -> Unit = {}) {
        connector.unsubscribeFromMessagesAsync(HandlerKey.Service(serviceTypeString), callback)
    }

    suspend fun unsubscribeFromCallMessages(serviceTypeString: String, callId: String) {
        connector.unsubscribeFromMessages(HandlerKey.ServiceCall(serviceTypeString, callId))
    }

    suspend fun subscribeToServiceMessages(
        serviceTypeString: String,
        subscription: suspend (KrpcCallMessage) -> Unit,
    ) {
        connector.subscribeToMessages(HandlerKey.Service(serviceTypeString)) {
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
