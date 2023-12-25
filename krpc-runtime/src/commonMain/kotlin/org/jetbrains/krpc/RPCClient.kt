package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * RPCClient represents an abstraction of a RPC client, that can handle requests from several RPC services,
 * transform them, send to the server and handle responses and errors.
 * [CoroutineScope] defines the lifetime of the client.
 */
interface RPCClient : CoroutineScope {
    /**
     * This method is used by generated clients to perform a call to the server.
     *
     * @param T type of the result
     * @param call an object that contains all required information about the called method,
     * that is needed to route it properly to the server.
     * @return actual result of the call, e.g. data from the server.
     */
    suspend fun <T> call(call: RPCCall): T

    /**
     * Registers Flow<T> field of the interface. Sends initialization request, subscribes to emitted values
     * and returns the instance of the flow to be consumed
     *
     * @param T type parameter for Flow
     * @param field object that contains information about the field,
     * that is used to be mapped to the corresponding field pn server.
     * @return Flow instance to be consumed.
     */
    fun <T> registerPlainFlowField(field: RPCField): Flow<T>

    /**
     * Registers SharedFlow<T> field of the interface. Sends initialization request, subscribes to emitted values
     * and returns the instance of the flow to be consumed
     *
     * @param T type parameter for SharedFlow
     * @param field object that contains information about the field,
     * that is used to be mapped to the corresponding field pn server.
     * @return SharedFlow instance to be consumed.
     */
    fun <T> registerSharedFlowField(field: RPCField): SharedFlow<T>

    /**
     * Registers StateFlow<T> field of the interface. Sends initialization request, subscribes to emitted values
     * and returns the instance of the flow to be consumed
     *
     * @param T type parameter for StateFlow
     * @param field object that contains information about the field,
     * that is used to be mapped to the corresponding field pn server.
     * @return StateFlow instance to be consumed.
     */
    fun <T> registerStateFlowField(field: RPCField): StateFlow<T>
}
