package org.jetbrains.krpc

sealed interface RPCTransportMessage {
    class StringMessage(val value: String) : RPCTransportMessage

    class BinaryMessage(val value: ByteArray) : RPCTransportMessage
}
