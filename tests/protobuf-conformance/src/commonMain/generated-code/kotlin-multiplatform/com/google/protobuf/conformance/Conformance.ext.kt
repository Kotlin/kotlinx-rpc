@file:OptIn(InternalRpcApi::class)
@file:Suppress("unused")

package com.google.protobuf.conformance

import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.internal.utils.InternalRpcApi

/**
 * Constructs a new message.
 * ```
 * val message = TestStatus {
 *    name = ...
 * }
 * ```
 */
operator fun TestStatus.Companion.invoke(body: TestStatus.Builder.() -> Unit): TestStatus {
    return TestStatusInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    name = ...
 * }
 * ```
 */
fun TestStatus.copy(body: TestStatus.Builder.() -> Unit = {}): TestStatus {
    return this.asInternal().copyInternal(body)
}

/**
 * Constructs a new message.
 * ```
 * val message = FailureSet {
 *    test = ...
 * }
 * ```
 */
operator fun FailureSet.Companion.invoke(body: FailureSet.Builder.() -> Unit): FailureSet {
    return FailureSetInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    test = ...
 * }
 * ```
 */
fun FailureSet.copy(body: FailureSet.Builder.() -> Unit = {}): FailureSet {
    return this.asInternal().copyInternal(body)
}

/**
 * Constructs a new message.
 * ```
 * val message = ConformanceRequest {
 *    protobufPayload = ...
 * }
 * ```
 */
operator fun ConformanceRequest.Companion.invoke(body: ConformanceRequest.Builder.() -> Unit): ConformanceRequest {
    return ConformanceRequestInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    protobufPayload = ...
 * }
 * ```
 */
fun ConformanceRequest.copy(body: ConformanceRequest.Builder.() -> Unit = {}): ConformanceRequest {
    return this.asInternal().copyInternal(body)
}

/**
 * Returns the field-presence view for this [ConformanceRequest] instance.
 */
val ConformanceRequest.presence: ConformanceRequestPresence get() = this.asInternal()._presence

/**
 * Returns the value of the `protobufPayload` field if present, otherwise null.
 */
val ConformanceRequest.protobufPayloadOrNull: ByteString? get() = if (this.presence.hasProtobufPayload) this.protobufPayload else null

/**
 * Returns the value of the `jsonPayload` field if present, otherwise null.
 */
val ConformanceRequest.jsonPayloadOrNull: String? get() = if (this.presence.hasJsonPayload) this.jsonPayload else null

/**
 * Returns the value of the `jspbPayload` field if present, otherwise null.
 */
val ConformanceRequest.jspbPayloadOrNull: String? get() = if (this.presence.hasJspbPayload) this.jspbPayload else null

/**
 * Returns the value of the `textPayload` field if present, otherwise null.
 */
val ConformanceRequest.textPayloadOrNull: String? get() = if (this.presence.hasTextPayload) this.textPayload else null

/**
 * Returns the value of the `jspbEncodingOptions` field if present, otherwise null.
 */
val ConformanceRequest.jspbEncodingOptionsOrNull: JspbEncodingConfig? get() = if (this.presence.hasJspbEncodingOptions) this.jspbEncodingOptions else null

/**
 * The active case of the `payload` oneof, or [ConformanceRequestPayloadCase.NOT_SET].
 */
val ConformanceRequest.payload: ConformanceRequestPayloadCase get() = this.asInternal()._payloadCase

/**
 * Clears the `payload` oneof regardless of its active case.
 */
fun ConformanceRequest.Builder.clearPayload() {
    this.asInternal().clearPayloadInternal()
}

/**
 * Exhaustive, typed dispatch on the active case of the `payload` oneof.
 */
inline fun <R> ConformanceRequest.whenPayload(
    protobufPayload: (ByteString) -> R,
    jsonPayload: (String) -> R,
    jspbPayload: (String) -> R,
    textPayload: (String) -> R,
    notSet: () -> R,
): R {
    return when (this.payload) {
        ConformanceRequestPayloadCase.PROTOBUF_PAYLOAD -> protobufPayload(this.protobufPayload)
        ConformanceRequestPayloadCase.JSON_PAYLOAD -> jsonPayload(this.jsonPayload)
        ConformanceRequestPayloadCase.JSPB_PAYLOAD -> jspbPayload(this.jspbPayload)
        ConformanceRequestPayloadCase.TEXT_PAYLOAD -> textPayload(this.textPayload)
        ConformanceRequestPayloadCase.NOT_SET -> notSet()
    }
}

/**
 * Constructs a new message.
 * ```
 * val message = ConformanceResponse {
 *    parseError = ...
 * }
 * ```
 */
operator fun ConformanceResponse.Companion.invoke(body: ConformanceResponse.Builder.() -> Unit): ConformanceResponse {
    return ConformanceResponseInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    parseError = ...
 * }
 * ```
 */
