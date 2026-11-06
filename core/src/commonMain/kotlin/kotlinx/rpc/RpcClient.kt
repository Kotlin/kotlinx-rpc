/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.descriptor.RpcServiceDescriptor
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@RequiresOptIn
public annotation class DelicateRpcApi

@OptIn(ExperimentalAtomicApi::class)
private val SERVICE_ID: AtomicLong = AtomicLong(0)

@DelicateRpcApi
public class RpcNonStreamingCall<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>>(
    public val descriptor: RpcServiceDescriptor<Protocol, Service>,
    public val callableName: String,
    public val arguments: Array<Any?>,
    public val serviceId: Long,
)

public interface RpcStreamingClient<Protocol : RpcProtocol> : RpcClient<Protocol> {
    @DelicateRpcApi
    public fun <Service : RpcService<Protocol, Service>, T> callStreaming(call: RpcNonStreamingCall<Protocol, Service>): Flow<T>
}

@DelicateRpcApi
public class RpcStreamingCall<Protocol : RpcProtocol, Service : RpcService<Protocol, Service>>(
    public val descriptor: RpcServiceDescriptor<Protocol, Service>,
    public val callableName: String,
    public val arguments: Array<Any?>,
    public val serviceId: Long,
)

public interface RpcSuspendingClient<Protocol : RpcProtocol> : RpcClient<Protocol> {
    @DelicateRpcApi
    public suspend fun <Service : RpcService<Protocol, Service>, T> callNonStreaming(call: RpcStreamingCall<Protocol, Service>): T
}

public interface RpcClient<Protocol : RpcProtocol> {
    @OptIn(ExperimentalAtomicApi::class)
    public fun getServiceId(): Long {
        return SERVICE_ID.incrementAndFetch()
    }
}
