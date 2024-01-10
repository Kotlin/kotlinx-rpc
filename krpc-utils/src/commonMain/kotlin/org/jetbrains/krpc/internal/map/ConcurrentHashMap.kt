/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.map

import org.jetbrains.krpc.internal.InternalKRPCApi

@InternalKRPCApi
interface ConcurrentHashMap<K : Any, V : Any> {
    fun put(key: K, value: V): V?

    operator fun set(key: K, value: V) {
        put(key, value)
    }

    fun putIfAbsent(key: K, value: V): V?

    fun putIfAbsentAndGet(key: K, value: V): V {
        return putIfAbsent(key, value) ?: value
    }

    operator fun get(key: K): V?

    fun remove(key: K): V?

    fun clear()

    val values: Collection<V>
}

@InternalKRPCApi
expect fun <K: Any, V: Any> ConcurrentHashMap(initialSize: Int = 32): ConcurrentHashMap<K, V>
