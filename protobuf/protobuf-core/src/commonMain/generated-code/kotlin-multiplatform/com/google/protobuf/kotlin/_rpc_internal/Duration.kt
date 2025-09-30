@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

public class DurationInternal: com.google.protobuf.kotlin.Duration, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    public override var seconds: Long by MsgFieldDelegate { 0L }
    public override var nanos: Int by MsgFieldDelegate { 0 }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Duration> { 
        public override fun encode(value: com.google.protobuf.kotlin.Duration): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Duration { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.DurationInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.DurationInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public operator fun com.google.protobuf.kotlin.Duration.Companion.invoke(body: com.google.protobuf.kotlin.DurationInternal.() -> Unit): com.google.protobuf.kotlin.Duration { 
    val msg = com.google.protobuf.kotlin.DurationInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.DurationInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.DurationInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (seconds != 0L) { 
        encoder.writeInt64(fieldNr = 1, value = seconds)
    }

    if (nanos != 0) { 
        encoder.writeInt32(fieldNr = 2, value = nanos)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.DurationInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.DurationInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.seconds = decoder.readInt64()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.nanos = decoder.readInt32()
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

private fun com.google.protobuf.kotlin.DurationInternal.computeSize(): Int { 
    var __result = 0
    if (seconds != 0L) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int64(seconds))
    }

    if (nanos != 0) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.int32(nanos))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Duration.asInternal(): com.google.protobuf.kotlin.DurationInternal { 
    return this as? com.google.protobuf.kotlin.DurationInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
