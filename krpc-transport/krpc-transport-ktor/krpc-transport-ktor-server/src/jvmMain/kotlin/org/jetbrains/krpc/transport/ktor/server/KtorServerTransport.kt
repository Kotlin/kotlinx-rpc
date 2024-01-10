/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.job
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.rpcServerConfig
import org.jetbrains.krpc.server.serverOf
import org.jetbrains.krpc.transport.ktor.KtorTransport
import kotlin.reflect.KClass

inline fun <reified T : RPC> Route.rpc(
    path: String,
    service: T,
    noinline rpcConfig: RPCConfigBuilder.Server.() -> Unit = {},
) {
    route(path) {
        rpc(service, T::class, rpcConfig)
    }
}

fun <T: RPC> Route.rpc(
    service: T,
    serviceKClass: KClass<T>,
    rpcConfigBuilder: RPCConfigBuilder.Server.() -> Unit = {},
) {
    webSocket {
        val pluginConfigBuilder = application.attributes.getOrNull(RPCServerPluginAttributesKey)
        val rpcConfig = pluginConfigBuilder?.apply(rpcConfigBuilder)?.build()
            ?: rpcServerConfig(rpcConfigBuilder)

        val transport = KtorTransport(rpcConfig.serialFormatInitializer.build(), this)
        val server = RPC.serverOf(service, serviceKClass, transport, rpcConfig)
        server.coroutineContext.job.join()
    }
}
