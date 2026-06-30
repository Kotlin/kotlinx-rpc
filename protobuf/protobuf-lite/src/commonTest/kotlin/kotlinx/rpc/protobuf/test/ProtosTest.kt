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
import kotlinx.io.readByteArray
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.ProtobufException
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
import test.submsg.presence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProtosTest {

    private fun <M> encodeDecode(
        msg: M,
        marshaller: GrpcMarshaller<M>,
    ): M {
        val source = marshaller.encode(msg)
        return marshaller.decode(source)
    }

    private fun encodeToBytes(block: (WireEncoder) -> Unit): ByteArray {
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        block(encoder)
        encoder.flush()
        return buffer.readByteArray()
    }

    // PresenceCheck wire bytes that contain only the optional field (field 2),
    // i.e. the required `RequiredPresence` (field 1) is missing.
    private fun presenceCheckMissingRequired(): ByteArray = encodeToBytes {
        it.writeFloat(2, 1f)
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

        val decoded = grpcMarshallerOf<AllPrimitives>().decode(buffer)
        assertEquals(12, decoded.sint32)
        assertFalse(decoded.presence.hasSint64)
        assertEquals(0, decoded.sint64)
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
            bytes = byteArrayOf(1, 2, 3).asByteString()
        }

        val decoded = encodeDecode(msg, grpcMarshallerOf<AllPrimitives>())

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

        val decoded = encodeDecode(msg, grpcMarshallerOf<Repeated>())

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
        assertFailsWith<ProtobufException> {
            RepeatedWithRequired {
                msgList = listOf(PresenceCheck { RequiredPresence = 2 }, PresenceCheck { })
            }
        }
    }

    @Test
    fun testRepeatedRequiredSubFieldMissingOnWire() {
        // A repeated message field where one of the list elements
        // is missing its required field during decoding.
        val validElement = encodeToBytes {
            it.writeInt32(1, 2)
            it.writeFloat(2, 1f)
        }

        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeBytes(1, validElement)
        encoder.writeBytes(1, presenceCheckMissingRequired())
        encoder.flush()

        assertFailsWith<ProtobufException> {
            grpcMarshallerOf<RepeatedWithRequired>().decode(buffer)
        }
    }

    @Test
    fun testPresenceCheckProto() {
        // Check a missing required field in a user-constructed message
        assertFailsWith<ProtobufException> {
            PresenceCheck {}
        }

        // Test missing field during decoding of an encoded message
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeFloat(2, 1f)
        encoder.flush()

        assertFailsWith<ProtobufException> {
            grpcMarshallerOf<PresenceCheck>().decode(buffer)
        }
    }

    @Test
    fun testEnumUnrecognized() {
        // write unknown enum value
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeEnum(1, 50)
        encoder.flush()

        val decodedMsg = grpcMarshallerOf<UsingEnum>().decode(buffer)
        assertEquals(MyEnum.UNRECOGNIZED(50), decodedMsg.enum)
    }

    @Test
    fun testEnumAlias() {
        val msg = UsingEnum {
            enum = MyEnum.ONE_SECOND
        }

        val decodedMsg = encodeDecode(msg, grpcMarshallerOf<UsingEnum>())
        assertEquals(MyEnum.ONE, decodedMsg.enum)
        assertEquals(MyEnum.ONE_SECOND, decodedMsg.enum)
    }

    @Test
    fun testDefault() {
        // create message without enum field set
        val msg = UsingEnum {}

        val buffer = grpcMarshallerOf<UsingEnum>().encode(msg)
        // buffer should be empty (default is not in wire)
        assertTrue(buffer.exhausted())

        val decoded = grpcMarshallerOf<UsingEnum>().decode(buffer)
        assertEquals(MyEnum.ZERO, decoded.enum)
    }

    @Test
    fun testOneOf() {
        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Sint(23)
            }
            val decoded = encodeDecode(msg, grpcMarshallerOf<OneOfMsg>())
            assertEquals(OneOfMsg.Field.Sint(23), decoded.field)
        }

        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Fixed(21u)
            }
            val decoded = encodeDecode(msg, grpcMarshallerOf<OneOfMsg>())
            assertEquals(OneOfMsg.Field.Fixed(21u), decoded.field)
        }

        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Other(Other { arg2 = "test" })
            }
            val decoded = encodeDecode(msg, grpcMarshallerOf<OneOfMsg>())
            assertIs<OneOfMsg.Field.Other>(decoded.field)
            val decodedOtherField = (decoded.field as OneOfMsg.Field.Other).value
            assertFalse(decodedOtherField.presence.hasArg1)
            assertEquals("", decodedOtherField.arg1)
            assertEquals("test", decodedOtherField.arg2)
            assertFalse(decodedOtherField.presence.hasArg1)
            assertEquals("", decodedOtherField.arg3)
        }

        run {
            val msg = OneOfMsg {
                field = OneOfMsg.Field.Enum(MyEnum.ONE_SECOND)
            }
            val decoded = encodeDecode(msg, grpcMarshallerOf<OneOfMsg>())
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


        val decoded = grpcMarshallerOf<OneOfMsg>().decode(buffer)
        assertIs<OneOfMsg.Field.Other>(decoded.field)
        val decodedOther = (decoded.field as OneOfMsg.Field.Other).value
        assertEquals("arg2", decodedOther.arg2)
        assertEquals("arg1", decodedOther.arg1)
        assertEquals("", decodedOther.arg3)
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

        val decoded = grpcMarshallerOf<OneOfMsg>().decode(buffer)
        assertEquals(OneOfMsg.Field.Fixed(123u), decoded.field)
    }

    @Test
    fun testOneOfRequiredSubField() {
        assertFailsWith<ProtobufException> {
            OneOfWithRequired {
                field = OneOfWithRequired.Field.Msg(PresenceCheck { })
            }
        }
    }

    @Test
    fun testOneOfRequiredSubFieldMissingOnWire() {
        // A oneof message branch whose message is missing
        // its required field during decoding (msg = 2).
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeBytes(2, presenceCheckMissingRequired())
        encoder.flush()

        assertFailsWith<ProtobufException> {
            grpcMarshallerOf<OneOfWithRequired>().decode(buffer)
        }
    }

    @Test
    fun testOneOfNull() {
        // write two values on the oneOf field.
        // the second value must be the one stored during decoding.
        val buffer = Buffer()
        val decoded = grpcMarshallerOf<OneOfMsg>().decode(buffer)
        assertNull(decoded.field)
    }

    @Test
    fun testSubMessage() {
        val msg = Outer {
            inner = Outer.Inner {
                field = 12345678
            }
        }
        val decoded = encodeDecode(msg, grpcMarshallerOf<Outer>())
        assertEquals(msg.inner.field, decoded.inner.field)
    }

    @Test
    fun testSubMessageRequiredFieldMissingOnWire() {
        // The `inner` submessage is present on the wire (field 1)
        // but is missing its required `field` during decoding.
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeBytes(1, ByteArray(0))
        encoder.flush()

        assertFailsWith<ProtobufException> {
            grpcMarshallerOf<Outer>().decode(buffer)
        }
    }

    @Test
    fun testRecursiveReqNotSet() {
        assertFailsWith<ProtobufException> {
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

        assertEquals(0, msg.rec.rec.rec.rec.num)
        assertEquals(3, msg.rec.num)

        val decoded = encodeDecode(msg, grpcMarshallerOf<Recursive>())

        assertEquals(3, decoded.rec.num)
        assertEquals(0, decoded.rec.rec.rec.rec.num)
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
                NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.UNSPECIFIED
        }

        assertEquals(123456789, outer.deep.num)
        assertEquals(
            NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.UNSPECIFIED,
            outer.deepEnum
        )
        assertEquals(-12, notInside.num)

        val decodedOuter = encodeDecode(outer, grpcMarshallerOf<NestedOuter>())
        assertEquals(123456789, decodedOuter.deep.num)
        assertEquals(
            NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.JustWayTooInner.UNSPECIFIED,
            decodedOuter.deepEnum
        )
        assertEquals(-12, notInside.num)

        val decodedNotInside = encodeDecode(notInside, grpcMarshallerOf<NotInside>())
        assertEquals(-12, decodedNotInside.num)

        val decodedInner = encodeDecode(
            inner,
            grpcMarshallerOf<NestedOuter.Inner.SuperInner.DuperInner.EvenMoreInner.CantBelieveItsSoInner>()
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

        val decoded = grpcMarshallerOf<Reference>().decode(buffer)
        assertEquals("first", decoded.other.arg1)
        assertEquals("third", decoded.other.arg2)
        assertEquals("fourth", decoded.other.arg3)
    }

    @Test
    fun testSharedDefaultsAreNotMutatedDuringDecoding() {
        val referenceMarshaller = grpcMarshallerOf<Reference>()
        val populatedReference = Reference {
            other = Other {
                arg1 = "first"
                arg2 = "second"
            }
        }

        val decodedReference = encodeDecode(populatedReference, referenceMarshaller)
        assertEquals("first", decodedReference.other.arg1)
        assertEquals("second", decodedReference.other.arg2)

        val emptyReference = referenceMarshaller.decode(Buffer())
        assertEquals("", emptyReference.other.arg1)
        assertEquals("", emptyReference.other.arg2)
        assertEquals("", emptyReference.other.arg3)

        val repeatedMarshaller = grpcMarshallerOf<Repeated>()
        val populatedRepeated = Repeated {
            listInt32 = listOf(1, 2, 3)
            listMessage = listOf(Repeated.Other { a = 7 })
        }

        val decodedRepeated = encodeDecode(populatedRepeated, repeatedMarshaller)
        assertEquals(listOf(1, 2, 3), decodedRepeated.listInt32)
        assertEquals(1, decodedRepeated.listMessage.size)
        assertEquals(7, decodedRepeated.listMessage.single().a)

        val emptyRepeated = repeatedMarshaller.decode(Buffer())
        assertTrue(emptyRepeated.listInt32.isEmpty())
        assertTrue(emptyRepeated.listMessage.isEmpty())

        val mapMarshaller = grpcMarshallerOf<TestMap>()
        val populatedMap = TestMap {
            primitives = mapOf("one" to 1L)
            messages = mapOf(1 to PresenceCheck { RequiredPresence = 7 })
        }

        val decodedMap = encodeDecode(populatedMap, mapMarshaller)
        assertEquals(mapOf("one" to 1L), decodedMap.primitives)
        assertEquals(7, decodedMap.messages.getValue(1).RequiredPresence)

        val emptyMap = mapMarshaller.decode(Buffer())
        assertTrue(emptyMap.primitives.isEmpty())
        assertTrue(emptyMap.messages.isEmpty())
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

        val decoded = encodeDecode(msg, grpcMarshallerOf<TestMap>())
        assertEquals(msg.primitives, decoded.primitives)
        assertEquals(msg.messages.size, decoded.messages.size)
        for ((key, value) in msg.messages) {
            assertEquals(value.RequiredPresence, decoded.messages[key]!!.RequiredPresence)
            assertEquals(value.OptionalPresence, decoded.messages[key]!!.OptionalPresence)
        }
    }

    @Test
    fun testMapRequiredSubField() {
        assertFailsWith<ProtobufException> {
            TestMap {
                messages = mapOf(
                    2 to PresenceCheck {}
                )
            }
        }
    }

    @Test
    fun testMapRequiredSubFieldMissingOnWire() {
        // A map<int32, PresenceCheck> entry (messages = 2) whose value message
        // is missing its required field during decoding.
        // A map entry is encoded as a submessage with key = field 1, value = field 2.
        val mapEntry = encodeToBytes {
            it.writeInt32(1, 7)
            it.writeBytes(2, presenceCheckMissingRequired())
        }

        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeBytes(2, mapEntry)
        encoder.flush()

        assertFailsWith<ProtobufException> {
            grpcMarshallerOf<TestMap>().decode(buffer)
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

        val decoded = encodeDecode(msg, grpcMarshallerOf<WithGroups>())
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

    @Test
    fun testGroupAsStandalone() {
        val standaloneGroup = WithGroups.FirstGroup {
            value = 42u
        }

        val decoded = encodeDecode(standaloneGroup, grpcMarshallerOf<WithGroups.FirstGroup>())
        assertEquals(standaloneGroup.value, decoded.value)
    }
}
