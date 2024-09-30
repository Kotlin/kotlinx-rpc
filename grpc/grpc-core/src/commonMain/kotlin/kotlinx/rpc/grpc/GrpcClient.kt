/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.job
import kotlinx.rpc.RPCCall
import kotlinx.rpc.RPCClient
import kotlinx.rpc.RPCField
import kotlin.coroutines.CoroutineContext

public class GrpcClient(
    private val channel: ManagedChannel,
) : RPCClient {
    override val coroutineContext: CoroutineContext = SupervisorJob()

    override suspend fun <T> call(call: RPCCall): T {
        // todo perform call
        error("not implemented")
    }

    override fun <T> registerPlainFlowField(serviceScope: CoroutineScope, field: RPCField): Flow<T> {
        error("gRPC client does not support field declarations")
    }

    override fun <T> registerSharedFlowField(serviceScope: CoroutineScope, field: RPCField): SharedFlow<T> {
        error("gRPC client does not support field declarations")
    }

    override fun <T> registerStateFlowField(serviceScope: CoroutineScope, field: RPCField): StateFlow<T> {
        error("gRPC client does not support field declarations")
    }

    override fun provideStubContext(serviceId: Long): CoroutineContext {
        // todo create lifetime hierarchy if possible
        return SupervisorJob(coroutineContext.job)
    }
}

public fun grpcClient(
    name: String,
    port: Int,
    configure: ManagedChannelBuilder<*>.() -> Unit = {},
): GrpcClient {
    val channel = ManagedChannelBuilder(name, port).apply(configure).buildChannel()
    return GrpcClient(channel)
}

public fun grpcClient(
    target: String,
    configure: ManagedChannelBuilder<*>.() -> Unit = {},
): GrpcClient {
    val channel = ManagedChannelBuilder(target).apply(configure).buildChannel()
    return GrpcClient(channel)
}
