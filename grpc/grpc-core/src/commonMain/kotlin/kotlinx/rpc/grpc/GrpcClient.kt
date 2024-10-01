/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import io.grpc.stub.AbstractStub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlin.coroutines.CoroutineContext

public class GrpcClient(private val channel: ManagedChannel) : RpcClient {
    override val coroutineContext: CoroutineContext = SupervisorJob()

    override suspend fun <T> call(call: RpcCall): T {
        val stub = grpcStubByServiceDescriptor(call.descriptor)
        return invokeRpcMethodOnGrpcStub(stub, call)
    }

    private fun grpcStubByServiceDescriptor(descriptor: RpcServiceDescriptor<*>): AbstractStub<*> {
        error("Not yet implemented")
    }

    private suspend fun <T> invokeRpcMethodOnGrpcStub(stub: AbstractStub<*>, call: RpcCall): T {
        error("Not yet implemented")
    }

    override fun <T> callAsync(
        serviceScope: CoroutineScope,
        call: RpcCall,
    ): Deferred<T> {
        TODO("Not yet implemented")
    }

    override fun provideStubContext(serviceId: Long): CoroutineContext {
        // todo create lifetime hierarchy if possible
        return SupervisorJob(coroutineContext.job)
    }
}

public fun GrpcClient(
    name: String,
    port: Int,
    configure: ManagedChannelBuilder<*>.() -> Unit = {},
): GrpcClient {
    val channel = ManagedChannelBuilder(name, port).apply(configure).buildChannel()
    return GrpcClient(channel)
}

public fun GrpcClient(
    target: String,
    configure: ManagedChannelBuilder<*>.() -> Unit = {},
): GrpcClient {
    val channel = ManagedChannelBuilder(target).apply(configure).buildChannel()
    return GrpcClient(channel)
}
