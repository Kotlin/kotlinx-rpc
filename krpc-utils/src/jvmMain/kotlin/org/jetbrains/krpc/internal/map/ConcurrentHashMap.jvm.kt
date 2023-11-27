package org.jetbrains.krpc.internal.map

import org.jetbrains.krpc.internal.InternalKRPCApi

@InternalKRPCApi
actual fun <K : Any, V : Any> ConcurrentHashMap(initialSize: Int): ConcurrentHashMap<K, V> {
    return ConcurrentHasMapJvm(initialSize)
}

private class ConcurrentHasMapJvm<K : Any, V: Any>(initialSize: Int) : ConcurrentHashMap<K, V> {
    private val map = java.util.concurrent.ConcurrentHashMap<K, V>(initialSize)

    override fun put(key: K, value: V): V? {
        return map.put(key, value)
    }

    override fun putIfAbsent(key: K, value: V): V? {
        return map.putIfAbsent(key, value)
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

    override val values: Collection<V>
        get() = map.values
}
