/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.GrpcChannel
import kotlin.time.Duration

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
public expect abstract class ManagedChannelPlatform : GrpcChannel

/**
 * A virtual connection to a conceptual endpoint, to perform RPCs.
 * A channel is free to have zero or many actual connections to the endpoint based on configuration,
 * load, etc. A channel is also free to determine which actual endpoints to use and may change it every RPC,
 * permitting client-side load balancing.
 *
 * Provides lifecycle management.
 */
public interface ManagedChannel {
    /**
     * Returns whether the channel is shutdown.
     * Shutdown channels immediately cancel any new calls but may still have some calls being processed.
     */
    public val isShutdown: Boolean

    /**
     * Returns whether the channel is terminated.
     * Terminated channels have no running calls and relevant resources released (like TCP connections).
     */
    public val isTerminated: Boolean

    /**
     * Waits for the channel to become terminated, giving up if the timeout is reached.
     *
     * @return whether the channel is terminated, as would be done by [isTerminated].
     */
    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE): Boolean

    /**
     * Initiates an orderly shutdown in which preexisting calls continue but new calls are immediately canceled.
     *
     * @return `this`
     */
    public fun shutdown(): ManagedChannel

    /**
     * Initiates a forceful shutdown in which preexisting and new calls are canceled.
     * Although forceful, the shutdown process is still not instantaneous; [isTerminated] will likely
     * return `false` immediately after this method returns.
     *
     * @return this
     */
    public fun shutdownNow(): ManagedChannel

    /**
     * Exposes the platform-specific version of this API.
     */
    public val platformApi: ManagedChannelPlatform
}

/**
 * Builder class for [ManagedChannel].
 */
public expect abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>> {
    public fun usePlaintext(): T
}

internal expect fun ManagedChannelBuilder(hostname: String, port: Int): ManagedChannelBuilder<*>
internal expect fun ManagedChannelBuilder(target: String): ManagedChannelBuilder<*>

internal expect fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel
