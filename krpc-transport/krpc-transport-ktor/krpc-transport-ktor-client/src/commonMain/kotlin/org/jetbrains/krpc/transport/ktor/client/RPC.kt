/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.client

import io.ktor.client.plugins.api.*
import io.ktor.util.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCConfigBuilder

internal val RPCClientPluginAttributesKey = AttributeKey<RPCConfigBuilder.Client>("RPCClientPluginAttributesKey")

/**
 * Plugin for setting global kRPC configuration. See [RPCConfig.Client]
 */
val RPC: ClientPlugin<RPCConfigBuilder.Client> = createClientPlugin("RPC", { RPCConfigBuilder.Client() }) {
    client.attributes.put(RPCClientPluginAttributesKey, pluginConfig)
}
