/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.transport.ktor

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.rpc.RPCTransport
import kotlinx.rpc.RPCTransportMessage
import kotlinx.rpc.internal.InternalRPCApi
import kotlin.coroutines.CoroutineContext

@InternalRPCApi
@OptIn(InternalCoroutinesApi::class, DelicateCoroutinesApi::class)
public class KtorTransport(private val webSocketSession: WebSocketSession): RPCTransport {
    // Transport job should always be cancelled and never closed
    private val transportJob = Job()

    override val coroutineContext: CoroutineContext = webSocketSession.coroutineContext + transportJob

    init {
        // Close the socket when the transport job is cancelled manually
        transportJob.invokeOnCompletion(onCancelling = true) {
            if (!webSocketSession.coroutineContext.isActive) return@invokeOnCompletion
            GlobalScope.launch {
                webSocketSession.close()
            }
        }
    }

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
