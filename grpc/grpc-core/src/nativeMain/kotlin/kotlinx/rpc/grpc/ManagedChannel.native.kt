/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.GrpcChannel
import kotlinx.rpc.grpc.internal.NativeManagedChannel
import kotlinx.rpc.grpc.internal.internalError

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
public actual abstract class ManagedChannelPlatform : GrpcChannel()

/**
 * Builder class for [ManagedChannel].
 */
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>> {
    public actual open fun usePlaintext(): T {
        error("Builder does not support usePlaintext()")
    }
}

internal class NativeManagedChannelBuilder(
    private val target: String,
    private var credentials: Lazy<ChannelCredentials>,
) : ManagedChannelBuilder<NativeManagedChannelBuilder>() {

    override fun usePlaintext(): NativeManagedChannelBuilder {
        credentials = lazy { InsecureChannelCredentials() }
        return this
    }

    fun buildChannel(): NativeManagedChannel {
        return NativeManagedChannel(
            target,
            credentials = credentials.value,
        )
    }

}

internal actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    check(this is NativeManagedChannelBuilder) { internalError("Wrong builder type, expected NativeManagedChannelBuilder") }
    return buildChannel()
}

internal actual fun ManagedChannelBuilder(
    hostname: String,
    port: Int,
    credentials: ChannelCredentials?,
): ManagedChannelBuilder<*> {
    val credentials = if (credentials == null) lazy { TlsChannelCredentials() } else lazy { credentials }
    return NativeManagedChannelBuilder(target = "$hostname:$port", credentials)
}

internal actual fun ManagedChannelBuilder(target: String, credentials: ChannelCredentials?): ManagedChannelBuilder<*> {
    val credentials = if (credentials == null) lazy { TlsChannelCredentials() } else lazy { credentials }
    return NativeManagedChannelBuilder(target, credentials)
}


