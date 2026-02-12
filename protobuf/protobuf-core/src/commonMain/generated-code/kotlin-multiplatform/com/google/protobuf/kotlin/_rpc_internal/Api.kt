@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
import kotlinx.rpc.protobuf.internal.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import kotlinx.rpc.protobuf.internal.WireSize
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.bool
import kotlinx.rpc.protobuf.internal.bytes
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlinx.rpc.protobuf.internal.checkForPlatformEncodeException
import kotlinx.rpc.protobuf.internal.enum
import kotlinx.rpc.protobuf.internal.int32
import kotlinx.rpc.protobuf.internal.string
import kotlinx.rpc.protobuf.internal.tag

public class ApiInternal: Api, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        public const val sourceContext: Int = 0
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.Api> get() = DESCRIPTOR

    public override var name: String by MsgFieldDelegate { "" }
    public override var methods: List<Method> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<Option> by MsgFieldDelegate { mutableListOf() }
    public override var version: String by MsgFieldDelegate { "" }
    public override var sourceContext: SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { SourceContextInternal() }
    public override var mixins: List<Mixin> by MsgFieldDelegate { mutableListOf() }
    public override var syntax: Syntax by MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }

    @InternalRpcApi
    public val _presence: ApiPresence = object : ApiPresence {
        public override val hasSourceContext: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + methods.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + if (presenceMask[0]) sourceContext.hashCode() else 0
        result = 31 * result + mixins.hashCode()
        result = 31 * result + syntax.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ApiInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (name != other.name) return false
        if (methods != other.methods) return false
        if (options != other.options) return false
        if (version != other.version) return false
        if (presenceMask[0] && sourceContext != other.sourceContext) return false
        if (mixins != other.mixins) return false
        if (syntax != other.syntax) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("Api(")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}methods=${methods},")
            appendLine("${nextIndentString}options=${options},")
            appendLine("${nextIndentString}version=${version},")
            if (presenceMask[0]) {
                appendLine("${nextIndentString}sourceContext=${sourceContext.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}sourceContext=<unset>,")
            }

            appendLine("${nextIndentString}mixins=${mixins},")
            appendLine("${nextIndentString}syntax=${syntax},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: ApiInternal.() -> Unit): ApiInternal {
        val copy = ApiInternal()
        copy.name = this.name
        copy.methods = this.methods.map { it.copy() }
        copy.options = this.options.map { it.copy() }
        copy.version = this.version
        if (presenceMask[0]) {
            copy.sourceContext = this.sourceContext.copy()
        }

        copy.mixins = this.mixins.map { it.copy() }
        copy.syntax = this.syntax
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<Api> {
        public override fun encode(value: Api, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: Source, config: CodecConfig?): Api {
            WireDecoder(source).use {
                val msg = ApiInternal()
                checkForPlatformDecodeException {
                    ApiInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Api> {
        public override val fullName: String = "google.protobuf.Api"
    }

    @InternalRpcApi
    public companion object
}

public class MethodInternal: Method, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.Method> get() = DESCRIPTOR

    public override var name: String by MsgFieldDelegate { "" }
    public override var requestTypeUrl: String by MsgFieldDelegate { "" }
    public override var requestStreaming: Boolean by MsgFieldDelegate { false }
    public override var responseTypeUrl: String by MsgFieldDelegate { "" }
    public override var responseStreaming: Boolean by MsgFieldDelegate { false }
    public override var options: List<Option> by MsgFieldDelegate { mutableListOf() }
    public override var syntax: Syntax by MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + requestTypeUrl.hashCode()
        result = 31 * result + requestStreaming.hashCode()
        result = 31 * result + responseTypeUrl.hashCode()
        result = 31 * result + responseStreaming.hashCode()
        result = 31 * result + options.hashCode()
        result = 31 * result + syntax.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MethodInternal
        other.checkRequiredFields()
        if (name != other.name) return false
        if (requestTypeUrl != other.requestTypeUrl) return false
        if (requestStreaming != other.requestStreaming) return false
        if (responseTypeUrl != other.responseTypeUrl) return false
        if (responseStreaming != other.responseStreaming) return false
        if (options != other.options) return false
        if (syntax != other.syntax) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("Method(")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}requestTypeUrl=${requestTypeUrl},")
            appendLine("${nextIndentString}requestStreaming=${requestStreaming},")
            appendLine("${nextIndentString}responseTypeUrl=${responseTypeUrl},")
            appendLine("${nextIndentString}responseStreaming=${responseStreaming},")
            appendLine("${nextIndentString}options=${options},")
            appendLine("${nextIndentString}syntax=${syntax},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: MethodInternal.() -> Unit): MethodInternal {
        val copy = MethodInternal()
        copy.name = this.name
        copy.requestTypeUrl = this.requestTypeUrl
        copy.requestStreaming = this.requestStreaming
        copy.responseTypeUrl = this.responseTypeUrl
        copy.responseStreaming = this.responseStreaming
        copy.options = this.options.map { it.copy() }
        copy.syntax = this.syntax
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<Method> {
        public override fun encode(value: Method, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: Source, config: CodecConfig?): Method {
            WireDecoder(source).use {
                val msg = MethodInternal()
                checkForPlatformDecodeException {
                    MethodInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Method> {
        public override val fullName: String = "google.protobuf.Method"
    }

    @InternalRpcApi
    public companion object
}

public class MixinInternal: Mixin, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override val _descriptor: kotlinx.rpc.protobuf.internal.ProtoDescriptor<com.google.protobuf.kotlin.Mixin> get() = DESCRIPTOR

    public override var name: String by MsgFieldDelegate { "" }
    public override var root: String by MsgFieldDelegate { "" }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + root.hashCode()
        return result
    }

    public override fun equals(other: kotlin.Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MixinInternal
        other.checkRequiredFields()
        if (name != other.name) return false
        if (root != other.root) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("Mixin(")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}root=${root},")
            append("${indentString})")
        }
    }

    @InternalRpcApi
    public fun copyInternal(body: MixinInternal.() -> Unit): MixinInternal {
        val copy = MixinInternal()
        copy.name = this.name
        copy.root = this.root
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    public object CODEC: MessageCodec<Mixin> {
        public override fun encode(value: Mixin, config: CodecConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtobufConfig)
            }
            encoder.flush()
            internalMsg._unknownFields.copyTo(buffer)
            return buffer
        }

        public override fun decode(source: Source, config: CodecConfig?): Mixin {
            WireDecoder(source).use {
                val msg = MixinInternal()
                checkForPlatformDecodeException {
                    MixinInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Mixin> {
        public override val fullName: String = "google.protobuf.Mixin"
    }

    @InternalRpcApi
    public companion object
}

@InternalRpcApi
public fun ApiInternal.checkRequiredFields() {
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

@InternalRpcApi
public fun ApiInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (methods.isNotEmpty()) {
        methods.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (options.isNotEmpty()) {
        options.forEach {
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (version.isNotEmpty()) {
        encoder.writeString(fieldNr = 4, value = version)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 5, value = sourceContext.asInternal()) { encodeWith(it, config) }
    }

    if (mixins.isNotEmpty()) {
        mixins.forEach {
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (syntax != Syntax.SYNTAX_PROTO2) {
        encoder.writeEnum(fieldNr = 7, value = syntax.number)
    }
}

@InternalRpcApi
public fun ApiInternal.Companion.decodeWith(msg: ApiInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = MethodInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> MethodInternal.decodeWith(msg, decoder, config) })
                (msg.methods as MutableList).add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.version = decoder.readString()
            }
            tag.fieldNr == 5 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.sourceContext = SourceContextInternal()
                }

                decoder.readMessage(msg.sourceContext.asInternal(), { msg, decoder -> SourceContextInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = MixinInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> MixinInternal.decodeWith(msg, decoder, config) })
                (msg.mixins as MutableList).add(elem)
            }
            tag.fieldNr == 7 && tag.wireType == WireType.VARINT -> {
                msg.syntax = Syntax.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun ApiInternal.computeSize(): Int {
    var __result = 0
    if (name.isNotEmpty()) {
        __result += WireSize.string(name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (methods.isNotEmpty()) {
        __result += methods.sumOf { it.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (options.isNotEmpty()) {
        __result += options.sumOf { it.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (version.isNotEmpty()) {
        __result += WireSize.string(version).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += sourceContext.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (mixins.isNotEmpty()) {
        __result += mixins.sumOf { it.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (syntax != Syntax.SYNTAX_PROTO2) {
        __result += (WireSize.tag(7, WireType.VARINT) + WireSize.enum(syntax.number))
    }

    return __result
}

@InternalRpcApi
public fun Api.asInternal(): ApiInternal {
    return this as? ApiInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun MethodInternal.checkRequiredFields() {
    // no required fields to check
    options.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun MethodInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
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
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (syntax != Syntax.SYNTAX_PROTO2) {
        encoder.writeEnum(fieldNr = 7, value = syntax.number)
    }
}

@InternalRpcApi
public fun MethodInternal.Companion.decodeWith(msg: MethodInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.requestTypeUrl = decoder.readString()
            }
            tag.fieldNr == 3 && tag.wireType == WireType.VARINT -> {
                msg.requestStreaming = decoder.readBool()
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.responseTypeUrl = decoder.readString()
            }
            tag.fieldNr == 5 && tag.wireType == WireType.VARINT -> {
                msg.responseStreaming = decoder.readBool()
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                (msg.options as MutableList).add(elem)
            }
            tag.fieldNr == 7 && tag.wireType == WireType.VARINT -> {
                msg.syntax = Syntax.fromNumber(decoder.readEnum())
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun MethodInternal.computeSize(): Int {
    var __result = 0
    if (name.isNotEmpty()) {
        __result += WireSize.string(name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (requestTypeUrl.isNotEmpty()) {
        __result += WireSize.string(requestTypeUrl).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (requestStreaming != false) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.bool(requestStreaming))
    }

    if (responseTypeUrl.isNotEmpty()) {
        __result += WireSize.string(responseTypeUrl).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (responseStreaming != false) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.bool(responseStreaming))
    }

    if (options.isNotEmpty()) {
        __result += options.sumOf { it.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (syntax != Syntax.SYNTAX_PROTO2) {
        __result += (WireSize.tag(7, WireType.VARINT) + WireSize.enum(syntax.number))
    }

    return __result
}

@InternalRpcApi
public fun Method.asInternal(): MethodInternal {
    return this as? MethodInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
public fun MixinInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
public fun MixinInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (root.isNotEmpty()) {
        encoder.writeString(fieldNr = 2, value = root)
    }
}

@InternalRpcApi
public fun MixinInternal.Companion.decodeWith(msg: MixinInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.root = decoder.readString()
            }
            else -> {
                if (tag.wireType == WireType.END_GROUP) {
                    throw ProtobufDecodingException("Unexpected END_GROUP tag.")
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

private fun MixinInternal.computeSize(): Int {
    var __result = 0
    if (name.isNotEmpty()) {
        __result += WireSize.string(name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (root.isNotEmpty()) {
        __result += WireSize.string(root).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun Mixin.asInternal(): MixinInternal {
    return this as? MixinInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
