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
import kotlinx.rpc.protobuf.internal.double
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.float
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.int64
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag
import kotlinx.rpc.protobuf.internal.uInt32
import kotlinx.rpc.protobuf.internal.uInt64

public class DoubleValueInternal: DoubleValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Double by MsgFieldDelegate { 0.0 }

    private val _owner: DoubleValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.toBits().hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DoubleValueInternal
        other.checkRequiredFields()
        if (this.value.toBits() != other.value.toBits()) return false
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
        builder.appendLine("DoubleValue(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): DoubleValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<DoubleValue> {
        public override fun encode(value: DoubleValue, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): DoubleValue {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class FloatValueInternal: FloatValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Float by MsgFieldDelegate { 0.0f }

    private val _owner: FloatValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.toBits().hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FloatValueInternal
        other.checkRequiredFields()
        if (this.value.toBits() != other.value.toBits()) return false
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
        builder.appendLine("FloatValue(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): FloatValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<FloatValue> {
        public override fun encode(value: FloatValue, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): FloatValue {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class Int64ValueInternal: Int64Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Long by MsgFieldDelegate { 0L }

    private val _owner: Int64ValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Int64ValueInternal
        other.checkRequiredFields()
        if (this.value != other.value) return false
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
        builder.appendLine("Int64Value(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): Int64ValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<Int64Value> {
        public override fun encode(value: Int64Value, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Int64Value {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class UInt64ValueInternal: UInt64Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: ULong by MsgFieldDelegate { 0uL }

    private val _owner: UInt64ValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UInt64ValueInternal
        other.checkRequiredFields()
        if (this.value != other.value) return false
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
        builder.appendLine("UInt64Value(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): UInt64ValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<UInt64Value> {
        public override fun encode(value: UInt64Value, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): UInt64Value {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class Int32ValueInternal: Int32Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Int by MsgFieldDelegate { 0 }

    private val _owner: Int32ValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Int32ValueInternal
        other.checkRequiredFields()
        if (this.value != other.value) return false
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
        builder.appendLine("Int32Value(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): Int32ValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<Int32Value> {
        public override fun encode(value: Int32Value, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Int32Value {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class UInt32ValueInternal: UInt32Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: UInt by MsgFieldDelegate { 0u }

    private val _owner: UInt32ValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UInt32ValueInternal
        other.checkRequiredFields()
        if (this.value != other.value) return false
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
        builder.appendLine("UInt32Value(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): UInt32ValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<UInt32Value> {
        public override fun encode(value: UInt32Value, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): UInt32Value {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class BoolValueInternal: BoolValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Boolean by MsgFieldDelegate { false }

    private val _owner: BoolValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BoolValueInternal
        other.checkRequiredFields()
        if (this.value != other.value) return false
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
        builder.appendLine("BoolValue(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): BoolValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<BoolValue> {
        public override fun encode(value: BoolValue, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): BoolValue {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class StringValueInternal: StringValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: String by MsgFieldDelegate { "" }

    private val _owner: StringValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StringValueInternal
        other.checkRequiredFields()
        if (this.value != other.value) return false
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
        builder.appendLine("StringValue(")
        builder.appendLine("${nextIndentString}value=${this.value},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): StringValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<StringValue> {
        public override fun encode(value: StringValue, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): StringValue {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class BytesValueInternal: BytesValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: ByteArray by MsgFieldDelegate { byteArrayOf() }

    private val _owner: BytesValueInternal = this

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = value.contentHashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BytesValueInternal
        other.checkRequiredFields()
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
        builder.appendLine("BytesValue(")
        builder.appendLine("${nextIndentString}value=${this.value.contentToString()},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): BytesValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<BytesValue> {
        public override fun encode(value: BytesValue, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): BytesValue {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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
    if (this.value != 0.0) {
        encoder.writeDouble(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun DoubleValueInternal.Companion.decodeWith(msg: DoubleValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(DoubleValue::class) ?: emptyMap()
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
    if (this.value != 0.0) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.double(this.value))
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
    if (this.value != 0.0f) {
        encoder.writeFloat(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun FloatValueInternal.Companion.decodeWith(msg: FloatValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(FloatValue::class) ?: emptyMap()
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
    if (this.value != 0.0f) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.float(this.value))
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
    if (this.value != 0L) {
        encoder.writeInt64(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun Int64ValueInternal.Companion.decodeWith(msg: Int64ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(Int64Value::class) ?: emptyMap()
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
    if (this.value != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(this.value))
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
    if (this.value != 0uL) {
        encoder.writeUInt64(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun UInt64ValueInternal.Companion.decodeWith(msg: UInt64ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(UInt64Value::class) ?: emptyMap()
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
    if (this.value != 0uL) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt64(this.value))
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
    if (this.value != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun Int32ValueInternal.Companion.decodeWith(msg: Int32ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(Int32Value::class) ?: emptyMap()
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
    if (this.value != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.value))
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
    if (this.value != 0u) {
        encoder.writeUInt32(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun UInt32ValueInternal.Companion.decodeWith(msg: UInt32ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(UInt32Value::class) ?: emptyMap()
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
    if (this.value != 0u) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt32(this.value))
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
    if (this.value != false) {
        encoder.writeBool(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun BoolValueInternal.Companion.decodeWith(msg: BoolValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(BoolValue::class) ?: emptyMap()
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
    if (this.value != false) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.value))
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
    if (this.value.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun StringValueInternal.Companion.decodeWith(msg: StringValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(StringValue::class) ?: emptyMap()
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
    if (this.value.isNotEmpty()) {
        __result += WireSize.string(this.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
    if (this.value.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun BytesValueInternal.Companion.decodeWith(msg: BytesValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(BytesValue::class) ?: emptyMap()
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
    if (this.value.isNotEmpty()) {
        __result += WireSize.bytes(this.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun BytesValue.asInternal(): BytesValueInternal {
    return this as? BytesValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
