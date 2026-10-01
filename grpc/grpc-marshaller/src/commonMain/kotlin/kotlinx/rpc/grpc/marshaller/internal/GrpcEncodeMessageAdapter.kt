/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter

internal abstract class SingleUseGrpcEncodedMessage : GrpcEncodedMessage {
    private var used: Boolean = false

    final override fun writeTo(writer: GrpcMessageWriter) {
        check(!used) {
            "GrpcEncodedMessage.writeTo was called more than once on the same message. " +
                "GrpcMarshaller.prepare must return a fresh GrpcEncodedMessage for each call; " +
                "do not cache or reuse encoded messages."
        }
        used = true
        writeOnce(writer)
    }

    protected abstract fun writeOnce(writer: GrpcMessageWriter)
}

internal class ByteArrayEncodedMessage(
    private val bytes: ByteArray,
) : SingleUseGrpcEncodedMessage() {
    override val size: Int = bytes.size

    override fun writeOnce(writer: GrpcMessageWriter) {
        writer.write(bytes)
    }
}

internal class BufferEncodedMessage(
    private val buffer: Buffer,
) : SingleUseGrpcEncodedMessage() {
    override val size: Int = buffer.size.toMessageSize()

    override fun writeOnce(writer: GrpcMessageWriter) {
        check(buffer.size == size.toLong()) {
            "The Buffer passed to GrpcEncodedMessage.of changed size from $size to ${buffer.size} bytes " +
                "before writeTo. GrpcEncodedMessage.of takes ownership of the Buffer; " +
                "do not read from, write to, or clear it after creating the message."
        }
        writer.write(buffer, size.toLong())
    }
}

private fun Long.toMessageSize(): Int {
    require(this <= Int.MAX_VALUE) {
        "GrpcEncodedMessage.of received a Buffer of size $this bytes, exceeding the maximum gRPC message size " +
            "of ${Int.MAX_VALUE} bytes. Reduce the encoded message size."
    }
    return toInt()
}
