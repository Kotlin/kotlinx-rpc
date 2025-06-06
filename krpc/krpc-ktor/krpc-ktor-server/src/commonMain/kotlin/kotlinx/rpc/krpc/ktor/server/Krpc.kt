/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.server

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import kotlinx.rpc.krpc.KrpcConfigBuilder

internal val KrpcServerPluginAttributesKey = AttributeKey<KrpcConfigBuilder.Server>("KrpcServerPluginAttributesKey")

/**
 * Ktor server plugin that allows to configure RPC globally for all mounted servers.
 */
public val Krpc: ApplicationPlugin<KrpcConfigBuilder.Server> = createApplicationPlugin(
    name = "Krpc",
    createConfiguration = { KrpcConfigBuilder.Server() },
) {
    application.install(WebSockets)
    application.attributes.put(KrpcServerPluginAttributesKey, pluginConfig)
}
