/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.utils.io.*
import kotlinx.coroutines.job
import kotlinx.rpc.krpc.rpcServerConfig

/**
 * Adds an RPC route to the specified [Route].
 * Provides builder to configure [kotlinx.rpc.RpcServer] that will be used internally.
 * Note that the [WebSockets] plugin is required for such a route to work.
 *
 * @param path The relative path on which RPC server should mount.
 * @param builder Builder function to configure RPC server.
 */
@KtorDsl
public fun Route.rpc(path: String, builder: KrpcRoute.() -> Unit) {
    route(path) {
        rpc(builder)
    }
}

/**
 * Adds an RPC route to the specified [Route].
 * Provides builder to configure [kotlinx.rpc.RpcServer] that will be used internally.
 * Note that the [WebSockets] plugin is required for such a route to work.
 *
 * @param builder Builder function to configure RPC server.
 */
@KtorDsl
public fun Route.rpc(builder: KrpcRoute.() -> Unit) {
    createRpcServer(builder)
}

private fun Route.createRpcServer(rpcRouteBuilder: KrpcRoute.() -> Unit) {
    application.pluginOrNull(WebSockets)
        ?: error("RPC for server requires $WebSockets plugin to be installed firstly")

    webSocket {
        val rpcRoute = KrpcRoute(this).apply(rpcRouteBuilder)
        val pluginConfigBuilder = application.attributes.getOrNull(KrpcServerPluginAttributesKey)
        val rpcConfig = pluginConfigBuilder?.apply(rpcRoute.configBuilder)?.build()
            ?: rpcServerConfig(rpcRoute.configBuilder)

        val server = KtorKrpcServer(this, rpcConfig)

        rpcRoute.registrations.forEach { registration ->
            registration(server)
        }

        server.coroutineContext.job.join()
    }
}
