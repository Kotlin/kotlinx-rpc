@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

public class FieldMaskInternal: com.google.protobuf.kotlin.FieldMask, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var paths: List<kotlin.String> by MsgFieldDelegate { mutableListOf() }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return paths.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FieldMaskInternal
        other.checkRequiredFields()
        if (paths != other.paths) return false
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
            appendLine("com.google.protobuf.kotlin.FieldMask(")
            appendLine("${nextIndentString}paths=${paths},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.FieldMaskInternal.() -> Unit): com.google.protobuf.kotlin.FieldMaskInternal { 
        val copy = com.google.protobuf.kotlin.FieldMaskInternal()
        copy.paths = this.paths.map { it }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.FieldMask> { 
        public override fun encode(value: com.google.protobuf.kotlin.FieldMask, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            val internalMsg = value.asInternal()
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                internalMsg.encodeWith(encoder, config as? kotlinx.rpc.protobuf.ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.FieldMask { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.FieldMaskInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.FieldMaskInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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
public fun com.google.protobuf.kotlin.FieldMaskInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldMaskInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    if (paths.isNotEmpty()) { 
        paths.forEach { 
            encoder.writeString(1, it)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldMaskInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.FieldMaskInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = decoder.readString()
                (msg.paths as MutableList).add(elem)
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

private fun com.google.protobuf.kotlin.FieldMaskInternal.computeSize(): Int { 
    var __result = 0
    if (paths.isNotEmpty()) { 
        __result += paths.sumOf { kotlinx.rpc.protobuf.internal.WireSize.string(it) + kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldMask.asInternal(): com.google.protobuf.kotlin.FieldMaskInternal { 
    return this as? com.google.protobuf.kotlin.FieldMaskInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
