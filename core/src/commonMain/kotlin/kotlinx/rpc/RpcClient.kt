/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.internal.rpcInternalKClass
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.reflect.KClass
import kotlin.reflect.KType

public abstract class RpcClient<Protocol : RpcProtocol> {
    @OptIn(ExperimentalAtomicApi::class)
    private val serviceIdCounter: AtomicLong = AtomicLong(0)

    public inline fun <reified T : RpcService<Protocol, T>> service(): T {
        return service(T::class)
    }

    public fun <T : RpcService<Protocol, T>> service(serviceKType: KType): T {
        return service(serviceKType.rpcInternalKClass())
    }

    @OptIn(ExperimentalAtomicApi::class)
    public fun <T : RpcService<Protocol, T>> service(serviceKClass: KClass<T>): T {
        return createStub(serviceKClass.factory, serviceIdCounter.incrementAndFetch())
    }

    protected abstract fun <T : RpcService<Protocol, T>> createStub(
        factory: RpcServiceStubFactory<Protocol, T>,
        serviceId: Long,
    ): T
}

public interface RpcUnaryResponseCaller<Protocol : RpcProtocol> {
    public suspend fun <Service : RpcService<Protocol, Service>, T> callWithUnaryResponse(call: RpcCall<Protocol, Service>): T
}

public interface RpcFlowResponseCaller<Protocol : RpcProtocol> {
    public fun <Service : RpcService<Protocol, Service>, T> callWithFlowResponse(call: RpcCall<Protocol, Service>): Flow<T>
}
