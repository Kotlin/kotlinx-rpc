@file:OptIn(ExperimentalRpcApi::class, kotlinx.rpc.internal.utils.InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.rpc.internal.utils.*
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.internal.*

public class ApiInternal: com.google.protobuf.kotlin.Api, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 1) { 
    private object PresenceIndices { 
        public const val sourceContext: Int = 0
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    public override var name: String by MsgFieldDelegate { "" }
    public override var methods: List<com.google.protobuf.kotlin.Method> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }
    public override var version: String by MsgFieldDelegate { "" }
    public override var sourceContext: com.google.protobuf.kotlin.SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { com.google.protobuf.kotlin.SourceContextInternal() }
    public override var mixins: List<com.google.protobuf.kotlin.Mixin> by MsgFieldDelegate { mutableListOf() }
    public override var syntax: com.google.protobuf.kotlin.Syntax by MsgFieldDelegate { com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Api> { 
        public override fun encode(value: com.google.protobuf.kotlin.Api): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Api { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.ApiInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.ApiInternal.decodeWith(msg, it)
                }
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public class MethodInternal: com.google.protobuf.kotlin.Method, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    public override var name: String by MsgFieldDelegate { "" }
    public override var requestTypeUrl: String by MsgFieldDelegate { "" }
    public override var requestStreaming: Boolean by MsgFieldDelegate { false }
    public override var responseTypeUrl: String by MsgFieldDelegate { "" }
    public override var responseStreaming: Boolean by MsgFieldDelegate { false }
    public override var options: List<com.google.protobuf.kotlin.Option> by MsgFieldDelegate { mutableListOf() }
    public override var syntax: com.google.protobuf.kotlin.Syntax by MsgFieldDelegate { com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2 }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Method> { 
        public override fun encode(value: com.google.protobuf.kotlin.Method): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Method { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.MethodInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.MethodInternal.decodeWith(msg, it)
                }
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public class MixinInternal: com.google.protobuf.kotlin.Mixin, kotlinx.rpc.protobuf.internal.InternalMessage(fieldsWithPresence = 0) { 
    @kotlinx.rpc.internal.utils.InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    public override var name: String by MsgFieldDelegate { "" }
    public override var root: String by MsgFieldDelegate { "" }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public object CODEC: kotlinx.rpc.grpc.codec.MessageCodec<com.google.protobuf.kotlin.Mixin> { 
        public override fun encode(value: com.google.protobuf.kotlin.Mixin): kotlinx.rpc.protobuf.input.stream.InputStream { 
            val buffer = kotlinx.io.Buffer()
            val encoder = kotlinx.rpc.protobuf.internal.WireEncoder(buffer)
            kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException { 
                value.asInternal().encodeWith(encoder)
            }
            encoder.flush()
            return buffer.asInputStream()
        }

        public override fun decode(stream: kotlinx.rpc.protobuf.input.stream.InputStream): com.google.protobuf.kotlin.Mixin { 
            kotlinx.rpc.protobuf.internal.WireDecoder(stream).use { 
                val msg = com.google.protobuf.kotlin.MixinInternal()
                kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException { 
                    com.google.protobuf.kotlin.MixinInternal.decodeWith(msg, it)
                }
                return msg
            }
        }
    }

    @kotlinx.rpc.internal.utils.InternalRpcApi
    public companion object
}

public operator fun com.google.protobuf.kotlin.Api.Companion.invoke(body: com.google.protobuf.kotlin.ApiInternal.() -> Unit): com.google.protobuf.kotlin.Api { 
    val msg = com.google.protobuf.kotlin.ApiInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

public operator fun com.google.protobuf.kotlin.Method.Companion.invoke(body: com.google.protobuf.kotlin.MethodInternal.() -> Unit): com.google.protobuf.kotlin.Method { 
    val msg = com.google.protobuf.kotlin.MethodInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

public operator fun com.google.protobuf.kotlin.Mixin.Companion.invoke(body: com.google.protobuf.kotlin.MixinInternal.() -> Unit): com.google.protobuf.kotlin.Mixin { 
    val msg = com.google.protobuf.kotlin.MixinInternal().apply(body)
    msg.checkRequiredFields()
    return msg
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ApiInternal.checkRequiredFields() { 
    // no required fields to check
    if (presenceMask[0]) { 
        sourceContext.asInternal().checkRequiredFields()
    }

    methods.forEach { 
        it.asInternal().checkRequiredFields()
    }

    options.forEach { 
        it.asInternal().checkRequiredFields()
    }

    mixins.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ApiInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (methods.isNotEmpty()) { 
        methods.forEach { 
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (version.isNotEmpty()) { 
        encoder.writeString(fieldNr = 4, value = version)
    }

    if (presenceMask[0]) { 
        encoder.writeMessage(fieldNr = 5, value = sourceContext.asInternal()) { encodeWith(it) }
    }

    if (mixins.isNotEmpty()) { 
        mixins.forEach { 
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        encoder.writeEnum(fieldNr = 7, value = syntax.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.ApiInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.ApiInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.MethodInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.MethodInternal::decodeWith)
                (msg.methods as MutableList).add(elem)
            }

            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.OptionInternal::decodeWith)
                (msg.options as MutableList).add(elem)
            }

            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.version = decoder.readString()
            }

            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                if (!msg.presenceMask[0]) { 
                    msg.sourceContext = com.google.protobuf.kotlin.SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), com.google.protobuf.kotlin.SourceContextInternal::decodeWith)
            }

            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.MixinInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.MixinInternal::decodeWith)
                (msg.mixins as MutableList).add(elem)
            }

            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.syntax = com.google.protobuf.kotlin.Syntax.fromNumber(decoder.readEnum())
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
}

private fun com.google.protobuf.kotlin.ApiInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (methods.isNotEmpty()) { 
        __result = methods.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (options.isNotEmpty()) { 
        __result = options.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (version.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(version).let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (presenceMask[0]) { 
        __result += sourceContext.asInternal()._size.let { kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (mixins.isNotEmpty()) { 
        __result = mixins.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(syntax.number))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Api.asInternal(): com.google.protobuf.kotlin.ApiInternal { 
    return this as? com.google.protobuf.kotlin.ApiInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.MethodInternal.checkRequiredFields() { 
    // no required fields to check
    options.forEach { 
        it.asInternal().checkRequiredFields()
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.MethodInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (requestTypeUrl.isNotEmpty()) { 
        encoder.writeString(fieldNr = 2, value = requestTypeUrl)
    }

    if (requestStreaming != false) { 
        encoder.writeBool(fieldNr = 3, value = requestStreaming)
    }

    if (responseTypeUrl.isNotEmpty()) { 
        encoder.writeString(fieldNr = 4, value = responseTypeUrl)
    }

    if (responseStreaming != false) { 
        encoder.writeBool(fieldNr = 5, value = responseStreaming)
    }

    if (options.isNotEmpty()) { 
        options.forEach { 
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encodeWith(it) }
        }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        encoder.writeEnum(fieldNr = 7, value = syntax.number)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.MethodInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.MethodInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.requestTypeUrl = decoder.readString()
            }

            tag.fieldNr == 3 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.requestStreaming = decoder.readBool()
            }

            tag.fieldNr == 4 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.responseTypeUrl = decoder.readString()
            }

            tag.fieldNr == 5 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.responseStreaming = decoder.readBool()
            }

            tag.fieldNr == 6 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                val elem = com.google.protobuf.kotlin.OptionInternal()
                decoder.readMessage(elem.asInternal(), com.google.protobuf.kotlin.OptionInternal::decodeWith)
                (msg.options as MutableList).add(elem)
            }

            tag.fieldNr == 7 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.VARINT -> { 
                msg.syntax = com.google.protobuf.kotlin.Syntax.fromNumber(decoder.readEnum())
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
}

private fun com.google.protobuf.kotlin.MethodInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (requestTypeUrl.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(requestTypeUrl).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (requestStreaming != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(3, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(requestStreaming))
    }

    if (responseTypeUrl.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(responseTypeUrl).let { kotlinx.rpc.protobuf.internal.WireSize.tag(4, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (responseStreaming != false) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(5, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.bool(responseStreaming))
    }

    if (options.isNotEmpty()) { 
        __result = options.sumOf { it.asInternal()._size + kotlinx.rpc.protobuf.internal.WireSize.tag(6, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) }
    }

    if (syntax != com.google.protobuf.kotlin.Syntax.SYNTAX_PROTO2) { 
        __result += (kotlinx.rpc.protobuf.internal.WireSize.tag(7, kotlinx.rpc.protobuf.internal.WireType.VARINT) + kotlinx.rpc.protobuf.internal.WireSize.enum(syntax.number))
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Method.asInternal(): com.google.protobuf.kotlin.MethodInternal { 
    return this as? com.google.protobuf.kotlin.MethodInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.MixinInternal.checkRequiredFields() { 
    // no required fields to check
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.MixinInternal.encodeWith(encoder: kotlinx.rpc.protobuf.internal.WireEncoder) { 
    if (name.isNotEmpty()) { 
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (root.isNotEmpty()) { 
        encoder.writeString(fieldNr = 2, value = root)
    }
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.MixinInternal.Companion.decodeWith(msg: com.google.protobuf.kotlin.MixinInternal, decoder: kotlinx.rpc.protobuf.internal.WireDecoder) { 
    while (true) { 
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when { 
            tag.fieldNr == 1 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.name = decoder.readString()
            }

            tag.fieldNr == 2 && tag.wireType == kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED -> { 
                msg.root = decoder.readString()
            }

            else -> { 
                // we are currently just skipping unknown fields (KRPC-191)
                decoder.skipValue(tag.wireType)
            }
        }
    }

    msg.checkRequiredFields()
}

private fun com.google.protobuf.kotlin.MixinInternal.computeSize(): Int { 
    var __result = 0
    if (name.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(name).let { kotlinx.rpc.protobuf.internal.WireSize.tag(1, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    if (root.isNotEmpty()) { 
        __result += kotlinx.rpc.protobuf.internal.WireSize.string(root).let { kotlinx.rpc.protobuf.internal.WireSize.tag(2, kotlinx.rpc.protobuf.internal.WireType.LENGTH_DELIMITED) + kotlinx.rpc.protobuf.internal.WireSize.int32(it) + it }
    }

    return __result
}

@kotlinx.rpc.internal.utils.InternalRpcApi
public fun com.google.protobuf.kotlin.Mixin.asInternal(): com.google.protobuf.kotlin.MixinInternal { 
    return this as? com.google.protobuf.kotlin.MixinInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

