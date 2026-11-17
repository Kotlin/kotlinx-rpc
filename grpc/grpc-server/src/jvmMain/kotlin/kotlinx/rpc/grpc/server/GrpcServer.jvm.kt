/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.grpc.server.internal.GrpcServerImpl
import kotlinx.rpc.grpc.server.internal.JvmPlatformServer
import kotlinx.rpc.grpc.server.internal.applyConfig
import java.net.SocketAddress
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Creates a gRPC server from a grpc-java server builder.
 *
 * Use this function when a port can't describe the listening address, or when the server needs
 * a grpc-java setting that [GrpcServerConfiguration] doesn't have.
 * Examples are an in-process server, a Unix domain socket and a custom transport.
 *
 * ```kotlin
 * val server = GrpcServer(InProcessServerBuilder.forName("my-server")) {
 *     services {
 *         registerService<MyService> { MyServiceImpl() }
 *     }
 * }
 * ```
 *
 * The function applies [configure] to [builder] and then builds the server:
 *
 * - A setting from [configure] replaces the value that [builder] has.
 * - A setting that [configure] doesn't set has the value from [builder],
 *   not the default of [GrpcServerBaseConfiguration].
 * - [configure] has no credentials. Set them on [builder].
 * - If [builder] doesn't support a setting from [configure], the function throws
 *   [GrpcUnsupportedSettingException].
 *
 * To start the server, call [GrpcServer.start].
 * [GrpcServer.shutdown] shuts down the server.
 * An executor or an event loop group that the caller gave to [builder] stays open.
 *
 * @param parentContext The parent coroutine context of the service calls.
 */
public fun GrpcServer(
    builder: io.grpc.ServerBuilder<*>,
    parentContext: CoroutineContext = EmptyCoroutineContext,
    configure: GrpcServerBaseConfiguration.() -> Unit = {},
): GrpcServer {
    val config = GrpcServerBaseConfiguration().apply(configure)

    return buildGrpcServer(builder.applyConfig(config, explicitOnly = true), config, parentContext)
}

/**
 * The addresses that the server listens on.
 *
 * Use this property for a server without a TCP port, where [GrpcServer.port] returns `-1`.
 * The type of an address depends on the transport of the server.
 * For a TCP port, the address is a [java.net.InetSocketAddress].
 *
 * The list is empty before [GrpcServer.start] and after the server terminates.
 */
public val GrpcServer.listenAddresses: List<SocketAddress>
    get() {
        val platformServer = (this as? GrpcServerImpl)?.platformServer as? JvmPlatformServer
            ?: internalError("Unknown GrpcServer implementation: ${this::class}")

        return try {
            platformServer.server.listenSockets
        } catch (_: IllegalStateException) {
            // grpc-java has no other way to check that the server is started and not terminated.
            emptyList()
        }
    }
