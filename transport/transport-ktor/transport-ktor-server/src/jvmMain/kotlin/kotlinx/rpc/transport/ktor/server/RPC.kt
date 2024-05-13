/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.transport.ktor.server

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import kotlinx.rpc.RPCConfigBuilder

internal val RPCServerPluginAttributesKey = AttributeKey<RPCConfigBuilder.Server>("RPCServerPluginAttributesKey")

/**
 * Ktor server plugin that allows to configure RPC globally for all mounted servers.
 */
public val RPC: ApplicationPlugin<RPCConfigBuilder.Server> = createApplicationPlugin(
    name = "RPC",
    createConfiguration = { RPCConfigBuilder.Server() },
) {
    application.install(WebSockets)
    application.attributes.put(RPCServerPluginAttributesKey, pluginConfig)
}
