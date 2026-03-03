/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import name_conflicts.FunctionParamConflicts
import name_conflicts.MoreInternalNameConflicts
import name_conflicts.InternalNameConflicts
import name_conflicts.KeywordCollections
import name_conflicts.KeywordEnum
import name_conflicts.LowercaseKeywordEnum
import name_conflicts.KeywordFields
import name_conflicts.KeywordFieldsWithPresence
import name_conflicts.KeywordOneof
import name_conflicts.NestedWithKeywords
import name_conflicts.copy
import name_conflicts.invoke
import name_conflicts.presence
import kotlinx.rpc.grpc.marshaller.marshallerOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Tests that name conflict resolution works correctly in generated code.
 *
 * Proto fields named as Kotlin keywords must be escaped with backticks.
 * Proto fields named as internal generated variables must not cause code conflicts.
 */
class NameConflictTest {

    // --- Kotlin keyword fields ---

    @Test
    fun keywordFieldsCanBeSetAndRead() {
        val msg = KeywordFields {
            `for` = 1
            `when` = 2
            `class` = 3
            `fun` = 4
            `val` = 5
            `var` = 6
            `if` = 7
            `else` = 8
            `return` = 9
            `while` = 10
            `do` = 11
            `is` = 12
            `in` = 13
            `object` = 14
            `this` = 15
            `super` = 16
            `null` = 17
            `true` = 18
            `false` = 19
            `try` = 20
            `throw` = 21
            `break` = 22
            `continue` = 23
            `as` = 24
            `interface` = 25
            `package` = 26
            `typealias` = 27
            `typeof` = 28
        }

        assertEquals(1, msg.`for`)
        assertEquals(2, msg.`when`)
        assertEquals(3, msg.`class`)
        assertEquals(4, msg.`fun`)
        assertEquals(5, msg.`val`)
        assertEquals(6, msg.`var`)
        assertEquals(7, msg.`if`)
        assertEquals(8, msg.`else`)
        assertEquals(9, msg.`return`)
        assertEquals(10, msg.`while`)
        assertEquals(11, msg.`do`)
        assertEquals(12, msg.`is`)
        assertEquals(13, msg.`in`)
        assertEquals(14, msg.`object`)
        assertEquals(15, msg.`this`)
        assertEquals(16, msg.`super`)
        assertEquals(17, msg.`null`)
        assertEquals(18, msg.`true`)
        assertEquals(19, msg.`false`)
        assertEquals(20, msg.`try`)
        assertEquals(21, msg.`throw`)
        assertEquals(22, msg.`break`)
        assertEquals(23, msg.`continue`)
        assertEquals(24, msg.`as`)
        assertEquals(25, msg.`interface`)
        assertEquals(26, msg.`package`)
        assertEquals(27, msg.`typealias`)
        assertEquals(28, msg.`typeof`)
    }

