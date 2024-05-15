/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal

import kotlinx.coroutines.*

/**
 * Bounds coroutine scopes of the request and provided RPC service.
 *
 * Used by code generators.
 */
@InternalRPCApi
@OptIn(InternalCoroutinesApi::class)
@Suppress("unused")
public suspend inline fun <T> scopedClientCall(serviceScope: CoroutineScope, crossinline body: suspend () -> T): T {
    val requestJob = currentCoroutineContext().job
    val handle = serviceScope.coroutineContext.job.invokeOnCompletion(onCancelling = true) {
        requestJob.cancel(it as CancellationException)
    }

    try {
        return serviceScoped(serviceScope) {
            body()
        }
    } finally {
        handle.dispose()
    }
}
