/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar

/**
 * The byte source of a [WireDecoderNative], implementing protobuf's C++ `ZeroCopyInputStream` contract.
 *
 * The C++ `CodedInputStream` calls [next], [backUp], [skip] and [byteCount] through C callbacks.
 * An exception thrown from them cannot unwind through the C++ frames and terminates the process.
 */
internal interface DecoderInput : AutoCloseable {
    /**
     * An upper bound of the unread bytes, which may include bytes already returned by [next].
     * Used to reject a length prefix before allocating for it, so it must never be lower than the real count.
     */
    val availableSize: Long

    /**
     * Stores the next chunk in [data] and [size] and consumes it; returns `false` at the end of the input.
     * The chunk stays valid until the next call to any method of this input.
     */
    fun next(data: CPointer<CPointerVar<ByteVar>>, size: CPointer<IntVar>): Boolean

    /**
     * Returns the last [count] bytes of the chunk from the directly preceding [next] call to the input.
     */
    fun backUp(count: Int)

    /**
     * Consumes [count] bytes; returns `false` if the input ends first.
     */
    fun skip(count: Int): Boolean

    /**
     * The total number of consumed bytes.
     */
    fun byteCount(): Long
}
