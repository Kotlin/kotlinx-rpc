/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

/**
 * Registry of services and their methods used by servers to dispatching incoming calls.
 */
public expect abstract class HandlerRegistry

@Suppress("RedundantConstructorKeyword")
internal expect class MutableHandlerRegistry constructor() : HandlerRegistry {
    internal fun addService(@Suppress("unused") service: ServerServiceDefinition): ServerServiceDefinition?
}
