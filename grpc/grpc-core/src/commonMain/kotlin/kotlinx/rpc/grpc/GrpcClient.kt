/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.grpc.descriptor.GrpcClientDelegate
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * GrpcClient manages gRPC communication by providing implementation for making asynchronous RPC calls.
 *
 * @property channel The [ManagedChannel] used to communicate with remote gRPC services.
 */
public class GrpcClient internal constructor(private val channel: ManagedChannel) : RpcClient {
    override val coroutineContext: CoroutineContext = SupervisorJob()

    private val stubs = RpcInternalConcurrentHashMap<Long, GrpcClientDelegate>()

    override suspend fun <T> call(call: RpcCall): T {
        return call.delegate().call(call)
    }

    override fun <T> callAsync(serviceScope: CoroutineScope, call: RpcCall): Deferred<T> {
        return call.delegate().callAsync(call)
    }

    private fun RpcCall.delegate(): GrpcClientDelegate {
        val grpc = (descriptor as? GrpcServiceDescriptor<*>)
            ?: error("Service ${descriptor.fqName} is not a gRPC service")

        return stubs.computeIfAbsent(serviceId) { grpc.delegate.clientProvider(channel) }
    }

    override fun provideStubContext(serviceId: Long): CoroutineContext {
        return SupervisorJob(coroutineContext.job)
    }
}

/**
 * Constructor function for the [GrpcClient] class.
 */
public fun GrpcClient(
    name: String,
    port: Int,
    configure: ManagedChannelBuilder<*>.() -> Unit = {},
): GrpcClient {
    val channel = ManagedChannelBuilder(name, port).apply(configure).buildChannel()
    return GrpcClient(channel)
}

/**
 * Constructor function for the [GrpcClient] class.
 */
public fun GrpcClient(
    target: String,
    configure: ManagedChannelBuilder<*>.() -> Unit = {},
): GrpcClient {
    val channel = ManagedChannelBuilder(target).apply(configure).buildChannel()
    return GrpcClient(channel)
}
