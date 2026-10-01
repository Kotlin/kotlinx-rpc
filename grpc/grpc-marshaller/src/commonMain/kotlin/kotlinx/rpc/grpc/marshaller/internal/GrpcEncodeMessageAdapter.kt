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
        check(!used) { "A GrpcEncodedMessage can only be written once" }
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
        check(buffer.size == size.toLong()) { "The owned Buffer was modified after the message was created" }
        writer.write(buffer, size.toLong())
    }
}

private fun Long.toMessageSize(): Int {
    require(this <= Int.MAX_VALUE) { "A gRPC message cannot be larger than Int.MAX_VALUE bytes: $this" }
    return toInt()
}
