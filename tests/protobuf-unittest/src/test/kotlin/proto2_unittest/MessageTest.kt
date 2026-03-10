/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MessageTest.java
//
// Tests NOT copied (depend on Java-specific APIs):
// - testMergeFromDynamic: DynamicMessage API
// - testDynamicMergeFrom: DynamicMessage API
// - testRequiredExtension: isInitialized() Java API (TestRequired.single, TestRequired.multi)
// - testRequiredDynamic: DynamicMessage.newBuilder(descriptor)
// - testRequiredDynamicForeign: DynamicMessage + reflection
// - testDynamicUninitializedException: DynamicMessage
// - testDynamicBuildPartial: DynamicMessage
// - testDynamicParseUninitialized: DynamicMessage
// - testDynamicRepeatedMessageNull: DynamicMessage getField() reflection
// - testDynamicRepeatedMessageNotNull: DynamicMessage reflection

package proto2_unittest

import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class MessageTest {

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MessageTest.java#testMergeFrom
    @Test
    fun testMergeFrom() {
        // In our API, "merge" is done by copying with overrides.
        // Here we test that copy() preserves all fields from the source.
        val source = TestUtil.getAllSet()
        val copy = source.copy()
        TestUtil.assertAllFieldsSet(copy)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MessageTest.java#testPreservesFloatingPointNegative0
    @Test
    fun testPreservesFloatingPointNegative0() {
        val marshaller = grpcMarshallerOf<TestAllTypes>()

        val withNegZeroFloat = TestAllTypes {
            optionalFloat = -0.0f
        }
        val decoded1 = TestUtil.encodeDecode(withNegZeroFloat, marshaller)
        assertEquals(-0.0f, decoded1.optionalFloat)
        // Check the sign bit
        assertTrue(1.0f / decoded1.optionalFloat!! < 0, "Expected negative zero for float")

        val withNegZeroDouble = TestAllTypes {
            optionalDouble = -0.0
        }
        val decoded2 = TestUtil.encodeDecode(withNegZeroDouble, marshaller)
        assertEquals(-0.0, decoded2.optionalDouble)
        assertTrue(1.0 / decoded2.optionalDouble!! < 0, "Expected negative zero for double")
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MessageTest.java#testNegative0FloatingPointEquality
    @Test
    fun testNegative0FloatingPointEquality() {
        // -0.0 should not equal +0.0 in message equality
        val negZero = TestAllTypes {
            optionalFloat = -0.0f
        }
        val posZero = TestAllTypes {
            optionalFloat = 0.0f
        }
        assertNotEquals(negZero, posZero)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MessageTest.java
    @Test
    fun testSerializeRoundTrip() {
        val message = TestUtil.getAllSet()
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertAllFieldsSet(decoded)
    }

    @Test
    fun testEmptyMessageRoundTrip() {
        val message = TestAllTypes {}
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertClear(decoded)
    }

    @Test
    fun testPartialFieldsRoundTrip() {
        val marshaller = grpcMarshallerOf<TestAllTypes>()

        val partial = TestAllTypes {
            optionalInt32 = 42
            optionalString = "hello"
            repeatedInt32 = listOf(1, 2, 3)
        }
        val decoded = TestUtil.encodeDecode(partial, marshaller)
        assertEquals(42, decoded.optionalInt32)
        assertEquals("hello", decoded.optionalString)
        assertEquals(listOf(1, 2, 3), decoded.repeatedInt32)
        // Unset fields should be clear
        kotlin.test.assertNull(decoded.optionalInt64)
        kotlin.test.assertNull(decoded.optionalBool)
    }
}
