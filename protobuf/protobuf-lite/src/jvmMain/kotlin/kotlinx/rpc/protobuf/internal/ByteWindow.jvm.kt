/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

internal actual class ByteWindow actual constructor() {
    internal var array: ByteArray = EMPTY
    internal var offset: Int = 0

    actual fun attach(array: ByteArray, offset: Int) {
        this.array = array
        this.offset = offset
    }

    actual fun detach() {
        array = EMPTY
        offset = 0
    }

    actual inline fun getByte(index: Int): Byte = array[offset + index]

    actual inline fun setByte(index: Int, value: Byte) {
        array[offset + index] = value
    }

    actual inline fun copyTo(destination: ByteArray, destinationOffset: Int, index: Int, count: Int) {
        array.copyInto(destination, destinationOffset, offset + index, offset + index + count)
    }

    actual inline fun copyFrom(source: ByteArray, sourceOffset: Int, index: Int, count: Int) {
        source.copyInto(array, offset + index, sourceOffset, sourceOffset + count)
    }

    private companion object {
        val EMPTY = ByteArray(0)
    }
}
