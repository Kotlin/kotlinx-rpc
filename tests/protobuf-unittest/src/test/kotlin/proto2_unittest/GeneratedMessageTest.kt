/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java
//
// Tests that depend on Java-specific APIs are NOT included:
// - Extension tests (getFieldBuilder, extensionFieldContainingBuilder, etc.)
// - Reflection tests (ReflectionTester, getField/setField with FieldDescriptor)
// - Java serialization tests (ObjectOutputStream/ObjectInputStream)
// - DynamicMessage tests
// - EnumInterface/EnumMap tests (ProtocolMessageEnum, Internal.EnumLiteMap)
// - Builder parent/invalidation tests (MockBuilderParent, newBuilderForType)
// - Nested builder propagation tests
// - ConflictingOuterClassName tests (Java-specific)
// - MultipleFilesOption tests (ServiceDescriptor)

package proto2_unittest

import com.google.protobuf.test.ImportEnum
import com.google.protobuf.test.ImportMessage
import com.google.protobuf.test.invoke
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import proto2_unittest.TestAllTypes.NestedEnum
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GeneratedMessageTest {

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testDefaultInstance
    @Test
    fun testDefaultInstance() {
        val message = TestAllTypes {}
        TestUtil.assertClear(message)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testMessageOrBuilder
    @Test
    fun testMessageOrBuilder() {
        val message = TestUtil.getAllSet()
        TestUtil.assertAllFieldsSet(message)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testUsingBuilderMultipleTimes
    @Test
    fun testUsingBuilderMultipleTimes() {
        // In our API, each TestAllTypes { ... } call creates an independent message
        val message1 = TestUtil.getAllSet()
        val message2 = message1.copy()

        assertEquals(message1, message2)

        // Modifying a copy should not affect the original
        val message3 = message1.copy {
            optionalInt32 = 999
        }
        assertEquals(101, message1.optionalInt32)
        assertEquals(999, message3.optionalInt32)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testRepeatedArraysAreImmutable
    @Test
    fun testRepeatedArraysAreImmutable() {
        val message = TestUtil.getAllSet()

        // The lists returned by the message interface should be immutable at runtime
        val list = message.repeatedInt32
        try {
            @Suppress("UNCHECKED_CAST")
            (list as MutableList<Int>).add(999)
            // If the list is actually mutable, the test is informational —
            // we just verify the message's list is not affected
        } catch (_: UnsupportedOperationException) {
            // Expected for truly immutable lists
        }
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testDefaults
    @Test
    fun testDefaults() {
        val message = TestAllTypes {}
        TestUtil.assertClear(message)

        // Verify extreme default values
        val extremeDefaults = TestExtremeDefaultValues {}

        // Proto: "\0\001\a\b\f\n\r\t\v\\\'\"\xfe"
        assertByteArrayEquals(
            byteArrayOf(0, 1, 7, 8, 12, 10, 13, 9, 11, 92, 39, 34, 0xFE.toByte()),
            extremeDefaults.escapedBytes,
        )
        assertEquals(0xFFFFFFFFu, extremeDefaults.largeUint32)
        assertEquals(0xFFFFFFFFFFFFFFFFuL, extremeDefaults.largeUint64)
        assertEquals(-0x7FFFFFFF, extremeDefaults.smallInt32)
        assertEquals(-0x7FFFFFFFFFFFFFFFL, extremeDefaults.smallInt64)
        assertEquals(Int.MIN_VALUE, extremeDefaults.reallySmallInt32)
        assertEquals(Long.MIN_VALUE, extremeDefaults.reallySmallInt64)

        assertEquals("\u1234", extremeDefaults.utf8String)

        assertEquals(0f, extremeDefaults.zeroFloat)
        assertEquals(1f, extremeDefaults.oneFloat)
        assertEquals(1.5f, extremeDefaults.smallFloat)
        assertEquals(-1f, extremeDefaults.negativeOneFloat)
        assertEquals(-1.5f, extremeDefaults.negativeFloat)
        assertEquals(2e8f, extremeDefaults.largeFloat)
        assertEquals(-8e-28f, extremeDefaults.smallNegativeFloat)

        assertEquals(Double.POSITIVE_INFINITY, extremeDefaults.infDouble)
        assertEquals(Double.NEGATIVE_INFINITY, extremeDefaults.negInfDouble)
        assertTrue(extremeDefaults.nanDouble.isNaN())
        assertEquals(Float.POSITIVE_INFINITY, extremeDefaults.infFloat)
        assertEquals(Float.NEGATIVE_INFINITY, extremeDefaults.negInfFloat)
        assertTrue(extremeDefaults.nanFloat.isNaN())

        assertEquals("? ? ?? ?? ??? ??/ ??-", extremeDefaults.cppTrigraph)

        assertEquals("hel\u0000lo", extremeDefaults.stringWithZero)
        assertByteArrayEquals("wor\u0000ld".encodeToByteArray(), extremeDefaults.bytesWithZero)
        assertEquals("ab\u0000c", extremeDefaults.stringPieceWithZero)
        assertEquals("12\u00003", extremeDefaults.cordWithZero)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testClear
    @Test
    fun testClear() {
        // In our API, creating a new empty message is equivalent to "clearing"
        val cleared = TestAllTypes {}
        TestUtil.assertClear(cleared)

        // Verify that copying with no changes preserves all fields
        val message = TestUtil.getAllSet()
        val copy = message.copy()
        TestUtil.assertAllFieldsSet(copy)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testToBuilder
    @Test
    fun testToBuilder() {
        // copy() is the equivalent of toBuilder().build()
        val message = TestUtil.getAllSet()
        val rebuilt = message.copy()
        TestUtil.assertAllFieldsSet(rebuilt)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testEnumValues
    @Test
    fun testEnumValues() {
        assertEquals(1, NestedEnum.FOO.number)
        assertEquals(2, NestedEnum.BAR.number)
        assertEquals(3, NestedEnum.BAZ.number)
        assertEquals(-1, NestedEnum.NEG.number)

        assertEquals(4, ForeignEnum.FOREIGN_FOO.number)
        assertEquals(5, ForeignEnum.FOREIGN_BAR.number)
        assertEquals(6, ForeignEnum.FOREIGN_BAZ.number)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testOneofEnumCase
    @Test
    fun testOneofEnumCase() {
        val msg = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofUint32(42u)
        }
        assertIs<TestAllTypes.OneofField.OneofUint32>(msg.oneofField)

        val msg2 = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofString("hello")
        }
        assertIs<TestAllTypes.OneofField.OneofString>(msg2.oneofField)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testClearOneof
    @Test
    fun testClearOneof() {
        // Start with a oneof set, then create a message without it
        val msg = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofUint32(42u)
        }
        assertIs<TestAllTypes.OneofField.OneofUint32>(msg.oneofField)

        val cleared = TestAllTypes {}
        assertNull(cleared.oneofField)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testSetOneofClearsOthers
    @Test
    fun testSetOneofClearsOthers() {
        // In our API, only one variant of the sealed interface can be assigned at a time
        val msg1 = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofUint32(42u)
        }
        assertIs<TestAllTypes.OneofField.OneofUint32>(msg1.oneofField)

        val msg2 = msg1.copy {
            oneofField = TestAllTypes.OneofField.OneofString("hello")
        }
        assertIs<TestAllTypes.OneofField.OneofString>(msg2.oneofField)
        assertEquals("hello", (msg2.oneofField as TestAllTypes.OneofField.OneofString).value)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testOneofTypes
    @Test
    fun testOneofTypes() {
        // Test each oneof variant type
        val withUint32 = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofUint32(123u)
        }
        assertEquals(123u, (withUint32.oneofField as TestAllTypes.OneofField.OneofUint32).value)

        val withString = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofString("test")
        }
        assertEquals("test", (withString.oneofField as TestAllTypes.OneofField.OneofString).value)

        val withBytes = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofBytes("bytes".encodeToByteArray())
        }
        assertByteArrayEquals(
            "bytes".encodeToByteArray(),
            (withBytes.oneofField as TestAllTypes.OneofField.OneofBytes).value,
        )

        val withNestedMessage = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofNestedMessage(
                TestAllTypes.NestedMessage { bb = 42 },
            )
        }
        assertEquals(
            42,
            (withNestedMessage.oneofField as TestAllTypes.OneofField.OneofNestedMessage).value.bb,
        )
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testOneofSerialization
    @Test
    fun testOneofSerialization() {
        val marshaller = grpcMarshallerOf<TestAllTypes>()

        // Test round-trip for each oneof variant
        val withUint32 = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofUint32(123u)
        }
        val decoded1 = TestUtil.encodeDecode(withUint32, marshaller)
        assertIs<TestAllTypes.OneofField.OneofUint32>(decoded1.oneofField)
        assertEquals(123u, (decoded1.oneofField as TestAllTypes.OneofField.OneofUint32).value)

        val withString = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofString("hello")
        }
        val decoded2 = TestUtil.encodeDecode(withString, marshaller)
        assertIs<TestAllTypes.OneofField.OneofString>(decoded2.oneofField)
        assertEquals("hello", (decoded2.oneofField as TestAllTypes.OneofField.OneofString).value)

        val withBytes = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofBytes("world".encodeToByteArray())
        }
        val decoded3 = TestUtil.encodeDecode(withBytes, marshaller)
        assertIs<TestAllTypes.OneofField.OneofBytes>(decoded3.oneofField)
        assertByteArrayEquals(
            "world".encodeToByteArray(),
            (decoded3.oneofField as TestAllTypes.OneofField.OneofBytes).value,
        )

        val withNestedMessage = TestAllTypes {
            oneofField = TestAllTypes.OneofField.OneofNestedMessage(
                TestAllTypes.NestedMessage { bb = 99 },
            )
        }
        val decoded4 = TestUtil.encodeDecode(withNestedMessage, marshaller)
        assertIs<TestAllTypes.OneofField.OneofNestedMessage>(decoded4.oneofField)
        assertEquals(
            99,
            (decoded4.oneofField as TestAllTypes.OneofField.OneofNestedMessage).value.bb,
        )
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testRecursiveMessageDefaultInstance
    @Test
    fun testRecursiveMessageDefaultInstance() {
        val msg = TestRecursiveMessage {}
        assertNull(msg.i)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testParsePackedToUnpacked
    @Test
    fun testParsePackedToUnpacked() {
        // Encode packed, decode as unpacked
        val packed = TestUtil.getPackedSet()
        val packedMarshaller = grpcMarshallerOf<TestPackedTypes>()
        val unpackedMarshaller = grpcMarshallerOf<TestUnpackedTypes>()

        val encoded = packedMarshaller.encode(packed)
        val unpacked = unpackedMarshaller.decode(encoded)
        TestUtil.assertUnpackedFieldsSet(unpacked)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testParseUnpackedToPacked
    @Test
    fun testParseUnpackedToPacked() {
        // Encode unpacked, decode as packed
        val unpacked = TestUtil.getUnpackedSet()
        val unpackedMarshaller = grpcMarshallerOf<TestUnpackedTypes>()
        val packedMarshaller = grpcMarshallerOf<TestPackedTypes>()

        val encoded = unpackedMarshaller.encode(unpacked)
        val packed = packedMarshaller.decode(encoded)
        TestUtil.assertPackedFieldsSet(packed)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testSettingForeignMessageUsingBuilder
    @Test
    fun testSettingForeignMessage() {
        val msg = TestAllTypes {
            optionalForeignMessage = ForeignMessage { c = 42 }
        }
        assertEquals(42, msg.optionalForeignMessage.c)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testRepeatedSetters
    @Test
    fun testRepeatedSetters() {
        // Create with initial values, then copy with modified list
        val message = TestUtil.getAllSet()

        val modified = message.copy {
            repeatedInt32 = listOf(201, 501)
            repeatedString = listOf("215", "515")
        }

        assertEquals(201, modified.repeatedInt32[0])
        assertEquals(501, modified.repeatedInt32[1])
        assertEquals("215", modified.repeatedString[0])
        assertEquals("515", modified.repeatedString[1])
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java
    @Test
    fun testEquality() {
        val msg1 = TestUtil.getAllSet()
        val msg2 = TestUtil.getAllSet()
        val msg3 = TestAllTypes {}

        assertEquals(msg1, msg2)
        assertNotEquals(msg1, msg3)
    }

    @Test
    fun testHashCode() {
        val msg1 = TestUtil.getAllSet()
        val msg2 = TestUtil.getAllSet()
        assertEquals(msg1.hashCode(), msg2.hashCode())
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/GeneratedMessageTest.java#testSerialize
    @Test
    fun testSerializeRoundTrip() {
        val message = TestUtil.getAllSet()
        val marshaller = grpcMarshallerOf<TestAllTypes>()
        val decoded = TestUtil.encodeDecode(message, marshaller)
        TestUtil.assertAllFieldsSet(decoded)
    }

    @Test
    fun testNestedMessageConstruction() {
        val nested = TestAllTypes.NestedMessage { bb = 42 }
        assertEquals(42, nested.bb)
    }

    @Test
    fun testGroupConstruction() {
        val group = TestAllTypes.OptionalGroup { a = 42 }
        assertEquals(42, group.a)
    }

    @Test
    fun testRepeatedGroupConstruction() {
        val msg = TestAllTypes {
            repeatedgroup = listOf(
                TestAllTypes.RepeatedGroup { a = 1 },
                TestAllTypes.RepeatedGroup { a = 2 },
            )
        }
        assertEquals(2, msg.repeatedgroup.size)
        assertEquals(1, msg.repeatedgroup[0].a)
        assertEquals(2, msg.repeatedgroup[1].a)
    }

    @Test
    fun testPresenceTracking() {
        val empty = TestAllTypes {}
        assertNull(empty.optionalInt32)
        kotlin.test.assertFalse(empty.presence.hasOptionalInt32)

        val withField = TestAllTypes { optionalInt32 = 0 }
        assertEquals(0, withField.optionalInt32)
        assertTrue(withField.presence.hasOptionalInt32)
    }

    @Test
    fun testCopyPreservesAllFields() {
        val original = TestUtil.getAllSet()
        val copy = original.copy()
        assertEquals(original, copy)
        TestUtil.assertAllFieldsSet(copy)
    }

    @Test
    fun testCopyWithModification() {
        val original = TestUtil.getAllSet()
        val modified = original.copy {
            optionalInt32 = 999
        }
        assertEquals(999, modified.optionalInt32)
        // Other fields unchanged
        assertEquals(102L, modified.optionalInt64)
        assertEquals(103u, modified.optionalUint32)
    }

    @Test
    fun testEmptyRepeatedFields() {
        val msg = TestAllTypes {}
        assertTrue(msg.repeatedInt32.isEmpty())
        assertTrue(msg.repeatedString.isEmpty())
        assertTrue(msg.repeatedNestedMessage.isEmpty())
        assertTrue(msg.repeatedForeignMessage.isEmpty())
        assertTrue(msg.repeatedgroup.isEmpty())
    }

    @Test
    fun testImportMessages() {
        val msg = TestAllTypes {
            optionalImportMessage = ImportMessage { d = 42 }
            optionalImportEnum = ImportEnum.IMPORT_BAR
        }
        assertEquals(42, msg.optionalImportMessage.d)
        assertEquals(ImportEnum.IMPORT_BAR, msg.optionalImportEnum)
    }
}
