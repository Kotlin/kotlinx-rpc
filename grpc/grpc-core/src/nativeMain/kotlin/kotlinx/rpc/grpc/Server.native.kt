/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.GrpcServerCredentials
import kotlinx.rpc.grpc.internal.NativeServer

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
) : ServerBuilder<NativeServerBuilder>() {

    // TODO: Add actual credentials
    private val credentials = GrpcServerCredentials.createInsecure()
    private val services = mutableListOf<ServerServiceDefinition>()

    override fun addService(service: ServerServiceDefinition): NativeServerBuilder {
        services.add(service)
        return this
    }

    override fun fallbackHandlerRegistry(registry: HandlerRegistry?): NativeServerBuilder {
        TODO("Not yet implemented")
    }

    override fun build(): Server {
        val server = NativeServer(port, credentials)

        for (service in services) {
            server.addService(service)
        }

        return server
    }

}

internal actual fun ServerBuilder(port: Int): ServerBuilder<*> {
    return NativeServerBuilder(port)
}

internal actual fun Server(builder: ServerBuilder<*>): Server {
    return builder.build()
}
