package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.websocket.DefaultWebSocketServerSession
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.server.RPCServer
import kotlin.reflect.KClass

class RPCRoute(webSocketSession: DefaultWebSocketServerSession) : DefaultWebSocketServerSession by webSocketSession {
    internal var configBuilder: RPCConfigBuilder.Server.() -> Unit = {}

    fun rpcConfig(configBuilder: RPCConfigBuilder.Server.() -> Unit) {
        this.configBuilder = configBuilder
    }

    internal val registrations = mutableListOf<RPCServer.() -> Unit>()

    fun <Service : RPC> registerService(service: Service, serviceKClass: KClass<Service>) {
        registrations.add {
            registerService(service, serviceKClass)
        }
    }

    inline fun <reified Service : RPC> registerService(service: Service) {
        registerService(service, Service::class)
    }
}
