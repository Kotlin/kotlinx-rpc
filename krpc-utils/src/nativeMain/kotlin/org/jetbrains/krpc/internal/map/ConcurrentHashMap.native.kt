/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package org.jetbrains.krpc.internal.map

import org.jetbrains.krpc.internal.InternalKRPCApi

@InternalKRPCApi
actual fun <K : Any, V : Any> ConcurrentHashMap(initialSize: Int): ConcurrentHashMap<K, V> {
    return SynchronizedHashMap()
}
