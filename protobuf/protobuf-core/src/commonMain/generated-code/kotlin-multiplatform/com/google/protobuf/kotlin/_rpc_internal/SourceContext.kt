@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

public class SourceContextInternal: com.google.protobuf.kotlin.SourceContext, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    public override var fileName: String by MsgFieldDelegate { "" }

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return fileName.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as SourceContextInternal
        other.checkRequiredFields()
        if (fileName != other.fileName) return false
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
            appendLine("com.google.protobuf.kotlin.SourceContext(")
            appendLine("${nextIndentString}fileName=${fileName},")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.SourceContextInternal.() -> Unit): com.google.protobuf.kotlin.SourceContextInternal { 
        val copy = com.google.protobuf.kotlin.SourceContextInternal()
        copy.fileName = fileName
        copy.apply(body)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.SourceContext> { 
        public override fun encode(value: com.google.protobuf.kotlin.SourceContext): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.SourceContext { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.SourceContextInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.SourceContextInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.SourceContextInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.SourceContextInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (fileName.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = fileName)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.SourceContextInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.SourceContextInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.fileName = decoder.readString()
            }
            else -> { 
                if (tag.wireType == kotlinx.rpc.protobuf.internal.WireType.END_GROUP) { 
                    throw kotlinx.rpc.protobuf.internal.ProtobufDecodingException("Unexpected END_GROUP tag.")
                }

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
            }
        }
    }
}

private fun com.google.protobuf.kotlin.SourceContextInternal.computeSize(): Int { 
    var __result = 0
    if (fileName.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(fileName).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.SourceContext.asInternal(): com.google.protobuf.kotlin.SourceContextInternal { 
    return this as? com.google.protobuf.kotlin.SourceContextInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
