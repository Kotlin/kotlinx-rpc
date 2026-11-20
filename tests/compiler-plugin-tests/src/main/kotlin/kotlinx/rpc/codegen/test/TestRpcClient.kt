/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.codegen.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.rpc.RpcCall
import kotlinx.rpc.RpcClient
import kotlinx.rpc.RpcFlowResponseCaller
import kotlinx.rpc.RpcServiceStubFactory
import kotlinx.rpc.RpcUnaryResponseCaller
import kotlinx.rpc.krpc.KrpcProtocol
import kotlinx.rpc.krpc.KrpcService

@Suppress("UNCHECKED_CAST", "unused")
object TestRpcClient : RpcClient<KrpcProtocol>(),
    RpcUnaryResponseCaller<KrpcProtocol>,
    RpcFlowResponseCaller<KrpcProtocol> {

    override suspend fun <Service : KrpcService<Service>, T> callWithUnaryResponse(
        call: RpcCall<KrpcProtocol, Service>
    ): T {
        return "call_42" as T
    }

    override fun <Service : KrpcService<Service>, T> callWithFlowResponse(call: RpcCall<KrpcProtocol, Service>): Flow<T> {
        return flow { emit("stream_42" as T) }
    }
    
    override fun <T : KrpcService<T>> createStub(
        factory: RpcServiceStubFactory<KrpcProtocol, T>,
        serviceId: Long
    ): T {
        return when (factory) {
            is RpcServiceStubFactory.UnaryResponse -> factory.create(serviceId, this)
            is RpcServiceStubFactory.FlowResponse -> factory.create(serviceId, this)
            is RpcServiceStubFactory.EitherResponse -> factory.create(serviceId, this)
        }
    }
}
