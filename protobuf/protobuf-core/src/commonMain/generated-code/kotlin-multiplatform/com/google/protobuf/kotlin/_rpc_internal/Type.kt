@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

public class TypeInternal: com.google.protobuf.kotlin.Type, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        public const val sourceContext: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    public override var name: String by MsgFieldDelegate { "" }
    public override var fields: List<com.google.protobuf.kotlin.Field> by MsgFieldDelegate { mutableListOf() }
    public override var oneofs: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }
    public override var sourceContext: com.google.protobuf.kotlin.SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { com.google.protobuf.kotlin.SourceContextInternal() }
    public override var syntax: com.google.protobuf.kotlin.Syntax by MsgFieldDelegate { com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 }
    public override var edition: String by MsgFieldDelegate { "" }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Type> { 
        public override fun encode(value: com.google.protobuf.kotlin.Type): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Type { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.TypeInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.TypeInternal.decodeWith(msg, it)
                }
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

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Field> { 
        public override fun encode(value: com.google.protobuf.kotlin.Field): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Field { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.FieldInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.FieldInternal.decodeWith(msg, it)
                }
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

    public override var name: String by MsgFieldDelegate { "" }
    public override var enumvalue: List<com.google.protobuf.kotlin.EnumValue> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }
    public override var sourceContext: com.google.protobuf.kotlin.SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { com.google.protobuf.kotlin.SourceContextInternal() }
    public override var syntax: com.google.protobuf.kotlin.Syntax by MsgFieldDelegate { com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 }
    public override var edition: String by MsgFieldDelegate { "" }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Enum> { 
        public override fun encode(value: com.google.protobuf.kotlin.Enum): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Enum { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.EnumInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.EnumInternal.decodeWith(msg, it)
                }
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

    public override var name: String by MsgFieldDelegate { "" }
    public override var number: Int by MsgFieldDelegate { 0 }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.EnumValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.EnumValue): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.EnumValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.EnumValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.EnumValueInternal.decodeWith(msg, it)
                }
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

    public override var name: String by MsgFieldDelegate { "" }
    public override var value: com.google.protobuf.kotlin.Any by MsgFieldDelegate(PresenceIndices.value) { com.google.protobuf.kotlin.AnyInternal() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Option> { 
        public override fun encode(value: com.google.protobuf.kotlin.Option): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Option { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.OptionInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.OptionInternal.decodeWith(msg, it)
                }
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public operator fun com.google.protobuf.kotlin.Type.Companion.invoke(body: com.google.protobuf.kotlin.TypeInternal.() -> Unit): com.google.protobuf.kotlin.Type { 
    val msg = com.google.protobuf.kotlin.TypeInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

public operator fun com.google.protobuf.kotlin.Field.Companion.invoke(body: com.google.protobuf.kotlin.FieldInternal.() -> Unit): com.google.protobuf.kotlin.Field { 
    val msg = com.google.protobuf.kotlin.FieldInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

public operator fun com.google.protobuf.kotlin.Enum.Companion.invoke(body: com.google.protobuf.kotlin.EnumInternal.() -> Unit): com.google.protobuf.kotlin.Enum { 
    val msg = com.google.protobuf.kotlin.EnumInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

public operator fun com.google.protobuf.kotlin.EnumValue.Companion.invoke(body: com.google.protobuf.kotlin.EnumValueInternal.() -> Unit): com.google.protobuf.kotlin.EnumValue { 
    val msg = com.google.protobuf.kotlin.EnumValueInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

public operator fun com.google.protobuf.kotlin.Option.Companion.invoke(body: com.google.protobuf.kotlin.OptionInternal.() -> Unit): com.google.protobuf.kotlin.Option { 
    val msg = com.google.protobuf.kotlin.OptionInternal().apply(body)
    msg.checkRequiredFields()
    return msg
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
public fun com.google.protobuf.kotlin.TypeInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (fields.isNotEmpty()) { 
        fields.forEach { 
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (oneofs.isNotEmpty()) { 
        oneofs.forEach { 
            encoder.writeString(3, it)
        }
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 4, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 5, value = sourceContext.asInternal()) { encodeWith(it) }
    }

    if (com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 != syntax) { 
        encoder.writeEnum(fieldNr = 6, value = syntax.number)
    }

    if (edition.isNotEmpty()) { 
        encoder.writeString(fieldNr = 7, value = edition)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.TypeInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.TypeInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.FieldInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.FieldInternal::decodeWith)
                (msg.fields as MutableList).add(elem)
            }

            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readString()
                (msg.oneofs as MutableList).add(elem)
            }

            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.OptionInternal::decodeWith)
                (msg.options as MutableList).add(elem)
            }

            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.sourceContext = com.google.protobuf.kotlin.SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), com.google.protobuf.kotlin.SourceContextInternal::decodeWith)
            }

            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.syntax = com.google.protobuf.kotlin.Syntax.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.edition = decoder.readString()
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
}

