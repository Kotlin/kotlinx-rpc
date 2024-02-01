/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.client.internal

import kotlinx.serialization.SerialFormat
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.transport.RPCConnector
import org.jetbrains.krpc.internal.transport.RPCMessage
import org.jetbrains.krpc.internal.transport.RPCMessageSender

internal data class CallSubscriptionId(
    val serviceTypeString: String,
    val callId: String,
)

internal class RPCClientConnector(
    private val connector: RPCConnector<CallSubscriptionId>
) : RPCMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transport: RPCTransport,
        waitForServices: Boolean = false,
    ) : this(
        RPCConnector(serialFormat, transport, waitForServices, isServer = false) {
            CallSubscriptionId(serviceType, callId)
        }
    )

    suspend fun subscribeToCallResponse(
        serviceTypeString: String,
        callId: String,
        subscription: suspend (RPCMessage) -> Unit,
    ) {
        connector.subscribeToMessages(CallSubscriptionId(serviceTypeString, callId), subscription)
    }
}
