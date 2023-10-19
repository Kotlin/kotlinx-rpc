package org.jetbrains.krpc

import java.util.concurrent.ConcurrentHashMap

@Suppress("FunctionName")
actual fun <K, V> ConcurrentMap(): MutableMap<K, V> = ConcurrentHashMap()
