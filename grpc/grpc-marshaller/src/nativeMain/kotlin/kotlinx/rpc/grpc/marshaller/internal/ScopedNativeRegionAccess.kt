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
        val wasAccessing = state == State.Accessing
        // A nested access invalidates the outer operation even if its action catches the exception.
        if (state == State.Accessing) state = State.Closed
        error(
            if (wasAccessing) {
                "Nested native reader/writer access is not allowed; the region has been invalidated. " +
                    "Use the supplied pointer instead of calling another reader/writer operation."
            } else {
                "The native region is closed. Readers are valid only during GrpcMarshaller.decode; " +
                    "writers only during GrpcEncodedMessage.writeTo."
            },
        )
    }

    fun close() {
        if (state == State.Accessing) {
            state = State.Closed
            error(
                "Cannot close a native region while its storage block is running. " +
                    "Release storage only after accessStorage returns.",
            )
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
                    check(!invoked) {
                        "Native accessStorage invoked its block more than once; exactly one invocation is required."
                    }
                    invoked = true
                    result = Result.success(action(pointer))
                    state == State.Accessing
                } catch (cause: Throwable) {
                    failure = cause
                    false
                }
            }
            failure?.let { throw it }
            check(accepted && invoked && state == State.Accessing) {
                "Native accessStorage failed (accepted=$accepted, blockInvoked=$invoked, state=$state). " +
                    "Invoke the block once and return its result without closing the region."
            }
            return checkNotNull(result).getOrThrow()
        } finally {
            if (state == State.Accessing) state = State.Open
        }
    }

    private enum class State { Open, Accessing, Closed }
}
