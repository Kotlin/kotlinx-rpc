/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.application.*
import io.ktor.util.*
import org.jetbrains.krpc.RPCConfig
import org.jetbrains.krpc.RPCConfigBuilder

internal val RPCServerPluginAttributesKey = AttributeKey<RPCConfigBuilder.Server>("RPCServerPluginAttributesKey")

/**
 * Plugin for setting global kRPC configuration. See [RPCConfig.Server]
 */
val RPC = createApplicationPlugin("RPC", { RPCConfigBuilder.Server() }) {
    application.attributes.put(RPCServerPluginAttributesKey, pluginConfig)
}
