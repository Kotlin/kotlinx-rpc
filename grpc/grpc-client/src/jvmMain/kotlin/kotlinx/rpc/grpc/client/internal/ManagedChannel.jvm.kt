/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc.client.internal

import io.grpc.Grpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.rpc.grpc.client.ClientCredentials
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
@InternalRpcApi
public actual typealias ManagedChannelPlatform = io.grpc.ManagedChannel

/**
 * Builder class for [ManagedChannel].
 */
@InternalRpcApi
public actual typealias ManagedChannelBuilder<T> = io.grpc.ManagedChannelBuilder<T>

@InternalRpcApi
public actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    return build().toKotlin()
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    hostname: String,
    port: Int,
    credentials: ClientCredentials?,
): ManagedChannelBuilder<*> {
    if (credentials != null) return Grpc.newChannelBuilderForAddress(hostname, port, credentials)
    return io.grpc.ManagedChannelBuilder.forAddress(hostname, port)
}

@InternalRpcApi
public actual fun ManagedChannelBuilder(
    target: String,
    credentials: ClientCredentials?,
): ManagedChannelBuilder<*> {
    if (credentials != null) return Grpc.newChannelBuilder(target, credentials)
    return io.grpc.ManagedChannelBuilder.forTarget(target)
}

public fun io.grpc.ManagedChannel.toKotlin(): ManagedChannel {
    return JvmManagedChannel(this)
}

private class JvmManagedChannel(private val channel: io.grpc.ManagedChannel) : ManagedChannel {
    override val isShutdown: Boolean
        get() = channel.isShutdown

    override val isTerminated: Boolean
        get() = channel.isTerminated

    override suspend fun awaitTermination(duration: Duration): Boolean {
        return withContext(Dispatchers.IO) {
            channel.awaitTermination(duration.inWholeNanoseconds, TimeUnit.NANOSECONDS)
        }
    }

    override fun shutdown(): ManagedChannel {
        channel.shutdown()
        return this
    }

    override fun shutdownNow(): ManagedChannel {
        channel.shutdownNow()
        return this
    }

    override val platformApi: ManagedChannelPlatform
        get() = channel
}
