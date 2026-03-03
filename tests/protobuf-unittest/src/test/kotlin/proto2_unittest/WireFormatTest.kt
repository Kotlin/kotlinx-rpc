/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java
//
// Tests NOT copied (depend on Java-specific APIs):
// - testSerializeExtensions: Extensions API
// - testSerializePackedExtensions: Extensions API
// - testParseExtensions: ExtensionRegistry
// - testParsePackedExtensions: ExtensionRegistry
// - testInterleavedFieldsAndExtensions: DynamicMessage + Extensions
// - testParseMultipleExtensionRanges: Extensions
// - testExtensionInsideTable: Extensions
// - testParseMultipleExtensionRangesDynamic: DynamicMessage
// - All MessageSet tests: MessageSet wire format (Java-specific legacy)

package proto2_unittest

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.marshallerOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class WireFormatTest {

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testSerialization
    @Test
    fun testSerialization() {
        val message = TestUtil.getAllSet()
        val marshaller = marshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertAllFieldsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testSerializationPacked
    @Test
    fun testSerializationPacked() {
        val message = TestUtil.getPackedSet()
        val marshaller = marshallerOf<TestPackedTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertPackedFieldsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testOneofWireFormat
    @Test
    fun testOneofWireFormat() {
        val marshaller = marshallerOf<TestAllTypes>()

        val withUint32 = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofUint32(42u)
        }
        val decoded1 = TestUtil.encodeDecode(withUint32, marshaller)
        assertIs<TestAllTypes.OneofField.OneofUint32>(decoded1.oneofField)
        assertEquals(42u, (decoded1.oneofField as TestAllTypes.OneofField.OneofUint32).value)

        val withString = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofString("hello")
        }
        val decoded2 = TestUtil.encodeDecode(withString, marshaller)
        assertIs<TestAllTypes.OneofField.OneofString>(decoded2.oneofField)
        assertEquals("hello", (decoded2.oneofField as TestAllTypes.OneofField.OneofString).value)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testOneofOnlyLastSet
    @Test
    fun testOneofOnlyLastSet() {
        val marshaller = marshallerOf<TestAllTypes>()

        val withUint32 = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofUint32(42u)
        }
        val withString = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofString("last")
        }

        // Simulate conflicting oneof fields on the wire: parser must keep the last one.
        val encoded = Buffer().apply {
            transferFrom(marshaller.encode(withUint32))
            transferFrom(marshaller.encode(withString))
        }
        val decoded = marshaller.decode(encoded)
        assertIs<TestAllTypes.OneofField.OneofString>(decoded.oneofField)
        assertEquals("last", (decoded.oneofField as TestAllTypes.OneofField.OneofString).value)

        val encodedReverse = Buffer().apply {
            transferFrom(marshaller.encode(withString))
            transferFrom(marshaller.encode(withUint32))
        }
        val decodedReverse = marshaller.decode(encodedReverse)
        assertIs<TestAllTypes.OneofField.OneofUint32>(decodedReverse.oneofField)
        assertEquals(42u, (decodedReverse.oneofField as TestAllTypes.OneofField.OneofUint32).value)
    }

    @Test
    fun testPackedToUnpackedWireCompat() {
        // Packed and unpacked have the same field numbers but different wire formats.
        // A conformant parser should accept both.
        val packed = TestUtil.getPackedSet()
        val packedMarshaller = marshallerOf<TestPackedTypes>()
        val unpackedMarshaller = marshallerOf<TestUnpackedTypes>()

        val encoded = packedMarshaller.encode(packed)
        val decoded = unpackedMarshaller.decode(encoded)
        TestUtil.assertUnpackedFieldsSet(decoded)
    }

    @Test
    fun testUnpackedToPackedWireCompat() {
        val unpacked = TestUtil.getUnpackedSet()
        val unpackedMarshaller = marshallerOf<TestUnpackedTypes>()
        val packedMarshaller = marshallerOf<TestPackedTypes>()

        val encoded = unpackedMarshaller.encode(unpacked)
        val decoded = packedMarshaller.decode(encoded)
        TestUtil.assertPackedFieldsSet(decoded)
    }

    @Test
    fun testEmptyMessageSerialization() {
        val msg = TestAllTypes {}
        val marshaller = marshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(msg, marshaller)
        assertNull(decoded.optionalInt32)
        assertEquals(0, decoded.repeatedInt32.size)
    }
}
