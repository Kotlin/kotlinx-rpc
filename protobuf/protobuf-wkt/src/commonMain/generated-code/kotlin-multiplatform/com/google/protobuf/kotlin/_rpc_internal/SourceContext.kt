@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.isNotEmpty
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
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
import kotlinx.rpc.protobuf.internal.protoToString
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

public class SourceContextInternal: SourceContext.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __fileNameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var fileName: String by __fileNameDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.fileName.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SourceContextInternal
        other.checkRequiredFields()
        if (this.fileName != other.fileName) return false
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
        builder.appendLine("SourceContext(")
        builder.appendLine("${nextIndentString}fileName=${this.fileName},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): SourceContextInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: SourceContextInternal.() -> Unit): SourceContextInternal {
        val copy = SourceContextInternal()
        copy.fileName = this.fileName
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: GrpcMarshaller<SourceContext> {
        public override fun encode(value: SourceContext, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): SourceContext {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = SourceContextInternal()
                checkForPlatformDecodeException {
                    SourceContextInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<SourceContext> {
        public override val fullName: String = "google.protobuf.SourceContext"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: SourceContext by lazy { SourceContextInternal() }
    }
}

@InternalRpcApi
public fun SourceContextInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun SourceContextInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.fileName.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.fileName)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun SourceContextInternal.Companion.decodeWith(msg: SourceContextInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.fileName = decoder.readString()
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

private fun SourceContextInternal.computeSize(): Int {
    var __result = 0
    if (this.fileName.isNotEmpty()) {
        __result += WireSize.string(this.fileName).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun SourceContext.asInternal(): SourceContextInternal {
    return this as? SourceContextInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
