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
import kotlinx.rpc.protobuf.internal.bytes
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

public class AnyInternal: Any, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var typeUrl: String by MsgFieldDelegate { "" }
    public override var value: ByteArray by MsgFieldDelegate { byteArrayOf() }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = typeUrl.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AnyInternal
        other.checkRequiredFields()
        if (typeUrl != other.typeUrl) return false
        if (!value.contentEquals(other.value)) return false
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
            appendLine("Any(")
            appendLine("${nextIndentString}typeUrl=${typeUrl},")
            appendLine("${nextIndentString}value=${value.contentToString()},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: AnyInternal.() -> Unit): AnyInternal {
        val copy = AnyInternal()
        copy.typeUrl = this.typeUrl
        copy.value = this.value.copyOf()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<Any> {
        public override fun encode(value: Any, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): Any {
            WireDecoder(source).use {
                val msg = AnyInternal()
                checkForPlatformDecodeException {
                    AnyInternal.decodeWith(msg, it, config as? ProtobufConfig)
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
public fun AnyInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun AnyInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (typeUrl.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = typeUrl)
    }

    if (value.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 2, value = value)
    }
}

@InternalRpcApi
public fun AnyInternal.Companion.decodeWith(msg: AnyInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.typeUrl = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.value = decoder.readBytes()
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

private fun AnyInternal.computeSize(): Int {
    var __result = 0
    if (typeUrl.isNotEmpty()) {
        __result += WireSize.string(typeUrl).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (value.isNotEmpty()) {
        __result += WireSize.bytes(value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun Any.asInternal(): AnyInternal {
    return this as? AnyInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
