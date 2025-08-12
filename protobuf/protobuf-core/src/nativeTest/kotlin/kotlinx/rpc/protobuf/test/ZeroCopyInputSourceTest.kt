/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

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
import kotlinx.rpc.protobuf.internal.ZeroCopyInputSource
import platform.posix.memcpy
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
class ZeroCopyInputSourceTest {

    @Test
    fun testEmptyBuffer() {
        val buffer = Buffer()
        val source = ZeroCopyInputSource(buffer)

        // next() should return false for empty buffer
        memScoped {
            val data = alloc<CPointerVar<ByteVar>>()
            val size = alloc<IntVar>()
            assertFalse(source.next(data.ptr, size.ptr))
        }

        // byteCount should be 0
        assertEquals(0, source.byteCount())

        // skip should return false for empty buffer
        assertFalse(source.skip(10))

        // close should work without errors
        source.close()
    }

    @Test
    fun testNextMethod() {
        val buffer = Buffer()
        val testData = ByteArray(100) { it.toByte() }
        buffer.write(testData)

        val source = ZeroCopyInputSource(buffer)

        // First next() call should return true and provide data
        val firstRead = source.nextIntoArray()
        assertEquals(100, firstRead.size)
        assertTrue(firstRead.contentEquals(testData))

        // Second next() call should return false (buffer exhausted)
        val secondRead = source.nextIntoArray()
        assertEquals(0, secondRead.size)

        // byteCount should reflect the bytes read
        assertEquals(100, source.byteCount())

        source.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testBackUpMethod() {
        val buffer = Buffer()
        val testData = ByteArray(100) { it.toByte() }
        buffer.write(testData)

        val source = ZeroCopyInputSource(buffer)

        // Read all data
        val firstRead = source.nextIntoArray()
        assertEquals(100, firstRead.size)

        // Back up 20 bytes
        source.backUp(20)

        // byteCount should be reduced by backup amount
        assertEquals(80, source.byteCount())

        // Next read should return the backed-up bytes
        val secondRead = source.nextIntoArray()
        assertEquals(20, secondRead.size)

        // Verify the backed-up bytes are correct (last 20 bytes of original data)
        for (i in 0 until 20) {
            assertEquals(testData[80 + i], secondRead[i])
        }

        // Buffer should be exhausted now
        val thirdRead = source.nextIntoArray()
        assertEquals(0, thirdRead.size)

        source.close()
        assertTrue(buffer.exhausted())
    }

    @Test
    fun testInvalidBackUpSequence() {
        val buffer = Buffer()
        buffer.write(ByteArray(10))
        val source = ZeroCopyInputSource(buffer)

        // Calling backUp() without a preceding next() should throw
        assertFailsWith<IllegalStateException> {
            source.backUp(5)
        }

        source.close()
    }

    @Test
    fun testSkipMethod() {
        val buffer = Buffer()
        val testData = ByteArray(100) { it.toByte() }
        buffer.write(testData)

        val source = ZeroCopyInputSource(buffer)

        // Skip 30 bytes
        assertTrue(source.skip(30))
        assertEquals(30, source.byteCount())

        // Reading all left segments
        val allLeftBytes = assertNBytesLeft(source, 70)

        // Verify we're reading from the correct position
        for (i in 0 until 70) {
            assertEquals(testData[30 + i], allLeftBytes.readByte())
        }

        assertEquals(100, source.byteCount())

        // Skip beyond the end should return false
        assertFalse(source.skip(10))

        source.close()
    }

    @Test
    fun testMultipleSegments() {
        val buffer = Buffer()
        // Create multiple segments by writing small chunks
        for (i in 0 until 100) {
            buffer.write(ByteArray(100) { (i * 100 + it).toByte() })
        }

        val source = ZeroCopyInputSource(buffer)
        val allData = mutableListOf<Byte>()

        // Read all segments
        var segmentCount = 0
        while (true) {
            val segment = source.nextIntoArray()
            if (segment.isEmpty()) break
            segmentCount++
            allData.addAll(segment.toList())
        }

        // assert there were more than 1 segment; otherwise the test is useless
        assertTrue(segmentCount > 1)

        // Verify we read all 100 bytes
        assertEquals(100 * 100, allData.size)
        assertEquals(100 * 100, source.byteCount())

        // Verify the data is correct
        for (i in 0 until 100 * 100) {
            assertEquals(i.toByte(), allData[i])
        }

        source.close()
    }

    @Test
    fun testCloseMethod() {
        val buffer = Buffer()
        val testData = ByteArray(100) { it.toByte() }
        buffer.write(testData)

        val source = ZeroCopyInputSource(buffer)

        // Read the data from source
        source.nextIntoArray()
        // Back up 20 bytes which have to be available in the original buffer after closing
        source.backUp(20)

        // Close the source
        source.close()

        // After closing, the buffer should be valid for reading
        assertFalse(buffer.exhausted())
        assertEquals(20, buffer.size)

        // Original buffer should contain last 20 bytes of test data
        for (i in 0 until 20) {
            assertEquals(testData[80 + i], buffer.readByte())
        }


        // But the source should not be usable
        assertFailsWith<IllegalStateException>(message = "ZeroCopyInputSource has already been closed.") {
            memScoped {
                val data = alloc<CPointerVar<ByteVar>>()
                val size = alloc<IntVar>()
                source.next(data.ptr, size.ptr)
                fail("Should not be able to use ZeroCopyInputSource after closing")
            }
        }
    }

    @Test
    fun testOutOfBoundsBackup() {
        val buffer = Buffer()
        val testData = ByteArray(100) { it.toByte() }
        buffer.write(testData)

        val source = ZeroCopyInputSource(buffer)

        // Read all data
        val read = source.nextIntoArray()
        assertEquals(100, read.size)

        // Try to back up more bytes than we read
        assertFailsWith<IllegalStateException>(message =
            "backUp() must not be called more than the number of bytes that were read in next()"
        ) { source.backUp(200) }

        source.close()
    }

    @Test
    fun testMultiChunkConsistency() {
        val buffer = Buffer()
        fillWithChunks(buffer, 1000, 10)
        val total = 1000L * 10

        val source = ZeroCopyInputSource(buffer)
        val seg1 = source.nextIntoArray()

        assertEquals(seg1.size.toLong(), source.byteCount())

        source.close()

        assertEquals(total - seg1.size, buffer.size)
    }

    @Test
    fun testMultiChunkBackupConsistency() {
        val buffer = Buffer()
        fillWithChunks(buffer, 1000, 10)
        val total = 1000L * 10

        val source = ZeroCopyInputSource(buffer)
        val seg1 = source.nextIntoArray()

        source.backUp(100)

        assertEquals(seg1.size.toLong() - 100, source.byteCount())

        source.close()

        assertEquals(total - seg1.size + 100, buffer.size)
    }

    @Test
    fun testMultiChunkBackup() {
        val buffer = Buffer()
        fillWithChunks(buffer, 1000, 10)
        val total = 1000L * 10

        val source = ZeroCopyInputSource(buffer)
        val seg1 = source.nextIntoArray()

        assertEquals(source.byteCount(), seg1.size.toLong())

        source.close()

        assertEquals(total - seg1.size, buffer.size)
    }

    private fun fillWithChunks(buffer: Buffer, numberOfChunks: Int, chunkSize: Int) {
        repeat(numberOfChunks) { i ->
            buffer.write(ByteArray(chunkSize) { i.toByte() })
        }
    }

}

private fun assertNBytesLeft(source: ZeroCopyInputSource, n: Long): Buffer {
    // Reading all left segments
    val combined = Buffer()
    while (combined.size < n) {
        val read = source.nextIntoArray()
        assertNotEquals(0, read.size)
        combined.write(read)
    }
    assertEquals(n, combined.size)
    return combined
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
