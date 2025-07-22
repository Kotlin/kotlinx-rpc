/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.IntVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.io.Buffer
import platform.posix.memcpy
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
class ZeroCopyInputSourceTest {


    @Test
    fun simpleTest() {


        val buffer = Buffer()
        fillWithChunks(buffer, 1000, 10)

        val zeroCopyInputSource = ZeroCopyInputSource(buffer)

        var i = 0
        var count = 0
        while (true) {
            val read = zeroCopyInputSource.nextIntoArray()
            println("$i reads: ${read.size}")
            if (read.isEmpty()) {
                break
            }
            if (read.size >= 10) {
                val toBackup = (read.size - 1) % ((i + 1) * 100)
                count -= toBackup
                zeroCopyInputSource.backUp( toBackup)
            }
            count += read.size
            i++
        }


        assertEquals(10000, zeroCopyInputSource.byteCount())
        assertEquals(10000, count)
    }

    private fun fillWithChunks(buffer: Buffer, numberOfChunks: Int, chunkSize: Int) {
        repeat(numberOfChunks) { i ->
            buffer.write(ByteArray(chunkSize) { i.toByte() })
        }

    }

}


@OptIn(ExperimentalForeignApi::class)
private fun ZeroCopyInputSource.nextIntoArray(): ByteArray = memScoped {
    val data = alloc<CPointerVar<ByteVar>>()
    val size = alloc<IntVar>()

    if (!next(data.ptr, size.ptr)) {
        return ByteArray(0)
    }

    val result = ByteArray(size.value)
    result.usePinned {
        memcpy(it.addressOf(0), data.value, size.value.toULong())
    }
    result
}