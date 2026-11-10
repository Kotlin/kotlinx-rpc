/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.free
import kotlinx.cinterop.get
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.set
import kotlinx.cinterop.usePinned
import kotlinx.rpc.protobuf.internal.ByteWindow
import platform.posix.memcpy
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ByteWindowNativeTest {
    @Test
    fun nativeMemoryWindow() {
        val memory = nativeHeap.allocArray<ByteVar>(8)
        try {
            repeat(8) { i -> memory[i] = i.toByte() }

            val window = ByteWindow()
            window.attach(memory)
            assertEquals(3, window.getByte(3).toInt())

            val destination = ByteArray(4)
            window.copyTo(destination, 0, 2, 4)
            assertContentEquals(byteArrayOf(2, 3, 4, 5), destination)

            window.setByte(5, 42)
            assertEquals(42, memory[5].toInt())

            window.copyFrom(byteArrayOf(9, 9, 9, 9), 0, 0, 4)
            assertContentEquals(byteArrayOf(9, 9, 9, 9, 4, 42, 6, 7), snapshot(memory, 8))

            // A copy of 0 bytes is valid when the destination offset is the destination size.
            window.copyTo(destination, destination.size, 0, 0)
            window.copyFrom(byteArrayOf(0), 1, 0, 0)
        } finally {
            nativeHeap.free(memory)
        }
    }

    private fun snapshot(memory: CPointer<ByteVar>, size: Int): ByteArray = ByteArray(size).also {
        it.usePinned { pinned -> memcpy(pinned.addressOf(0), memory, size.convert()) }
    }
}
