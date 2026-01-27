@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.WireEncoder

public class EmptyInternal: com.google.protobuf.kotlin.Empty, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @kotlinx.rpc.internal.utils.InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override fun hashCode(): kotlin.Int { 
        checkRequiredFields()
        return this::class.hashCode()
    }

    public override fun equals(other: kotlin.Any?): kotlin.Boolean { 
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EmptyInternal
        other.checkRequiredFields()
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
            appendLine("com.google.protobuf.kotlin.Empty(")
            append("${indentString})")
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public fun copyInternal(body: com.google.protobuf.kotlin.EmptyInternal.() -> Unit): com.google.protobuf.kotlin.EmptyInternal { 
        val copy = com.google.protobuf.kotlin.EmptyInternal()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Empty> { 
        public override fun encode(value: com.google.protobuf.kotlin.Empty, config: kotlinx.rpc.grpc.codec.CodecConfig?): kotlinx.rpc.protobuf.input.stream.InputStream { 
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

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream, config: kotlinx.rpc.grpc.codec.CodecConfig?): com.google.protobuf.kotlin.Empty { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.EmptyInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.EmptyInternal.decodeWith(msg, it, config as? kotlinx.rpc.protobuf.ProtobufConfig)
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
public fun com.google.protobuf.kotlin.EmptyInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EmptyInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    // no fields to encode
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.EmptyInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.EmptyInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder, config: kotlinx.rpc.protobuf.ProtobufConfig?) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
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

private fun com.google.protobuf.kotlin.EmptyInternal.computeSize(): Int { 
    var __result = 0
    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Empty.asInternal(): com.google.protobuf.kotlin.EmptyInternal { 
    return this as? com.google.protobuf.kotlin.EmptyInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
