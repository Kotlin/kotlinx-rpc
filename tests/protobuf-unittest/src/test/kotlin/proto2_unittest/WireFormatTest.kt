/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java
//
// Tests NOT copied (depend on Java-specific APIs):
// - testInterleavedFieldsAndExtensions: DynamicMessage + assertFieldsInOrder helper
// - testParseMultipleExtensionRangesDynamic: DynamicMessage
// - All MessageSet tests: MessageSet wire format (Java-specific legacy)

package proto2_unittest

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtoExtensionRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class WireFormatTest {

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testSerialization
    @Test
    fun testSerialization() {
        val message = TestUtil.getAllSet()
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertAllFieldsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testSerializationPacked
    @Test
    fun testSerializationPacked() {
        val message = TestUtil.getPackedSet()
        val marshaller = grpcMarshallerOf<TestPackedTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertPackedFieldsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testOneofWireFormat
    @Test
    fun testOneofWireFormat() {
        val marshaller = grpcMarshallerOf<TestAllTypes>()

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
        val marshaller = grpcMarshallerOf<TestAllTypes>()

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
        val packedMarshaller = grpcMarshallerOf<TestPackedTypes>()
        val unpackedMarshaller = grpcMarshallerOf<TestUnpackedTypes>()

        val encoded = packedMarshaller.encode(packed)
        val decoded = unpackedMarshaller.decode(encoded)
        TestUtil.assertUnpackedFieldsSet(decoded)
    }

    @Test
    fun testUnpackedToPackedWireCompat() {
        val unpacked = TestUtil.getUnpackedSet()
        val unpackedMarshaller = grpcMarshallerOf<TestUnpackedTypes>()
        val packedMarshaller = grpcMarshallerOf<TestPackedTypes>()

        val encoded = unpackedMarshaller.encode(unpacked)
        val decoded = packedMarshaller.decode(encoded)
        TestUtil.assertPackedFieldsSet(decoded)
    }

    @Test
    fun testEmptyMessageSerialization() {
        val msg = TestAllTypes {}
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(msg, marshaller)
        assertNull(decoded.optionalInt32)
        assertEquals(0, decoded.repeatedInt32.size)
    }

    // ===========================================================================================================
    // Extension tests — translated from WireFormatTest.java
    // ===========================================================================================================

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testSerializeExtensions
    @Test
    fun testSerializeExtensions() {
        // Extension round-trip: encode with extensions, decode with extension registry
        val message = TestUtil.getAllExtensionsSet()
        val config = ProtoConfig { extensionRegistry = TestUtil.getExtensionRegistry() }
        val marshaller = grpcMarshallerOf<TestAllExtensions>(config)
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertAllExtensionsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testSerializePackedExtensions
    @Test
    fun testSerializePackedExtensions() {
        val message = TestUtil.getPackedExtensionsSet()
        val packedExtMarshaller = grpcMarshallerOf<TestPackedExtensions>()
        val packedMarshaller = grpcMarshallerOf<TestPackedTypes>()
        // Packed extension wire format should be identical to regular packed types
        // (no groups involved in packed fields).
        val encoded = packedExtMarshaller.encode(message)
        val decoded = packedMarshaller.decode(encoded)
        TestUtil.assertPackedFieldsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testParseExtensions
    @Test
    fun testParseExtensions() {
        // Cross-format: encode packed types, decode as packed extensions
        val message = TestUtil.getPackedSet()
        val packedMarshaller = grpcMarshallerOf<TestPackedTypes>()
        val encoded = packedMarshaller.encode(message)
        val config = ProtoConfig { extensionRegistry = TestUtil.getPackedExtensionRegistry() }
        val packedExtMarshaller = grpcMarshallerOf<TestPackedExtensions>(config)
        val decoded = packedExtMarshaller.decode(encoded)
        TestUtil.assertPackedExtensionsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testParsePackedExtensions
    @Test
    fun testParsePackedExtensions() {
        val message = TestUtil.getPackedExtensionsSet()
        val config = ProtoConfig { extensionRegistry = TestUtil.getPackedExtensionRegistry() }
        val marshaller = grpcMarshallerOf<TestPackedExtensions>(config)
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertPackedExtensionsSet(decoded)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testParseMultipleExtensionRanges
    @Test
    fun testParseMultipleExtensionRanges() {
        val source = TestFieldOrderings {
            myInt = 1
            myString = "foo"
            myFloat = 1.0f
            myExtensionInt = 23
            myExtensionString = "bar"
        }
        val registry = ProtoExtensionRegistry {
            +TestFieldOrderings.myExtensionInt
            +TestFieldOrderings.myExtensionString
        }
        val config = ProtoConfig { extensionRegistry = registry }
        val marshaller = grpcMarshallerOf<TestFieldOrderings>(config)
        val dest = TestUtil.encodeDecode(source, marshaller)
        assertEquals(source, dest)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/WireFormatTest.java#testExtensionInsideTable
    @Test
    fun testExtensionInsideTable() {
        val source = TestExtensionInsideTable {
            field1 = 1
            testExtensionInsideTableExtension = 23
        }
        val registry = ProtoExtensionRegistry {
            +TestExtensionInsideTable.testExtensionInsideTableExtension
        }
        val config = ProtoConfig { extensionRegistry = registry }
        val marshaller = grpcMarshallerOf<TestExtensionInsideTable>(config)
        val dest = TestUtil.encodeDecode(source, marshaller)
        assertEquals(source, dest)
    }
}
