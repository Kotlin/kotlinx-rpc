package org.jetbrains.krpc

import kotlinx.coroutines.channels.Channel

expect fun <K, V> ConcurrentMap(): MutableMap<K, V>
