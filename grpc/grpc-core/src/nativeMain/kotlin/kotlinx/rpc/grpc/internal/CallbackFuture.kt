/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.atomicfu.atomic

internal class CallbackFuture<T : Any> {
    private val value = atomic<T?>(null)
    private val callback = atomic<((T) -> Unit)?>(null)

    fun complete(result: T) {
        if (value.compareAndSet(null, result)) {
            callback.getAndSet(null)?.invoke(result)
        } else {
            error("Already completed")
        }
    }

    fun onComplete(cb: (T) -> Unit) {
        val r = value.value
        if (r != null) cb(r)
        else if (!callback.compareAndSet(null, cb)) {
            // Already someone registered → run immediately
            value.value?.let(cb)
        }
    }
}