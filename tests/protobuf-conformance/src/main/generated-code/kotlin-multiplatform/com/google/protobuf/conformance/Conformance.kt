@file:OptIn(ExperimentalRpcApi::class, InternalRpcApi::class)
package com.google.protobuf.conformance

import kotlin.jvm.JvmInline
import kotlinx.rpc.internal.utils.*

/**
* Meant to encapsulate all types of tests: successes, skips, failures, etc.
* Therefore, this may or may not have a failure message. Failure messages
* may be truncated for our failure lists.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.TestStatusInternal.CODEC::class)
interface TestStatus { 
    val name: String
    val failureMessage: String
    /**
    * What an actual test name matched to in a failure list. Can be wildcarded or
    * an exact match without wildcards.
    */
    val matchedName: String

    companion object
}

/**
* The conformance runner will request a list of failures as the first request.
* This will be known by message_type == "conformance.FailureSet", a conformance
* test should return a serialized FailureSet in protobuf_payload.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.FailureSetInternal.CODEC::class)
interface FailureSet { 
    val test: List<com.google.protobuf.conformance.TestStatus>

    companion object
}

/**
* Represents a single test case's input.  The testee should:
* 
*   1. parse this proto (which should always succeed)
*   2. parse the protobuf or JSON payload in "payload" (which may fail)
*   3. if the parse succeeded, serialize the message in the requested format.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.ConformanceRequestInternal.CODEC::class)
interface ConformanceRequest { 
    /**
    * Which format should the testee serialize its message to?
    */
    val requestedOutputFormat: com.google.protobuf.conformance.WireFormat
    /**
    * The full name for the test message to use; for the moment, either:
    * protobuf_test_messages.proto3.TestAllTypesProto3 or
    * protobuf_test_messages.proto2.TestAllTypesProto2 or
    * protobuf_test_messages.editions.proto2.TestAllTypesProto2 or
    * protobuf_test_messages.editions.proto3.TestAllTypesProto3 or
    * protobuf_test_messages.editions.TestAllTypesEdition2023.
    */
    val messageType: String
    /**
    * Each test is given a specific test category. Some category may need
    * specific support in testee programs. Refer to the definition of
    * TestCategory for more information.
    */
    val testCategory: com.google.protobuf.conformance.TestCategory
    /**
    * Specify details for how to encode jspb.
    */
    val jspbEncodingOptions: com.google.protobuf.conformance.JspbEncodingConfig
    /**
    * This can be used in json and text format. If true, testee should print
    * unknown fields instead of ignore. This feature is optional.
    */
    val printUnknownFields: Boolean
    val payload: com.google.protobuf.conformance.ConformanceRequest.Payload?

    sealed interface Payload { 
        @JvmInline
        value class ProtobufPayload(val value: ByteArray): Payload

        @JvmInline
        value class JsonPayload(val value: String): Payload

        /**
        * Only used inside Google.  Opensource testees just skip it.
        */
        @JvmInline
        value class JspbPayload(val value: String): Payload

        @JvmInline
        value class TextPayload(val value: String): Payload
    }

    companion object
}

/**
* Represents a single test case's output.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.ConformanceResponseInternal.CODEC::class)
interface ConformanceResponse { 
    val result: com.google.protobuf.conformance.ConformanceResponse.Result?

    sealed interface Result { 
        /**
        * This string should be set to indicate parsing failed.  The string can
        * provide more information about the parse error if it is available.
        * 
        * Setting this string does not necessarily mean the testee failed the
        * test.  Some of the test cases are intentionally invalid input.
        */
        @JvmInline
        value class ParseError(val value: String): Result

        /**
        * If the input was successfully parsed but errors occurred when
        * serializing it to the requested output format, set the error message in
        * this field.
        */
        @JvmInline
        value class SerializeError(val value: String): Result

        /**
        * This should be set if the test program timed out.  The string should
        * provide more information about what the child process was doing when it
        * was killed.
        */
        @JvmInline
        value class TimeoutError(val value: String): Result

        /**
        * This should be set if some other error occurred.  This will always
        * indicate that the test failed.  The string can provide more information
        * about the failure.
        */
        @JvmInline
        value class RuntimeError(val value: String): Result

        /**
        * If the input was successfully parsed and the requested output was
        * protobuf, serialize it to protobuf and set it in this field.
        */
        @JvmInline
        value class ProtobufPayload(val value: ByteArray): Result

        /**
        * If the input was successfully parsed and the requested output was JSON,
        * serialize to JSON and set it in this field.
        */
        @JvmInline
        value class JsonPayload(val value: String): Result

        /**
        * For when the testee skipped the test, likely because a certain feature
        * wasn't supported, like JSON input/output.
        */
        @JvmInline
        value class Skipped(val value: String): Result

        /**
        * If the input was successfully parsed and the requested output was JSPB,
        * serialize to JSPB and set it in this field. JSPB is only used inside
        * Google. Opensource testees can just skip it.
        */
        @JvmInline
        value class JspbPayload(val value: String): Result

        /**
        * If the input was successfully parsed and the requested output was
        * TEXT_FORMAT, serialize to TEXT_FORMAT and set it in this field.
        */
        @JvmInline
        value class TextPayload(val value: String): Result
    }

    companion object
}

