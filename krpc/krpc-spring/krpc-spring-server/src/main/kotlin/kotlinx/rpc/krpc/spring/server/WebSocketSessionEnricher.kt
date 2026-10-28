/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.coroutines.currentCoroutineContext
import org.springframework.web.socket.WebSocketSession
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Enriches the coroutine context of `@RPC` Services with the @see[WebSocketSession].
 */
public open class WebSocketSessionEnricher : KrpcContextEnricher {
    override fun contextFor(session: WebSocketSession): CoroutineContext {
        return WebSocketSessionHolder(session)
    }
}

/**
 * Holds the @see[WebSocketSession] to provide access to it in a coroutine context.
 * Get it through @see[getWebSocketSession]
 */
public data class WebSocketSessionHolder(
    val session: WebSocketSession
) : AbstractCoroutineContextElement(WebSocketSessionHolder) {
    public companion object Key : CoroutineContext.Key<WebSocketSessionHolder>
}

/**
 * Provides access to the WebSocket session. Requires the @see[WebSocketSessionEnricher] to be registered as a bean.
 * ```
 * @Bean
 * fun websocketSessionEnricher(): WebsocketSessionEnricher = WebsocketSessionEnricher()
 *
 * @Rpc
 * class MyServiceImpl : MyService {
 *     suspend override message(): String {
 *         return getWebSocketSession().id
 *     }
 * }
 * ```
 *
 * @throws IllegalStateException if the WebSocket session is not found in the coroutine context.
 */
public suspend fun getWebSocketSession(): WebSocketSession =
    currentCoroutineContext()[WebSocketSessionHolder]?.session ?: error("WebSocket session not found")