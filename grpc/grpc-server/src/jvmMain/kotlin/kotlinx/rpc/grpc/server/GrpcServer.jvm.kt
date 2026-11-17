/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
import kotlinx.rpc.grpc.server.internal.applyConfig
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
