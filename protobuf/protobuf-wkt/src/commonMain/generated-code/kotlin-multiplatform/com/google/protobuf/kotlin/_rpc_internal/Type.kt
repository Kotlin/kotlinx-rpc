@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import com.google.protobuf.kotlin.asInternal
import com.google.protobuf.kotlin.checkRequiredFields
import com.google.protobuf.kotlin.copy
import com.google.protobuf.kotlin.decodeWith
import com.google.protobuf.kotlin.encodeWith
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
import kotlinx.rpc.protobuf.internal.InternalPresenceObject
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
import kotlinx.rpc.protobuf.internal.protoToString
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

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __fieldsDelegate: MsgFieldDelegate<List<Field>> = MsgFieldDelegate { emptyList() }
    public override var fields: List<Field> by __fieldsDelegate
    internal val __oneofsDelegate: MsgFieldDelegate<List<String>> = MsgFieldDelegate { emptyList() }
    public override var oneofs: List<String> by __oneofsDelegate
    internal val __optionsDelegate: MsgFieldDelegate<List<Option>> = MsgFieldDelegate { emptyList() }
    public override var options: List<Option> by __optionsDelegate
    internal val __sourceContextDelegate: MsgFieldDelegate<SourceContext> = MsgFieldDelegate(PresenceIndices.sourceContext) { SourceContextInternal.DEFAULT }
    public override var sourceContext: SourceContext by __sourceContextDelegate
    internal val __syntaxDelegate: MsgFieldDelegate<Syntax> = MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }
    public override var syntax: Syntax by __syntaxDelegate
    internal val __editionDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var edition: String by __editionDelegate

    private val _owner: TypeInternal = this

    @InternalRpcApi
    public val _presence: TypePresence = object : TypePresence, InternalPresenceObject {
        public override val _message: TypeInternal get() = _owner

        public override val hasSourceContext: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + this.fields.hashCode()
        result = 31 * result + this.oneofs.hashCode()
        result = 31 * result + this.options.hashCode()
        result = 31 * result + if (presenceMask[0]) this.sourceContext.hashCode() else 0
        result = 31 * result + this.syntax.hashCode()
        result = 31 * result + this.edition.hashCode()
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

    public override fun copyInternal(): TypeInternal {
        return copyInternal { }
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
    public object MARSHALLER: GrpcMarshaller<Type> {
        public override fun encode(value: Type, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Type {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TypeInternal()
                checkForPlatformDecodeException {
                    TypeInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Type> {
        public override val fullName: String = "google.protobuf.Type"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Type by lazy { TypeInternal() }
    }
}

public class FieldInternal: Field.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __kindDelegate: MsgFieldDelegate<Field.Kind> = MsgFieldDelegate { Field.Kind.TYPE_UNKNOWN }
    public override var kind: Field.Kind by __kindDelegate
    internal val __cardinalityDelegate: MsgFieldDelegate<Field.Cardinality> = MsgFieldDelegate { Field.Cardinality.CARDINALITY_UNKNOWN }
    public override var cardinality: Field.Cardinality by __cardinalityDelegate
    internal val __numberDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate { 0 }
    public override var number: Int by __numberDelegate
    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __typeUrlDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var typeUrl: String by __typeUrlDelegate
    internal val __oneofIndexDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate { 0 }
    public override var oneofIndex: Int by __oneofIndexDelegate
    internal val __packedDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate { false }
    public override var packed: Boolean by __packedDelegate
    internal val __optionsDelegate: MsgFieldDelegate<List<Option>> = MsgFieldDelegate { emptyList() }
    public override var options: List<Option> by __optionsDelegate
    internal val __jsonNameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var jsonName: String by __jsonNameDelegate
    internal val __defaultValueDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var defaultValue: String by __defaultValueDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.kind.hashCode()
        result = 31 * result + this.cardinality.hashCode()
        result = 31 * result + this.number.hashCode()
        result = 31 * result + this.name.hashCode()
        result = 31 * result + this.typeUrl.hashCode()
        result = 31 * result + this.oneofIndex.hashCode()
        result = 31 * result + this.packed.hashCode()
        result = 31 * result + this.options.hashCode()
        result = 31 * result + this.jsonName.hashCode()
        result = 31 * result + this.defaultValue.hashCode()
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

    public override fun copyInternal(): FieldInternal {
        return copyInternal { }
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
    public object MARSHALLER: GrpcMarshaller<Field> {
        public override fun encode(value: Field, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Field {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = FieldInternal()
                checkForPlatformDecodeException {
                    FieldInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Field> {
        public override val fullName: String = "google.protobuf.Field"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Field by lazy { FieldInternal() }
    }
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

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __enumvalueDelegate: MsgFieldDelegate<List<EnumValue>> = MsgFieldDelegate { emptyList() }
    public override var enumvalue: List<EnumValue> by __enumvalueDelegate
    internal val __optionsDelegate: MsgFieldDelegate<List<Option>> = MsgFieldDelegate { emptyList() }
    public override var options: List<Option> by __optionsDelegate
    internal val __sourceContextDelegate: MsgFieldDelegate<SourceContext> = MsgFieldDelegate(PresenceIndices.sourceContext) { SourceContextInternal.DEFAULT }
    public override var sourceContext: SourceContext by __sourceContextDelegate
    internal val __syntaxDelegate: MsgFieldDelegate<Syntax> = MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }
    public override var syntax: Syntax by __syntaxDelegate
    internal val __editionDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var edition: String by __editionDelegate

    private val _owner: EnumInternal = this

    @InternalRpcApi
    public val _presence: EnumPresence = object : EnumPresence, InternalPresenceObject {
        public override val _message: EnumInternal get() = _owner

        public override val hasSourceContext: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + this.enumvalue.hashCode()
        result = 31 * result + this.options.hashCode()
        result = 31 * result + if (presenceMask[0]) this.sourceContext.hashCode() else 0
        result = 31 * result + this.syntax.hashCode()
        result = 31 * result + this.edition.hashCode()
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

    public override fun copyInternal(): EnumInternal {
        return copyInternal { }
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
    public object MARSHALLER: GrpcMarshaller<Enum> {
        public override fun encode(value: Enum, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Enum {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = EnumInternal()
                checkForPlatformDecodeException {
                    EnumInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Enum> {
        public override val fullName: String = "google.protobuf.Enum"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Enum by lazy { EnumInternal() }
    }
}

public class EnumValueInternal: EnumValue.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __numberDelegate: MsgFieldDelegate<Int> = MsgFieldDelegate { 0 }
    public override var number: Int by __numberDelegate
    internal val __optionsDelegate: MsgFieldDelegate<List<Option>> = MsgFieldDelegate { emptyList() }
    public override var options: List<Option> by __optionsDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + this.number.hashCode()
        result = 31 * result + this.options.hashCode()
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

    public override fun copyInternal(): EnumValueInternal {
        return copyInternal { }
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
    public object MARSHALLER: GrpcMarshaller<EnumValue> {
        public override fun encode(value: EnumValue, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): EnumValue {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = EnumValueInternal()
                checkForPlatformDecodeException {
                    EnumValueInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<EnumValue> {
        public override val fullName: String = "google.protobuf.EnumValue"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: EnumValue by lazy { EnumValueInternal() }
    }
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

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __valueDelegate: MsgFieldDelegate<Any> = MsgFieldDelegate(PresenceIndices.value) { AnyInternal.DEFAULT }
    public override var value: Any by __valueDelegate

    private val _owner: OptionInternal = this

    @InternalRpcApi
    public val _presence: OptionPresence = object : OptionPresence, InternalPresenceObject {
        public override val _message: OptionInternal get() = _owner

        public override val hasValue: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + if (presenceMask[0]) this.value.hashCode() else 0
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

    public override fun copyInternal(): OptionInternal {
        return copyInternal { }
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
    public object MARSHALLER: GrpcMarshaller<Option> {
        public override fun encode(value: Option, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Option {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = OptionInternal()
                checkForPlatformDecodeException {
                    OptionInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Option> {
        public override val fullName: String = "google.protobuf.Option"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Option by lazy { OptionInternal() }
    }
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
public fun TypeInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun TypeInternal.Companion.decodeWith(msg: TypeInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__fieldsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = FieldInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> FieldInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__oneofsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = decoder.readString()
                target.add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 5 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__sourceContextDelegate.getOrCreate(msg) { SourceContextInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> SourceContextInternal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
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

    __result += _unknownFields.size.toInt()
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
public fun FieldInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun FieldInternal.Companion.decodeWith(msg: FieldInternal, decoder: WireDecoder, config: ProtoConfig?) {
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
                val target = msg.__optionsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
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

    __result += _unknownFields.size.toInt()
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
public fun EnumInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EnumInternal.Companion.decodeWith(msg: EnumInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__enumvalueDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = EnumValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> EnumValueInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__sourceContextDelegate.getOrCreate(msg) { SourceContextInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> SourceContextInternal.decodeWith(msg, decoder, config) })
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
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

    __result += _unknownFields.size.toInt()
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
public fun EnumValueInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun EnumValueInternal.Companion.decodeWith(msg: EnumValueInternal, decoder: WireDecoder, config: ProtoConfig?) {
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
                val target = msg.__optionsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
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

    __result += _unknownFields.size.toInt()
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
public fun OptionInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 2, value = this.value.asInternal()) { encodeWith(it, config) }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun OptionInternal.Companion.decodeWith(msg: OptionInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__valueDelegate.getOrCreate(msg) { AnyInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> AnyInternal.decodeWith(msg, decoder, config) })
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

private fun OptionInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += this.value.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
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
