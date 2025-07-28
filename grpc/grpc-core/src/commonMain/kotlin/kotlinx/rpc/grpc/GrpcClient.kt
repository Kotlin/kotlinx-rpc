/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.grpc.descriptor.GrpcClientDelegate
import kotlinx.rpc.grpc.descriptor.GrpcServiceDescriptor
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap
import kotlin.time.Duration

/**
 * GrpcClient manages gRPC communication by providing implementation for making asynchronous RPC calls.
 *
 * @field channel The [ManagedChannel] used to communicate with remote gRPC services.
 */
public class GrpcClient internal constructor(private val channel: ManagedChannel) : RpcClient {
    private val stubs = RpcInternalConcurrentHashMap<Long, GrpcClientDelegate>()

    public fun shutdown() {
        stubs.clear()
        channel.shutdown()
    }

    public fun shutdownNow() {
        stubs.clear()
        channel.shutdownNow()
    }

    public suspend fun awaitTermination(duration: Duration = Duration.INFINITE) {
        channel.awaitTermination(duration)
    }

    override suspend fun <T> call(call: RpcCall): T {
        return call.delegate().call(call)
    }

    override fun <T> callServerStreaming(call: RpcCall): Flow<T> {
        return call.delegate().callServerStreaming(call)
    }

    private fun RpcCall.delegate(): GrpcClientDelegate {
        val grpc = (descriptor as? GrpcServiceDescriptor<*>)
            ?: error("Service ${descriptor.fqName} is not a gRPC service")

        return stubs.computeIfAbsent(serviceId) { grpc.delegate.clientProvider(channel) }
    }
}

/**
 * Constructor function for the [GrpcClient] class.
 */
public fun GrpcClient(
    hostname: String,
    port: Int,
    configure: ManagedChannelBuilder<*>.() -> Unit = {},
): GrpcClient {
    val channel = ManagedChannelBuilder(hostname, port).apply(configure).buildChannel()
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
