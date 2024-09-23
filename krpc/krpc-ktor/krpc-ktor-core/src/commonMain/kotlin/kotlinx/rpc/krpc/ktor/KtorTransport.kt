/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.rpc.internal.utils.InternalRPCApi
import kotlinx.rpc.krpc.RPCTransport
import kotlinx.rpc.krpc.RPCTransportMessage

@InternalRPCApi
public class KtorTransport(
    private val webSocketSession: WebSocketSession,
) : RPCTransport, CoroutineScope by webSocketSession {

    /**
     * Sends a single encoded RPC message over network (or any other medium) to a peer endpoint.
     *
     * @param message a message to send. Either of string or binary type.
     */
    override suspend fun send(message: RPCTransportMessage) {
        when (message) {
            is RPCTransportMessage.StringMessage -> {
                webSocketSession.send(message.value)
            }

            is RPCTransportMessage.BinaryMessage -> {
                webSocketSession.send(message.value)
            }
        }
    }

    /**
     * Suspends until next RPC message from a peer endpoint is received and then returns it.
     *
     * @return received RPC message.
     */
    override suspend fun receive(): RPCTransportMessage {
        return when (val message = webSocketSession.incoming.receive()) {
            is Frame.Text -> {
                RPCTransportMessage.StringMessage(message.readText())
            }

            is Frame.Binary -> {
                RPCTransportMessage.BinaryMessage(message.readBytes())
            }

            else -> {
                error("Unsupported websocket frame type: ${message::class}. Expected Frame.Text or Frame.Binary")
            }
        }
    }
}
