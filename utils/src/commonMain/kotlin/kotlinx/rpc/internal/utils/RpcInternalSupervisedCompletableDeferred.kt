/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job

/**
 * Cancels when parent is canceled, but not otherwise
 */
@InternalRpcApi
public class RpcInternalSupervisedCompletableDeferred<T>(
    parent: Job,
) : CompletableDeferred<T> by CompletableDeferred() {
    init {
        val handle = parent.invokeOnCompletion { cause ->
            if (cause != null) {
                completeExceptionally(cause)
            }
        }

        invokeOnCompletion {
            handle.dispose()
        }
    }
}

@InternalRpcApi
public suspend fun <T> RpcInternalSupervisedCompletableDeferred(): RpcInternalSupervisedCompletableDeferred<T> {
    return RpcInternalSupervisedCompletableDeferred(currentCoroutineContext().job)
}
