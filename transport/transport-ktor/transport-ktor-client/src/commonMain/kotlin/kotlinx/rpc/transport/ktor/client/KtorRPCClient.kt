/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.transport.ktor.client

import io.ktor.websocket.*
import kotlinx.rpc.RPCConfig
import kotlinx.rpc.client.KRPCClient
import kotlinx.rpc.transport.ktor.KtorTransport

internal class KtorRPCClient(
    webSocketSession: WebSocketSession,
    config: RPCConfig.Client,
): KRPCClient(config, KtorTransport(webSocketSession))
