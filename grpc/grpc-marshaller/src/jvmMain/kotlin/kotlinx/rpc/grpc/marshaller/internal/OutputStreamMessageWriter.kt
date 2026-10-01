/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(kotlinx.rpc.internal.utils.InternalRpcApi::class)

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.internal.utils.InternalRpcApi
import java.io.OutputStream

/** A bounded writer that sends a message directly to a JVM output stream. */
@InternalRpcApi
public class OutputStreamMessageWriter(
    private val output: OutputStream,
    size: Int,
) : GrpcMessageWriter(size) {
    override fun writeByte(byte: Byte) {
        recordWrite(1) { output.write(byte.toInt() and 0xff) }
    }

    override fun write(bytes: ByteArray, startIndex: Int, endIndex: Int) {
        val byteCount = checkedWriteRangeSize(bytes, startIndex, endIndex)
        recordWrite(byteCount) { output.write(bytes, startIndex, byteCount) }
    }

    /** Allows a buffered encoder to write directly to the output stream. */
    @InternalRpcApi
    public fun writeDirect(writeAction: (OutputStream) -> Unit) {
        val byteCount = remaining
        recordWrite(byteCount) {
            val countingOutput = CountingOutputStream(output, byteCount)
            writeAction(countingOutput)
            check(countingOutput.written == byteCount) {
                "OutputStreamMessageWriter.writeDirect wrote ${countingOutput.written} bytes, " +
                    "but expected exactly $byteCount bytes ($size declared bytes in total). " +
                    "Check the encoder used by GrpcEncodedMessage.writeTo and its size calculation; " +
                    "the callback must write exactly writer.remaining bytes and flush any buffered output."
            }
        }
    }
}

private class CountingOutputStream(
    private val output: OutputStream,
    private val size: Int,
) : OutputStream() {
    var written: Int = 0
        private set

    override fun write(value: Int) {
        check(written < size) {
            "OutputStreamMessageWriter.writeDirect attempted to write 1 more byte after writing all $size " +
                "expected bytes. Check the encoder used by GrpcEncodedMessage.writeTo and its size calculation."
        }
        output.write(value)
        written++
    }

    override fun write(bytes: ByteArray, offset: Int, length: Int) {
        if (offset < 0 || length < 0 || offset > bytes.size - length) {
            throw IndexOutOfBoundsException(
                "OutputStreamMessageWriter.writeDirect received offset=$offset and length=$length " +
                    "for an array of size ${bytes.size}. Check the encoder's OutputStream.write arguments: " +
                    "offset and length must be non-negative, and length must not exceed bytes.size - offset.",
            )
        }
        check(length <= size - written) {
            "OutputStreamMessageWriter.writeDirect attempted to write $length bytes with only ${size - written} " +
                "bytes remaining ($written of $size expected bytes already written). " +
                "Check the encoder used by GrpcEncodedMessage.writeTo and its size calculation."
        }
        output.write(bytes, offset, length)
        written += length
    }

    override fun flush() {
        output.flush()
    }
}
