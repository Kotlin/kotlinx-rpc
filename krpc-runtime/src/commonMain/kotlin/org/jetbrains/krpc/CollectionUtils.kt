package org.jetbrains.krpc

@Suppress("FunctionName")
expect fun <K, V> ConcurrentMap(): MutableMap<K, V>
