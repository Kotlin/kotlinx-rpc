@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.internal.*

public class DoubleValueInternal: com.google.protobuf.kotlin.DoubleValue, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Double by MsgFieldDelegate { 0.0 }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as DoubleValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.DoubleValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.DoubleValueInternal.() -> Unit): com.google.protobuf.kotlin.DoubleValueInternal { 
        val copy = com.google.protobuf.kotlin.DoubleValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.DoubleValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.DoubleValue, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.DoubleValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.DoubleValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.DoubleValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class FloatValueInternal: com.google.protobuf.kotlin.FloatValue, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Float by MsgFieldDelegate { 0.0f }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FloatValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.FloatValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.FloatValueInternal.() -> Unit): com.google.protobuf.kotlin.FloatValueInternal { 
        val copy = com.google.protobuf.kotlin.FloatValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.FloatValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.FloatValue, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.FloatValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.FloatValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.FloatValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class Int64ValueInternal: com.google.protobuf.kotlin.Int64Value, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Long by MsgFieldDelegate { 0L }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Int64ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.Int64Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.Int64ValueInternal.() -> Unit): com.google.protobuf.kotlin.Int64ValueInternal { 
        val copy = com.google.protobuf.kotlin.Int64ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Int64Value> { 
        public override fun encode(value: com.google.protobuf.kotlin.Int64Value, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Int64Value { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.Int64ValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.Int64ValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class UInt64ValueInternal: com.google.protobuf.kotlin.UInt64Value, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: ULong by MsgFieldDelegate { 0uL }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UInt64ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.UInt64Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.UInt64ValueInternal.() -> Unit): com.google.protobuf.kotlin.UInt64ValueInternal { 
        val copy = com.google.protobuf.kotlin.UInt64ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.UInt64Value> { 
        public override fun encode(value: com.google.protobuf.kotlin.UInt64Value, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.UInt64Value { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.UInt64ValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.UInt64ValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class Int32ValueInternal: com.google.protobuf.kotlin.Int32Value, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Int by MsgFieldDelegate { 0 }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Int32ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.Int32Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.Int32ValueInternal.() -> Unit): com.google.protobuf.kotlin.Int32ValueInternal { 
        val copy = com.google.protobuf.kotlin.Int32ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Int32Value> { 
        public override fun encode(value: com.google.protobuf.kotlin.Int32Value, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Int32Value { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.Int32ValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.Int32ValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class UInt32ValueInternal: com.google.protobuf.kotlin.UInt32Value, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: UInt by MsgFieldDelegate { 0u }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as UInt32ValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.UInt32Value(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.UInt32ValueInternal.() -> Unit): com.google.protobuf.kotlin.UInt32ValueInternal { 
        val copy = com.google.protobuf.kotlin.UInt32ValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.UInt32Value> { 
        public override fun encode(value: com.google.protobuf.kotlin.UInt32Value, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.UInt32Value { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.UInt32ValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.UInt32ValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class BoolValueInternal: com.google.protobuf.kotlin.BoolValue, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: Boolean by MsgFieldDelegate { false }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BoolValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.BoolValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.BoolValueInternal.() -> Unit): com.google.protobuf.kotlin.BoolValueInternal { 
        val copy = com.google.protobuf.kotlin.BoolValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.BoolValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.BoolValue, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.BoolValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.BoolValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.BoolValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class StringValueInternal: com.google.protobuf.kotlin.StringValue, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: String by MsgFieldDelegate { "" }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StringValueInternal
        other.checkRequiredFields()
        if (value != other.value) return false
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
            appendLine("com.google.protobuf.kotlin.StringValue(")
            appendLine("${nextIndentString}value=${value},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.StringValueInternal.() -> Unit): com.google.protobuf.kotlin.StringValueInternal { 
        val copy = com.google.protobuf.kotlin.StringValueInternal()
        copy.value = this.value
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.StringValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.StringValue, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.StringValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.StringValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.StringValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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

public class BytesValueInternal: com.google.protobuf.kotlin.BytesValue, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var value: ByteArray by MsgFieldDelegate { byteArrayOf() }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return value.contentHashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as BytesValueInternal
        other.checkRequiredFields()
        if (!value.contentEquals(other.value)) return false
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
            appendLine("com.google.protobuf.kotlin.BytesValue(")
            appendLine("${nextIndentString}value=${value.contentToString()},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.BytesValueInternal.() -> Unit): com.google.protobuf.kotlin.BytesValueInternal { 
        val copy = com.google.protobuf.kotlin.BytesValueInternal()
        copy.value = this.value.copyOf()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.BytesValue> { 
        public override fun encode(value: com.google.protobuf.kotlin.BytesValue, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.io.Source { 
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

        public override fun decode(source: kotlinx.io.Source, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.BytesValue { 
            kotlinx.rpc.protobuf.internal.WireDecoder(source).use { 
                val msg = com.google.protobuf.kotlin.BytesValueInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.BytesValueInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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
public fun com.google.protobuf.kotlin.DoubleValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.DoubleValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value != 0.0) { 
        encoder.writeDouble(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.DoubleValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.DoubleValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED64 -> { 
                msg.value = decoder.readDouble()
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

private fun com.google.protobuf.kotlin.DoubleValueInternal.computeSize(): Int { 
    var __result = 0
    if (value != 0.0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED64) + kotlinx.rpc.protobuf.internal.WireSize.double(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.DoubleValue.asInternal(): com.google.protobuf.kotlin.DoubleValueInternal { 
    return this as? com.google.protobuf.kotlin.DoubleValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FloatValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FloatValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value != 0.0f) { 
        encoder.writeFloat(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FloatValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.FloatValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.FIXED32 -> { 
                msg.value = decoder.readFloat()
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

private fun com.google.protobuf.kotlin.FloatValueInternal.computeSize(): Int { 
    var __result = 0
    if (value != 0.0f) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.FIXED32) + kotlinx.rpc.protobuf.internal.WireSize.float(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FloatValue.asInternal(): com.google.protobuf.kotlin.FloatValueInternal { 
    return this as? com.google.protobuf.kotlin.FloatValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int64ValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int64ValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value != 0L) { 
        encoder.writeInt64(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int64ValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.Int64ValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readInt64()
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

private fun com.google.protobuf.kotlin.Int64ValueInternal.computeSize(): Int { 
    var __result = 0
    if (value != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int64Value.asInternal(): com.google.protobuf.kotlin.Int64ValueInternal { 
    return this as? com.google.protobuf.kotlin.Int64ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt64ValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt64ValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value != 0uL) { 
        encoder.writeUInt64(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt64ValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.UInt64ValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readUInt64()
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

private fun com.google.protobuf.kotlin.UInt64ValueInternal.computeSize(): Int { 
    var __result = 0
    if (value != 0uL) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt64(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt64Value.asInternal(): com.google.protobuf.kotlin.UInt64ValueInternal { 
    return this as? com.google.protobuf.kotlin.UInt64ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int32ValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int32ValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value != 0) { 
        encoder.writeInt32(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int32ValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.Int32ValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readInt32()
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

private fun com.google.protobuf.kotlin.Int32ValueInternal.computeSize(): Int { 
    var __result = 0
    if (value != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Int32Value.asInternal(): com.google.protobuf.kotlin.Int32ValueInternal { 
    return this as? com.google.protobuf.kotlin.Int32ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt32ValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt32ValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value != 0u) { 
        encoder.writeUInt32(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt32ValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.UInt32ValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readUInt32()
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

private fun com.google.protobuf.kotlin.UInt32ValueInternal.computeSize(): Int { 
    var __result = 0
    if (value != 0u) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.uInt32(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.UInt32Value.asInternal(): com.google.protobuf.kotlin.UInt32ValueInternal { 
    return this as? com.google.protobuf.kotlin.UInt32ValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BoolValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BoolValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value != false) { 
        encoder.writeBool(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BoolValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.BoolValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.value = decoder.readBool()
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

private fun com.google.protobuf.kotlin.BoolValueInternal.computeSize(): Int { 
    var __result = 0
    if (value != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(value))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BoolValue.asInternal(): com.google.protobuf.kotlin.BoolValueInternal { 
    return this as? com.google.protobuf.kotlin.BoolValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StringValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StringValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StringValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.StringValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.value = decoder.readString()
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

private fun com.google.protobuf.kotlin.StringValueInternal.computeSize(): Int { 
    var __result = 0
    if (value.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.StringValue.asInternal(): com.google.protobuf.kotlin.StringValueInternal { 
    return this as? com.google.protobuf.kotlin.StringValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BytesValueInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BytesValueInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (value.isNotEmpty()) { 
        encoder.writeBytes(fieldNr = 1, value = value)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BytesValueInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.BytesValueInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.value = decoder.readBytes()
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

private fun com.google.protobuf.kotlin.BytesValueInternal.computeSize(): Int { 
    var __result = 0
    if (value.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.bytes(value).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.BytesValue.asInternal(): com.google.protobuf.kotlin.BytesValueInternal { 
    return this as? com.google.protobuf.kotlin.BytesValueInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
