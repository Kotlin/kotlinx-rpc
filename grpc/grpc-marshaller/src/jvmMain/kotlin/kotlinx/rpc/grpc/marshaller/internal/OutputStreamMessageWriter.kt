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
                "The direct writer wrote ${countingOutput.written} bytes, expected $byteCount"
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
        check(written < size) { "The direct writer exceeded its size of $size bytes" }
        output.write(value)
        written++
    }

    override fun write(bytes: ByteArray, offset: Int, length: Int) {
        if (offset < 0 || length < 0 || offset > bytes.size - length) {
            throw IndexOutOfBoundsException("Invalid byte array range")
        }
        check(length <= size - written) { "The direct writer exceeded its size of $size bytes" }
        output.write(bytes, offset, length)
        written += length
    }

    override fun flush() {
        output.flush()
    }
}
