/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext

/**
 * [RpcClient] represents an abstraction of an RPC client, that can handle requests from several RPC services,
 * transform them, send to the server and handle responses and errors.
 * [CoroutineScope] defines the lifetime of the client.
 */
public interface RpcClient : CoroutineScope {
    /**
     * This method is used by generated clients to perform a call to the server.
     *
     * @param T type of the result
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return actual result of the call, for example, data from the server.
     */
    public suspend fun <T> call(call: RpcCall): T

    /**
     * This method is used by generated clients to perform a call to the server.
     *
     * @param T type of the result
     * @param serviceScope service's coroutine scope
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return actual result of the call, for example, data from the server
     */
    public fun <T> callAsync(serviceScope: CoroutineScope, call: RpcCall): Deferred<T>

    /**
     * Provides child [CoroutineContext] for a new [RemoteService] service stub.
     *
     * This function shouldn't be called directly.
     *
     * @param serviceId id of the new service. Used for service cancellation messages.
     */
    public fun provideStubContext(serviceId: Long): CoroutineContext
}