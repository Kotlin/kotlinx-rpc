/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.HandlerRegistry
import kotlinx.rpc.grpc.internal.ServerServiceDefinition

public expect abstract class ServerBuilder<T : ServerBuilder<T>> {
    public abstract fun addService(service: ServerServiceDefinition): T

    public abstract fun fallbackHandlerRegistry(registry: HandlerRegistry?): T
}

internal expect fun ServerBuilder(port: Int): ServerBuilder<*>
