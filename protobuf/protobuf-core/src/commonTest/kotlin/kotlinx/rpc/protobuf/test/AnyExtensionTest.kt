/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import Outer
import com.google.protobuf.kotlin.pack
import com.google.protobuf.kotlin.unpack
import com.google.protobuf.kotlin.Any
import com.google.protobuf.kotlin.Duration
import com.google.protobuf.kotlin.Empty
import com.google.protobuf.kotlin.Timestamp
import com.google.protobuf.kotlin.invoke
import com.google.protobuf.kotlin.contains
import invoke
import test.nested.NestedOuter
import test.nested.invoke
import test.recursive.Recursive
import test.recursive.invoke
import test.submsg.Other
import test.submsg.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AnyExtensionTest {

    @Test
    fun testPackAndUnpackPrimitiveMessage() {
        val original = AllPrimitives {
            int32 = 42
            string = "test"
            bool = true
            double = 3.14
        }

        val packed = Any.pack(original)

        assertTrue(packed.typeUrl.endsWith("/kotlinx.rpc.protobuf.test.AllPrimitives"))
        assertTrue(packed.value.isNotEmpty())

        val unpacked = packed.unpack<AllPrimitives>()

        assertEquals(original.int32, unpacked.int32)
        assertEquals(original.string, unpacked.string)
        assertEquals(original.bool, unpacked.bool)
        assertEquals(original.double, unpacked.double)
    }

    @Test
    fun testPackWithCustomUrlPrefix() {
        val original = AllPrimitives {
            int32 = 123
        }

        val packed = Any.pack(original, urlPrefix = "example.com/custom")

        assertTrue(packed.typeUrl.startsWith("example.com/custom/"))
        assertTrue(packed.typeUrl.endsWith("/kotlinx.rpc.protobuf.test.AllPrimitives"))
    }

    @Test
    fun testPackAndUnpackNestedMessage() {
        val original = Outer {
            inner = Outer.Inner {
                field = 999
            }
        }

        val packed = Any.pack(original)
        val unpacked = packed.unpack<Outer>()

        assertEquals(original.inner.field, unpacked.inner.field)
    }

    @Test
    fun testPackAndUnpackRecursiveMessage() {
        val original = Recursive {
            num = 42
            rec = Recursive {
                num = 24
                rec = Recursive {
                    num = 7
                }
            }
        }

        val packed = Any.pack(original)
        val unpacked = packed.unpack<Recursive>()

        assertEquals(42, unpacked.num)
        assertEquals(24, unpacked.rec.num)
        assertEquals(7, unpacked.rec.rec.num)
    }

    @Test
    fun testPackAndUnpackDeeplyNestedMessage() {
        val original = NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner {
            num = 123456
        }

        val packed = Any.pack(original)
        val unpacked = packed.unpack<NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner>()

        assertEquals(original.num, unpacked.num)
    }

    @Test
    fun testPackAndUnpackWellKnownTypes() {
        // Test Timestamp
        val timestamp = Timestamp {
            seconds = 1234567890
            nanos = 123456789
        }
        val packedTimestamp = Any.pack(timestamp)
        val unpackedTimestamp = packedTimestamp.unpack<Timestamp>()
        assertEquals(timestamp.seconds, unpackedTimestamp.seconds)
        assertEquals(timestamp.nanos, unpackedTimestamp.nanos)

        // Test Duration
        val duration = Duration {
            seconds = 3600
            nanos = 500000000
        }
        val packedDuration = Any.pack(duration)
        val unpackedDuration = packedDuration.unpack<Duration>()
        assertEquals(duration.seconds, unpackedDuration.seconds)
        assertEquals(duration.nanos, unpackedDuration.nanos)

        // Test Empty
        val empty = Empty {}
        val packedEmpty = Any.pack(empty)
        val unpackedEmpty = packedEmpty.unpack<Empty>()
        assertEquals(empty, unpackedEmpty)
    }

    @Test
    fun testIsAWithDifferentMessages() {
        val primitives = Any.pack(AllPrimitives { int32 = 1 })
        val other = Any.pack(Other { arg1 = "test" })
        val outer = Any.pack(Outer { inner = Outer.Inner { field = 1 } })

        assertTrue(primitives.contains<AllPrimitives>())
        assertFalse(primitives.contains<Other>())
        assertFalse(primitives.contains<Outer>())

        assertTrue(other.contains<Other>())
        assertFalse(other.contains<AllPrimitives>())
        assertFalse(other.contains<Outer>())

        assertTrue(outer.contains<Outer>())
        assertFalse(outer.contains<AllPrimitives>())
        assertFalse(outer.contains<Other>())
    }

    @Test
    fun testUnpackWrongTypeFails() {
        val original = AllPrimitives {
            int32 = 42
        }

        val packed = Any.pack(original)

        assertFailsWith<IllegalArgumentException> {
            packed.unpack<Timestamp>()
        }
    }

    @Test
    fun testPackAndUnpackMessageWithNestedClass() {
        // Test message containing a nested class type (Outer contains Outer.Inner)
        val original = NestedOuter {
            deep = NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner {
                num = 987654
            }
            deepEnum = NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.JUST_WAY_TOO_INNER_UNSPECIFIED
        }

        val packed = Any.pack(original)
        assertTrue(packed.contains<NestedOuter>())

        val unpacked = packed.unpack<NestedOuter>()
        assertEquals(987654, unpacked.deep.num)
        assertEquals(
            NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.JUST_WAY_TOO_INNER_UNSPECIFIED,
            unpacked.deepEnum
        )

        val innerPacked = Any.pack(unpacked.deep)
        val innerUnpacked = innerPacked.unpack<NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner>()
        assertEquals(987654, innerUnpacked.num)
        assertEquals("type.googleapis.com/test.nested.NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner", innerPacked.typeUrl)
    }
}