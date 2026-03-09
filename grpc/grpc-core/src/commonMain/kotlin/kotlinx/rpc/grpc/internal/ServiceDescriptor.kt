/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public expect class ServiceDescriptor {
    public fun getName(): String
    public fun getMethods(): Collection<GrpcMethodDescriptor<*, *>>
    public fun getSchemaDescriptor(): Any?
}

@InternalRpcApi
public expect fun serviceDescriptor(
    name: String,
    methods: Collection<GrpcMethodDescriptor<*, *>>,
    schemaDescriptor: Any? = null,
): ServiceDescriptor
