/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor

import io.ktor.websocket.*
import kotlinx.coroutines.*
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.RPCTransportMessage
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class, DelicateCoroutinesApi::class)
class KtorTransport(private val webSocketSession: WebSocketSession): RPCTransport {
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
