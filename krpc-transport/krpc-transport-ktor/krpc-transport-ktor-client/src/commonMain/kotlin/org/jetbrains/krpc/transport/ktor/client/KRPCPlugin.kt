package org.jetbrains.krpc.transport.ktor.client

import io.ktor.client.plugins.api.*
import io.ktor.util.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCConfigBuilder

internal val RPCClientPluginAttributesKey = AttributeKey<RPCConfigBuilder.Client>("RPCClientPluginAttributesKey")

/**
 * Plugin for setting global kRPC configuration. See [RPCConfig.Client]
 */
val KRPC = createClientPlugin("kRPC", { RPCConfigBuilder.Client() }) {
    client.attributes.put(RPCClientPluginAttributesKey, pluginConfig)
}
