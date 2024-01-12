/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.client

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.util.*
import org.jetbrains.krpc.RPCClient
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.rpcClientConfig

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
fun HttpRequestBuilder.rpcConfig(configBuilder: RPCConfigBuilder.Client.() -> Unit = {}) {
    attributes.put(RPCRequestConfigAttributeKey, configBuilder)
}

/**
 * Configures [RPCClient] for the following path. Provides means for additional configuration via [block].
 * Note that the [WebSockets] plugin is required for these calls.
 *
 * @param urlString The URL to use for the request.
 * @param block Optional configuration for the
 * @return An instance of [RPCClient] that is configured to send messages to the server.
 */
suspend fun HttpClient.rpc(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {},
): RPCClient {
    return rpc {
        url(urlString)
        block()
    }
}

/**
 * Configures [RPCClient] for the following path. Provides means for additional configuration via [block].
 * Note that the [WebSockets] plugin is required for these calls.
 *
 * @param block Optional configuration for the
 * @return An instance of [RPCClient] that is configured to send messages to the server.
 */
suspend fun HttpClient.rpc(
    block: HttpRequestBuilder.() -> Unit = {},
): RPCClient {
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

    return KtorRPCClient(session, rpcConfig)
}
