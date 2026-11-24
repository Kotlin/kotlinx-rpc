/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

/** A one-shot gate that suspends coroutines until a test calls [open]. */
class TestGate(val name: String) {
    private val opened = CompletableDeferred<Unit>()
    private val reached = CompletableDeferred<Unit>()
    private val waiters = MutableStateFlow(0)

    val isOpen: Boolean get() = opened.isCompleted

    /** Whether a coroutine waits in [pass]. Cancellation removes the coroutine from this count. */
    val hasWaiters: Boolean get() = waiters.value > 0

    suspend fun pass() {
        reached.complete(Unit)
        if (opened.isCompleted) {
            return
        }
        waiters.update { it + 1 }
        try {
            opened.await()
        } finally {
            waiters.update { it - 1 }
        }
    }

    suspend fun awaitReached(timeout: Duration) {
        withTimeoutOrNull(timeout) { reached.await() }
            ?: throw AssertionError("No coroutine reached the gate '$name' in $timeout")
    }

    fun open() {
        opened.complete(Unit)
    }
}
