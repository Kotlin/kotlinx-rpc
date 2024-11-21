/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
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
class SupervisedCompletableDeferred<T>(parent: Job) : CompletableDeferred<T> by CompletableDeferred() {
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
suspend fun <T> SupervisedCompletableDeferred(): SupervisedCompletableDeferred<T> {
    return SupervisedCompletableDeferred(currentCoroutineContext().job)
}
