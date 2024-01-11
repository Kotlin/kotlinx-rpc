package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope

/**
 * A single message that can be transferred from one RPC endpoint to another.
 * Can be either of string or binary type.
 */
sealed interface RPCTransportMessage {
    class StringMessage(val value: String) : RPCTransportMessage

    class BinaryMessage(val value: ByteArray) : RPCTransportMessage
}

/**
 * An abstraction of transport capabilities for KRPCClient and KRPCServer.
 *
 * For developers of custom transports:
 * - The implementation should be able to handle both binary and string formats,
 * though not necessary if you absolutely sure that only one will be supplied and received.
 * - The KRPCClient and KRPCServer suppose that they have exclusive instance of transport.
 * That means that each client or/and server should have only one transport instance,
 * otherwise some messages may be lost or processed incorrectly.
 * - The implementation should manage lifetime of the connection using its [CoroutineScope].
 *
 * Good example of the implementation is KtorTransport, that uses websocket protocol to deliver messages.
 */
interface RPCTransport : CoroutineScope {
    /**
     * Sends a single encoded RPC message over network (or any other medium) to a peer endpoint.
     *
     * @param message a message to send. Either of string or binary type.
     */
    suspend fun send(message: RPCTransportMessage)

    /**
     * Suspends until next RPC message from a peer endpoint is received and then returns it.
     *
     * @return received RPC message.
     */
    suspend fun receive(): RPCTransportMessage
}
