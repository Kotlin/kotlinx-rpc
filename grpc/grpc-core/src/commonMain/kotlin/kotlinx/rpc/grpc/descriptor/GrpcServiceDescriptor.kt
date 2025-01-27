/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.coroutines.Deferred
import kotlinx.rpc.RpcCall
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.grpc.ManagedChannel
import kotlinx.rpc.grpc.ServerServiceDefinition
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@ExperimentalRpcApi
public interface GrpcServiceDescriptor<@Grpc T : Any> : RpcServiceDescriptor<T> {
    public val delegate: GrpcDelegate<T>
}

@ExperimentalRpcApi
public interface GrpcDelegate<@Grpc T : Any> {
    public fun clientProvider(channel: ManagedChannel): GrpcClientDelegate

    public fun definitionFor(impl: T): ServerServiceDefinition
}

@ExperimentalRpcApi
public interface GrpcClientDelegate {
    public suspend fun <R> call(rpcCall: RpcCall): R

    public fun <R> callAsync(rpcCall: RpcCall): Deferred<R>
}
