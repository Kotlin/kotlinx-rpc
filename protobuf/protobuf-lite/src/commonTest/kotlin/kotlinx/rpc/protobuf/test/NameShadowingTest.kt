/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import name_shadowing.Any
import name_shadowing.BoolValue
import name_shadowing.Collection
import name_shadowing.ComplexNesting
import name_shadowing.DescriptorProto
import name_shadowing.Duration
import name_shadowing.Empty
import name_shadowing.FieldMask
import name_shadowing.FieldOptions
import name_shadowing.Int
import name_shadowing.List
import name_shadowing.Map
import name_shadowing.MixedNesting
import name_shadowing.MixedOneof
import name_shadowing.MixedWktAndLocal
import name_shadowing.MyClass1
import name_shadowing.MyClass2
import name_shadowing.RecursiveWithShadowing
import name_shadowing.ShadowedOneof
import name_shadowing.String
import name_shadowing.StringValue
import name_shadowing.Struct
import name_shadowing.Timestamp
import name_shadowing.Value
import name_shadowing.WktNesting
import name_shadowing.WktOneof
import name_shadowing.copy
import name_shadowing.invoke
import com.google.protobuf.kotlin.invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NameShadowingTest {
    @Test
    fun testShadowedString() {
        val msg = String {
            value = "hello"
            nestedInt = Int {
                value = 10
                nestedString = String {
                    value = "nested in int"
                }
            }
        }

        assertEquals("hello", msg.value)
        assertEquals(10, msg.nestedInt.value)
        assertEquals("nested in int", msg.nestedInt.nestedString.value)
    }

    @Test
    fun testShadowedInt() {
        val msg = Int {
            value = 42
            nestedString = String {
                value = "string in int"
                nestedInt = Int {
                    value = 100
                }
            }
        }

        assertEquals(42, msg.value)
        assertEquals("string in int", msg.nestedString.value)
        assertEquals(100, msg.nestedString.nestedInt.value)
    }

    @Test
    fun testShadowedList() {
        val msg = List {
            items = listOf("a", "b", "c")
            nestedMap = Map {
                entries = mapOf("key" to "value")
                nestedList = List {
                    items = listOf("x", "y")
                }
            }
        }

        assertEquals(listOf("a", "b", "c"), msg.items)
        assertEquals(mapOf("key" to "value"), msg.nestedMap.entries)
        assertEquals(listOf("x", "y"), msg.nestedMap.nestedList.items)
    }

    @Test
    fun testShadowedMap() {
        val msg = Map {
            entries = mapOf("key1" to "value1", "key2" to "value2")
            nestedList = List {
                items = listOf("item1", "item2")
                nestedMap = Map {
                    entries = mapOf("inner" to "map")
                }
            }
        }

        assertEquals(mapOf("key1" to "value1", "key2" to "value2"), msg.entries)
        assertEquals(listOf("item1", "item2"), msg.nestedList.items)
        assertEquals(mapOf("inner" to "map"), msg.nestedList.nestedMap.entries)
    }

    @Test
    fun testCircularReferences() {
        val deepString = String {
            value = "deep"
            nestedInt = Int {
                value = 999
            }
        }

        val middleInt = Int {
            value = 42
            nestedString = deepString
        }

        val topString = String {
            value = "top"
            nestedInt = middleInt
        }

        assertEquals("top", topString.value)
        assertEquals(42, topString.nestedInt.value)
        assertEquals("deep", topString.nestedInt.nestedString.value)
        assertEquals(999, topString.nestedInt.nestedString.nestedInt.value)
    }

    @Test
    fun testCollectionEnum() {
        assertEquals(0, Collection.UNSPECIFIED.number)
        assertEquals(1, Collection.LIST.number)
        assertEquals(2, Collection.MAP.number)
    }

    @Test
    fun testComplexNesting() {
        val veryDeep = ComplexNesting.Nested.DeeplyNested.VeryDeep {
            shadowedMap = Map {
                entries = mapOf("deep" to "value")
                nestedList = List {
                    items = listOf("deep list")
                }
            }
            outerString = String {
                value = "very deep string"
                nestedInt = Int {
                    value = 777
                }
            }
        }

        val deeplyNested = ComplexNesting.Nested.DeeplyNested {
            shadowedList = List {
                items = listOf("item1", "item2")
                nestedMap = Map {
                    entries = mapOf("listMap" to "val")
                }
            }
            this.veryDeep = veryDeep
        }

        val nested = ComplexNesting.Nested {
            shadowedInt = Int {
                value = 100
                nestedString = String {
                    value = "int nested string"
                }
            }
            this.deeplyNested = deeplyNested
        }

        val msg = ComplexNesting {
            shadowedString = String {
                value = "top level"
                nestedInt = Int {
                    value = 1
                }
            }
            this.nested = nested
            stringList = listOf(
                String { value = "list1"; nestedInt = Int { value = 10 } },
                String { value = "list2"; nestedInt = Int { value = 20 } }
            )
            intMap = mapOf(
                "one" to Int { value = 1; nestedString = String { value = "one str" } },
                "two" to Int { value = 2; nestedString = String { value = "two str" } }
            )
        }

        assertEquals("top level", msg.shadowedString.value)
        assertEquals(1, msg.shadowedString.nestedInt.value)
        assertEquals(100, msg.nested.shadowedInt.value)
        assertEquals("int nested string", msg.nested.shadowedInt.nestedString.value)
        assertEquals(listOf("item1", "item2"), msg.nested.deeplyNested.shadowedList.items)
        assertEquals(mapOf("listMap" to "val"), msg.nested.deeplyNested.shadowedList.nestedMap.entries)
        assertEquals(mapOf("deep" to "value"), msg.nested.deeplyNested.veryDeep.shadowedMap.entries)
        assertEquals(listOf("deep list"), msg.nested.deeplyNested.veryDeep.shadowedMap.nestedList.items)
        assertEquals("very deep string", msg.nested.deeplyNested.veryDeep.outerString.value)
        assertEquals(777, msg.nested.deeplyNested.veryDeep.outerString.nestedInt.value)
        assertEquals(2, msg.stringList.size)
        assertEquals("list1", msg.stringList[0].value)
        assertEquals(10, msg.stringList[0].nestedInt.value)
        assertEquals(1, msg.intMap["one"]?.value)
        assertEquals("one str", msg.intMap["one"]?.nestedString?.value)
    }

    @Test
    fun testShadowedOneof() {
        val msgWithString = ShadowedOneof {
            value = ShadowedOneof.Value.String(String {
                value = "oneof string"
                nestedInt = Int { value = 1 }
            })
        }

        val msgWithInt = ShadowedOneof {
            value = ShadowedOneof.Value.Int(Int {
                value = 123
                nestedString = String { value = "int str" }
            })
        }

        val msgWithList = ShadowedOneof {
            value = ShadowedOneof.Value.List(List {
                items = listOf("a", "b")
                nestedMap = Map { entries = mapOf("k" to "v") }
            })
        }

        val msgWithMap = ShadowedOneof {
            value = ShadowedOneof.Value.Map(Map {
                entries = mapOf("k" to "v")
                nestedList = List { items = listOf("e") }
            })
        }

        val stringValue = msgWithString.value as ShadowedOneof.Value.String
        assertEquals("oneof string", stringValue.value.value)
        assertEquals(1, stringValue.value.nestedInt.value)

        val intValue = msgWithInt.value as ShadowedOneof.Value.Int
        assertEquals(123, intValue.value.value)
        assertEquals("int str", intValue.value.nestedString.value)

        val listValue = msgWithList.value as ShadowedOneof.Value.List
        assertEquals(listOf("a", "b"), listValue.value.items)
        assertEquals(mapOf("k" to "v"), listValue.value.nestedMap.entries)

        val mapValue = msgWithMap.value as ShadowedOneof.Value.Map
        assertEquals(mapOf("k" to "v"), mapValue.value.entries)
        assertEquals(listOf("e"), mapValue.value.nestedList.items)
    }

    @Test
    fun testRecursiveWithShadowing() {
        val child1 = RecursiveWithShadowing {
            name = String {
                value = "child1"
                nestedInt = Int { value = 1 }
            }
            metadata = Map {
                entries = mapOf("level" to "1")
                nestedList = List { items = listOf("child1 list") }
            }
            iterator = RecursiveWithShadowing.Iterator {
                current = Int {
                    value = 0
                    nestedString = String { value = "iter 0" }
                }
                status = String {
                    value = "done"
                }
            }
        }

        val child2 = RecursiveWithShadowing {
            name = String {
                value = "child2"
                nestedInt = Int { value = 2 }
            }
            metadata = Map {
                entries = mapOf("level" to "1")
                nestedList = List { items = listOf("child2 list") }
            }
            iterator = RecursiveWithShadowing.Iterator {
                current = Int {
                    value = 1
                    nestedString = String { value = "iter 1" }
                }
                status = String {
                    value = "done"
                }
            }
        }

        val parent = RecursiveWithShadowing {
            name = String {
                value = "parent"
                nestedInt = Int { value = 0 }
            }
            children = listOf(child1, child2)
            metadata = Map {
                entries = mapOf("level" to "0")
                nestedList = List { items = listOf("parent list") }
            }
            iterator = RecursiveWithShadowing.Iterator {
                current = Int {
                    value = 0
                    nestedString = String { value = "parent iter" }
                }
                status = String {
                    value = "running"
                }
            }
        }

        assertEquals("parent", parent.name.value)
        assertEquals(0, parent.name.nestedInt.value)
        assertEquals(2, parent.children.size)
        assertEquals("child1", parent.children[0].name.value)
        assertEquals(1, parent.children[0].name.nestedInt.value)
        assertEquals("child2", parent.children[1].name.value)
        assertEquals(2, parent.children[1].name.nestedInt.value)
        assertEquals("running", parent.iterator.status.value)
        assertEquals("parent iter", parent.iterator.current.nestedString.value)
        assertEquals("done", parent.children[0].iterator.status.value)
        assertEquals("iter 0", parent.children[0].iterator.current.nestedString.value)
    }

    @Test
    fun testEquality() {
        val msg1 = String {
            value = "test"
            nestedInt = Int {
                value = 42
                nestedString = String { value = "nested" }
            }
        }

        val msg2 = String {
            value = "test"
            nestedInt = Int {
                value = 42
                nestedString = String { value = "nested" }
            }
        }

        val msg3 = String {
            value = "different"
            nestedInt = Int {
                value = 42
                nestedString = String { value = "nested" }
            }
        }

        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())
        assertNotEquals(msg1, msg3)
    }

    @Test
    fun testCopy() {
        val original = String {
            value = "original"
            nestedInt = Int {
                value = 1
                nestedString = String { value = "nested" }
            }
        }

        val copied = original.copy {
            value = "copied"
        }

        assertEquals("original", original.value)
        assertEquals("copied", copied.value)
        assertEquals(1, copied.nestedInt.value)
        assertEquals("nested", copied.nestedInt.nestedString.value)
    }

    @Test
    fun testMyClass1NestedStringShadowing() {
        val deepestString = MyClass1.String.String {
            primitive = "deepest"
        }

        val innerString = MyClass1.String.String {
            primitive = "inner most"
            ref = deepestString
        }

        val middleString = MyClass1.String {
            primitive = "middle"
            ref = innerString
        }

        val msg = MyClass1 {
            primitive = "outer"
            ref = middleString
            nestedRef = innerString
        }

        assertEquals("outer", msg.primitive)
        assertEquals("middle", msg.ref.primitive)
        assertEquals("inner most", msg.ref.ref.primitive)
        assertEquals("deepest", msg.ref.ref.ref.primitive)
        assertEquals("inner most", msg.nestedRef.primitive)
        assertEquals("deepest", msg.nestedRef.ref.primitive)
    }

    @Test
    fun testMyClass2ComplexSiblingReferences() {
        val b = MyClass2.A.B {}

        val innerIntType = MyClass2.Int.String.Int {}

        val inner = MyClass2.Int.String.Inner {
            primitive = 99
            ref = innerIntType
        }

        val deepNestedInt = MyClass2.Int {
            primitive = 10
            sibling = b
        }

        val nestedInt = MyClass2.Int {
            primitive = 42
            ref = deepNestedInt
            sibling = b
        }

        val msg = MyClass2 {
            primitive = "test"
            nested = MyClass2.Int.String {}
        }

        assertEquals("test", msg.primitive)
        assertEquals(42, nestedInt.primitive)
        assertEquals(10, nestedInt.ref.primitive)
        assertEquals(99, inner.primitive)
    }

    @Test
    fun testListMapCycle() {
        val innerList = List {
            items = listOf("inner")
        }

        val map = Map {
            entries = mapOf("key" to "value")
            nestedList = innerList
        }

        val outerList = List {
            items = listOf("outer1", "outer2")
            nestedMap = map
        }

        assertEquals(listOf("outer1", "outer2"), outerList.items)
        assertEquals(mapOf("key" to "value"), outerList.nestedMap.entries)
        assertEquals(listOf("inner"), outerList.nestedMap.nestedList.items)
    }

    @Test
    fun testDeepStringIntChain() {
        val deepestString = String {
            value = "level 5"
        }

        val level4Int = Int {
            value = 4
            nestedString = deepestString
        }

        val level3String = String {
            value = "level 3"
            nestedInt = level4Int
        }

        val level2Int = Int {
            value = 2
            nestedString = level3String
        }

        val rootString = String {
            value = "root"
            nestedInt = level2Int
        }

        assertEquals("root", rootString.value)
        assertEquals(2, rootString.nestedInt.value)
        assertEquals("level 3", rootString.nestedInt.nestedString.value)
        assertEquals(4, rootString.nestedInt.nestedString.nestedInt.value)
        assertEquals("level 5", rootString.nestedInt.nestedString.nestedInt.nestedString.value)
    }

    // WKT name shadowing tests

    @Test
    fun testShadowedTimestamp() {
        val msg = Timestamp {
            seconds = 1234567890L
            label = "epoch"
        }

        assertEquals(1234567890L, msg.seconds)
        assertEquals("epoch", msg.label)
    }

    @Test
    fun testShadowedDuration() {
        val ts = Timestamp {
            seconds = 100L
            label = "start"
        }

        val msg = Duration {
            millis = 5000L
            start = ts
        }

        assertEquals(5000L, msg.millis)
        assertEquals(100L, msg.start.seconds)
        assertEquals("start", msg.start.label)
    }

    @Test
    fun testShadowedAny() {
        val dur = Duration {
            millis = 1000L
            start = Timestamp {
                seconds = 0L
                label = "zero"
            }
        }

        val msg = Any {
            typeUrl = "type.googleapis.com/test"
            duration = dur
        }

        assertEquals("type.googleapis.com/test", msg.typeUrl)
        assertEquals(1000L, msg.duration.millis)
        assertEquals(0L, msg.duration.start.seconds)
    }

    @Test
    fun testShadowedEmpty() {
        val msg = Empty {
            placeholder = "not really empty"
        }

        assertEquals("not really empty", msg.placeholder)
    }

    @Test
    fun testShadowedFieldMask() {
        val msg = FieldMask {
            paths = listOf("field1", "field2.nested")
            createdAt = Timestamp {
                seconds = 999L
                label = "created"
            }
        }

        assertEquals(listOf("field1", "field2.nested"), msg.paths)
        assertEquals(999L, msg.createdAt.seconds)
        assertEquals("created", msg.createdAt.label)
    }

    @Test
    fun testShadowedValueAndStruct() {
        val anyVal = Any {
            typeUrl = "some.type"
            duration = Duration { millis = 42L }
        }

        val value = Value {
            data = "test data"
            anyValue = anyVal
        }

        val empty = Empty { placeholder = "" }

        val msg = Struct {
            fields = mapOf("key1" to value)
            this.empty = empty
        }

        assertEquals("test data", msg.fields["key1"]?.data)
        assertEquals("some.type", msg.fields["key1"]?.anyValue?.typeUrl)
        assertEquals("", msg.empty.placeholder)
    }

    @Test
    fun testShadowedBoolValue() {
        val msg = BoolValue {
            value = true
        }

        assertEquals(true, msg.value)
    }

    @Test
    fun testShadowedStringValue() {
        val msg = StringValue {
            value = "wrapped"
            ts = Timestamp {
                seconds = 42L
                label = "ts"
            }
        }

        assertEquals("wrapped", msg.value)
        assertEquals(42L, msg.ts.seconds)
    }

    @Test
    fun testShadowedDescriptorTypes() {
        val opts = FieldOptions {
            deprecated = true
            defaultValue = StringValue {
                value = "default"
            }
        }

        val msg = DescriptorProto {
            name = "MyMessage"
            options = opts
        }

        assertEquals("MyMessage", msg.name)
        assertEquals(true, msg.options.deprecated)
        assertEquals("default", msg.options.defaultValue.value)
    }

    @Test
    fun testWktNesting() {
        val deep = WktNesting.Nested.DeeplyNested {
            mask = FieldMask {
                paths = listOf("a.b")
                createdAt = Timestamp { seconds = 1L; label = "mask" }
            }
            flag = BoolValue { value = true }
            descriptor = DescriptorProto {
                name = "Desc"
                options = FieldOptions { deprecated = false }
            }
        }

        val nested = WktNesting.Nested {
            empty = Empty { placeholder = "nested empty" }
            struct = Struct {
                fields = mapOf(
                    "f" to Value { data = "val" }
                )
                this.empty = Empty { placeholder = "" }
            }
            value = Value { data = "nested value" }
            this.deep = deep
        }

        val msg = WktNesting {
            ts = Timestamp { seconds = 10L; label = "root ts" }
            dur = Duration { millis = 200L; start = Timestamp { seconds = 5L; label = "dur start" } }
            any = Any { typeUrl = "root.any" }
            this.nested = nested
            values = listOf(
                Value { data = "v1" },
                Value { data = "v2" },
            )
            anyMap = mapOf(
                "a" to Any { typeUrl = "map.any" },
            )
        }

        assertEquals(10L, msg.ts.seconds)
        assertEquals("root ts", msg.ts.label)
        assertEquals(200L, msg.dur.millis)
        assertEquals(5L, msg.dur.start.seconds)
        assertEquals("root.any", msg.any.typeUrl)
        assertEquals("nested empty", msg.nested.empty.placeholder)
        assertEquals("val", msg.nested.struct.fields["f"]?.data)
        assertEquals("nested value", msg.nested.value.data)
        assertEquals(listOf("a.b"), msg.nested.deep.mask.paths)
        assertEquals(true, msg.nested.deep.flag.value)
        assertEquals("Desc", msg.nested.deep.descriptor.name)
        assertEquals(2, msg.values.size)
        assertEquals("v1", msg.values[0].data)
        assertEquals("v2", msg.values[1].data)
        assertEquals("map.any", msg.anyMap["a"]?.typeUrl)
    }

    @Test
    fun testWktOneof() {
        val msgWithTs = WktOneof {
            content = WktOneof.Content.Timestamp(Timestamp {
                seconds = 123L
                label = "oneof ts"
            })
        }

        val msgWithDuration = WktOneof {
            content = WktOneof.Content.Duration(Duration {
                millis = 500L
            })
        }

        val msgWithAny = WktOneof {
            content = WktOneof.Content.Any(Any {
                typeUrl = "oneof.any"
            })
        }

        val msgWithEmpty = WktOneof {
            content = WktOneof.Content.Empty(Empty {
                placeholder = "oneof empty"
            })
        }

        val msgWithValue = WktOneof {
            content = WktOneof.Content.Value(Value {
                data = "oneof value"
            })
        }

        val msgWithStruct = WktOneof {
            content = WktOneof.Content.Struct(Struct {
                fields = mapOf("s" to Value { data = "struct val" })
                empty = Empty { placeholder = "" }
            })
        }

        val tsContent = msgWithTs.content as WktOneof.Content.Timestamp
        assertEquals(123L, tsContent.value.seconds)
        assertEquals("oneof ts", tsContent.value.label)

        val durContent = msgWithDuration.content as WktOneof.Content.Duration
        assertEquals(500L, durContent.value.millis)

        val anyContent = msgWithAny.content as WktOneof.Content.Any
        assertEquals("oneof.any", anyContent.value.typeUrl)

        val emptyContent = msgWithEmpty.content as WktOneof.Content.Empty
        assertEquals("oneof empty", emptyContent.value.placeholder)

        val valueContent = msgWithValue.content as WktOneof.Content.Value
        assertEquals("oneof value", valueContent.value.data)

        val structContent = msgWithStruct.content as WktOneof.Content.Struct
        assertEquals("struct val", structContent.value.fields["s"]?.data)
    }

    @Test
    fun testWktShadowedCopy() {
        val original = Timestamp {
            seconds = 100L
            label = "original"
        }

        val copied = original.copy {
            label = "copied"
        }

        assertEquals(100L, original.seconds)
        assertEquals("original", original.label)
        assertEquals(100L, copied.seconds)
        assertEquals("copied", copied.label)
    }

    @Test
    fun testWktShadowedEquality() {
        val ts1 = Timestamp { seconds = 42L; label = "test" }
        val ts2 = Timestamp { seconds = 42L; label = "test" }
        val ts3 = Timestamp { seconds = 42L; label = "different" }

        assertEquals(ts1, ts2)
        assertEquals(ts1.hashCode(), ts2.hashCode())
        assertNotEquals(ts1, ts3)
    }

    @Test
    fun testWktCrossReferences() {
        val ts = Timestamp { seconds = 1L; label = "ts" }
        val dur = Duration { millis = 10L; start = ts }
        val any = Any { typeUrl = "url"; duration = dur }
        val sv = StringValue { value = "sv"; this.ts = ts }
        val fo = FieldOptions { deprecated = true; defaultValue = sv }
        val dp = DescriptorProto { name = "dp"; options = fo }

        assertEquals(1L, dur.start.seconds)
        assertEquals(10L, any.duration.millis)
        assertEquals(1L, any.duration.start.seconds)
        assertEquals("sv", fo.defaultValue.value)
        assertEquals(1L, fo.defaultValue.ts.seconds)
        assertEquals("dp", dp.name)
        assertEquals(true, dp.options.deprecated)
        assertEquals("sv", dp.options.defaultValue.value)
    }

    // Tests mixing local shadowed types with actual WKT types

    @Test
    fun testMixedWktAndLocal() {
        // Local shadowed types
        val localTs = Timestamp { seconds = 1L; label = "local" }
        val localDur = Duration { millis = 100L }
        val localAny = Any { typeUrl = "local.any" }
        val localEmpty = Empty { placeholder = "local" }
        val localValue = Value { data = "local val" }
        val localStruct = Struct {
            fields = mapOf("k" to localValue)
            empty = localEmpty
        }
        val localBool = BoolValue { value = true }
        val localSv = StringValue { value = "local sv" }

        // Actual WKT types (FQN because local names shadow them)
        val wktTs = com.google.protobuf.kotlin.Timestamp { seconds = 2L; nanos = 500 }
        val wktDur = com.google.protobuf.kotlin.Duration { seconds = 60L; nanos = 0 }
        val wktAny = com.google.protobuf.kotlin.Any { typeUrl = "type.googleapis.com/wkt" }
        val wktEmpty = com.google.protobuf.kotlin.Empty {}
        val wktValue = com.google.protobuf.kotlin.Value {
            kind = com.google.protobuf.kotlin.Value.Kind.StringValue("wkt string")
        }
        val wktStruct = com.google.protobuf.kotlin.Struct {
            fields = mapOf("wkt_key" to wktValue)
        }
        val wktBool = com.google.protobuf.kotlin.BoolValue { value = false }
        val wktSv = com.google.protobuf.kotlin.StringValue { value = "wkt sv" }
        val wktFieldMask = com.google.protobuf.kotlin.FieldMask { paths = listOf("f1", "f2") }

        val msg = MixedWktAndLocal {
            this.localTs = localTs
            this.localDur = localDur
            this.localAny = localAny
            this.localEmpty = localEmpty
            this.localValue = localValue
            this.localStruct = localStruct
            this.localBool = localBool
            this.localStringValue = localSv
            this.wktTs = wktTs
            this.wktDur = wktDur
            this.wktAny = wktAny
            this.wktEmpty = wktEmpty
            this.wktValue = wktValue
            this.wktStruct = wktStruct
            this.wktBool = wktBool
            this.wktStringValue = wktSv
            this.wktFieldMask = wktFieldMask
        }

        // Verify local types
        assertEquals(1L, msg.localTs.seconds)
        assertEquals("local", msg.localTs.label)
        assertEquals(100L, msg.localDur.millis)
        assertEquals("local.any", msg.localAny.typeUrl)
        assertEquals("local", msg.localEmpty.placeholder)
        assertEquals("local val", msg.localValue.data)
        assertEquals("local val", msg.localStruct.fields["k"]?.data)
        assertEquals(true, msg.localBool.value)
        assertEquals("local sv", msg.localStringValue.value)

        // Verify WKT types
        assertEquals(2L, msg.wktTs.seconds)
        assertEquals(500, msg.wktTs.nanos)
        assertEquals(60L, msg.wktDur.seconds)
        assertEquals(0, msg.wktDur.nanos)
        assertEquals("type.googleapis.com/wkt", msg.wktAny.typeUrl)
        assertEquals(false, msg.wktBool.value)
        assertEquals("wkt sv", msg.wktStringValue.value)
        assertEquals(listOf("f1", "f2"), msg.wktFieldMask.paths)

        // Verify WKT Value kind
        val kind = msg.wktValue.kind as com.google.protobuf.kotlin.Value.Kind.StringValue
        assertEquals("wkt string", kind.value)
    }

    @Test
    fun testMixedNesting() {
        val localTs = Timestamp { seconds = 10L; label = "local nested" }
        val wktTs = com.google.protobuf.kotlin.Timestamp { seconds = 20L; nanos = 100 }

        val localDur = Duration { millis = 500L }
        val wktDur = com.google.protobuf.kotlin.Duration { seconds = 5L; nanos = 0 }

        val localAny = Any { typeUrl = "local.nested.any" }
        val wktAny = com.google.protobuf.kotlin.Any { typeUrl = "type.googleapis.com/nested" }

        val localSv = StringValue { value = "local sv nested" }
        val wktSv = com.google.protobuf.kotlin.StringValue { value = "wkt sv nested" }

        val localMask = FieldMask {
            paths = listOf("local.path")
            createdAt = localTs
        }
        val wktMask = com.google.protobuf.kotlin.FieldMask { paths = listOf("wkt.path") }

        val deep = MixedNesting.Inner.DeepInner {
            this.localSv = localSv
            this.wktSv = wktSv
            this.localMask = localMask
            this.wktMask = wktMask
        }

        val inner = MixedNesting.Inner {
            this.localDur = localDur
            this.wktDur = wktDur
            this.localAny = localAny
            this.wktAny = wktAny
            this.deep = deep
        }

        val msg = MixedNesting {
            this.localTs = localTs
            this.wktTs = wktTs
            this.inner = inner
            this.wktTsList = listOf(
                com.google.protobuf.kotlin.Timestamp { seconds = 1L; nanos = 0 },
                com.google.protobuf.kotlin.Timestamp { seconds = 2L; nanos = 0 },
            )
            this.localTsList = listOf(
                Timestamp { seconds = 100L; label = "l1" },
                Timestamp { seconds = 200L; label = "l2" },
            )
            this.wktAnyMap = mapOf(
                "w" to com.google.protobuf.kotlin.Any { typeUrl = "type.googleapis.com/map" }
            )
            this.localAnyMap = mapOf(
                "l" to Any { typeUrl = "local.map" }
            )
        }

        // Verify local types at root
        assertEquals(10L, msg.localTs.seconds)
        assertEquals("local nested", msg.localTs.label)

        // Verify WKT types at root
        assertEquals(20L, msg.wktTs.seconds)
        assertEquals(100, msg.wktTs.nanos)

        // Verify inner local
        assertEquals(500L, msg.inner.localDur.millis)
        assertEquals("local.nested.any", msg.inner.localAny.typeUrl)

        // Verify inner WKT
        assertEquals(5L, msg.inner.wktDur.seconds)
        assertEquals("type.googleapis.com/nested", msg.inner.wktAny.typeUrl)

        // Verify deep inner local
        assertEquals("local sv nested", msg.inner.deep.localSv.value)
        assertEquals(listOf("local.path"), msg.inner.deep.localMask.paths)
        assertEquals(10L, msg.inner.deep.localMask.createdAt.seconds)

        // Verify deep inner WKT
        assertEquals("wkt sv nested", msg.inner.deep.wktSv.value)
        assertEquals(listOf("wkt.path"), msg.inner.deep.wktMask.paths)

        // Verify repeated WKT vs local
        assertEquals(2, msg.wktTsList.size)
        assertEquals(1L, msg.wktTsList[0].seconds)
        assertEquals(2L, msg.wktTsList[1].seconds)

        assertEquals(2, msg.localTsList.size)
        assertEquals(100L, msg.localTsList[0].seconds)
        assertEquals("l1", msg.localTsList[0].label)
        assertEquals(200L, msg.localTsList[1].seconds)

        // Verify map WKT vs local
        assertEquals("type.googleapis.com/map", msg.wktAnyMap["w"]?.typeUrl)
        assertEquals("local.map", msg.localAnyMap["l"]?.typeUrl)
    }

    @Test
    fun testMixedOneof() {
        val msgLocalTs = MixedOneof {
            content = MixedOneof.Content.LocalTs(Timestamp {
                seconds = 1L; label = "oneof local"
            })
        }

        val msgWktTs = MixedOneof {
            content = MixedOneof.Content.WktTs(com.google.protobuf.kotlin.Timestamp {
                seconds = 2L; nanos = 999
            })
        }

        val msgLocalDur = MixedOneof {
            content = MixedOneof.Content.LocalDur(Duration {
                millis = 100L
            })
        }

        val msgWktDur = MixedOneof {
            content = MixedOneof.Content.WktDur(com.google.protobuf.kotlin.Duration {
                seconds = 30L; nanos = 0
            })
        }

        val msgLocalAny = MixedOneof {
            content = MixedOneof.Content.LocalAny(Any {
                typeUrl = "local.oneof"
            })
        }

        val msgWktAny = MixedOneof {
            content = MixedOneof.Content.WktAny(com.google.protobuf.kotlin.Any {
                typeUrl = "type.googleapis.com/oneof"
            })
        }

        // Verify local variants
        val localTs = msgLocalTs.content as MixedOneof.Content.LocalTs
        assertEquals(1L, localTs.value.seconds)
        assertEquals("oneof local", localTs.value.label)

        val localDur = msgLocalDur.content as MixedOneof.Content.LocalDur
        assertEquals(100L, localDur.value.millis)

        val localAny = msgLocalAny.content as MixedOneof.Content.LocalAny
        assertEquals("local.oneof", localAny.value.typeUrl)

        // Verify WKT variants
        val wktTs = msgWktTs.content as MixedOneof.Content.WktTs
        assertEquals(2L, wktTs.value.seconds)
        assertEquals(999, wktTs.value.nanos)

        val wktDur = msgWktDur.content as MixedOneof.Content.WktDur
        assertEquals(30L, wktDur.value.seconds)

        val wktAny = msgWktAny.content as MixedOneof.Content.WktAny
        assertEquals("type.googleapis.com/oneof", wktAny.value.typeUrl)
    }

    @Test
    fun testMixedTypesNotEqual() {
        // Local Timestamp has different fields than WKT Timestamp -
        // they are completely different types
        val localTs = Timestamp { seconds = 42L; label = "local" }
        val wktTs = com.google.protobuf.kotlin.Timestamp { seconds = 42L; nanos = 0 }

        // They should be different types entirely (no equals possible)
        assertNotEquals<kotlin.Any>(localTs, wktTs)
    }
}
