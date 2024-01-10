/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.map

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

internal class SynchronizedHashMap<K : Any, V: Any> : ConcurrentHashMap<K, V>, SynchronizedObject() {
    private val map = hashMapOf<K, V>()

    override fun put(key: K, value: V): V? = synchronized(this) {
        map.put(key, value)
    }

    override fun putIfAbsent(key: K, value: V): V? = synchronized(this) {
        map[key] ?: map.put(key, value)
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

    override val values: Collection<V>
        get() = synchronized(this) { map.values }
}
