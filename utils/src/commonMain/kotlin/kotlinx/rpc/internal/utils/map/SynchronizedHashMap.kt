/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.map

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

internal class SynchronizedHashMap<K : Any, V: Any> : ConcurrentHashMap<K, V>, SynchronizedObject() {
    private val map = hashMapOf<K, V>()

    override fun put(key: K, value: V): V? = synchronized(this) {
        map.put(key, value)
    }

    override fun computeIfAbsent(key: K, computeValue: () -> V): V = synchronized(this) {
        map[key] ?: computeValue().also { map[key] = it }
    }

    override operator fun get(key: K): V? = synchronized(this) {
        map[key]
    }

    override fun remove(key: K): V? = synchronized(this) {
        map.remove(key)
    }

    override fun clear() = synchronized(this) {
        map.clear()
    }

    override fun containsKey(key: K): Boolean = synchronized(this) {
        map.containsKey(key)
    }

    override val entries: Set<ConcurrentHashMap.Entry<K, V>>
        get() = synchronized(this) { map.entries }.map { ConcurrentHashMap.Entry(it.key, it.value) }.toSet()

    override val keys: Collection<K>
        get() = synchronized(this) { map.keys }

    override val values: Collection<V>
        get() = synchronized(this) { map.values }
}
