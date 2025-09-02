/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.internal.ServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class ServerServiceDefinition internal constructor(
    private val serviceDescriptor: ServiceDescriptor,
    private val methods: Collection<ServerMethodDefinition<*, *>>,
) {
    public actual fun getServiceDescriptor(): ServiceDescriptor = serviceDescriptor

    public actual fun getMethods(): Collection<ServerMethodDefinition<*, *>> = methods
}

@InternalRpcApi
public actual fun serverServiceDefinition(
    serviceDescriptor: ServiceDescriptor,
    methods: Collection<ServerMethodDefinition<*, *>>,
): ServerServiceDefinition {
    return ServerServiceDefinition(serviceDescriptor, methods)
}
