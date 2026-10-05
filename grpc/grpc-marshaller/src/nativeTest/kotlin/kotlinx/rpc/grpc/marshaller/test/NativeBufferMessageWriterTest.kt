/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    ExperimentalForeignApi::class,
    ExperimentalRpcApi::class,
    InternalRpcApi::class,
)

package kotlinx.rpc.grpc.marshaller.test

import kotlinx.cinterop.*
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageWriterOperations
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NativeBufferMessageWriterTest {
    @Test
    fun commitsOnlyReportedBytesAndChecksBounds() = withWriter(4) { writer ->
        var calls = 0
        assertEquals(2, UnsafeGrpcMessageWriterOperations.writeToTail(writer, 2) { pointer, start, end ->
            calls++
            assertEquals(0, start)
            assertEquals(4, end)
            pointer[start] = 1
            pointer[start + 1] = 2
            2
        })
        assertEquals(1, calls)
        assertEquals(2, writer.written)
        assertEquals(2, writer.remaining)

        assertEquals(2, UnsafeGrpcMessageWriterOperations.writeToTail(writer, 2) { pointer, start, end ->
            assertEquals(2, start)
            assertEquals(4, end)
            pointer[start] = 3
            pointer[start + 1] = 4
            2
        })
        assertTrue(writer.isComplete)
        assertContentEquals(byteArrayOf(1, 2, 3, 4), writer.snapshot())
        assertEquals(0, UnsafeGrpcMessageWriterOperations.writeToTail(writer, 0) { _, _, _ ->
            error("A complete writer must not expose a pointer")
        })
        assertEquals(2, writer.scopeCount)
        assertFailsWith<IllegalStateException> { writer.writeByte(5) }
        assertTrue(writer.isFailed)
    }

    @Test
    fun rejectsInsufficientCapacityBeforeOpeningScope() = withWriter(2) { writer ->
        assertFailsWith<IndexOutOfBoundsException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 3) { _, _, _ -> 0 }
        }
        assertEquals(0, writer.scopeCount)
        assertTrue(writer.isFailed)
    }

    @Test
    fun invalidReportedCountFailsWriter() {
        for (count in listOf(-1, 3)) {
            withWriter(2) { writer ->
                assertFailsWith<IllegalStateException> {
                    UnsafeGrpcMessageWriterOperations.writeToTail(writer, 0) { _, _, _ -> count }
                }
                assertEquals(0, writer.written)
                assertTrue(writer.isFailed)
                assertFailsWith<IllegalStateException> { writer.writeByte(1) }
            }
        }
    }

    @Test
    fun actionFailureIsRethrownOutsideBufferScope() = withWriter(1) { writer ->
        val failure = IllegalArgumentException("encode failed")
        val caught = assertFailsWith<IllegalArgumentException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 1) { _, _, _ -> throw failure }
        }
        assertTrue(caught === failure)
        assertFalse(writer.thrownThroughBuffer)
        assertFalse(writer.scopeOpen)
        assertTrue(writer.isFailed)
    }

    @Test
    fun safeWritesAndBufferSourceAreAtomic() = withWriter(5) { writer ->
        writer.writeByte(1)
        writer.write(byteArrayOf(9, 2, 3, 9), 1, 3)
        val source = Buffer().apply { write(byteArrayOf(4, 5)) }
        writer.write(source)
        assertEquals(0, source.size)
        assertTrue(writer.isComplete)
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), writer.snapshot())
    }

    @Test
    fun bufferWriteCopiesMultipleSegmentsAndConsumesOnlyRequestedBytes() = withWriter(20_000) { writer ->
        val expected = ByteArray(20_000) { it.toByte() }
        val source = Buffer().apply {
            write(expected)
            write(byteArrayOf(42, 43))
        }

        writer.write(source, expected.size.toLong())
        assertTrue(writer.isComplete)

        assertContentEquals(expected, writer.snapshot())
        assertContentEquals(byteArrayOf(42, 43), source.readByteArray())
    }

    @Test
    fun oversizedBufferWriteLeavesSourceUntouched() = withWriter(1) { writer ->
        val source = Buffer().apply { write(byteArrayOf(1, 2)) }
        assertFailsWith<IndexOutOfBoundsException> { writer.write(source) }
        assertContentEquals(byteArrayOf(1, 2), source.readByteArray())
        assertEquals(0, writer.written)
    }

    @Test
    fun failedBufferAccessLeavesBufferSourceUntouched() = withWriter(2) { writer ->
        writer.beforeAccess = { error("buffer unavailable") }
        val source = Buffer().apply { write(byteArrayOf(1, 2)) }
        assertFailsWith<IllegalStateException> { writer.write(source) }
        assertContentEquals(byteArrayOf(1, 2), source.readByteArray())
        assertEquals(0, writer.written)
        assertTrue(writer.isFailed)
    }

    @Test
    fun sourceSkipFailureFailsWriter() = withWriter(1) { writer ->
        val source = Buffer().apply { writeByte(1) }
        writer.beforeAccess = { source.readByteArray() }

        assertFailsWith<Exception> { writer.write(source) }
        assertTrue(writer.isFailed)
    }

    @Test
    fun emptyWriterNeedsNoBufferScope() = withWriter(0) { writer ->
        writer.write(byteArrayOf())
        writer.write(Buffer())
        assertEquals(0, writer.scopeCount)
        assertTrue(writer.isComplete)
    }

    private inline fun withWriter(size: Int, action: (HeapWriter) -> Unit) {
        val writer = HeapWriter(size)
        try {
            action(writer)
        } finally {
            writer.releaseStorage()
        }
    }
}

private class HeapWriter(size: Int) : NativeBufferMessageWriter(size) {
    private val storage: CPointer<ByteVar> = nativeHeap.allocArray(maxOf(size, 1))
    private var storageReleased: Boolean = false
    var scopeCount: Int = 0
    var scopeOpen: Boolean = false
    var thrownThroughBuffer: Boolean = false
    var beforeAccess: (() -> Unit)? = null

    override fun accessBuffer(block: (base: CPointer<ByteVar>?) -> Unit) {
        scopeCount++
        scopeOpen = true
        try {
            beforeAccess?.invoke()
            try {
                block(storage)
            } catch (cause: Throwable) {
                thrownThroughBuffer = true
                throw cause
            }
        } finally {
            scopeOpen = false
        }
    }

    fun releaseStorage() {
        if (storageReleased) return
        storageReleased = true
        nativeHeap.free(storage)
    }

    fun snapshot(): ByteArray = ByteArray(size) { storage[it] }
}
