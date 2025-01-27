/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

/**
 * Platform-specific gRPC server builder.
 */
public actual abstract class ServerBuilder<T : ServerBuilder<T>> {
    public actual abstract fun addService(service: ServerServiceDefinition): T

    public actual abstract fun fallbackHandlerRegistry(registry: HandlerRegistry?): T
}

internal actual fun ServerBuilder(port: Int): ServerBuilder<*> {
    error("JS target is not supported in gRPC")
}

internal actual fun Server(builder: ServerBuilder<*>): Server {
    error("JS target is not supported in gRPC")
}
