/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.internal.utils.thread

import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.internal.utils.map.RpcInternalConcurrentHashMap

/**
 * A cross-platform implementation of ThreadLocal for Kotlin Multiplatform.
 * This class provides thread-local variables, which are variables that are local to a thread.
 * Each thread that accesses a thread-local variable has its own, independently initialized copy of the variable.
 */
@InternalRpcApi
public class RpcInternalThreadLocal<T : Any> {
    private val map = RpcInternalConcurrentHashMap<Long, T>()

    /**
     * Returns the value of this thread-local variable for the current thread or null.
     */
    public fun get(): T? {
        val threadId = currentThreadId()
        return map[threadId]
    }

    /**
     * Sets the value of this thread-local variable for the current thread.
     */
    public fun set(value: T) {
        val threadId = currentThreadId()
        map[threadId] = value
    }

    /**
     * Removes the value of this thread-local variable for the current thread.
     */
    public fun remove() {
        val threadId = currentThreadId()
        map.remove(threadId)
    }
}

/**
 * Returns the current thread's ID.
 * This is a platform-specific function that must be implemented for each platform.
 */
internal expect fun currentThreadId(): Long
