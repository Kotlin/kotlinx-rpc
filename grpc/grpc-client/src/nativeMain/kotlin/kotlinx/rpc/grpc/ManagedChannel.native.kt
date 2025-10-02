/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.GrpcChannel
import kotlinx.rpc.grpc.internal.NativeManagedChannel
import kotlinx.rpc.grpc.internal.internalError
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
public actual abstract class ManagedChannelPlatform : GrpcChannel()

/**
 * Builder class for [ManagedChannel].
 */
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>> {
    public actual abstract fun overrideAuthority(authority: String): T
}

internal class NativeManagedChannelBuilder(
    private val target: String,
    private var credentials: Lazy<ClientCredentials>,
) : ManagedChannelBuilder<NativeManagedChannelBuilder>() {

    private var authority: String? = null

    override fun overrideAuthority(authority: String): NativeManagedChannelBuilder {
        this.authority = authority
        return this
    }

    fun buildChannel(): NativeManagedChannel {
        return NativeManagedChannel(
            target,
            authority = authority,
            credentials = credentials.value,
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


