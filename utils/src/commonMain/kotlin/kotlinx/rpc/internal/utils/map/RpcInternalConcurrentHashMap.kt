/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.map

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public interface RpcInternalConcurrentHashMap<K : Any, V : Any> {
    public fun put(key: K, value: V): V?

    public operator fun set(key: K, value: V) {
        put(key, value)
    }

    public fun computeIfAbsent(key: K, computeValue: () -> V): V

    public operator fun get(key: K): V?

    public fun remove(key: K): V?

    public fun clear()

    public fun containsKey(key: K): Boolean

    public val entries: Set<Entry<K, V>>

    public val keys: Collection<K>

    public val values: Collection<V>

    public data class Entry<K : Any, V : Any>(
        val key: K,
        val value: V,
    )
}

@InternalRpcApi
public expect fun <K : Any, V : Any> RpcInternalConcurrentHashMap(
    initialSize: Int = 32,
): RpcInternalConcurrentHashMap<K, V>
