/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope

/**
 * A single message that can be transferred from one RPC endpoint to another.
 * Can be either of string or binary type.
 */
public sealed interface RPCTransportMessage {
    public class StringMessage(public val value: String) : RPCTransportMessage

    public class BinaryMessage(public val value: ByteArray) : RPCTransportMessage
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
public interface RPCTransport : CoroutineScope {
    /**
     * Sends a single encoded RPC message over network (or any other medium) to a peer endpoint.
     *
     * @param message a message to send. Either of string or binary type.
     */
    public suspend fun send(message: RPCTransportMessage)

    /**
     * Suspends until next RPC message from a peer endpoint is received and then returns it.
     *
     * @return received RPC message.
     */
    public suspend fun receive(): RPCTransportMessage

    /**
     * Suspends until next RPC message from a peer endpoint is received and then returns it.
     *
     * @return received RPC message as a [Result].
     */
    public suspend fun receiveCatching(): Result<RPCTransportMessage> {
        return runCatching { receive() }
    }
}
