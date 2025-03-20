/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.map

import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public actual fun <K : Any, V : Any> RpcInternalConcurrentHashMap(
    initialSize: Int,
): RpcInternalConcurrentHashMap<K, V> {
    return ConcurrentHashMapJvm(initialSize)
}

private class ConcurrentHashMapJvm<K : Any, V: Any>(initialSize: Int) : RpcInternalConcurrentHashMap<K, V> {
    private val map = java.util.concurrent.ConcurrentHashMap<K, V>(initialSize)

    override fun put(key: K, value: V): V? {
        return map.put(key, value)
    }

    override fun computeIfAbsent(key: K, computeValue: () -> V): V {
        return map.computeIfAbsent(key) { computeValue() }
    }

    override operator fun get(key: K): V? {
        return map[key]
    }

    override fun remove(key: K): V? {
        return map.remove(key)
    }

    override fun clear() {
        map.clear()
    }

    override fun containsKey(key: K): Boolean {
        return map.containsKey(key)
    }

    override val entries: Set<RpcInternalConcurrentHashMap.Entry<K, V>>
        get() = map.entries.map { RpcInternalConcurrentHashMap.Entry(it.key, it.value) }.toSet()

    override val keys: Collection<K>
        get() = map.keys

    override val values: Collection<V>
        get() = map.values
}
