/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.spring.server

import org.springframework.web.socket.WebSocketSession

/**
 * Stores the kRPC server associated with each WebSocket session.
 *
 * Implementations must be thread-safe. Spring calls them from the threads of different sessions at the same time.
 */
public interface WebSocketSessionRegistry {
    /**
     * Stores [server] for [session] and replaces the server stored before.
     *
     * @param session The WebSocket session of the server.
     * @param server The server to store.
     */
    public fun put(session: WebSocketSession, server: SpringKrpcServer)

    /**
     * Returns the server for [session].
     *
     * @param session The WebSocket session to check.
     * @return The stored server, or `null` if no server exists.
     */
    public fun get(session: WebSocketSession): SpringKrpcServer?

    /**
     * Removes and returns the server for [session], if one exists.
     *
     * @param session The WebSocket session whose server to remove.
     * @return The removed server, or `null` if no server exists.
     */
    public fun remove(session: WebSocketSession): SpringKrpcServer?
}
