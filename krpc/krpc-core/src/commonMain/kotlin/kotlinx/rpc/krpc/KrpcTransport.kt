/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc

import kotlinx.coroutines.CoroutineScope

/**
 * A single message that can be transferred from one RPC endpoint to another.
 * Can be either of string or binary type.
 */
public sealed interface KrpcTransportMessage {
    /**
     * A single message of the string type that can be transferred from one RPC endpoint to another.
     */
    public class StringMessage(public val value: String) : KrpcTransportMessage

    /**
     * A single message of the binary type that can be transferred from one RPC endpoint to another.
     */
    public class BinaryMessage(public val value: ByteArray) : KrpcTransportMessage
}

/**
 * An abstraction of transport capabilities for KrpcClient and KrpcServer.
 *
 * For developers of custom transports:
 * - The implementation should be able to handle both binary and string formats,
 * though not necessary if you absolutely sure that only one will be supplied and received.
 * - The KrpcClient and KrpcServer suppose that they have an exclusive instance of transport.
 * That means that each client or/and server should have only one transport instance,
 * otherwise some messages may be lost or processed incorrectly.
 *
 * [CoroutineScope] is used to define connection's lifetime.
 * If canceled, no messages will be able to go to the other side,
 * so ideally, it should be canceled only after its client or server is.
 *
 * A good example of the implementation is KtorTransport, that uses websocket protocol to deliver messages.
 */
public interface KrpcTransport : CoroutineScope {
    /**
     * Sends a single encoded RPC message over a network (or any other medium) to a peer endpoint.
     *
     * @param message a message to send. Either of string or binary type.
     */
    public suspend fun send(message: KrpcTransportMessage)

    /**
     * Suspends until the next RPC message from a peer endpoint is received and then returns it.
     *
     * @return the received RPC message.
     */
    public suspend fun receive(): KrpcTransportMessage
}

/**
 * Suspends until the next RPC message from a peer endpoint is received and then returns it.
 *
 * @return the received RPC message as a [Result].
 */
public suspend fun KrpcTransport.receiveCatching(): Result<KrpcTransportMessage> {
    return runCatching { receive() }
}
