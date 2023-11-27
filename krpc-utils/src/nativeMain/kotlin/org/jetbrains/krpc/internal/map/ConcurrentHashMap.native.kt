package org.jetbrains.krpc.internal.map

import org.jetbrains.krpc.internal.InternalKRPCApi

@InternalKRPCApi
actual fun <K : Any, V : Any> ConcurrentHashMap(initialSize: Int): ConcurrentHashMap<K, V> {
    return SynchronizedHashMap()
}
