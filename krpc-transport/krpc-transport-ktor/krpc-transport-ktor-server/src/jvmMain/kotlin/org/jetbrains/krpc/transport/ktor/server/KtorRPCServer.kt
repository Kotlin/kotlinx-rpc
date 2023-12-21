package org.jetbrains.krpc.transport.ktor.server

import io.ktor.websocket.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.server.KRPCServer
import org.jetbrains.krpc.transport.ktor.KtorTransport

class KtorRPCServer(
    webSocketSession: WebSocketSession,
    config: RPCConfig.Server,
) : KRPCServer(config), RPCTransport by KtorTransport(webSocketSession)
