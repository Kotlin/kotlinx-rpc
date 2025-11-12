/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.GlobalScope
import kotlinx.rpc.grpc.client.ClientCredentials
import kotlinx.rpc.grpc.client.GrpcClientConfiguration
import kotlinx.rpc.grpc.client.TlsClientCredentials
import kotlinx.rpc.grpc.client.createRaw
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
@InternalRpcApi
public actual abstract class ManagedChannelPlatform : GrpcChannel()

/**
 * Builder class for [ManagedChannel].
 */
@InternalRpcApi
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>> {
    internal var config: GrpcClientConfiguration? = null
}

internal class NativeManagedChannelBuilder(
    private val target: String,
    private val credentials: Lazy<ClientCredentials>,
) : ManagedChannelBuilder<NativeManagedChannelBuilder>() {
    fun buildChannel(): NativeManagedChannel {
        val keepAlive = config?.keepAlive
        keepAlive?.run {
            require(time.isPositive()) { "keepalive time must be positive" }
            require(timeout.isPositive()) { "keepalive timeout must be positive" }
        }

        return NativeManagedChannel(
            target,
            overrideAuthority = config?.overrideAuthority,
            keepAlive = config?.keepAlive,
            clientCredentials = credentials.value,
        )
    }

}

@InternalRpcApi
public actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    check(this is NativeManagedChannelBuilder) { internalError("Wrong builder type, expected NativeManagedChannelBuilder") }
    return buildChannel()
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    hostname: String,
    port: Int,
    credentials: ClientCredentials?,
): ManagedChannelBuilder<*> {
    val credentials = if (credentials == null) lazy { TlsClientCredentials() } else lazy { credentials }
    return NativeManagedChannelBuilder(target = "$hostname:$port", credentials)
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(target: String, credentials: ClientCredentials?): ManagedChannelBuilder<*> {
    val credentials = if (credentials == null) lazy { TlsClientCredentials() } else lazy { credentials }
    return NativeManagedChannelBuilder(target, credentials)
}

internal actual fun ManagedChannelBuilder<*>.applyConfig(config: GrpcClientConfiguration): ManagedChannelBuilder<*> {
    this.config = config
    return this
}