/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.map

import kotlinx.rpc.internal.utils.InternalRPCApi

@InternalRPCApi
actual fun <K : Any, V : Any> ConcurrentHashMap(initialSize: Int): ConcurrentHashMap<K, V> {
    return SynchronizedHashMap()
}
