/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

/**
 * Same as [ManagedChannel], but is platform-exposed.
 */
public actual abstract class ManagedChannelPlatform

/**
 * Builder class for [ManagedChannel].
 */
public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>>

internal actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    error("Native target is not supported in gRPC")
}

internal actual fun ManagedChannelBuilder(name: String, port: Int): ManagedChannelBuilder<*> {
    error("Native target is not supported in gRPC")
}

internal actual fun ManagedChannelBuilder(target: String): ManagedChannelBuilder<*> {
    error("Native target is not supported in gRPC")
}
