/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.transport.ktor.client

import io.ktor.websocket.*
import kotlinx.rpc.RPCClient
import kotlinx.rpc.RPCConfig
import kotlinx.rpc.client.KRPCClient
import kotlinx.rpc.transport.ktor.KtorTransport

/**
 * [RPCClient] implementation for Ktor, containing [webSocketSession] object,
 * that is used to maintain connection.
 */
public interface KtorRPCClient : RPCClient {
    public val webSocketSession: WebSocketSession
}

internal class KtorRPCClientImpl(
    override val webSocketSession: WebSocketSession,
    config: RPCConfig.Client,
): KRPCClient(config, KtorTransport(webSocketSession)), KtorRPCClient

