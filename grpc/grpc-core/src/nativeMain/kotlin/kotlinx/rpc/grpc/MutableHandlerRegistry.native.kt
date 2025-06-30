/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

/**
 * Registry of services and their methods used by servers to dispatching incoming calls.
 */
public actual abstract class HandlerRegistry

internal actual class MutableHandlerRegistry : HandlerRegistry() {
    actual fun addService(service: ServerServiceDefinition): ServerServiceDefinition? {
        error("Native target is not supported in gRPC")
    }

    actual fun removeService(service: ServerServiceDefinition): Boolean {
        error("Native target is not supported in gRPC")
    }
}
