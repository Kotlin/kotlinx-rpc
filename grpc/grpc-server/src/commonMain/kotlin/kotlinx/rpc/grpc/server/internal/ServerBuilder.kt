/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.server.internal

import kotlinx.rpc.grpc.server.GrpcHandlerRegistry
import kotlinx.rpc.grpc.server.GrpcServerCredentials
import kotlinx.rpc.grpc.server.GrpcServerServiceDefinition
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.time.Duration

@InternalRpcApi
public interface PlatformServer {
    public val port: Int
    public val isShutdown: Boolean
    public val isTerminated: Boolean
    public fun start(): PlatformServer
    public fun shutdown(): PlatformServer
    public fun shutdownNow(): PlatformServer
    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE): PlatformServer
}

/**
 * Platform-specific gRPC server builder.
 */
@InternalRpcApi
public expect abstract class ServerBuilder<T : ServerBuilder<T>> {
    /**
     * Adds a service implementation to the handler registry.
     *
     * @return `this`
     */
    public abstract fun addService(service: GrpcServerServiceDefinition): T

    /**
     * Sets a fallback handler registry that will be looked up in if a method is not found in the
     * primary registry.
     * The primary registry (configured via [addService]) is faster but immutable.
     * The fallback registry is more flexible and allows implementations to mutate over
     * time and load services on-demand.
     *
     * @return `this`
     */
    public abstract fun fallbackHandlerRegistry(registry: GrpcHandlerRegistry?): T
}

@InternalRpcApi
public expect fun ServerBuilder(port: Int, credentials: GrpcServerCredentials? = null): ServerBuilder<*>

@InternalRpcApi
public expect fun PlatformServer(builder: ServerBuilder<*>): PlatformServer
