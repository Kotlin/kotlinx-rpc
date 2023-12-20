package org.jetbrains.krpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Client that can bee used by any RPC service.
 * Handles incoming messages from server and routes them to registered fields, streams or calls.
 * Handles requests made via [call] and routes them to the server.
 *
 * Client is also in change of serialization, exception handling and stream management.
 */
interface RPCClient : CoroutineScope {
    suspend fun <T> call(call: RPCCall): T

    fun <T> registerPlainFlowField(field: RPCField): Flow<T>

    fun <T> registerSharedFlowField(field: RPCField): SharedFlow<T>

    fun <T> registerStateFlowField(field: RPCField): StateFlow<T>
}
