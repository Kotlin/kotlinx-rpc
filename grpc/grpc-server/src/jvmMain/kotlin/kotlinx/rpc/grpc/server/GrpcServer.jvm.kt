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
 * Use this function for an in-process server, a Unix domain socket, a custom transport,
 * or a grpc-java setting that [GrpcServerConfiguration] doesn't have.
 *
 * ```kotlin
 * val server = GrpcServer(InProcessServerBuilder.forName("my-server")) {
 *     services {
 *         registerService<MyService> { MyServiceImpl() }
 *     }
 * }
 * ```
 *
 * A setting from [configure] replaces the value of [builder]. A setting that [configure] doesn't set
 * has the value of [builder], not the default of [GrpcServerBaseConfiguration]. Exceptions:
 *
 * - [GrpcServerBuilderConfiguration.keepAlive]:
 *   replaces the [GrpcServerBuilderConfiguration.KeepAlive.time]
 *   and the [GrpcServerBuilderConfiguration.KeepAlive.timeout] of [builder].
 *   A value that the block doesn't set has the default of [GrpcServerBaseConfiguration.KeepAlive].
 * - [GrpcServerBuilderConfiguration.services]: the server also has the services of [builder].
 *   A service from [GrpcServerBuilderConfiguration.services] replaces a service of [builder] that has the same name.
 * - [GrpcServerBuilderConfiguration.intercept]: each call runs the interceptors of [builder], then these interceptors.
 *   The interceptors of [builder] run nearer to the network.
 *   A call to a service of [builder] doesn't run these interceptors.
 * - Credentials: [configure] has none. Set them on [builder].
 *
 * To start the server, call [GrpcServer.start].
 * The server doesn't shut down an executor or an event loop group that the caller gave to [builder].
 *
 * @param parentContext The parent coroutine context of the service calls.
 * @throws GrpcUnsupportedSettingException when [builder] rejects a setting from [configure]
 * with [UnsupportedOperationException]. [builder] then has a part of the settings, so don't use it again.
 */
public fun GrpcServer(
    builder: io.grpc.ServerBuilder<*>,
    parentContext: CoroutineContext = EmptyCoroutineContext,
    configure: GrpcServerBuilderConfiguration.() -> Unit = {},
): GrpcServer {
    val config = GrpcServerBuilderConfiguration().apply(configure)

    return buildGrpcServer(builder.applyConfig(config, explicitOnly = true), config, parentContext)
}

/**
 * The configuration of a [GrpcServer] on a grpc-java `ServerBuilder`.
 *
 * A setting that the configuration block doesn't set has the value of the builder, not the documented default.
 * Set the credentials on the builder. This configuration has none.
 */
public class GrpcServerBuilderConfiguration internal constructor() : GrpcServerBaseConfiguration()

/**
 * The addresses that the server listens on.
 *
 * Use this property for a server without a TCP port, where [GrpcServer.port] returns `-1`.
 * The type of an address depends on the transport. A TCP address is a [java.net.InetSocketAddress].
 *
 * The list is empty:
 *
 * - Before [GrpcServer.start].
 * - After the server terminates.
 * - For a grpc-java server that doesn't report its addresses.
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
        } catch (_: UnsupportedOperationException) {
            // `io.grpc.Server.getListenSockets` throws this exception unless the server class overrides it.
            emptyList()
        }
    }
