/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)

package kotlinx.rpc.protobuf.internal

import kotlin.native.internal.NativePtr
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.interpretCPointer
import kotlinx.cinterop.interpretPointed
import kotlinx.cinterop.nativeNullPtr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import platform.posix.memcpy

/**
 * A window over a [ByteArray] or over native memory.
 *
 * An array segment needs no pinning, so each access is a plain array access.
 */
internal actual class ByteWindow actual constructor() {
    internal var array: ByteArray? = null
    internal var offset: Int = 0

    // A raw `NativePtr`, not a `CPointer`: `CPointer.plus` compares with `NativePtr.NULL`, which adds a global
    // initialization check and a call frame to each byte access.
    internal var address: NativePtr = nativeNullPtr

    actual fun attach(array: ByteArray, offset: Int) {
        this.array = array
        this.offset = offset
        address = nativeNullPtr
    }

    /** Uses native memory from [pointer]. The memory must stay valid until [detach] or the next attach. */
    fun attach(pointer: CPointer<ByteVar>) {
        array = null
        offset = 0
        address = pointer.rawValue
    }

    actual fun detach() {
        array = null
        offset = 0
        address = nativeNullPtr
    }

    actual inline fun getByte(index: Int): Byte {
        val array = array
        return if (array != null) {
            array[offset + index]
        } else {
            getNativeByte(index)
        }
    }

    actual inline fun setByte(index: Int, value: Byte) {
        val array = array
        if (array != null) {
            array[offset + index] = value
        } else {
            setNativeByte(index, value)
        }
    }

    actual inline fun copyTo(destination: ByteArray, destinationOffset: Int, index: Int, count: Int) {
        val array = array
        if (array != null) {
            array.copyInto(destination, destinationOffset, offset + index, offset + index + count)
        } else {
            copyNativeTo(destination, destinationOffset, index, count)
        }
    }

    actual inline fun copyFrom(source: ByteArray, sourceOffset: Int, index: Int, count: Int) {
        val array = array
        if (array != null) {
            source.copyInto(array, offset + index, sourceOffset, sourceOffset + count)
        } else {
            copyNativeFrom(source, sourceOffset, index, count)
        }
    }

    // A detached window has a null address, so an access after `detach` throws a NullPointerException.
    internal inline fun getNativeByte(index: Int): Byte = interpretPointed<ByteVar>(address + index.toLong()).value

    internal inline fun setNativeByte(index: Int, value: Byte) {
        interpretPointed<ByteVar>(address + index.toLong()).value = value
    }

    internal fun copyNativeTo(destination: ByteArray, destinationOffset: Int, index: Int, count: Int) {
        if (count > 0) {
            destination.usePinned { memcpy(it.addressOf(destinationOffset), nativePointer(index), count.convert()) }
        }
    }

    internal fun copyNativeFrom(source: ByteArray, sourceOffset: Int, index: Int, count: Int) {
        if (count > 0) {
            source.usePinned { memcpy(nativePointer(index), it.addressOf(sourceOffset), count.convert()) }
        }
    }

    private fun nativePointer(index: Int): CPointer<ByteVar> =
        checkNotNull(interpretCPointer<ByteVar>(address + index.toLong()))
}
