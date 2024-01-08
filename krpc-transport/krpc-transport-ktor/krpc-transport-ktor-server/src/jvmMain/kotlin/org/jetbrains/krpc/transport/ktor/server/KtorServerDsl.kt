/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.job
import org.jetbrains.krpc.rpcServerConfig
import org.jetbrains.krpc.RPCServer

/**
 * Adds an RPC route to the specified [Route]. Provides builder to configure [RPCServer] that will be used internally.
 * Note that the [WebSockets] plugin is required for such a route to work.
 *
 * @param path The relative path on which RPC server should mount.
 * @param builder Builder function to configure RPC server.
 */
@KtorDsl
public fun Route.rpc(path: String, builder: RPCRoute.() -> Unit) {
    route(path) {
        rpc(builder)
    }
}

/**
 * Adds an RPC route to the specified [Route]. Provides builder to configure [RPCServer] that will be used internally.
 * Note that the [WebSockets] plugin is required for such a route to work.
 *
 * @param builder Builder function to configure RPC server.
 */
@KtorDsl
public fun Route.rpc(builder: RPCRoute.() -> Unit) {
    createRPCServer(builder)
}

private fun Route.createRPCServer(rpcRouteBuilder: RPCRoute.() -> Unit) {
    application.pluginOrNull(WebSockets)
        ?: error("RPC for server requires $WebSockets plugin to be installed firstly")

    webSocket {
        val rpcRoute = RPCRoute(this).apply(rpcRouteBuilder)
        val pluginConfigBuilder = application.attributes.getOrNull(RPCServerPluginAttributesKey)
        val rpcConfig = pluginConfigBuilder?.apply(rpcRoute.configBuilder)?.build()
            ?: rpcServerConfig(rpcRoute.configBuilder)

        val server = KtorRPCServer(this, rpcConfig)

        rpcRoute.registrations.forEach { registration ->
            server.registration()
        }

        server.coroutineContext.job.join()
    }
}
