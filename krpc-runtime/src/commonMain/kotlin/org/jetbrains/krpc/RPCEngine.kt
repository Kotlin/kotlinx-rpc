package org.jetbrains.krpc

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType

interface RPCEngine : CoroutineScope {
    suspend fun call(callInfo: RPCCallInfo, deferred: CompletableDeferred<*> = CompletableDeferred<Any?>()): Any?

    fun <T> registerFlowField(fieldName: String, type: KType): Flow<T>

    fun <T> registerSharedFlowField(fieldName: String, type: KType): SharedFlow<T>

    fun <T> registerStateFlowField(fieldName: String, type: KType): StateFlow<T>
}
