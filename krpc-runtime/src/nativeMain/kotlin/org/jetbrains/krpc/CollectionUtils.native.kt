package org.jetbrains.krpc

@Suppress("FunctionName")
actual fun <K, V> ConcurrentMap(): MutableMap<K, V> = mutableMapOf()
