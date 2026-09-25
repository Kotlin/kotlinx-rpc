@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
@file:Suppress("PropertyName", "CanBeVal", "ConstPropertyName", "LocalVariableName", "DuplicatedCode")

package com.google.protobuf.conformance

import kotlin.reflect.cast
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.GeneratedProtoOneOfs
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
@GeneratedProtoOneOfs(names = ["payload"])
class ConformanceRequestInternal: ConformanceRequest.Builder, InternalMessage(fieldsWithPresence = 5) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val protobufPayload: Int = 0
        const val jsonPayload: Int = 1
        const val jspbPayload: Int = 2
        const val textPayload: Int = 3
        const val jspbEncodingOptions: Int = 4
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    private var _payloadRef: Any? = null

    @InternalRpcApi
    val _payloadCase: ConformanceRequestPayloadCase get() = when {
        presenceMask[PresenceIndices.protobufPayload] -> ConformanceRequestPayloadCase.PROTOBUF_PAYLOAD
        presenceMask[PresenceIndices.jsonPayload] -> ConformanceRequestPayloadCase.JSON_PAYLOAD
        presenceMask[PresenceIndices.jspbPayload] -> ConformanceRequestPayloadCase.JSPB_PAYLOAD
        presenceMask[PresenceIndices.textPayload] -> ConformanceRequestPayloadCase.TEXT_PAYLOAD
        else -> ConformanceRequestPayloadCase.NOT_SET
    }

    override fun clearPayload() {
        presenceMask.clearRange(PresenceIndices.protobufPayload, PresenceIndices.textPayload)
        _payloadRef = null
    }

    override var protobufPayload: ByteString
        get() = if (presenceMask[PresenceIndices.protobufPayload]) (_payloadRef as ByteString) else ByteString()
        set(value) { presenceMask.setExclusive(PresenceIndices.protobufPayload, PresenceIndices.protobufPayload, PresenceIndices.textPayload); _payloadRef = value }

    override fun clearProtobufPayload() {
        if (presenceMask[PresenceIndices.protobufPayload]) clearPayload()
    }

    override var jsonPayload: String
        get() = if (presenceMask[PresenceIndices.jsonPayload]) (_payloadRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.jsonPayload, PresenceIndices.protobufPayload, PresenceIndices.textPayload); _payloadRef = value }

    override fun clearJsonPayload() {
        if (presenceMask[PresenceIndices.jsonPayload]) clearPayload()
    }

    override var jspbPayload: String
        get() = if (presenceMask[PresenceIndices.jspbPayload]) (_payloadRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.jspbPayload, PresenceIndices.protobufPayload, PresenceIndices.textPayload); _payloadRef = value }

    override fun clearJspbPayload() {
        if (presenceMask[PresenceIndices.jspbPayload]) clearPayload()
    }

    override var textPayload: String
        get() = if (presenceMask[PresenceIndices.textPayload]) (_payloadRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.textPayload, PresenceIndices.protobufPayload, PresenceIndices.textPayload); _payloadRef = value }

    override fun clearTextPayload() {
        if (presenceMask[PresenceIndices.textPayload]) clearPayload()
    }

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

    private val _owner: ConformanceRequestInternal = this

    @InternalRpcApi
    val _presence: ConformanceRequestPresence = object : ConformanceRequestPresence, InternalPresenceObject {
        override val _message: ConformanceRequestInternal get() = _owner

        override val hasProtobufPayload: Boolean get() = presenceMask[PresenceIndices.protobufPayload]

        override val hasJsonPayload: Boolean get() = presenceMask[PresenceIndices.jsonPayload]

        override val hasJspbPayload: Boolean get() = presenceMask[PresenceIndices.jspbPayload]

        override val hasTextPayload: Boolean get() = presenceMask[PresenceIndices.textPayload]

        override val hasJspbEncodingOptions: Boolean get() = presenceMask[PresenceIndices.jspbEncodingOptions]
    }

    override fun hashCode(): Int {
        var result = when {
            presenceMask[PresenceIndices.protobufPayload] -> 1 * 31 + this.protobufPayload.hashCode()
            presenceMask[PresenceIndices.jsonPayload] -> 2 * 31 + this.jsonPayload.hashCode()
            presenceMask[PresenceIndices.jspbPayload] -> 7 * 31 + this.jspbPayload.hashCode()
            presenceMask[PresenceIndices.textPayload] -> 8 * 31 + this.textPayload.hashCode()
            else -> 0
        }

        result = 31 * result + this.requestedOutputFormat.hashCode()
        result = 31 * result + this.messageType.hashCode()
        result = 31 * result + this.testCategory.hashCode()
        result = 31 * result + if (presenceMask[PresenceIndices.jspbEncodingOptions]) this.jspbEncodingOptions.hashCode() else 0
        result = 31 * result + this.printUnknownFields.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceRequestInternal
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.protobufPayload] && this.protobufPayload != other.protobufPayload) return false
        if (presenceMask[PresenceIndices.jsonPayload] && this.jsonPayload != other.jsonPayload) return false
        if (presenceMask[PresenceIndices.jspbPayload] && this.jspbPayload != other.jspbPayload) return false
        if (presenceMask[PresenceIndices.textPayload] && this.textPayload != other.textPayload) return false
        if (this.requestedOutputFormat != other.requestedOutputFormat) return false
        if (this.messageType != other.messageType) return false
        if (this.testCategory != other.testCategory) return false
        if (presenceMask[PresenceIndices.jspbEncodingOptions] && this.jspbEncodingOptions != other.jspbEncodingOptions) return false
        return this.printUnknownFields == other.printUnknownFields
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ConformanceRequest(")
        if (presenceMask[PresenceIndices.protobufPayload]) {
            builder.appendLine("${nextIndentString}protobufPayload=${this.protobufPayload.protoToString()},")
        }

        if (presenceMask[PresenceIndices.jsonPayload]) {
            builder.appendLine("${nextIndentString}jsonPayload=${this.jsonPayload},")
        }

        if (presenceMask[PresenceIndices.jspbPayload]) {
            builder.appendLine("${nextIndentString}jspbPayload=${this.jspbPayload},")
        }

        if (presenceMask[PresenceIndices.textPayload]) {
            builder.appendLine("${nextIndentString}textPayload=${this.textPayload},")
        }

        builder.appendLine("${nextIndentString}requestedOutputFormat=${this.requestedOutputFormat},")
        builder.appendLine("${nextIndentString}messageType=${this.messageType},")
        builder.appendLine("${nextIndentString}testCategory=${this.testCategory},")
        if (presenceMask[PresenceIndices.jspbEncodingOptions]) {
            builder.appendLine("${nextIndentString}jspbEncodingOptions=${this.jspbEncodingOptions.asInternal().asString(indent = indent + 4)},")
        } else {
            builder.appendLine("${nextIndentString}jspbEncodingOptions=<unset>,")
        }

        builder.appendLine("${nextIndentString}printUnknownFields=${this.printUnknownFields},")
        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ConformanceRequestInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ConformanceRequestInternal.() -> Unit): ConformanceRequestInternal {
        val copy = ConformanceRequestInternal()
        if (presenceMask[PresenceIndices.protobufPayload]) {
            copy.protobufPayload = this.protobufPayload
        }

        if (presenceMask[PresenceIndices.jsonPayload]) {
            copy.jsonPayload = this.jsonPayload
        }

        if (presenceMask[PresenceIndices.jspbPayload]) {
            copy.jspbPayload = this.jspbPayload
        }

        if (presenceMask[PresenceIndices.textPayload]) {
            copy.textPayload = this.textPayload
        }

        copy.requestedOutputFormat = this.requestedOutputFormat
        copy.messageType = this.messageType
        copy.testCategory = this.testCategory
        if (presenceMask[PresenceIndices.jspbEncodingOptions]) {
            copy.jspbEncodingOptions = this.jspbEncodingOptions.copy()
        }

        copy.printUnknownFields = this.printUnknownFields
        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
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
@GeneratedProtoOneOfs(names = ["result"])
class ConformanceResponseInternal: ConformanceResponse.Builder, InternalMessage(fieldsWithPresence = 9) {
    @InternalRpcApi
    internal object PresenceIndices {
        const val parseError: Int = 0
        const val serializeError: Int = 1
        const val timeoutError: Int = 2
        const val runtimeError: Int = 3
        const val protobufPayload: Int = 4
        const val jsonPayload: Int = 5
        const val skipped: Int = 6
        const val jspbPayload: Int = 7
        const val textPayload: Int = 8
    }

    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    private var _resultRef: Any? = null

    @InternalRpcApi
    val _resultCase: ConformanceResponseResultCase get() = when {
        presenceMask[PresenceIndices.parseError] -> ConformanceResponseResultCase.PARSE_ERROR
        presenceMask[PresenceIndices.serializeError] -> ConformanceResponseResultCase.SERIALIZE_ERROR
        presenceMask[PresenceIndices.timeoutError] -> ConformanceResponseResultCase.TIMEOUT_ERROR
        presenceMask[PresenceIndices.runtimeError] -> ConformanceResponseResultCase.RUNTIME_ERROR
        presenceMask[PresenceIndices.protobufPayload] -> ConformanceResponseResultCase.PROTOBUF_PAYLOAD
        presenceMask[PresenceIndices.jsonPayload] -> ConformanceResponseResultCase.JSON_PAYLOAD
        presenceMask[PresenceIndices.skipped] -> ConformanceResponseResultCase.SKIPPED
        presenceMask[PresenceIndices.jspbPayload] -> ConformanceResponseResultCase.JSPB_PAYLOAD
        presenceMask[PresenceIndices.textPayload] -> ConformanceResponseResultCase.TEXT_PAYLOAD
        else -> ConformanceResponseResultCase.NOT_SET
    }

    override fun clearResult() {
        presenceMask.clearRange(PresenceIndices.parseError, PresenceIndices.textPayload)
        _resultRef = null
    }

    override var parseError: String
        get() = if (presenceMask[PresenceIndices.parseError]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.parseError, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearParseError() {
        if (presenceMask[PresenceIndices.parseError]) clearResult()
    }

    override var serializeError: String
        get() = if (presenceMask[PresenceIndices.serializeError]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.serializeError, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearSerializeError() {
        if (presenceMask[PresenceIndices.serializeError]) clearResult()
    }

    override var timeoutError: String
        get() = if (presenceMask[PresenceIndices.timeoutError]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.timeoutError, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearTimeoutError() {
        if (presenceMask[PresenceIndices.timeoutError]) clearResult()
    }

    override var runtimeError: String
        get() = if (presenceMask[PresenceIndices.runtimeError]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.runtimeError, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearRuntimeError() {
        if (presenceMask[PresenceIndices.runtimeError]) clearResult()
    }

    override var protobufPayload: ByteString
        get() = if (presenceMask[PresenceIndices.protobufPayload]) (_resultRef as ByteString) else ByteString()
        set(value) { presenceMask.setExclusive(PresenceIndices.protobufPayload, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearProtobufPayload() {
        if (presenceMask[PresenceIndices.protobufPayload]) clearResult()
    }

    override var jsonPayload: String
        get() = if (presenceMask[PresenceIndices.jsonPayload]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.jsonPayload, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearJsonPayload() {
        if (presenceMask[PresenceIndices.jsonPayload]) clearResult()
    }

    override var skipped: String
        get() = if (presenceMask[PresenceIndices.skipped]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.skipped, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearSkipped() {
        if (presenceMask[PresenceIndices.skipped]) clearResult()
    }

    override var jspbPayload: String
        get() = if (presenceMask[PresenceIndices.jspbPayload]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.jspbPayload, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearJspbPayload() {
        if (presenceMask[PresenceIndices.jspbPayload]) clearResult()
    }

    override var textPayload: String
        get() = if (presenceMask[PresenceIndices.textPayload]) (_resultRef as String) else ""
        set(value) { presenceMask.setExclusive(PresenceIndices.textPayload, PresenceIndices.parseError, PresenceIndices.textPayload); _resultRef = value }

    override fun clearTextPayload() {
        if (presenceMask[PresenceIndices.textPayload]) clearResult()
    }

    private val _owner: ConformanceResponseInternal = this

    @InternalRpcApi
    val _presence: ConformanceResponsePresence = object : ConformanceResponsePresence, InternalPresenceObject {
        override val _message: ConformanceResponseInternal get() = _owner

        override val hasParseError: Boolean get() = presenceMask[PresenceIndices.parseError]

        override val hasSerializeError: Boolean get() = presenceMask[PresenceIndices.serializeError]

        override val hasTimeoutError: Boolean get() = presenceMask[PresenceIndices.timeoutError]

        override val hasRuntimeError: Boolean get() = presenceMask[PresenceIndices.runtimeError]

        override val hasProtobufPayload: Boolean get() = presenceMask[PresenceIndices.protobufPayload]

        override val hasJsonPayload: Boolean get() = presenceMask[PresenceIndices.jsonPayload]

        override val hasSkipped: Boolean get() = presenceMask[PresenceIndices.skipped]

        override val hasJspbPayload: Boolean get() = presenceMask[PresenceIndices.jspbPayload]

        override val hasTextPayload: Boolean get() = presenceMask[PresenceIndices.textPayload]
    }

    override fun hashCode(): Int {
        var result = when {
            presenceMask[PresenceIndices.parseError] -> 1 * 31 + this.parseError.hashCode()
            presenceMask[PresenceIndices.serializeError] -> 6 * 31 + this.serializeError.hashCode()
            presenceMask[PresenceIndices.timeoutError] -> 9 * 31 + this.timeoutError.hashCode()
            presenceMask[PresenceIndices.runtimeError] -> 2 * 31 + this.runtimeError.hashCode()
            presenceMask[PresenceIndices.protobufPayload] -> 3 * 31 + this.protobufPayload.hashCode()
            presenceMask[PresenceIndices.jsonPayload] -> 4 * 31 + this.jsonPayload.hashCode()
            presenceMask[PresenceIndices.skipped] -> 5 * 31 + this.skipped.hashCode()
            presenceMask[PresenceIndices.jspbPayload] -> 7 * 31 + this.jspbPayload.hashCode()
            presenceMask[PresenceIndices.textPayload] -> 8 * 31 + this.textPayload.hashCode()
            else -> 0
        }

        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceResponseInternal
        if (presenceMask != other.presenceMask) return false
        if (presenceMask[PresenceIndices.parseError] && this.parseError != other.parseError) return false
        if (presenceMask[PresenceIndices.serializeError] && this.serializeError != other.serializeError) return false
        if (presenceMask[PresenceIndices.timeoutError] && this.timeoutError != other.timeoutError) return false
        if (presenceMask[PresenceIndices.runtimeError] && this.runtimeError != other.runtimeError) return false
        if (presenceMask[PresenceIndices.protobufPayload] && this.protobufPayload != other.protobufPayload) return false
        if (presenceMask[PresenceIndices.jsonPayload] && this.jsonPayload != other.jsonPayload) return false
        if (presenceMask[PresenceIndices.skipped] && this.skipped != other.skipped) return false
        if (presenceMask[PresenceIndices.jspbPayload] && this.jspbPayload != other.jspbPayload) return false
        return !presenceMask[PresenceIndices.textPayload] || this.textPayload == other.textPayload
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        val builder = StringBuilder()
        builder.appendLine("ConformanceResponse(")
        if (presenceMask[PresenceIndices.parseError]) {
            builder.appendLine("${nextIndentString}parseError=${this.parseError},")
        }

        if (presenceMask[PresenceIndices.serializeError]) {
            builder.appendLine("${nextIndentString}serializeError=${this.serializeError},")
        }

        if (presenceMask[PresenceIndices.timeoutError]) {
            builder.appendLine("${nextIndentString}timeoutError=${this.timeoutError},")
        }

        if (presenceMask[PresenceIndices.runtimeError]) {
            builder.appendLine("${nextIndentString}runtimeError=${this.runtimeError},")
        }

        if (presenceMask[PresenceIndices.protobufPayload]) {
            builder.appendLine("${nextIndentString}protobufPayload=${this.protobufPayload.protoToString()},")
        }

        if (presenceMask[PresenceIndices.jsonPayload]) {
            builder.appendLine("${nextIndentString}jsonPayload=${this.jsonPayload},")
        }

        if (presenceMask[PresenceIndices.skipped]) {
            builder.appendLine("${nextIndentString}skipped=${this.skipped},")
        }

        if (presenceMask[PresenceIndices.jspbPayload]) {
            builder.appendLine("${nextIndentString}jspbPayload=${this.jspbPayload},")
        }

        if (presenceMask[PresenceIndices.textPayload]) {
            builder.appendLine("${nextIndentString}textPayload=${this.textPayload},")
        }

        builder.append("${indentString})")
        return builder.toString()
    }

    override fun copyInternal(): ConformanceResponseInternal {
        return copyInternal { }
    }

    @InternalRpcApi
    fun copyInternal(body: ConformanceResponseInternal.() -> Unit): ConformanceResponseInternal {
        val copy = ConformanceResponseInternal()
        if (presenceMask[PresenceIndices.parseError]) {
            copy.parseError = this.parseError
        }

        if (presenceMask[PresenceIndices.serializeError]) {
            copy.serializeError = this.serializeError
        }

        if (presenceMask[PresenceIndices.timeoutError]) {
            copy.timeoutError = this.timeoutError
        }

        if (presenceMask[PresenceIndices.runtimeError]) {
            copy.runtimeError = this.runtimeError
        }

        if (presenceMask[PresenceIndices.protobufPayload]) {
            copy.protobufPayload = this.protobufPayload
        }

        if (presenceMask[PresenceIndices.jsonPayload]) {
            copy.jsonPayload = this.jsonPayload
        }

        if (presenceMask[PresenceIndices.skipped]) {
            copy.skipped = this.skipped
        }

        if (presenceMask[PresenceIndices.jspbPayload]) {
            copy.jspbPayload = this.jspbPayload
        }

        if (presenceMask[PresenceIndices.textPayload]) {
            copy.textPayload = this.textPayload
        }

        copy.apply(body)
        this._unknownFields.copyTo(copy._unknownFields)
        return copy
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
fun TestStatusInternal.Companion.decodeWith(
    msg: TestStatusInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
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
fun FailureSetInternal.Companion.decodeWith(
    msg: FailureSetInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
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
    if (presenceMask[ConformanceRequestInternal.PresenceIndices.protobufPayload]) {
        encoder.writeBytes(fieldNr = 1, value = this.protobufPayload)
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.jsonPayload]) {
        encoder.writeString(fieldNr = 2, value = this.jsonPayload)
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.jspbPayload]) {
        encoder.writeString(fieldNr = 7, value = this.jspbPayload)
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.textPayload]) {
        encoder.writeString(fieldNr = 8, value = this.textPayload)
    }

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

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun ConformanceRequestInternal.Companion.decodeWith(
    msg: ConformanceRequestInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.protobufPayload = decoder.readBytes()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.jsonPayload = decoder.readString()
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.jspbPayload = decoder.readString()
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.textPayload = decoder.readString()
            }
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
    if (presenceMask[ConformanceRequestInternal.PresenceIndices.protobufPayload]) {
        __result += WireSize.bytes(this.protobufPayload).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.jsonPayload]) {
        __result += WireSize.string(this.jsonPayload).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.jspbPayload]) {
        __result += WireSize.string(this.jspbPayload).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceRequestInternal.PresenceIndices.textPayload]) {
        __result += WireSize.string(this.textPayload).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

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

    __result += _unknownFields.size.toInt()
    return __result
}

@InternalRpcApi
fun ConformanceRequest.asInternal(): ConformanceRequestInternal {
    return this as? ConformanceRequestInternal ?: error("Message ${this::class.simpleName} is a non-internal message type.")
}

@InternalRpcApi
fun ConformanceResponseInternal.encodeWith(encoder: WireEncoder, config: ProtoConfig?) {
    if (presenceMask[ConformanceResponseInternal.PresenceIndices.parseError]) {
        encoder.writeString(fieldNr = 1, value = this.parseError)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.serializeError]) {
        encoder.writeString(fieldNr = 6, value = this.serializeError)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.timeoutError]) {
        encoder.writeString(fieldNr = 9, value = this.timeoutError)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.runtimeError]) {
        encoder.writeString(fieldNr = 2, value = this.runtimeError)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.protobufPayload]) {
        encoder.writeBytes(fieldNr = 3, value = this.protobufPayload)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.jsonPayload]) {
        encoder.writeString(fieldNr = 4, value = this.jsonPayload)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.skipped]) {
        encoder.writeString(fieldNr = 5, value = this.skipped)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.jspbPayload]) {
        encoder.writeString(fieldNr = 7, value = this.jspbPayload)
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.textPayload]) {
        encoder.writeString(fieldNr = 8, value = this.textPayload)
    }

    _extensions.forEach { (key, value) ->
        value.descriptor.let { descriptor ->
            descriptor.encode(encoder, key, descriptor.valueType.cast(value.value), config)
        }
    }

    encoder.writeRawBytes(_unknownFields)
}

