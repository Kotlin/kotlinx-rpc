/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.descriptor

import io.grpc.Drainable
import io.grpc.KnownLength
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayMessageWriter
import kotlinx.rpc.grpc.marshaller.internal.OutputStreamMessageWriter
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.OutputStream

/** Defers encoding until grpc-java has checked the uncompressed message size. */
internal class EncodedMessageInputStream(
    private val message: GrpcEncodedMessage,
    private val marshallerName: String,
) : InputStream(), KnownLength, Drainable {
    private var materialized: ByteArrayInputStream? = null
    // Keeping the materialized bytes around allows us to efficiently transfer them
    // to the OutputStream in drainTo() without reading a copy from the materialized input stream.
    private var materializedBytes: ByteArray? = null
    private var drained: Boolean = false

    override fun available(): Int = if (drained) 0 else materialized?.available() ?: message.size

    override fun read(): Int = if (drained) -1 else bytes().read()

    override fun read(bytes: ByteArray, offset: Int, length: Int): Int {
        if (drained) {
            if (offset < 0 || length < 0 || offset > bytes.size - length) {
                throw IndexOutOfBoundsException("Marshaller $marshallerName read an invalid byte array range")
            }
            return if (length == 0) 0 else -1
        }
        return bytes().read(bytes, offset, length)
    }

    override fun drainTo(target: OutputStream): Int {
        if (drained) return 0
        materialized?.let { stream ->
            val remaining = stream.available()
            val bytes = checkNotNull(materializedBytes)
            target.write(bytes, bytes.size - remaining, remaining)
            drained = true
            return remaining
        }

        val writer = OutputStreamMessageWriter(target, message.size)
        writeMessage(writer)
        drained = true
        return message.size
    }

    private fun bytes(): ByteArrayInputStream {
        materialized?.let { return it }

        val bytes = ByteArray(message.size)
        writeMessage(ByteArrayMessageWriter(bytes))
        materializedBytes = bytes
        return ByteArrayInputStream(bytes).also { materialized = it }
    }

    private fun writeMessage(writer: GrpcMessageWriter) {
        try {
            message.writeTo(writer)
            check(writer.remaining == 0) {
                "Encoding of $message failed as only ${writer.written} of ${message.size} bytes were written"
            }
        } catch (cause: Throwable) {
            throw IllegalStateException("Failed to encode a message with $marshallerName", cause)
        }
    }
}
