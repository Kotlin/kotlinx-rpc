package org.jetbrains.krpc.internal

import java.util.concurrent.ConcurrentHashMap

@Suppress("FunctionName")
internal actual fun <K, V> ConcurrentMap(): MutableMap<K, V> = ConcurrentHashMap()
