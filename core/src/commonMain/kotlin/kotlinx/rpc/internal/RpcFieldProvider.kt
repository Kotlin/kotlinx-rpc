/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.rpc.UninitializedRpcFieldException
import kotlin.reflect.KProperty

internal class RpcFieldProvider<T, R>(
    private val serviceName: String,
    private val deferred: Deferred<T> = CompletableDeferred(),
    val getter: T.() -> R,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun getValue(ref: Any?, property: KProperty<*>): R {
        if (deferred.isCompleted) {
            return deferred.getCompleted().getter()
        }

        throw UninitializedRpcFieldException(serviceName, property)
    }
}

@Suppress("unused")
internal fun <T> RpcFieldProvider(
    serviceName: String,
    deferred: CompletableDeferred<T> = CompletableDeferred()
): RpcFieldProvider<T, T> {
    return RpcFieldProvider(serviceName, deferred) { this }
}
