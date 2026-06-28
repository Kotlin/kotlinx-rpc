@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
@file:Suppress("PropertyName", "CanBeVal", "ConstPropertyName", "LocalVariableName", "DuplicatedCode")

package com.google.protobuf.conformance

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.isNotEmpty
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.InternalPresenceObject
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
import kotlinx.rpc.protobuf.internal.ProtoDescriptor
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

@InternalRpcApi
class TestStatusInternal: TestStatus.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __nameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    override var name: String by __nameDelegate
    internal val __failureMessageDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    override var failureMessage: String by __failureMessageDelegate
    internal val __matchedNameDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    override var matchedName: String by __matchedNameDelegate

    override fun hashCode(): Int {
        var result = this.name.hashCode()
        result = 31 * result + this.failureMessage.hashCode()
        result = 31 * result + this.matchedName.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestStatusInternal
        if (this.name != other.name) return false
        if (this.failureMessage != other.failureMessage) return false
        return this.matchedName == other.matchedName
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
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
    object MARSHALLER: GrpcMarshaller<TestStatus> {
        override fun encode(value: TestStatus, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): TestStatus {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = TestStatusInternal()
                checkForPlatformDecodeException {
                    TestStatusInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<TestStatus> {
        override val fullName: String = "conformance.TestStatus"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: TestStatus by lazy { TestStatusInternal() }
    }
}

@InternalRpcApi
class FailureSetInternal: FailureSet.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __testDelegate: MsgFieldDelegate<List<TestStatus>> = MsgFieldDelegate { emptyList() }
    override var test: List<TestStatus> by __testDelegate

    override fun hashCode(): Int {
        var result = this.test.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FailureSetInternal
        return this.test == other.test
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
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
    object MARSHALLER: GrpcMarshaller<FailureSet> {
        override fun encode(value: FailureSet, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): FailureSet {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = FailureSetInternal()
                checkForPlatformDecodeException {
                    FailureSetInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<FailureSet> {
        override val fullName: String = "conformance.FailureSet"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: FailureSet by lazy { FailureSetInternal() }
    }
}

@InternalRpcApi
class ConformanceRequestInternal: ConformanceRequest.Builder, InternalMessage(fieldsWithPresence = 1) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val jspbEncodingOptions: Int = 0
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __requestedOutputFormatDelegate: MsgFieldDelegate<WireFormat> = MsgFieldDelegate { WireFormat.UNSPECIFIED }
    override var requestedOutputFormat: WireFormat by __requestedOutputFormatDelegate
    internal val __messageTypeDelegate: MsgFieldDelegate<String> = MsgFieldDelegate { "" }
    override var messageType: String by __messageTypeDelegate
    internal val __testCategoryDelegate: MsgFieldDelegate<TestCategory> = MsgFieldDelegate { TestCategory.UNSPECIFIED_TEST }
    override var testCategory: TestCategory by __testCategoryDelegate
    internal val __jspbEncodingOptionsDelegate: MsgFieldDelegate<JspbEncodingConfig> = MsgFieldDelegate(PresenceIndices.jspbEncodingOptions) { JspbEncodingConfigInternal.DEFAULT }
    override var jspbEncodingOptions: JspbEncodingConfig by __jspbEncodingOptionsDelegate
    override fun clearJspbEncodingOptions() {
        __jspbEncodingOptionsDelegate.clearField(this)
    }

    internal val __printUnknownFieldsDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate { false }
    override var printUnknownFields: Boolean by __printUnknownFieldsDelegate
    override var payload: ConformanceRequest.Payload? = null

    private val _owner: ConformanceRequestInternal = this

    @InternalRpcApi
    val _presence: ConformanceRequestPresence = object : ConformanceRequestPresence, InternalPresenceObject {
        override val _message: ConformanceRequestInternal get() = _owner

        override val hasJspbEncodingOptions: Boolean get() = presenceMask[PresenceIndices.jspbEncodingOptions]
    }

    override fun hashCode(): Int {
        var result = this.requestedOutputFormat.hashCode()
        result = 31 * result + this.messageType.hashCode()
        result = 31 * result + this.testCategory.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.jspbEncodingOptions]) this.jspbEncodingOptions.hashCode() else 0
        result = 31 * result + this.printUnknownFields.hashCode()
        result = 31 * result + (this.payload?.oneOfHashCode() ?: 0)
        return result
    }

    fun ConformanceRequest.Payload.oneOfHashCode(): Int {
        val offset = when (this) {
            is ConformanceRequest.Payload.ProtobufPayload -> 0
            is ConformanceRequest.Payload.JsonPayload -> 1
            is ConformanceRequest.Payload.JspbPayload -> 2
            is ConformanceRequest.Payload.TextPayload -> 3
        }

        return hashCode() + offset
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceRequestInternal
        if (presenceMask != other.presenceMask) return false
        if (this.requestedOutputFormat != other.requestedOutputFormat) return false
        if (this.messageType != other.messageType) return false
        if (this.testCategory != other.testCategory) return false
        if (presenceMask[PresenceIndices.jspbEncodingOptions] && this.jspbEncodingOptions != other.jspbEncodingOptions) return false
        if (this.printUnknownFields != other.printUnknownFields) return false
        return this.payload == other.payload
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ConformanceRequest(")
        builder.appendLine("${nextIndentString}requestedOutputFormat=${this.requestedOutputFormat},")
        builder.appendLine("${nextIndentString}messageType=${this.messageType},")
        builder.appendLine("${nextIndentString}testCategory=${this.testCategory},")
        if (presenceMask[PresenceIndices.jspbEncodingOptions]) {
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
        if (presenceMask[PresenceIndices.jspbEncodingOptions]) {
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
        return this
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<ConformanceRequest> {
        override fun encode(value: ConformanceRequest, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): ConformanceRequest {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ConformanceRequestInternal()
                checkForPlatformDecodeException {
                    ConformanceRequestInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ConformanceRequest> {
        override val fullName: String = "conformance.ConformanceRequest"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: ConformanceRequest by lazy { ConformanceRequestInternal() }
    }
}

@InternalRpcApi
class ConformanceResponseInternal: ConformanceResponse.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var result: ConformanceResponse.Result? = null

    override fun hashCode(): Int {
        var result = (this.result?.oneOfHashCode() ?: 0)
        return result
    }

    fun ConformanceResponse.Result.oneOfHashCode(): Int {
        val offset = when (this) {
            is ConformanceResponse.Result.ParseError -> 0
            is ConformanceResponse.Result.SerializeError -> 1
            is ConformanceResponse.Result.TimeoutError -> 2
            is ConformanceResponse.Result.RuntimeError -> 3
            is ConformanceResponse.Result.ProtobufPayload -> 4
            is ConformanceResponse.Result.JsonPayload -> 5
            is ConformanceResponse.Result.Skipped -> 6
            is ConformanceResponse.Result.JspbPayload -> 7
            is ConformanceResponse.Result.TextPayload -> 8
        }

        return hashCode() + offset
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceResponseInternal
        return this.result == other.result
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
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
        return this
    }

    @InternalRpcApi
    object MARSHALLER: GrpcMarshaller<ConformanceResponse> {
        override fun encode(value: ConformanceResponse, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): ConformanceResponse {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = ConformanceResponseInternal()
                checkForPlatformDecodeException {
                    ConformanceResponseInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<ConformanceResponse> {
        override val fullName: String = "conformance.ConformanceResponse"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: ConformanceResponse by lazy { ConformanceResponseInternal() }
    }
}

@InternalRpcApi
class JspbEncodingConfigInternal: JspbEncodingConfig.Builder, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    internal val __useJspbArrayAnyFormatDelegate: MsgFieldDelegate<Boolean> = MsgFieldDelegate { false }
    override var useJspbArrayAnyFormat: Boolean by __useJspbArrayAnyFormatDelegate

    override fun hashCode(): Int {
        var result = this.useJspbArrayAnyFormat.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as JspbEncodingConfigInternal
        return this.useJspbArrayAnyFormat == other.useJspbArrayAnyFormat
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
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
    object MARSHALLER: GrpcMarshaller<JspbEncodingConfig> {
        override fun encode(value: JspbEncodingConfig, config: GrpcMarshallerConfig?): Source {
            val buffer = Buffer()
            val encoder = WireEncoder(buffer)
            val internalMsg = value.asInternal()
            checkForPlatformEncodeException {
                internalMsg.encodeWith(encoder, config as? ProtoConfig)
            }
            encoder.flush()
            return buffer
        }

        override fun decode(source: Source, config: GrpcMarshallerConfig?): JspbEncodingConfig {
            WireDecoder(source).use {
                (config as? ProtoConfig)?.let { pbConfig -> it.recursionLimit = pbConfig.recursionLimit }
                val msg = JspbEncodingConfigInternal()
                checkForPlatformDecodeException {
                    JspbEncodingConfigInternal.decodeWith(msg, it, config as? ProtoConfig)
                }
                return msg
            }
        }
    }

    @InternalRpcApi
    object DESCRIPTOR: ProtoDescriptor<JspbEncodingConfig> {
        override val fullName: String = "conformance.JspbEncodingConfig"
    }

    @InternalRpcApi
    companion object {
        val DEFAULT: JspbEncodingConfig by lazy { JspbEncodingConfigInternal() }
    }
}

@InternalRpcApi
fun TestStatusInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
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

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun TestStatusInternal.Companion.decodeWith(msg: TestStatusInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.name = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.failureMessage = decoder.readString()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
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

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun TestStatus.asInternal(): TestStatusInternal {
    return this as? TestStatusInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun FailureSetInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.test.isNotEmpty()) {
        this.test.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encoder -> encodeWith(encoder, config) }
        }
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun FailureSetInternal.Companion.decodeWith(msg: FailureSetInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__testDelegate.getOrCreate(msg) { mutableListOf() } as MutableList
                val elem = TestStatusInternal()
                decoder.readMessage(elem.asInternal()) { msg, decoder -> TestStatusInternal.decodeWith(msg, decoder, config) }
                target.add(elem)
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

private fun FailureSetInternal.computeSize(): Int {
    var __result = 0
    if (this.test.isNotEmpty()) {
        __result += this.test.sumOf { element -> element.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun FailureSet.asInternal(): FailureSetInternal {
    return this as? FailureSetInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ConformanceRequestInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.requestedOutputFormat != WireFormat.UNSPECIFIED) {
        encoder.writeEnum(fieldNr = 3, value = this.requestedOutputFormat.number)
    }

    if (this.messageType.isNotEmpty()) {
        encoder.writeString(fieldNr = 4, value = this.messageType)
    }

    if (this.testCategory != TestCategory.UNSPECIFIED_TEST) {
        encoder.writeEnum(fieldNr = 5, value = this.testCategory.number)
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.jspbEncodingOptions]) {
        encoder.writeMessage(fieldNr = 6, value = this.jspbEncodingOptions.asInternal()) { encoder -> encodeWith(encoder, config) }
    }

    if (this.printUnknownFields) {
        encoder.writeBool(fieldNr = 9, value = this.printUnknownFields)
    }

    this.payload?.also { value ->
        when (value) {
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

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun ConformanceRequestInternal.Companion.decodeWith(msg: ConformanceRequestInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            3 if tag.wireType == WireType.VARINT -> {
                msg.requestedOutputFormat = WireFormat.fromNumber(decoder.readEnum())
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.messageType = decoder.readString()
            }
            5 if tag.wireType == WireType.VARINT -> {
                msg.testCategory = TestCategory.fromNumber(decoder.readEnum())
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                val target = msg.__jspbEncodingOptionsDelegate.getOrCreate(msg) { JspbEncodingConfigInternal() }
                decoder.readMessage(target.asInternal()) { msg, decoder -> JspbEncodingConfigInternal.decodeWith(msg, decoder, config) }
            }
            9 if tag.wireType == WireType.VARINT -> {
                msg.printUnknownFields = decoder.readBool()
            }
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.payload = ConformanceRequest.Payload.ProtobufPayload(decoder.readBytes())
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.payload = ConformanceRequest.Payload.JsonPayload(decoder.readString())
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.payload = ConformanceRequest.Payload.JspbPayload(decoder.readString())
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ConformanceRequestInternal.computeSize(): Int {
    var __result = 0
    if (this.requestedOutputFormat != WireFormat.UNSPECIFIED) {
        __result += WireSize.tag(3, WireType.VARINT) + WireSize.enum(this.requestedOutputFormat.number)
    }

    if (this.messageType.isNotEmpty()) {
        __result += WireSize.string(this.messageType).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.testCategory != TestCategory.UNSPECIFIED_TEST) {
        __result += WireSize.tag(5, WireType.VARINT) + WireSize.enum(this.testCategory.number)
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.jspbEncodingOptions]) {
        __result += this.jspbEncodingOptions.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (this.printUnknownFields) {
        __result += WireSize.tag(9, WireType.VARINT) + WireSize.bool(this.printUnknownFields)
    }

    this.payload?.also { value ->
        __result += when (value) {
            is ConformanceRequest.Payload.ProtobufPayload -> {
                WireSize.bytes(value.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceRequest.Payload.JsonPayload -> {
                WireSize.string(value.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceRequest.Payload.JspbPayload -> {
                WireSize.string(value.value).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceRequest.Payload.TextPayload -> {
                WireSize.string(value.value).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
        }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun ConformanceRequest.asInternal(): ConformanceRequestInternal {
    return this as? ConformanceRequestInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ConformanceResponseInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    this.result?.also { value ->
        when (value) {
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

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun ConformanceResponseInternal.Companion.decodeWith(msg: ConformanceResponseInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.ParseError(decoder.readString())
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.SerializeError(decoder.readString())
            }
            9 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.TimeoutError(decoder.readString())
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.RuntimeError(decoder.readString())
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.ProtobufPayload(decoder.readBytes())
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.JsonPayload(decoder.readString())
            }
            5 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.Skipped(decoder.readString())
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.result = ConformanceResponse.Result.JspbPayload(decoder.readString())
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun ConformanceResponseInternal.computeSize(): Int {
    var __result = 0
    this.result?.also { value ->
        __result += when (value) {
            is ConformanceResponse.Result.ParseError -> {
                WireSize.string(value.value).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.SerializeError -> {
                WireSize.string(value.value).let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.TimeoutError -> {
                WireSize.string(value.value).let { WireSize.tag(9, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.RuntimeError -> {
                WireSize.string(value.value).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.ProtobufPayload -> {
                WireSize.bytes(value.value).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.JsonPayload -> {
                WireSize.string(value.value).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.Skipped -> {
                WireSize.string(value.value).let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.JspbPayload -> {
                WireSize.string(value.value).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
            is ConformanceResponse.Result.TextPayload -> {
                WireSize.string(value.value).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
            }
        }
    }

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun ConformanceResponse.asInternal(): ConformanceResponseInternal {
    return this as? ConformanceResponseInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun JspbEncodingConfigInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (this.useJspbArrayAnyFormat) {
        encoder.writeBool(fieldNr = 1, value = this.useJspbArrayAnyFormat)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun JspbEncodingConfigInternal.Companion.decodeWith(msg: JspbEncodingConfigInternal, decoder: WireDecoder, config: ProtoConfig?) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.VARINT -> {
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

    msg._unknownFieldsEncoder?.flush()
    msg._unknownFieldsEncoder = null
}

private fun JspbEncodingConfigInternal.computeSize(): Int {
    var __result = 0
    if (this.useJspbArrayAnyFormat) {
        __result += WireSize.tag(1, WireType.VARINT) + WireSize.bool(this.useJspbArrayAnyFormat)
    }

    __result += _unknownFields.size.toInt()
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
