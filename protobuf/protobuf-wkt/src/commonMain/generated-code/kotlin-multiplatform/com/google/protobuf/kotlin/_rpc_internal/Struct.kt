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
import kotlinx.rpc.protobuf.internal.GeneratedProtoOneOfs
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.InternalPresenceObject
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.bool
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException
import kotlinx.rpc.protobuf.internal.double
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

@InternalRpcApi
public class StructInternal: Struct.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __fieldsDelegate: MsgFieldDelegate<Map<String, Value>> = MsgFieldDelegate { emptyMap() }
    public override var fields: Map<String, Value> by __fieldsDelegate

    public override fun hashCode(): Int {
        var result = this.fields.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StructInternal
        return this.fields == other.fields
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("Struct(")
        builder.appendLine("${nextIndentString}fields=${this.fields},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): StructInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: StructInternal.() -> Unit): StructInternal {
        val copy = StructInternal()
        copy.fields = this.fields.mapValues { it.value.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public class FieldsEntryInternal: InternalMessage(fieldsWithPresence = 1) {
        @InternalRpcApi
        internal object PresenceIndices {
            const val value: Int = 0
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        internal val __keyDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
        public var key: String by __keyDelegate
        internal val __valueDelegate: MsgFieldDelegate<Value> = MsgFieldDelegate(PresenceIndices.value) { ValueInternal.DEFAULT }
        public var value: Value by __valueDelegate

        public override fun hashCode(): Int {
            var result = this.key.hashCode()
            result = 31 * result + if (presenceMask[PresenceIndices.value]) this.value.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as FieldsEntryInternal
            if (presenceMask != other.presenceMask) return false
            if (this.key != other.key) return false
            return !presenceMask[PresenceIndices.value] || this.value == other.value
        }

        public override fun toString(): String {
            return asString()
        }

        public fun asString(indent: Int = 0): String {
            val indentString = " ".repeat(indent)
            val nextIndentString = " ".repeat(indent + 4)
            val builder = StringBuilder()
            builder.appendLine("Struct.FieldsEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            if (presenceMask[PresenceIndices.value]) {
                builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        public override fun copyInternal(): FieldsEntryInternal {
            return this
        }

        @InternalRpcApi
        public companion object
    }

    @InternalRpcApi
    public object MARSHALLER: GrpcMarshaller<Struct> {
        public override fun encode(value: Struct, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Struct {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = StructInternal()
                checkForPlatformDecodeException {
                    StructInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Struct> {
        public override val fullName: String = "google.protobuf.Struct"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Struct by lazy { StructInternal() }
    }
}

@InternalRpcApi
@GeneratedProtoOneOfs(names = ["kind"])
public class ValueInternal: Value.Builder, InternalMessage(fieldsWithPresence = 6) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val nullValue: Int = 0
        const val numberValue: Int = 1
        const val stringValue: Int = 2
        const val boolValue: Int = 3
        const val structValue: Int = 4
        const val listValue: Int = 5
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    private var _kindRef: kotlin.Any? = null
    private var _kindNum: Long = 0L

    @InternalRpcApi
    public val _kindCase: ValueKindCase get() = when {
        presenceMask[PresenceIndices.nullValue] -> ValueKindCase.NULL_VALUE
        presenceMask[PresenceIndices.numberValue] -> ValueKindCase.NUMBER_VALUE
        presenceMask[PresenceIndices.stringValue] -> ValueKindCase.STRING_VALUE
        presenceMask[PresenceIndices.boolValue] -> ValueKindCase.BOOL_VALUE
        presenceMask[PresenceIndices.structValue] -> ValueKindCase.STRUCT_VALUE
        presenceMask[PresenceIndices.listValue] -> ValueKindCase.LIST_VALUE
        else -> ValueKindCase.NOT_SET
    }

    public override fun clearKind() {
        presenceMask.clearRange(PresenceIndices.nullValue, PresenceIndices.listValue)
        _kindRef = null
        _kindNum = 0L
    }

    public override var nullValue: NullValue
        get() = if (presenceMask[PresenceIndices.nullValue]) NullValue.fromNumber(_kindNum.toInt()) else NullValue.NULL_VALUE
        set(value) { presenceMask.setExclusive(PresenceIndices.nullValue, PresenceIndices.nullValue, PresenceIndices.listValue); _kindNum = value.number.toLong(); _kindRef = null }

    public override fun clearNullValue() {
        if (presenceMask[PresenceIndices.nullValue]) clearKind()
    }

    public override var numberValue: Double
        get() = if (presenceMask[PresenceIndices.numberValue]) Double.fromBits(_kindNum) else 0.0
        set(value) { presenceMask.setExclusive(PresenceIndices.numberValue, PresenceIndices.nullValue, PresenceIndices.listValue); _kindNum = value.toRawBits(); _kindRef = null }

    public override fun clearNumberValue() {
        if (presenceMask[PresenceIndices.numberValue]) clearKind()
    }

    public override var stringValue: String
        get() = if (presenceMask[PresenceIndices.stringValue]) (_kindRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.stringValue, PresenceIndices.nullValue, PresenceIndices.listValue); _kindRef = value; _kindNum = 0L }

    public override fun clearStringValue() {
        if (presenceMask[PresenceIndices.stringValue]) clearKind()
    }

    public override var boolValue: Boolean
        get() = if (presenceMask[PresenceIndices.boolValue]) (_kindNum != 0L) else false
        set(value) { presenceMask.setExclusive(PresenceIndices.boolValue, PresenceIndices.nullValue, PresenceIndices.listValue); _kindNum = if (value) 1L else 0L; _kindRef = null }

    public override fun clearBoolValue() {
        if (presenceMask[PresenceIndices.boolValue]) clearKind()
    }

    public override var structValue: Struct
        get() = if (presenceMask[PresenceIndices.structValue]) (_kindRef as Struct) else StructInternal.DEFAULT
        set(value) { presenceMask.setExclusive(PresenceIndices.structValue, PresenceIndices.nullValue, PresenceIndices.listValue); _kindRef = value; _kindNum = 0L }

    public override fun clearStructValue() {
        if (presenceMask[PresenceIndices.structValue]) clearKind()
    }

    public override var listValue: ListValue
        get() = if (presenceMask[PresenceIndices.listValue]) (_kindRef as ListValue) else ListValueInternal.DEFAULT
        set(value) { presenceMask.setExclusive(PresenceIndices.listValue, PresenceIndices.nullValue, PresenceIndices.listValue); _kindRef = value; _kindNum = 0L }

    public override fun clearListValue() {
        if (presenceMask[PresenceIndices.listValue]) clearKind()
    }

    private val _owner: ValueInternal = this

    @InternalRpcApi
    public val _presence: ValuePresence = object : ValuePresence, InternalPresenceObject {
        public override val _message: ValueInternal get() = _owner

        public override val hasNullValue: Boolean get() = presenceMask[PresenceIndices.nullValue]

        public override val hasNumberValue: Boolean get() = presenceMask[PresenceIndices.numberValue]

        public override val hasStringValue: Boolean get() = presenceMask[PresenceIndices.stringValue]

        public override val hasBoolValue: Boolean get() = presenceMask[PresenceIndices.boolValue]

        public override val hasStructValue: Boolean get() = presenceMask[PresenceIndices.structValue]

        public override val hasListValue: Boolean get() = presenceMask[PresenceIndices.listValue]
    }

    public override fun hashCode(): Int {
        var result = when {
            presenceMask[PresenceIndices.nullValue] -> 1 * 31 + this.nullValue.hashCode()
            presenceMask[PresenceIndices.numberValue] -> 2 * 31 + this.numberValue.toBits().hashCode()
            presenceMask[PresenceIndices.stringValue] -> 3 * 31 + this.stringValue.hashCode()
            presenceMask[PresenceIndices.boolValue] -> 4 * 31 + this.boolValue.hashCode()
            presenceMask[PresenceIndices.structValue] -> 5 * 31 + this.structValue.hashCode()
            presenceMask[PresenceIndices.listValue] -> 6 * 31 + this.listValue.hashCode()
            else -> 0
        }

        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ValueInternal
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.nullValue] && this.nullValue != other.nullValue) return false
        if (presenceMask[PresenceIndices.numberValue] && this.numberValue.toBits() != other.numberValue.toBits()) return false
        if (presenceMask[PresenceIndices.stringValue] && this.stringValue != other.stringValue) return false
        if (presenceMask[PresenceIndices.boolValue] && this.boolValue != other.boolValue) return false
        if (presenceMask[PresenceIndices.structValue] && this.structValue != other.structValue) return false
        return !presenceMask[PresenceIndices.listValue] || this.listValue == other.listValue
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("Value(")
        if (presenceMask[PresenceIndices.nullValue]) {
            builder.appendLine("${nextIndentString}nullValue=${this.nullValue},")
        }

        if (presenceMask[PresenceIndices.numberValue]) {
            builder.appendLine("${nextIndentString}numberValue=${this.numberValue},")
        }

        if (presenceMask[PresenceIndices.stringValue]) {
            builder.appendLine("${nextIndentString}stringValue=${this.stringValue},")
        }

        if (presenceMask[PresenceIndices.boolValue]) {
            builder.appendLine("${nextIndentString}boolValue=${this.boolValue},")
        }

        if (presenceMask[PresenceIndices.structValue]) {
            builder.appendLine("${nextIndentString}structValue=${this.structValue.asInternal().asString(indent = indent + 4)},")
        }

        if (presenceMask[PresenceIndices.listValue]) {
            builder.appendLine("${nextIndentString}listValue=${this.listValue.asInternal().asString(indent = indent + 4)},")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): ValueInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: ValueInternal.() -> Unit): ValueInternal {
        val copy = ValueInternal()
        if (presenceMask[PresenceIndices.nullValue]) {
            copy.nullValue = this.nullValue
        }

        if (presenceMask[PresenceIndices.numberValue]) {
            copy.numberValue = this.numberValue
        }

        if (presenceMask[PresenceIndices.stringValue]) {
            copy.stringValue = this.stringValue
        }

        if (presenceMask[PresenceIndices.boolValue]) {
            copy.boolValue = this.boolValue
        }

        if (presenceMask[PresenceIndices.structValue]) {
            copy.structValue = this.structValue.copy()
        }

        if (presenceMask[PresenceIndices.listValue]) {
            copy.listValue = this.listValue.copy()
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: GrpcMarshaller<Value> {
        public override fun encode(value: Value, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Value {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ValueInternal()
                checkForPlatformDecodeException {
                    ValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Value> {
        public override val fullName: String = "google.protobuf.Value"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Value by lazy { ValueInternal() }
    }
}

@InternalRpcApi
public class ListValueInternal: ListValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __valuesDelegate: MsgFieldDelegate<List<Value>> = MsgFieldDelegate { emptyList() }
    public override var values: List<Value> by __valuesDelegate

    public override fun hashCode(): Int {
        var result = this.values.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ListValueInternal
        return this.values == other.values
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ListValue(")
        builder.appendLine("${nextIndentString}values=${this.values},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): ListValueInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    public fun copyInternal(body: ListValueInternal.() -> Unit): ListValueInternal {
        val copy = ListValueInternal()
        copy.values = this.values.map { it.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: GrpcMarshaller<ListValue> {
        public override fun encode(value: ListValue, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): ListValue {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ListValueInternal()
                checkForPlatformDecodeException {
                    ListValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<ListValue> {
        public override val fullName: String = "google.protobuf.ListValue"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: ListValue by lazy { ListValueInternal() }
    }
}

@InternalRpcApi
public fun StructInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.fields.isNotEmpty()) {
        this.fields.forEach { kEntry ->
            StructInternal.FieldsEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 1, value = entry.asInternal()) { encoder -> encodeWith(encoder, config) }
            }
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
public fun StructInternal.Companion.decodeWith(
    msg: StructInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__fieldsDelegate.getOrCreate(msg) { mutableMapOf() } as MutableMap
                with(StructInternal.FieldsEntryInternal()) {
                    decoder.readMessage(this.asInternal()) { msg, decoder -> StructInternal.FieldsEntryInternal.decodeWith(msg, decoder, config) }
                    target[key] = value
                }
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

private fun StructInternal.computeSize(): Int {
    var __result = 0
    if (this.fields.isNotEmpty()) {
        __result += this.fields.entries.sumOf { kEntry ->
            StructInternal.FieldsEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun Struct.asInternal(): StructInternal {
    return this as? StructInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[ValueInternal.PresenceIndices.nullValue]) {
        encoder.writeEnum(fieldNr = 1, value = this.nullValue.number)
    }

    if (presenceMask[ValueInternal.PresenceIndices.numberValue]) {
        encoder.writeDouble(fieldNr = 2, value = this.numberValue)
    }

    if (presenceMask[ValueInternal.PresenceIndices.stringValue]) {
        encoder.writeString(fieldNr = 3, value = this.stringValue)
    }

    if (presenceMask[ValueInternal.PresenceIndices.boolValue]) {
        encoder.writeBool(fieldNr = 4, value = this.boolValue)
    }

    if (presenceMask[ValueInternal.PresenceIndices.structValue]) {
        encoder.writeMessage(fieldNr = 5, value = this.structValue.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (presenceMask[ValueInternal.PresenceIndices.listValue]) {
        encoder.writeMessage(fieldNr = 6, value = this.listValue.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun ValueInternal.Companion.decodeWith(
    msg: ValueInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
                msg.nullValue = NullValue.fromNumber(decoder.readEnum())
            }
            2 if tag.wireType == WireType.FIXED64 -> {
                msg.numberValue = decoder.readDouble()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.stringValue = decoder.readString()
            }
            4 if tag.wireType == WireType.VARINT -> {
                msg.boolValue = decoder.readBool()
            }
            5 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = if (msg.presenceMask[ValueInternal.PresenceIndices.structValue]) msg.structValue.asInternal() else StructInternal().also { msg.structValue = it }
                decoder.readMessage(target.asInternal()) { msg, decoder -> StructInternal.decodeWith(msg, decoder, config) }
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = if (msg.presenceMask[ValueInternal.PresenceIndices.listValue]) msg.listValue.asInternal() else ListValueInternal().also { msg.listValue = it }
                decoder.readMessage(target.asInternal()) { msg, decoder -> ListValueInternal.decodeWith(msg, decoder, config) }
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

private fun ValueInternal.computeSize(): Int {
    var __result = 0
    if (presenceMask[ValueInternal.PresenceIndices.nullValue]) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.enum(this.nullValue.number)
    }

    if (presenceMask[ValueInternal.PresenceIndices.numberValue]) {
        __result += WireSize.tag(2, WireType.FIXED64) + WireSize.double(this.numberValue)
    }

    if (presenceMask[ValueInternal.PresenceIndices.stringValue]) {
        __result += WireSize.string(this.stringValue).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ValueInternal.PresenceIndices.boolValue]) {
        __result += WireSize.tag(4, WireType.VARINT) + WireSize.bool(this.boolValue)
    }

    if (presenceMask[ValueInternal.PresenceIndices.structValue]) {
        __result += this.structValue.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ValueInternal.PresenceIndices.listValue]) {
        __result += this.listValue.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun Value.asInternal(): ValueInternal {
    return this as? ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ListValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.values.isNotEmpty()) {
        this.values.forEach {
            encoder.writeMessage(fieldNr = 1, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
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
public fun ListValueInternal.Companion.decodeWith(
    msg: ListValueInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__valuesDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = ValueInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> ValueInternal.decodeWith(msg, decoder, config) }
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

private fun ListValueInternal.computeSize(): Int {
    var __result = 0
    if (this.values.isNotEmpty()) {
        __result += this.values.sumOf { element -> element.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun ListValue.asInternal(): ListValueInternal {
    return this as? ListValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun StructInternal.FieldsEntryInternal.encodeWith(
    encoder: WireEncoder,
    config: ProtoConfig?,
) {
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[StructInternal.FieldsEntryInternal.PresenceIndices.value]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun StructInternal.FieldsEntryInternal.Companion.decodeWith(
    msg: StructInternal.FieldsEntryInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__valueDelegate.getOrCreate(msg) { ValueInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> ValueInternal.decodeWith(msg, decoder, config) }
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

private fun StructInternal.FieldsEntryInternal.computeSize(): Int {
    var __result = 0
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[StructInternal.FieldsEntryInternal.PresenceIndices.value]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun StructInternal.FieldsEntryInternal.asInternal(): StructInternal.FieldsEntryInternal {
    return this
}

@InternalRpcApi
public fun NullValue.Companion.fromNumber(number: Int): NullValue {
    return when (number) {
        0 -> {
            NullValue.NULL_VALUE
        }
        else -> {
            NullValue.UNRECOGNIZED(number)
        }
    }
}
