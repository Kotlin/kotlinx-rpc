/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.internal

import kotlinx.rpc.grpc.server.GrpcHandlerRegistry
import kotlinx.rpc.grpc.server.GrpcServerConfiguration
import kotlinx.rpc.grpc.server.GrpcServerCredentials
import kotlinx.rpc.grpc.server.GrpcServerServiceDefinition
import kotlinx.rpc.grpc.server.createInsecureServerCredentials
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Platform-specific gRPC server builder.
 */
@InternalRpcApi
public actual abstract class ServerBuilder<T : ServerBuilder<T>> {
    public actual abstract fun addService(service: GrpcServerServiceDefinition): T

    public actual abstract fun fallbackHandlerRegistry(registry: GrpcHandlerRegistry?): T

    public abstract fun build(): PlatformServer
}

private class NativeServerBuilder(
    val port: Int,
    val credentials: GrpcServerCredentials,
) : ServerBuilder<NativeServerBuilder>() {
    private val services = mutableListOf<GrpcServerServiceDefinition>()
    private var fallbackRegistry: GrpcHandlerRegistry = DefaultFallbackRegistry
    var config: GrpcServerConfiguration? = null

    override fun addService(service: GrpcServerServiceDefinition): NativeServerBuilder {
        services.add(service)
        return this
    }

    override fun fallbackHandlerRegistry(registry: GrpcHandlerRegistry?): NativeServerBuilder {
        fallbackRegistry = registry ?: DefaultFallbackRegistry
        return this
    }

    override fun build(): PlatformServer {
        return NativeServer(
            port,
            credentials,
            services,
            fallbackRegistry,
            maxInboundMessageSize = config?.maxInboundMessageSize,
            maxInboundMetadataSize = config?.maxInboundMetadataSize,
            keepAlive = config?.keepAlive,
            maxConnectionIdle = config?.maxConnectionIdle,
            maxConnectionAge = config?.maxConnectionAge,
            maxConnectionAgeGrace = config?.maxConnectionAgeGrace,
        )
    }
}

@InternalRpcApi
public actual fun ServerBuilder(port: Int, credentials: GrpcServerCredentials?): ServerBuilder<*> {
    return NativeServerBuilder(port, credentials ?: createInsecureServerCredentials())
}

@InternalRpcApi
public actual fun PlatformServer(builder: ServerBuilder<*>): PlatformServer {
    return builder.build()
}

internal actual fun ServerBuilder<*>.applyConfig(config: GrpcServerConfiguration): ServerBuilder<*> {
    check(this is NativeServerBuilder) { "Expected NativeServerBuilder" }
    this.config = config
    return this
}

private object DefaultFallbackRegistry : GrpcHandlerRegistry() {
    override fun getServices(): List<GrpcServerServiceDefinition> {
        return listOf()
    }

    override fun lookupMethod(
        methodName: String,
        authority: String?,
    ): ServerMethodDefinition<*, *>? {
        return null
    }
}
