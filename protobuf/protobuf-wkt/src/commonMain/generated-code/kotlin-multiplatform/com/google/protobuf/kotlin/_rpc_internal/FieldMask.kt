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
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.bool
import kotlinx.rpc.protobuf.internal.bytes
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException
import kotlinx.rpc.protobuf.internal.double
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.float
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.int64
import kotlinx.rpc.protobuf.internal.protoToString
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag
import kotlinx.rpc.protobuf.internal.uInt32
import kotlinx.rpc.protobuf.internal.uInt64

public class FieldMaskInternal: FieldMask.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __pathsDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    public override var paths: List<String> by __pathsDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.paths.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FieldMaskInternal
        other.checkRequiredFields()
        if (this.paths != other.paths) return false
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
        builder.appendLine("FieldMask(")
        builder.appendLine("${nextIndentString}paths=${this.paths},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FieldMaskInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: FieldMaskInternal.() -> Unit): FieldMaskInternal {
        val copy = FieldMaskInternal()
        copy.paths = this.paths.map { it }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: GrpcMarshaller<FieldMask> {
        public override fun encode(value: FieldMask, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): FieldMask {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = FieldMaskInternal()
                checkForPlatformDecodeException {
                    FieldMaskInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FieldMask> {
        public override val fullName: String = "google.protobuf.FieldMask"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FieldMask by lazy { FieldMaskInternal() }
    }
}

@InternalRpcApi
public fun FieldMaskInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun FieldMaskInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.paths.isNotEmpty()) {
        this.paths.forEach {
            encoder.writeString(1, it)
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FieldMaskInternal.Companion.decodeWith(msg: FieldMaskInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__pathsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
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

private fun FieldMaskInternal.computeSize(): Int {
    var __result = 0
    if (this.paths.isNotEmpty()) {
        __result += this.paths.sumOf { WireSize.string(it).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun FieldMask.asInternal(): FieldMaskInternal {
    return this as? FieldMaskInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
