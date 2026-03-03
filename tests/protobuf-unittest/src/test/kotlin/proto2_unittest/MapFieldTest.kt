/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from (applicable tests only):
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapTest.java
// https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapForProto2Test.java
//
// Tests NOT copied from MapTest.java (depend on Java-specific APIs):
// - testGettersAndSetters: Java Builder getXOrDefault/getXOrThrow pattern
// - testRemove: Java Builder removeX pattern
// - testPut: Java Builder putX/putAllX pattern
// - testClear: Java Builder clearField pattern
// - testMutableMapReflection: DynamicMessage/Reflection API
// - testMergeFrom: Java mergeFrom Builder API
// - testEqualsAndHashCode: Tests Java Message equals with DynamicMessage
// - testContains: Java Builder containsX pattern
// - testPutForUnknownEnumValues: Java putAllX with unknown enum values
// - testGetMap: Java getMutableXMap/getXMap pattern
// - testPutChecksNullKeysAndValues: Java putX null checking
// - testUnmodifiable: Java getXMap unmodifiable checks
// - testMapFieldRoundTripOneByOne: Java putX iteration
// - testIterationOrder: Java entrySet order testing
// - testGettersAndSettersWithByteStrings: Java ByteString-based APIs
// - testPutAllForUnknownEnumValues: Extensions/DynamicMessage
// - testPutNewField: Java Builder field clearing semantics
//
// Tests NOT copied from MapForProto2Test.java (depend on Java-specific APIs):
// - testGettersAndSetters: Java Builder pattern
// - testRemove: Java Builder pattern
// - testPut: Java Builder pattern
// - testClear: Java Builder pattern
// - testMergeFrom: Java Builder mergeFrom
// - testEqualsAndHashCode: Java Message equals with DynamicMessage
// - testContains: Java Builder pattern
// - testGetMap: Java Builder pattern
// - testUnknownEnumValues: Java Builder with unknown enums
// - testTextFormatParsing/testTextFormat: TextFormat API
// - testDynamicMessage: DynamicMessage API
// - testReflectionApi: Reflection/Descriptors API
// - testPutChecksNullKeysAndValues: Java null checking
// - testSerializeAndParse: Java toByteString/parseFrom pattern (covered by our round-trip tests)
// - testMissingSingularMapField: Java parseFrom edge case
// - testHasFieldForMapField: Descriptor reflection
// - testMapFieldRoundTripOneByOne: Java putX iteration
// - testIterationOrder: Java entrySet
// - testPutAllForUnknownEnumValues: Java putAll with Descriptors
// - testPutNewField: Java Builder clearing semantics

package proto2_unittest

