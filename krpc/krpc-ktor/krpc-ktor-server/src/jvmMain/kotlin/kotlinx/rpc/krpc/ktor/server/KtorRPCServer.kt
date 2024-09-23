/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.server

import io.ktor.websocket.*
import kotlinx.rpc.krpc.RPCConfig
import kotlinx.rpc.krpc.ktor.KtorTransport
import kotlinx.rpc.krpc.server.KRPCServer

internal class KtorRPCServer(
    webSocketSession: WebSocketSession,
    config: RPCConfig.Server,
) : KRPCServer(config, KtorTransport(webSocketSession))
