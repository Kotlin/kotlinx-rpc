/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlin.time.Duration

public interface ManagedChannel {
    public val isShutdown: Boolean
    public val isTerminated: Boolean

    public suspend fun awaitTermination(duration: Duration): Boolean

    public fun shutdown(): ManagedChannel
    public fun shutdownNow(): ManagedChannel
}

public expect abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>>

public expect fun ManagedChannelBuilder(name: String, port: Int): ManagedChannelBuilder<*>
public expect fun ManagedChannelBuilder(target: String): ManagedChannelBuilder<*>

public expect fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel
