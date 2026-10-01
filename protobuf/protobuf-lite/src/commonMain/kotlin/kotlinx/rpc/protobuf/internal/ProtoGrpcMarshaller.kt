/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.internal

import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.GrpcMessageWriter
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig

/** Size-first gRPC marshaller shared by generated protobuf message marshallers. */
@InternalRpcApi
public abstract class ProtoGrpcMarshaller<T : Any, I : InternalMessage> : GrpcMarshaller<T> {

    /** Returns the generated internal representation of [value]. */
    protected abstract fun asInternal(value: T): I

    /** Creates an empty generated internal representation for decoding. */
    protected abstract fun newInternal(): I

    /** Encodes [message]. */
    protected abstract fun encodeWith(message: I, encoder: WireEncoder, config: ProtoConfig?)

    /** Decodes into [message]. */
    protected abstract fun decodeWith(message: I, decoder: WireDecoder, config: ProtoConfig?)

    final override fun prepare(value: T, config: GrpcMarshallerConfig?): GrpcEncodedMessage {
        val message = asInternal(value)
        val protoConfig = config as? ProtoConfig
        return ProtoEncodedMessage(message._size) { writer ->
            checkForPlatformEncodeException {
                withWireEncoder(writer) { encoder ->
                    encodeWith(message, encoder, protoConfig)
                }
            }
        }
    }

    final override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): T {
        val message = newInternal()
        val protoConfig = config as? ProtoConfig
        withWireDecoder(reader) { decoder ->
            protoConfig?.let { decoder.recursionLimit = it.recursionLimit }
            checkForPlatformDecodeException {
                decodeWith(message, decoder, protoConfig)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return message as T
    }
}

private class ProtoEncodedMessage(
    override val size: Int,
    private val encode: (GrpcMessageWriter) -> Unit,
) : GrpcEncodedMessage {
    private var used: Boolean = false

    override fun writeTo(writer: GrpcMessageWriter) {
        check(!used) {
            "GrpcEncodedMessage.writeTo was called more than once on the same protobuf message. " +
                "GrpcMarshaller.prepare must return a fresh GrpcEncodedMessage for each call; " +
                "do not cache or reuse encoded messages."
        }
        used = true
        require(writer.remaining == size) {
            "The writer has ${writer.remaining} bytes remaining, but the protobuf message size is $size"
        }
        encode(writer)
    }
}

/** Runs [block] with a platform wire encoder that writes to [writer]. */
@InternalRpcApi
public expect inline fun withWireEncoder(
    writer: GrpcMessageWriter,
    crossinline block: (WireEncoder) -> Unit,
)

/** Runs [block] with a platform wire decoder that reads from [reader]. */
@InternalRpcApi
public expect inline fun <R> withWireDecoder(
    reader: GrpcMessageReader,
    crossinline block: (WireDecoder) -> R,
): R
