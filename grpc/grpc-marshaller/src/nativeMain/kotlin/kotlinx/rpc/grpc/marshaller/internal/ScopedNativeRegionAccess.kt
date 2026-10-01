/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer

/** Guards a native pointer scope and keeps Kotlin exceptions out of its callback. */
internal class ScopedNativeRegionAccess {
    private var state: State = State.Open

    val isAccessing: Boolean
        get() = state == State.Accessing

    fun ensureOpen() {
        if (state == State.Open) return
        // A nested access invalidates the outer operation even if its action catches the exception.
        if (state == State.Accessing) state = State.Closed
        error("The native region is not open")
    }

    fun close() {
        if (state == State.Accessing) {
            state = State.Closed
            error("The native region cannot be closed during an access")
        }
        state = State.Closed
    }

    fun invalidate() {
        state = State.Closed
    }

    fun <T> access(
        accessStorage: (block: (CPointer<ByteVar>?) -> Boolean) -> Boolean,
        action: (CPointer<ByteVar>?) -> T,
    ): T {
        ensureOpen()
        state = State.Accessing
        var invoked = false
        var result: Result<T>? = null
        var failure: Throwable? = null
        try {
            val accepted = accessStorage { pointer ->
                try {
                    check(!invoked) { "The storage invoked the action more than once" }
                    invoked = true
                    result = Result.success(action(pointer))
                    state == State.Accessing
                } catch (cause: Throwable) {
                    failure = cause
                    false
                }
            }
            failure?.let { throw it }
            check(accepted && invoked && state == State.Accessing) { "The storage rejected the access" }
            return checkNotNull(result).getOrThrow()
        } finally {
            if (state == State.Accessing) state = State.Open
        }
    }

    private enum class State { Open, Accessing, Closed }
}
