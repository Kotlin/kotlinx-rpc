/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
public actual typealias ManagedChannelPlatform = io.grpc.ManagedChannel

/**
 * Builder class for [ManagedChannel].
 */
public actual typealias ManagedChannelBuilder<T> = io.grpc.ManagedChannelBuilder<T>

internal actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    return build().toKotlin()
}

internal actual fun ManagedChannelBuilder(name: String, port: Int): ManagedChannelBuilder<*> {
    return io.grpc.ManagedChannelBuilder.forAddress(name, port)
}

internal actual fun ManagedChannelBuilder(target: String): ManagedChannelBuilder<*> {
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
            channel.awaitTermination(duration.inWholeNanoseconds, java.util.concurrent.TimeUnit.NANOSECONDS)
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
