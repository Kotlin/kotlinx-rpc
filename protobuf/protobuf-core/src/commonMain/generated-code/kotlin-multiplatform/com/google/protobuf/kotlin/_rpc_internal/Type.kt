@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.internal.*

public class TypeInternal: com.google.protobuf.kotlin.Type, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        public const val sourceContext: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var fields: List<com.google.protobuf.kotlin.Field> by MsgFieldDelegate { mutableListOf() }
    public override var oneofs: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }
    public override var sourceContext: com.google.protobuf.kotlin.SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { com.google.protobuf.kotlin.SourceContextInternal() }
    public override var syntax: com.google.protobuf.kotlin.Syntax by MsgFieldDelegate { com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 }
    public override var edition: String by MsgFieldDelegate { "" }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public val _presence: com.google.protobuf.kotlin.TypePresence = object : com.google.protobuf.kotlin.TypePresence { 
        public override val hasSourceContext: kotlin.Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): kotlin.Int { 
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

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TypeInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (name != other.name) return false
        if (fields != other.fields) return false
        if (oneofs != other.oneofs) return false
        if (options != other.options) return false
        if (presenceMask[0] && sourceContext != other.sourceContext) return false
        if (syntax != other.syntax) return false
        if (edition != other.edition) return false
        return true
    }

    public override fun toString(): kotlin.String { 
        return asString()
    }

    public fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf.kotlin.Type(")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}fields=${fields},")
            appendLine("${nextIndentString}oneofs=${oneofs},")
            appendLine("${nextIndentString}options=${options},")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}sourceContext=${sourceContext.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}sourceContext=<unset>,")
            }

            appendLine("${nextIndentString}syntax=${syntax},")
            appendLine("${nextIndentString}edition=${edition},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.TypeInternal.() -> Unit): com.google.protobuf.kotlin.TypeInternal { 
        val copy = com.google.protobuf.kotlin.TypeInternal()
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

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Type> { 
        public override fun encode(value: com.google.protobuf.kotlin.Type, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder, config as? kotlinx.rpc.protobuf.ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Type { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.TypeInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.TypeInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public class FieldInternal: com.google.protobuf.kotlin.Field, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var kind: com.google.protobuf.kotlin.Field.Kind by MsgFieldDelegate { com.google.protobuf.kotlin.Field.Kind.TYPE_UNKNOWN }
    public override var cardinality: com.google.protobuf.kotlin.Field.Cardinality by MsgFieldDelegate { com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_UNKNOWN }
    public override var number: Int by MsgFieldDelegate { 0 }
    public override var name: String by MsgFieldDelegate { "" }
    public override var typeUrl: String by MsgFieldDelegate { "" }
    public override var oneofIndex: Int by MsgFieldDelegate { 0 }
    public override var packed: Boolean by MsgFieldDelegate { false }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }
    public override var jsonName: String by MsgFieldDelegate { "" }
    public override var defaultValue: String by MsgFieldDelegate { "" }

    public override fun hashCode(): kotlin.Int { 
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

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FieldInternal
        other.checkRequiredFields()
        if (kind != other.kind) return false
        if (cardinality != other.cardinality) return false
        if (number != other.number) return false
        if (name != other.name) return false
        if (typeUrl != other.typeUrl) return false
        if (oneofIndex != other.oneofIndex) return false
        if (packed != other.packed) return false
        if (options != other.options) return false
        if (jsonName != other.jsonName) return false
        if (defaultValue != other.defaultValue) return false
        return true
    }

    public override fun toString(): kotlin.String { 
        return asString()
    }

    public fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf.kotlin.Field(")
            appendLine("${nextIndentString}kind=${kind},")
            appendLine("${nextIndentString}cardinality=${cardinality},")
            appendLine("${nextIndentString}number=${number},")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}typeUrl=${typeUrl},")
            appendLine("${nextIndentString}oneofIndex=${oneofIndex},")
            appendLine("${nextIndentString}packed=${packed},")
            appendLine("${nextIndentString}options=${options},")
            appendLine("${nextIndentString}jsonName=${jsonName},")
            appendLine("${nextIndentString}defaultValue=${defaultValue},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.FieldInternal.() -> Unit): com.google.protobuf.kotlin.FieldInternal { 
        val copy = com.google.protobuf.kotlin.FieldInternal()
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

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Field> { 
        public override fun encode(value: com.google.protobuf.kotlin.Field, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder, config as? kotlinx.rpc.protobuf.ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Field { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.FieldInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.FieldInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public class EnumInternal: com.google.protobuf.kotlin.Enum, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        public const val sourceContext: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var enumvalue: List<com.google.protobuf.kotlin.EnumValue> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }
    public override var sourceContext: com.google.protobuf.kotlin.SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { com.google.protobuf.kotlin.SourceContextInternal() }
    public override var syntax: com.google.protobuf.kotlin.Syntax by MsgFieldDelegate { com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 }
    public override var edition: String by MsgFieldDelegate { "" }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public val _presence: com.google.protobuf.kotlin.EnumPresence = object : com.google.protobuf.kotlin.EnumPresence { 
        public override val hasSourceContext: kotlin.Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + enumvalue.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + if (presenceMask[0]) sourceContext.hashCode() else 0
        result = 31 * result + syntax.hashCode()
        result = 31 * result + edition.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (name != other.name) return false
        if (enumvalue != other.enumvalue) return false
        if (options != other.options) return false
        if (presenceMask[0] && sourceContext != other.sourceContext) return false
        if (syntax != other.syntax) return false
        if (edition != other.edition) return false
        return true
    }

    public override fun toString(): kotlin.String { 
        return asString()
    }

    public fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf.kotlin.Enum(")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}enumvalue=${enumvalue},")
            appendLine("${nextIndentString}options=${options},")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}sourceContext=${sourceContext.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}sourceContext=<unset>,")
            }

            appendLine("${nextIndentString}syntax=${syntax},")
            appendLine("${nextIndentString}edition=${edition},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.EnumInternal.() -> Unit): com.google.protobuf.kotlin.EnumInternal { 
        val copy = com.google.protobuf.kotlin.EnumInternal()
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

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Enum> { 
        public override fun encode(value: com.google.protobuf.kotlin.Enum, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder, config as? kotlinx.rpc.protobuf.ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Enum { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.EnumInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.EnumInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public class EnumValueInternal: com.google.protobuf.kotlin.EnumValue, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var number: Int by MsgFieldDelegate { 0 }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + options.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EnumValueInternal
        other.checkRequiredFields()
        if (name != other.name) return false
        if (number != other.number) return false
        if (options != other.options) return false
        return true
    }

    public override fun toString(): kotlin.String { 
        return asString()
    }

    public fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf.kotlin.EnumValue(")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}number=${number},")
            appendLine("${nextIndentString}options=${options},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.EnumValueInternal.() -> Unit): com.google.protobuf.kotlin.EnumValueInternal { 
        val copy = com.google.protobuf.kotlin.EnumValueInternal()
        copy.name = this.name
        copy.number = this.number
        copy.options = this.options.map { it.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.EnumValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.EnumValue, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder, config as? kotlinx.rpc.protobuf.ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.EnumValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.EnumValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.EnumValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public class OptionInternal: com.google.protobuf.kotlin.Option, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        public const val value: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var value: com.google.protobuf.kotlin.Any by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf.kotlin.AnyInternal() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public val _presence: com.google.protobuf.kotlin.OptionPresence = object : com.google.protobuf.kotlin.OptionPresence { 
        public override val hasValue: kotlin.Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + if (presenceMask[0]) value.hashCode() else 0
        return result
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as OptionInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (name != other.name) return false
        if (presenceMask[0] && value != other.value) return false
        return true
    }

    public override fun toString(): kotlin.String { 
        return asString()
    }

    public fun asString(indent: kotlin.Int = 0): kotlin.String { 
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString { 
            appendLine("com.google.protobuf.kotlin.Option(")
            appendLine("${nextIndentString}name=${name},")
            if (presenceMask[0]) { 
                appendLine("${nextIndentString}value=${value.asInternal().asString(indent = indent + 4)},")
            } else { 
                appendLine("${nextIndentString}value=<unset>,")
            }

            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.OptionInternal.() -> Unit): com.google.protobuf.kotlin.OptionInternal { 
        val copy = com.google.protobuf.kotlin.OptionInternal()
        copy.name = this.name
        if (presenceMask[0]) { 
            copy.value = this.value.copy()
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Option> { 
        public override fun encode(value: com.google.protobuf.kotlin.Option, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder, config as? kotlinx.rpc.protobuf.ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Option { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.OptionInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.OptionInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.TypeInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        sourceContext.asInternal().checkRequiredFields()
    }

    fields.forEach { 
        it.asInternal().checkRequiredFields()
    }

    options.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.TypeInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (fields.isNotEmpty()) { 
        fields.forEach { 
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (oneofs.isNotEmpty()) { 
        oneofs.forEach { 
            encoder.writeString(3, it)
        }
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 4, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 5, value = sourceContext.asInternal()) { encodeWith(it, config) }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        encoder.writeEnum(fieldNr = 6, value = syntax.number)
    }

    if (edition.isNotEmpty()) { 
        encoder.writeString(fieldNr = 7, value = edition)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.TypeInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.TypeInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.FieldInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.FieldInternal.decodeWith( msg, decoder, config ) })
                (msg.fields as MutableList).add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readString()
                (msg.oneofs as MutableList).add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.OptionInternal.decodeWith( msg, decoder, config ) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.sourceContext = com.google.protobuf.kotlin.SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.SourceContextInternal.decodeWith( msg, decoder, config ) })
            }
            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.syntax = com.google.protobuf.kotlin.Syntax.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.edition = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun com.google.protobuf.kotlin.TypeInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (fields.isNotEmpty()) { 
        __result += fields.sumOf { it.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it } }
    }

    if (oneofs.isNotEmpty()) { 
        __result += oneofs.sumOf { kotlinx.rpc.protobuf.internal.WireSize.string(it).let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it } }
    }

    if (options.isNotEmpty()) { 
        __result += options.sumOf { it.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it } }
    }

    if (presenceMask[0]) { 
        __result += sourceContext.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(syntax.number))
    }

    if (edition.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(edition).let { kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Type.asInternal(): com.google.protobuf.kotlin.TypeInternal { 
    return this as? com.google.protobuf.kotlin.TypeInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldInternal.checkRequiredFields() { 
    // no required fields to check
    options.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (kind != com.google.protobuf.kotlin.Field.Kind.TYPE_UNKNOWN) { 
        encoder.writeEnum(fieldNr = 1, value = kind.number)
    }

    if (cardinality != com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_UNKNOWN) { 
        encoder.writeEnum(fieldNr = 2, value = cardinality.number)
    }

    if (number != 0) { 
        encoder.writeInt32(fieldNr = 3, value = number)
    }

    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 4, value = name)
    }

    if (typeUrl.isNotEmpty()) { 
        encoder.writeString(fieldNr = 6, value = typeUrl)
    }

    if (oneofIndex != 0) { 
        encoder.writeInt32(fieldNr = 7, value = oneofIndex)
    }

    if (packed != false) { 
        encoder.writeBool(fieldNr = 8, value = packed)
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 9, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (jsonName.isNotEmpty()) { 
        encoder.writeString(fieldNr = 10, value = jsonName)
    }

    if (defaultValue.isNotEmpty()) { 
        encoder.writeString(fieldNr = 11, value = defaultValue)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.FieldInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.kind = com.google.protobuf.kotlin.Field.Kind.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.cardinality = com.google.protobuf.kotlin.Field.Cardinality.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.number = decoder.readInt32()
            }
            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }
            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.typeUrl = decoder.readString()
            }
            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.oneofIndex = decoder.readInt32()
            }
            tag.fieldNr == 8 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.packed = decoder.readBool()
            }
            tag.fieldNr == 9 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.OptionInternal.decodeWith( msg, decoder, config ) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 10 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.jsonName = decoder.readString()
            }
            tag.fieldNr == 11 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.defaultValue = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun com.google.protobuf.kotlin.FieldInternal.computeSize(): Int { 
    var __result = 0
    if (kind != com.google.protobuf.kotlin.Field.Kind.TYPE_UNKNOWN) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(kind.number))
    }

    if (cardinality != com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_UNKNOWN) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(cardinality.number))
    }

    if (number != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(number))
    }

    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (typeUrl.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(typeUrl).let { kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (oneofIndex != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(oneofIndex))
    }

    if (packed != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(8, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(packed))
    }

    if (options.isNotEmpty()) { 
        __result += options.sumOf { it.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it } }
    }

    if (jsonName.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(jsonName).let { kotlinx.rpc.protobuf.internal.WireSize.tag(10, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (defaultValue.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(defaultValue).let { kotlinx.rpc.protobuf.internal.WireSize.tag(11, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Field.asInternal(): com.google.protobuf.kotlin.FieldInternal { 
    return this as? com.google.protobuf.kotlin.FieldInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        sourceContext.asInternal().checkRequiredFields()
    }

    enumvalue.forEach { 
        it.asInternal().checkRequiredFields()
    }

    options.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (enumvalue.isNotEmpty()) { 
        enumvalue.forEach { 
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 4, value = sourceContext.asInternal()) { encodeWith(it, config) }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        encoder.writeEnum(fieldNr = 5, value = syntax.number)
    }

    if (edition.isNotEmpty()) { 
        encoder.writeString(fieldNr = 6, value = edition)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.EnumInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.EnumValueInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.EnumValueInternal.decodeWith( msg, decoder, config ) })
                (msg.enumvalue as MutableList).add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.OptionInternal.decodeWith( msg, decoder, config ) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.sourceContext = com.google.protobuf.kotlin.SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.SourceContextInternal.decodeWith( msg, decoder, config ) })
            }
            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.syntax = com.google.protobuf.kotlin.Syntax.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.edition = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun com.google.protobuf.kotlin.EnumInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (enumvalue.isNotEmpty()) { 
        __result += enumvalue.sumOf { it.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it } }
    }

    if (options.isNotEmpty()) { 
        __result += options.sumOf { it.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it } }
    }

    if (presenceMask[0]) { 
        __result += sourceContext.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(syntax.number))
    }

    if (edition.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(edition).let { kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Enum.asInternal(): com.google.protobuf.kotlin.EnumInternal { 
    return this as? com.google.protobuf.kotlin.EnumInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumValueInternal.checkRequiredFields() { 
    // no required fields to check
    options.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (number != 0) { 
        encoder.writeInt32(fieldNr = 2, value = number)
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it, config) }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.EnumValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.number = decoder.readInt32()
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.OptionInternal.decodeWith( msg, decoder, config ) })
                (msg.options as MutableList).add(elem)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun com.google.protobuf.kotlin.EnumValueInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (number != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(number))
    }

    if (options.isNotEmpty()) { 
        __result += options.sumOf { it.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it } }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumValue.asInternal(): com.google.protobuf.kotlin.EnumValueInternal { 
    return this as? com.google.protobuf.kotlin.EnumValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.OptionInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.OptionInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it, config) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.OptionInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.OptionInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.value = com.google.protobuf.kotlin.AnyInternal()
                }

                decoder.readMessage(msg.value.asInternal(), { msg, decoder -> com.google.protobuf.kotlin.AnyInternal.decodeWith( msg, decoder, config ) })
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun com.google.protobuf.kotlin.OptionInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[0]) { 
        __result += value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Option.asInternal(): com.google.protobuf.kotlin.OptionInternal { 
    return this as? com.google.protobuf.kotlin.OptionInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Syntax.Companion.fromNumber(number: Int): com.google.protobuf.kotlin.Syntax { 
    return when (number) { 
        0 -> { 
            com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2
        }
        1 -> { 
            com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO3
        }
        2 -> { 
            com.google.protobuf.kotlin.Syntax.SYNTAX_EDITIONS
        }
        else -> { 
            com.google.protobuf.kotlin.Syntax.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Field.Kind.Companion.fromNumber(number: Int): com.google.protobuf.kotlin.Field.Kind { 
    return when (number) { 
        0 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_UNKNOWN
        }
        1 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_DOUBLE
        }
        2 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_FLOAT
        }
        3 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_INT64
        }
        4 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_UINT64
        }
        5 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_INT32
        }
        6 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_FIXED64
        }
        7 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_FIXED32
        }
        8 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_BOOL
        }
        9 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_STRING
        }
        10 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_GROUP
        }
        11 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_MESSAGE
        }
        12 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_BYTES
        }
        13 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_UINT32
        }
        14 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_ENUM
        }
        15 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_SFIXED32
        }
        16 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_SFIXED64
        }
        17 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_SINT32
        }
        18 -> { 
            com.google.protobuf.kotlin.Field.Kind.TYPE_SINT64
        }
        else -> { 
            com.google.protobuf.kotlin.Field.Kind.UNRECOGNIZED(number)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Field.Cardinality.Companion.fromNumber(number: Int): com.google.protobuf.kotlin.Field.Cardinality { 
    return when (number) { 
        0 -> { 
            com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_UNKNOWN
        }
        1 -> { 
            com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_OPTIONAL
        }
        2 -> { 
            com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_REQUIRED
        }
        3 -> { 
            com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_REPEATED
        }
        else -> { 
            com.google.protobuf.kotlin.Field.Cardinality.UNRECOGNIZED(number)
        }
    }
}
