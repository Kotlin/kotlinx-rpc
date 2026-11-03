/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.RpcService
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.grpc.GrpcProtocol
import kotlinx.rpc.grpc.internal.ServiceDescriptor
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.reflect.KClass

@InternalRpcApi
public interface GrpcServiceDescriptor<T : RpcService<GrpcProtocol, T>> : RpcServiceDescriptor<GrpcProtocol, T> {
    public fun delegate(resolver: GrpcMarshallerResolver, marshallerConfig: GrpcMarshallerConfig?): GrpcServiceDelegate
}

@InternalRpcApi
public class GrpcServiceDelegate(
    private val methodDescriptorMap: Map<String, GrpcMethodDescriptor<*, *>>,
    public val serviceDescriptor: ServiceDescriptor,
) {
    public fun getMethodDescriptor(methodName: String): GrpcMethodDescriptor<*, *>? = methodDescriptorMap[methodName]
}

@InternalRpcApi
public val <Service : RpcService<GrpcProtocol, Service>> KClass<Service>.grpcDescriptor: GrpcServiceDescriptor<Service>
    @Suppress("UNCHECKED_CAST")
    get() = serviceDescriptorOf(this) as? GrpcServiceDescriptor<Service>
        ?: error("Service $this is not a gRPC service")
