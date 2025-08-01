/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual class ServiceDescriptor {
    public actual fun getName(): String {
        TODO("Not yet implemented")
    }

    public actual fun getMethods(): Collection<MethodDescriptor<*, *>> {
        TODO("Not yet implemented")
    }

    public actual fun getSchemaDescriptor(): Any? {
        TODO("Not yet implemented")
    }
}

@InternalRpcApi
public actual fun serviceDescriptor(
    name: String,
    methods: Collection<MethodDescriptor<*, *>>,
    schemaDescriptor: Any?,
): ServiceDescriptor {
    TODO("Not yet implemented")
}
