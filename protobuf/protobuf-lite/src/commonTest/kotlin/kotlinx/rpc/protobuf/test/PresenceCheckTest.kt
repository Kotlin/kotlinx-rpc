/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import WithDefaults
import doubleOrNull
import enum1OrNull
import enum2OrNull
import floatOrNull
import invoke
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import presence
import stringOrNull
import test.nested.NestedOuter
import test.nested.deepOrNull
import test.nested.invoke
import test.nested.innerOrNull
import test.nested.notinsideOrNull
import test.nested.presence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PresenceCheckTest {

    @Test
    fun `test optional fields proto2`() {
        val check = { message: WithDefaults ->
            assertTrue(message.presence.hasDouble)
            assertFalse(message.presence.hasFloat)
            assertFalse(message.presence.hasBool)
            assertFalse(message.presence.hasString)
            assertFalse(message.presence.hasEnum1)
            assertFalse(message.presence.hasEnum2)
            assertEquals(0.2, message.doubleOrNull)
            assertNull(message.floatOrNull)
            assertNull(message.stringOrNull)
            assertNull(message.enum1OrNull)
            assertNull(message.enum2OrNull)
        }

        val message = WithDefaults {
            double = 0.2
        }
        check(message)

        val decoded = message.encodeDecode(grpcMarshallerOf<WithDefaults>())
        check(decoded)
    }

    @Test
    fun `test nested messages proto2`() {
        val check = { message: NestedOuter ->
            assertFalse(message.presence.hasInner)
            assertFalse(message.presence.hasDeep)
            assertFalse(message.presence.hasNotinside)
            assertNull(message.innerOrNull)
            assertNull(message.deepOrNull)
            assertNull(message.notinsideOrNull)
            assertFalse(message.inner.presence.hasInnerSubmsg)
            assertFalse(message.inner.innerSubmsg.presence.hasFlag)
            assertFalse(message.deep.presence.hasNum)
        }
        val message = NestedOuter {}
        check(message)
        val decoded = message.encodeDecode(grpcMarshallerOf<NestedOuter>())
        check(decoded)
    }

}
