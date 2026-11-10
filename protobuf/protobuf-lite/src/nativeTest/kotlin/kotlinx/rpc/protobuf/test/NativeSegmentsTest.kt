/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.free
import kotlinx.cinterop.get
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.plus
import kotlinx.cinterop.set
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.ProtobufEncodingException
import kotlinx.rpc.protobuf.internal.ByteWindow
import kotlinx.rpc.protobuf.internal.InputSegments
import kotlinx.rpc.protobuf.internal.NativeInputSegments
import kotlinx.rpc.protobuf.internal.NativeOutputSegments
import kotlinx.rpc.protobuf.internal.UInt32
import kotlinx.rpc.protobuf.internal.UInt64
import kotlinx.rpc.protobuf.internal.WireInput
import kotlinx.rpc.protobuf.internal.WireOutput
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** The chunk sizes that each test uses. The small sizes make every slow path run. */
private val CHUNK_SIZES = listOf(1, 3, 7, 8192)

private const val STRING = "a😊b"

private const val GUARD: Byte = 0x5A

class NativeSegmentsTest {
    @Test
    fun inputOverNativeMemory() {
        val expected = sample()
        val memory = nativeHeap.allocArray<ByteVar>(expected.size)
        try {
            expected.forEachIndexed { i, byte -> memory[i] = byte }
            for (chunk in CHUNK_SIZES) {
                checkSample(WireInput(NativeChunkInputSegments(memory, expected.size, chunk), expected.size))
            }
            checkSample(WireInput(NativeInputSegments(memory, expected.size), expected.size))
        } finally {
            nativeHeap.free(memory)
        }
    }

    @Test
    fun inputStopsAtLimit() {
        val memory = nativeHeap.allocArray<ByteVar>(16)
        try {
            repeat(16) { memory[it] = 1 }
            for (chunk in CHUNK_SIZES) {
                WireInput(NativeChunkInputSegments(memory, 16, chunk), 16).use {
                    it.pushLimit(3)
                    assertFailsWith<ProtobufDecodingException> { it.readFixed32() }
                }
                WireInput(NativeChunkInputSegments(memory, 16, chunk), 16).use {
                    it.pushLimit(4)
                    assertEquals(0x01010101, it.readFixed32())
                    assertTrue(it.isAtLimit)
                }
            }
        } finally {
            nativeHeap.free(memory)
        }
    }

    @Test
    fun emptyInputThrows() {
        val memory = nativeHeap.allocArray<ByteVar>(1)
        try {
            WireInput(NativeInputSegments(memory, 0), 0).use {
                assertTrue(it.isAtLimit)
                assertFailsWith<ProtobufDecodingException> { it.readByte() }
            }
        } finally {
            nativeHeap.free(memory)
        }
    }

    @Test
    fun outputOverNativeMemory() {
        val expected = sample()
        GuardedMemory(expected.size).use { memory ->
            WireOutput(NativeOutputSegments(memory.pointer, expected.size)).use {
                writeSample(it)
                it.flush()
                assertEquals(expected.size, it.written)
            }
            assertContentEquals(expected, memory.content())
            memory.assertGuards()
        }
    }

    @Test
    fun outputContinuesAfterFlush() {
        GuardedMemory(6).use { memory ->
            WireOutput(NativeOutputSegments(memory.pointer, 6)).use {
                it.writeVarint(UInt32(300))
                it.flush()
                it.writeFixed32(0x01020304)
                it.flush()
            }
            assertContentEquals(bytes(0xAC, 0x02, 4, 3, 2, 1), memory.content())
            memory.assertGuards()
        }
    }

    @Test
    fun outputPastCapacityThrows() {
        GuardedMemory(3).use { memory ->
            WireOutput(NativeOutputSegments(memory.pointer, 3)).use {
                assertFailsWith<ProtobufEncodingException> { it.writeFixed32(0x01020304) }
            }
            memory.assertGuards()
        }
        GuardedMemory(2).use { memory ->
            WireOutput(NativeOutputSegments(memory.pointer, 2)).use {
                it.writeVarint(UInt32(300))
                assertFailsWith<ProtobufEncodingException> { it.writeByte(0) }
            }
            memory.assertGuards()
        }
    }

    /** A tag, two varints, two fixed values, a string, and 4 raw bytes. */
    private fun sample(): ByteArray {
        val fixed32 = ByteArray(4) { (0x01020304 ushr (8 * it)).toByte() }
        val fixed64 = ByteArray(8) { (0x0102030405060708L ushr (8 * it)).toByte() }
        return varintBytes(UInt32(8)) +
            varintBytes(UInt64(300L)) +
            varintBytes(UInt64(-1L)) +
            fixed32 +
            fixed64 +
            utf8Bytes(STRING) +
            bytes(1, 2, 3, 4)
    }

    private fun writeSample(output: WireOutput) {
        output.writeVarint(UInt32(8))
        output.writeVarint(UInt64(300L))
        output.writeVarint(UInt64(-1L))
        output.writeFixed32(0x01020304)
        output.writeFixed64(0x0102030405060708L)
        output.writeUtf8(STRING, utf8Bytes(STRING).size)
        output.writeBytes(bytes(1, 2, 3, 4), 0, 4)
    }

    private fun checkSample(input: WireInput) {
        input.use {
            assertEquals(8, it.readTagVarint())
            assertEquals(300L, it.readVarint64())
            assertEquals(-1L, it.readVarint64())
            assertEquals(0x01020304, it.readFixed32())
            assertEquals(0x0102030405060708L, it.readFixed64())
            assertEquals(STRING, it.readUtf8(utf8Bytes(STRING).size))
            assertContentEquals(bytes(1, 2, 3, 4), it.readBytes(4))
            assertTrue(it.isAtLimit)
        }
    }
}

/** Gives [size] bytes of native memory in segments of [chunkSize] bytes. */
private class NativeChunkInputSegments(
    private val pointer: CPointer<ByteVar>,
    private val size: Int,
    private val chunkSize: Int,
) : InputSegments {
    private var offset = 0

    override fun next(window: ByteWindow): Int {
        if (offset == size) {
            return -1
        }
        window.attach((pointer + offset)!!)
        val count = minOf(chunkSize, size - offset)
        offset += count
        return count
    }
}

/** Native memory of [capacity] bytes with a guard byte before and after it. */
private class GuardedMemory(private val capacity: Int) : AutoCloseable {
    private val allocation = nativeHeap.allocArray<ByteVar>(capacity + 2)

    val pointer: CPointer<ByteVar> = (allocation + 1)!!

    init {
        repeat(capacity + 2) { allocation[it] = 0 }
        allocation[0] = GUARD
        allocation[capacity + 1] = GUARD
    }

    fun content(): ByteArray = ByteArray(capacity) { allocation[it + 1] }

    fun assertGuards() {
        assertEquals(GUARD, allocation[0])
        assertEquals(GUARD, allocation[capacity + 1])
    }

    override fun close() {
        nativeHeap.free(allocation)
    }
}
