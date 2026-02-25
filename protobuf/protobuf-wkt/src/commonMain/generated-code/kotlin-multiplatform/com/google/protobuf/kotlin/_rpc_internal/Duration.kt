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
import kotlinx.rpc.protobuf.internal.bool
import kotlinx.rpc.protobuf.internal.bytes
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.int64
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

public class DurationInternal: Duration.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var seconds: Long by MsgFieldDelegate { 0L }
    public override var nanos: Int by MsgFieldDelegate { 0 }

    private val _owner: DurationInternal = this

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
        other as DurationInternal
        other.checkRequiredFields()
        if (this.seconds != other.seconds) return false
        if (this.nanos != other.nanos) return false
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
        builder.appendLine("Duration(")
        builder.appendLine("${nextIndentString}seconds=${this.seconds},")
        builder.appendLine("${nextIndentString}nanos=${this.nanos},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): DurationInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: DurationInternal.() -> Unit): DurationInternal {
        val copy = DurationInternal()
        copy.seconds = this.seconds
        copy.nanos = this.nanos
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<Duration> {
        public override fun encode(value: Duration, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Duration {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = DurationInternal()
                checkForPlatformDecodeException {
                    DurationInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Duration> {
        public override val fullName: String = "google.protobuf.Duration"
    }

    @InternalRpcApi
    public companion object
}

@InternalRpcApi
public fun DurationInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun DurationInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.seconds != 0L) {
        encoder.writeInt64(fieldNr = 1, value = this.seconds)
    }

    if (this.nanos != 0) {
        encoder.writeInt32(fieldNr = 2, value = this.nanos)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value))
        }
    }
}

@InternalRpcApi
public fun DurationInternal.Companion.decodeWith(msg: DurationInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(Duration::class) ?: emptyMap()
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

private fun DurationInternal.computeSize(): Int {
    var __result = 0
    if (this.seconds != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(this.seconds))
    }

    if (this.nanos != 0) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.nanos))
    }

    return __result
}

@InternalRpcApi
public fun Duration.asInternal(): DurationInternal {
    return this as? DurationInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
