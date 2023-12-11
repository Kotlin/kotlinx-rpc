package org.jetbrains.krpc.transport.ktor

import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import org.jetbrains.krpc.RPCMessage
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.internal.unsupportedSerialFormatError
import kotlin.coroutines.CoroutineContext

@OptIn(InternalCoroutinesApi::class, DelicateCoroutinesApi::class)
class KtorTransport(
    private val serialFormat: SerialFormat,
    private val webSocketSession: WebSocketSession,
) : RPCTransport() {
    // Transport job should always be cancelled and never closed
    private val transportJob = Job()

    override val coroutineContext: CoroutineContext = webSocketSession.coroutineContext + transportJob

    private val subscribers = mutableListOf<suspend (RPCMessage) -> Boolean>()
    private val first = Job()


    init {
        // Close the socket when the transport job is cancelled manually
        transportJob.invokeOnCompletion(onCancelling = true) {
            if (!webSocketSession.coroutineContext.isActive) return@invokeOnCompletion
            GlobalScope.launch {
                webSocketSession.close()
            }
        }

        launch {
            first.join()

            for (message in webSocketSession.incoming) {
                val rpcMessage = when (serialFormat) {
                    is StringFormat -> {
                        check(message is Frame.Text)
                        val messageText = message.readText()
                        serialFormat.decodeFromString<RPCMessage>(messageText)
                    }

                    is BinaryFormat -> {
                        check(message is Frame.Binary)
                        val messageText = message.readBytes()
                        serialFormat.decodeFromByteArray<RPCMessage>(messageText)
                    }

                    else -> {
                        unsupportedSerialFormatError(serialFormat)
                    }
                }
                subscribers.forEach { it(rpcMessage) }
            }

            webSocketSession.close()
            transportJob.cancel()
        }
    }

    override suspend fun send(message: RPCMessage) {
        when (serialFormat) {
            is StringFormat -> {
                val messageText = serialFormat.encodeToString(message)
                webSocketSession.send(messageText)
            }
            is BinaryFormat -> {
                val messageText = serialFormat.encodeToByteArray(message)
                webSocketSession.send(messageText)
            }
        }
    }

    override suspend fun subscribe(block: suspend (RPCMessage) -> Boolean) {
        subscribers.add(block)
        first.complete()
    }
}
