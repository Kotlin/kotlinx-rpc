/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.rpc.internal.utils.map.ConcurrentHashMap

@InternalRPCApi
fun <K : Any, V> ConcurrentHashMap<K, CompletableDeferred<V>>.getDeferred(key: K): CompletableDeferred<V> {
    return computeIfAbsent(key) { CompletableDeferred() }
}

@InternalRPCApi
operator fun <K : Any, V> ConcurrentHashMap<K, CompletableDeferred<V>>.set(key: K, value: V) {
    getDeferred(key).complete(value)
}

@InternalRPCApi
@OptIn(ExperimentalCoroutinesApi::class)
fun <T> CompletableDeferred<T>?.getOrNull() = if (this != null && isCompleted) this.getCompleted() else null
