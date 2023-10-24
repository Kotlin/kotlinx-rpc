package org.jetbrains.krpc.client

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.reflect.KProperty

internal class RPCPropertyProvider<T, R>(
    private val serviceName: String,
    private val deferred: CompletableDeferred<T> = CompletableDeferred(),
    val getter: T.() -> R,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun getValue(ref: Any?, property: KProperty<*>): R {
        if (deferred.isCompleted) {
            return deferred.getCompleted().getter()
        }

        throw UninitializedRPCFieldException(serviceName, property)
    }
}

@Suppress("FunctionName", "unused")
internal fun <T> RPCFieldProvider(serviceName: String, deferred: CompletableDeferred<T> = CompletableDeferred()): RPCPropertyProvider<T, T> {
    return RPCPropertyProvider(serviceName, deferred) { this }
}