private fun com.google.protobuf.kotlin.TypeInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (fields.isNotEmpty()) { 
        __result = fields.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (oneofs.isNotEmpty()) { 
        __result = oneofs.sumOf { kotlinx.rpc.protobuf.internal.WireSize.string(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (options.isNotEmpty()) { 
        __result = options.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (presenceMask[0]) { 
        __result += sourceContext.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 != syntax) { 
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
public fun com.google.protobuf.kotlin.FieldInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (com.google.protobuf.kotlin.Field.Kind.TYPE_UNKNOWN != kind) { 
        encoder.writeEnum(fieldNr = 1, value = kind.number)
    }

    if (com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_UNKNOWN != cardinality) { 
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
            encoder.writeMessage(fieldNr = 9, value = it.asInternal()) { encodeWith(it) }
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
public fun com.google.protobuf.kotlin.FieldInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.FieldInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.OptionInternal::decodeWith)
                (msg.options as MutableList).add(elem)
            }

            tag.fieldNr == 10 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.jsonName = decoder.readString()
            }

            tag.fieldNr == 11 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.defaultValue = decoder.readString()
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
}

private fun com.google.protobuf.kotlin.FieldInternal.computeSize(): Int { 
    var __result = 0
    if (com.google.protobuf.kotlin.Field.Kind.TYPE_UNKNOWN != kind) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(kind.number))
    }

    if (com.google.protobuf.kotlin.Field.Cardinality.CARDINALITY_UNKNOWN != cardinality) { 
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
        __result = options.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(9, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
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
public fun com.google.protobuf.kotlin.EnumInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (enumvalue.isNotEmpty()) { 
        enumvalue.forEach { 
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 4, value = sourceContext.asInternal()) { encodeWith(it) }
    }

    if (com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 != syntax) { 
        encoder.writeEnum(fieldNr = 5, value = syntax.number)
    }

    if (edition.isNotEmpty()) { 
        encoder.writeString(fieldNr = 6, value = edition)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.EnumInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.EnumValueInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.EnumValueInternal::decodeWith)
                (msg.enumvalue as MutableList).add(elem)
            }

            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.OptionInternal::decodeWith)
                (msg.options as MutableList).add(elem)
            }

            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.sourceContext = com.google.protobuf.kotlin.SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), com.google.protobuf.kotlin.SourceContextInternal::decodeWith)
            }

            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.syntax = com.google.protobuf.kotlin.Syntax.fromNumber(decoder.readEnum())
            }

            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.edition = decoder.readString()
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
}

private fun com.google.protobuf.kotlin.EnumInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (enumvalue.isNotEmpty()) { 
        __result = enumvalue.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (options.isNotEmpty()) { 
        __result = options.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (presenceMask[0]) { 
        __result += sourceContext.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 != syntax) { 
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
public fun com.google.protobuf.kotlin.EnumValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (number != 0) { 
        encoder.writeInt32(fieldNr = 2, value = number)
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it) }
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EnumValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.EnumValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.OptionInternal::decodeWith)
                (msg.options as MutableList).add(elem)
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
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
        __result = options.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
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
public fun com.google.protobuf.kotlin.OptionInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 2, value = value.asInternal()) { encodeWith(it) }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.OptionInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.OptionInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                decoder.readMessage(msg.value.asInternal(), com.google.protobuf.kotlin.AnyInternal::decodeWith)
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
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

