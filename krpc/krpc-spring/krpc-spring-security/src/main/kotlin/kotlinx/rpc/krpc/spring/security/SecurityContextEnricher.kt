/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.security

import kotlinx.rpc.krpc.spring.server.KrpcContextEnricher
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.web.socket.WebSocketSession
import kotlin.coroutines.CoroutineContext

/**
 * Adds the WebSocket session's authentication to `SecurityContextHolder` while kRPC coroutines run.
 *
 * If the session principal isn't an [Authentication], this enricher installs an empty security context.
 *
 * The default strategy is the current strategy from `SecurityContextHolder`.
 *
 * @param strategy The strategy that reads and writes Spring Security's security context.
 */
public open class SecurityContextEnricher(
    private val strategy: SecurityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy(),
) : KrpcContextEnricher {
    /**
     * Returns a coroutine element that sets the session's authentication in `SecurityContextHolder`.
     *
     * @param session The WebSocket session with an authentication principal, if present.
     * @return The coroutine context element for the session's authentication.
     */
    override fun contextFor(session: WebSocketSession): CoroutineContext =
        SecurityContextElement(session.principal as? Authentication, strategy)
}