fun ConformanceResponse.copy(body: ConformanceResponse.Builder.() -> Unit = {}): ConformanceResponse {
    return this.asInternal().copyInternal(body)
}

/**
 * Returns the field-presence view for this [ConformanceResponse] instance.
 */
val ConformanceResponse.presence: ConformanceResponsePresence get() = this.asInternal()._presence

/**
 * Returns the value of the `parseError` field if present, otherwise null.
 */
val ConformanceResponse.parseErrorOrNull: String? get() = if (this.presence.hasParseError) this.parseError else null

/**
 * Returns the value of the `serializeError` field if present, otherwise null.
 */
val ConformanceResponse.serializeErrorOrNull: String? get() = if (this.presence.hasSerializeError) this.serializeError else null

/**
 * Returns the value of the `timeoutError` field if present, otherwise null.
 */
val ConformanceResponse.timeoutErrorOrNull: String? get() = if (this.presence.hasTimeoutError) this.timeoutError else null

/**
 * Returns the value of the `runtimeError` field if present, otherwise null.
 */
val ConformanceResponse.runtimeErrorOrNull: String? get() = if (this.presence.hasRuntimeError) this.runtimeError else null

/**
 * Returns the value of the `protobufPayload` field if present, otherwise null.
 */
val ConformanceResponse.protobufPayloadOrNull: ByteString? get() = if (this.presence.hasProtobufPayload) this.protobufPayload else null

/**
 * Returns the value of the `jsonPayload` field if present, otherwise null.
 */
val ConformanceResponse.jsonPayloadOrNull: String? get() = if (this.presence.hasJsonPayload) this.jsonPayload else null

/**
 * Returns the value of the `skipped` field if present, otherwise null.
 */
val ConformanceResponse.skippedOrNull: String? get() = if (this.presence.hasSkipped) this.skipped else null

/**
 * Returns the value of the `jspbPayload` field if present, otherwise null.
 */
val ConformanceResponse.jspbPayloadOrNull: String? get() = if (this.presence.hasJspbPayload) this.jspbPayload else null

/**
 * Returns the value of the `textPayload` field if present, otherwise null.
 */
val ConformanceResponse.textPayloadOrNull: String? get() = if (this.presence.hasTextPayload) this.textPayload else null

/**
 * The active case of the `result` oneof, or [ConformanceResponseResultCase.NOT_SET].
 */
val ConformanceResponse.result: ConformanceResponseResultCase get() = this.asInternal()._resultCase

/**
 * Clears the `result` oneof regardless of its active case.
 */
fun ConformanceResponse.Builder.clearResult() {
    this.asInternal().clearResultInternal()
}

/**
 * Exhaustive, typed dispatch on the active case of the `result` oneof.
 */
inline fun <R> ConformanceResponse.whenResult(
    parseError: (String) -> R,
    serializeError: (String) -> R,
    timeoutError: (String) -> R,
    runtimeError: (String) -> R,
    protobufPayload: (ByteString) -> R,
    jsonPayload: (String) -> R,
    skipped: (String) -> R,
    jspbPayload: (String) -> R,
    textPayload: (String) -> R,
    notSet: () -> R,
): R {
    return when (this.result) {
        ConformanceResponseResultCase.PARSE_ERROR -> parseError(this.parseError)
        ConformanceResponseResultCase.SERIALIZE_ERROR -> serializeError(this.serializeError)
        ConformanceResponseResultCase.TIMEOUT_ERROR -> timeoutError(this.timeoutError)
        ConformanceResponseResultCase.RUNTIME_ERROR -> runtimeError(this.runtimeError)
        ConformanceResponseResultCase.PROTOBUF_PAYLOAD -> protobufPayload(this.protobufPayload)
        ConformanceResponseResultCase.JSON_PAYLOAD -> jsonPayload(this.jsonPayload)
        ConformanceResponseResultCase.SKIPPED -> skipped(this.skipped)
        ConformanceResponseResultCase.JSPB_PAYLOAD -> jspbPayload(this.jspbPayload)
        ConformanceResponseResultCase.TEXT_PAYLOAD -> textPayload(this.textPayload)
        ConformanceResponseResultCase.NOT_SET -> notSet()
    }
}

