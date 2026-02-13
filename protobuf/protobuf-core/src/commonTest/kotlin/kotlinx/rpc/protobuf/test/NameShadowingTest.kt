/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import name_shadowing.Collection
import name_shadowing.ComplexNesting
import name_shadowing.Int
import name_shadowing.List
import name_shadowing.Map
import name_shadowing.MyClass1
import name_shadowing.MyClass2
import name_shadowing.RecursiveWithShadowing
import name_shadowing.ShadowedOneof
import name_shadowing.String
import name_shadowing.copy
import name_shadowing.invoke
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
        assertEquals(0, Collection.COLLECTION_UNSPECIFIED.number)
        assertEquals(1, Collection.COLLECTION_LIST.number)
        assertEquals(2, Collection.COLLECTION_MAP.number)
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
}
