/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.pin
import kotlinx.cinterop.plus
import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

internal actual class ByteWindow actual constructor() {
    internal var base: CPointer<ByteVar>? = null
    private var pinned: Pinned<ByteArray>? = null

    actual fun attach(array: ByteArray, offset: Int) {
        detach()
        val pin = array.pin()
        pinned = pin
        base = pin.addressOf(offset)
    }

    /** Uses native memory from [pointer]. The memory must stay valid until [detach] or the next attach. */
    fun attach(pointer: CPointer<ByteVar>) {
        detach()
        base = pointer
    }

    actual fun detach() {
        val pin = pinned
        pinned = null
        base = null
        pin?.unpin()
    }

    actual inline fun getByte(index: Int): Byte = base!![index]

    actual inline fun setByte(index: Int, value: Byte) {
        base!![index] = value
    }

    actual inline fun copyTo(destination: ByteArray, destinationOffset: Int, index: Int, count: Int) {
        if (count > 0) {
            destination.usePinned { memcpy(it.addressOf(destinationOffset), base!! + index, count.convert()) }
        }
    }

    actual inline fun copyFrom(source: ByteArray, sourceOffset: Int, index: Int, count: Int) {
        if (count > 0) {
            source.usePinned { memcpy(base!! + index, it.addressOf(sourceOffset), count.convert()) }
        }
    }
}
