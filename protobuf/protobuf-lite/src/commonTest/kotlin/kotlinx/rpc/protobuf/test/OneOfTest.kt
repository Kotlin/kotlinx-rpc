/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import OneOfMsg
import OneOfMsgFieldCase
import OneOfNumeric32
import OneOfNumeric32NumCase
import OneOfNumeric64
import OneOfNumeric64NumCase
import OneOfRefs
import OneOfRefsRefCase
import clearField
import clearNum
import clearRef
import copy
import field
import fixedOrNull
import invoke
import kotlinx.io.Buffer
import kotlinx.io.bytestring.ByteString
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.internal.WireEncoder
import num
import presence
import ref
import sintOrNull
import test.submsg.Other
import test.submsg.invoke
import whenField
import whenNum
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OneOfTest {
    private fun <M> encodeDecode(msg: M, marshaller: GrpcMarshaller<M>): M {
        return marshaller.decode(marshaller.encode(msg))
    }

    @Test
    fun setSelectsCaseAndOtherMembersReturnDefaults() {
        val msg = OneOfMsg { sint = 5 }

        assertEquals(OneOfMsgFieldCase.SINT, msg.field)
        assertTrue(msg.presence.hasSint)
        assertFalse(msg.presence.hasFixed)
        assertFalse(msg.presence.hasOther)
        assertFalse(msg.presence.hasEnum)
        assertFalse(msg.presence.hasBytes)

        assertEquals(5, msg.sint)
        assertEquals(0u, msg.fixed)
        assertEquals("", msg.other.arg1)
        assertEquals(MyEnum.ZERO, msg.enum)
        assertEquals(ByteString(), msg.bytes)
    }

    @Test
    fun lastSetMemberWins() {
        val msg = OneOfMsg {
            sint = 5
            fixed = 7u
        }

        assertEquals(OneOfMsgFieldCase.FIXED, msg.field)
        assertFalse(msg.presence.hasSint)
        assertEquals(0, msg.sint)
        assertEquals(7u, msg.fixed)
    }

    @Test
    fun settingTheDefaultValueStillSelectsTheCase() {
        val msg = OneOfMsg { sint = 0 }
        assertEquals(OneOfMsgFieldCase.SINT, msg.field)
        assertTrue(msg.presence.hasSint)

        // the zero is written to the wire and survives the round trip
        val encoded = grpcMarshallerOf<OneOfMsg>().encode(msg)
        assertFalse(encoded.exhausted())
        val decoded = grpcMarshallerOf<OneOfMsg>().decode(encoded)
        assertEquals(OneOfMsgFieldCase.SINT, decoded.field)
        assertEquals(0, decoded.sint)

        val empty = OneOfRefs { str = "" }
        assertEquals(OneOfRefsRefCase.STR, empty.ref)
        assertEquals(OneOfRefsRefCase.STR, encodeDecode(empty, grpcMarshallerOf<OneOfRefs>()).ref)
    }

    @Test
    fun clearMemberOnlyClearsTheActiveCase() {
        val cleared = OneOfMsg {
            sint = 5
            clearSint()
        }
        assertEquals(OneOfMsgFieldCase.NOT_SET, cleared.field)
        assertEquals(0, cleared.sint)

        val untouched = OneOfMsg {
            sint = 5
            clearFixed()
        }
        assertEquals(OneOfMsgFieldCase.SINT, untouched.field)
        assertEquals(5, untouched.sint)
    }

    @Test
    fun clearOneOfClearsAnyCase() {
        val msg = OneOfMsg {
            other = Other { arg1 = "x" }
            clearField()
        }
        assertEquals(OneOfMsgFieldCase.NOT_SET, msg.field)
        assertFalse(msg.presence.hasOther)
        assertEquals("", msg.other.arg1)

        val viaCopy = OneOfMsg { bytes = ByteString(1, 2) }.copy { clearField() }
        assertEquals(OneOfMsgFieldCase.NOT_SET, viaCopy.field)
    }

    @Test
    fun copySwitchesCase() {
        val original = OneOfMsg { sint = 3 }
        val copy = original.copy { enum = MyEnum.TWO }

        assertEquals(OneOfMsgFieldCase.SINT, original.field)
        assertEquals(OneOfMsgFieldCase.ENUM, copy.field)
        assertEquals(MyEnum.TWO, copy.enum)
        assertEquals(0, copy.sint)
    }

    @Test
    fun orNullGetters() {
        val msg = OneOfMsg { sint = 5 }
        assertEquals(5, msg.sintOrNull)
        assertNull(msg.fixedOrNull)
    }

    @Test
    fun whenFunctionDispatchesOnTheActiveCase() {
        fun OneOfMsg.describe(): String = whenField(
            sint = { "sint=$it" },
            fixed = { "fixed=$it" },
            other = { "other=${it.arg1}" },
            enum = { "enum=${it.number}" },
            bytes = { "bytes=${it.size}" },
            notSet = { "not set" },
        )

        assertEquals("sint=1", OneOfMsg { sint = 1 }.describe())
        assertEquals("fixed=2", OneOfMsg { fixed = 2u }.describe())
        assertEquals("other=x", OneOfMsg { other = Other { arg1 = "x" } }.describe())
        assertEquals("enum=3", OneOfMsg { enum = MyEnum.THREE }.describe())
        assertEquals("bytes=2", OneOfMsg { bytes = ByteString(1, 2) }.describe())
        assertEquals("not set", OneOfMsg { }.describe())
    }

    @Test
    fun whenOverTheCaseIsExhaustive() {
        val msg = OneOfMsg { fixed = 9u }
        val result = when (msg.field) {
            OneOfMsgFieldCase.SINT -> msg.sint.toLong()
            OneOfMsgFieldCase.FIXED -> msg.fixed.toLong()
            OneOfMsgFieldCase.OTHER -> -1L
            OneOfMsgFieldCase.ENUM -> msg.enum.number.toLong()
            OneOfMsgFieldCase.BYTES -> msg.bytes.size.toLong()
            OneOfMsgFieldCase.NOT_SET -> 0L
        }
        assertEquals(9L, result)
        assertEquals(
            listOf("SINT", "FIXED", "OTHER", "ENUM", "BYTES", "NOT_SET"),
            OneOfMsgFieldCase.entries.map { it.name },
        )
    }

    @Test
    fun numeric32MembersRoundTrip() {
        val marshaller = grpcMarshallerOf<OneOfNumeric32>()

        fun check(msg: OneOfNumeric32, case: OneOfNumeric32NumCase, read: (OneOfNumeric32) -> Any) {
            assertEquals(case, msg.num)
            val decoded = encodeDecode(msg, marshaller)
            assertEquals(case, decoded.num)
            assertEquals(read(msg), read(decoded))
            assertEquals(msg, decoded)
            assertEquals(msg.hashCode(), decoded.hashCode())
        }

        check(OneOfNumeric32 { i32 = Int.MIN_VALUE }, OneOfNumeric32NumCase.I32) { it.i32 }
        check(OneOfNumeric32 { u32 = UInt.MAX_VALUE }, OneOfNumeric32NumCase.U32) { it.u32 }
        check(OneOfNumeric32 { f = -1.5f }, OneOfNumeric32NumCase.F) { it.f }
        check(OneOfNumeric32 { b = true }, OneOfNumeric32NumCase.B) { it.b }
        check(OneOfNumeric32 { s32 = -77 }, OneOfNumeric32NumCase.S32) { it.s32 }
        check(OneOfNumeric32 { fx32 = 0xFFFF_FFFFu }, OneOfNumeric32NumCase.FX32) { it.fx32 }
        check(OneOfNumeric32 { sfx32 = -1 }, OneOfNumeric32NumCase.SFX32) { it.sfx32 }
        check(OneOfNumeric32 { enum = MyEnum.THREE }, OneOfNumeric32NumCase.ENUM) { it.enum }

        assertEquals(UInt.MAX_VALUE, OneOfNumeric32 { u32 = UInt.MAX_VALUE }.u32)
        assertTrue(OneOfNumeric32 { b = true }.b)
        assertFalse(OneOfNumeric32 { b = true }.copy { i32 = 1 }.b)
    }

    @Test
    fun numeric64MembersRoundTrip() {
        val marshaller = grpcMarshallerOf<OneOfNumeric64>()

        fun check(msg: OneOfNumeric64, case: OneOfNumeric64NumCase, read: (OneOfNumeric64) -> Any) {
            assertEquals(case, msg.num)
            val decoded = encodeDecode(msg, marshaller)
            assertEquals(case, decoded.num)
            assertEquals(read(msg), read(decoded))
            assertEquals(msg, decoded)
        }

        check(OneOfNumeric64 { i64 = Long.MIN_VALUE }, OneOfNumeric64NumCase.I64) { it.i64 }
        check(OneOfNumeric64 { u64 = ULong.MAX_VALUE }, OneOfNumeric64NumCase.U64) { it.u64 }
        check(OneOfNumeric64 { d = -2.25 }, OneOfNumeric64NumCase.D) { it.d }
        check(OneOfNumeric64 { f = 3.5f }, OneOfNumeric64NumCase.F) { it.f }
        check(OneOfNumeric64 { s64 = -99L }, OneOfNumeric64NumCase.S64) { it.s64 }
        check(OneOfNumeric64 { fx64 = ULong.MAX_VALUE - 1u }, OneOfNumeric64NumCase.FX64) { it.fx64 }
        check(OneOfNumeric64 { sfx64 = -5L }, OneOfNumeric64NumCase.SFX64) { it.sfx64 }
        check(OneOfNumeric64 { u32 = UInt.MAX_VALUE }, OneOfNumeric64NumCase.U32) { it.u32 }
        check(OneOfNumeric64 { b = true }, OneOfNumeric64NumCase.B) { it.b }

        // a 32-bit unsigned value stored in the 64-bit slot keeps its value
        assertEquals(UInt.MAX_VALUE, OneOfNumeric64 { u32 = UInt.MAX_VALUE }.u32)
        assertEquals(-1.0f, OneOfNumeric64 { f = -1.0f }.f)
    }

    @Test
    fun nanAndNegativeZero() {
        val nan1 = OneOfNumeric64 { d = Double.NaN }
        val nan2 = OneOfNumeric64 { d = Double.NaN }
        assertEquals(nan1, nan2)
        assertEquals(nan1.hashCode(), nan2.hashCode())
        assertTrue(encodeDecode(nan1, grpcMarshallerOf<OneOfNumeric64>()).d.isNaN())

        val fNan1 = OneOfNumeric32 { f = Float.NaN }
        val fNan2 = OneOfNumeric32 { f = Float.NaN }
        assertEquals(fNan1, fNan2)
        assertTrue(encodeDecode(fNan1, grpcMarshallerOf<OneOfNumeric32>()).f.isNaN())

        val zero = OneOfNumeric64 { d = 0.0 }
        val negativeZero = OneOfNumeric64 { d = -0.0 }
        assertNotEquals(zero, negativeZero)
        assertEquals(1.0 / Double.NEGATIVE_INFINITY, encodeDecode(negativeZero, grpcMarshallerOf<OneOfNumeric64>()).d)

        val fZero = OneOfNumeric32 { f = 0.0f }
        val fNegativeZero = OneOfNumeric32 { f = -0.0f }
        assertNotEquals(fZero, fNegativeZero)
        assertEquals((-0.0f).toRawBits(), encodeDecode(fNegativeZero, grpcMarshallerOf<OneOfNumeric32>()).f.toRawBits())
    }

    @Test
    fun unknownEnumNumberRoundTrips() {
        val msg = OneOfNumeric32 { enum = MyEnum.UNRECOGNIZED(42) }
        assertEquals(MyEnum.UNRECOGNIZED(42), msg.enum)

        val decoded = encodeDecode(msg, grpcMarshallerOf<OneOfNumeric32>())
        assertEquals(OneOfNumeric32NumCase.ENUM, decoded.num)
        assertEquals(MyEnum.UNRECOGNIZED(42), decoded.enum)
        assertEquals(msg, decoded)

        // decoding an unknown number directly from the wire
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeEnum(8, 1234)
        encoder.flush()
        val fromWire = grpcMarshallerOf<OneOfNumeric32>().decode(buffer)
        assertEquals(MyEnum.UNRECOGNIZED(1234), fromWire.enum)
        val reEncoded = encodeDecode(fromWire, grpcMarshallerOf<OneOfNumeric32>())
        assertEquals(1234, reEncoded.enum.number)
    }

    @Test
    fun sameValueInDifferentCasesIsNotEqual() {
        val i32 = OneOfNumeric32 { i32 = 1 }
        val s32 = OneOfNumeric32 { s32 = 1 }
        assertNotEquals(i32, s32)
        assertNotEquals(i32.hashCode(), s32.hashCode())

        val emptyStr = OneOfRefs { str = "" }
        val emptyBytes = OneOfRefs { bytes = ByteString() }
        assertNotEquals(emptyStr, emptyBytes)
        assertNotEquals(OneOfRefs { }, emptyStr)
        assertEquals(OneOfRefs { str = "a" }, OneOfRefs { str = "a" })
    }

    @Test
    fun referenceMembersRoundTrip() {
        val marshaller = grpcMarshallerOf<OneOfRefs>()

        val withString = OneOfRefs { str = "hello" }
        assertEquals(OneOfRefsRefCase.STR, withString.ref)
        assertEquals("hello", encodeDecode(withString, marshaller).str)

        val withBytes = OneOfRefs { bytes = ByteString(1, 2, 3) }
        assertEquals(OneOfRefsRefCase.BYTES, withBytes.ref)
        assertEquals(ByteString(1, 2, 3), encodeDecode(withBytes, marshaller).bytes)
        assertEquals("", withBytes.str)

        val withMessage = OneOfRefs { other = Other { arg3 = "z" } }
        assertEquals(OneOfRefsRefCase.OTHER, withMessage.ref)
        assertEquals("z", encodeDecode(withMessage, marshaller).other.arg3)
        assertEquals(ByteString(), withMessage.bytes)

        val cleared = withMessage.copy { clearRef() }
        assertEquals(OneOfRefsRefCase.NOT_SET, cleared.ref)
        assertEquals("", cleared.other.arg3)
    }

    @Test
    fun numericOneOfWhenFunctionDoesNotBox() {
        val msg = OneOfNumeric64 { i64 = 40L }
        val result = msg.whenNum(
            i64 = { it + 2 },
            u64 = { it.toLong() },
            d = { it.toLong() },
            f = { it.toLong() },
            s64 = { it },
            fx64 = { it.toLong() },
            sfx64 = { it },
            u32 = { it.toLong() },
            b = { if (it) 1L else 0L },
            notSet = { -1L },
        )
        assertEquals(42L, result)

        val cleared = msg.copy { clearNum() }
        assertEquals(OneOfNumeric64NumCase.NOT_SET, cleared.num)
    }

    @Test
    fun toStringPrintsOnlyTheActiveMember() {
        assertEquals(
            """
            OneOfRefs(
                str=hi,
            )
            """.trimIndent(),
            OneOfRefs { str = "hi" }.toString(),
        )
        assertEquals(
            """
            OneOfRefs(
            )
            """.trimIndent(),
            OneOfRefs { }.toString(),
        )
        assertEquals(
            """
            OneOfNumeric32(
                b=true,
            )
            """.trimIndent(),
            OneOfNumeric32 { b = true }.toString(),
        )
    }
}
