@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

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

public class StructInternal: Struct.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var fields: Map<String, Value> by MsgFieldDelegate { mutableMapOf() }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return fields.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StructInternal
        other.checkRequiredFields()
        if (this.fields != other.fields) return false
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
        builder.appendLine("Struct(")
        builder.appendLine("${nextIndentString}fields=${this.fields},")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    public fun copyInternal(body: StructInternal.() -> Unit): StructInternal {
        val copy = StructInternal()
        copy.fields = this.fields.mapValues { it.value.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    public class FieldsEntryInternal: InternalMessage(fieldsWithPresence = 1) {
        private object PresenceIndices {
            public const val value: Int = 0
        }

        @InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        public var key: String by MsgFieldDelegate { "" }
        public var value: Value by MsgFieldDelegate(PresenceIndices.value) { ValueInternal() }

        public override fun hashCode(): Int {
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + if (presenceMask[0]) value.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): Boolean {
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as FieldsEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (this.key != other.key) return false
            if (presenceMask[0] && this.value != other.value) return false
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
            builder.appendLine("Struct.FieldsEntry(")
            builder.appendLine("${nextIndentString}key=${this.key},")
            if (presenceMask[0]) {
                builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
            } else {
                builder.appendLine("${nextIndentString}value=<unset>,")
            }

            builder.append("${indentString})")
            return builder.toString()
        }

        @InternalRpcApi
        public companion object
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<Struct> {
        public override fun encode(value: Struct, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Struct {
            WireDecoder(source).use {
                val msg = StructInternal()
                checkForPlatformDecodeException {
                    StructInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Struct> {
        public override val fullName: String = "google.protobuf.Struct"
    }

    @InternalRpcApi
    public companion object
}

public class ValueInternal: Value.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var kind: Value.Kind? = null

    public override fun hashCode(): Int {
        checkRequiredFields()
        return (kind?.oneOfHashCode() ?: 0)
    }

    public fun Value.Kind.oneOfHashCode(): Int {
        return when (this) {
            is Value.Kind.NullValue -> hashCode() + 0
            is Value.Kind.NumberValue -> value.toBits().hashCode() + 1
            is Value.Kind.StringValue -> hashCode() + 2
            is Value.Kind.BoolValue -> hashCode() + 3
            is Value.Kind.StructValue -> hashCode() + 4
            is Value.Kind.ListValue -> hashCode() + 5
        }
    }

    public fun oneOfEquals(a: Value.Kind?, b: Value.Kind?): Boolean {
        if (a === b) return true
        if (a == null || b == null) return false
        if (a::class != b::class) return false
        return when (a) {
            is Value.Kind.NullValue -> a == b
            is Value.Kind.NumberValue -> a.value.toBits() == (b as Value.Kind.NumberValue).value.toBits()
            is Value.Kind.StringValue -> a == b
            is Value.Kind.BoolValue -> a == b
            is Value.Kind.StructValue -> a == b
            is Value.Kind.ListValue -> a == b
        }
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ValueInternal
        other.checkRequiredFields()
        if (!oneOfEquals(this.kind, other.kind)) return false
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
        builder.appendLine("Value(")
        builder.appendLine("${nextIndentString}kind=${this.kind},")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    public fun copyInternal(body: ValueInternal.() -> Unit): ValueInternal {
        val copy = ValueInternal()
        copy.kind = this.kind?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public fun Value.Kind.oneOfCopy(): Value.Kind {
        return when (this) {
            is Value.Kind.NullValue -> {
                this
            }
            is Value.Kind.NumberValue -> {
                this
            }
            is Value.Kind.StringValue -> {
                this
            }
            is Value.Kind.BoolValue -> {
                this
            }
            is Value.Kind.StructValue -> {
                Value.Kind.StructValue(this.value.copy())
            }
            is Value.Kind.ListValue -> {
                Value.Kind.ListValue(this.value.copy())
            }
        }
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<Value> {
        public override fun encode(value: Value, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Value {
            WireDecoder(source).use {
                val msg = ValueInternal()
                checkForPlatformDecodeException {
                    ValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Value> {
        public override val fullName: String = "google.protobuf.Value"
    }

    @InternalRpcApi
    public companion object
}

public class ListValueInternal: ListValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var values: List<Value> by MsgFieldDelegate { mutableListOf() }

    public override fun hashCode(): Int {
        checkRequiredFields()
        return values.hashCode()
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ListValueInternal
        other.checkRequiredFields()
        if (this.values != other.values) return false
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
        builder.appendLine("ListValue(")
        builder.appendLine("${nextIndentString}values=${this.values},")
        builder.append("${indentString})")
        return builder.toString()
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
    public object MARSHALLER: MessageMarshaller<ListValue> {
        public override fun encode(value: ListValue, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): ListValue {
            WireDecoder(source).use {
                val msg = ListValueInternal()
                checkForPlatformDecodeException {
                    ListValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<ListValue> {
        public override val fullName: String = "google.protobuf.ListValue"
    }

    @InternalRpcApi
    public companion object
}

@InternalRpcApi
public fun StructInternal.checkRequiredFields() {
    // no required fields to check
    this.fields.values.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun StructInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.fields.isNotEmpty()) {
        this.fields.forEach { kEntry ->
            StructInternal.FieldsEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 1, value = entry.asInternal()) { encodeWith(it, config) }
            }
        }
    }
}

@InternalRpcApi
public fun StructInternal.Companion.decodeWith(msg: StructInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                with(StructInternal.FieldsEntryInternal()) {
                    decoder.readMessage(this.asInternal(), { msg, decoder -> StructInternal.FieldsEntryInternal.decodeWith(msg, decoder, config) })
                    (msg.fields as MutableMap)[key] = value
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
}

private fun StructInternal.computeSize(): Int {
    var __result = 0
    if (this.fields.isNotEmpty()) {
        __result += fields.entries.sumOf { kEntry ->
            StructInternal.FieldsEntryInternal().apply {
                key = kEntry.key
                value = kEntry.value
            }
            ._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
        }
    }

    return __result
}

@InternalRpcApi
public fun Struct.asInternal(): StructInternal {
    return this as? StructInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ValueInternal.checkRequiredFields() {
    // no required fields to check
    this.kind?.also {
        when {
            it is Value.Kind.StructValue -> {
                it.value.asInternal().checkRequiredFields()
            }
            it is Value.Kind.ListValue -> {
                it.value.asInternal().checkRequiredFields()
            }
        }
    }
}

@InternalRpcApi
public fun ValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    this.kind?.also {
        when (val value = it) {
            is Value.Kind.NullValue -> {
                encoder.writeEnum(fieldNr = 1, value = value.value.number)
            }
            is Value.Kind.NumberValue -> {
                encoder.writeDouble(fieldNr = 2, value = value.value)
            }
            is Value.Kind.StringValue -> {
                encoder.writeString(fieldNr = 3, value = value.value)
            }
            is Value.Kind.BoolValue -> {
                encoder.writeBool(fieldNr = 4, value = value.value)
            }
            is Value.Kind.StructValue -> {
                encoder.writeMessage(fieldNr = 5, value = value.value.asInternal()) { encodeWith(it, config) }
            }
            is Value.Kind.ListValue -> {
                encoder.writeMessage(fieldNr = 6, value = value.value.asInternal()) { encodeWith(it, config) }
            }
        }
    }
}

@InternalRpcApi
public fun ValueInternal.Companion.decodeWith(msg: ValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.kind = Value.Kind.NullValue(NullValue.fromNumber(decoder.readEnum()))
            }
            tag.fieldNr == 2 && tag.wireType == WireType.FIXED64 -> {
                msg.kind = Value.Kind.NumberValue(decoder.readDouble())
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.kind = Value.Kind.StringValue(decoder.readString())
            }
            tag.fieldNr == 4 && tag.wireType == WireType.VARINT -> {
                msg.kind = Value.Kind.BoolValue(decoder.readBool())
            }
            tag.fieldNr == 5 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.kind as? Value.Kind.StructValue) ?: Value.Kind.StructValue(StructInternal()).also {
                    msg.kind = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> StructInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val field = (msg.kind as? Value.Kind.ListValue) ?: Value.Kind.ListValue(ListValueInternal()).also {
                    msg.kind = it
                }

                decoder.readMessage(field.value.asInternal(), { msg, decoder -> ListValueInternal.decodeWith(msg, decoder, config) })
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

private fun ValueInternal.computeSize(): Int {
    var __result = 0
    this.kind?.also {
        when (val value = it) {
            is Value.Kind.NullValue -> {
                __result += (WireSize.tag(1, WireType.VARINT) + WireSize.enum(value.value.number))
            }
            is Value.Kind.NumberValue -> {
                __result += (WireSize.tag(2, WireType.FIXED64) + WireSize.double(value.value))
            }
            is Value.Kind.StringValue -> {
                __result += WireSize.string(value.value).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is Value.Kind.BoolValue -> {
                __result += (WireSize.tag(4, WireType.VARINT) + WireSize.bool(value.value))
            }
            is Value.Kind.StructValue -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is Value.Kind.ListValue -> {
                __result += value.value.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
        }
    }

    return __result
}

@InternalRpcApi
public fun Value.asInternal(): ValueInternal {
    return this as? ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun ListValueInternal.checkRequiredFields() {
    // no required fields to check
    this.values.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun ListValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.values.isNotEmpty()) {
        this.values.forEach {
            encoder.writeMessage(fieldNr = 1, value = it.asInternal()) { encodeWith(it, config) }
        }
    }
}

@InternalRpcApi
public fun ListValueInternal.Companion.decodeWith(msg: ListValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = ValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> ValueInternal.decodeWith(msg, decoder, config) })
                (msg.values as MutableList).add(elem)
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

private fun ListValueInternal.computeSize(): Int {
    var __result = 0
    if (this.values.isNotEmpty()) {
        __result += this.values.sumOf { it.asInternal()._size.let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    return __result
}

@InternalRpcApi
public fun ListValue.asInternal(): ListValueInternal {
    return this as? ListValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun StructInternal.FieldsEntryInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun StructInternal.FieldsEntryInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.key.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.key)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
    }
}

@InternalRpcApi
public fun StructInternal.FieldsEntryInternal.Companion.decodeWith(msg: StructInternal.FieldsEntryInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.value = ValueInternal()
                }

                decoder.readMessage(msg.value.asInternal(), { msg, decoder -> ValueInternal.decodeWith(msg, decoder, config) })
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

private fun StructInternal.FieldsEntryInternal.computeSize(): Int {
    var __result = 0
    if (this.key.isNotEmpty()) {
        __result += WireSize.string(this.key).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

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
