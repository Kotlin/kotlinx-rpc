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
                "A native region reader/writer operation was called inside an active storage block. " +
                    "Do not nest reader/writer operations inside accessStorage, readFromHead or writeToTail; " +
                    "use the supplied pointer and return the consumed/written byte count. " +
                    "The nested access has invalidated the region."
            } else {
                "The native region is closed. A reader is valid only during GrpcMarshaller.decode " +
                    "and a writer only during GrpcEncodedMessage.writeTo. Do not reuse the region " +
                    "after closing it or after a nested access has invalidated it."
            },
        )
    }

    fun close() {
        if (state == State.Accessing) {
            state = State.Closed
            error(
                "The native region was closed inside an active storage block. " +
                    "Call close() only after accessStorage returns, so storage is not released " +
                    "while the read action is using its pointer.",
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
                        "NativeRegionMessageReader/Writer.accessStorage invoked its block more than once. " +
                            "The implementation must invoke the block exactly once and return its Boolean result; " +
                            "retrying the block can read or write the same message bytes twice."
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
                "NativeRegionMessageReader/Writer.accessStorage did not complete the access " +
                    "(accepted=$accepted, blockInvoked=$invoked, state=$state). " +
                    "Ensure backing storage is available, invoke the block exactly once " +
                    "and return its Boolean result. " +
                    "Do not close the region or call another reader/writer operation inside the block."
            }
            return checkNotNull(result).getOrThrow()
        } finally {
            if (state == State.Accessing) state = State.Open
        }
    }

    private enum class State { Open, Accessing, Closed }
}
