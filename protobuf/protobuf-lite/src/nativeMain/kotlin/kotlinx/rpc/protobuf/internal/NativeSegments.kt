/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.NativePtr
import kotlinx.cinterop.plus

/**
 * The input over [length] bytes of native memory at [pointer].
 *
 * The memory must stay valid while the [WireInput] uses it.
 */
internal class NativeInputSegments(
    private val pointer: CPointer<ByteVar>,
    private val length: Int,
) : InputSegments {
    private var done = false

    override fun next(window: ByteWindow): Int {
        if (done || length == 0) {
            done = true
            return -1
        }
        done = true
        window.attach(pointer)
        return length
    }
}

/**
 * The one fixed output segment of [capacity] bytes of native memory at [pointer].
 *
 * The memory must stay valid while the [WireOutput] uses it.
 */
internal class NativeOutputSegments(
    private val pointer: CPointer<ByteVar>,
    private val capacity: Int,
) : OutputSegments {
    private var committed = 0

    override fun next(window: ByteWindow): Int {
        if (committed == capacity) {
            throw destinationFull(capacity)
        }
        window.attach((pointer + committed)!!)
        return capacity - committed
    }

    override fun commit(count: Int) {
        committed += count
    }
}
