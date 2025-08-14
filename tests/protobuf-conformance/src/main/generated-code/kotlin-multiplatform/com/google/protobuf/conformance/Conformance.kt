@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.conformance

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.TestStatusInternal.CODEC::class)
interface TestStatus { 
    val name: String
    val failureMessage: String
    val matchedName: String

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.FailureSetInternal.CODEC::class)
interface FailureSet { 
    val test: List<com.google.protobuf.conformance.TestStatus>

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.ConformanceRequestInternal.CODEC::class)
interface ConformanceRequest { 
    val requestedOutputFormat: com.google.protobuf.conformance.WireFormat
    val messageType: String
    val testCategory: com.google.protobuf.conformance.TestCategory
    val jspbEncodingOptions: com.google.protobuf.conformance.JspbEncodingConfig
    val printUnknownFields: Boolean
    val payload: com.google.protobuf.conformance.ConformanceRequest.Payload?

    sealed interface Payload { 
        @JvmInline
        value class ProtobufPayload(val value: ByteArray): Payload

        @JvmInline
        value class JsonPayload(val value: String): Payload

        @JvmInline
        value class JspbPayload(val value: String): Payload

        @JvmInline
        value class TextPayload(val value: String): Payload
    }

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.ConformanceResponseInternal.CODEC::class)
interface ConformanceResponse { 
    val result: com.google.protobuf.conformance.ConformanceResponse.Result?

    sealed interface Result { 
        @JvmInline
        value class ParseError(val value: String): Result

        @JvmInline
        value class SerializeError(val value: String): Result

        @JvmInline
        value class TimeoutError(val value: String): Result

        @JvmInline
        value class RuntimeError(val value: String): Result

        @JvmInline
        value class ProtobufPayload(val value: ByteArray): Result

        @JvmInline
        value class JsonPayload(val value: String): Result

        @JvmInline
        value class Skipped(val value: String): Result

        @JvmInline
        value class JspbPayload(val value: String): Result

        @JvmInline
        value class TextPayload(val value: String): Result
    }

    companion object
}

@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.JspbEncodingConfigInternal.CODEC::class)
interface JspbEncodingConfig { 
    val useJspbArrayAnyFormat: Boolean

    companion object
}

sealed class WireFormat(open val number: Int) { 
    object UNSPECIFIED: WireFormat(number = 0)

    object PROTOBUF: WireFormat(number = 1)

    object JSON: WireFormat(number = 2)

    object JSPB: WireFormat(number = 3)

    object TEXT_FORMAT: WireFormat(number = 4)

    data class UNRECOGNIZED(override val number: Int): WireFormat(number)

    companion object { 
        val entries: List<WireFormat> by lazy { listOf(UNSPECIFIED, PROTOBUF, JSON, JSPB, TEXT_FORMAT) }
    }
}

sealed class TestCategory(open val number: Int) { 
    object UNSPECIFIED_TEST: TestCategory(number = 0)

    object BINARY_TEST: TestCategory(number = 1)

    object JSON_TEST: TestCategory(number = 2)

    object JSON_IGNORE_UNKNOWN_PARSING_TEST: TestCategory(number = 3)

    object JSPB_TEST: TestCategory(number = 4)

    object TEXT_FORMAT_TEST: TestCategory(number = 5)

    data class UNRECOGNIZED(override val number: Int): TestCategory(number)

    companion object { 
        val entries: List<TestCategory> by lazy { listOf(UNSPECIFIED_TEST, BINARY_TEST, JSON_TEST, JSON_IGNORE_UNKNOWN_PARSING_TEST, JSPB_TEST, TEXT_FORMAT_TEST) }
    }
}

