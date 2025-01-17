/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package kotlinx.rpc.grpc

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual abstract class HandlerRegistry

@InternalRpcApi
public actual class MutableHandlerRegistry : HandlerRegistry() {
    public actual fun addService(service: ServerServiceDefinition): ServerServiceDefinition? {
        error("WasmJS target is not supported in gRPC")
    }
}
