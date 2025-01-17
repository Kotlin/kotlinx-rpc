/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlin.time.Duration

public expect abstract class ServerBuilder<T : ServerBuilder<T>> {
    public abstract fun addService(service: ServerServiceDefinition): T

    public abstract fun fallbackHandlerRegistry(registry: HandlerRegistry?): T
}

internal expect fun ServerBuilder(port: Int): ServerBuilder<*>

public interface Server {
    public val port: Int
    public val isShutdown: Boolean
    public val isTerminated: Boolean

    public fun start() : Server
    public fun shutdown() : Server
    public fun shutdownNow() : Server
    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE) : Server
}

internal expect fun Server(builder: ServerBuilder<*>): Server
