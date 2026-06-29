@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
@file:Suppress("PropertyName", "CanBeVal", "ConstPropertyName", "LocalVariableName", "DuplicatedCode")

package com.google.protobuf.kotlin

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException

@InternalRpcApi
public class EmptyInternal: Empty.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override fun hashCode(): Int {
        var result = this::class.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EmptyInternal
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val builder = StringBuilder()
        builder.appendLine("Empty(")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): EmptyInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: EmptyInternal.() -> Unit): EmptyInternal {
        val copy = EmptyInternal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: GrpcMarshaller<Empty> {
        public override fun encode(value: Empty, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Empty {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = EmptyInternal()
                checkForPlatformDecodeException {
                    EmptyInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Empty> {
        public override val fullName: String = "google.protobuf.Empty"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Empty by lazy { EmptyInternal() }
    }
}

@InternalRpcApi
public fun EmptyInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    // no fields to encode
    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EmptyInternal.Companion.decodeWith(msg: EmptyInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
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

private fun EmptyInternal.computeSize(): Int {
    var __result = 0
    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun Empty.asInternal(): EmptyInternal {
    return this as? EmptyInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
