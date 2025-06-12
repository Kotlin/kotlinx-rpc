/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.rpc.krpc.KrpcConfigBuilder

private val KrpcRequestConfigAttributeKey = AttributeKey<KrpcConfigBuilder.Client.() -> Unit>(
    name = "KrpcRequestConfigAttributeKey"
)

/**
 * Extension function for the [HttpRequestBuilder] that allows to configure RPC for the call.
 * Usually used with the [rpc] functions.
 * Overrides [Krpc] plugin configuration.
 *
 * @param configBuilder The function that configures RPC.
 */
public fun HttpRequestBuilder.rpcConfig(configBuilder: KrpcConfigBuilder.Client.() -> Unit = {}) {
    attributes.put(KrpcRequestConfigAttributeKey, configBuilder)
}

/**
 * Configures [KtorRpcClient] for the following path. Provides means for additional configuration via [block].
 * Note that the [WebSockets] plugin is required for these calls.
 *
 * @param urlString The URL to use for the request.
 * @param block Optional configuration for the [HttpRequestBuilder].
 * @return An instance of [KtorRpcClient] that is configured to send messages to the server.
 * The instance is cold and will establish WebSocket connection on the first request.
 *
 * @See [KtorRpcClient]
 */
public fun HttpClient.rpc(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {},
): KtorRpcClient {
    return rpc {
        url(urlString)
        block()
    }
}

/**
 * Configures [KtorRpcClient] for the following path. Provides means for additional configuration via [block].
 * Note that the [WebSockets] plugin is required for these calls.
 *
 * @param block Optional configuration for the [HttpRequestBuilder].
 * @return An instance of [KtorRpcClient] that is configured to send messages to the server.
 * The instance is cold and will establish WebSocket connection on the first request.
 *
 * @See [KtorRpcClient]
 */
public fun HttpClient.rpc(
    block: HttpRequestBuilder.() -> Unit = {},
): KtorRpcClient {
    pluginOrNull(WebSockets)
        ?: error("RPC for client requires $WebSockets plugin to be installed firstly")

    val pluginConfigBuilder = attributes.getOrNull(KrpcClientPluginAttributesKey)

    return KtorKrpcClientImpl(pluginConfigBuilder) { configSetter ->
        webSocketSession {
            block()

            attributes.getOrNull(KrpcRequestConfigAttributeKey)?.let { configSetter(it) }
        }
    }
}
