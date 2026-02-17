@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.bool
import kotlinx.rpc.protobuf.internal.bytes
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.int64
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

public class TimestampInternal: Timestamp, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var seconds: Long by MsgFieldDelegate { 0L }
    public override var nanos: Int by MsgFieldDelegate { 0 }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = seconds.hashCode()
        result = 31 * result + nanos.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TimestampInternal
        other.checkRequiredFields()
        if (seconds != other.seconds) return false
        if (nanos != other.nanos) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("Timestamp(")
            appendLine("${nextIndentString}seconds=${seconds},")
            appendLine("${nextIndentString}nanos=${nanos},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: TimestampInternal.() -> Unit): TimestampInternal {
        val copy = TimestampInternal()
        copy.seconds = this.seconds
        copy.nanos = this.nanos
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<Timestamp> {
        public override fun encode(value: Timestamp, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: Source, config: CodecConfig?): Timestamp {
            WireDecoder(source).use {
                val msg = TimestampInternal()
                checkForPlatformDecodeException {
                    TimestampInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public companion object
}

@InternalRpcApi
public fun TimestampInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun TimestampInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (seconds != 0L) {
        encoder.writeInt64(fieldNr = 1, value = seconds)
    }

    if (nanos != 0) {
        encoder.writeInt32(fieldNr = 2, value = nanos)
    }
}

@InternalRpcApi
public fun TimestampInternal.Companion.decodeWith(msg: TimestampInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.seconds = decoder.readInt64()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.nanos = decoder.readInt32()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (config?.discardUnknownFields ?: false) {
                    decoder.skipUnknownField(tag)
                } else {
                    if (msg._unknownFieldsEncoder == null) {
                        msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                    }

                    decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
                }
            }
        }
    }
}

private fun TimestampInternal.computeSize(): Int {
    var __result = 0
    if (seconds != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(seconds))
    }

    if (nanos != 0) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int32(nanos))
    }

    return __result
}

@InternalRpcApi
public fun Timestamp.asInternal(): TimestampInternal {
    return this as? TimestampInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
