/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.atomicfu.atomic
import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.internal.rpcInternalKClass
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * [RpcClient] represents an abstraction of an RPC client, that can handle requests from several RPC services,
 * transform them, send to the server and handle responses and errors.
 */
public abstract class RpcClient<Protocol: RpcProtocol> {
    /**
     * This method is used by generated clients to perform a call to the server.
     *
     * @param T type of the result
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return result of the call, for example, data from the server.
     */
    public abstract suspend fun <T> call(call: RpcCall): T

    /**
     * This method is used by generated clients to perform a call to the server
     * that returns a streaming flow.
     *
     * @param T type of the result
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return result of the call, for example, data from the server
     */
    public abstract fun <T> callStreaming(call: RpcCall): Flow<T>

    /**
     * Creates an instance of the generated service [T], that is able to communicate with a server using this [RpcClient].
     *
     * @param T the exact type of the service to be created.
     * @return instance of the generated service.
     *
     * @see kotlinx.rpc.annotations.CheckedTypeAnnotation
     */
    public inline fun <reified T : RpcService<Protocol, T>> service(): T {
        return service(T::class)
    }

    /**
     * Creates an instance of the generated service [T], that is able to communicate with a server using this [RpcClient].
     *
     * @param T the exact type of the service to be created.
     * @param serviceKType [KType] of the service to be created.
     * @return instance of the generated service.
     *
     * @see kotlinx.rpc.annotations.CheckedTypeAnnotation
     */
    public fun <T : RpcService<Protocol, T>> service(serviceKType: KType): T {
        return service(serviceKType.rpcInternalKClass())
    }

    private val SERVICE_ID = atomic(0L)

    /**
     * Creates an instance of the generated service [T], that is able to communicate with a server using this [RpcClient].
     *
     * @param T the exact type of the service to be created.
     * @param serviceKClass [KClass] of the service to be created.
     * @return instance of the generated service.
     *
     * @see kotlinx.rpc.annotations.CheckedTypeAnnotation
     */
    public fun <T : RpcService<Protocol, T>> service(serviceKClass: KClass<T>): T {
        val descriptor = serviceKClass.descriptor

        val id = SERVICE_ID.incrementAndGet()

        return descriptor.createInstance(id, this)
    }
}
