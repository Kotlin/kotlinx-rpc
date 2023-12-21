package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.job
import org.jetbrains.krpc.rpcServerConfig

fun Route.rpc(path: String, builder: RPCRoute.() -> Unit = {}) {
    route(path) {
        rpc(builder)
    }
}

fun Route.rpc(builder: RPCRoute.() -> Unit = {}) {
    createRPCServer(RPCRoute().apply(builder))
}

private fun Route.createRPCServer(rpcRoute: RPCRoute): Unit = with(rpcRoute) {
    webSocket {
        val pluginConfigBuilder = application.attributes.getOrNull(RPCServerPluginAttributesKey)
        val rpcConfig = pluginConfigBuilder?.apply(configBuilder)?.build()
            ?: rpcServerConfig(configBuilder)

        val server = KtorRPCServer(this, rpcConfig)

        registrations.forEach { registration ->
            server.registration()
        }

        server.coroutineContext.job.join()
    }
}
