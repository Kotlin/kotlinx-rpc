/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.RPCConfigBuilder
import kotlinx.rpc.krpc.rpcClientConfig

private val RPCRequestConfigAttributeKey = AttributeKey<RPCConfigBuilder.Client.() -> Unit>(
    name = "RPCRequestConfigAttributeKey"
)

/**
 * Extension function for the [HttpRequestBuilder] that allows to configure RPC for the call.
 * Usually used with the [rpc] functions.
 * Overrides [RPC] plugin configuration.
 *
 * @param configBuilder The function that configures RPC.
 */
public fun HttpRequestBuilder.rpcConfig(configBuilder: RPCConfigBuilder.Client.() -> Unit = {}) {
    attributes.put(RPCRequestConfigAttributeKey, configBuilder)
}

/**
 * Configures [RpcClient] for the following path. Provides means for additional configuration via [block].
 * Note that the [WebSockets] plugin is required for these calls.
 *
 * @param urlString The URL to use for the request.
 * @param block Optional configuration for the
 * @return An instance of [RpcClient] that is configured to send messages to the server.
 */
public suspend fun HttpClient.rpc(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {},
): KtorRPCClient {
    return rpc {
        url(urlString)
        block()
    }
}

/**
 * Configures [RpcClient] for the following path. Provides means for additional configuration via [block].
 * Note that the [WebSockets] plugin is required for these calls.
 *
 * @param block Optional configuration for the
 * @return An instance of [RpcClient] that is configured to send messages to the server.
 */
public suspend fun HttpClient.rpc(
    block: HttpRequestBuilder.() -> Unit = {},
): KtorRPCClient {
    pluginOrNull(WebSockets)
        ?: error("RPC for client requires $WebSockets plugin to be installed firstly")

    var requestConfigBuilder: RPCConfigBuilder.Client.() -> Unit = {}
    val session = webSocketSession {
        block()

        attributes.getOrNull(RPCRequestConfigAttributeKey)?.let {
            requestConfigBuilder = it
        }
    }

    val pluginConfigBuilder = attributes.getOrNull(RPCClientPluginAttributesKey)
    val rpcConfig = pluginConfigBuilder?.apply(requestConfigBuilder)?.build()
        ?: rpcClientConfig(requestConfigBuilder)

    return KtorRPCClientImpl(session, rpcConfig)
}
