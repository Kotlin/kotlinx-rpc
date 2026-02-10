/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import OneOfMsg
import OneOfWithRequired
import Outer
import asInternal
import encodeWith
import invoke
import kotlinx.io.Buffer
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.codec.codec
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.input.stream.asSource
import kotlinx.rpc.protobuf.internal.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.WireEncoder
import test.groups.WithGroups
import test.groups.invoke
import test.nested.NestedOuter
import test.nested.NotInside
import test.nested.invoke
import test.recursive.Recursive
import test.recursive.RecursiveReq
import test.recursive.invoke
import test.submsg.Other
import test.submsg.OtherInternal
import test.submsg.Reference
import test.submsg.encodeWith
import test.submsg.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProtosTest {

    private fun <M> encodeDecode(
        msg: M,
        codec: MessageCodec<M>,
    ): M {
        val source = codec.encode(msg)
        return codec.decode(source)
    }

    @Test
    fun testUnknownFieldsDontCrash() {
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        // optional sint32 sint32 = 7
        encoder.writeSInt32(7, 12)
        // optional sint64 sint64 = 8; (unknown as wrong wire-type)
        encoder.writeFloat(8, 2f)
        // optional fixed32 fixed32 = 9;
        encoder.writeFixed32(9, 1234u)
        encoder.flush()

        val decoded = codec<AllPrimitives>().decode(buffer.asInputStream())
        assertEquals(12, decoded.sint32)
        assertNull(decoded.sint64)
        assertEquals(1234u, decoded.fixed32)
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

        val decoded = encodeDecode(msgObj, codec<AllPrimitives>())

        assertEquals(msg.double, decoded.double)
    }

    @Test
    fun testRepeatedProto() {
        val elem = { i: Int -> Repeated.Other { a = i } }
        val msg = Repeated {
            listFixed32 = listOf(1, 5, 3).map { it.toUInt() }
            listFixed32Packed = listOf(1, 2, 3).map { it.toUInt() }
            listInt32 = listOf(4, 7, 6)
            listInt32Packed = listOf(4, 5, 6)
            listString = listOf("a", "b", "c")
            listMessage = listOf(elem(1), elem(2), elem(3))
        }

        val decoded = encodeDecode(msg, codec<Repeated>())

        assertEquals(msg.listInt32, decoded.listInt32)
        assertEquals(msg.listFixed32, decoded.listFixed32)
        assertEquals(msg.listString, decoded.listString)
        assertEquals(msg.listMessage.size, decoded.listMessage.size)
        for (i in msg.listMessage.indices) {
            assertEquals(msg.listMessage[i].a, decoded.listMessage[i].a)
        }
    }

    @Test
    fun testRepeatedWithRequiredSubField() {
        assertFailsWith<ProtobufDecodingException> {
            RepeatedWithRequired {
                // we construct the message using the internal class,
                // so it is not invoking the checkRequired method on construction
                msgList = listOf(PresenceCheck { RequiredPresence = 2 }, PresenceCheckInternal())
            }
        }
    }

    @Test
    fun testPresenceCheckProto() {
        // Check a missing required field in a user-constructed message
        assertFailsWith<ProtobufDecodingException> {
            PresenceCheck {}
        }

        // Test missing field during decoding of an encoded message
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeFloat(2, 1f)
        encoder.flush()

        assertFailsWith<ProtobufDecodingException> {
            codec<PresenceCheck>().decode(buffer.asInputStream())
        }
    }

    @Test
    fun testEnumUnrecognized() {
        // write unknown enum value
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeEnum(1, 50)
        encoder.flush()

        val decodedMsg = codec<UsingEnum>().decode(buffer.asInputStream())
        assertEquals(MyEnum.UNRECOGNIZED(50), decodedMsg.enum)
    }

    @Test
    fun testEnumAlias() {
        val msg = UsingEnum {
            enum = MyEnum.ONE_SECOND
        }

        val decodedMsg = encodeDecode(msg, codec<UsingEnum>())
        assertEquals(MyEnum.ONE, decodedMsg.enum)
        assertEquals(MyEnum.ONE_SECOND, decodedMsg.enum)
    }

    @Test
    fun testDefault() {
        // create message without enum field set
        val msg = UsingEnum {}

        val buffer = codec<UsingEnum>().encode(msg).asSource()
        // buffer should be empty (default is not in wire)
        assertTrue(buffer.exhausted())

        val decoded = codec<UsingEnum>().decode(buffer.asInputStream())
        assertEquals(MyEnum.ZERO, decoded.enum)
    }

    @Test
    fun testOneOf() {
        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Sint(23)
            }
            val decoded = encodeDecode(msg, codec<OneOfMsg>())
            assertEquals(OneOfMsg.Field.Sint(23), decoded.field)
        }

        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Fixed(21u)
            }
            val decoded = encodeDecode(msg, codec<OneOfMsg>())
            assertEquals(OneOfMsg.Field.Fixed(21u), decoded.field)
        }

        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Other(Other { arg2 = "test" })
            }
            val decoded = encodeDecode(msg, codec<OneOfMsg>())
            assertIs<OneOfMsg.Field.Other>(decoded.field)
            assertNull((decoded.field as OneOfMsg.Field.Other).value.arg1)
            assertEquals("test", (decoded.field as OneOfMsg.Field.Other).value.arg2)
            assertNull((decoded.field as OneOfMsg.Field.Other).value.arg3)
        }

        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Enum(MyEnum.ONE_SECOND)
            }
            val decoded = encodeDecode(msg, codec<OneOfMsg>())
            assertEquals(MyEnum.ONE, (decoded.field as OneOfMsg.Field.Enum).value)
        }
    }

    @Test
    fun testOneOfMsgMerging() {
        val part1 = OneOfMsg {
            field = OneOfMsg.Field.Other(Other { arg2 = "arg2" })
        }
        val part2 = OneOfMsg {
            field = OneOfMsg.Field.Other(Other { arg1 = "arg1" })
        }

        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        part1.asInternal().encodeWith(encoder, null)
        part2.asInternal().encodeWith(encoder, null)
        encoder.flush()


        val decoded = codec<OneOfMsg>().decode(buffer.asInputStream())
        assertIs<OneOfMsg.Field.Other>(decoded.field)
        val decodedOther = (decoded.field as OneOfMsg.Field.Other).value
        assertEquals("arg2", decodedOther.arg2)
        assertEquals("arg1", decodedOther.arg1)
        assertEquals(null, decodedOther.arg3)
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

        val decoded = codec<OneOfMsg>().decode(buffer.asInputStream())
        assertEquals(OneOfMsg.Field.Fixed(123u), decoded.field)
    }

    @Test
    fun testOneOfRequiredSubField() {
        assertFailsWith<ProtobufDecodingException> {
            OneOfWithRequired {
                // we construct the message using the internal class,
                // so it is not invoking the checkRequired method on construction
                field = OneOfWithRequired.Field.Msg(PresenceCheckInternal())
            }
        }
    }

    @Test
    fun testOneOfNull() {
        // write two values on the oneOf field.
        // the second value must be the one stored during decoding.
        val buffer = Buffer()
        val decoded = codec<OneOfMsg>().decode(buffer.asInputStream())
        assertNull(decoded.field)
    }

    @Test
    fun testSubMessage() {
        val msg = Outer {
            inner = Outer.Inner {
                field = 12345678
            }
        }
        val decoded = encodeDecode(msg, codec<Outer>())
        assertEquals(msg.inner.field, decoded.inner.field)
    }

    @Test
    fun testRecursiveReqNotSet() {
        assertFailsWith<ProtobufDecodingException> {
            RecursiveReq {
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

        val decoded = encodeDecode(msg, codec<Recursive>())

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

        val decodedOuter = encodeDecode(outer, codec<NestedOuter>())
        assertEquals(123456789, decodedOuter.deep.num)
        assertEquals(
            NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.JUST_WAY_TOO_INNER_UNSPECIFIED,
            decodedOuter.deepEnum
        )
        assertEquals(-12, notInside.num)

        val decodedNotInside = encodeDecode(notInside, codec<NotInside>())
        assertEquals(-12, decodedNotInside.num)

        val decodedInner = encodeDecode(
            inner,
            codec<NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner>()
        )
        assertEquals(123456789, decodedInner.num)
    }

    @Test
    fun testMessageMerging() {

        val buffer = Buffer()
        val encoder = WireEncoder(buffer)

        val firstPart = Other {
            arg1 = "first"
            arg2 = "second"
        }
        val secondPart = Other {
            arg2 = "third"
            arg3 = "fourth"
        }

        encoder.writeMessage(1, firstPart as OtherInternal) { encodeWith(encoder, null) }
        encoder.flush()
        encoder.writeMessage(1, secondPart as OtherInternal) { encodeWith(encoder, null) }
        encoder.flush()

        val decoded = codec<Reference>().decode(buffer.asInputStream())
        assertEquals("first", decoded.other.arg1)
        assertEquals("third", decoded.other.arg2)
        assertEquals("fourth", decoded.other.arg3)
    }


    @Test
    fun testMap() {
        val msg = TestMap {
            primitives = mapOf("one" to 1, "two" to 2, "three" to 3)
            messages = mapOf(
                1 to PresenceCheck { RequiredPresence = 1 },
                2 to PresenceCheck { RequiredPresence = 2; OptionalPresence = 3F }
            )
        }

        val decoded = encodeDecode(msg, codec<TestMap>())
        assertEquals(msg.primitives, decoded.primitives)
        assertEquals(msg.messages.size, decoded.messages.size)
        for ((key, value) in msg.messages) {
            assertEquals(value.RequiredPresence, decoded.messages[key]!!.RequiredPresence)
            assertEquals(value.OptionalPresence, decoded.messages[key]!!.OptionalPresence)
        }
    }

    @Test
    fun testMapRequiredSubField() {
        // we use the internal constructor to avoid a "missing required field" error during object construction
        val missingRequiredMessage = PresenceCheckInternal()

        assertFailsWith<ProtobufDecodingException> {
            TestMap {
                messages = mapOf(
                    2 to missingRequiredMessage
                )
            }
        }
    }

    @Test
    fun testGroup() {
        val msg = WithGroups {
            firstgroup = WithGroups.FirstGroup {
                value = 23u
            }
            secondgroup = listOf(
                WithGroups.SecondGroup {
                    value = "First Item"
                }, WithGroups.SecondGroup {
                    value = "Second Item"
                }
            )
            oneOfWithGroup = WithGroups.OneOfWithGroup.Testgroup(WithGroups.TestGroup {
                value = 42u
            })
        }

        val decoded = encodeDecode(msg, codec<WithGroups>())
        assertEquals(msg.firstgroup.value, decoded.firstgroup.value)
        for ((i, group) in msg.secondgroup.withIndex()) {
            assertEquals(group.value, decoded.secondgroup[i].value)
        }
        assertTrue(decoded.oneOfWithGroup is WithGroups.OneOfWithGroup.Testgroup)
        assertEquals(
            (msg.oneOfWithGroup as WithGroups.OneOfWithGroup.Testgroup).value.value,
            (decoded.oneOfWithGroup as WithGroups.OneOfWithGroup.Testgroup).value.value
        )
    }
}
