/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordGettersAndSetters
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordHazzers
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordClears
//
// Proto source:
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto3.proto
//
// Tests NOT copied (depend on Kotlin protobuf DSL API that differs from ours):
// - testHardKeywordClears: Java/Kotlin clearX() builder pattern (our API uses copy())

package protobuf.kotlin.generator

import kotlinx.rpc.grpc.marshaller.marshallerOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EvilNamesProto3Test {

    // ---------- Compilation smoke tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testEvilNamesProto3Compiles() {
        val msg = EvilNamesProto3 {}
        assertNotNull(msg)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto3.proto - message Class
    @Test
    fun testClassMessageCompiles() {
        // Message named "Class" — Kotlin hard keyword
        val msg = Class {}
        assertNotNull(msg)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordsAllTypesProto3Compiles() {
        val msg = HardKeywordsAllTypesProto3 {}
        assertNotNull(msg)
    }

    // ---------- Keyword field access tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testKeywordFieldsCanBeSet() {
        val msg = EvilNamesProto3 {
            `class` = "class_value"
            int = 3.14
            long = true
            boolean = 42L
            sealed = "sealed_value"
            `interface` = 2.71f
            `in` = 100
            `object` = "object_value"
        }

        assertEquals("class_value", msg.`class`)
        assertEquals(3.14, msg.int)
        assertEquals(true, msg.long)
        assertEquals(42L, msg.boolean)
        assertEquals("sealed_value", msg.sealed)
        assertEquals(2.71f, msg.`interface`)
        assertEquals(100, msg.`in`)
        assertEquals("object_value", msg.`object`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testProto3StringDefaults() {
        // Proto3 scalar fields have default values (not null)
        val msg = EvilNamesProto3 {}
        assertEquals("", msg.`class`)
        assertEquals(0.0, msg.int)
        assertEquals(false, msg.long)
        assertEquals(0L, msg.boolean)
        assertEquals("", msg.sealed)
        assertEquals(0.0f, msg.`interface`)
        assertEquals(0, msg.`in`)
        assertEquals("", msg.`object`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testAdditionalEvilNames() {
        // Fields that shadow common names: value, index, values, builder, map, key, pairs
        val msg = EvilNamesProto3 {
            value = "val"
            index = 42L
            values = listOf("v1", "v2")
            newValues = listOf("nv1")
            builder = true
            k = mapOf(1 to 10)
            v = mapOf("a" to "b")
            key = mapOf("k" to 1)
            map = mapOf(1 to "one")
            pairs = mapOf("p" to 2)
        }
        assertEquals("val", msg.value)
        assertEquals(42L, msg.index)
        assertEquals(listOf("v1", "v2"), msg.values)
        assertEquals(listOf("nv1"), msg.newValues)
        assertEquals(true, msg.builder)
        assertEquals(mapOf(1 to 10), msg.k)
        assertEquals(mapOf("a" to "b"), msg.v)
        assertEquals(mapOf("k" to 1), msg.key)
        assertEquals(mapOf(1 to "one"), msg.map)
        assertEquals(mapOf("p" to 2), msg.pairs)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testCachedSizeAndSerializedSizeFields() {
        // Fields named cached_size and serialized_size — shadow internal protobuf method names
        val msg = EvilNamesProto3 {
            cachedSize = "cached"
            serializedSize = true
        }
        assertEquals("cached", msg.cachedSize)
        assertEquals(true, msg.serializedSize)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testLeadingUnderscoreField() {
        val msg = EvilNamesProto3 {
            LeadingUnderscore = "leading"
        }
        assertEquals("leading", msg.LeadingUnderscore)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testOneofFields() {
        val msg1 = EvilNamesProto3 {
            camelCase = EvilNamesProto3.CamelCase.FooBar("test")
        }
        assertTrue(msg1.camelCase is EvilNamesProto3.CamelCase.FooBar)
        assertEquals("test", (msg1.camelCase as EvilNamesProto3.CamelCase.FooBar).value)

        val msg2 = EvilNamesProto3 {
            leadingUnderscoreOneof = EvilNamesProto3.LeadingUnderscoreOneof.Option(42)
        }
        assertTrue(msg2.leadingUnderscoreOneof is EvilNamesProto3.LeadingUnderscoreOneof.Option)
        assertEquals(42, (msg2.leadingUnderscoreOneof as EvilNamesProto3.LeadingUnderscoreOneof.Option).value)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testDeprecatedFieldNames() {
        val msg = EvilNamesProto3 {
            DEPRECATEDFoo = "deprecated"
            __DEPRECATED_Bar = "also_deprecated"
            not_DEPRECATEDFoo = "not_deprecated"
            ID = "id-123"
            aBNotification = "notif"
        }
        assertEquals("deprecated", msg.DEPRECATEDFoo)
        assertEquals("also_deprecated", msg.__DEPRECATED_Bar)
        assertEquals("not_deprecated", msg.not_DEPRECATEDFoo)
        assertEquals("id-123", msg.ID)
        assertEquals("notif", msg.aBNotification)
    }

    // ---------- Hard keywords tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordOptionalFields() {
        val msg = HardKeywordsAllTypesProto3 {
            `as` = 42
            `in` = "inside"
            `break` = HardKeywordsAllTypesProto3.NestedEnum.FOO
            `do` = HardKeywordsAllTypesProto3.NestedMessage { `while` = 100 }
        }
        assertEquals(42, msg.`as`)
        assertEquals("inside", msg.`in`)
        assertEquals(HardKeywordsAllTypesProto3.NestedEnum.FOO, msg.`break`)
        assertEquals(100, msg.`do`.`while`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordMapField() {
        val msg = HardKeywordsAllTypesProto3 {
            `continue` = mapOf(1 to 10, 2 to 20)
        }
        assertEquals(mapOf(1 to 10, 2 to 20), msg.`continue`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordRepeatedFields() {
        val msg = HardKeywordsAllTypesProto3 {
            `else` = listOf(1, 2, 3)
            `for` = listOf("a", "b")
            `fun` = listOf(HardKeywordsAllTypesProto3.NestedEnum.FOO, HardKeywordsAllTypesProto3.NestedEnum.BAR)
            `if` = listOf(
                HardKeywordsAllTypesProto3.NestedMessage { `while` = 1 },
                HardKeywordsAllTypesProto3.NestedMessage { `while` = 2 },
            )
        }
        assertEquals(listOf(1, 2, 3), msg.`else`)
        assertEquals(listOf("a", "b"), msg.`for`)
        assertEquals(
            listOf(HardKeywordsAllTypesProto3.NestedEnum.FOO, HardKeywordsAllTypesProto3.NestedEnum.BAR),
            msg.`fun`,
        )
        assertEquals(2, msg.`if`.size)
        assertEquals(1, msg.`if`[0].`while`)
        assertEquals(2, msg.`if`[1].`while`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto3.proto - HardKeywordsAllTypesProto3.NestedEnum
    @Test
    fun testNestedEnumWithKeywordName() {
        assertEquals(0, HardKeywordsAllTypesProto3.NestedEnum.ZERO.number)
        assertEquals(1, HardKeywordsAllTypesProto3.NestedEnum.FOO.number)
        assertEquals(2, HardKeywordsAllTypesProto3.NestedEnum.BAR.number)
        assertEquals(3, HardKeywordsAllTypesProto3.NestedEnum.entries.size)
    }

    // ---------- Serialization round-trip tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testEvilNamesProto3RoundTrip() {
        val marshaller = marshallerOf<EvilNamesProto3>()
        val msg = EvilNamesProto3 {
            hasFoo = true
            Bar = "test"
            `class` = "cls"
            int = 3.14
            long = false
            boolean = 99L
            sealed = "sealed"
            `interface` = 1.5f
            `in` = 7
            `object` = "obj"
            value = "v"
            index = 10L
            builder = true
            k = mapOf(1 to 2)
            v = mapOf("x" to "y")
            ALL_CAPS = listOf("X")
            ALL_CAPS_MAP = mapOf(1 to true)
        }
        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(msg.hasFoo, decoded.hasFoo)
        assertEquals(msg.Bar, decoded.Bar)
        assertEquals(msg.`class`, decoded.`class`)
        assertEquals(msg.int, decoded.int)
        assertEquals(msg.long, decoded.long)
        assertEquals(msg.boolean, decoded.boolean)
        assertEquals(msg.sealed, decoded.sealed)
        assertEquals(msg.`interface`, decoded.`interface`)
        assertEquals(msg.`in`, decoded.`in`)
        assertEquals(msg.`object`, decoded.`object`)
        assertEquals(msg.value, decoded.value)
        assertEquals(msg.index, decoded.index)
        assertEquals(msg.builder, decoded.builder)
        assertEquals(msg.k, decoded.k)
        assertEquals(msg.v, decoded.v)
        assertEquals(msg.ALL_CAPS, decoded.ALL_CAPS)
        assertEquals(msg.ALL_CAPS_MAP, decoded.ALL_CAPS_MAP)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordsProto3RoundTrip() {
        val marshaller = marshallerOf<HardKeywordsAllTypesProto3>()
        val msg = HardKeywordsAllTypesProto3 {
            `as` = 42
            `in` = "inside"
            `break` = HardKeywordsAllTypesProto3.NestedEnum.FOO
            `continue` = mapOf(1 to 10)
            `do` = HardKeywordsAllTypesProto3.NestedMessage { `while` = 7 }
            `else` = listOf(1, 2, 3)
            `for` = listOf("x", "y")
            `fun` = listOf(HardKeywordsAllTypesProto3.NestedEnum.BAR)
            `if` = listOf(HardKeywordsAllTypesProto3.NestedMessage { `while` = 99 })
        }
        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(42, decoded.`as`)
        assertEquals("inside", decoded.`in`)
        assertEquals(HardKeywordsAllTypesProto3.NestedEnum.FOO, decoded.`break`)
        assertEquals(mapOf(1 to 10), decoded.`continue`)
        assertEquals(7, decoded.`do`.`while`)
        assertEquals(listOf(1, 2, 3), decoded.`else`)
        assertEquals(listOf("x", "y"), decoded.`for`)
        assertEquals(listOf(HardKeywordsAllTypesProto3.NestedEnum.BAR), decoded.`fun`)
        assertEquals(1, decoded.`if`.size)
        assertEquals(99, decoded.`if`[0].`while`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto3Test.kt#testEvilNames
    @Test
    fun testCopyWithKeywordFields() {
        val original = EvilNamesProto3 {
            `class` = "original"
            int = 1.0
        }
        val copy = original.copy {
            `class` = "modified"
        }
        assertEquals("modified", copy.`class`)
        assertEquals(1.0, copy.int)
    }
}
