/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

public actual abstract class ManagedChannelPlatform

public actual abstract class ManagedChannelBuilder<T : ManagedChannelBuilder<T>>

public actual fun ManagedChannelBuilder<*>.buildChannel(): ManagedChannel {
    error("WasmJS target is not supported in gRPC")
}

public actual fun ManagedChannelBuilder(name: String, port: Int): ManagedChannelBuilder<*> {
    error("WasmJS target is not supported in gRPC")
}

public actual fun ManagedChannelBuilder(target: String): ManagedChannelBuilder<*> {
    error("WasmJS target is not supported in gRPC")
}
