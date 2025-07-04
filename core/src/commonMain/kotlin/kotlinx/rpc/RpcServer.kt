/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.annotations.Rpc
import kotlin.reflect.KClass

/**
 * RpcServer is used to accept RPC messages, route them to a specific service, and process given responses.
 * Server may contain multiple services.
 */
public interface RpcServer {
    /**
     * Registers new service. Server will route all designated messages to it.
     * Service of any type should be unique on the server, but RpcServer doesn't specify the actual retention policy.
     *
     * @param Service the exact type of the server to be registered.
     * For example, for a service with `MyService` interface and `MyServiceImpl` implementation
     * the type `MyService` should be specified explicitly.
     * @param serviceKClass [KClass] of the [Service].
     * @param serviceFactory function that produces the actual implementation of the service that will handle the calls.
     *
     * @see kotlinx.rpc.annotations.CheckedTypeAnnotation
     */
    public fun <@Rpc Service : Any> registerService(
        serviceKClass: KClass<Service>,
        serviceFactory: () -> Service,
    )

    /**
     * Deregisters a service. Server will stop routing messages to it.
     *
     * @param Service the exact type of the server to be deregistered, the same one that was used for registration.
     * For example, for a service with `MyService` interface and `MyServiceImpl` implementation
     * the type `MyService` should be specified explicitly.
     * @param serviceKClass [KClass] of the [Service].
     *
     * @see kotlinx.rpc.annotations.CheckedTypeAnnotation
     */
    public fun <@Rpc Service : Any> deregisterService(
        serviceKClass: KClass<Service>,
    )
}

/**
 * Registers new service to the server. Server will route all designated messages to it.
 * Service of any type should be unique on the server, but RpcServer doesn't specify the actual retention policy.
 *
 * @param Service the exact type of the server to be registered.
 * For example, for a service with `MyService` interface and `MyServiceImpl` implementation
 * the type `MyService` should be specified explicitly.
 * @param serviceFactory function that produces the actual implementation of the service that will handle the calls.
 *
 * @see kotlinx.rpc.annotations.CheckedTypeAnnotation
 */
public inline fun <@Rpc reified Service : Any> RpcServer.registerService(
    noinline serviceFactory: () -> Service,
) {
    registerService(Service::class, serviceFactory)
}
