/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual class ServiceDescriptor internal constructor(
    private val name: String,
    private val methods: Collection<MethodDescriptor<*, *>>,
    private val schemaDescriptor: Any?,
) {
    public actual fun getName(): String = name

    public actual fun getMethods(): Collection<MethodDescriptor<*, *>> = methods

    public actual fun getSchemaDescriptor(): Any? = schemaDescriptor
}

@InternalRpcApi
public actual fun serviceDescriptor(
    name: String,
    methods: Collection<MethodDescriptor<*, *>>,
    schemaDescriptor: Any?,
): ServiceDescriptor {
    return ServiceDescriptor(name, methods, schemaDescriptor)
}
