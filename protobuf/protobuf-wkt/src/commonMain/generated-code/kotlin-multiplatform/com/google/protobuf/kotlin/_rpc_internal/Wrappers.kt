@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
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

public class DoubleValueInternal: DoubleValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<Double> = MsgFieldDelegate { 0.0 }
    public override var value: Double by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.toBits().hashCode()
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
    public object MARSHALLER: GrpcMarshaller<DoubleValue> {
        public override fun encode(value: DoubleValue, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): DoubleValue {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = DoubleValueInternal()
                checkForPlatformDecodeException {
                    DoubleValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<DoubleValue> {
        public override val fullName: String = "google.protobuf.DoubleValue"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: DoubleValue by lazy { DoubleValueInternal() }
    }
}

public class FloatValueInternal: FloatValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<Float> = MsgFieldDelegate { 0.0f }
    public override var value: Float by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.toBits().hashCode()
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
    public object MARSHALLER: GrpcMarshaller<FloatValue> {
        public override fun encode(value: FloatValue, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): FloatValue {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = FloatValueInternal()
                checkForPlatformDecodeException {
                    FloatValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<FloatValue> {
        public override val fullName: String = "google.protobuf.FloatValue"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: FloatValue by lazy { FloatValueInternal() }
    }
}

public class Int64ValueInternal: Int64Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<Long> = MsgFieldDelegate { 0L }
    public override var value: Long by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<Int64Value> {
        public override fun encode(value: Int64Value, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Int64Value {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = Int64ValueInternal()
                checkForPlatformDecodeException {
                    Int64ValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Int64Value> {
        public override val fullName: String = "google.protobuf.Int64Value"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Int64Value by lazy { Int64ValueInternal() }
    }
}

public class UInt64ValueInternal: UInt64Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<ULong> = MsgFieldDelegate { 0uL }
    public override var value: ULong by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<UInt64Value> {
        public override fun encode(value: UInt64Value, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): UInt64Value {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = UInt64ValueInternal()
                checkForPlatformDecodeException {
                    UInt64ValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<UInt64Value> {
        public override val fullName: String = "google.protobuf.UInt64Value"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: UInt64Value by lazy { UInt64ValueInternal() }
    }
}

public class Int32ValueInternal: Int32Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate { 0 }
    public override var value: Int by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<Int32Value> {
        public override fun encode(value: Int32Value, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Int32Value {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = Int32ValueInternal()
                checkForPlatformDecodeException {
                    Int32ValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Int32Value> {
        public override val fullName: String = "google.protobuf.Int32Value"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Int32Value by lazy { Int32ValueInternal() }
    }
}

public class UInt32ValueInternal: UInt32Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<UInt> = MsgFieldDelegate { 0u }
    public override var value: UInt by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<UInt32Value> {
        public override fun encode(value: UInt32Value, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): UInt32Value {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = UInt32ValueInternal()
                checkForPlatformDecodeException {
                    UInt32ValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<UInt32Value> {
        public override val fullName: String = "google.protobuf.UInt32Value"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: UInt32Value by lazy { UInt32ValueInternal() }
    }
}

public class BoolValueInternal: BoolValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate { false }
    public override var value: Boolean by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<BoolValue> {
        public override fun encode(value: BoolValue, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): BoolValue {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = BoolValueInternal()
                checkForPlatformDecodeException {
                    BoolValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<BoolValue> {
        public override val fullName: String = "google.protobuf.BoolValue"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: BoolValue by lazy { BoolValueInternal() }
    }
}

public class StringValueInternal: StringValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var value: String by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<StringValue> {
        public override fun encode(value: StringValue, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): StringValue {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = StringValueInternal()
                checkForPlatformDecodeException {
                    StringValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<StringValue> {
        public override val fullName: String = "google.protobuf.StringValue"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: StringValue by lazy { StringValueInternal() }
    }
}

public class BytesValueInternal: BytesValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valueDelegate: MsgFieldDelegate<ByteString> = MsgFieldDelegate { ByteString() }
    public override var value: ByteString by __valueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.value.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BytesValueInternal
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
        builder.appendLine("BytesValue(")
        builder.appendLine("${nextIndentString}value=${this.value.protoToString()},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): BytesValueInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: BytesValueInternal.() -> Unit): BytesValueInternal {
        val copy = BytesValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: GrpcMarshaller<BytesValue> {
        public override fun encode(value: BytesValue, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): BytesValue {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = BytesValueInternal()
                checkForPlatformDecodeException {
                    BytesValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<BytesValue> {
        public override val fullName: String = "google.protobuf.BytesValue"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: BytesValue by lazy { BytesValueInternal() }
    }
}

@InternalRpcApi
public fun DoubleValueInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun DoubleValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value != 0.0) {
        encoder.writeDouble(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun DoubleValueInternal.Companion.decodeWith(msg: DoubleValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun DoubleValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value != 0.0) {
        __result += (WireSize.tag(1, WireType.FIXED64) + WireSize.double(this.value))
    }

    __result += _unknownFields.size.toInt()
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
public fun FloatValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value != 0.0f) {
        encoder.writeFloat(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FloatValueInternal.Companion.decodeWith(msg: FloatValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun FloatValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value != 0.0f) {
        __result += (WireSize.tag(1, WireType.FIXED32) + WireSize.float(this.value))
    }

    __result += _unknownFields.size.toInt()
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
public fun Int64ValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value != 0L) {
        encoder.writeInt64(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun Int64ValueInternal.Companion.decodeWith(msg: Int64ValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun Int64ValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value != 0L) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int64(this.value))
    }

    __result += _unknownFields.size.toInt()
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
public fun UInt64ValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value != 0uL) {
        encoder.writeUInt64(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun UInt64ValueInternal.Companion.decodeWith(msg: UInt64ValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun UInt64ValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value != 0uL) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt64(this.value))
    }

    __result += _unknownFields.size.toInt()
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
public fun Int32ValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value != 0) {
        encoder.writeInt32(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun Int32ValueInternal.Companion.decodeWith(msg: Int32ValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun Int32ValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value != 0) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.int32(this.value))
    }

    __result += _unknownFields.size.toInt()
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
public fun UInt32ValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value != 0u) {
        encoder.writeUInt32(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun UInt32ValueInternal.Companion.decodeWith(msg: UInt32ValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun UInt32ValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value != 0u) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.uInt32(this.value))
    }

    __result += _unknownFields.size.toInt()
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
public fun BoolValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value != false) {
        encoder.writeBool(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun BoolValueInternal.Companion.decodeWith(msg: BoolValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun BoolValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value != false) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.value))
    }

    __result += _unknownFields.size.toInt()
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
public fun StringValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun StringValueInternal.Companion.decodeWith(msg: StringValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun StringValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value.isNotEmpty()) {
        __result += WireSize.string(this.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
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
public fun BytesValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.value.isNotEmpty()) {
        encoder.writeBytes(fieldNr = 1, value = this.value)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun BytesValueInternal.Companion.decodeWith(msg: BytesValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun BytesValueInternal.computeSize(): Int {
    var __result = 0
    if (this.value.isNotEmpty()) {
        __result += WireSize.bytes(this.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun BytesValue.asInternal(): BytesValueInternal {
    return this as? BytesValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
