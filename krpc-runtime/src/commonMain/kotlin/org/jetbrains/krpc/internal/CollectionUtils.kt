package org.jetbrains.krpc.internal

@Suppress("FunctionName")
internal expect fun <K, V> ConcurrentMap(): MutableMap<K, V>