/**
 * Constructs a new message.
 * ```
 * val message = JspbEncodingConfig {
 *    useJspbArrayAnyFormat = ...
 * }
 * ```
 */
operator fun JspbEncodingConfig.Companion.invoke(body: JspbEncodingConfig.Builder.() -> Unit): JspbEncodingConfig {
    return JspbEncodingConfigInternal().apply(body)
}

/**
 * Copies the original message, including unknown fields.
 * ```
 * val copy = original.copy {
 *    useJspbArrayAnyFormat = ...
 * }
 * ```
 */
fun JspbEncodingConfig.copy(body: JspbEncodingConfig.Builder.() -> Unit = {}): JspbEncodingConfig {
    return this.asInternal().copyInternal(body)
}

/**
 * Interface providing field-presence information for [ConformanceRequest] messages.
 * Retrieve it via the [ConformanceRequest.presence] extension property.
 */
interface ConformanceRequestPresence {
    val hasProtobufPayload: Boolean

    val hasJsonPayload: Boolean

    val hasJspbPayload: Boolean

    val hasTextPayload: Boolean

    val hasJspbEncodingOptions: Boolean
}

/**
 * Interface providing field-presence information for [ConformanceResponse] messages.
 * Retrieve it via the [ConformanceResponse.presence] extension property.
 */
interface ConformanceResponsePresence {
    val hasParseError: Boolean

    val hasSerializeError: Boolean

    val hasTimeoutError: Boolean

    val hasRuntimeError: Boolean

    val hasProtobufPayload: Boolean

    val hasJsonPayload: Boolean

    val hasSkipped: Boolean

    val hasJspbPayload: Boolean

    val hasTextPayload: Boolean
}

/**
 * Cases of the `payload` oneof of [ConformanceRequest].
 * Retrieve the active case via the [ConformanceRequest.payload] extension property.
 * The payload (whether protobuf of JSON) is always for a
 * protobuf_test_messages.proto3.TestAllTypes proto (as defined in
 * src/google/protobuf/proto3_test_messages.proto).
 */
enum class ConformanceRequestPayloadCase {
    PROTOBUF_PAYLOAD,
    JSON_PAYLOAD,
    /**
     * Only used inside Google.  Opensource testees just skip it.
     */
    JSPB_PAYLOAD,
    TEXT_PAYLOAD,
    NOT_SET,
}

/**
 * Cases of the `result` oneof of [ConformanceResponse].
 * Retrieve the active case via the [ConformanceResponse.result] extension property.
 */
enum class ConformanceResponseResultCase {
    /**
     * This string should be set to indicate parsing failed.  The string can
     * provide more information about the parse error if it is available.
     * 
     * Setting this string does not necessarily mean the testee failed the
     * test.  Some of the test cases are intentionally invalid input.
     */
    PARSE_ERROR,
    /**
     * If the input was successfully parsed but errors occurred when
     * serializing it to the requested output format, set the error message in
     * this field.
     */
    SERIALIZE_ERROR,
    /**
     * This should be set if the test program timed out.  The string should
     * provide more information about what the child process was doing when it
     * was killed.
     */
    TIMEOUT_ERROR,
    /**
     * This should be set if some other error occurred.  This will always
     * indicate that the test failed.  The string can provide more information
     * about the failure.
     */
    RUNTIME_ERROR,
    /**
     * If the input was successfully parsed and the requested output was
     * protobuf, serialize it to protobuf and set it in this field.
     */
    PROTOBUF_PAYLOAD,
    /**
     * If the input was successfully parsed and the requested output was JSON,
     * serialize to JSON and set it in this field.
     */
    JSON_PAYLOAD,
    /**
     * For when the testee skipped the test, likely because a certain feature
     * wasn't supported, like JSON input/output.
     */
    SKIPPED,
    /**
     * If the input was successfully parsed and the requested output was JSPB,
     * serialize to JSPB and set it in this field. JSPB is only used inside
     * Google. Opensource testees can just skip it.
     */
    JSPB_PAYLOAD,
    /**
     * If the input was successfully parsed and the requested output was
     * TEXT_FORMAT, serialize to TEXT_FORMAT and set it in this field.
     */
    TEXT_PAYLOAD,
    NOT_SET,
}
