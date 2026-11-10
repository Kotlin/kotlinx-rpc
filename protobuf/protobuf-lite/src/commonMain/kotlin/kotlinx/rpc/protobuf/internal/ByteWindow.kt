/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

/**
 * The memory of the segment that a [WireInput] or a [WireOutput] uses now.
 *
 * Index 0 is the first byte of the segment. The accessors don't check the indices, so the caller must.
 * A window has one segment at a time. [attach] replaces the segment, and [detach] removes it.
 */
internal expect class ByteWindow() {
    /** Uses the bytes of [array] from [offset]. `offset` must be less than `array.size`. */
    fun attach(array: ByteArray, offset: Int)

    /** Removes the segment. Calling it with no segment does nothing. */
    fun detach()

    inline fun getByte(index: Int): Byte

    inline fun setByte(index: Int, value: Byte)

    /** Copies [count] bytes from [index] to [destination] at [destinationOffset]. */
    inline fun copyTo(destination: ByteArray, destinationOffset: Int, index: Int, count: Int)

    /** Copies [count] bytes from [source] at [sourceOffset] to [index]. */
    inline fun copyFrom(source: ByteArray, sourceOffset: Int, index: Int, count: Int)
}
