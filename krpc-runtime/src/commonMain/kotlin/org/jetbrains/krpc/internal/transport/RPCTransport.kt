package org.jetbrains.krpc.internal.transport

import kotlinx.coroutines.CoroutineScope
import org.jetbrains.krpc.RPCTransportMessage
import org.jetbrains.krpc.internal.InternalKRPCApi

@InternalKRPCApi
interface RPCTransport : CoroutineScope {
    suspend fun send(message: RPCTransportMessage)

    suspend fun receive(): RPCTransportMessage
}
