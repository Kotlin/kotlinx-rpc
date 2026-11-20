/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.test

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

/** A thread-safe event log that lets tests await matching events in insertion order. */
class EventLog<E> {
    private val state = MutableStateFlow<List<E>>(emptyList())

    val events: List<E> get() = state.value

    fun add(event: E) {
        state.update { it + event }
    }

    suspend fun await(timeout: Duration, count: Int = 1, predicate: (E) -> Boolean): E {
        require(count > 0) { "Event count must be positive: $count" }
        val matches = withTimeoutOrNull(timeout) {
            state.map { events -> events.filter(predicate) }.first { it.size >= count }
        } ?: throw AssertionError("The log has no $count matching events after $timeout: ${state.value}")
        return matches[count - 1]
    }
}
