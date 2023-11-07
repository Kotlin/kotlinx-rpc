package org.jetbrains.krpc

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType

/**
 * Clint engine that can bee used by any RPC service.
 * Handles incoming messages from server and routes them to registered fields, streams or calls.
 * Handles requests made via [call] and routes them to the server.
 *
 * Client engine is also in change of serialization, exception handling and stream management.
 */
interface RPCClientEngine : CoroutineScope {
    suspend fun call(callInfo: RPCCallInfo, callResult: CompletableDeferred<*> = CompletableDeferred<Any?>()): Any?

    fun <T> registerPlainFlowField(fieldName: String, type: KType): Flow<T>

    fun <T> registerSharedFlowField(fieldName: String, type: KType): SharedFlow<T>

    fun <T> registerStateFlowField(fieldName: String, type: KType): StateFlow<T>
}
