/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor

import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage

@InternalRpcApi
public class KtorTransport(
    private val webSocketSession: WebSocketSession,
) : KrpcTransport, CoroutineScope by webSocketSession {

    /**
     * Sends a single encoded RPC message over network (or any other medium) to a peer endpoint.
     *
     * @param message a message to send. Either of string or binary type.
     */
    override suspend fun send(message: KrpcTransportMessage) {
        when (message) {
            is KrpcTransportMessage.StringMessage -> {
                webSocketSession.send(message.value)
            }

            is KrpcTransportMessage.BinaryMessage -> {
                webSocketSession.send(message.value)
            }
        }
    }

    /**
     * Suspends until next RPC message from a peer endpoint is received and then returns it.
     *
     * @return received RPC message.
     */
    override suspend fun receive(): KrpcTransportMessage {
        return when (val message = webSocketSession.incoming.receive()) {
            is Frame.Text -> {
                KrpcTransportMessage.StringMessage(message.readText())
            }

            is Frame.Binary -> {
                KrpcTransportMessage.BinaryMessage(message.readBytes())
            }

            else -> {
                error("Unsupported websocket frame type: ${message::class}. Expected Frame.Text or Frame.Binary")
            }
        }
    }
}
