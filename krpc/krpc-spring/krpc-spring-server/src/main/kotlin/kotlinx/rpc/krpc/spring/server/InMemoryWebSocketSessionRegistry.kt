/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

/**
 * Stores one [SpringKrpcServer] for each WebSocket session in memory.
 *
 * The registry uses a thread-safe map keyed by the session ID.
 */
public class InMemoryWebSocketSessionRegistry : WebSocketSessionRegistry {
    private val sessions = ConcurrentHashMap<String, SpringKrpcServer>()

    override fun put(session: WebSocketSession, server: SpringKrpcServer) {
        sessions[session.id] = server
    }

    override fun get(session: WebSocketSession): SpringKrpcServer? {
        return sessions[session.id]
    }

    override fun remove(session: WebSocketSession): SpringKrpcServer? {
        return sessions.remove(session.id)
    }
}
