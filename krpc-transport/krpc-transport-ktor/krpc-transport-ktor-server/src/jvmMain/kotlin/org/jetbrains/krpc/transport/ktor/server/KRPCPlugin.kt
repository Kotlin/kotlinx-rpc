package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.application.*
import io.ktor.util.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCConfigBuilder

internal val RPCServerPluginAttributesKey = AttributeKey<RPCConfigBuilder.Server>("RPCServerPluginAttributesKey")

/**
 * Plugin for setting global kRPC configuration. See [RPCConfig.Server]
 */
val KRPC = createApplicationPlugin("kRPC", { RPCConfigBuilder.Server() }) {
    application.attributes.put(RPCServerPluginAttributesKey, pluginConfig)
}
