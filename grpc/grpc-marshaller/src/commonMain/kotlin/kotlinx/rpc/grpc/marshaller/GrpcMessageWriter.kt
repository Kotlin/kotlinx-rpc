/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * An exact-size destination supplied by a gRPC runtime.
 *
 * Implementations reject writes beyond [size]. A writer is valid only while
 * [GrpcEncodedMessage.writeTo] is running.
 */
@ExperimentalRpcApi
public abstract class GrpcMessageWriter @InternalRpcApi constructor(
    /** The exact number of bytes expected by this writer. */
    public val size: Int,
) {
    private var state: WriterState
    private var writtenCount: Int = 0

    init {
        require(size >= 0) {
            "Cannot encode a gRPC message with size $size: GrpcEncodedMessage.size must be non-negative. " +
                "Check the message returned by GrpcMarshaller.prepare."
        }
        state = if (size == 0) WriterState.Complete else WriterState.Open
    }

    /** The number of bytes written so far. */
    public val written: Int
        get() = writtenCount

    /** The number of bytes that may still be written. */
    public val remaining: Int
        get() = size - writtenCount

    internal val isComplete: Boolean
        get() = state == WriterState.Complete

    internal val isFailed: Boolean
        get() = state == WriterState.Failed

    /** Writes one byte. */
    public abstract fun writeByte(byte: Byte)

    /** Writes the bytes in the range from [startIndex] (inclusive) to [endIndex] (exclusive). */
    public abstract fun write(bytes: ByteArray, startIndex: Int = 0, endIndex: Int = bytes.size)

    /**
     * Removes [byteCount] bytes from [source] and writes them to this writer.
     */
    public open fun write(source: Buffer, byteCount: Long = source.size) {
        val count = checkedWriteByteCount(source, byteCount)
        val bytes = try {
            source.readByteArray(count)
        } catch (cause: Throwable) {
            fail()
            throw cause
        }
        write(bytes)
    }

    /**
     * Records a write performed by an implementation and transitions the writer to failed if it throws.
     */
    @InternalRpcApi
    protected fun recordWrite(byteCount: Int, writeAction: () -> Unit) {
        if (byteCount == 0) {
            check(state != WriterState.Failed) {
                "GrpcEncodedMessage.writeTo attempted to use a writer after a previous write failed. " +
                    "Do not catch and ignore write failures."
            }
            writeActionOrFail(writeAction)
            return
        }

        if (state != WriterState.Open) {
            val wasFailed = state == WriterState.Failed
            fail()
            error(
                if (wasFailed) {
                    "GrpcEncodedMessage.writeTo attempted to use a writer after a previous write failed. " +
                        "Do not catch and ignore write failures."
                } else {
                    "GrpcEncodedMessage.writeTo attempted to write $byteCount more bytes after writing all $size " +
                        "declared bytes. Ensure GrpcEncodedMessage.size matches the total number of bytes written."
                },
            )
        }
        if (byteCount !in 0..remaining) {
            fail()
            throw IndexOutOfBoundsException(
                "GrpcEncodedMessage.writeTo attempted to write $byteCount bytes with only $remaining bytes remaining " +
                    "($written of $size declared bytes already written). " +
                    "Ensure GrpcEncodedMessage.size matches the total number of bytes written.",
            )
        }

        writeActionOrFail(writeAction)
        writtenCount += byteCount
        if (writtenCount == size) {
            state = WriterState.Complete
        }
    }

    /** Validates an array range and marks the writer failed when it is invalid. */
    @InternalRpcApi
    protected fun checkedWriteRangeSize(bytes: ByteArray, startIndex: Int, endIndex: Int): Int {
        if (startIndex !in 0..endIndex || endIndex > bytes.size) {
            fail()
            throw IndexOutOfBoundsException(
                "GrpcMessageWriter.write received an invalid byte array range [$startIndex, $endIndex) " +
                    "for an array of size ${bytes.size}.",
            )
        }

        val byteCount = endIndex - startIndex
        if (byteCount > remaining) {
            fail()
            throw IndexOutOfBoundsException(
                "GrpcEncodedMessage.writeTo attempted to write $byteCount bytes with only $remaining bytes remaining " +
                    "($written of $size declared bytes already written). " +
                    "Ensure GrpcEncodedMessage.size matches the total number of bytes written.",
            )
        }
        return byteCount
    }

    /** Validates a buffer write and marks the writer failed when it is invalid. */
    @InternalRpcApi
    protected fun checkedWriteByteCount(source: Buffer, byteCount: Long): Int {
        if (byteCount < 0L || byteCount > Int.MAX_VALUE || byteCount > source.size || byteCount > remaining.toLong()) {
            fail()
            throw IndexOutOfBoundsException(
                "GrpcMessageWriter.write received byteCount=$byteCount for a source Buffer of size ${source.size}, " +
                    "with $remaining bytes remaining ($written of $size declared bytes already written). " +
                    "Check the byteCount passed by GrpcEncodedMessage.writeTo: it must be non-negative, " +
                    "no larger than the source Buffer or writer.remaining, and fit in an Int. " +
                    "Ensure GrpcEncodedMessage.size matches the total number of bytes written.",
            )
        }
        return byteCount.toInt()
    }

    private fun writeActionOrFail(writeAction: () -> Unit) {
        try {
            writeAction()
        } catch (cause: Throwable) {
            fail()
            throw cause
        }
    }

    /** Marks the writer failed when an implementation rejects a scoped write. */
    @InternalRpcApi
    protected fun fail() {
        state = WriterState.Failed
    }

    private enum class WriterState {
        Open,
        Complete,
        Failed,
    }
}
