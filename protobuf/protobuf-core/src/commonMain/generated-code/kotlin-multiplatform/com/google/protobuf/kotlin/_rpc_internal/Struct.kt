@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

public class StructInternal: com.google.protobuf.kotlin.Struct, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var fields: Map<kotlin.String, com.google.protobuf.kotlin.Value> by MsgFieldDelegate { mutableMapOf() }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return fields.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StructInternal
        other.checkRequiredFields()
        if (fields != other.fields) return false
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
            appendLine("com.google.protobuf.kotlin.Struct(")
            appendLine("${nextIndentString}fields=${fields},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.StructInternal.() -> Unit): com.google.protobuf.kotlin.StructInternal { 
        val copy = com.google.protobuf.kotlin.StructInternal()
        copy.fields = this.fields.mapValues { it.value.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    public class FieldsEntryInternal: kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
        private object PresenceIndices { 
            public const val value: Int = 0
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        public override val _size: Int by lazy { computeSize() }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        public override val _unknownFields: Buffer = Buffer()

        @kotlinx.rpc.internal.utils.InternalRpcApi
        internal var _unknownFieldsEncoder: WireEncoder? = null

        public var key: String by MsgFieldDelegate { "" }
        public var value: com.google.protobuf.kotlin.Value by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf.kotlin.ValueInternal() }

        public override fun hashCode(): kotlin.Int { 
            checkRequiredFields()
            var result = key.hashCode()
            result = 31 * result + if (presenceMask[0]) value.hashCode() else 0
            return result
        }

        public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
            checkRequiredFields()
            if (this === other) return true
            if (other == null || this::class != other::class) return false
            other as FieldsEntryInternal
            other.checkRequiredFields()
            if (presenceMask != other.presenceMask) return false
            if (key != other.key) return false
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
                appendLine("com.google.protobuf.kotlin.Struct.FieldsEntry(")
                appendLine("${nextIndentString}key=${key},")
                if (presenceMask[0]) { 
                    appendLine("${nextIndentString}value=${value.asInternal().asString(indent = indent + 4)},")
                } else { 
                    appendLine("${nextIndentString}value=<unset>,")
                }

                append("${indentString})")
            }
        }

        @kotlinx.rpc.internal.utils.InternalRpcApi
        public companion object
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Struct> { 
        public override fun encode(value: com.google.protobuf.kotlin.Struct, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Struct { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.StructInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.StructInternal.decodeWith(msg, it)
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

public class ValueInternal: com.google.protobuf.kotlin.Value, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var kind: com.google.protobuf.kotlin.Value.Kind? = null

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return (kind?.oneOfHashCode() ?: 0)
    }

    public fun com.google.protobuf.kotlin.Value.Kind.oneOfHashCode(): kotlin.Int { 
        val offset = when (this) { 
            is com.google.protobuf.kotlin.Value.Kind.NullValue -> 0
            is com.google.protobuf.kotlin.Value.Kind.NumberValue -> 1
            is com.google.protobuf.kotlin.Value.Kind.StringValue -> 2
            is com.google.protobuf.kotlin.Value.Kind.BoolValue -> 3
            is com.google.protobuf.kotlin.Value.Kind.StructValue -> 4
            is com.google.protobuf.kotlin.Value.Kind.ListValue -> 5
        }

        return hashCode() + offset
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ValueInternal
        other.checkRequiredFields()
        if (kind != other.kind) return false
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
            appendLine("com.google.protobuf.kotlin.Value(")
            appendLine("${nextIndentString}kind=${kind},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.ValueInternal.() -> Unit): com.google.protobuf.kotlin.ValueInternal { 
        val copy = com.google.protobuf.kotlin.ValueInternal()
        copy.kind = this.kind?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun com.google.protobuf.kotlin.Value.Kind.oneOfCopy(): com.google.protobuf.kotlin.Value.Kind { 
        return when (this) { 
            is com.google.protobuf.kotlin.Value.Kind.NullValue -> { 
                this
            }
            is com.google.protobuf.kotlin.Value.Kind.NumberValue -> { 
                this
            }
            is com.google.protobuf.kotlin.Value.Kind.StringValue -> { 
                this
            }
            is com.google.protobuf.kotlin.Value.Kind.BoolValue -> { 
                this
            }
            is com.google.protobuf.kotlin.Value.Kind.StructValue -> { 
                com.google.protobuf.kotlin.Value.Kind.StructValue(this.value.copy())
            }
            is com.google.protobuf.kotlin.Value.Kind.ListValue -> { 
                com.google.protobuf.kotlin.Value.Kind.ListValue(this.value.copy())
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Value> { 
        public override fun encode(value: com.google.protobuf.kotlin.Value, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Value { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.ValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.ValueInternal.decodeWith(msg, it)
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

public class ListValueInternal: com.google.protobuf.kotlin.ListValue, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var values: List<com.google.protobuf.kotlin.Value> by MsgFieldDelegate { mutableListOf() }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return values.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ListValueInternal
        other.checkRequiredFields()
        if (values != other.values) return false
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
            appendLine("com.google.protobuf.kotlin.ListValue(")
            appendLine("${nextIndentString}values=${values},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.ListValueInternal.() -> Unit): com.google.protobuf.kotlin.ListValueInternal { 
        val copy = com.google.protobuf.kotlin.ListValueInternal()
        copy.values = this.values.map { it.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.ListValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.ListValue, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.ListValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.ListValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.ListValueInternal.decodeWith(msg, it)
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
public fun com.google.protobuf.kotlin.StructInternal.checkRequiredFields() { 
    // no required fields to check
    fields.values.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StructInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (fields.isNotEmpty()) { 
        fields.forEach { kEntry ->
            com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            .also { entry ->
                encoder.writeMessage(fieldNr = 1, value = entry.asInternal()) { encodeWith(it) }
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StructInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.StructInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                with(com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal()) { 
                    decoder.readMessage(this.asInternal(), com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal::decodeWith)
                    (msg.fields as MutableMap)[key] = value
                }
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf.kotlin.StructInternal.computeSize(): Int { 
    var __result = 0
    if (fields.isNotEmpty()) { 
        __result += fields.entries.sumOf { kEntry ->
            com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal().apply { 
                key = kEntry.key
                value = kEntry.value
            }
            ._size
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Struct.asInternal(): com.google.protobuf.kotlin.StructInternal { 
    return this as? com.google.protobuf.kotlin.StructInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ValueInternal.checkRequiredFields() { 
    // no required fields to check
    kind?.also { 
        when { 
            it is com.google.protobuf.kotlin.Value.Kind.StructValue -> { 
                it.value.asInternal().checkRequiredFields()
            }
            it is com.google.protobuf.kotlin.Value.Kind.ListValue -> { 
                it.value.asInternal().checkRequiredFields()
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    kind?.also { 
        when (val value = it) { 
            is com.google.protobuf.kotlin.Value.Kind.NullValue -> { 
                encoder.writeEnum(fieldNr = 1, value = value.value.number)
            }
            is com.google.protobuf.kotlin.Value.Kind.NumberValue -> { 
                encoder.writeDouble(fieldNr = 2, value = value.value)
            }
            is com.google.protobuf.kotlin.Value.Kind.StringValue -> { 
                encoder.writeString(fieldNr = 3, value = value.value)
            }
            is com.google.protobuf.kotlin.Value.Kind.BoolValue -> { 
                encoder.writeBool(fieldNr = 4, value = value.value)
            }
            is com.google.protobuf.kotlin.Value.Kind.StructValue -> { 
                encoder.writeMessage(fieldNr = 5, value = value.value.asInternal()) { encodeWith(it) }
            }
            is com.google.protobuf.kotlin.Value.Kind.ListValue -> { 
                encoder.writeMessage(fieldNr = 6, value = value.value.asInternal()) { encodeWith(it) }
            }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.ValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.kind = com.google.protobuf.kotlin.Value.Kind.NullValue(com.google.protobuf.kotlin.NullValue.fromNumber(decoder.readEnum()))
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.kind = com.google.protobuf.kotlin.Value.Kind.NumberValue(decoder.readDouble())
            }
            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.kind = com.google.protobuf.kotlin.Value.Kind.StringValue(decoder.readString())
            }
            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.kind = com.google.protobuf.kotlin.Value.Kind.BoolValue(decoder.readBool())
            }
            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.kind as? com.google.protobuf.kotlin.Value.Kind.StructValue) ?: com.google.protobuf.kotlin.Value.Kind.StructValue(com.google.protobuf.kotlin.StructInternal()).also { 
                    msg.kind = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf.kotlin.StructInternal::decodeWith)
            }
            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val field = (msg.kind as? com.google.protobuf.kotlin.Value.Kind.ListValue) ?: com.google.protobuf.kotlin.Value.Kind.ListValue(com.google.protobuf.kotlin.ListValueInternal()).also { 
                    msg.kind = it
                }

                decoder.readMessage(field.value.asInternal(), com.google.protobuf.kotlin.ListValueInternal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf.kotlin.ValueInternal.computeSize(): Int { 
    var __result = 0
    kind?.also { 
        when (val value = it) { 
            is com.google.protobuf.kotlin.Value.Kind.NullValue -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(value.value.number))
            }
            is com.google.protobuf.kotlin.Value.Kind.NumberValue -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(value.value))
            }
            is com.google.protobuf.kotlin.Value.Kind.StringValue -> { 
                __result += kotlinx.rpc.protobuf.internal.WireSize.string(value.value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf.kotlin.Value.Kind.BoolValue -> { 
                __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value.value))
            }
            is com.google.protobuf.kotlin.Value.Kind.StructValue -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
            is com.google.protobuf.kotlin.Value.Kind.ListValue -> { 
                __result += value.value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
            }
        }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Value.asInternal(): com.google.protobuf.kotlin.ValueInternal { 
    return this as? com.google.protobuf.kotlin.ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ListValueInternal.checkRequiredFields() { 
    // no required fields to check
    values.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ListValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (values.isNotEmpty()) { 
        values.forEach { 
            encoder.writeMessage(fieldNr = 1, value = it.asInternal()) { encodeWith(it) }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ListValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.ListValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.ValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.ValueInternal::decodeWith)
                (msg.values as MutableList).add(elem)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf.kotlin.ListValueInternal.computeSize(): Int { 
    var __result = 0
    if (values.isNotEmpty()) { 
        __result += values.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ListValue.asInternal(): com.google.protobuf.kotlin.ListValueInternal { 
    return this as? com.google.protobuf.kotlin.ListValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        value.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (key.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = key)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.key = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.value = com.google.protobuf.kotlin.ValueInternal()
                }

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf.kotlin.ValueInternal::decodeWith)
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                if (msg._unknownFieldsEncoder == null) { 
                    msg._unknownFieldsEncoder = WireEncoder(msg._unknownFields)
                }

                decoder.readUnknownField(tag, msg._unknownFieldsEncoder!!)
            }
        }
    }
}

private fun com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal.computeSize(): Int { 
    var __result = 0
    if (key.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(key).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[0]) { 
        __result += value.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal.asInternal(): com.google.protobuf.kotlin.StructInternal.FieldsEntryInternal { 
    return this
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.NullValue.Companion.fromNumber(number: Int): com.google.protobuf.kotlin.NullValue { 
    return when (number) { 
        0 -> { 
            com.google.protobuf.kotlin.NullValue.NULL_VALUE
        }
        else -> { 
            com.google.protobuf.kotlin.NullValue.UNRECOGNIZED(number)
        }
    }
}
