package org.jetbrains.krpc.transport.ktor.client

import io.ktor.websocket.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCTransport
import org.jetbrains.krpc.client.KRPCClient
import org.jetbrains.krpc.transport.ktor.KtorTransport

class KtorRPCClient(
    webSocketSession: WebSocketSession,
    config: RPCConfig.Client,
): KRPCClient(config), RPCTransport by KtorTransport(webSocketSession)
