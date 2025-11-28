@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

public class FieldMaskInternal: com.google.protobuf.kotlin.FieldMask, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

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
    public fun copyInternal(body: FieldMaskInternal.() -> Unit): FieldMaskInternal { 
        val copy = FieldMaskInternal()
        copy.paths = paths.map { it }
        copy.apply(body)
        return copy
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.FieldMask> { 
        public override fun encode(value: com.google.protobuf.kotlin.FieldMask): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.FieldMask { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.FieldMaskInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.FieldMaskInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

/**
* Constructs a new message.
* ```
* val message = FieldMask {
*    paths = ...
* }
* ```
*/
public operator fun com.google.protobuf.kotlin.FieldMask.Companion.invoke(body: com.google.protobuf.kotlin.FieldMaskInternal.() -> Unit): com.google.protobuf.kotlin.FieldMask { 
    val msg = com.google.protobuf.kotlin.FieldMaskInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

/**
* Copies the original message, including unknown fields.
* ```
* val copy = original.copy {
*    paths = ...
* }
* ```
*/
public fun com.google.protobuf.kotlin.FieldMask.copy(body: com.google.protobuf.kotlin.FieldMaskInternal.() -> Unit = {}): com.google.protobuf.kotlin.FieldMask { 
    return this.asInternal().copyInternal(body)
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldMaskInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldMaskInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (paths.isNotEmpty()) { 
        paths.forEach { 
            encoder.writeString(1, it)
        }
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.FieldMaskInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.FieldMaskInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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

                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag)
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
