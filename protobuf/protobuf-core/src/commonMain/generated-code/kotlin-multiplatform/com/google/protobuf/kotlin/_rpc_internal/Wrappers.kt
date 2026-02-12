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
import kotlinx.rpc.protobuf.internal.double
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.float
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.int64
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag
import kotlinx.rpc.protobuf.internal.uInt32
import kotlinx.rpc.protobuf.internal.uInt64

public class DoubleValueInternal: DoubleValue, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.DoubleValue> get() = DESCRIPTOR

    public override var value: Double by MsgFieldDelegate { 0.0 }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DoubleValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("DoubleValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: DoubleValueInternal.() -> Unit): DoubleValueInternal {
        val copy = DoubleValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<DoubleValue> {
        public override fun encode(value: DoubleValue, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): DoubleValue {
            WireDecoder(source).use {
                val msg = DoubleValueInternal()
                checkForPlatformDecodeException {
                    DoubleValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<DoubleValue> {
        public override val fullName: String = "google.protobuf.DoubleValue"
    }

    @InternalRpcApi
    public companion object
}

public class FloatValueInternal: FloatValue, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.FloatValue> get() = DESCRIPTOR

    public override var value: Float by MsgFieldDelegate { 0.0f }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FloatValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("FloatValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: FloatValueInternal.() -> Unit): FloatValueInternal {
        val copy = FloatValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<FloatValue> {
        public override fun encode(value: FloatValue, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): FloatValue {
            WireDecoder(source).use {
                val msg = FloatValueInternal()
                checkForPlatformDecodeException {
                    FloatValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FloatValue> {
        public override val fullName: String = "google.protobuf.FloatValue"
    }

    @InternalRpcApi
    public companion object
}

public class Int64ValueInternal: Int64Value, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.Int64Value> get() = DESCRIPTOR

    public override var value: Long by MsgFieldDelegate { 0L }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Int64ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("Int64Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: Int64ValueInternal.() -> Unit): Int64ValueInternal {
        val copy = Int64ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<Int64Value> {
        public override fun encode(value: Int64Value, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): Int64Value {
            WireDecoder(source).use {
                val msg = Int64ValueInternal()
                checkForPlatformDecodeException {
                    Int64ValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Int64Value> {
        public override val fullName: String = "google.protobuf.Int64Value"
    }

    @InternalRpcApi
    public companion object
}

public class UInt64ValueInternal: UInt64Value, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.UInt64Value> get() = DESCRIPTOR

    public override var value: ULong by MsgFieldDelegate { 0uL }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UInt64ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("UInt64Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: UInt64ValueInternal.() -> Unit): UInt64ValueInternal {
        val copy = UInt64ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<UInt64Value> {
        public override fun encode(value: UInt64Value, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): UInt64Value {
            WireDecoder(source).use {
                val msg = UInt64ValueInternal()
                checkForPlatformDecodeException {
                    UInt64ValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<UInt64Value> {
        public override val fullName: String = "google.protobuf.UInt64Value"
    }

    @InternalRpcApi
    public companion object
}

public class Int32ValueInternal: Int32Value, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.Int32Value> get() = DESCRIPTOR

    public override var value: Int by MsgFieldDelegate { 0 }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Int32ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("Int32Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: Int32ValueInternal.() -> Unit): Int32ValueInternal {
        val copy = Int32ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<Int32Value> {
        public override fun encode(value: Int32Value, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): Int32Value {
            WireDecoder(source).use {
                val msg = Int32ValueInternal()
                checkForPlatformDecodeException {
                    Int32ValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Int32Value> {
        public override val fullName: String = "google.protobuf.Int32Value"
    }

    @InternalRpcApi
    public companion object
}

public class UInt32ValueInternal: UInt32Value, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.UInt32Value> get() = DESCRIPTOR

    public override var value: UInt by MsgFieldDelegate { 0u }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UInt32ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("UInt32Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: UInt32ValueInternal.() -> Unit): UInt32ValueInternal {
        val copy = UInt32ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<UInt32Value> {
        public override fun encode(value: UInt32Value, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): UInt32Value {
            WireDecoder(source).use {
                val msg = UInt32ValueInternal()
                checkForPlatformDecodeException {
                    UInt32ValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<UInt32Value> {
        public override val fullName: String = "google.protobuf.UInt32Value"
    }

    @InternalRpcApi
    public companion object
}

public class BoolValueInternal: BoolValue, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.BoolValue> get() = DESCRIPTOR

    public override var value: Boolean by MsgFieldDelegate { false }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BoolValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("BoolValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: BoolValueInternal.() -> Unit): BoolValueInternal {
        val copy = BoolValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<BoolValue> {
        public override fun encode(value: BoolValue, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): BoolValue {
            WireDecoder(source).use {
                val msg = BoolValueInternal()
                checkForPlatformDecodeException {
                    BoolValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<BoolValue> {
        public override val fullName: String = "google.protobuf.BoolValue"
    }

    @InternalRpcApi
    public companion object
}

public class StringValueInternal: StringValue, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.StringValue> get() = DESCRIPTOR

    public override var value: String by MsgFieldDelegate { "" }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StringValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("StringValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: StringValueInternal.() -> Unit): StringValueInternal {
        val copy = StringValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<StringValue> {
        public override fun encode(value: StringValue, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): StringValue {
            WireDecoder(source).use {
                val msg = StringValueInternal()
                checkForPlatformDecodeException {
                    StringValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<StringValue> {
        public override val fullName: String = "google.protobuf.StringValue"
    }

    @InternalRpcApi
    public companion object
}

public class BytesValueInternal: BytesValue, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.BytesValue> get() = DESCRIPTOR

    public override var value: ByteArray by MsgFieldDelegate { byteArrayOf() }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return value.contentHashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BytesValueInternal
        other.checkRequiredFields()
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
            appendLine("BytesValue(")
            appendLine("${nextIndentString}value=${value.contentToString()},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: BytesValueInternal.() -> Unit): BytesValueInternal {
        val copy = BytesValueInternal()
        copy.value = this.value.copyOf()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<BytesValue> {
        public override fun encode(value: BytesValue, config: CodecConfig?): Source {
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

        public override fun decode(source: Source, config: CodecConfig?): BytesValue {
            WireDecoder(source).use {
                val msg = BytesValueInternal()
                checkForPlatformDecodeException {
                    BytesValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<BytesValue> {
        public override val fullName: String = "google.protobuf.BytesValue"
    }

    @InternalRpcApi
    public companion object
}

@InternalRpcApi
public fun DoubleValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun DoubleValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value != 0.0) {
        encoder.writeDouble(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun DoubleValueInternal.Companion.decodeWith(msg: DoubleValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.FIXED64 -> {
                msg.value = decoder.readDouble()
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

private fun DoubleValueInternal.computeSize(): Int {
    var __result = 0
    if (value != 0.0) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.double(value))
    }

    return __result
}

@InternalRpcApi
public fun DoubleValue.asInternal(): DoubleValueInternal {
    return this as? DoubleValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FloatValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun FloatValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value != 0.0f) {
        encoder.writeFloat(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun FloatValueInternal.Companion.decodeWith(msg: FloatValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.FIXED32 -> {
                msg.value = decoder.readFloat()
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

private fun FloatValueInternal.computeSize(): Int {
    var __result = 0
    if (value != 0.0f) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.float(value))
    }

    return __result
}

@InternalRpcApi
public fun FloatValue.asInternal(): FloatValueInternal {
    return this as? FloatValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun Int64ValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun Int64ValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value != 0L) {
        encoder.writeInt64(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun Int64ValueInternal.Companion.decodeWith(msg: Int64ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readInt64()
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

private fun Int64ValueInternal.computeSize(): Int {
    var __result = 0
    if (value != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(value))
    }

    return __result
}

@InternalRpcApi
public fun Int64Value.asInternal(): Int64ValueInternal {
    return this as? Int64ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun UInt64ValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun UInt64ValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value != 0uL) {
        encoder.writeUInt64(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun UInt64ValueInternal.Companion.decodeWith(msg: UInt64ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readUInt64()
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

private fun UInt64ValueInternal.computeSize(): Int {
    var __result = 0
    if (value != 0uL) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt64(value))
    }

    return __result
}

@InternalRpcApi
public fun UInt64Value.asInternal(): UInt64ValueInternal {
    return this as? UInt64ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun Int32ValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun Int32ValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value != 0) {
        encoder.writeInt32(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun Int32ValueInternal.Companion.decodeWith(msg: Int32ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readInt32()
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

private fun Int32ValueInternal.computeSize(): Int {
    var __result = 0
    if (value != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(value))
    }

    return __result
}

@InternalRpcApi
public fun Int32Value.asInternal(): Int32ValueInternal {
    return this as? Int32ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun UInt32ValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun UInt32ValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value != 0u) {
        encoder.writeUInt32(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun UInt32ValueInternal.Companion.decodeWith(msg: UInt32ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readUInt32()
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

private fun UInt32ValueInternal.computeSize(): Int {
    var __result = 0
    if (value != 0u) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt32(value))
    }

    return __result
}

@InternalRpcApi
public fun UInt32Value.asInternal(): UInt32ValueInternal {
    return this as? UInt32ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun BoolValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun BoolValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value != false) {
        encoder.writeBool(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun BoolValueInternal.Companion.decodeWith(msg: BoolValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.value = decoder.readBool()
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

private fun BoolValueInternal.computeSize(): Int {
    var __result = 0
    if (value != false) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(value))
    }

    return __result
}

@InternalRpcApi
public fun BoolValue.asInternal(): BoolValueInternal {
    return this as? BoolValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun StringValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun StringValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun StringValueInternal.Companion.decodeWith(msg: StringValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.value = decoder.readString()
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

private fun StringValueInternal.computeSize(): Int {
    var __result = 0
    if (value.isNotEmpty()) {
        __result += WireSize.string(value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun StringValue.asInternal(): StringValueInternal {
    return this as? StringValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun BytesValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun BytesValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (value.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 1, value = value)
    }
}

@InternalRpcApi
public fun BytesValueInternal.Companion.decodeWith(msg: BytesValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
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

private fun BytesValueInternal.computeSize(): Int {
    var __result = 0
    if (value.isNotEmpty()) {
        __result += WireSize.bytes(value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun BytesValue.asInternal(): BytesValueInternal {
    return this as? BytesValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
