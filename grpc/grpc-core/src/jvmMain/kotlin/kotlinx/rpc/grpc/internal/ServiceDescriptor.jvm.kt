/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual typealias ServiceDescriptor = io.grpc.ServiceDescriptor

@InternalRpcApi
public actual fun serviceDescriptor(
    name: String,
    methods: Collection<MethodDescriptor<*, *>>,
    schemaDescriptor: Any?,
): ServiceDescriptor {
    return io.grpc.ServiceDescriptor.newBuilder(name)
        .apply {
            methods.forEach { addMethod(it) }
        }
        .setSchemaDescriptor(schemaDescriptor)
        .build()
}
