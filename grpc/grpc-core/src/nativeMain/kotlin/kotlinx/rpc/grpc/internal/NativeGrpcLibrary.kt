/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)

package kotlinx.rpc.grpc.internal

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.cinterop.ExperimentalForeignApi
import libkgrpc.grpc_init
import libkgrpc.grpc_shutdown
import kotlin.experimental.ExperimentalNativeApi

internal object GrpcRuntime {
    private val refLock = reentrantLock()
    private var refs = 0

    /** Acquire a runtime reference. Must be closed exactly once. */
    fun acquire(): AutoCloseable {
        refLock.withLock {
            val prev = refs++
            if (prev == 0) grpc_init()
        }
        return object : AutoCloseable {
            private val done = atomic(false)
            override fun close() {
                if (!done.compareAndSet(expect = false, update = true)) return
                refLock.withLock {
                    val now = --refs
                    require(now >= 0) { internalError("release() without matching acquire()") }
                    if (now == 0) {
                        grpc_shutdown()
                    }
                }
            }
        }
    }
}