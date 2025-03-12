/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.ktor.server

import io.ktor.server.websocket.*
import kotlinx.rpc.RpcServer
import kotlinx.rpc.annotations.Rpc
import kotlinx.rpc.krpc.KrpcConfigBuilder
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

@Deprecated("Use KrpcRoute instead", ReplaceWith("KrpcRoute"), level = DeprecationLevel.ERROR)
public typealias RPCRoute = KrpcRoute

/**
 * RpcRoute class represents an RPC server that is mounted in Ktor routing.
 * This class provides API to register services and optionally setup configuration.
 */
public class KrpcRoute(
    webSocketSession: DefaultWebSocketServerSession
) : DefaultWebSocketServerSession by webSocketSession {
    internal var configBuilder: KrpcConfigBuilder.Server.() -> Unit = {}

    /**
     * Optionally configures the declared RPC server.
     * Overrides [Krpc] plugin configuration.
     *
     * @param configBuilder The function that configures RPC.
     */
    public fun rpcConfig(configBuilder: KrpcConfigBuilder.Server.() -> Unit) {
        this.configBuilder = configBuilder
    }

    internal val registrations = mutableListOf<(RpcServer) -> Unit>()

    /**
     * Registers new service to the server. Server will route all designated messages to it.
     * Service of any type should be unique on the server, but RpcServer does not specify the actual retention policy.
     *
     * @param Service the exact type of the server to be registered.
     * For example for service with `MyService` interface and `MyServiceImpl` implementation,
     * type `MyService` should be specified explicitly.
     * @param serviceKClass [KClass] of the [Service].
     * @param serviceFactory function that produces the actual implementation of the service that will handle the calls.
     */
    public fun <@Rpc Service : Any> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: (CoroutineContext) -> Service,
    ) {
        registrations.add { server ->
            server.registerService(serviceKClass, serviceFactory)
        }
    }

    /**
     * Registers new service to the server. Server will route all designated messages to it.
     * Service of any type should be unique on the server, but RpcServer does not specify the actual retention policy.
     *
     * @param Service the exact type of the server to be registered.
     * For example for service with `MyService` interface and `MyServiceImpl` implementation,
     * type `MyService` should be specified explicitly.
     * @param serviceFactory function that produces the actual implementation of the service that will handle the calls.
     */
    public inline fun <@Rpc reified Service : Any> registerService(
        noinline serviceFactory: (CoroutineContext) -> Service,
    ) {
        registerService(Service::class, serviceFactory)
    }
}
