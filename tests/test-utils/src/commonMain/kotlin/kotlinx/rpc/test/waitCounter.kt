/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.collections.orEmpty
import kotlin.collections.plus
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class WaitCounter {
    val value: Int get() = counter.value
    private val counter = atomic(0)
    private val lock = ReentrantLock()
    private val waiters = mutableMapOf<Int, List<Continuation<Unit>>>()

    fun increment() {
        lock.withLock {
            val current = counter.incrementAndGet()
            waiters[current]?.forEach { it.resume(Unit) }
        }
    }

    suspend fun await(value: Int) = suspendCancellableCoroutine {
        lock.withLock {
            if (counter.value == value) {
                it.resume(Unit)
            } else {
                waiters[value] = waiters[value].orEmpty() + it
            }
        }
    }
}
