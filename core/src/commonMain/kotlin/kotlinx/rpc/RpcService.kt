/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.rpc.descriptor.RpcServiceDescriptor

public interface RpcService<Protocol : RpcProtocol, Self : RpcService<Protocol, Self>> {
    public val descriptor: RpcServiceDescriptor<Protocol, Self>
}

public interface KrpcProtocol : RpcProtocol

public abstract class KrpcClient : RpcStreamingClient<KrpcProtocol>, RpcSuspendingClient<KrpcProtocol>

public typealias KrpcService<T> = RpcService<KrpcProtocol, T>

public interface MyService : RpcService<KrpcProtocol, MyService> {
    public companion object {
        public val descriptor: RpcServiceDescriptor<KrpcProtocol, MyService> = TODO()

        public fun on(client: KrpcClient): MyService {
            val id = client.getServiceId()
            return descriptor.createInstance(id, client)
        }
    }
}

@OptIn(kotlinx.rpc.DelicateRpcApi::class)
public suspend fun some(client: KrpcClient) {
    client.callStreaming<MyService, String>(RpcNonStreamingCall(
        descriptor = MyService.descriptor,
        callableName = "",
        arguments = arrayOf(),
        serviceId = 1,
    )).collect {

    }

    client.callNonStreaming<MyService, String>(RpcStreamingCall(
        descriptor = MyService.descriptor,
        callableName = "",
        arguments = arrayOf(),
        serviceId = 1,
    ))

    MyService.on(client).descriptor
}
