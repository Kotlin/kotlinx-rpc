/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import invoke
import kotlinx.rpc.protobuf.test.invoke
import test.submsg.Other
import test.submsg.invoke
import kotlin.collections.iterator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CopyTest {

    @Test
    fun `test list copy - should be real copy`() {
        val userList = mutableListOf(1,2,3)
        val original = Repeated {
            listInt32 = userList
        }
        val copy = original.copy()
        userList.add(4)

        assertEquals(listOf(1,2,3,4), original.listInt32)
        assertEquals(listOf(1,2,3), copy.listInt32)
    }

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
}