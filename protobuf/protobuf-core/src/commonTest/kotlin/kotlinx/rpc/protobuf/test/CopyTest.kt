/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import Equals
import OneOfMsg
import copy
import invoke
import test.submsg.Other
import test.submsg.invoke
import kotlin.collections.iterator
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CopyTest {

    @Test
    fun `copy primitives and bytes - equality and independence`() {
        val bytesSrc = byteArrayOf(1, 2, 3)
        val msg = AllPrimitives {
            int32 = 42
            int64 = 123L
            uint32 = 7u
            uint64 = 9uL
            sint32 = -5
            sint64 = -7L
            fixed32 = 11u
            fixed64 = 13uL
            sfixed32 = -17
            sfixed64 = -19L
            bool = true
            float = 2.5f
            double = 3.5
            string = "hello"
            bytes = bytesSrc
        }

        val copy = msg.copy()

        // verify all fields are copied
        assertEquals(msg, copy)

        // bytes are copied
        assertEquals(byteArrayOf(1, 2, 3).toList(), msg.bytes?.toList())
        assertEquals(byteArrayOf(1, 2, 3).toList(), copy.bytes?.toList())
    }

    @Test
    fun `copy repeated messages - deep copy of list and elements`() {
        val orig = Repeated {
            listMessage = listOf(
                Repeated.Other { a = 1 },
                Repeated.Other { a = 2 }
            )
        }
        val cp = orig.copy()

        assertEquals(orig, cp)

        // Lists are distinct instances
        assertTrue(orig.listMessage !== cp.listMessage)
        // Elements are deep-copied
        for (i in orig.listMessage.indices) {
            assertEquals(orig.listMessage[i].a, cp.listMessage[i].a)
            assertTrue(orig.listMessage[i] !== cp.listMessage[i])
        }

        // Modify copy via copy { } and ensure original unaffected
        val mutated = orig.copy {
            listMessage = listMessage + Repeated.Other { a = 3 }
        }
        assertEquals(listOf(1, 2), orig.listMessage.map { it.a })
        assertEquals(listOf(1, 2, 3), mutated.listMessage.map { it.a })
    }

    @Test
    fun `copy maps - deep copy keys and values`() {
        val original = TestMap {
            primitives = mapOf("a" to 10L, "b" to 20L)
            messages = mapOf(1 to PresenceCheck { RequiredPresence = 1 }, 2 to PresenceCheck { RequiredPresence = 2 })
        }

        val copy = original.copy()

        // verify maps are copied
        assertEquals(mapOf("a" to 10L, "b" to 20L), original.primitives)
        assertEquals(mapOf("a" to 10L, "b" to 20L), copy.primitives)

        // deep copy for message map values
        for ((k, v) in original.messages) {
            val cv = copy.messages.getValue(k)
            assertEquals(v.RequiredPresence, cv.RequiredPresence)
            assertTrue(v !== cv)
        }

        // copy { } change and ensure isolation
        val mutated = original.copy { primitives = primitives + ("z" to 90L) }
        assertEquals(setOf("a","b"), original.primitives.keys)
        assertEquals(setOf("a","b","z"), mutated.primitives.keys)
    }

    @Test
    fun `copy oneof - preserve active case and allow mutation in lambda`() {
        val o1 = OneOfMsg.Companion { field = OneOfMsg.Field.Sint(7) }
        val c1 = o1.copy()
        assertEquals(o1.field, c1.field)

        // mutate with copy-lambda (switch case)
        val c2 = o1.copy { field = OneOfMsg.Field.Other(Other.Companion { arg1 = "x" }) }
        // original unaffected
        assertEquals(OneOfMsg.Field.Sint(7), o1.field)
        // new case set
        assertTrue(c2.field is OneOfMsg.Field.Other)
    }

    @Test
    fun `copy nested sub-message - deep copy and lambda modification`() {
        val equals = Equals.Companion {
            str1 = "root"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum2 = Equals.SomeEnum.VALUE1
            nested = Equals.Nested { content = "leaf" }
        }
        val copy = equals.copy()
        // deep copy of nested
        assertTrue(equals.nested !== copy.nested)
        assertEquals(equals.nested.content, copy.nested.content)

        // modify nested in copy-lambda
        val mutated = equals.copy {
            nested = Equals.Nested { content = "mut" }
        }
        assertEquals("leaf", equals.nested.content)
        assertEquals("mut", mutated.nested.content)
    }

    @Test
    fun `copy preserves presence and required fields`() {
        val p = PresenceCheck { RequiredPresence = 1 }
        val cp = p.copy()
        assertEquals(1, cp.RequiredPresence)
        assertNull(cp.OptionalPresence)

        val cp2 = p.copy { OptionalPresence = 5f }
        assertEquals(1, cp2.RequiredPresence)
        assertEquals(5f, cp2.OptionalPresence)
    }

    @Test
    fun `copy with user-provided mutable map - mutation after copy should not affect copy`() {
        val userMap = mutableMapOf("a" to 10L, "b" to 20L)
        val original = TestMap {
            primitives = userMap
        }

        val copy = original.copy()

        // Mutate user's map after copy - should not affect copy
        userMap["c"] = 30L
        userMap["a"] = 999L

        assertEquals(mapOf("a" to 10L, "b" to 20L, "c" to 30L, "a" to 999L), original.primitives)
        assertEquals(mapOf("a" to 10L, "b" to 20L), copy.primitives)
    }

    @Test
    fun `copy with user-provided bytes - mutation after copy should not affect copy`() {
        val userBytes = byteArrayOf(1, 2, 3)
        val original = AllPrimitives {
            bytes = userBytes
        }
        val copy = original.copy()
        userBytes[0] = 99
        assertEquals(listOf<Byte>(99, 2, 3), original.bytes?.toList())
        assertEquals(listOf<Byte>(1, 2, 3), copy.bytes?.toList())
    }

    @Test
    fun `copy with nested messages - user mutation after copy should not affect copy`() {
        val userMessages = mutableMapOf(
            1 to PresenceCheck { RequiredPresence = 1 },
            2 to PresenceCheck { RequiredPresence = 2 }
        )
        val original = TestMap {
            messages = userMessages
        }

        val copy = original.copy()

        // Mutate user's map after copy
        userMessages[3] = PresenceCheck { RequiredPresence = 3 }

        // Original has all 3, copy should only have original 2
        assertEquals(3, original.messages.size)
        assertEquals(2, copy.messages.size)
        assertEquals(setOf(1, 2, 3), original.messages.keys)
        assertEquals(setOf(1, 2), copy.messages.keys)
    }

    @Test
    fun `copy with bytes - mutating source array after construction should not affect copy`() {
        val bytesSrc = byteArrayOf(1, 2, 3)
        val msg = AllPrimitives {
            bytes = bytesSrc
        }

        val copy = msg.copy()

        // Mutate source array after message creation
        bytesSrc[0] = 99
        bytesSrc[1] = 88

        // Neither original nor copy should be affected
        assertEquals(listOf<Byte>(99, 88, 3), msg.bytes?.toList())
        assertEquals(listOf<Byte>(1, 2, 3), copy.bytes?.toList())
    }

    @Test
    fun `copy with bytes in oneof - mutating must not affect copy`() {
        val userBytes = byteArrayOf(1, 2, 3)
        val original = OneOfMsg {
            field = OneOfMsg.Field.Bytes(userBytes)
        }
        val copy = original.copy()
        userBytes[0] = 99

        assertContentEquals(byteArrayOf(99, 2, 3), (original.field as OneOfMsg.Field.Bytes).value)
        assertContentEquals(byteArrayOf(1, 2, 3), (copy.field as OneOfMsg.Field.Bytes).value)
    }

    @Test
    fun `copy with empty collections`() {
        val msg = Repeated {
            listInt32 = emptyList()
            listMessage = emptyList()
        }

        val copy = msg.copy()

        assertTrue(copy.listInt32.isEmpty())
        assertTrue(copy.listMessage.isEmpty())

        // Verify they are distinct instances
        assertTrue(msg.listInt32 !== copy.listInt32)
        assertTrue(msg.listMessage !== copy.listMessage)
    }

    @Test
    fun `copy with nested message in list - mutations after copy don't affect copy`() {
        val nestedList = mutableListOf(
            Repeated.Other { a = 1 },
            Repeated.Other { a = 2 }
        )
        val original = Repeated {
            listMessage = nestedList
        }

        val copy = original.copy()

        // Mutate the user's list after copy
        nestedList.add(Repeated.Other { a = 3 })

        assertEquals(3, original.listMessage.size)
        assertEquals(2, copy.listMessage.size)
        assertEquals(listOf(1, 2, 3), original.listMessage.map { it.a })
        assertEquals(listOf(1, 2), copy.listMessage.map { it.a })
    }

    @Test
    fun `copy lambda modifications don't affect original`() {
        val original = AllPrimitives {
            int32 = 10
            string = "original"
        }

        val modified = original.copy {
            int32 = 20
            string = "modified"
        }

        // Original should remain unchanged
        assertEquals(10, original.int32)
        assertEquals("original", original.string)

        // Modified should have new values
        assertEquals(20, modified.int32)
        assertEquals("modified", modified.string)
    }
}