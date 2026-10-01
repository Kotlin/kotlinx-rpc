/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.marshaller

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.internal.BufferEncodedMessage
import kotlinx.rpc.grpc.marshaller.internal.ByteArrayEncodedMessage
import kotlinx.rpc.internal.utils.ExperimentalRpcApi

/**
 * An encoded gRPC message whose exact [size] is known before it is written.
 *
 * A message is single-use. [writeTo] may be called once, potentially on a different thread from the one that
 * created the message.
 */
@ExperimentalRpcApi
public interface GrpcEncodedMessage {
    /** The exact number of bytes that [writeTo] writes. */
    public val size: Int

    /**
     * Writes exactly [size] bytes to [writer].
     *
     * The writer is valid only for the duration of this call.
     */
    public fun writeTo(writer: GrpcMessageWriter)

    /** Creates encoded messages backed by common in-memory representations. */
    public companion object {
        /**
         * Creates a message that takes ownership of [bytes].
         *
         * The array must not be modified after this call.
         */
        public fun of(bytes: ByteArray): GrpcEncodedMessage = ByteArrayEncodedMessage(bytes)

        /**
         * Creates a message that takes ownership of [buffer].
         *
         * The buffer must not be read from or written to after this call.
         */
        public fun of(buffer: Buffer): GrpcEncodedMessage = BufferEncodedMessage(buffer)
    }
}

