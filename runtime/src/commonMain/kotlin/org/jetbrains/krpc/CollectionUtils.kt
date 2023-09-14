package org.jetbrains.krpc

expect fun <K, V> ConcurrentMap(): MutableMap<K, V>
