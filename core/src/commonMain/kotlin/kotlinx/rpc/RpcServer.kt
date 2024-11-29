/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.rpc.annotations.Rpc
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

@Deprecated("Use RpcServer instead", ReplaceWith("RpcServer"), level = DeprecationLevel.ERROR)
public typealias RPCServer = RpcServer

/**
 * RpcServer is used to accept RPC messages, route them to a specific service, and process given responses.
 * Server may contain multiple services.
 * [CoroutineScope] defines server lifetime.
 */
public interface RpcServer : CoroutineScope {
    /**
     * Registers new service to the server. Server will route all designated messages to it.
     * Service of any type should be unique on the server, but RpcServer doesn't specify the actual retention policy.
     *
     * @param Service the exact type of the server to be registered.
     * For example, for service with `MyService` interface and `MyServiceImpl` implementation,
     * type `MyService` should be specified explicitly.
     * @param serviceKClass [KClass] of the [Service].
     * @param serviceFactory function that produces the actual implementation of the service that will handle the calls.
     */
    public fun <@Rpc Service : Any> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: (CoroutineContext) -> Service,
    )
}

/**
 * Registers new service to the server. Server will route all designated messages to it.
 * Service of any type should be unique on the server, but RpcServer doesn't specify the actual retention policy.
 *
 * @param Service the exact type of the server to be registered.
 * For example, for service with `MyService` interface and `MyServiceImpl` implementation,
 * type `MyService` should be specified explicitly.
 * @param serviceFactory function that produces the actual implementation of the service that will handle the calls.
 */
public inline fun <@Rpc reified Service : Any> RpcServer.registerService(
    noinline serviceFactory: (CoroutineContext) -> Service,
) {
    registerService(Service::class, serviceFactory)
}
