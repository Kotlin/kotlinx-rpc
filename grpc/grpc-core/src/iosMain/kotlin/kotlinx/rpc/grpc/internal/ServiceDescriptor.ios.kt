/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual class ServiceDescriptor internal constructor(
    name: String,
    methods: Collection<GrpcMethodDescriptor<*, *>>,
    schemaDescriptor: Any?,
) {
    public actual fun getName(): String = TODO("Implement iOS gRPC service descriptors")

    public actual fun getMethods(): Collection<GrpcMethodDescriptor<*, *>> =
        TODO("Implement iOS gRPC service descriptors")

    public actual fun getSchemaDescriptor(): Any? = TODO("Implement iOS gRPC service descriptors")
}

@InternalRpcApi
public actual fun serviceDescriptor(
    name: String,
    methods: Collection<GrpcMethodDescriptor<*, *>>,
    schemaDescriptor: Any?,
): ServiceDescriptor = TODO("Implement iOS gRPC service descriptors")
