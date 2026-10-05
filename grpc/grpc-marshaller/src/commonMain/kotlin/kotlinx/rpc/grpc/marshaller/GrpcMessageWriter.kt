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
 * Implementations reject writes beyond [size].
 * A writer is valid only within the execution of [GrpcEncodedMessage.writeTo].
 */
@ExperimentalRpcApi
public interface GrpcMessageWriter {
    /**
     * The total number of bytes the writer expects.
     */
    public val size: Int

    /**
     * The number of bytes written so far.
     */
    public val written: Int

    /**
     * The number of bytes remaining to be written.
     */
    public val remaining: Int
        get() = size - written

    /** Writes one byte. */
    public fun writeByte(byte: Byte)

    /** Writes the bytes in the range from [startIndex] (inclusive) to [endIndex] (exclusive). */
    public fun write(bytes: ByteArray, startIndex: Int = 0, endIndex: Int = bytes.size)

    /**
     * Removes [byteCount] bytes from [source] and writes them to this writer.
     */
    public fun write(source: Buffer, byteCount: Long = source.size)
}
