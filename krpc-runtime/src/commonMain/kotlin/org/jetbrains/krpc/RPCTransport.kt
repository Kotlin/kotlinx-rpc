package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope

sealed interface RPCTransportMessage {
    class StringMessage(val value: String) : RPCTransportMessage

    class BinaryMessage(val value: ByteArray) : RPCTransportMessage
}

interface RPCTransport : CoroutineScope {
    suspend fun send(message: RPCTransportMessage)

    suspend fun receive(): RPCTransportMessage
}
