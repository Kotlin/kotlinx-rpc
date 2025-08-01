/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.internal.ServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

public actual typealias ServerServiceDefinition = io.grpc.ServerServiceDefinition

@InternalRpcApi
public actual fun serverServiceDefinition(
    serviceDescriptor: ServiceDescriptor,
    methods: Collection<ServerMethodDefinition<*, *>>,
): ServerServiceDefinition {
    return io.grpc.ServerServiceDefinition.builder(serviceDescriptor).apply {
        methods.forEach { addMethod(it) }
    }.build()
}
