/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.grpc.client.internal.applyConfig
import kotlinx.rpc.grpc.client.internal.toKotlin

/**
 * Creates a gRPC client on the channel that [builder] builds.
 *
 * Use this function when a hostname and a port can't describe the connection, or when the channel
 * needs a grpc-java setting that [GrpcClientConfiguration] doesn't have.
 * Examples are an in-process channel, a Unix domain socket and a custom transport.
 *
 * ```kotlin
 * val client = GrpcClient(InProcessChannelBuilder.forName("my-server")) {
 *     userAgent = "MyApp/1.2.3"
 * }
 * ```
 *
 * The function applies [configure] to [builder] and then builds the channel:
 *
 * - A setting from [configure] replaces the value that [builder] has.
 * - When [configure] doesn't set [GrpcClientConfiguration.maxInboundMessageSize],
 *   [GrpcClientConfiguration.maxInboundMetadataSize] or [GrpcClientConfiguration.idleTimeout],
 *   the setting has the value from [builder].
 * - [GrpcClientConfiguration.credentials] must stay `null`, or the function throws
 *   [IllegalArgumentException]. Set the transport credentials on [builder].
 *   To send call credentials with every call, add them to [GrpcCallOptions.callCredentials]
 *   in a [GrpcClientInterceptor].
 * - If [builder] doesn't support a setting from [configure], [builder] throws
 *   [UnsupportedOperationException].
 *
 * [GrpcClient.shutdown] shuts down the channel.
 * An executor or an event loop group that the caller gave to [builder] stays open.
 */
public fun GrpcClient(
    builder: io.grpc.ManagedChannelBuilder<*>,
    configure: GrpcClientConfiguration.() -> Unit = {},
): GrpcClient {
    val config = GrpcClientConfiguration().apply(configure)
    require(config.credentials == null) {
        "A client from a grpc-java `ManagedChannelBuilder` doesn't accept `credentials`. " +
            "Set the credentials on the builder."
    }

    return GrpcClient(
        channel = builder.applyConfig(config, explicitOnly = true).build().toKotlin(),
        messageMarshallerResolver = config.messageMarshallerResolver,
        interceptors = config.interceptors,
        callCredentials = GrpcEmptyCallCredentials,
        marshallerConfig = config.marshallerConfig,
    )
}
