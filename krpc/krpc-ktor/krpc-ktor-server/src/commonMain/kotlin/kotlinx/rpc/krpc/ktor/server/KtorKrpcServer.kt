/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.server

import io.ktor.websocket.*
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.ktor.KtorTransport
import kotlinx.rpc.krpc.server.KrpcServer

internal class KtorKrpcServer(
    webSocketSession: WebSocketSession,
    config: KrpcConfig.Server,
) : KrpcServer(config, KtorTransport(webSocketSession))
