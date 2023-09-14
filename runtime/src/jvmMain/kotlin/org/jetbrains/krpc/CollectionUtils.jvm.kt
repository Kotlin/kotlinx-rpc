package org.jetbrains.krpc

import java.util.concurrent.ConcurrentHashMap

actual fun <K, V> ConcurrentMap(): MutableMap<K, V> = ConcurrentHashMap()