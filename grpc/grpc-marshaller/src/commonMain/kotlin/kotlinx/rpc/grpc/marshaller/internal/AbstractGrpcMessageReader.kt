/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller.internal

import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.internal.utils.InternalRpcApi

@InternalRpcApi
public abstract class AbstractGrpcMessageReader(
    public override val size: Int,
) : GrpcMessageReader {
    private var consumed: Int = 0

    init {
        require(size >= 0) { "size must be non-negative: $size" }
    }

    public override val remaining: Int
        get() = size - consumed

    public override fun readByteArray(byteCount: Int): ByteArray {
        val result = ByteArray(byteCount)
        readTo(result)
        return result
    }

    /** Records a read performed by an implementation after checking the reader bounds. */
    @InternalRpcApi
    protected fun <T> recordRead(byteCount: Int, readAction: () -> T): T {
        if (byteCount !in 0..remaining) {
            throw IndexOutOfBoundsException(
                "GrpcMarshaller.decode attempted to read $byteCount bytes with only $remaining bytes remaining " +
                    "in a message of size $size. Check the lengths and read order in your decoder. ",
            )
        }
        val result = readAction()
        consumed += byteCount
        return result
    }
}
