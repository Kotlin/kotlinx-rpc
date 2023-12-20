package org.jetbrains.krpc.client.internal

import kotlinx.serialization.SerialFormat
import org.jetbrains.krpc.internal.transport.RPCConnector
import org.jetbrains.krpc.internal.transport.RPCMessage
import org.jetbrains.krpc.internal.transport.RPCMessageSender
import org.jetbrains.krpc.internal.transport.RPCTransport

internal data class CallSubscriptionId(
    val serviceTypeString: String,
    val callId: String,
)

internal class RPCClientConnector(
    private val connector: RPCConnector<CallSubscriptionId>
) : RPCMessageSender by connector {
    constructor(
        serialFormat: SerialFormat,
        transfer: RPCTransport,
        waitForServices: Boolean = false
    ) : this(
        RPCConnector(serialFormat, transfer, waitForServices) {
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
