package org.jetbrains.krpc.client.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.krpc.client.UninitializedRPCFieldException
import kotlin.reflect.KProperty

internal class RPCFieldProvider<T, R>(
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

@Suppress("unused")
internal fun <T> RPCFieldProvider(serviceName: String, deferred: CompletableDeferred<T> = CompletableDeferred()): RPCFieldProvider<T, T> {
    return RPCFieldProvider(serviceName, deferred) { this }
}
