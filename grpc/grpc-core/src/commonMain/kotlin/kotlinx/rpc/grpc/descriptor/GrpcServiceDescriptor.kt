/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodecResolver
import kotlinx.rpc.grpc.internal.ServiceDescriptor
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public interface GrpcServiceDescriptor<@Grpc T : Any> : RpcServiceDescriptor<T> {
    public fun delegate(resolver: MessageCodecResolver, codecConfig: CodecConfig?): GrpcServiceDelegate
}

@InternalRpcApi
public class GrpcServiceDelegate(
    private val methodDescriptorMap: Map<String, MethodDescriptor<*, *>>,
    public val serviceDescriptor: ServiceDescriptor,
) {
    public fun getMethodDescriptor(methodName: String): MethodDescriptor<*, *>? = methodDescriptorMap[methodName]
}
