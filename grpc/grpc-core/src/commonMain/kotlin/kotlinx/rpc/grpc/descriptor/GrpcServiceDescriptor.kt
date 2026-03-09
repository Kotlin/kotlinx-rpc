/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlinx.rpc.grpc.internal.ServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public interface GrpcServiceDescriptor<@Grpc T : Any> : RpcServiceDescriptor<T> {
    public fun delegate(resolver: GrpcMarshallerResolver, marshallerConfig: GrpcMarshallerConfig?): GrpcServiceDelegate
}

@InternalRpcApi
public class GrpcServiceDelegate(
    private val methodDescriptorMap: Map<String, GrpcMethodDescriptor<*, *>>,
    public val serviceDescriptor: ServiceDescriptor,
) {
    public fun getMethodDescriptor(methodName: String): GrpcMethodDescriptor<*, *>? = methodDescriptorMap[methodName]
}
