/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.client

import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.KrpcConfig
import kotlinx.rpc.krpc.KrpcConfigBuilder
import kotlinx.rpc.krpc.KrpcTransport
import kotlinx.rpc.krpc.client.KrpcClient
import kotlinx.rpc.krpc.ktor.KtorTransport
import kotlinx.rpc.krpc.rpcClientConfig

/**
 * [RpcClient] implementation for Ktor, containing [webSocketSession] object,
 * that is used to maintain connection.
 */
public interface KtorRpcClient : RpcClient {
    public val webSocketSession: Deferred<WebSocketSession>
}

internal class KtorKrpcClientImpl(
    private val pluginConfigBuilder: KrpcConfigBuilder.Client?,
    private val webSocketSessionFactory: suspend (
        configSetter: (KrpcConfigBuilder.Client.() -> Unit) -> Unit,
    ) -> WebSocketSession,
): KrpcClient(), KtorRpcClient {
    private var requestConfigBuilder: KrpcConfigBuilder.Client.() -> Unit = {}

    private val _webSocketSession = CompletableDeferred<WebSocketSession>()
    override val webSocketSession: Deferred<WebSocketSession> = _webSocketSession

    override suspend fun initializeTransport(): KrpcTransport {
        val session = webSocketSessionFactory {
            requestConfigBuilder = it
        }

        _webSocketSession.complete(session)
        return KtorTransport(session)
    }

    override fun initializeConfig(): KrpcConfig.Client {
        return pluginConfigBuilder?.apply(requestConfigBuilder)?.build()
            ?: rpcClientConfig(requestConfigBuilder)
    }
}