/**
* Encoding options for jspb format.
*/
@kotlinx.rpc.grpc.codec.WithCodec(com.google.protobuf.conformance.JspbEncodingConfigInternal.CODEC::class)
interface JspbEncodingConfig { 
    /**
    * Encode the value field of Any as jspb array if true, otherwise binary.
    */
    val useJspbArrayAnyFormat: Boolean

    companion object
}

/**
* This defines the conformance testing protocol.  This protocol exists between
* the conformance test suite itself and the code being tested.  For each test,
* the suite will send a ConformanceRequest message and expect a
* ConformanceResponse message.
* 
* You can either run the tests in two different ways:
* 
*   1. in-process (using the interface in conformance_test.h).
* 
*   2. as a sub-process communicating over a pipe.  Information about how to
*      do this is in conformance_test_runner.cc.
* 
* Pros/cons of the two approaches:
* 
*   - running as a sub-process is much simpler for languages other than C/C++.
* 
*   - running as a sub-process may be more tricky in unusual environments like
*     iOS apps, where fork/stdin/stdout are not available.
*/
sealed class WireFormat(open val number: Int) { 
    object UNSPECIFIED: WireFormat(number = 0)

    object PROTOBUF: WireFormat(number = 1)

    object JSON: WireFormat(number = 2)

    /**
    * Only used inside Google. Opensource testees just skip it.
    */
    object JSPB: WireFormat(number = 3)

    object TEXT_FORMAT: WireFormat(number = 4)

    data class UNRECOGNIZED(override val number: Int): WireFormat(number)

    companion object { 
        val entries: List<WireFormat> by lazy { listOf(UNSPECIFIED, PROTOBUF, JSON, JSPB, TEXT_FORMAT) }
    }
}

sealed class TestCategory(open val number: Int) { 
    object UNSPECIFIED_TEST: TestCategory(number = 0)

    /**
    * Test binary wire format.
    */
    object BINARY_TEST: TestCategory(number = 1)

    /**
    * Test json wire format.
    */
    object JSON_TEST: TestCategory(number = 2)

    /**
    * Similar to JSON_TEST. However, during parsing json, testee should ignore
    * unknown fields. This feature is optional. Each implementation can decide
    * whether to support it.  See
    * https://developers.google.com/protocol-buffers/docs/proto3#json_options
    * for more detail.
    */
    object JSON_IGNORE_UNKNOWN_PARSING_TEST: TestCategory(number = 3)

    /**
    * Test jspb wire format. Only used inside Google. Opensource testees just
    * skip it.
    */
    object JSPB_TEST: TestCategory(number = 4)

    /**
    * Test text format. For cpp, java and python, testees can already deal with
    * this type. Testees of other languages can simply skip it.
    */
    object TEXT_FORMAT_TEST: TestCategory(number = 5)

    data class UNRECOGNIZED(override val number: Int): TestCategory(number)

    companion object { 
        val entries: List<TestCategory> by lazy { listOf(UNSPECIFIED_TEST, BINARY_TEST, JSON_TEST, JSON_IGNORE_UNKNOWN_PARSING_TEST, JSPB_TEST, TEXT_FORMAT_TEST) }
    }
}
