/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.transport.ktor.client

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.websocket.*
import io.ktor.util.*
import kotlinx.rpc.RPCConfigBuilder

internal val RPCClientPluginAttributesKey = AttributeKey<RPCConfigBuilder.Client>("RPCClientPluginAttributesKey")

/**
 * Ktor client plugin that allows to configure RPC globally for all instances obtained via [rpc] functions.
 */
public val RPC: ClientPlugin<RPCConfigBuilder.Client> = createClientPlugin("RPC", { RPCConfigBuilder.Client() }) {
    client.attributes.put(RPCClientPluginAttributesKey, pluginConfig)
}

/**
 * Installs [WebSockets] and [RPC] client plugins
 */
public fun HttpClientConfig<*>.installRPC(
    configure: RPCConfigBuilder.Client.() -> Unit = {}
) {
    install(WebSockets)
    install(RPC, configure)
}
