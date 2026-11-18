/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.internal.ByteWindow
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ByteWindowTest {
    @Test
    fun attachUsesOffset() {
        val array = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val window = ByteWindow()
        window.attach(array, 3)
        assertEquals(3, window.getByte(0).toInt())
        assertEquals(9, window.getByte(6).toInt())
    }

    @Test
    fun setByteWritesIntoArray() {
        val array = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val window = ByteWindow()
        window.attach(array, 2)
        window.setByte(1, 42)
        assertEquals(42, array[3].toInt())
    }

    @Test
    fun copyToAndCopyFrom() {
        val array = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        val window = ByteWindow()
        window.attach(array, 0)

        val destination = ByteArray(8)
        window.copyTo(destination, 2, 1, 4)
        assertContentEquals(byteArrayOf(0, 0, 1, 2, 3, 4, 0, 0), destination)

        array.fill(0)
        window.copyFrom(destination, 2, 1, 4)
        assertContentEquals(byteArrayOf(0, 1, 2, 3, 4, 0, 0, 0, 0, 0), array)

        // A copy of 0 bytes is valid when the destination offset is the destination size.
        window.copyTo(destination, destination.size, 0, 0)
        window.copyFrom(byteArrayOf(0), 1, 0, 0)
    }

    @Test
    fun attachReplacesSegment() {
        val window = ByteWindow()
        window.attach(byteArrayOf(0, 1, 2), 0)
        window.attach(byteArrayOf(7, 8, 9), 0)
        assertEquals(7, window.getByte(0).toInt())
    }

    @Test
    fun detachTwiceDoesNothing() {
        val window = ByteWindow()
        window.attach(byteArrayOf(1, 2, 3), 0)
        window.detach()
        window.detach()
    }
}
