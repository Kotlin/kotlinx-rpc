/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc


import kotlinx.rpc.grpc.internal.NativeServer
import kotlinx.rpc.grpc.internal.ServerMethodDefinition

/**
 * Platform-specific gRPC server builder.
 */
public actual abstract class ServerBuilder<T : ServerBuilder<T>> {
    public actual abstract fun addService(service: ServerServiceDefinition): T

    public actual abstract fun fallbackHandlerRegistry(registry: HandlerRegistry?): T

    public abstract fun build(): Server
}

private class NativeServerBuilder(
    val port: Int,
    val credentials: ServerCredentials,
) : ServerBuilder<NativeServerBuilder>() {
    private val services = mutableListOf<ServerServiceDefinition>()
    private var fallbackRegistry: HandlerRegistry = DefaultFallbackRegistry

    override fun addService(service: ServerServiceDefinition): NativeServerBuilder {
        services.add(service)
        return this
    }

    override fun fallbackHandlerRegistry(registry: HandlerRegistry?): NativeServerBuilder {
        fallbackRegistry = registry ?: DefaultFallbackRegistry
        return this
    }

    override fun build(): Server {
        return NativeServer(port, credentials, services, fallbackRegistry)
    }

}

internal actual fun ServerBuilder(port: Int, credentials: ServerCredentials?): ServerBuilder<*> {
    return NativeServerBuilder(port, credentials ?: createInsecureServerCredentials())
}

internal actual fun Server(builder: ServerBuilder<*>): Server {
    return builder.build()
}

private object DefaultFallbackRegistry : HandlerRegistry() {
    override fun getServices(): List<ServerServiceDefinition> {
        return listOf()
    }

    override fun lookupMethod(
        methodName: String,
        authority: String?,
    ): ServerMethodDefinition<*, *>? {
        return null
    }

}