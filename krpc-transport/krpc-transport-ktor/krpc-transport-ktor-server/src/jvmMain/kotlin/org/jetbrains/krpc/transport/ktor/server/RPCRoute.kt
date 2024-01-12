/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.transport.ktor.server

import io.ktor.server.websocket.DefaultWebSocketServerSession
import org.jetbrains.krpc.RPC
import org.jetbrains.krpc.RPCConfigBuilder
import org.jetbrains.krpc.RPCServer
import kotlin.reflect.KClass

/**
 * RPCRoute class represents an RPC server that is mounted in Ktor routing.
 * This class provides API to register services and optionally setup configuration.
 */
public class RPCRoute(
    webSocketSession: DefaultWebSocketServerSession
) : DefaultWebSocketServerSession by webSocketSession {
    internal var configBuilder: RPCConfigBuilder.Server.() -> Unit = {}

    /**
     * Optionally configures the declared RPC server.
     * Overrides [RPC] plugin configuration.
     *
     * @param configBuilder The function that configures RPC.
     */
    public fun rpcConfig(configBuilder: RPCConfigBuilder.Server.() -> Unit) {
        this.configBuilder = configBuilder
    }

    internal val registrations = mutableListOf<RPCServer.() -> Unit>()

    /**
     * Registers new service to the server. Server will route all designated messages to it.
     * Service of any type should be unique on the server, but RPCServer does not specify the actual retention policy.
     *
     * @param Service the exact type of the server to be registered.
     * For example for service with `MyService` interface and `MyServiceImpl` implementation,
     * type `MyService` should be specified explicitly.
     * @param service the actual implementation of the service that will handle the calls.
     * @param serviceKClass [KClass] of the [Service].
     */
    public fun <Service : RPC> registerService(service: Service, serviceKClass: KClass<Service>) {
        registrations.add {
            registerService(service, serviceKClass)
        }
    }

    /**
     * Registers new service to the server. Server will route all designated messages to it.
     * Service of any type should be unique on the server, but RPCServer does not specify the actual retention policy.
     *
     * @param Service the exact type of the server to be registered.
     * For example for service with `MyService` interface and `MyServiceImpl` implementation,
     * type `MyService` should be specified explicitly.
     * @param service the actual implementation of the service that will handle the calls.
     */
    public inline fun <reified Service : RPC> registerService(service: Service) {
        registerService(service, Service::class)
    }
}
