/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Job
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcTransportMessage
import org.springframework.beans.factory.BeanFactory
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.AbstractWebSocketHandler
import java.io.EOFException
import java.io.IOException
import java.nio.channels.ClosedChannelException
import kotlin.reflect.KClass

internal class KrpcWebSocketHandler(
    private val services: List<KrpcServiceBean>,
    private val config: KrpcConfig.Server,
    private val webSocketSessionRegistry: WebSocketSessionRegistry,
    private val beanFactory: BeanFactory,
    private val sessionsJob: Job,
) : AbstractWebSocketHandler() {

    private val exceptionHandler = CoroutineExceptionHandler { context, exception ->
        logger.error(exception) { "Unhandled exception in kRPC coroutine ${context[CoroutineName.Key]?.name}" }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val transport = KrpcWebSocketTransport(session, sessionsJob, exceptionHandler)

        val server = try {
            SpringKrpcServer(config, transport).also { server ->
                services.forEach { service -> server.register(service.rpcInterface, service.beanName) }
            }
        } catch (e: Throwable) {
            transport.connectionClosed()
            throw e
        }

        webSocketSessionRegistry.put(session, server)

        /* Register the handler after `put`.
        If the transport is already complete, the handler runs now and removes the entry. */
        server.invokeOnTransportCompletion {
            webSocketSessionRegistry.remove(session)
            if (session.isOpen) {
                runCatching { session.close(CloseStatus.NORMAL) }
                    .onFailure { logger.debug(it) { "Failed to close WebSocket ${session.id}" } }
            }
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        deliver(session, KrpcTransportMessage.StringMessage(message.payload))
    }

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        val payload = message.payload
        val bytes = ByteArray(payload.remaining()).also { payload.duplicate().get(it) }
        deliver(session, KrpcTransportMessage.BinaryMessage(bytes))
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        webSocketSessionRegistry.remove(session)?.connectionClosed()
    }

    override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
        if (exception.isClientDisconnect) {
            logger.debug { "WebSocket ${session.id} disconnected abruptly: ${exception.message}" }
        } else {
            logger.warn(exception) { "Transport error on WebSocket ${session.id}" }
        }

        webSocketSessionRegistry.remove(session)?.connectionClosed()
        if (session.isOpen) {
            runCatching { session.close(CloseStatus.SERVER_ERROR) }
        }
    }

    // Spring calls the message handlers of one session one after another, so the messages arrive in order.
    private fun deliver(session: WebSocketSession, message: KrpcTransportMessage) {
        val server = webSocketSessionRegistry.get(session)
        if (server == null || !server.deliver(message)) {
            logger.debug { "Dropped a message on WebSocket ${session.id}, because its kRPC server is closed" }
        }
    }

    private fun SpringKrpcServer.register(rpcInterface: KClass<*>, beanName: String) {
        registerTyped(rpcInterface) { beanFactory.getBean(beanName) }
    }

    // The type check of the kRPC compiler plugin rejects a concrete type argument without @Rpc.
    // The caller gives a star-projected class, so the compiler infers a captured type for Service.
    @Suppress("UNCHECKED_CAST")
    private fun <@Rpc Service : Any> SpringKrpcServer.registerTyped(
        rpcInterface: KClass<Service>,
        factory: () -> Any,
    ) {
        registerService(rpcInterface) { factory() as Service }
    }

    private val Throwable.isClientDisconnect: Boolean
        get() =
            this is EOFException ||
                this is ClosedChannelException ||
                (this is IOException && message?.contains("Broken pipe", ignoreCase = true) == true)

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}