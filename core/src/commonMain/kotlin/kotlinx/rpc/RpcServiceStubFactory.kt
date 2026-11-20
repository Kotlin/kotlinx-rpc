/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

public sealed interface RpcServiceStubFactory<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>> {

    public interface UnaryResponse<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>> :
        RpcServiceStubFactory<Protocol, Service> {

        public fun create(serviceId: Long, caller: RpcUnaryResponseCaller<Protocol>): Service
    }

    public interface FlowResponse<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>> :
        RpcServiceStubFactory<Protocol, Service> {

        public fun create(serviceId: Long, caller: RpcFlowResponseCaller<Protocol>): Service
    }

    public interface EitherResponse<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>> :
        RpcServiceStubFactory<Protocol, Service> {

        public fun <Caller> create(serviceId: Long, caller: Caller): Service
            where Caller : RpcFlowResponseCaller<Protocol>,
                  Caller : RpcUnaryResponseCaller<Protocol>
    }
}
