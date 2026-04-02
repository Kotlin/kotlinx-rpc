@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.kotlin

import com.google.protobuf.kotlin.asInternal
import com.google.protobuf.kotlin.checkRequiredFields
import com.google.protobuf.kotlin.copy
import com.google.protobuf.kotlin.decodeWith
import com.google.protobuf.kotlin.encodeWith
import com.google.protobuf.kotlin.fromNumber
import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.isNotEmpty
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
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
import kotlinx.rpc.protobuf.internal.protoToString
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

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __methodsDelegate: MsgFieldDelegate<List<Method>> = MsgFieldDelegate { emptyList() }
    public override var methods: List<Method> by __methodsDelegate
    internal val __optionsDelegate: MsgFieldDelegate<List<Option>> = MsgFieldDelegate { emptyList() }
    public override var options: List<Option> by __optionsDelegate
    internal val __versionDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var version: String by __versionDelegate
    internal val __sourceContextDelegate: MsgFieldDelegate<SourceContext> = MsgFieldDelegate(PresenceIndices.sourceContext) { SourceContextInternal.DEFAULT }
    public override var sourceContext: SourceContext by __sourceContextDelegate
    public override fun clearSourceContext() {
        __sourceContextDelegate.clearField(this)
    }

    internal val __mixinsDelegate: MsgFieldDelegate<List<Mixin>> = MsgFieldDelegate { emptyList() }
    public override var mixins: List<Mixin> by __mixinsDelegate
    internal val __syntaxDelegate: MsgFieldDelegate<Syntax> = MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }
    public override var syntax: Syntax by __syntaxDelegate

    private val _owner: ApiInternal = this

    @InternalRpcApi
    public val _presence: ApiPresence = object : ApiPresence, InternalPresenceObject {
        public override val _message: ApiInternal get() = _owner

        public override val hasSourceContext: Boolean get() = presenceMask[0]
    }

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + this.methods.hashCode()
        result = 31 * result + this.options.hashCode()
        result = 31 * result + this.version.hashCode()
        result = 31 * result + if (presenceMask[0]) this.sourceContext.hashCode() else 0
        result = 31 * result + this.mixins.hashCode()
        result = 31 * result + this.syntax.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<Api> {
        public override fun encode(value: Api, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Api {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ApiInternal()
                checkForPlatformDecodeException {
                    ApiInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Api> {
        public override val fullName: String = "google.protobuf.Api"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Api by lazy { ApiInternal() }
    }
}

public class MethodInternal: Method.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __requestTypeUrlDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var requestTypeUrl: String by __requestTypeUrlDelegate
    internal val __requestStreamingDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate { false }
    public override var requestStreaming: Boolean by __requestStreamingDelegate
    internal val __responseTypeUrlDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var responseTypeUrl: String by __responseTypeUrlDelegate
    internal val __responseStreamingDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate { false }
    public override var responseStreaming: Boolean by __responseStreamingDelegate
    internal val __optionsDelegate: MsgFieldDelegate<List<Option>> = MsgFieldDelegate { emptyList() }
    public override var options: List<Option> by __optionsDelegate
    internal val __syntaxDelegate: MsgFieldDelegate<Syntax> = MsgFieldDelegate { Syntax.SYNTAX_PROTO2 }
    public override var syntax: Syntax by __syntaxDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + this.requestTypeUrl.hashCode()
        result = 31 * result + this.requestStreaming.hashCode()
        result = 31 * result + this.responseTypeUrl.hashCode()
        result = 31 * result + this.responseStreaming.hashCode()
        result = 31 * result + this.options.hashCode()
        result = 31 * result + this.syntax.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<Method> {
        public override fun encode(value: Method, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Method {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = MethodInternal()
                checkForPlatformDecodeException {
                    MethodInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Method> {
        public override val fullName: String = "google.protobuf.Method"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Method by lazy { MethodInternal() }
    }
}

public class MixinInternal: Mixin.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    public override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    public override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var name: String by __nameDelegate
    internal val __rootDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    public override var root: String by __rootDelegate

    public override fun hashCode(): Int {
        checkRequiredFields()
        var result = this.name.hashCode()
        result = 31 * result + this.root.hashCode()
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
    public object MARSHALLER: GrpcMarshaller<Mixin> {
        public override fun encode(value: Mixin, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        public override fun decode(source: Source, config: GrpcMarshallerConfig?): Mixin {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = MixinInternal()
                checkForPlatformDecodeException {
                    MixinInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                msg.checkRequiredFields()
                return msg
            }
        }
    }

    @InternalRpcApi
    public object DESCRIPTOR: ProtoDescriptor<Mixin> {
        public override val fullName: String = "google.protobuf.Mixin"
    }

    @InternalRpcApi
    public companion object {
        public val DEFAULT: Mixin by lazy { MixinInternal() }
    }
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
public fun ApiInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun ApiInternal.Companion.decodeWith(msg: ApiInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__methodsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = MethodInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> MethodInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__optionsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.version = decoder.readString()
            }
            tag.fieldNr == 5 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__sourceContextDelegate.getOrCreate(msg) { SourceContextInternal() }
                decoder.readMessage(target.asInternal(), { msg, decoder -> SourceContextInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__mixinsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = MixinInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> MixinInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
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

    __result += _unknownFields.size.toInt()
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
public fun MethodInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun MethodInternal.Companion.decodeWith(msg: MethodInternal, decoder: WireDecoder, config: ProtoConfig?) {
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
                val target = msg.__optionsDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = OptionInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> OptionInternal.decodeWith(msg, decoder, config) })
                target.add(elem)
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
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

    __result += _unknownFields.size.toInt()
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
public fun MixinInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
public fun MixinInternal.Companion.decodeWith(msg: MixinInternal, decoder: WireDecoder, config: ProtoConfig?) {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun MixinInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.root.isNotEmpty()) {
        __result += WireSize.string(this.root).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
public fun Mixin.asInternal(): MixinInternal {
    return this as? MixinInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}
