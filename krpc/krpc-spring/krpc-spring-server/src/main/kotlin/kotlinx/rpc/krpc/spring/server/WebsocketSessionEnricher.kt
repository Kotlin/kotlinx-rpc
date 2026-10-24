/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import kotlinx.coroutines.currentCoroutineContext
import org.springframework.web.socket.WebSocketSession
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

internal open class WebsocketSessionEnricher : TransportContextEnricher {
    override fun enrich(session: WebSocketSession, coroutineContext: CoroutineContext): CoroutineContext {
        return coroutineContext + WebSocketSessionHolder(session)
    }
}

public data class WebSocketSessionHolder(
    val session: WebSocketSession
) : AbstractCoroutineContextElement(WebSocketSessionHolder) {
    public companion object Key : CoroutineContext.Key<WebSocketSessionHolder>
}

public suspend fun getWebSocketSession(): WebSocketSession =
    currentCoroutineContext()[WebSocketSessionHolder]?.session ?: error("WebSocket session not found")