import kotlinx.rpc.grpc.marshaller.marshallerOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MapFieldTest {

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapTest.java#testSetMapValues
    @Test
    fun testSetMapValues() {
        val msg = TestMap {
            mapInt32Int32 = mapOf(1 to 11, 2 to 22, 3 to 33)
            mapInt64Int64 = mapOf(11L to 22L, 33L to 44L)
            mapStringString = mapOf("a" to "alpha", "b" to "beta")
            mapBoolBool = mapOf(true to false, false to true)
        }

        assertEquals(mapOf(1 to 11, 2 to 22, 3 to 33), msg.mapInt32Int32)
        assertEquals(mapOf(11L to 22L, 33L to 44L), msg.mapInt64Int64)
        assertEquals(mapOf("a" to "alpha", "b" to "beta"), msg.mapStringString)
        assertEquals(mapOf(true to false, false to true), msg.mapBoolBool)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapTest.java#testSerializeAndParse
    @Test
    fun testMapRoundTrip() {
        val marshaller = marshallerOf<TestMap>()

        val msg = TestMap {
            mapInt32Int32 = mapOf(1 to 10, 2 to 20)
            mapInt64Int64 = mapOf(100L to 200L)
            mapStringString = mapOf("key" to "value")
            mapBoolBool = mapOf(true to true)
            mapInt32Double = mapOf(1 to 1.5, 2 to 2.5)
            mapInt32Float = mapOf(3 to 3.5f)
            mapInt32Enum = mapOf(1 to MapEnum.MAP_ENUM_FOO, 2 to MapEnum.MAP_ENUM_BAR)
        }

        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(msg.mapInt32Int32, decoded.mapInt32Int32)
        assertEquals(msg.mapInt64Int64, decoded.mapInt64Int64)
        assertEquals(msg.mapStringString, decoded.mapStringString)
        assertEquals(msg.mapBoolBool, decoded.mapBoolBool)
        assertEquals(msg.mapInt32Double, decoded.mapInt32Double)
        assertEquals(msg.mapInt32Float, decoded.mapInt32Float)
        assertEquals(msg.mapInt32Enum, decoded.mapInt32Enum)
    }

    @Test
    fun testEmptyMapFields() {
        val msg = TestMap {}
        assertTrue(msg.mapInt32Int32.isEmpty())
        assertTrue(msg.mapStringString.isEmpty())
        assertTrue(msg.mapBoolBool.isEmpty())
    }

    @Test
    fun testMapWithMessageValues() {
        val marshaller = marshallerOf<TestMap>()

        val nested1 = ForeignMessage { c = 42 }
        val nested2 = ForeignMessage { c = 99 }

        val msg = TestMap {
            mapInt32ForeignMessage = mapOf(1 to nested1, 2 to nested2)
            mapStringForeignMessage = mapOf("first" to nested1, "second" to nested2)
        }

        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(2, decoded.mapInt32ForeignMessage.size)
        assertEquals(42, decoded.mapInt32ForeignMessage[1]?.c)
        assertEquals(99, decoded.mapInt32ForeignMessage[2]?.c)
        assertEquals(2, decoded.mapStringForeignMessage.size)
        assertEquals(42, decoded.mapStringForeignMessage["first"]?.c)
        assertEquals(99, decoded.mapStringForeignMessage["second"]?.c)
    }

    @Test
    fun testMapWithUnsignedKeys() {
        val marshaller = marshallerOf<TestMap>()

        val msg = TestMap {
            mapUint32Uint32 = mapOf(1u to 10u, 2u to 20u)
            mapUint64Uint64 = mapOf(100uL to 200uL)
            mapFixed32Fixed32 = mapOf(1u to 2u)
            mapFixed64Fixed64 = mapOf(3uL to 4uL)
        }

        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(msg.mapUint32Uint32, decoded.mapUint32Uint32)
        assertEquals(msg.mapUint64Uint64, decoded.mapUint64Uint64)
        assertEquals(msg.mapFixed32Fixed32, decoded.mapFixed32Fixed32)
        assertEquals(msg.mapFixed64Fixed64, decoded.mapFixed64Fixed64)
    }

    @Test
    fun testMapWithSignedKeys() {
        val marshaller = marshallerOf<TestMap>()

        val msg = TestMap {
            mapSint32Sint32 = mapOf(1 to -1, -2 to 2)
            mapSint64Sint64 = mapOf(1L to -1L, -2L to 2L)
            mapSfixed32Sfixed32 = mapOf(1 to -1)
            mapSfixed64Sfixed64 = mapOf(1L to -1L)
        }

        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(msg.mapSint32Sint32, decoded.mapSint32Sint32)
        assertEquals(msg.mapSint64Sint64, decoded.mapSint64Sint64)
        assertEquals(msg.mapSfixed32Sfixed32, decoded.mapSfixed32Sfixed32)
        assertEquals(msg.mapSfixed64Sfixed64, decoded.mapSfixed64Sfixed64)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/core/src/test/java/com/google/protobuf/MapForProto2Test.java - testSameTypeMaps
    @Test
    fun testSameTypeMap() {
        val marshaller = marshallerOf<TestSameTypeMap>()

        val msg = TestSameTypeMap {
            map1 = mapOf(1 to 10, 2 to 20)
            map2 = mapOf(3 to 30, 4 to 40)
        }

        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(mapOf(1 to 10, 2 to 20), decoded.map1)
        assertEquals(mapOf(3 to 30, 4 to 40), decoded.map2)
    }

    @Test
    fun testCopyWithMapFields() {
        val original = TestMap {
            mapInt32Int32 = mapOf(1 to 10)
            mapStringString = mapOf("a" to "b")
        }
        val copy = original.copy {
            mapInt32Int32 = mapOf(1 to 10, 2 to 20)
        }
        assertEquals(mapOf(1 to 10, 2 to 20), copy.mapInt32Int32)
        assertEquals(mapOf("a" to "b"), copy.mapStringString)
    }

    @Test
    fun testMapEnumValues() {
        val msg = TestMap {
            mapInt32Enum = mapOf(
                1 to MapEnum.MAP_ENUM_FOO,
                2 to MapEnum.MAP_ENUM_BAR,
                3 to MapEnum.MAP_ENUM_BAZ,
            )
        }
        assertEquals(MapEnum.MAP_ENUM_FOO, msg.mapInt32Enum[1])
        assertEquals(MapEnum.MAP_ENUM_BAR, msg.mapInt32Enum[2])
        assertEquals(MapEnum.MAP_ENUM_BAZ, msg.mapInt32Enum[3])
    }

    @Test
    fun testMapSubmessageRoundTrip() {
        val marshaller = marshallerOf<TestMapSubmessage>()

        val inner = TestMap {
            mapInt32Int32 = mapOf(1 to 2)
            mapStringString = mapOf("x" to "y")
        }
        val msg = TestMapSubmessage {
            testMap = inner
        }

        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(mapOf(1 to 2), decoded.testMap.mapInt32Int32)
        assertEquals(mapOf("x" to "y"), decoded.testMap.mapStringString)
    }
}
