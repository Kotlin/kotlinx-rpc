/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Thread safe future for callbacks.
 */
@InternalRpcApi
public class CallbackFuture<T : Any> {
    private sealed interface State<out T> {
        data class Pending<T>(val callbacks: List<(T) -> Unit> = emptyList()) : State<T>
        data class Done<T>(val value: T) : State<T>
    }

    private val state = atomic<State<T>>(State.Pending())

    public fun complete(result: T) {
        var toInvoke: List<(T) -> Unit>
        while (true) {
            when (val s = state.value) {
                is State.Pending -> if (state.compareAndSet(s, State.Done(result))) {
                    toInvoke = s.callbacks
                    break
                }

                is State.Done -> error("Already completed")
            }
        }
        for (cb in toInvoke) cb(result)
    }

    public fun onComplete(callback: (T) -> Unit) {
        while (true) {
            when (val s = state.value) {
                is State.Done -> {
                    callback(s.value); return
                }

                is State.Pending -> {
                    val next = State.Pending(s.callbacks + callback) // copy-on-write append
                    if (state.compareAndSet(s, next)) return
                }
            }
        }
    }

    public val isCompleted: Boolean get() = state.value is State.Done
}