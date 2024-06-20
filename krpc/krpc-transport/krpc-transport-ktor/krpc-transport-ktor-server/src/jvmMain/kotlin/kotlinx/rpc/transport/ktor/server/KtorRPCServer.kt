/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.transport.ktor.server

import io.ktor.websocket.*
import kotlinx.rpc.RPCConfig
import kotlinx.rpc.server.KRPCServer
import kotlinx.rpc.transport.ktor.KtorTransport

internal class KtorRPCServer(
    webSocketSession: WebSocketSession,
    config: RPCConfig.Server,
) : KRPCServer(config, KtorTransport(webSocketSession))
