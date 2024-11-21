/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.client

import io.ktor.websocket.*
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.client.KrpcClient
import kotlinx.rpc.krpc.ktor.KtorTransport

/**
 * [RpcClient] implementation for Ktor, containing [webSocketSession] object,
 * that is used to maintain connection.
 */
public interface KtorRpcClient : RpcClient {
    public val webSocketSession: WebSocketSession
}

internal class KtorKrpcClientImpl(
    override val webSocketSession: WebSocketSession,
    config: KrpcConfig.Client,
): KrpcClient(config, KtorTransport(webSocketSession)), KtorRpcClient

