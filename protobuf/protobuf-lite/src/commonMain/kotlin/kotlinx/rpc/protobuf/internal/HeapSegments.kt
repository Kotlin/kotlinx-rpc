/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.io.UnsafeIoApi::class)

package kotlinx.rpc.protobuf.internal

import kotlinx.io.Buffer
import kotlinx.io.Segment
import kotlinx.io.unsafe.BufferIterationContext
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.io.unsafe.withData
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.protobuf.ProtobufEncodingException

private const val CHUNK_SIZE: Int = 8192

/** The size that a caller of [BufferOutputSegments] passes when it doesn't know the message size in advance. */
internal const val UNKNOWN_SIZE: Int = -1

/** The exception for a write that goes past the end of a destination of [size] bytes. */
internal fun destinationFull(size: Int): ProtobufEncodingException =
    ProtobufEncodingException("The encoded protobuf message is larger than its destination of $size bytes.")

/** The input over the bytes of [array] from [start] to [end]. */
internal class ArrayInputSegments(
    private val array: ByteArray,
    private val start: Int,
    private val end: Int,
) : InputSegments {
    private var done = false

    override fun next(window: ByteWindow): Int {
        if (done || start == end) {
            done = true
            return -1
        }
        done = true
        window.attach(array, start)
        return end - start
    }
}

/**
 * The input over the segments of a kotlinx-io [Buffer].
 *
 * The class keeps [context] and the segment after `UnsafeBufferOperations.iterate` gave them. That is valid
 * because the decoder uses both only inside the `iterate` call (see `decodeWire`), and the buffer doesn't
 * change during the decode.
 */
internal class BufferInputSegments(
    private val context: BufferIterationContext,
    private var segment: Segment?,
) : InputSegments {
    override fun next(window: ByteWindow): Int {
        while (true) {
            val current = segment ?: return -1
            segment = context.next(current)
            var length = -1
            context.withData(current) { bytes, from, to ->
                if (to > from) {
                    window.attach(bytes, from)
                    length = to - from
                }
            }
            if (length > 0) {
                return length
            }
        }
    }
}

/**
 * The input over a [GrpcMessageReader] that gives no direct memory access, through one scratch array.
 *
 * The reader can come from outside the gRPC runtime, so each call checks `remaining`. If the reader breaks its
 * contract, the call throws [IllegalStateException].
 */
internal class ReaderInputSegments(private val reader: GrpcMessageReader) : InputSegments {
    private val scratch = ByteArray(minOf(checkedRemaining(), CHUNK_SIZE))

    override fun next(window: ByteWindow): Int {
        val remaining = checkedRemaining()
        val count = minOf(remaining, scratch.size)
        if (count == 0) {
            return -1
        }
        reader.readTo(scratch, 0, count)
        val after = reader.remaining
        check(after == remaining - count) {
            "${reader.typeName} reports $after remaining bytes after readTo read $count of $remaining bytes. " +
                "Make readTo reduce remaining by the number of bytes that it reads."
        }
        window.attach(scratch, 0)
        return count
    }

    private fun checkedRemaining(): Int {
        val remaining = reader.remaining
        check(remaining >= 0) {
            "${reader.typeName} reports $remaining remaining bytes. Make remaining return 0 or more."
        }
        return remaining
    }
}

/** The one fixed output segment of the bytes of [array] from [start] to [end]. */
internal class ArrayOutputSegments(
    private val array: ByteArray,
    private val start: Int,
    private val end: Int,
) : OutputSegments {
    private var committed = 0

    override fun next(window: ByteWindow): Int {
        if (committed == end - start) {
            throw destinationFull(end - start)
        }
        window.attach(array, start + committed)
        return end - start - committed
    }

    override fun commit(count: Int) {
        committed += count
    }
}

/**
 * The output that appends to a kotlinx-io [Buffer].
 *
 * [size] is the exact number of bytes the message has, or [UNKNOWN_SIZE] when the caller doesn't know it yet.
 * Each `next` makes a new array, so after a `commit` the codec never writes to the array again.
 */
internal class BufferOutputSegments(
    private val buffer: Buffer,
    private val size: Int,
) : OutputSegments {
    private var array: ByteArray = ByteArray(0)
    private var remaining: Int = size

    override fun next(window: ByteWindow): Int {
        val arraySize = if (size == UNKNOWN_SIZE) {
            // If the size is set unknown, the first segment starts with 256,
            // and the next ones doubles until CHUNK_SIZE.
            minOf(maxOf(array.size * 2, 256), CHUNK_SIZE)
        } else {
            if (remaining == 0) {
                throw destinationFull(size)
            }
            minOf(remaining, CHUNK_SIZE)
        }
        array = ByteArray(arraySize)
        window.attach(array, 0)
        return arraySize
    }

    override fun commit(count: Int) {
        if (count > 0) {
            UnsafeBufferOperations.moveToTail(buffer, array, 0, count)
            remaining -= count
        }
    }
}

/**
 * The output over a [GrpcMessageWriter] that takes no direct memory access, through one scratch array.
 *
 * The writer can come from outside the gRPC runtime, so each call checks `remaining`. If the writer breaks its
 * contract, the call throws [IllegalStateException].
 */
internal class WriterOutputSegments(private val writer: GrpcMessageWriter) : OutputSegments {
    private val scratch = ByteArray(minOf(checkedRemaining(), CHUNK_SIZE))

    override fun next(window: ByteWindow): Int {
        val remaining = checkedRemaining()
        if (remaining == 0) {
            throw destinationFull(writer.size)
        }
        window.attach(scratch, 0)
        return minOf(scratch.size, remaining)
    }

    override fun commit(count: Int) {
        if (count > 0) {
            val remaining = checkedRemaining()
            writer.write(scratch, 0, count)
            val after = writer.remaining
            check(after == remaining - count) {
                "${writer.typeName} reports $after remaining bytes after write took $count of $remaining bytes. " +
                    "Make write reduce remaining by the number of bytes that it writes."
            }
        }
    }

    private fun checkedRemaining(): Int {
        val remaining = writer.remaining
        check(remaining >= 0) {
            "${writer.typeName} reports $remaining remaining bytes. Make remaining return 0 or more."
        }
        return remaining
    }
}

private val GrpcMessageReader.typeName: String
    get() = this::class.simpleName ?: "GrpcMessageReader"

private val GrpcMessageWriter.typeName: String
    get() = this::class.simpleName ?: "GrpcMessageWriter"
