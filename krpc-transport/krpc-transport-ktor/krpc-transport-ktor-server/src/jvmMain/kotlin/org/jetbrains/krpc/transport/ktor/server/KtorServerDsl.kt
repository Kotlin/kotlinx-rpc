package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.job
import org.jetbrains.krpc.rpcServerConfig

@KtorDsl
fun Route.rpc(path: String, builder: RPCRoute.() -> Unit) {
    route(path) {
        rpc(builder)
    }
}

@KtorDsl
fun Route.rpc(builder: RPCRoute.() -> Unit) {
    createRPCServer(builder)
}

private fun Route.createRPCServer(rpcRouteBuilder: RPCRoute.() -> Unit) {
    pluginOrNull(WebSockets) ?: error("RPC for server requires $WebSockets plugin to be installed firstly")

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
