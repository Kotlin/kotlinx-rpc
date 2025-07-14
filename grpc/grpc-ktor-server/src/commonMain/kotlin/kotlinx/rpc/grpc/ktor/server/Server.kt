/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.ktor.server

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.log
import io.ktor.server.config.getAs
import io.ktor.util.AttributeKey
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.grpc.ServerBuilder

@Suppress("ConstPropertyName")
public object GrpcConfigKeys {
    public const val grpcHostPortPath: String = "ktor.deployment.grpcPort"
}

/**
 * Key used to store and retrieve the [GrpcServer] instance within the application's attributes.
 */
public val GrpcServerKey: AttributeKey<GrpcServer> = AttributeKey<GrpcServer>("GrpcServerPluginAttributesKey")

/**
 * Configures and starts a gRPC server within the Ktor application.
 * This function integrates with the Ktor lifecycle and manages the lifecycle of the gRPC server
 * by subscribing to [ApplicationStopping] and [ApplicationStopped] events.
 * It ensures that a gRPC server is properly initialized, started, and shutdown when the application stops.
 *
 * @param port The port on which the gRPC server will listen for incoming connections.
 * Defaults to the value specified in the `ktor.deployment.grpcPort` configuration, or 8001 if not configured.
 * @param configure Allows additional configuration of the gRPC server using a platform-specific [ServerBuilder].
 * @param builder A block used to define and register gRPC services for the gRPC server.
 * @return The instance of the initialized and running [GrpcServer].
 * @throws IllegalStateException if a gRPC server is already installed or the specified port conflicts with
 * an existing HTTP/HTTPS server port.
 */
public fun Application.grpc(
    port: Int = environment.config.propertyOrNull(GrpcConfigKeys.grpcHostPortPath)?.getAs<Int>() ?: 8001,
    configure: ServerBuilder<*>.() -> Unit = {},
    builder: RpcServer.() -> Unit,
): GrpcServer {
    if (attributes.contains(GrpcServerKey)) {
        error("gRPC Server is already installed, second call to grpc() is not allowed")
    }

    var newServer = false
    val server = attributes.computeIfAbsent(GrpcServerKey) {
        newServer = true
        GrpcServer(port, configure, builder)
    }

    if (!newServer) {
        error("A race detected while installing gRPC Server, second call to grpc() is not allowed")
    }

    server.start()
    log.debug("Started gRPC server on port $port")

    val stoppingHandle = monitor.subscribe(ApplicationStopping) {
        log.debug("Stopping gRPC server")
        attributes.getOrNull(GrpcServerKey)?.shutdown()
    }

    monitor.subscribe(ApplicationStopped) {
        log.debug("gRPC server complete shutdown")
        attributes.getOrNull(GrpcServerKey)?.shutdownNow()

        stoppingHandle.dispose()
    }

    return server
}
