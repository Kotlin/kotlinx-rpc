/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.client

import kotlinx.rpc.grpc.GrpcUnsupportedSettingException
import kotlinx.rpc.grpc.client.internal.applyConfig
import kotlinx.rpc.grpc.client.internal.toKotlin
import org.jetbrains.kotlinx.library.api.watchdog.ExemptionReason
import org.jetbrains.kotlinx.library.api.watchdog.IntentionallyWithoutEqualsHashCodeOrToString

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
 * - A setting that [configure] doesn't set has the value from [builder],
 *   not the default of [GrpcClientBaseConfiguration].
 * - [configure] has no transport credentials. Set them on [builder].
 * - If [builder] rejects a setting from [configure] with [UnsupportedOperationException],
 *   the function throws [GrpcUnsupportedSettingException].
 *   [builder] can then have a part of the settings, so make a new builder for the next call.
 * - A builder can ignore a setting that it doesn't support. The function doesn't report such a setting.
 *
 * [GrpcClient.shutdown] shuts down the channel.
 * An executor or an event loop group that the caller gave to [builder] stays open.
 */
public fun GrpcClient(
    builder: io.grpc.ManagedChannelBuilder<*>,
    configure: GrpcClientChannelBuilderConfiguration.() -> Unit = {},
): GrpcClient {
    val config = GrpcClientChannelBuilderConfiguration().apply(configure)

    return GrpcClient(
        channel = builder.applyConfig(config, explicitOnly = true).build().toKotlin(),
        messageMarshallerResolver = config.messageMarshallerResolver,
        interceptors = config.interceptors,
        callCredentials = config.callCredentials,
        marshallerConfig = config.marshallerConfig,
    )
}

/**
 * The settings of a `GrpcClient` that gets its channel from a grpc-java `ManagedChannelBuilder`.
 *
 * The builder already has the transport credentials, so this configuration has only [callCredentials].
 *
 * A setting that the block doesn't set has the value of the builder,
 * not the default that the property docs of [GrpcClientBaseConfiguration] give.
 */
@IntentionallyWithoutEqualsHashCodeOrToString(reason = ExemptionReason.TODO, description = "review before 1.0")
public class GrpcClientChannelBuilderConfiguration internal constructor() : GrpcClientBaseConfiguration() {
    /**
     * The call credentials that the client sends with every call.
     *
     * The default is [GrpcEmptyCallCredentials], which doesn't send anything.
     * To send more than one, combine them with `+`.
     * To use the call credentials of a value that also has transport credentials,
     * read its [GrpcClientCredentials.callCredentials].
     *
     * Call credentials with [GrpcCallCredentials.requiresTransportSecurity] `true`, which is the default,
     * need a channel that reports the security level `PRIVACY_AND_INTEGRITY`.
     * On another channel, each call fails with `UNAUTHENTICATED`.
     * A plaintext channel doesn't report this level, and a custom transport reports it only
     * when its code sets it.
     *
     * ```kotlin
     * val credentials = GrpcTlsClientCredentials { ... } + BearerTokenCredentials(token)
     *
     * GrpcClient(channelBuilder) {
     *     callCredentials = credentials.callCredentials
     * }
     * ```
     */
    public var callCredentials: GrpcCallCredentials = GrpcEmptyCallCredentials
}
