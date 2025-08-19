/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.*

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
) : ManagedChannelBuilder<NativeManagedChannelBuilder>() {
    private var credentials: GrpcCredentials? = null

    override fun usePlaintext(): NativeManagedChannelBuilder {
        credentials = GrpcInsecureCredentials()
        return this
    }

    fun buildChannel(): NativeManagedChannel {
        return NativeManagedChannel(
            target,
            credentials = credentials ?: error("No credentials set"),
        )
    }

}

internal actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    check(this is NativeManagedChannelBuilder) { internalError("Wrong builder type, expected NativeManagedChannelBuilder") }
    return buildChannel()
}

internal actual fun ManagedChannelBuilder(hostname: String, port: Int): ManagedChannelBuilder<*> {
    return NativeManagedChannelBuilder(target = "$hostname:$port")
}

internal actual fun ManagedChannelBuilder(target: String): ManagedChannelBuilder<*> {
    return NativeManagedChannelBuilder(target)
}


