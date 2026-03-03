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
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

public class TypeInternal: Type.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        public const val sourceContext: Int = 0
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var fields: List<Field> by MsgFieldDelegate { mutableListOf() }
    public override var oneofs: List<String> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<Option> by MsgFieldDelegate { mutableListOf() }
    public override var sourceContext: SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { SourceContextInternal() }
    public override var syntax: Syntax by MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }
    public override var edition: String by MsgFieldDelegate { "" }

    @InternalRpcApi
    public val _presence: TypePresence = object : TypePresence {
        public override val hasSourceContext: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + fields.hashCode()
        result = 31 * result + oneofs.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + if (presenceMask[0]) sourceContext.hashCode() else 0
        result = 31 * result + syntax.hashCode()
        result = 31 * result + edition.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TypeInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (this.name != other.name) return false
        if (this.fields != other.fields) return false
        if (this.oneofs != other.oneofs) return false
        if (this.options != other.options) return false
        if (presenceMask[0] && this.sourceContext != other.sourceContext) return false
        if (this.syntax != other.syntax) return false
        if (this.edition != other.edition) return false
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
        builder.appendLine("Type(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}fields=${this.fields},")
        builder.appendLine("${nextIndentString}oneofs=${this.oneofs},")
        builder.appendLine("${nextIndentString}options=${this.options},")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}sourceContext=${this.sourceContext.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}sourceContext=<unset>,")
        }

        builder.appendLine("${nextIndentString}syntax=${this.syntax},")
        builder.appendLine("${nextIndentString}edition=${this.edition},")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    public fun copyInternal(body: TypeInternal.() -> Unit): TypeInternal {
        val copy = TypeInternal()
        copy.name = this.name
        copy.fields = this.fields.map { it.copy() }
        copy.oneofs = this.oneofs.map { it }
        copy.options = this.options.map { it.copy() }
        if (presenceMask[0]) {
            copy.sourceContext = this.sourceContext.copy()
        }

        copy.syntax = this.syntax
        copy.edition = this.edition
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<Type> {
        public override fun encode(value: Type, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Type {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TypeInternal()
                checkForPlatformDecodeException {
                    TypeInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Type> {
        public override val fullName: String = "google.protobuf.Type"
    }

    @InternalRpcApi
    public companion object
}

public class FieldInternal: Field.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var kind: Field.Kind by MsgFieldDelegate { Field.Kind.TYPE_UNKNOWN }
    public override var cardinality: Field.Cardinality by MsgFieldDelegate { Field.Cardinality.CARDINALITY_UNKNOWN }
    public override var number: Int by MsgFieldDelegate { 0 }
    public override var name: String by MsgFieldDelegate { "" }
    public override var typeUrl: String by MsgFieldDelegate { "" }
    public override var oneofIndex: Int by MsgFieldDelegate { 0 }
    public override var packed: Boolean by MsgFieldDelegate { false }
    public override var options: List<Option> by MsgFieldDelegate { mutableListOf() }
    public override var jsonName: String by MsgFieldDelegate { "" }
    public override var defaultValue: String by MsgFieldDelegate { "" }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = kind.hashCode()
        result = 31 * result + cardinality.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + typeUrl.hashCode()
        result = 31 * result + oneofIndex.hashCode()
        result = 31 * result + packed.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + jsonName.hashCode()
        result = 31 * result + defaultValue.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FieldInternal
        other.checkRequiredFields()
        if (this.kind != other.kind) return false
        if (this.cardinality != other.cardinality) return false
        if (this.number != other.number) return false
        if (this.name != other.name) return false
        if (this.typeUrl != other.typeUrl) return false
        if (this.oneofIndex != other.oneofIndex) return false
        if (this.packed != other.packed) return false
        if (this.options != other.options) return false
        if (this.jsonName != other.jsonName) return false
        if (this.defaultValue != other.defaultValue) return false
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
        builder.appendLine("Field(")
        builder.appendLine("${nextIndentString}kind=${this.kind},")
        builder.appendLine("${nextIndentString}cardinality=${this.cardinality},")
        builder.appendLine("${nextIndentString}number=${this.number},")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}typeUrl=${this.typeUrl},")
        builder.appendLine("${nextIndentString}oneofIndex=${this.oneofIndex},")
        builder.appendLine("${nextIndentString}packed=${this.packed},")
        builder.appendLine("${nextIndentString}options=${this.options},")
        builder.appendLine("${nextIndentString}jsonName=${this.jsonName},")
        builder.appendLine("${nextIndentString}defaultValue=${this.defaultValue},")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    public fun copyInternal(body: FieldInternal.() -> Unit): FieldInternal {
        val copy = FieldInternal()
        copy.kind = this.kind
        copy.cardinality = this.cardinality
        copy.number = this.number
        copy.name = this.name
        copy.typeUrl = this.typeUrl
        copy.oneofIndex = this.oneofIndex
        copy.packed = this.packed
        copy.options = this.options.map { it.copy() }
        copy.jsonName = this.jsonName
        copy.defaultValue = this.defaultValue
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<Field> {
        public override fun encode(value: Field, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Field {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = FieldInternal()
                checkForPlatformDecodeException {
                    FieldInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Field> {
        public override val fullName: String = "google.protobuf.Field"
    }

    @InternalRpcApi
    public companion object
}

public class EnumInternal: Enum.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        public const val sourceContext: Int = 0
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var enumvalue: List<EnumValue> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<Option> by MsgFieldDelegate { mutableListOf() }
    public override var sourceContext: SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { SourceContextInternal() }
    public override var syntax: Syntax by MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }
    public override var edition: String by MsgFieldDelegate { "" }

    @InternalRpcApi
    public val _presence: EnumPresence = object : EnumPresence {
        public override val hasSourceContext: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + enumvalue.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + if (presenceMask[0]) sourceContext.hashCode() else 0
        result = 31 * result + syntax.hashCode()
        result = 31 * result + edition.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (this.name != other.name) return false
        if (this.enumvalue != other.enumvalue) return false
        if (this.options != other.options) return false
        if (presenceMask[0] && this.sourceContext != other.sourceContext) return false
        if (this.syntax != other.syntax) return false
        if (this.edition != other.edition) return false
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
        builder.appendLine("Enum(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}enumvalue=${this.enumvalue},")
        builder.appendLine("${nextIndentString}options=${this.options},")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}sourceContext=${this.sourceContext.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}sourceContext=<unset>,")
        }

        builder.appendLine("${nextIndentString}syntax=${this.syntax},")
        builder.appendLine("${nextIndentString}edition=${this.edition},")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    public fun copyInternal(body: EnumInternal.() -> Unit): EnumInternal {
        val copy = EnumInternal()
        copy.name = this.name
        copy.enumvalue = this.enumvalue.map { it.copy() }
        copy.options = this.options.map { it.copy() }
        if (presenceMask[0]) {
            copy.sourceContext = this.sourceContext.copy()
        }

        copy.syntax = this.syntax
        copy.edition = this.edition
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<Enum> {
        public override fun encode(value: Enum, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Enum {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = EnumInternal()
                checkForPlatformDecodeException {
                    EnumInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Enum> {
        public override val fullName: String = "google.protobuf.Enum"
    }

    @InternalRpcApi
    public companion object
}

public class EnumValueInternal: EnumValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var number: Int by MsgFieldDelegate { 0 }
    public override var options: List<Option> by MsgFieldDelegate { mutableListOf() }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + options.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumValueInternal
        other.checkRequiredFields()
        if (this.name != other.name) return false
        if (this.number != other.number) return false
        if (this.options != other.options) return false
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
        builder.appendLine("EnumValue(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}number=${this.number},")
        builder.appendLine("${nextIndentString}options=${this.options},")
        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    public fun copyInternal(body: EnumValueInternal.() -> Unit): EnumValueInternal {
        val copy = EnumValueInternal()
        copy.name = this.name
        copy.number = this.number
        copy.options = this.options.map { it.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<EnumValue> {
        public override fun encode(value: EnumValue, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): EnumValue {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = EnumValueInternal()
                checkForPlatformDecodeException {
                    EnumValueInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<EnumValue> {
        public override val fullName: String = "google.protobuf.EnumValue"
    }

    @InternalRpcApi
    public companion object
}

public class OptionInternal: Option.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        public const val value: Int = 0
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var value: Any by MsgFieldDelegate(PresenceIndices.value) { AnyInternal() }

    @InternalRpcApi
    public val _presence: OptionPresence = object : OptionPresence {
        public override val hasValue: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + if (presenceMask[0]) value.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as OptionInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (this.name != other.name) return false
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
        builder.appendLine("Option(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}value=${this.value.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}value=<unset>,")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    @InternalRpcApi
    public fun copyInternal(body: OptionInternal.() -> Unit): OptionInternal {
        val copy = OptionInternal()
        copy.name = this.name
        if (presenceMask[0]) {
            copy.value = this.value.copy()
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object MARSHALLER: MessageMarshaller<Option> {
        public override fun encode(value: Option, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Option {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = OptionInternal()
                checkForPlatformDecodeException {
                    OptionInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Option> {
        public override val fullName: String = "google.protobuf.Option"
    }

    @InternalRpcApi
    public companion object
}

@InternalRpcApi
public fun TypeInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        this.sourceContext.asInternal().checkRequiredFields()
    }

    this.fields.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.options.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun TypeInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.fields.isNotEmpty()) {
        this.fields.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.oneofs.isNotEmpty()) {
        this.oneofs.forEach {
            encoder.writeString(3, it)
        }
    }

    if (this.options.isNotEmpty()) {
        this.options.forEach {
            encoder.writeMessage(fieldNr = 4, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 5, value = this.sourceContext.asInternal()) { encodeWith(it, config) }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        encoder.writeEnum(fieldNr = 6, value = this.syntax.number)
    }

    if (this.edition.isNotEmpty()) {
        encoder.writeString(fieldNr = 7, value = this.edition)
    }
}

@InternalRpcApi
public fun TypeInternal.Companion.decodeWith(msg: TypeInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = FieldInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> FieldInternal.decodeWith(msg, decoder, config) })
                (msg.fields as MutableList).add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = decoder.readString()
                (msg.oneofs as MutableList).add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 5 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.sourceContext = SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), { msg, decoder -> SourceContextInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 6 && tag.wireType == WireType.VARINT -> {
                msg.syntax = Syntax.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 7 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.edition = decoder.readString()
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

private fun TypeInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.fields.isNotEmpty()) {
        __result += this.fields.sumOf { it.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.oneofs.isNotEmpty()) {
        __result += this.oneofs.sumOf { WireSize.string(it).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.options.isNotEmpty()) {
        __result += this.options.sumOf { it.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[0]) {
        __result += this.sourceContext.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        __result += (WireSize.tag(6, WireType.VARINT) + WireSize.enum(this.syntax.number))
    }

    if (this.edition.isNotEmpty()) {
        __result += WireSize.string(this.edition).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun Type.asInternal(): TypeInternal {
    return this as? TypeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun FieldInternal.checkRequiredFields() {
    // no required fields to check
    this.options.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun FieldInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.kind != Field.Kind.TYPE_UNKNOWN) {
        encoder.writeEnum(fieldNr = 1, value = this.kind.number)
    }

    if (this.cardinality != Field.Cardinality.CARDINALITY_UNKNOWN) {
        encoder.writeEnum(fieldNr = 2, value = this.cardinality.number)
    }

    if (this.number != 0) {
        encoder.writeInt32(fieldNr = 3, value = this.number)
    }

    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 4, value = this.name)
    }

    if (this.typeUrl.isNotEmpty()) {
        encoder.writeString(fieldNr = 6, value = this.typeUrl)
    }

    if (this.oneofIndex != 0) {
        encoder.writeInt32(fieldNr = 7, value = this.oneofIndex)
    }

    if (this.packed != false) {
        encoder.writeBool(fieldNr = 8, value = this.packed)
    }

    if (this.options.isNotEmpty()) {
        this.options.forEach {
            encoder.writeMessage(fieldNr = 9, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.jsonName.isNotEmpty()) {
        encoder.writeString(fieldNr = 10, value = this.jsonName)
    }

    if (this.defaultValue.isNotEmpty()) {
        encoder.writeString(fieldNr = 11, value = this.defaultValue)
    }
}

@InternalRpcApi
public fun FieldInternal.Companion.decodeWith(msg: FieldInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.kind = Field.Kind.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.cardinality = Field.Cardinality.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 3 && tag.wireType == WireType.VARINT -> {
                msg.number = decoder.readInt32()
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.typeUrl = decoder.readString()
            }
            tag.fieldNr == 7 && tag.wireType == WireType.VARINT -> {
                msg.oneofIndex = decoder.readInt32()
            }
            tag.fieldNr == 8 && tag.wireType == WireType.VARINT -> {
                msg.packed = decoder.readBool()
            }
            tag.fieldNr == 9 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 10 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.jsonName = decoder.readString()
            }
            tag.fieldNr == 11 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.defaultValue = decoder.readString()
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

private fun FieldInternal.computeSize(): Int {
    var __result = 0
    if (this.kind != Field.Kind.TYPE_UNKNOWN) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.enum(this.kind.number))
    }

    if (this.cardinality != Field.Cardinality.CARDINALITY_UNKNOWN) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.enum(this.cardinality.number))
    }

    if (this.number != 0) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.int32(this.number))
    }

    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.typeUrl.isNotEmpty()) {
        __result += WireSize.string(this.typeUrl).let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.oneofIndex != 0) {
        __result += (WireSize.tag(7, WireType.VARINT) + WireSize.int32(this.oneofIndex))
    }

    if (this.packed != false) {
        __result += (WireSize.tag(8, WireType.VARINT) + WireSize.bool(this.packed))
    }

    if (this.options.isNotEmpty()) {
        __result += this.options.sumOf { it.asInternal()._size.let { WireSize.tag(9, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.jsonName.isNotEmpty()) {
        __result += WireSize.string(this.jsonName).let { WireSize.tag(10, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.defaultValue.isNotEmpty()) {
        __result += WireSize.string(this.defaultValue).let { WireSize.tag(11, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun Field.asInternal(): FieldInternal {
    return this as? FieldInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun EnumInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        this.sourceContext.asInternal().checkRequiredFields()
    }

    this.enumvalue.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.options.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun EnumInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.enumvalue.isNotEmpty()) {
        this.enumvalue.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.options.isNotEmpty()) {
        this.options.forEach {
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 4, value = this.sourceContext.asInternal()) { encodeWith(it, config) }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        encoder.writeEnum(fieldNr = 5, value = this.syntax.number)
    }

    if (this.edition.isNotEmpty()) {
        encoder.writeString(fieldNr = 6, value = this.edition)
    }
}

@InternalRpcApi
public fun EnumInternal.Companion.decodeWith(msg: EnumInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = EnumValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> EnumValueInternal.decodeWith(msg, decoder, config) })
                (msg.enumvalue as MutableList).add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.sourceContext = SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), { msg, decoder -> SourceContextInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 5 && tag.wireType == WireType.VARINT -> {
                msg.syntax = Syntax.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.edition = decoder.readString()
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

private fun EnumInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.enumvalue.isNotEmpty()) {
        __result += this.enumvalue.sumOf { it.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.options.isNotEmpty()) {
        __result += this.options.sumOf { it.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (presenceMask[0]) {
        __result += this.sourceContext.asInternal()._size.let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.enum(this.syntax.number))
    }

    if (this.edition.isNotEmpty()) {
        __result += WireSize.string(this.edition).let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun Enum.asInternal(): EnumInternal {
    return this as? EnumInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun EnumValueInternal.checkRequiredFields() {
    // no required fields to check
    this.options.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun EnumValueInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.number != 0) {
        encoder.writeInt32(fieldNr = 2, value = this.number)
    }

    if (this.options.isNotEmpty()) {
        this.options.forEach {
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it, config) }
        }
    }
}

@InternalRpcApi
public fun EnumValueInternal.Companion.decodeWith(msg: EnumValueInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.VARINT -> {
                msg.number = decoder.readInt32()
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                (msg.options as MutableList).add(elem)
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

private fun EnumValueInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.number != 0) {
        __result += (WireSize.tag(2, WireType.VARINT) + WireSize.int32(this.number))
    }

    if (this.options.isNotEmpty()) {
        __result += this.options.sumOf { it.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    return __result
}

@InternalRpcApi
public fun EnumValue.asInternal(): EnumValueInternal {
    return this as? EnumValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun OptionInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        this.value.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun OptionInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
    }
}

@InternalRpcApi
public fun OptionInternal.Companion.decodeWith(msg: OptionInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.value = AnyInternal()
                }

                decoder.readMessage(msg.value.asInternal(), { msg, decoder -> AnyInternal.decodeWith(msg, decoder, config) })
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

private fun OptionInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun Option.asInternal(): OptionInternal {
    return this as? OptionInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun Syntax.Companion.fromNumber(number: Int): Syntax {
    return when (number) {
        0 -> {
            Syntax.SYNTAX_PROTO2
        }
        1 -> {
            Syntax.SYNTAX_PROTO3
        }
        2 -> {
            Syntax.SYNTAX_EDITIONS
        }
        else -> {
            Syntax.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun Field.Kind.Companion.fromNumber(number: Int): Field.Kind {
    return when (number) {
        0 -> {
            Field.Kind.TYPE_UNKNOWN
        }
        1 -> {
            Field.Kind.TYPE_DOUBLE
        }
        2 -> {
            Field.Kind.TYPE_FLOAT
        }
        3 -> {
            Field.Kind.TYPE_INT64
        }
        4 -> {
            Field.Kind.TYPE_UINT64
        }
        5 -> {
            Field.Kind.TYPE_INT32
        }
        6 -> {
            Field.Kind.TYPE_FIXED64
        }
        7 -> {
            Field.Kind.TYPE_FIXED32
        }
        8 -> {
            Field.Kind.TYPE_BOOL
        }
        9 -> {
            Field.Kind.TYPE_STRING
        }
        10 -> {
            Field.Kind.TYPE_GROUP
        }
        11 -> {
            Field.Kind.TYPE_MESSAGE
        }
        12 -> {
            Field.Kind.TYPE_BYTES
        }
        13 -> {
            Field.Kind.TYPE_UINT32
        }
        14 -> {
            Field.Kind.TYPE_ENUM
        }
        15 -> {
            Field.Kind.TYPE_SFIXED32
        }
        16 -> {
            Field.Kind.TYPE_SFIXED64
        }
        17 -> {
            Field.Kind.TYPE_SINT32
        }
        18 -> {
            Field.Kind.TYPE_SINT64
        }
        else -> {
            Field.Kind.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
public fun Field.Cardinality.Companion.fromNumber(number: Int): Field.Cardinality {
    return when (number) {
        0 -> {
            Field.Cardinality.CARDINALITY_UNKNOWN
        }
        1 -> {
            Field.Cardinality.CARDINALITY_OPTIONAL
        }
        2 -> {
            Field.Cardinality.CARDINALITY_REQUIRED
        }
        3 -> {
            Field.Cardinality.CARDINALITY_REPEATED
        }
        else -> {
            Field.Cardinality.UNRECOGNIZED(number)
        }
    }
}
