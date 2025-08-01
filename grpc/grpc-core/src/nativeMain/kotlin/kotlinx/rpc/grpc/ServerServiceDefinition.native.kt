/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.rpc.grpc.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.internal.ServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

public actual class ServerServiceDefinition {
    public actual fun getServiceDescriptor(): ServiceDescriptor {
        TODO("Not yet implemented")
    }

    public actual fun getMethods(): Collection<ServerMethodDefinition<*, *>> {
        TODO("Not yet implemented")
    }
}

@InternalRpcApi
public actual fun serverServiceDefinition(
    serviceDescriptor: ServiceDescriptor,
    methods: Collection<ServerMethodDefinition<*, *>>,
): ServerServiceDefinition {
    TODO("Not yet implemented")
}
