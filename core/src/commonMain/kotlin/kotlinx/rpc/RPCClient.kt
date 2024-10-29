/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.CoroutineContext

/**
 * RPCClient represents an abstraction of a RPC client, that can handle requests from several RPC services,
 * transform them, send to the server and handle responses and errors.
 * [CoroutineScope] defines the lifetime of the client.
 */
public interface RPCClient : CoroutineScope {
    /**
     * This method is used by generated clients to perform a call to the server.
     *
     * @param T type of the result
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return actual result of the call, e.g. data from the server.
     */
    public suspend fun <T> call(call: RPCCall): T

    /**
     * Registers Flow<T> field of the interface. Sends initialization request, subscribes to emitted values
     * and returns the instance of the flow to be consumed
     *
     * @param T type parameter for Flow
     * @param serviceScope Service's coroutine scope
     * @param field object that contains information about the field,
     * that is used to be mapped to the corresponding field pn server.
     * @return Flow instance to be consumed.
     */
    public fun <T> registerPlainFlowField(serviceScope: CoroutineScope, field: RPCField): Flow<T>

    /**
     * Registers SharedFlow<T> field of the interface. Sends initialization request, subscribes to emitted values
     * and returns the instance of the flow to be consumed
     *
     * @param T type parameter for SharedFlow
     * @param serviceScope Service's coroutine scope
     * @param field object that contains information about the field,
     * that is used to be mapped to the corresponding field pn server.
     * @return SharedFlow instance to be consumed.
     */
    public fun <T> registerSharedFlowField(serviceScope: CoroutineScope, field: RPCField): SharedFlow<T>

    /**
     * Registers StateFlow<T> field of the interface. Sends initialization request, subscribes to emitted values
     * and returns the instance of the flow to be consumed
     *
     * @param T type parameter for StateFlow
     * @param serviceScope Service's coroutine scope
     * @param field object that contains information about the field,
     * that is used to be mapped to the corresponding field pn server.
     * @return StateFlow instance to be consumed.
     */
    public fun <T> registerStateFlowField(serviceScope: CoroutineScope, field: RPCField): StateFlow<T>

    /**
     * Provides child [CoroutineContext] for a new [RemoteService] service stub.
     *
     * This function shouldn't be called directly.
     *
     * @param serviceId id of the new service. Used for service cancellation messages.
     */
    public fun provideStubContext(serviceId: Long): CoroutineContext
}
