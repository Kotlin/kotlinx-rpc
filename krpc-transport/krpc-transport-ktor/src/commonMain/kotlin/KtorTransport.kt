package org.jetbrains.krpc.transport.ktor

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class, DelicateCoroutinesApi::class)
class KtorTransport(
    private val json: Json,
    private val webSocketSession: WebSocketSession
) : RPCTransport {
    // Transport job should always be cancelled and never closed
    private val transportJob = Job()

    override val coroutineContext: CoroutineContext = webSocketSession.coroutineContext + transportJob

    private val incomingFlow = MutableSharedFlow<RPCMessage>()

    init {
        // Close the socket when the transport job is cancelled manually
        transportJob.invokeOnCompletion(onCancelling = true) {
            if (!webSocketSession.coroutineContext.isActive) return@invokeOnCompletion
            GlobalScope.launch {
                webSocketSession.close()
            }
        }
    }

    init {
        launch {
            for (message in webSocketSession.incoming) {
                check(message is Frame.Text)
                val messageText = message.readText()
                val rpcMessage = json.decodeFromString<RPCMessage>(messageText)
                incomingFlow.emit(rpcMessage)

            }

            webSocketSession.close()
            // Cancelling the job when the socket is closed from the other side
            transportJob.cancel()
        }
    }

    override val incoming: SharedFlow<RPCMessage> = incomingFlow

    override suspend fun send(message: RPCMessage) {
        val messageText = json.encodeToString(message)
        webSocketSession.send(messageText)
    }
}