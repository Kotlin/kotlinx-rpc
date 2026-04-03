/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParserTest.java
//
// Tests NOT copied (depend on Java-specific APIs):
// - testGeneratedMessageParserSingleton: getParserForType() Java Parser API
// - testOptimizeForSize: TestOptimizedForSize + separate proto file registry
// - testParsingMerge: Complex groups + TestParsingMerge.RepeatedFieldsGenerator
// - testExceptionWhenMergingExtendedMessagesMissingRequiredFields: Java exception API

package proto2_unittest

import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.ProtoConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ParserTest {

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParserTest.java#testNormalMessage
    @Test
    fun testNormalMessage() {
        val message = TestUtil.getAllSet()
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertAllFieldsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParserTest.java#testParsePacked
    @Test
    fun testParsePacked() {
        val packed = TestUtil.getPackedSet()
        val marshaller = grpcMarshallerOf<TestPackedTypes>()
        val decoded = TestUtil.encodeDecode(packed, marshaller)
        TestUtil.assertPackedFieldsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParserTest.java#testParseUnknownFields
    @Test
    fun testParseUnknownFields() {
        // Encode a full message, then decode as an empty message type.
        // All fields should be treated as unknown and the bytes should round-trip.
        val message = TestUtil.getAllSet()
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val emptyMarshaller = grpcMarshallerOf<TestEmptyMessage>()

        val encoded = marshaller.encode(message)
        val empty = emptyMarshaller.decode(encoded)

        // Re-encode the empty message (which should preserve unknown fields)
        val reEncoded = emptyMarshaller.encode(empty)

        // Decode as TestAllTypes again — should recover all fields
        val recovered = marshaller.decode(reEncoded)
        TestUtil.assertAllFieldsSet(recovered)
    }

    @Test
    fun testParseEmptyMessage() {
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val msg = TestAllTypes {}
        val decoded = TestUtil.encodeDecode(msg, marshaller)
        assertNull(decoded.optionalInt32OrNull)
        assertEquals(0, decoded.optionalInt32)
        assertEquals(0, decoded.repeatedInt32.size)
    }

    @Test
    fun testParsePartialMessage() {
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val msg = TestAllTypes {
            optionalInt32 = 42
            optionalString = "hello"
        }
        val decoded = TestUtil.encodeDecode(msg, marshaller)
        assertEquals(42, decoded.optionalInt32)
        assertEquals("hello", decoded.optionalString)
        assertNull(decoded.optionalInt64OrNull)
        assertEquals(0L, decoded.optionalInt64)
    }

    @Test
    fun testParsePackedRoundTrip() {
        val marshaller = grpcMarshallerOf<TestPackedTypes>()

        val packed = TestPackedTypes {
            packedInt32 = listOf(1, 2, 3)
            packedBool = listOf(true, false, true)
            packedEnum = listOf(ForeignEnum.FOREIGN_FOO, ForeignEnum.FOREIGN_BAR)
        }
        val decoded = TestUtil.encodeDecode(packed, marshaller)
        assertEquals(listOf(1, 2, 3), decoded.packedInt32)
        assertEquals(listOf(true, false, true), decoded.packedBool)
        assertEquals(listOf(ForeignEnum.FOREIGN_FOO, ForeignEnum.FOREIGN_BAR), decoded.packedEnum)
    }

    @Test
    fun testNestedMessageParsing() {
        val marshaller = grpcMarshallerOf<TestAllTypes>()

        val msg = TestAllTypes {
            optionalNestedMessage = TestAllTypes.NestedMessage { bb = 42 }
            repeatedNestedMessage = listOf(
                TestAllTypes.NestedMessage { bb = 1 },
                TestAllTypes.NestedMessage { bb = 2 },
            )
        }
        val decoded = TestUtil.encodeDecode(msg, marshaller)
        assertEquals(42, decoded.optionalNestedMessage.bb)
        assertEquals(2, decoded.repeatedNestedMessage.size)
        assertEquals(1, decoded.repeatedNestedMessage[0].bb)
        assertEquals(2, decoded.repeatedNestedMessage[1].bb)
    }

    // ===========================================================================================================
    // Extension tests — translated from ParserTest.java
    // ===========================================================================================================

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/ParserTest.java#testParseExtensions
    @Test
    fun testParseExtensions() {
        val config = ProtoConfig(extensionRegistry = TestUtil.getExtensionRegistry())
        val marshaller = grpcMarshallerOf<TestAllExtensions>(config)
        val message = TestUtil.getAllExtensionsSet()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertAllExtensionsSet(decoded)
    }
}
