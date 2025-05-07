/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.client

import io.ktor.client.*
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.websocket.*
import io.ktor.util.*
import kotlinx.rpc.krpc.KrpcConfigBuilder

internal val KrpcClientPluginAttributesKey = AttributeKey<KrpcConfigBuilder.Client>("KrpcClientPluginAttributesKey")

@Deprecated("Use Krpc instead", ReplaceWith("Krpc"), level = DeprecationLevel.ERROR)
public val RPC: ClientPlugin<KrpcConfigBuilder.Client> get() = Krpc

/**
 * Ktor client plugin that allows to configure RPC globally for all instances obtained via [rpc] functions.
 */
public val Krpc: ClientPlugin<KrpcConfigBuilder.Client> = createClientPlugin("Krpc", { KrpcConfigBuilder.Client() }) {
    client.attributes.put(KrpcClientPluginAttributesKey, pluginConfig)
}

@Deprecated("Use installKrpc instead", ReplaceWith("installKrpc"), level = DeprecationLevel.ERROR)
public fun HttpClientConfig<*>.installRPC(
    configure: KrpcConfigBuilder.Client.() -> Unit = {}
): Unit = installKrpc(configure)

/**
 * Installs [WebSockets] and [Krpc] client plugins
 */
public fun HttpClientConfig<*>.installKrpc(
    configure: KrpcConfigBuilder.Client.() -> Unit = {}
) {
    install(WebSockets)
    install(Krpc, configure)
}
