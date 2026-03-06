@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.marshaller.MarshallerConfig
import kotlinx.rpc.grpc.marshaller.MessageMarshaller
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.InternalPresenceObject
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

public class ApiInternal: Api.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        public const val sourceContext: Int = 0
    }

    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    public override var name: String by MsgFieldDelegate { "" }
    public override var methods: List<Method> by MsgFieldDelegate { mutableListOf() }
    public override var options: List<Option> by MsgFieldDelegate { mutableListOf() }
    public override var version: String by MsgFieldDelegate { "" }
    public override var sourceContext: SourceContext by MsgFieldDelegate(PresenceIndices.sourceContext) { SourceContextInternal() }
    public override var mixins: List<Mixin> by MsgFieldDelegate { mutableListOf() }
    public override var syntax: Syntax by MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }

    private val _owner: ApiInternal = this

    @InternalRpcApi
    public val _presence: ApiPresence = object : ApiPresence, InternalPresenceObject {
        public override val _message: ApiInternal get() = _owner

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
        if (this.name != other.name) return false
        if (this.methods != other.methods) return false
        if (this.options != other.options) return false
        if (this.version != other.version) return false
        if (presenceMask[0] && this.sourceContext != other.sourceContext) return false
        if (this.mixins != other.mixins) return false
        if (this.syntax != other.syntax) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("Api(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}methods=${this.methods},")
        builder.appendLine("${nextIndentString}options=${this.options},")
        builder.appendLine("${nextIndentString}version=${this.version},")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}sourceContext=${this.sourceContext.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}sourceContext=<unset>,")
        }

        builder.appendLine("${nextIndentString}mixins=${this.mixins},")
        builder.appendLine("${nextIndentString}syntax=${this.syntax},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): ApiInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<Api> {
        public override fun encode(value: Api, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Api {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class MethodInternal: Method.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

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
        if (this.name != other.name) return false
        if (this.requestTypeUrl != other.requestTypeUrl) return false
        if (this.requestStreaming != other.requestStreaming) return false
        if (this.responseTypeUrl != other.responseTypeUrl) return false
        if (this.responseStreaming != other.responseStreaming) return false
        if (this.options != other.options) return false
        if (this.syntax != other.syntax) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("Method(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}requestTypeUrl=${this.requestTypeUrl},")
        builder.appendLine("${nextIndentString}requestStreaming=${this.requestStreaming},")
        builder.appendLine("${nextIndentString}responseTypeUrl=${this.responseTypeUrl},")
        builder.appendLine("${nextIndentString}responseStreaming=${this.responseStreaming},")
        builder.appendLine("${nextIndentString}options=${this.options},")
        builder.appendLine("${nextIndentString}syntax=${this.syntax},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): MethodInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<Method> {
        public override fun encode(value: Method, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Method {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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

public class MixinInternal: Mixin.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

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
        if (this.name != other.name) return false
        if (this.root != other.root) return false
        return true
    }

    public override fun toString(): String {
        return asString()
    }

    public fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("Mixin(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}root=${this.root},")
        builder.append("${indentString})")
        return builder.toString()
    }

    public override fun copyInternal(): MixinInternal {
        return copyInternal { }
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
    public object MARSHALLER: MessageMarshaller<Mixin> {
        public override fun encode(value: Mixin, config: MarshallerConfig?): Source {
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

        public override fun decode(source: Source, config: MarshallerConfig?): Mixin {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
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
        this.sourceContext.asInternal().checkRequiredFields()
    }

    this.methods.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.options.forEach {
        it.asInternal().checkRequiredFields()
    }

    this.mixins.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun ApiInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.methods.isNotEmpty()) {
        this.methods.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.options.isNotEmpty()) {
        this.options.forEach {
            encoder.writeMessage(fieldNr = 3, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.version.isNotEmpty()) {
        encoder.writeString(fieldNr = 4, value = this.version)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 5, value = this.sourceContext.asInternal()) { encodeWith(it, config) }
    }

    if (this.mixins.isNotEmpty()) {
        this.mixins.forEach {
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        encoder.writeEnum(fieldNr = 7, value = this.syntax.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun ApiInternal.Companion.decodeWith(msg: ApiInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(Api::class) ?: emptyMap()
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
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.methods.isNotEmpty()) {
        __result += this.methods.sumOf { it.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.options.isNotEmpty()) {
        __result += this.options.sumOf { it.asInternal()._size.let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.version.isNotEmpty()) {
        __result += WireSize.string(this.version).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[0]) {
        __result += this.sourceContext.asInternal()._size.let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.mixins.isNotEmpty()) {
        __result += this.mixins.sumOf { it.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        __result += (WireSize.tag(7, WireType.VARINT) + WireSize.enum(this.syntax.number))
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
    this.options.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
public fun MethodInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.requestTypeUrl.isNotEmpty()) {
        encoder.writeString(fieldNr = 2, value = this.requestTypeUrl)
    }

    if (this.requestStreaming != false) {
        encoder.writeBool(fieldNr = 3, value = this.requestStreaming)
    }

    if (this.responseTypeUrl.isNotEmpty()) {
        encoder.writeString(fieldNr = 4, value = this.responseTypeUrl)
    }

    if (this.responseStreaming != false) {
        encoder.writeBool(fieldNr = 5, value = this.responseStreaming)
    }

    if (this.options.isNotEmpty()) {
        this.options.forEach {
            encoder.writeMessage(fieldNr = 6, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        encoder.writeEnum(fieldNr = 7, value = this.syntax.number)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun MethodInternal.Companion.decodeWith(msg: MethodInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(Method::class) ?: emptyMap()
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
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.requestTypeUrl.isNotEmpty()) {
        __result += WireSize.string(this.requestTypeUrl).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.requestStreaming != false) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.bool(this.requestStreaming))
    }

    if (this.responseTypeUrl.isNotEmpty()) {
        __result += WireSize.string(this.responseTypeUrl).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.responseStreaming != false) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.bool(this.responseStreaming))
    }

    if (this.options.isNotEmpty()) {
        __result += this.options.sumOf { it.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    if (this.syntax != Syntax.SYNTAX_PROTO2) {
        __result += (WireSize.tag(7, WireType.VARINT) + WireSize.enum(this.syntax.number))
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
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.root.isNotEmpty()) {
        encoder.writeString(fieldNr = 2, value = this.root)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
public fun MixinInternal.Companion.decodeWith(msg: MixinInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(Mixin::class) ?: emptyMap()
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
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.root.isNotEmpty()) {
        __result += WireSize.string(this.root).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
public fun Mixin.asInternal(): MixinInternal {
    return this as? MixinInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
