@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.conformance

import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.internal.utils.InternalRpcApi
import kotlinx.rpc.protobuf.ProtobufConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.MsgFieldDelegate
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

class TestStatusInternal: TestStatus, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var name: String by MsgFieldDelegate { "" }
    override var failureMessage: String by MsgFieldDelegate { "" }
    override var matchedName: String by MsgFieldDelegate { "" }

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
        if (name != other.name) return false
        if (failureMessage != other.failureMessage) return false
        if (matchedName != other.matchedName) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("TestStatus(")
            appendLine("${nextIndentString}name=${name},")
            appendLine("${nextIndentString}failureMessage=${failureMessage},")
            appendLine("${nextIndentString}matchedName=${matchedName},")
            append("${indentString})")
        }
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
    object CODEC: MessageCodec<TestStatus> {
        override fun encode(value: TestStatus, config: CodecConfig?): Source {
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

        override fun decode(source: Source, config: CodecConfig?): TestStatus {
            WireDecoder(source).use {
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
    companion object
}

class FailureSetInternal: FailureSet, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var test: List<TestStatus> by MsgFieldDelegate { mutableListOf() }

    override fun hashCode(): Int {
        checkRequiredFields()
        return test.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as FailureSetInternal
        other.checkRequiredFields()
        if (test != other.test) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("FailureSet(")
            appendLine("${nextIndentString}test=${test},")
            append("${indentString})")
        }
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
    object CODEC: MessageCodec<FailureSet> {
        override fun encode(value: FailureSet, config: CodecConfig?): Source {
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

        override fun decode(source: Source, config: CodecConfig?): FailureSet {
            WireDecoder(source).use {
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
    companion object
}

class ConformanceRequestInternal: ConformanceRequest, InternalMessage(fieldsWithPresence = 1) {
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

    @InternalRpcApi
    val _presence: ConformanceRequestPresence = object : ConformanceRequestPresence {
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
        val offset = when (this) {
            is ConformanceRequest.Payload.ProtobufPayload -> 0
            is ConformanceRequest.Payload.JsonPayload -> 1
            is ConformanceRequest.Payload.JspbPayload -> 2
            is ConformanceRequest.Payload.TextPayload -> 3
        }

        return hashCode() + offset
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceRequestInternal
        other.checkRequiredFields()
        if (presenceMask != other.presenceMask) return false
        if (requestedOutputFormat != other.requestedOutputFormat) return false
        if (messageType != other.messageType) return false
        if (testCategory != other.testCategory) return false
        if (presenceMask[0] && jspbEncodingOptions != other.jspbEncodingOptions) return false
        if (printUnknownFields != other.printUnknownFields) return false
        if (payload != other.payload) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("ConformanceRequest(")
            appendLine("${nextIndentString}requestedOutputFormat=${requestedOutputFormat},")
            appendLine("${nextIndentString}messageType=${messageType},")
            appendLine("${nextIndentString}testCategory=${testCategory},")
            if (presenceMask[0]) {
                appendLine("${nextIndentString}jspbEncodingOptions=${jspbEncodingOptions.asInternal().asString(indent = indent + 4)},")
            } else {
                appendLine("${nextIndentString}jspbEncodingOptions=<unset>,")
            }

            appendLine("${nextIndentString}printUnknownFields=${printUnknownFields},")
            appendLine("${nextIndentString}payload=${payload},")
            append("${indentString})")
        }
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
    object CODEC: MessageCodec<ConformanceRequest> {
        override fun encode(value: ConformanceRequest, config: CodecConfig?): Source {
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

        override fun decode(source: Source, config: CodecConfig?): ConformanceRequest {
            WireDecoder(source).use {
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
    companion object
}

class ConformanceResponseInternal: ConformanceResponse, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var result: ConformanceResponse.Result? = null

    override fun hashCode(): Int {
        checkRequiredFields()
        return (result?.oneOfHashCode() ?: 0)
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
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as ConformanceResponseInternal
        other.checkRequiredFields()
        if (result != other.result) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("ConformanceResponse(")
            appendLine("${nextIndentString}result=${result},")
            append("${indentString})")
        }
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
    object CODEC: MessageCodec<ConformanceResponse> {
        override fun encode(value: ConformanceResponse, config: CodecConfig?): Source {
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

        override fun decode(source: Source, config: CodecConfig?): ConformanceResponse {
            WireDecoder(source).use {
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
    companion object
}

class JspbEncodingConfigInternal: JspbEncodingConfig, InternalMessage(fieldsWithPresence = 0) {
    @InternalRpcApi
    override val _size: Int by lazy { computeSize() }

    @InternalRpcApi
    override val _unknownFields: Buffer = Buffer()

    @InternalRpcApi
    internal var _unknownFieldsEncoder: WireEncoder? = null

    override var useJspbArrayAnyFormat: Boolean by MsgFieldDelegate { false }

    override fun hashCode(): Int {
        checkRequiredFields()
        return useJspbArrayAnyFormat.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        checkRequiredFields()
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as JspbEncodingConfigInternal
        other.checkRequiredFields()
        if (useJspbArrayAnyFormat != other.useJspbArrayAnyFormat) return false
        return true
    }

    override fun toString(): String {
        return asString()
    }

    fun asString(indent: Int = 0): String {
        checkRequiredFields()
        val indentString = " ".repeat(indent)
        val nextIndentString = " ".repeat(indent + 4)
        return buildString {
            appendLine("JspbEncodingConfig(")
            appendLine("${nextIndentString}useJspbArrayAnyFormat=${useJspbArrayAnyFormat},")
            append("${indentString})")
        }
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
    object CODEC: MessageCodec<JspbEncodingConfig> {
        override fun encode(value: JspbEncodingConfig, config: CodecConfig?): Source {
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

        override fun decode(source: Source, config: CodecConfig?): JspbEncodingConfig {
            WireDecoder(source).use {
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
    companion object
}

@InternalRpcApi
fun TestStatusInternal.checkRequiredFields() {
    // no required fields to check
}

@InternalRpcApi
fun TestStatusInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (name.isNotEmpty()) {
        encoder.writeString(fieldNr = 1, value = name)
    }

    if (failureMessage.isNotEmpty()) {
        encoder.writeString(fieldNr = 2, value = failureMessage)
    }

    if (matchedName.isNotEmpty()) {
        encoder.writeString(fieldNr = 3, value = matchedName)
    }
}

@InternalRpcApi
fun TestStatusInternal.Companion.decodeWith(msg: TestStatusInternal, decoder: WireDecoder, config: ProtobufConfig?) {
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
    if (name.isNotEmpty()) {
        __result += WireSize.string(name).let { WireSize.tag(1, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (failureMessage.isNotEmpty()) {
        __result += WireSize.string(failureMessage).let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (matchedName.isNotEmpty()) {
        __result += WireSize.string(matchedName).let { WireSize.tag(3, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
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
    test.forEach {
        it.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun FailureSetInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (test.isNotEmpty()) {
        test.forEach {
            encoder.writeMessage(fieldNr = 2, value = it.asInternal()) { encodeWith(it, config) }
        }
    }
}

@InternalRpcApi
fun FailureSetInternal.Companion.decodeWith(msg: FailureSetInternal, decoder: WireDecoder, config: ProtobufConfig?) {
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
    if (test.isNotEmpty()) {
        __result += test.sumOf { it.asInternal()._size.let { WireSize.tag(2, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it } }
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
        jspbEncodingOptions.asInternal().checkRequiredFields()
    }
}

@InternalRpcApi
fun ConformanceRequestInternal.encodeWith(encoder: WireEncoder, config: ProtobufConfig?) {
    if (requestedOutputFormat != WireFormat.UNSPECIFIED) {
        encoder.writeEnum(fieldNr = 3, value = requestedOutputFormat.number)
    }

    if (messageType.isNotEmpty()) {
        encoder.writeString(fieldNr = 4, value = messageType)
    }

    if (testCategory != TestCategory.UNSPECIFIED_TEST) {
        encoder.writeEnum(fieldNr = 5, value = testCategory.number)
    }

    if (presenceMask[0]) {
        encoder.writeMessage(fieldNr = 6, value = jspbEncodingOptions.asInternal()) { encodeWith(it, config) }
    }

    if (printUnknownFields != false) {
        encoder.writeBool(fieldNr = 9, value = printUnknownFields)
    }

    payload?.also {
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
}

@InternalRpcApi
fun ConformanceRequestInternal.Companion.decodeWith(msg: ConformanceRequestInternal, decoder: WireDecoder, config: ProtobufConfig?) {
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
    if (requestedOutputFormat != WireFormat.UNSPECIFIED) {
        __result += (WireSize.tag(3, WireType.VARINT) + WireSize.enum(requestedOutputFormat.number))
    }

    if (messageType.isNotEmpty()) {
        __result += WireSize.string(messageType).let { WireSize.tag(4, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (testCategory != TestCategory.UNSPECIFIED_TEST) {
        __result += (WireSize.tag(5, WireType.VARINT) + WireSize.enum(testCategory.number))
    }

    if (presenceMask[0]) {
        __result += jspbEncodingOptions.asInternal()._size.let { WireSize.tag(6, WireType.LENGTH_DELIMITED) + WireSize.int32(it) + it }
    }

    if (printUnknownFields != false) {
        __result += (WireSize.tag(9, WireType.VARINT) + WireSize.bool(printUnknownFields))
    }

    payload?.also {
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
    result?.also {
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
}

@InternalRpcApi
fun ConformanceResponseInternal.Companion.decodeWith(msg: ConformanceResponseInternal, decoder: WireDecoder, config: ProtobufConfig?) {
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
    result?.also {
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
    if (useJspbArrayAnyFormat != false) {
        encoder.writeBool(fieldNr = 1, value = useJspbArrayAnyFormat)
    }
}

@InternalRpcApi
fun JspbEncodingConfigInternal.Companion.decodeWith(msg: JspbEncodingConfigInternal, decoder: WireDecoder, config: ProtobufConfig?) {
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
    if (useJspbArrayAnyFormat != false) {
        __result += (WireSize.tag(1, WireType.VARINT) + WireSize.bool(useJspbArrayAnyFormat))
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
