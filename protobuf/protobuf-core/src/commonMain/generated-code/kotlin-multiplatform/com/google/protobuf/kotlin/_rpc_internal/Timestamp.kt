@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

public class TimestampInternal: com.google.protobuf.kotlin.Timestamp, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    public override var seconds: Long by MsgFieldDelegate { 0L }
    public override var nanos: Int by MsgFieldDelegate { 0 }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Timestamp> { 
        public override fun encode(value: com.google.protobuf.kotlin.Timestamp): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Timestamp { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.TimestampInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.TimestampInternal.decodeWith(msg, it)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public operator fun com.google.protobuf.kotlin.Timestamp.Companion.invoke(body: com.google.protobuf.kotlin.TimestampInternal.() -> Unit): com.google.protobuf.kotlin.Timestamp { 
    val msg = com.google.protobuf.kotlin.TimestampInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.TimestampInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.TimestampInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (seconds != 0L) { 
        encoder.writeInt64(fieldNr = 1, value = seconds)
    }

    if (nanos != 0) { 
        encoder.writeInt32(fieldNr = 2, value = nanos)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.TimestampInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.TimestampInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
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
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }
}

private fun com.google.protobuf.kotlin.TimestampInternal.computeSize(): Int { 
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
public fun com.google.protobuf.kotlin.Timestamp.asInternal(): com.google.protobuf.kotlin.TimestampInternal { 
    return this as? com.google.protobuf.kotlin.TimestampInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

