/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.grpc.marshaller.test

import kotlinx.cinterop.*
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageReaderOperations
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NativeBufferMessageReaderTest {
    @Test
    fun commitsOnlyReportedBytesAndChecksBounds() = withReader(byteArrayOf(1, 2, 3, 4)) { reader ->
        var calls = 0
        assertEquals(2, UnsafeGrpcMessageReaderOperations.readFromHead(reader) { pointer, start, end ->
            calls++
            assertEquals(0, start)
            assertEquals(4, end)
            assertEquals(1, pointer[start])
            2
        })
        assertEquals(1, calls)
        assertEquals(2, reader.remaining)
        assertEquals(3, reader.readByte())
        assertContentEquals(byteArrayOf(4), reader.readByteArray())
        assertEquals(0, reader.remaining)
        assertFailsWith<IndexOutOfBoundsException> { reader.readByte() }
    }

    @Test
    fun invalidReportedCountsDoNotAdvanceReader() {
        for (count in listOf(-1, 3)) {
            withReader(byteArrayOf(1, 2)) { reader ->
                assertFailsWith<IllegalStateException> {
                    UnsafeGrpcMessageReaderOperations.readFromHead(reader) { _, _, _ -> count }
                }
                assertEquals(2, reader.remaining)
            }
        }
    }

    @Test
    fun actionExceptionIsRethrownOutsideBufferScope() = withReader(byteArrayOf(1)) { reader ->
        val failure = IllegalArgumentException("decode failed")
        val caught = assertFailsWith<IllegalArgumentException> {
            UnsafeGrpcMessageReaderOperations.readFromHead(reader) { _, _, _ -> throw failure }
        }
        assertTrue(caught === failure)
        assertFalse(reader.thrownThroughBuffer)
        assertFalse(reader.scopeOpen)
        assertEquals(1, reader.remaining)
    }

    @Test
    fun readsAfterCloseFail() = withReader(byteArrayOf(1, 2)) { reader ->
        reader.close()
        reader.close()
        assertFailsWith<IllegalStateException> { reader.readByte() }
        assertFailsWith<IllegalStateException> { reader.readTo(ByteArray(0)) }
        assertFailsWith<IllegalStateException> { reader.asSource() }
        assertFailsWith<IllegalStateException> {
            UnsafeGrpcMessageReaderOperations.readFromHead(reader) { _, _, _ -> 0 }
        }
    }

    @Test
    fun sourceCopiesOnlyRemainderOnce() = withReader(byteArrayOf(1, 2, 3)) { reader ->
        assertEquals(1, reader.readByte())
        val source = reader.asSource()
        reader.overwrite(2, 9)
        assertSame(source, reader.asSource())
        assertContentEquals(byteArrayOf(2, 3), source.readByteArray())
        assertEquals(0, reader.remaining)
        assertEquals(2, reader.scopeCount)
    }

    @Test
    fun sourceCopiesMultipleBufferSegments() {
        val bytes = ByteArray(20_000) { it.toByte() }
        withReader(bytes) { reader ->
            val source = reader.asSource()
            reader.overwrite(0, 42)
            assertContentEquals(bytes, source.readByteArray())
            assertEquals(1, reader.scopeCount)
        }
    }

    @Test
    fun readToChecksRangesAndCopiesOnlyRequestedBytes() = withReader(byteArrayOf(1, 2, 3)) { reader ->
        val target = byteArrayOf(9, 9, 9, 9)
        assertFailsWith<IndexOutOfBoundsException> { reader.readTo(target, -1, 2) }
        assertFailsWith<IndexOutOfBoundsException> { reader.readTo(target, 2, 5) }
        assertFailsWith<IndexOutOfBoundsException> { reader.readTo(target, 0, 4) }
        assertEquals(0, reader.scopeCount)
        reader.readTo(target, 1, 3)
        assertContentEquals(byteArrayOf(9, 1, 2, 9), target)
        assertContentEquals(byteArrayOf(3), reader.readByteArray())
    }

    @Test
    fun emptyMessageNeedsNoBufferScope() = withReader(byteArrayOf()) { reader ->
        assertContentEquals(byteArrayOf(), reader.readByteArray())
        assertEquals(0, UnsafeGrpcMessageReaderOperations.readFromHead(reader) { _, _, _ ->
            error("An empty buffer must not expose a pointer")
        })
        assertContentEquals(byteArrayOf(), reader.asSource().readByteArray())
        assertEquals(0, reader.scopeCount)
        assertFailsWith<IndexOutOfBoundsException> { reader.readByte() }
    }

    private inline fun withReader(bytes: ByteArray, action: (HeapReader) -> Unit) {
        val reader = HeapReader(bytes)
        try {
            action(reader)
        } finally {
            reader.close()
            reader.releaseStorage()
        }
    }
}

private class HeapReader(bytes: ByteArray) : NativeBufferMessageReader(bytes.size) {
    private val storage: CPointer<ByteVar> = nativeHeap.allocArray(maxOf(size, 1))
    var scopeCount: Int = 0
    var scopeOpen: Boolean = false
    var thrownThroughBuffer: Boolean = false

    init {
        for (index in bytes.indices) storage[index] = bytes[index]
    }

    override fun accessBuffer(block: (base: CPointer<ByteVar>?) -> Unit) {
        scopeCount++
        scopeOpen = true
        try {
            block(if (size == 0) null else storage)
        } catch (cause: Throwable) {
            thrownThroughBuffer = true
            throw cause
        } finally {
            scopeOpen = false
        }
    }

    fun overwrite(index: Int, byte: Byte) {
        storage[index] = byte
    }

    fun releaseStorage() {
        nativeHeap.free(storage)
    }
}
