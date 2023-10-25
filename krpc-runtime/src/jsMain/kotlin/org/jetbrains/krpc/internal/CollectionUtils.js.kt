package org.jetbrains.krpc.internal

@Suppress("FunctionName")
internal actual fun <K, V> ConcurrentMap(): MutableMap<K, V> = mutableMapOf()
