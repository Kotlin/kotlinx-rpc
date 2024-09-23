/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.map

import kotlinx.rpc.internal.utils.InternalRPCApi

@InternalRPCApi
interface ConcurrentHashMap<K : Any, V : Any> {
    fun put(key: K, value: V): V?

    operator fun set(key: K, value: V) {
        put(key, value)
    }

    fun computeIfAbsent(key: K, computeValue: () -> V): V

    operator fun get(key: K): V?

    fun remove(key: K): V?

    fun clear()

    fun containsKey(key: K): Boolean

    val entries: Set<Entry<K, V>>

    val keys: Collection<K>

    val values: Collection<V>

    data class Entry<K : Any, V : Any>(
        val key: K,
        val value: V,
    )
}

@InternalRPCApi
expect fun <K : Any, V : Any> ConcurrentHashMap(initialSize: Int = 32): ConcurrentHashMap<K, V>
