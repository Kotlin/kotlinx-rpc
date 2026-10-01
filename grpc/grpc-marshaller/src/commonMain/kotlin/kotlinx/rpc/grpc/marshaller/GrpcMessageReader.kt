/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Source
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * A bounded view of one received gRPC message, supplied by a gRPC runtime.
 *
 * A reader is valid only while [GrpcMarshaller.decode] is running.
 */
@ExperimentalRpcApi
public abstract class GrpcMessageReader @InternalRpcApi constructor(
    /** The total number of bytes in the message. */
    public val size: Int,
) {
    private var consumed: Int = 0

    init {
        require(size >= 0) { "size must be non-negative: $size" }
    }

    /** The number of bytes that may still be read. */
    public val remaining: Int
        get() = size - consumed

    /** Reads one byte. */
    public abstract fun readByte(): Byte

    /** Reads exactly `endIndex - startIndex` bytes into [bytes]. */
    public abstract fun readTo(bytes: ByteArray, startIndex: Int = 0, endIndex: Int = bytes.size)

    /** Reads exactly [byteCount] bytes into a new array. */
    public fun readByteArray(byteCount: Int = remaining): ByteArray {
        val result = ByteArray(byteCount)
        readTo(result)
        return result
    }

    /**
     * Returns a kotlinx-io view of the unread bytes.
     *
     * Buffer-backed readers share their buffer. Other implementations may copy the unread bytes.
     */
    public abstract fun asSource(): Source

    /** Records a read performed by an implementation after checking the reader bounds. */
    @InternalRpcApi
    protected fun <T> recordRead(byteCount: Int, readAction: () -> T): T {
        if (byteCount !in 0..remaining) {
            throw IndexOutOfBoundsException(
                "GrpcMarshaller.decode attempted to read $byteCount bytes with only $remaining bytes remaining " +
                    "in a message of size $size. Check the lengths and read order in your decoder. " +
                    "After calling GrpcMessageReader.asSource(), read from the returned Source instead of the reader.",
            )
        }
        val result = readAction()
        consumed += byteCount
        return result
    }
}
