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
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.internal.NativeRegionMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.UnsafeGrpcMessageWriterOperations
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NativeRegionMessageWriterTest {
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
        writer.seal()
        assertContentEquals(byteArrayOf(1, 2, 3, 4), writer.snapshot())
        assertFailsWith<IllegalStateException> { writer.seal() }
        assertFailsWith<IllegalStateException> { writer.writeByte(5) }
        assertFailsWith<IllegalStateException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 0) { _, _, _ -> 0 }
        }
    }

    @Test
    fun rejectsInsufficientCapacityBeforeOpeningScope() = withWriter(2) { writer ->
        assertFailsWith<IndexOutOfBoundsException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 3) { _, _, _ -> 0 }
        }
        assertEquals(0, writer.scopeCount)
        assertFailsWith<IllegalStateException> { writer.seal() }
    }

    @Test
    fun invalidReportedCountFailsWriter() {
        for (count in listOf(-1, 3)) {
            withWriter(2) { writer ->
                assertFailsWith<IllegalStateException> {
                    UnsafeGrpcMessageWriterOperations.writeToTail(writer, 0) { _, _, _ -> count }
                }
                assertEquals(0, writer.written)
                assertFailsWith<IllegalStateException> { writer.seal() }
                assertFailsWith<IllegalStateException> { writer.writeByte(1) }
            }
        }
    }

    @Test
    fun actionFailureIsRethrownOutsideStorageScope() = withWriter(1) { writer ->
        val failure = IllegalArgumentException("encode failed")
        val caught = assertFailsWith<IllegalArgumentException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 1) { _, _, _ -> throw failure }
        }
        assertTrue(caught === failure)
        assertFalse(writer.scopeOpen)
        assertFailsWith<IllegalStateException> { writer.seal() }
    }

    @Test
    fun nestedWriteFailsEvenIfActionCatchesIt() = withWriter(1) { writer ->
        assertFailsWith<IllegalStateException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 1) { _, _, _ ->
                assertFailsWith<IllegalStateException> { writer.writeByte(1) }
                1
            }
        }
        assertEquals(0, writer.written)
        assertFalse(writer.scopeOpen)
        assertFailsWith<IllegalStateException> { writer.seal() }
    }

    @Test
    fun nestedDiscardDoesNotFreeStorageInsideScope() = withWriter(1) { writer ->
        assertFailsWith<IllegalStateException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 1) { _, _, _ ->
                assertFailsWith<IllegalStateException> { writer.discard() }
                assertEquals(0, writer.discardCount)
                1
            }
        }
        assertEquals(0, writer.discardCount)
    }

    @Test
    fun safeWritesAndBufferSourceAreAtomic() = withWriter(5) { writer ->
        writer.writeByte(1)
        writer.write(byteArrayOf(9, 2, 3, 9), 1, 3)
        val source = Buffer().apply { write(byteArrayOf(4, 5)) }
        writer.write(source)
        assertEquals(0, source.size)
        writer.seal()
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), writer.snapshot())
    }

    @Test
    fun oversizedBufferWriteLeavesSourceUntouched() = withWriter(1) { writer ->
        val source = Buffer().apply { write(byteArrayOf(1, 2)) }
        assertFailsWith<IndexOutOfBoundsException> { writer.write(source) }
        assertContentEquals(byteArrayOf(1, 2), source.readByteArray())
        assertEquals(0, writer.written)
    }

    @Test
    fun rejectedScopeLeavesBufferSourceUntouched() = withWriter(2) { writer ->
        writer.rejectScope = true
        val source = Buffer().apply { write(byteArrayOf(1, 2)) }
        assertFailsWith<IllegalStateException> { writer.write(source) }
        assertContentEquals(byteArrayOf(1, 2), source.readByteArray())
        assertEquals(0, writer.written)
    }

    @Test
    fun sourceSkipFailurePreventsSealing() = withWriter(1) { writer ->
        val source = Buffer().apply { writeByte(1) }
        writer.beforeAccess = { source.readByteArray() }

        assertFailsWith<Exception> { writer.write(source) }
        assertFailsWith<IllegalStateException> { writer.seal() }
    }

    @Test
    fun storageCannotInvokeActionTwice() = withWriter(1) { writer ->
        writer.invokeTwice = true
        var calls = 0
        assertFailsWith<IllegalStateException> {
            UnsafeGrpcMessageWriterOperations.writeToTail(writer, 1) { _, _, _ ->
                calls++
                0
            }
        }
        assertEquals(1, calls)
        assertEquals(0, writer.written)
    }

    @Test
    fun incompleteStorageCannotBeSealed() = withWriter(2) { writer ->
        writer.writeByte(1)
        assertFailsWith<IllegalStateException> { writer.seal() }
        writer.writeByte(2)
        writer.seal()
        assertTrue(writer.sealed)
    }

    @Test
    fun emptyStorageNeedsNoPointerScope() = withWriter(0) { writer ->
        writer.write(byteArrayOf())
        writer.write(Buffer())
        writer.seal()
        assertEquals(0, writer.scopeCount)
        assertTrue(writer.sealed)
    }

    @Test
    fun discardIsIdempotentAndPreventsFurtherAccess() = withWriter(1) { writer ->
        writer.discard()
        writer.discard()
        assertEquals(1, writer.discardCount)
        assertFailsWith<IllegalStateException> { writer.writeByte(1) }
        assertFailsWith<IllegalStateException> { writer.seal() }
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

private class HeapWriter(size: Int) : NativeRegionMessageWriter(size) {
    private val storage: CPointer<ByteVar> = nativeHeap.allocArray(maxOf(size, 1))
    private var storageReleased: Boolean = false
    var scopeCount: Int = 0
    var scopeOpen: Boolean = false
    var rejectScope: Boolean = false
    var beforeAccess: (() -> Unit)? = null
    var invokeTwice: Boolean = false
    var sealed: Boolean = false
    var discardCount: Int = 0

    override fun accessStorage(block: (base: CPointer<ByteVar>?) -> Boolean): Boolean {
        scopeCount++
        scopeOpen = true
        try {
            beforeAccess?.invoke()
            if (rejectScope) return false
            val accepted = block(storage)
            if (invokeTwice) block(storage)
            return accepted
        } finally {
            scopeOpen = false
        }
    }

    override fun onSeal() {
        sealed = true
    }

    override fun onDiscard() {
        discardCount++
        releaseStorage()
    }

    fun releaseStorage() {
        if (storageReleased) return
        storageReleased = true
        nativeHeap.free(storage)
    }

    fun snapshot(): ByteArray = ByteArray(size) { storage[it] }
}
