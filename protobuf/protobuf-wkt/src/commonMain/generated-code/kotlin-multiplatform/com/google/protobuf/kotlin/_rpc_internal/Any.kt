@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.marshaller.MarshallerConfig
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
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

public class AnyInternal: Any.Builder, InternalMessage(fieldsWithPresence = 0) {
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
        if (this.typeUrl != other.typeUrl) return false
        if (!this.value.contentEquals(other.value)) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("Any(")
        builder.appendLine("${nextIndentString}typeUrl=${this.typeUrl},")
        builder.appendLine("${nextIndentString}value=${this.value.contentToString()},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): AnyInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<Any> {
        public override fun encode(value: Any, config: MarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: MarshallerConfig?): Any {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = AnyInternal()
                checkForPlatformDecodeException {
                    AnyInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Any> {
        public override val fullName: String = "google.protobuf.Any"
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
    if (this.typeUrl.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.typeUrl)
    }

    if (this.value.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 2, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun AnyInternal.computeSize(): Int {
    var __result = 0
    if (this.typeUrl.isNotEmpty()) {
        __result += WireSize.string(this.typeUrl).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.value.isNotEmpty()) {
        __result += WireSize.bytes(this.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun Any.asInternal(): AnyInternal {
    return this as? AnyInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
