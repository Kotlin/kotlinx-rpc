/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.krpc.descriptor

import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.descriptor.serviceDescriptorOf
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.krpc.KrpcProtocol
import kotlinx.rpc.krpc.KrpcService
import kotlinx.serialization.KSerializer
import kotlin.reflect.KClass

@InternalRpcApi
public typealias KrpcSerializers = Map<KClass<KSerializer<Any?>>, KSerializer<Any?>>

@InternalRpcApi
public interface KrpcServiceDescriptor<T : KrpcService<T>> : RpcServiceDescriptor<KrpcProtocol, T> {
    public val serializers: KrpcSerializers
}

@InternalRpcApi
public val <Service : KrpcService<Service>> KClass<Service>.krpcDescriptor: KrpcServiceDescriptor<Service>
    @Suppress("UNCHECKED_CAST")
    get() = serviceDescriptorOf(this) as? KrpcServiceDescriptor<Service>
        ?: error("Service $this is not a kRPC service")
