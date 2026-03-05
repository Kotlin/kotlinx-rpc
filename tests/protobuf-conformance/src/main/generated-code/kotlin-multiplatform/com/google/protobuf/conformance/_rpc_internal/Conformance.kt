@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.conformance

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

class TestStatusInternal: TestStatus.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var name: String by MsgFieldDelegate { "" }
    override var failureMessage: String by MsgFieldDelegate { "" }
    override var matchedName: String by MsgFieldDelegate { "" }

    private val _owner: TestStatusInternal = this

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = name.hashCode()
        result = 31 * result + failureMessage.hashCode()
        result = 31 * result + matchedName.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestStatusInternal
        other.checkRequiredFields()
        if (this.name != other.name) return false
        if (this.failureMessage != other.failureMessage) return false
        if (this.matchedName != other.matchedName) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("TestStatus(")
        builder.appendLine("${nextIndentString}name=${this.name},")
        builder.appendLine("${nextIndentString}failureMessage=${this.failureMessage},")
        builder.appendLine("${nextIndentString}matchedName=${this.matchedName},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): TestStatusInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: TestStatusInternal.() -> Unit): TestStatusInternal {
        val copy = TestStatusInternal()
        copy.name = this.name
        copy.failureMessage = this.failureMessage
        copy.matchedName = this.matchedName
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<TestStatus> {
        override fun encode(value: TestStatus, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): TestStatus {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TestStatusInternal()
                checkForPlatformDecodeException {
                    TestStatusInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<TestStatus> {
        override val fullName: String = "conformance.TestStatus"
    }

    @InternalRpcApi
    companion object
}

class FailureSetInternal: FailureSet.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var test: List<TestStatus> by MsgFieldDelegate { mutableListOf() }

    private val _owner: FailureSetInternal = this

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = test.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FailureSetInternal
        other.checkRequiredFields()
        if (this.test != other.test) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("FailureSet(")
        builder.appendLine("${nextIndentString}test=${this.test},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): FailureSetInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: FailureSetInternal.() -> Unit): FailureSetInternal {
        val copy = FailureSetInternal()
        copy.test = this.test.map { it.copy() }
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<FailureSet> {
        override fun encode(value: FailureSet, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): FailureSet {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = FailureSetInternal()
                checkForPlatformDecodeException {
                    FailureSetInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<FailureSet> {
        override val fullName: String = "conformance.FailureSet"
    }

    @InternalRpcApi
    companion object
}

class ConformanceRequestInternal: ConformanceRequest.Builder, InternalMessage(fieldsWithPresence = 1) {
    private object PresenceIndices {
        const val jspbEncodingOptions: Int = 0
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var requestedOutputFormat: WireFormat by MsgFieldDelegate { WireFormat.UNSPECIFIED }
    override var messageType: String by MsgFieldDelegate { "" }
    override var testCategory: TestCategory by MsgFieldDelegate { TestCategory.UNSPECIFIED_TEST }
    override var jspbEncodingOptions: JspbEncodingConfig by MsgFieldDelegate(PresenceIndices.jspbEncodingOptions) { JspbEncodingConfigInternal() }
    override var printUnknownFields: Boolean by MsgFieldDelegate { false }
    override var payload: ConformanceRequest.Payload? = null

    private val _owner: ConformanceRequestInternal = this

    @InternalRpcApi
    val _presence: ConformanceRequestPresence = object : ConformanceRequestPresence, InternalPresenceObject {
        override val _message: ConformanceRequestInternal get() = _owner

        override val hasJspbEncodingOptions: Boolean get() = presenceMask[0]
    }

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = requestedOutputFormat.hashCode()
        result = 31 * result + messageType.hashCode()
        result = 31 * result + testCategory.hashCode()
        result = 31 * result + if (presenceMask[0]) jspbEncodingOptions.hashCode() else 0
        result = 31 * result + printUnknownFields.hashCode()
        result = 31 * result + (payload?.oneOfHashCode() ?: 0)
        return result
    }

    fun ConformanceRequest.Payload.oneOfHashCode(): Int {
        return when (this) {
            is ConformanceRequest.Payload.ProtobufPayload -> value.contentHashCode() + 0
            is ConformanceRequest.Payload.JsonPayload -> hashCode() + 1
            is ConformanceRequest.Payload.JspbPayload -> hashCode() + 2
            is ConformanceRequest.Payload.TextPayload -> hashCode() + 3
        }
    }

    fun oneOfEquals(a: ConformanceRequest.Payload?, b: ConformanceRequest.Payload?): Boolean {
        if (a === b) return true
        if (a == null || b == null) return false
        if (a::class != b::class) return false
        return when (a) {
            is ConformanceRequest.Payload.ProtobufPayload -> a.value.contentEquals((b as ConformanceRequest.Payload.ProtobufPayload).value)
            is ConformanceRequest.Payload.JsonPayload -> a == b
            is ConformanceRequest.Payload.JspbPayload -> a == b
            is ConformanceRequest.Payload.TextPayload -> a == b
        }
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceRequestInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (this.requestedOutputFormat != other.requestedOutputFormat) return false
        if (this.messageType != other.messageType) return false
        if (this.testCategory != other.testCategory) return false
        if (presenceMask[0] && this.jspbEncodingOptions != other.jspbEncodingOptions) return false
        if (this.printUnknownFields != other.printUnknownFields) return false
        if (!oneOfEquals(this.payload, other.payload)) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ConformanceRequest(")
        builder.appendLine("${nextIndentString}requestedOutputFormat=${this.requestedOutputFormat},")
        builder.appendLine("${nextIndentString}messageType=${this.messageType},")
        builder.appendLine("${nextIndentString}testCategory=${this.testCategory},")
        if (presenceMask[0]) {
            builder.appendLine("${nextIndentString}jspbEncodingOptions=${this.jspbEncodingOptions.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}jspbEncodingOptions=<unset>,")
        }

        builder.appendLine("${nextIndentString}printUnknownFields=${this.printUnknownFields},")
        builder.appendLine("${nextIndentString}payload=${this.payload},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ConformanceRequestInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ConformanceRequestInternal.() -> Unit): ConformanceRequestInternal {
        val copy = ConformanceRequestInternal()
        copy.requestedOutputFormat = this.requestedOutputFormat
        copy.messageType = this.messageType
        copy.testCategory = this.testCategory
        if (presenceMask[0]) {
            copy.jspbEncodingOptions = this.jspbEncodingOptions.copy()
        }

        copy.printUnknownFields = this.printUnknownFields
        copy.payload = this.payload?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    fun ConformanceRequest.Payload.oneOfCopy(): ConformanceRequest.Payload {
        return when (this) {
            is ConformanceRequest.Payload.ProtobufPayload -> {
                ConformanceRequest.Payload.ProtobufPayload(this.value.copyOf())
            }
            is ConformanceRequest.Payload.JsonPayload -> {
                this
            }
            is ConformanceRequest.Payload.JspbPayload -> {
                this
            }
            is ConformanceRequest.Payload.TextPayload -> {
                this
            }
        }
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<ConformanceRequest> {
        override fun encode(value: ConformanceRequest, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): ConformanceRequest {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ConformanceRequestInternal()
                checkForPlatformDecodeException {
                    ConformanceRequestInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ConformanceRequest> {
        override val fullName: String = "conformance.ConformanceRequest"
    }

    @InternalRpcApi
    companion object
}

class ConformanceResponseInternal: ConformanceResponse.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var result: ConformanceResponse.Result? = null

    private val _owner: ConformanceResponseInternal = this

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = (result?.oneOfHashCode() ?: 0)
        return result
    }

    fun ConformanceResponse.Result.oneOfHashCode(): Int {
        return when (this) {
            is ConformanceResponse.Result.ParseError -> hashCode() + 0
            is ConformanceResponse.Result.SerializeError -> hashCode() + 1
            is ConformanceResponse.Result.TimeoutError -> hashCode() + 2
            is ConformanceResponse.Result.RuntimeError -> hashCode() + 3
            is ConformanceResponse.Result.ProtobufPayload -> value.contentHashCode() + 4
            is ConformanceResponse.Result.JsonPayload -> hashCode() + 5
            is ConformanceResponse.Result.Skipped -> hashCode() + 6
            is ConformanceResponse.Result.JspbPayload -> hashCode() + 7
            is ConformanceResponse.Result.TextPayload -> hashCode() + 8
        }
    }

    fun oneOfEquals(a: ConformanceResponse.Result?, b: ConformanceResponse.Result?): Boolean {
        if (a === b) return true
        if (a == null || b == null) return false
        if (a::class != b::class) return false
        return when (a) {
            is ConformanceResponse.Result.ParseError -> a == b
            is ConformanceResponse.Result.SerializeError -> a == b
            is ConformanceResponse.Result.TimeoutError -> a == b
            is ConformanceResponse.Result.RuntimeError -> a == b
            is ConformanceResponse.Result.ProtobufPayload -> a.value.contentEquals((b as ConformanceResponse.Result.ProtobufPayload).value)
            is ConformanceResponse.Result.JsonPayload -> a == b
            is ConformanceResponse.Result.Skipped -> a == b
            is ConformanceResponse.Result.JspbPayload -> a == b
            is ConformanceResponse.Result.TextPayload -> a == b
        }
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceResponseInternal
        other.checkRequiredFields()
        if (!oneOfEquals(this.result, other.result)) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ConformanceResponse(")
        builder.appendLine("${nextIndentString}result=${this.result},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ConformanceResponseInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ConformanceResponseInternal.() -> Unit): ConformanceResponseInternal {
        val copy = ConformanceResponseInternal()
        copy.result = this.result?.oneOfCopy()
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    fun ConformanceResponse.Result.oneOfCopy(): ConformanceResponse.Result {
        return when (this) {
            is ConformanceResponse.Result.ParseError -> {
                this
            }
            is ConformanceResponse.Result.SerializeError -> {
                this
            }
            is ConformanceResponse.Result.TimeoutError -> {
                this
            }
            is ConformanceResponse.Result.RuntimeError -> {
                this
            }
            is ConformanceResponse.Result.ProtobufPayload -> {
                ConformanceResponse.Result.ProtobufPayload(this.value.copyOf())
            }
            is ConformanceResponse.Result.JsonPayload -> {
                this
            }
            is ConformanceResponse.Result.Skipped -> {
                this
            }
            is ConformanceResponse.Result.JspbPayload -> {
                this
            }
            is ConformanceResponse.Result.TextPayload -> {
                this
            }
        }
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<ConformanceResponse> {
        override fun encode(value: ConformanceResponse, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): ConformanceResponse {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ConformanceResponseInternal()
                checkForPlatformDecodeException {
                    ConformanceResponseInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ConformanceResponse> {
        override val fullName: String = "conformance.ConformanceResponse"
    }

    @InternalRpcApi
    companion object
}

class JspbEncodingConfigInternal: JspbEncodingConfig.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var useJspbArrayAnyFormat: Boolean by MsgFieldDelegate { false }

    private val _owner: JspbEncodingConfigInternal = this

    override fun hashCode(): Int {
        checkRequiredFields()
        var result = useJspbArrayAnyFormat.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as JspbEncodingConfigInternal
        other.checkRequiredFields()
        if (this.useJspbArrayAnyFormat != other.useJspbArrayAnyFormat) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("JspbEncodingConfig(")
        builder.appendLine("${nextIndentString}useJspbArrayAnyFormat=${this.useJspbArrayAnyFormat},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): JspbEncodingConfigInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: JspbEncodingConfigInternal.() -> Unit): JspbEncodingConfigInternal {
        val copy = JspbEncodingConfigInternal()
        copy.useJspbArrayAnyFormat = this.useJspbArrayAnyFormat
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
    }

    @InternalRpcApi
    object MARSHALLER: MessageMarshaller<JspbEncodingConfig> {
        override fun encode(value: JspbEncodingConfig, config: MarshallerConfig?): Source {
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

        override fun decode(source: Source, config: MarshallerConfig?): JspbEncodingConfig {
            WireDecoder(source).use {
                (config as? ProtobufConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = JspbEncodingConfigInternal()
                checkForPlatformDecodeException {
                    JspbEncodingConfigInternal.decodeWith(msg, it, config as? ProtobufConfig)
                }
                msg.checkRequiredFields()
                msg._unknownFieldsEncoder?.flush()
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<JspbEncodingConfig> {
        override val fullName: String = "conformance.JspbEncodingConfig"
    }

    @InternalRpcApi
    companion object
}

@InternalRpcApi
fun TestStatusInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestStatusInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = this.name)
    }

    if (this.failureMessage.isNotEmpty()) {
        encoder.writeString(fieldNr = 2, value = this.failureMessage)
    }

    if (this.matchedName.isNotEmpty()) {
        encoder.writeString(fieldNr = 3, value = this.matchedName)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
fun TestStatusInternal.Companion.decodeWith(msg: TestStatusInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(TestStatus::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.failureMessage = decoder.readString()
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.matchedName = decoder.readString()
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

private fun TestStatusInternal.computeSize(): Int {
    var __result = 0
    if (this.name.isNotEmpty()) {
        __result += WireSize.string(this.name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.failureMessage.isNotEmpty()) {
        __result += WireSize.string(this.failureMessage).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.matchedName.isNotEmpty()) {
        __result += WireSize.string(this.matchedName).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    return __result
}

@InternalRpcApi
fun TestStatus.asInternal(): TestStatusInternal {
    return this as? TestStatusInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun FailureSetInternal.checkRequiredFields() {
    // no required fields to check
    this.test.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun FailureSetInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.test.isNotEmpty()) {
        this.test.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
fun FailureSetInternal.Companion.decodeWith(msg: FailureSetInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(FailureSet::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                val elem = TestStatusInternal()
                decoder.readMessage(elem.asInternal(), { msg, decoder -> TestStatusInternal.decodeWith(msg, decoder, config) })
                (msg.test as MutableList).add(elem)
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

private fun FailureSetInternal.computeSize(): Int {
    var __result = 0
    if (this.test.isNotEmpty()) {
        __result += this.test.sumOf { it.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    return __result
}

@InternalRpcApi
fun FailureSet.asInternal(): FailureSetInternal {
    return this as? FailureSetInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ConformanceRequestInternal.checkRequiredFields() {
    // no required fields to check
    if (presenceMask[0]) {
        this.jspbEncodingOptions.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun ConformanceRequestInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.requestedOutputFormat != WireFormat.UNSPECIFIED) {
        encoder.writeEnum(fieldNr = 3, value = this.requestedOutputFormat.number)
    }

    if (this.messageType.isNotEmpty()) {
        encoder.writeString(fieldNr = 4, value = this.messageType)
    }

    if (this.testCategory != TestCategory.UNSPECIFIED_TEST) {
        encoder.writeEnum(fieldNr = 5, value = this.testCategory.number)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 6, value = this.jspbEncodingOptions.asInternal()) { encodeWith(it, config) }
    }

    if (this.printUnknownFields != false) {
        encoder.writeBool(fieldNr = 9, value = this.printUnknownFields)
    }

    this.payload?.also {
        when (val value = it) {
            is ConformanceRequest.Payload.ProtobufPayload -> {
                encoder.writeBytes(fieldNr = 1, value = value.value)
            }
            is ConformanceRequest.Payload.JsonPayload -> {
                encoder.writeString(fieldNr = 2, value = value.value)
            }
            is ConformanceRequest.Payload.JspbPayload -> {
                encoder.writeString(fieldNr = 7, value = value.value)
            }
            is ConformanceRequest.Payload.TextPayload -> {
                encoder.writeString(fieldNr = 8, value = value.value)
            }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
fun ConformanceRequestInternal.Companion.decodeWith(msg: ConformanceRequestInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(ConformanceRequest::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 3 && tag.wireType == WireType.VARINT -> {
                msg.requestedOutputFormat = WireFormat.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.messageType = decoder.readString()
            }
            tag.fieldNr == 5 && tag.wireType == WireType.VARINT -> {
                msg.testCategory = TestCategory.fromNumber(decoder.readEnum())
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                if (!msg.presenceMask[0]) {
                    msg.jspbEncodingOptions = JspbEncodingConfigInternal()
                }

                decoder.readMessage(msg.jspbEncodingOptions.asInternal(), { msg, decoder -> JspbEncodingConfigInternal.decodeWith(msg, decoder, config) })
            }
            tag.fieldNr == 9 && tag.wireType == WireType.VARINT -> {
                msg.printUnknownFields = decoder.readBool()
            }
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.payload = ConformanceRequest.Payload.ProtobufPayload(decoder.readBytes())
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.payload = ConformanceRequest.Payload.JsonPayload(decoder.readString())
            }
            tag.fieldNr == 7 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.payload = ConformanceRequest.Payload.JspbPayload(decoder.readString())
            }
            tag.fieldNr == 8 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.payload = ConformanceRequest.Payload.TextPayload(decoder.readString())
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

private fun ConformanceRequestInternal.computeSize(): Int {
    var __result = 0
    if (this.requestedOutputFormat != WireFormat.UNSPECIFIED) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.enum(this.requestedOutputFormat.number))
    }

    if (this.messageType.isNotEmpty()) {
        __result += WireSize.string(this.messageType).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.testCategory != TestCategory.UNSPECIFIED_TEST) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.enum(this.testCategory.number))
    }

    if (presenceMask[0]) {
        __result += this.jspbEncodingOptions.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.printUnknownFields != false) {
        __result += (WireSize.tag(9, WireType.VARINT) + WireSize.bool(this.printUnknownFields))
    }

    this.payload?.also {
        when (val value = it) {
            is ConformanceRequest.Payload.ProtobufPayload -> {
                __result += WireSize.bytes(value.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceRequest.Payload.JsonPayload -> {
                __result += WireSize.string(value.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceRequest.Payload.JspbPayload -> {
                __result += WireSize.string(value.value).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceRequest.Payload.TextPayload -> {
                __result += WireSize.string(value.value).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
        }
    }

    return __result
}

@InternalRpcApi
fun ConformanceRequest.asInternal(): ConformanceRequestInternal {
    return this as? ConformanceRequestInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ConformanceResponseInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun ConformanceResponseInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    this.result?.also {
        when (val value = it) {
            is ConformanceResponse.Result.ParseError -> {
                encoder.writeString(fieldNr = 1, value = value.value)
            }
            is ConformanceResponse.Result.SerializeError -> {
                encoder.writeString(fieldNr = 6, value = value.value)
            }
            is ConformanceResponse.Result.TimeoutError -> {
                encoder.writeString(fieldNr = 9, value = value.value)
            }
            is ConformanceResponse.Result.RuntimeError -> {
                encoder.writeString(fieldNr = 2, value = value.value)
            }
            is ConformanceResponse.Result.ProtobufPayload -> {
                encoder.writeBytes(fieldNr = 3, value = value.value)
            }
            is ConformanceResponse.Result.JsonPayload -> {
                encoder.writeString(fieldNr = 4, value = value.value)
            }
            is ConformanceResponse.Result.Skipped -> {
                encoder.writeString(fieldNr = 5, value = value.value)
            }
            is ConformanceResponse.Result.JspbPayload -> {
                encoder.writeString(fieldNr = 7, value = value.value)
            }
            is ConformanceResponse.Result.TextPayload -> {
                encoder.writeString(fieldNr = 8, value = value.value)
            }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
fun ConformanceResponseInternal.Companion.decodeWith(msg: ConformanceResponseInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(ConformanceResponse::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.ParseError(decoder.readString())
            }
            tag.fieldNr == 6 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.SerializeError(decoder.readString())
            }
            tag.fieldNr == 9 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.TimeoutError(decoder.readString())
            }
            tag.fieldNr == 2 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.RuntimeError(decoder.readString())
            }
            tag.fieldNr == 3 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.ProtobufPayload(decoder.readBytes())
            }
            tag.fieldNr == 4 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.JsonPayload(decoder.readString())
            }
            tag.fieldNr == 5 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.Skipped(decoder.readString())
            }
            tag.fieldNr == 7 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.JspbPayload(decoder.readString())
            }
            tag.fieldNr == 8 && tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.TextPayload(decoder.readString())
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

private fun ConformanceResponseInternal.computeSize(): Int {
    var __result = 0
    this.result?.also {
        when (val value = it) {
            is ConformanceResponse.Result.ParseError -> {
                __result += WireSize.string(value.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.SerializeError -> {
                __result += WireSize.string(value.value).let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.TimeoutError -> {
                __result += WireSize.string(value.value).let { WireSize.tag(9, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.RuntimeError -> {
                __result += WireSize.string(value.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.ProtobufPayload -> {
                __result += WireSize.bytes(value.value).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.JsonPayload -> {
                __result += WireSize.string(value.value).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.Skipped -> {
                __result += WireSize.string(value.value).let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.JspbPayload -> {
                __result += WireSize.string(value.value).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.TextPayload -> {
                __result += WireSize.string(value.value).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
        }
    }

    return __result
}

@InternalRpcApi
fun ConformanceResponse.asInternal(): ConformanceResponseInternal {
    return this as? ConformanceResponseInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun JspbEncodingConfigInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun JspbEncodingConfigInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (this.useJspbArrayAnyFormat != false) {
        encoder.writeBool(fieldNr = 1, value = this.useJspbArrayAnyFormat)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }
}

@InternalRpcApi
fun JspbEncodingConfigInternal.Companion.decodeWith(msg: JspbEncodingConfigInternal, decoder: WireDecoder, config: ProtobufConfig?) {
    val knownExtensions = config?.extensionRegistry?.allExtensionsForMessage(JspbEncodingConfig::class) ?: emptyMap()
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when {
            tag.fieldNr == 1 && tag.wireType == WireType.VARINT -> {
                msg.useJspbArrayAnyFormat = decoder.readBool()
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

private fun JspbEncodingConfigInternal.computeSize(): Int {
    var __result = 0
    if (this.useJspbArrayAnyFormat != false) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.useJspbArrayAnyFormat))
    }

    return __result
}

@InternalRpcApi
fun JspbEncodingConfig.asInternal(): JspbEncodingConfigInternal {
    return this as? JspbEncodingConfigInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun WireFormat.Companion.fromNumber(number: Int): WireFormat {
    return when (number) {
        0 -> {
            WireFormat.UNSPECIFIED
        }
        1 -> {
            WireFormat.PROTOBUF
        }
        2 -> {
            WireFormat.JSON
        }
        3 -> {
            WireFormat.JSPB
        }
        4 -> {
            WireFormat.TEXT_FORMAT
        }
        else -> {
            WireFormat.UNRECOGNIZED(number)
        }
    }
}

@InternalRpcApi
fun TestCategory.Companion.fromNumber(number: Int): TestCategory {
    return when (number) {
        0 -> {
            TestCategory.UNSPECIFIED_TEST
        }
        1 -> {
            TestCategory.BINARY_TEST
        }
        2 -> {
            TestCategory.JSON_TEST
        }
        3 -> {
            TestCategory.JSON_IGNORE_UNKNOWN_PARSING_TEST
        }
        4 -> {
            TestCategory.JSPB_TEST
        }
        5 -> {
            TestCategory.TEXT_FORMAT_TEST
        }
        else -> {
            TestCategory.UNRECOGNIZED(number)
        }
    }
}
