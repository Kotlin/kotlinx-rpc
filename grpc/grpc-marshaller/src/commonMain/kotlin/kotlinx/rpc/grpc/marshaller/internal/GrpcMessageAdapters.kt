/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.rpc.internal.utils.InternalRpcApi::class)

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
) : GrpcMessageWriter(size) {
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
}

/** A [GrpcMessageWriter] backed by [array]. */
@InternalRpcApi
public class ByteArrayMessageWriter(
    public val array: ByteArray,
) : GrpcMessageWriter(array.size) {
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
}

/** A [GrpcMessageReader] that takes ownership of [buffer]. */
@InternalRpcApi
public class BufferMessageReader(
    public val buffer: Buffer,
    size: Int,
) : GrpcMessageReader(size) {
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
}

/** A [GrpcMessageReader] backed by [array]. */
@InternalRpcApi
public class ByteArrayMessageReader(
    public val array: ByteArray,
) : GrpcMessageReader(array.size) {
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
}

private fun checkedReadRangeSize(bytes: ByteArray, startIndex: Int, endIndex: Int): Int {
    if (startIndex !in 0..endIndex || endIndex > bytes.size) {
        throw IndexOutOfBoundsException(
            "Invalid byte array range [$startIndex, $endIndex) for size ${bytes.size}",
        )
    }
    return endIndex - startIndex
}
