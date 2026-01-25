/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import WithDefaults
import invoke
import presence
import test.nested.NestedOuter
import test.nested.NestedOuterInternal
import test.nested.invoke
import test.nested.presence
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PresenceCheckTest {

    @Test
    fun `test optional fields proto2`() {
        WithDefaults.codec

        val check = { message: WithDefaults ->
            assertTrue(message.presence.hasDouble)
            assertFalse(message.presence.hasFloat)
            assertFalse(message.presence.hasBool)
            assertFalse(message.presence.hasString)
            assertFalse(message.presence.hasEnum1)
            assertFalse(message.presence.hasEnum2)
        }

        val message = WithDefaults {
            double = 0.2
        }
        check(message)

        val decoded = message.encodeDecode(WithDefaultsInternal.CODEC)
        check(decoded)
    }

    @Test
    fun `test nested messages proto2`() {
        val check = { message: NestedOuter ->
            assertFalse(message.presence.hasInner)
            assertFalse(message.presence.hasDeep)
            assertFalse(message.presence.hasNotinside)
            assertFalse(message.inner.presence.hasInnerSubmsg)
            assertFalse(message.inner.innerSubmsg.presence.hasFlag)
            assertFalse(message.deep.presence.hasNum)
        }
        val message = NestedOuter {}
        check(message)
        val decoded = message.encodeDecode(NestedOuterInternal.CODEC)
        check(decoded)
    }

}