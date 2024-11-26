/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import kotlinx.coroutines.Deferred
import kotlinx.rpc.RemoteService
import kotlinx.rpc.RpcCall
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlinx.rpc.grpc.ManagedChannel
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

@ExperimentalRpcApi
public interface GrpcServiceDescriptor<T : RemoteService> : RpcServiceDescriptor<T> {
    public fun delegate(channel: ManagedChannel): GrpcDelegate
}

@ExperimentalRpcApi
public interface GrpcDelegate {
    public suspend fun <R> call(rpcCall: RpcCall): R

    public fun <R> callAsync(rpcCall: RpcCall): Deferred<R>
}
