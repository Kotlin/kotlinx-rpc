package org.jetbrains.krpc

actual fun <K, V> ConcurrentMap(): MutableMap<K, V> = mutableMapOf()