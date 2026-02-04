/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.input.stream.asSource
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.WireEncoder
import test.nested.NestedOuter
import test.nested.invoke
import test.submsg.Other
import test.submsg.asInternal
import test.submsg.encodeWith
import test.submsg.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UnknownFieldsTest {

    fun send(msg: UnknownFieldsAll): UnknownFieldsSubset {
        val encoded = UnknownFieldsAllInternal.CODEC.encode(msg)
        return UnknownFieldsSubsetInternal.CODEC.decode(encoded)
    }

    fun send(msg: UnknownFieldsSubset): UnknownFieldsAll {
        val encoded = UnknownFieldsSubsetInternal.CODEC.encode(msg)
        return UnknownFieldsAllInternal.CODEC.decode(encoded)
    }

    @Test
    fun `test unknown fields - empty`() {
        val all = UnknownFieldsAll {
            field1 = 123
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields;
        assertEquals(unknownFields.size, 0L)

        val all2 = send(subset)

        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - fields propergated`() {
        val all = UnknownFieldsAll {
            field1 = 123
            intMissing = 456
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields;
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all2.intMissing, all.intMissing)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - copy`() {
        val all = UnknownFieldsAll {
            field1 = 123
            intMissing = 456
        }
        val subset = send(all)
        val copy = subset.copy()

        val all2 = send(copy)
        assertEquals(all2.intMissing, all.intMissing)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - all primitive types`() {
        val all = UnknownFieldsAll {
            field1 = 123
            intMissing = 456
            int64Missing = 789L
            uint32Missing = 111u
            uint64Missing = 222uL
            sint32Missing = -333
            sint64Missing = -444L
            fixed32Missing = 555u
            fixed64Missing = 666uL
            sfixed32Missing = -777
            sfixed64Missing = -888L
            boolMissing = true
            stringMissing = "test string"
            bytesMissing = byteArrayOf(1, 2, 3, 4, 5)
            floatMissing = 3.14f
            doubleMissing = 2.718
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - nested message`() {
        val all = UnknownFieldsAll {
            field1 = 123
            allPrimitivesMissing = AllPrimitives {
                double = 1.23
                float = 4.56f
                int32 = 789
                int64 = 1011L
                uint32 = 1213u
                uint64 = 1415uL
                string = "nested"
                bytes = byteArrayOf(6, 7, 8)
            }
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - enum`() {
        val all = UnknownFieldsAll {
            field1 = 123
            enumMissing = MyEnum.TWO
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - repeated primitives`() {
        val all = UnknownFieldsAll {
            field1 = 123
            repeatedIntMissing = listOf(1, 2, 3, 4, 5)
            repeatedStringMissing = listOf("a", "b", "c")
            repeatedFixed32Missing = listOf(10u, 20u, 30u)
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - repeated messages`() {
        val all = UnknownFieldsAll {
            field1 = 123
            repeatedMessageMissing = listOf(
                AllPrimitives {
                    int32 = 100
                    string = "first"
                },
                AllPrimitives {
                    int32 = 200
                    string = "second"
                }
            )
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - repeated enums`() {
        val all = UnknownFieldsAll {
            field1 = 123
            repeatedEnumMissing = listOf(MyEnum.ZERO, MyEnum.ONE, MyEnum.TWO, MyEnum.THREE)
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - map primitives`() {
        val all = UnknownFieldsAll {
            field1 = 123
            mapStringIntMissing = mapOf("key1" to 100, "key2" to 200)
            mapIntStringMissing = mapOf(1 to "value1", 2 to "value2")
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - map messages`() {
        val all = UnknownFieldsAll {
            field1 = 123
            mapStringMessageMissing = mapOf(
                "first" to AllPrimitives {
                    int32 = 100
                    string = "msg1"
                },
                "second" to AllPrimitives {
                    int32 = 200
                    string = "msg2"
                }
            )
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - oneof int`() {
        val all = UnknownFieldsAll {
            field1 = 123
            testOneof = UnknownFieldsAll.TestOneof.OneofInt(999)
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - oneof string`() {
        val all = UnknownFieldsAll {
            field1 = 123
            testOneof = UnknownFieldsAll.TestOneof.OneofString("oneof value")
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - oneof message`() {
        val all = UnknownFieldsAll {
            field1 = 123
            testOneof = UnknownFieldsAll.TestOneof.OneofMessage(AllPrimitives {
                int32 = 777
                string = "oneof msg"
            })
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - oneof enum`() {
        val all = UnknownFieldsAll {
            field1 = 123
            testOneof = UnknownFieldsAll.TestOneof.OneofEnum(MyEnum.THREE)
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - multiple field types combined`() {
        val all = UnknownFieldsAll {
            field1 = 123
            intMissing = 456
            stringMissing = "test"
            boolMissing = true
            allPrimitivesMissing = AllPrimitives {
                int32 = 789
                string = "nested"
            }
            enumMissing = MyEnum.ONE
            repeatedIntMissing = listOf(1, 2, 3)
            repeatedStringMissing = listOf("a", "b")
            mapStringIntMissing = mapOf("key" to 100)
            testOneof = UnknownFieldsAll.TestOneof.OneofString("oneof")
        }

        val subset = send(all)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
    }

    @Test
    fun `test unknown fields - deeply nested message`() {
        val all = UnknownFieldsAll {
            field1 = 123
            allPrimitivesMissing = AllPrimitives {
                int32 = 100
                string = "level1"
            }
            repeatedMessageMissing = listOf(
                AllPrimitives {
                    int32 = 200
                    string = "level2-item1"
                },
                AllPrimitives {
                    int32 = 300
                    string = "level2-item2"
                }
            )
            mapStringMessageMissing = mapOf(
                "nested" to AllPrimitives {
                    int32 = 400
                    string = "level2-map"
                }
            )
        }

        val subset = send(all)
        assertEquals(all.field1, subset.field1)
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size != 0L)

        val all2 = send(subset)
        assertEquals(all, all2)
        assertEquals(all2.allPrimitivesMissing.int32, 100)
        assertEquals(all2.repeatedMessageMissing.size, 2)
        assertEquals(all2.mapStringMessageMissing["nested"]?.int32, 400)
    }

    @Test
    fun `test unknown fields - groups`() {
        // create a buffer with nested group messages manually using WireEncoder
        val originalBuffer = Buffer()
        val encoder = WireEncoder(originalBuffer)

        // write field1 = 123
        encoder.writeInt32(fieldNr = 1, value = 123)

        val internalMessage = Other {
            arg1 = "Hello"
            arg2 = "World"
        }

        // write a group field (field 50) with nested content
        // groups use START_GROUP (wire type 3) and END_GROUP (wire type 4)
        encoder.writeGroupMessage(fieldNr = 50, internalMessage.asInternal()) { encodeWith(it) }
        // write another regular (unknown) field after the group
        encoder.writeInt32(fieldNr = 2, value = 456)

        encoder.flush()

        // save the original bytes for comparison
        val originalCopy = originalBuffer.copy()
        val originalBytes = originalCopy.readByteArray()

        // decode with UnknownFieldsSubset (which doesn't know about the group fields)
        val subset = UnknownFieldsSubsetInternal.CODEC.decode(originalBuffer.asInputStream())

        // the unknown fields should be preserved
        val unknownFields = subset.asInternal()._unknownFields
        assertTrue(unknownFields.size > 0L, "Unknown fields should contain the group data")

        // re-encode and check that the buffer contains the same data
        val reencodedBuffer = UnknownFieldsSubsetInternal.CODEC.encode(subset).asSource()
        val reencodedBytes = reencodedBuffer.readByteArray()

        // the buffers should be identical
        assertEquals(originalBytes.size, reencodedBytes.size, "Buffer sizes should match")
        assertTrue(originalBytes.contentEquals(reencodedBytes), "Buffers should be identical")
    }

}