/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readTo
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.internal.utils.InternalRpcApi

/** A [GrpcMessageWriter] that appends to an initially empty [Buffer]. */
@InternalRpcApi
public class BufferMessageWriter(
    public val buffer: Buffer,
    size: Int,
) : AbstractGrpcMessageWriter(size) {
    init {
        require(buffer.size == 0L) { "The destination Buffer must be empty" }
    }

    override fun writeByte(byte: Byte) {
        recordWrite(1) { buffer.writeByte(byte) }
    }

    override fun write(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val byteCount = checkedWriteRangeSize(bytes, startIndex, endIndex)
        recordWrite(byteCount) { buffer.write(bytes, startIndex, endIndex) }
    }

    override fun write(source: Buffer, byteCount: Long) {
        val count = checkedWriteByteCount(source, byteCount)
        recordWrite(count) { buffer.write(source, byteCount) }
    }

    /** Provides direct, exact-size access to the destination buffer. */
    @InternalRpcApi
    public fun writeDirect(writeAction: (Buffer) -> Unit) {
        val byteCount = remaining
        recordWrite(byteCount) {
            val sizeBefore = buffer.size
            writeAction(buffer)
            check(buffer.size - sizeBefore == byteCount.toLong()) {
                "BufferMessageWriter.writeDirect wrote ${buffer.size - sizeBefore} bytes, " +
                    "but expected exactly $byteCount bytes ($size declared bytes in total). " +
                    "Check the encoder used by GrpcEncodedMessage.writeTo and its size calculation."
            }
        }
    }
}

/** A [GrpcMessageWriter] backed by [array]. */
@InternalRpcApi
public class ByteArrayMessageWriter(
    public val array: ByteArray,
) : AbstractGrpcMessageWriter(array.size) {
    override fun writeByte(byte: Byte) {
        recordWrite(1) { array[written] = byte }
    }

    override fun write(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val byteCount = checkedWriteRangeSize(bytes, startIndex, endIndex)
        recordWrite(byteCount) {
            bytes.copyInto(array, destinationOffset = written, startIndex = startIndex, endIndex = endIndex)
        }
    }

    override fun write(source: Buffer, byteCount: Long) {
        val count = checkedWriteByteCount(source, byteCount)
        recordWrite(count) { source.readTo(array, written, written + count) }
    }

    /** Provides direct, exact-size access to the unwritten array range. */
    @InternalRpcApi
    public fun writeDirect(writeAction: (ByteArray, startIndex: Int, endIndex: Int) -> Unit) {
        val startIndex = written
        val byteCount = remaining
        recordWrite(byteCount) {
            writeAction(array, startIndex, startIndex + byteCount)
        }
    }
}

/** A [GrpcMessageReader] that takes ownership of [buffer]. */
@InternalRpcApi
public class BufferMessageReader(
    public val buffer: Buffer,
    size: Int,
) : AbstractGrpcMessageReader(size) {
    private var sourceProvided: Boolean = false

    init {
        require(buffer.size == size.toLong()) {
            "The Buffer size (${buffer.size}) does not match the message size ($size)"
        }
    }

    override fun readByte(): Byte = recordRead(1) { buffer.readByte() }

    override fun readTo(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val byteCount = checkedReadRangeSize(bytes, startIndex, endIndex)
        recordRead(byteCount) { buffer.readTo(bytes, startIndex, endIndex) }
    }

    override fun asSource(): Source {
        if (!sourceProvided) {
            recordRead(remaining) {}
            sourceProvided = true
        }
        return buffer
    }

    /** Provides direct access to the unread buffer for the duration of [readAction]. */
    @InternalRpcApi
    public fun <T> readDirect(readAction: (Buffer) -> T): T {
        val byteCount = remaining
        return recordRead(byteCount) { readAction(buffer) }
    }
}

/** A [GrpcMessageReader] backed by [array]. */
@InternalRpcApi
public class ByteArrayMessageReader(
    public val array: ByteArray,
) : AbstractGrpcMessageReader(array.size) {
    private var position: Int = 0
    private var source: Buffer? = null

    override fun readByte(): Byte = recordRead(1) { array[position++] }

    override fun readTo(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val byteCount = checkedReadRangeSize(bytes, startIndex, endIndex)
        recordRead(byteCount) {
            array.copyInto(
                destination = bytes,
                destinationOffset = startIndex,
                startIndex = position,
                endIndex = position + byteCount,
            )
            position += byteCount
        }
    }

    override fun asSource(): Source {
        source?.let { return it }

        val copied = Buffer()
        recordRead(remaining) {
            copied.write(array, position, array.size)
            position = array.size
        }
        source = copied
        return copied
    }

    /** Provides direct access to the unread array range for the duration of [readAction]. */
    @InternalRpcApi
    public fun <T> readDirect(
        readAction: (ByteArray, startIndex: Int, endIndex: Int) -> T,
    ): T {
        val startIndex = position
        val byteCount = remaining
        return recordRead(byteCount) {
            readAction(array, startIndex, startIndex + byteCount).also {
                position += byteCount
            }
        }
    }
}

private fun checkedReadRangeSize(bytes: ByteArray, startIndex: Int, endIndex: Int): Int {
    if (startIndex !in 0..endIndex || endIndex > bytes.size) {
        throw IndexOutOfBoundsException(
            "GrpcMessageReader.readTo received an invalid destination array range [$startIndex, $endIndex) " +
                "for an array of size ${bytes.size}. Check the indices passed by GrpcMarshaller.decode: " +
                "0 <= startIndex <= endIndex <= bytes.size; endIndex is exclusive.",
        )
    }
    return endIndex - startIndex
}
