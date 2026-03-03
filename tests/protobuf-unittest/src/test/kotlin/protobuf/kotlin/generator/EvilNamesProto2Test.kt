/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

// Translated from:
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordGettersAndSetters
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordHazzers
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordClears
//
// Proto source:
// https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto2.proto
//
// Tests NOT copied (depend on Kotlin protobuf DSL API that differs from ours):
// - testHardKeywordClears: Java/Kotlin clearX() builder pattern (our API uses copy())

package protobuf.kotlin.generator

import kotlinx.rpc.grpc.marshaller.marshallerOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EvilNamesProto2Test {

    // ---------- Compilation smoke tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testEvilNamesProto2Compiles() {
        // If this compiles and runs, the generator correctly handled all evil names.
        val msg = EvilNamesProto2 {}
        assertNotNull(msg)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto2.proto - message List
    @Test
    fun testListMessageCompiles() {
        // Message named "List" — conflicts with kotlin.collections.List
        val msg = List {}
        assertNotNull(msg)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/proto/com/google/protobuf/evil_names_proto2.proto - message Interface
    @Test
    fun testInterfaceMessageCompiles() {
        // Message named "Interface" — Kotlin soft keyword
        val msg = Interface {}
        assertNotNull(msg)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordsAllTypesProto2Compiles() {
        val msg = HardKeywordsAllTypesProto2 {}
        assertNotNull(msg)
    }

    // ---------- Keyword field access tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testKeywordFieldsCanBeSet() {
        val msg = EvilNamesProto2 {
            `class` = listOf(1, 2, 3)
            int = 3.14
            long = true
            boolean = 42L
            sealed = "sealed_value"
            `interface` = 2.71f
            `object` = "object_value"
            by = "by_value"
        }

        assertEquals(listOf(1, 2, 3), msg.`class`)
        assertEquals(3.14, msg.int)
        assertEquals(true, msg.long)
        assertEquals(42L, msg.boolean)
        assertEquals("sealed_value", msg.sealed)
        assertEquals(2.71f, msg.`interface`)
        assertEquals("object_value", msg.`object`)
        assertEquals("by_value", msg.by)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testHasFooField() {
        val msg = EvilNamesProto2 {
            hasFoo = true
        }
        assertEquals(true, msg.hasFoo)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testBarField() {
        val msg = EvilNamesProto2 {
            Bar = "hello"
        }
        assertEquals("hello", msg.Bar)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testRepeatedKeywordFields() {
        val msg = EvilNamesProto2 {
            `class` = listOf(10, 20, 30)
            extension = listOf("ext1", "ext2")
            ALL_CAPS = listOf("A", "B")
        }
        assertEquals(listOf(10, 20, 30), msg.`class`)
        assertEquals(listOf("ext1", "ext2"), msg.extension)
        assertEquals(listOf("A", "B"), msg.ALL_CAPS)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testMapField() {
        val msg = EvilNamesProto2 {
            ALL_CAPS_MAP = mapOf(1 to true, 2 to false)
        }
        assertEquals(mapOf(1 to true, 2 to false), msg.ALL_CAPS_MAP)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testOneofField() {
        val msg = EvilNamesProto2 {
            camelCase = EvilNamesProto2.CamelCase.FooBar("test")
        }
        val camelCase = msg.camelCase
        assertTrue(camelCase is EvilNamesProto2.CamelCase.FooBar)
        assertEquals("test", camelCase.value)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordHazzers
    @Test
    fun testPresenceForKeywordFields() {
        val empty = EvilNamesProto2 {}
        assertEquals(false, empty.presence.hasInt)
        assertEquals(false, empty.presence.hasLong)
        assertEquals(false, empty.presence.hasBoolean)
        assertEquals(false, empty.presence.hasSealed)
        assertEquals(false, empty.presence.hasInterface)
        assertEquals(false, empty.presence.hasObject)
        assertEquals(false, empty.presence.hasBy)

        val filled = EvilNamesProto2 {
            int = 1.0
            long = true
            boolean = 1L
            sealed = "s"
            `interface` = 1.0f
            `object` = "o"
            by = "b"
        }
        assertEquals(true, filled.presence.hasInt)
        assertEquals(true, filled.presence.hasLong)
        assertEquals(true, filled.presence.hasBoolean)
        assertEquals(true, filled.presence.hasSealed)
        assertEquals(true, filled.presence.hasInterface)
        assertEquals(true, filled.presence.hasObject)
        assertEquals(true, filled.presence.hasBy)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testDeprecatedFieldNames() {
        val msg = EvilNamesProto2 {
            DEPRECATEDFoo = "deprecated"
            __DEPRECATED_Bar = "also_deprecated"
            not_DEPRECATEDFoo = "not_deprecated"
        }
        assertEquals("deprecated", msg.DEPRECATEDFoo)
        assertEquals("also_deprecated", msg.__DEPRECATED_Bar)
        assertEquals("not_deprecated", msg.not_DEPRECATEDFoo)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testIDField() {
        val msg = EvilNamesProto2 {
            ID = "test-id"
        }
        assertEquals("test-id", msg.ID)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testUnderbarPrecedingNumericFields() {
        val msg = EvilNamesProto2 {
            hasUnderbarPrecedingNumeric_1foo = true
            hasUnderbarPrecedingNumeric_42bar = false
            hasUnderbarPrecedingNumeric_123foo42barBaz = true
        }
        assertEquals(true, msg.hasUnderbarPrecedingNumeric_1foo)
        assertEquals(false, msg.hasUnderbarPrecedingNumeric_42bar)
        assertEquals(true, msg.hasUnderbarPrecedingNumeric_123foo42barBaz)
    }

    // ---------- Hard keywords tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordOptionalFields() {
        val msg = HardKeywordsAllTypesProto2 {
            `as` = 42
            `break` = HardKeywordsAllTypesProto2.NestedEnum.FOO
            `do` = HardKeywordsAllTypesProto2.NestedMessage { `while` = 100 }
        }
        assertEquals(42, msg.`as`)
        assertEquals(HardKeywordsAllTypesProto2.NestedEnum.FOO, msg.`break`)
        assertEquals(100, msg.`do`.`while`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordMapField() {
        val msg = HardKeywordsAllTypesProto2 {
            `continue` = mapOf(1 to 10, 2 to 20)
        }
        assertEquals(mapOf(1 to 10, 2 to 20), msg.`continue`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordRepeatedFields() {
        val msg = HardKeywordsAllTypesProto2 {
            `else` = listOf(1, 2, 3)
            `for` = listOf("a", "b")
            `fun` = listOf(HardKeywordsAllTypesProto2.NestedEnum.FOO, HardKeywordsAllTypesProto2.NestedEnum.BAR)
            `if` = listOf(
                HardKeywordsAllTypesProto2.NestedMessage { `while` = 1 },
                HardKeywordsAllTypesProto2.NestedMessage { `while` = 2 },
            )
        }
        assertEquals(listOf(1, 2, 3), msg.`else`)
        assertEquals(listOf("a", "b"), msg.`for`)
        assertEquals(
            listOf(HardKeywordsAllTypesProto2.NestedEnum.FOO, HardKeywordsAllTypesProto2.NestedEnum.BAR),
            msg.`fun`,
        )
        assertEquals(2, msg.`if`.size)
        assertEquals(1, msg.`if`[0].`while`)
        assertEquals(2, msg.`if`[1].`while`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordHazzers
    @Test
    fun testHardKeywordPresence() {
        val empty = HardKeywordsAllTypesProto2 {}
        assertEquals(false, empty.presence.hasAs)
        assertEquals(false, empty.presence.hasBreak)

        val filled = HardKeywordsAllTypesProto2 {
            `as` = 1
            `break` = HardKeywordsAllTypesProto2.NestedEnum.BAR
        }
        assertEquals(true, filled.presence.hasAs)
        assertEquals(true, filled.presence.hasBreak)
    }

    // ---------- Serialization round-trip tests ----------

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testEvilNamesRoundTrip() {
        val marshaller = marshallerOf<EvilNamesProto2>()
        val msg = EvilNamesProto2 {
            hasFoo = true
            Bar = "test"
            `class` = listOf(1, 2)
            int = 3.14
            long = false
            boolean = 99L
            sealed = "sealed"
            `interface` = 1.5f
            `object` = "obj"
            by = "by"
            ALL_CAPS = listOf("X")
            ALL_CAPS_MAP = mapOf(1 to true)
            extension = listOf("e1")
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
        assertEquals(msg.`object`, decoded.`object`)
        assertEquals(msg.by, decoded.by)
        assertEquals(msg.ALL_CAPS, decoded.ALL_CAPS)
        assertEquals(msg.ALL_CAPS_MAP, decoded.ALL_CAPS_MAP)
        assertEquals(msg.extension, decoded.extension)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testHardKeywordGettersAndSetters
    @Test
    fun testHardKeywordsRoundTrip() {
        val marshaller = marshallerOf<HardKeywordsAllTypesProto2>()
        val msg = HardKeywordsAllTypesProto2 {
            `as` = 42
            `break` = HardKeywordsAllTypesProto2.NestedEnum.FOO
            `continue` = mapOf(1 to 10)
            `do` = HardKeywordsAllTypesProto2.NestedMessage { `while` = 7 }
            `else` = listOf(1, 2, 3)
            `for` = listOf("x", "y")
            `fun` = listOf(HardKeywordsAllTypesProto2.NestedEnum.BAR)
            `if` = listOf(HardKeywordsAllTypesProto2.NestedMessage { `while` = 99 })
        }
        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)

        assertEquals(42, decoded.`as`)
        assertEquals(HardKeywordsAllTypesProto2.NestedEnum.FOO, decoded.`break`)
        assertEquals(mapOf(1 to 10), decoded.`continue`)
        assertEquals(7, decoded.`do`.`while`)
        assertEquals(listOf(1, 2, 3), decoded.`else`)
        assertEquals(listOf("x", "y"), decoded.`for`)
        assertEquals(listOf(HardKeywordsAllTypesProto2.NestedEnum.BAR), decoded.`fun`)
        assertEquals(1, decoded.`if`.size)
        assertEquals(99, decoded.`if`[0].`while`)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testCopyWithKeywordFields() {
        val original = EvilNamesProto2 {
            int = 1.0
            sealed = "original"
        }
        val copy = original.copy {
            sealed = "modified"
        }
        assertEquals(1.0, copy.int)
        assertEquals("modified", copy.sealed)
    }

    // https://github.com/protocolbuffers/protobuf/blob/main/java/kotlin/src/test/kotlin/com/google/protobuf/Proto2Test.kt#testEvilNames
    @Test
    fun testEmptyEvilNamesMessageRoundTrip() {
        val marshaller = marshallerOf<EvilNamesProto2>()
        val msg = EvilNamesProto2 {}
        val encoded = marshaller.encode(msg)
        val decoded = marshaller.decode(encoded)
        assertNull(decoded.hasFoo)
        assertNull(decoded.int)
        assertNull(decoded.long)
        assertEquals(emptyList(), decoded.`class`)
    }
}