    @Test
    fun keywordFieldsEquality() {
        val msg1 = KeywordFields { `for` = 1; `when` = 2 }
        val msg2 = KeywordFields { `for` = 1; `when` = 2 }
        val msg3 = KeywordFields { `for` = 99; `when` = 2 }

        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())
        assertNotEquals(msg1, msg3)
    }

    @Test
    fun keywordFieldsCopy() {
        val original = KeywordFields { `for` = 1; `when` = 2; `class` = 3 }
        val copied = original.copy { `for` = 99 }

        assertEquals(1, original.`for`)
        assertEquals(99, copied.`for`)
        assertEquals(2, copied.`when`)
        assertEquals(3, copied.`class`)
    }

    @Test
    fun keywordFieldsEncodeDecode() {
        val msg = KeywordFields {
            `for` = 42
            `when` = 100
            `class` = 200
            `return` = 999
        }

        val decoded = msg.encodeDecode(marshallerOf<KeywordFields>())

        assertEquals(42, decoded.`for`)
        assertEquals(100, decoded.`when`)
        assertEquals(200, decoded.`class`)
        assertEquals(999, decoded.`return`)
    }

    // --- Internal name conflict fields ---

    @Test
    fun internalNameConflictsCanBeSetAndRead() {
        val msg = InternalNameConflicts {
            copy = 1
            result = 2
            tag = "test"
            value = 4
        }

        assertEquals(1, msg.copy)
        assertEquals(2, msg.result)
        assertEquals("test", msg.tag)
        assertEquals(4, msg.value)
    }

    @Test
    fun internalNameConflictsEncodeDecode() {
        val msg = InternalNameConflicts {
            copy = 10
            result = 50
            tag = "hello"
            value = 70
        }

        val decoded = msg.encodeDecode(marshallerOf<InternalNameConflicts>())

        assertEquals(10, decoded.copy)
        assertEquals(50, decoded.result)
        assertEquals("hello", decoded.tag)
        assertEquals(70, decoded.value)
    }

    @Test
    fun internalNameConflictsCopy() {
        val original = InternalNameConflicts { copy = 1; result = 2; tag = "test" }
        val copied = original.copy { copy = 99 }

        assertEquals(1, original.copy)
        assertEquals(99, copied.copy)
        assertEquals(2, copied.result)
        assertEquals("test", copied.tag)
    }

    // --- Function parameter conflicts ---

    @Test
    fun functionParamConflictsCanBeSetAndRead() {
        val msg = FunctionParamConflicts {
            msg = 1
            buffer = 2
            encoder = 3
        }

        assertEquals(1, msg.msg)
        assertEquals(2, msg.buffer)
        assertEquals(3, msg.encoder)
    }

    @Test
    fun functionParamConflictsEncodeDecode() {
        val msg = FunctionParamConflicts {
            msg = 10
            buffer = 20
            encoder = 30
        }

        val decoded = msg.encodeDecode(marshallerOf<FunctionParamConflicts>())

        assertEquals(10, decoded.msg)
        assertEquals(20, decoded.buffer)
        assertEquals(30, decoded.encoder)
    }

    @Test
    fun functionParamConflictsCopy() {
        val original = FunctionParamConflicts { msg = 1; buffer = 2; encoder = 3 }
        val copied = original.copy { msg = 99 }

        assertEquals(1, original.msg)
        assertEquals(99, copied.msg)
        assertEquals(2, copied.buffer)
        assertEquals(3, copied.encoder)
    }

    // --- Keyword fields with presence ---

    @Test
    fun keywordFieldsWithPresence() {
        val msg = KeywordFieldsWithPresence {
            `for` = 42
            `class` = "test"
        }

        val presence = msg.presence
        assertTrue(presence.hasFor)
        assertTrue(presence.hasClass)

        assertEquals(42, msg.`for`)
        assertEquals("test", msg.`class`)
    }

    @Test
    fun keywordFieldsWithPresenceEncodeDecode() {
        val msg = KeywordFieldsWithPresence {
            `for` = 42
            `when` = 100
            `class` = "hello"
            `if` = true
        }

        val decoded = msg.encodeDecode(marshallerOf<KeywordFieldsWithPresence>())

        assertEquals(42, decoded.`for`)
        assertEquals(100, decoded.`when`)
        assertEquals("hello", decoded.`class`)
        assertEquals(true, decoded.`if`)
    }

    // --- Keyword collections ---

    @Test
    fun keywordCollections() {
        val msg = KeywordCollections {
            `for` = listOf(1, 2, 3)
            `while` = mapOf("a" to 1, "b" to 2)
        }

        assertEquals(listOf(1, 2, 3), msg.`for`)
        assertEquals(mapOf("a" to 1, "b" to 2), msg.`while`)
    }

    @Test
    fun keywordCollectionsEncodeDecode() {
        val msg = KeywordCollections {
            `for` = listOf(10, 20, 30)
            `while` = mapOf("key" to 42)
        }

        val decoded = msg.encodeDecode(marshallerOf<KeywordCollections>())

        assertEquals(listOf(10, 20, 30), decoded.`for`)
        assertEquals(mapOf("key" to 42), decoded.`while`)
    }

    // --- Keyword oneof ---

    @Test
    fun keywordOneofFor() {
        val msg = KeywordOneof {
            value = KeywordOneof.Value.For(42)
        }
        assertEquals(42, (msg.value as KeywordOneof.Value.For).value)
    }

    @Test
    fun keywordOneofWhen() {
        val msg = KeywordOneof {
            value = KeywordOneof.Value.When("hello")
        }
        assertEquals("hello", (msg.value as KeywordOneof.Value.When).value)
    }

    @Test
    fun keywordOneofIf() {
        val msg = KeywordOneof {
            value = KeywordOneof.Value.If(true)
        }
        assertEquals(true, (msg.value as KeywordOneof.Value.If).value)
    }

    @Test
    fun keywordOneofEncodeDecode() {
        val msg = KeywordOneof { value = KeywordOneof.Value.For(99) }
        val decoded = msg.encodeDecode(marshallerOf<KeywordOneof>())

        assertEquals(99, (decoded.value as KeywordOneof.Value.For).value)
    }

    // --- Nested with keywords ---

    @Test
    fun nestedWithKeywords() {
        val msg = NestedWithKeywords {
            `while` = NestedWithKeywords.Inner {
                `for` = 1
                copy = 2
            }
            `return` = 42
        }

        assertEquals(1, msg.`while`.`for`)
        assertEquals(2, msg.`while`.copy)
        assertEquals(42, msg.`return`)
    }

    @Test
    fun nestedWithKeywordsEncodeDecode() {
        val msg = NestedWithKeywords {
            `while` = NestedWithKeywords.Inner {
                `for` = 10
                copy = 20
            }
            `return` = 99
        }

        val decoded = msg.encodeDecode(marshallerOf<NestedWithKeywords>())

        assertEquals(10, decoded.`while`.`for`)
        assertEquals(20, decoded.`while`.copy)
        assertEquals(99, decoded.`return`)
    }

    // --- Enum ---

    @Test
    fun keywordEnum() {
        assertEquals(0, KeywordEnum.UNSPECIFIED.number)
        assertEquals(1, KeywordEnum.FOR.number)
        assertEquals(2, KeywordEnum.WHEN.number)
        assertEquals(3, KeywordEnum.CLASS.number)
    }

    // --- Lowercase keyword enum ---

    @Test
    fun lowercaseKeywordEnum() {
        assertEquals(0, LowercaseKeywordEnum.unspecified.number)
        assertEquals(1, LowercaseKeywordEnum.`object`.number)
        assertEquals(2, LowercaseKeywordEnum.`class`.number)
        assertEquals(3, LowercaseKeywordEnum.`val`.number)
    }

    @Test
    fun lowercaseKeywordEnumEntries() {
        val entries = LowercaseKeywordEnum.entries
        assertEquals(4, entries.size)
        assertEquals(LowercaseKeywordEnum.unspecified, entries[0])
        assertEquals(LowercaseKeywordEnum.`object`, entries[1])
        assertEquals(LowercaseKeywordEnum.`class`, entries[2])
        assertEquals(LowercaseKeywordEnum.`val`, entries[3])
    }

    // --- More internal name conflicts ---

    @Test
    fun moreInternalNameConflictsCanBeSetAndRead() {
        val msg = MoreInternalNameConflicts {
            other = 1
            indent = 2
            indentString = "a"
            nextIndentString = "b"
            body = 5
            config = 6
            decoder = 7
            tag = 8
            result = 9
            elem = 10
            entry = 11
            kEntry = 12
            number = 13
            offset = 14
            internalMsg = 15
            unknownFieldsEncoder = 16
            startGroup = 17
        }

        assertEquals(1, msg.other)
        assertEquals(2, msg.indent)
        assertEquals("a", msg.indentString)
        assertEquals("b", msg.nextIndentString)
        assertEquals(5, msg.body)
        assertEquals(6, msg.config)
        assertEquals(7, msg.decoder)
        assertEquals(8, msg.tag)
        assertEquals(9, msg.result)
        assertEquals(10, msg.elem)
        assertEquals(11, msg.entry)
        assertEquals(12, msg.kEntry)
        assertEquals(13, msg.number)
        assertEquals(14, msg.offset)
        assertEquals(15, msg.internalMsg)
        assertEquals(16, msg.unknownFieldsEncoder)
        assertEquals(17, msg.startGroup)
    }

    @Test
    fun moreInternalNameConflictsEncodeDecode() {
        val msg = MoreInternalNameConflicts {
            other = 10
            indent = 20
            indentString = "hello"
            nextIndentString = "world"
            body = 50
            config = 60
            decoder = 70
            tag = 80
            result = 90
            elem = 100
            entry = 110
            kEntry = 120
            number = 130
            offset = 140
            internalMsg = 150
            unknownFieldsEncoder = 160
            startGroup = 170
        }

        val decoded = msg.encodeDecode(marshallerOf<MoreInternalNameConflicts>())

        assertEquals(10, decoded.other)
        assertEquals(20, decoded.indent)
        assertEquals("hello", decoded.indentString)
        assertEquals("world", decoded.nextIndentString)
        assertEquals(50, decoded.body)
        assertEquals(60, decoded.config)
        assertEquals(70, decoded.decoder)
        assertEquals(80, decoded.tag)
        assertEquals(90, decoded.result)
        assertEquals(100, decoded.elem)
        assertEquals(110, decoded.entry)
        assertEquals(120, decoded.kEntry)
        assertEquals(130, decoded.number)
        assertEquals(140, decoded.offset)
        assertEquals(150, decoded.internalMsg)
        assertEquals(160, decoded.unknownFieldsEncoder)
        assertEquals(170, decoded.startGroup)
    }

    @Test
    fun moreInternalNameConflictsCopy() {
        val original = MoreInternalNameConflicts {
            other = 1; body = 2; config = 3; decoder = 4
        }
        val copied = original.copy { other = 99 }

        assertEquals(1, original.other)
        assertEquals(99, copied.other)
        assertEquals(2, copied.body)
        assertEquals(3, copied.config)
        assertEquals(4, copied.decoder)
    }

    @Test
    fun moreInternalNameConflictsEquality() {
        val msg1 = MoreInternalNameConflicts { other = 1; indent = 2 }
        val msg2 = MoreInternalNameConflicts { other = 1; indent = 2 }
        val msg3 = MoreInternalNameConflicts { other = 99; indent = 2 }

        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())
        assertNotEquals(msg1, msg3)
    }
}
