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
 * Use this function for an in-process channel, a Unix domain socket, a custom transport,
 * or a grpc-java setting that [GrpcClientConfiguration] doesn't have.
 *
 * ```kotlin
 * val client = GrpcClient(InProcessChannelBuilder.forName("my-server")) {
 *     userAgent = "MyApp/1.2.3"
 * }
 * ```
 *
 * A setting from [configure] replaces the value of [builder]. A setting that [configure] doesn't set
 * has the value of [builder], not the default of [GrpcClientBaseConfiguration]. Exceptions:
 *
 * - [GrpcClientChannelBuilderConfiguration.keepAlive]:
 *   replaces the [GrpcClientChannelBuilderConfiguration.KeepAlive.time],
 *   the [GrpcClientChannelBuilderConfiguration.KeepAlive.timeout],
 *   and [GrpcClientChannelBuilderConfiguration.KeepAlive.withoutCalls] of [builder].
 *   A value that the block doesn't set has the default of [GrpcClientBaseConfiguration.KeepAlive].
 * - [GrpcClientChannelBuilderConfiguration.callCredentials]: each call uses them and the call credentials of [builder].
 * - [GrpcClientChannelBuilderConfiguration.intercept]: each call runs these interceptors,
 *   then the interceptors of [builder]. The interceptors of [builder] run nearer to the network.
 * - Transport credentials: [configure] has none. Set them on [builder].
 *
 * By default, call credentials require a secure channel.
 * On a channel that isn't secure, each call with call credentials fails with `UNAUTHENTICATED`.
 *
 * | Channel of [builder] | Secure                                                                     |
 * |----------------------|----------------------------------------------------------------------------|
 * | TLS                  | Yes                                                                        |
 * | In-process           | Yes                                                                        |
 * | Plaintext            | No                                                                         |
 * | Custom transport     | Only when the transport reports the security level `PRIVACY_AND_INTEGRITY` |
 *
 * Call credentials with [GrpcCallCredentials.requiresTransportSecurity] set to `false` work on every channel.
 *
 * [GrpcClient.shutdown] shuts down the channel.
 * It doesn't shut down an executor or an event loop group that the caller gave to [builder].
 *
 * @throws GrpcUnsupportedSettingException when [builder] rejects a setting from [configure]
 * with [UnsupportedOperationException]. [builder] then has a part of the settings, so don't use it again.
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
 * The configuration of a [GrpcClient] on a grpc-java `ManagedChannelBuilder`.
 *
 * A setting that the configuration block doesn't set has the value of the builder, not the documented default.
 * Set the transport credentials on the builder. This configuration has only [callCredentials].
 */
@IntentionallyWithoutEqualsHashCodeOrToString(reason = ExemptionReason.TODO, description = "review before 1.0")
public class GrpcClientChannelBuilderConfiguration internal constructor() : GrpcClientBaseConfiguration() {
    /**
     * The call credentials that the client sends with every call.
     * The default is [GrpcEmptyCallCredentials]. To send more than one, combine them with `+`.
     *
     * To take them from a value that also has transport credentials,
     * read its [GrpcClientCredentials.callCredentials]:
     *
     * ```kotlin
     * val credentials = GrpcTlsClientTransportCredentials { ... } + BearerTokenCredentials(token)
     *
     * GrpcClient(channelBuilder) {
     *     callCredentials = credentials.callCredentials
     * }
     * ```
     *
     * The builder can have call credentials, for example from `io.grpc.CompositeChannelCredentials`.
     * This property doesn't replace them. Each call uses both.
     *
     * By default, call credentials require a secure channel.
     * On a channel that isn't secure, each call with call credentials fails with `UNAUTHENTICATED`.
     *
     * | Channel of the builder | Secure                                                                     |
     * |------------------------|----------------------------------------------------------------------------|
     * | TLS                    | Yes                                                                        |
     * | In-process             | Yes                                                                        |
     * | Plaintext              | No                                                                         |
     * | Custom transport       | Only when the transport reports the security level `PRIVACY_AND_INTEGRITY` |
     *
     * Call credentials with [GrpcCallCredentials.requiresTransportSecurity] set to `false` work on every channel.
     */
    public var callCredentials: GrpcCallCredentials = GrpcEmptyCallCredentials
}