@InternalRpcApi
fun ConformanceResponseInternal.Companion.decodeWith(
    msg: ConformanceResponseInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
    while (true) {
        val tag = decoder.readTag() ?: break // EOF, we read the whole message
        when (tag.fieldNr) {
            1 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.parseError = decoder.readString()
            }
            6 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.serializeError = decoder.readString()
            }
            9 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.timeoutError = decoder.readString()
            }
            2 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.runtimeError = decoder.readString()
            }
            3 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.protobufPayload = decoder.readBytes()
            }
            4 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.jsonPayload = decoder.readString()
            }
            5 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.skipped = decoder.readString()
            }
            7 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.jspbPayload = decoder.readString()
            }
            8 if tag.wireType == WireType.LENGTH_DELIMITED -> {
                msg.textPayload = decoder.readString()
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
    if (presenceMask[ConformanceResponseInternal.PresenceIndices.parseError]) {
        __result += WireSize.string(this.parseError).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.serializeError]) {
        __result += WireSize.string(this.serializeError).let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.timeoutError]) {
        __result += WireSize.string(this.timeoutError).let { WireSize.tag(9, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.runtimeError]) {
        __result += WireSize.string(this.runtimeError).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.protobufPayload]) {
        __result += WireSize.bytes(this.protobufPayload).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.jsonPayload]) {
        __result += WireSize.string(this.jsonPayload).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.skipped]) {
        __result += WireSize.string(this.skipped).let { WireSize.tag(5, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.jspbPayload]) {
        __result += WireSize.string(this.jspbPayload).let { WireSize.tag(7, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (presenceMask[ConformanceResponseInternal.PresenceIndices.textPayload]) {
        __result += WireSize.string(this.textPayload).let { WireSize.tag(8, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
fun JspbEncodingConfigInternal.Companion.decodeWith(
    msg: JspbEncodingConfigInternal,
    decoder: WireDecoder,
    config: ProtoConfig?,
) {
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
