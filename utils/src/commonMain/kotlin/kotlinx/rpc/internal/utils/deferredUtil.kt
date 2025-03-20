/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap

@InternalRpcApi
public fun <K : Any, V> RpcInternalConcurrentHashMap<K, CompletableDeferred<V>>.getDeferred(
    key: K,
): CompletableDeferred<V> {
    return computeIfAbsent(key) { CompletableDeferred() }
}

@InternalRpcApi
public operator fun <K : Any, V> RpcInternalConcurrentHashMap<K, CompletableDeferred<V>>.set(key: K, value: V) {
    getDeferred(key).complete(value)
}

@InternalRpcApi
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> CompletableDeferred<T>?.getOrNull() = if (this != null && isCompleted) this.getCompleted() else null
