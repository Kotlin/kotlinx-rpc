/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import OneOfMsg
import OneOfMsgInternal
import Outer
import OuterInternal
import invoke
import kotlinx.io.Buffer
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.test.MyEnum
import kotlinx.rpc.grpc.test.UsingEnum
import kotlinx.rpc.grpc.test.UsingEnumInternal
import kotlinx.rpc.grpc.test.common.*
import kotlinx.rpc.grpc.test.invoke
import test.nested.*
import test.recursive.Recursive
import test.recursive.RecursiveInternal
import test.recursive.RecursiveReq
import test.recursive.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ProtosTest {

    private fun <M> encodeDecode(
        msg: M,
        codec: MessageCodec<M>
    ): M {
        val source = codec.encode(msg)
        return codec.decode(source)
    }

    @Test
    fun testAllPrimitiveProto() {
        val msg = AllPrimitives {
            int32 = 12
            int64 = 1234567890123456789L
            uint32 = 12345u
            uint64 = 1234567890123456789uL
            sint32 = -12
            sint64 = -1234567890123456789L
            fixed32 = 12345u
            fixed64 = 1234567890123456789uL
            sfixed32 = -12345
            sfixed64 = -1234567890123456789L
            bool = true
            float = 1.0f
            double = 3.0
            string = "test"
            bytes = byteArrayOf(1, 2, 3)
        }

        val msgObj = msg

        val decoded = encodeDecode(msgObj, AllPrimitivesInternal.CODEC)

        assertEquals(msg.double, decoded.double)
    }

    @Test
    fun testRepeatedProto() {
        val msg = Repeated {
            listFixed32 = listOf(1, 5, 3).map { it.toUInt() }
            listFixed32Packed = listOf(1, 2, 3).map { it.toUInt() }
            listInt32 = listOf(4, 7, 6)
            listInt32Packed = listOf(4, 5, 6)
            listString = listOf("a", "b", "c")
        }

        val decoded = encodeDecode(msg, RepeatedInternal.CODEC)

        assertEquals(msg.listInt32, decoded.listInt32)
        assertEquals(msg.listFixed32, decoded.listFixed32)
        assertEquals(msg.listString, decoded.listString)
    }

    @Test
    fun testPresenceCheckProto() {

        // Check a missing required field in a user-constructed message
        assertFailsWith<IllegalStateException>("PresenceCheck is missing required field: RequiredPresence") {
            PresenceCheck {}
        }

        // Test missing field during decoding of an encoded message
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeFloat(2, 1f)
        encoder.flush()

        assertFailsWith<IllegalStateException>("PresenceCheck is missing required field: RequiredPresence") {
            PresenceCheckInternal.CODEC.decode(buffer)
        }
    }

    @Test
    fun testEnumUnrecognized() {
        // write unknown enum value
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeEnum(1, 50)
        encoder.flush()

        val decodedMsg = UsingEnumInternal.CODEC.decode(buffer)
        assertEquals(MyEnum.UNRECOGNIZED(50), decodedMsg.enum)
    }

    @Test
    fun testEnumAlias() {
        val msg = UsingEnum {
            enum = MyEnum.ONE_SECOND
        }

        val decodedMsg = encodeDecode(msg, UsingEnumInternal.CODEC)
        assertEquals(MyEnum.ONE, decodedMsg.enum)
        assertEquals(MyEnum.ONE_SECOND, decodedMsg.enum)
    }

    @Test
    fun testDefault() {
        // create message without enum field set
        val msg = UsingEnum {}

        val buffer = UsingEnumInternal.CODEC.encode(msg) as Buffer
        // buffer should be empty (default is not in wire)
        assertEquals(0, buffer.size)

        val decoded = UsingEnumInternal.CODEC.decode(buffer)
        assertEquals(MyEnum.ZERO, decoded.enum)
    }

    @Test
    fun testOneOf() {
        val msg1 = OneOfMsg {
            field = OneOfMsg.Field.Sint(23)
        }
        val decoded1 = encodeDecode(msg1, OneOfMsgInternal.CODEC)
        assertEquals(OneOfMsg.Field.Sint(23), decoded1.field)

        val msg2 = OneOfMsg {
            field = OneOfMsg.Field.Fixed(21u)
        }
        val decoded2 = encodeDecode(msg2, OneOfMsgInternal.CODEC)
        assertEquals(OneOfMsg.Field.Fixed(21u), decoded2.field)
    }

    @Test
    fun testOneOfLastWins() {
        // write two values on the oneOf field.
        // the second value must be the one stored during decoding.
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeInt32(2, 99)
        encoder.writeFixed64(3, 123u)
        encoder.flush()

        val decoded = OneOfMsgInternal.CODEC.decode(buffer)
        assertEquals(OneOfMsg.Field.Fixed(123u), decoded.field)
    }

    @Test
    fun testOneOfNull() {
        // write two values on the oneOf field.
        // the second value must be the one stored during decoding.
        val buffer = Buffer()
        val decoded = OneOfMsgInternal.CODEC.decode(buffer)
        assertNull(decoded.field)
    }

    @Test
    fun testSubMessage() {
        val msg = Outer {
            inner = Outer.Inner {
                field = 12345678
            }
        }
        val decoded = encodeDecode(msg, OuterInternal.CODEC)
        assertEquals(msg.inner.field, decoded.inner.field)
    }

    @Test
    fun testRecursiveReqNotSet() {
        assertFailsWith<IllegalStateException>("RecursiveReq is missing required field: rec") {
            val msg = RecursiveReq {
                rec = RecursiveReq {
                    rec = RecursiveReq {

                    }
                    num = 3
                }
            }
        }
    }

    @Test
    fun testRecursive() {
        val msg = Recursive {
            rec = Recursive {
                rec = Recursive {}
                num = 3
            }
        }

        assertEquals(null, msg.rec.rec.rec.rec.num)
        assertEquals(3, msg.rec.num)

        val decoded = encodeDecode(msg, RecursiveInternal.CODEC)

        assertEquals(3, decoded.rec.num)
        assertEquals(null, decoded.rec.rec.rec.rec.num)
    }

    @Test
    fun testNested() {
        val inner = NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner {
            num = 123456789
        }

        val notInside = NotInside {
            num = -12
        }
        val outer = NestedOuter {
            deep = inner
            deepEnum =
                NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.JUST_WAY_TOO_INNER_UNSPECIFIED
        }

        assertEquals(123456789, outer.deep.num)
        assertEquals(
            NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.JUST_WAY_TOO_INNER_UNSPECIFIED,
            outer.deepEnum
        )
        assertEquals(-12, notInside.num)

        val decodedOuter = encodeDecode(outer, NestedOuterInternal.CODEC)
        assertEquals(123456789, decodedOuter.deep.num)
        assertEquals(
            NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.JUST_WAY_TOO_INNER_UNSPECIFIED,
            decodedOuter.deepEnum
        )
        assertEquals(-12, notInside.num)

        val decodedNotInside = encodeDecode(notInside, NotInsideInternal.CODEC)
        assertEquals(-12, decodedNotInside.num)

        val decodedInner = encodeDecode(
            inner,
            NestedOuterInternal.InnerInternal.SuperInnerInternal.DuperInnerInternal.EvenMoreInnerInternal.CantBelieveItsSoInnerInternal.CODEC
        )
        assertEquals(123456789, decodedInner.num)

    }

}
