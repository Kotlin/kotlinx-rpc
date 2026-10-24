/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.KrpcTransportMessage
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import kotlin.coroutines.CoroutineContext

/**
 * Connects one [WebSocketSession] to one kRPC server.
 *
 * The transport is complete when one of these happens:
 * - The kRPC server closes the transport after it sends its last message.
 * - Spring reports that the WebSocket connection is closed ([connectionClosed]).
 * - The [parent] job is cancelled.
 */
internal class KrpcWebSocketTransport(
    private val session: WebSocketSession,
    parent: Job,
    transportContextEnrichers: List<TransportContextEnricher>,
    exceptionHandler: CoroutineExceptionHandler,
) : KrpcTransport {
    private val job = SupervisorJob(parent)

    override val coroutineContext: CoroutineContext =
        transportContextEnrichers.fold(
            job +
                Dispatchers.IO +
                exceptionHandler +
                CoroutineName("krpc-spring-transport-${session.id}")
        ) { context, enricher ->
            enricher.enrich(session, context)
        }


    private val incoming = Channel<KrpcTransportMessage>(INCOMING_BUFFER_SIZE)
    private val sendMutex = Mutex()

    init {
        job.invokeOnCompletion { incoming.close() }
    }

    /**
     * Gives [message] to the kRPC server.
     *
     * Call it from the thread that reads the session, one message at a time, so that the message order stays the same.
     * When the buffer is full, the call blocks the thread until the server takes a message.
     *
     * @return `false` if the transport is complete and the message is dropped.
     */
    fun deliver(message: KrpcTransportMessage): Boolean {
        if (incoming.trySend(message).isSuccess) {
            return true
        }

        return try {
            runBlocking { incoming.send(message) }
            true
        } catch (_: ClosedSendChannelException) {
            false
        }
    }

    /** Runs [handler] once, when the transport is complete. Runs it immediately if the transport is already complete. */
    fun invokeOnCompletion(handler: () -> Unit) {
        job.invokeOnCompletion { handler() }
    }

    /** Completes the transport. Call it only when the WebSocket connection is closed. */
    fun connectionClosed() {
        job.cancel()
    }

    override suspend fun send(message: KrpcTransportMessage) {
        // WebSocketSession.sendMessage isn't thread-safe.
        sendMutex.withLock {
            when (message) {
                is KrpcTransportMessage.StringMessage -> session.sendMessage(TextMessage(message.value))
                is KrpcTransportMessage.BinaryMessage -> session.sendMessage(BinaryMessage(message.value))
            }
        }
    }

    override suspend fun receive(): KrpcTransportMessage {
        return incoming.receive()
    }

    private companion object {
        const val INCOMING_BUFFER_SIZE = 64
    }
}
