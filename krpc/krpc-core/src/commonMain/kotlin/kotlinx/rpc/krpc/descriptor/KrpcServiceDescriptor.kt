/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.descriptor

import kotlinx.rpc.RpcService
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.KrpcProtocol
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

@InternalRpcApi
public interface KrpcServiceDescriptor<T : RpcService<KrpcProtocol, T>> : RpcServiceDescriptor<KrpcProtocol, T> {
    public val serializers: Map<KClass<KSerializer<Any?>>, KSerializer<Any?>>
}

@InternalRpcApi
public val <Service : RpcService<KrpcProtocol, Service>> KClass<Service>.krpcDescriptor: KrpcServiceDescriptor<Service>
    @Suppress("UNCHECKED_CAST")
    get() = serviceDescriptorOf(this) as? KrpcServiceDescriptor<Service>
        ?: error("Service $this is not a kRPC service")
