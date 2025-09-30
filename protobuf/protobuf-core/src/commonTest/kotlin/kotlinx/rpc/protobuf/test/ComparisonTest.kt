/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import Equals
import invoke
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ComparisonTest {
    @Test
    fun equal() {
        val msg1 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        val msg2 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())
    }

    @Test
    fun equalWithOptional() {
        val msg1 = Equals {
            str1 = "hello"
            str2 = "world"
            bytes1 = byteArrayOf(1, 2, 3)
            bytes2 = byteArrayOf(1, 2, 4)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        val msg2 = Equals {
            str1 = "hello"
            str2 = "world"
            bytes1 = byteArrayOf(1, 2, 3)
            bytes2 = byteArrayOf(1, 2, 4)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())
    }

    @Test
    fun equalWithUnsetOptionalReference() {
        val msg1 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            nested = Equals.Nested {
                content = "hello"
            }
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        val msg2 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        assertNotEquals(msg1, msg2)
        assertNotEquals(msg1.hashCode(), msg2.hashCode())
    }

    @Test
    fun notEqual() {
        val msg1 = Equals {
            str1 = "hello1"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        val msg2 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        assertNotEquals(msg1, msg2)
        assertNotEquals(msg1.hashCode(), msg2.hashCode())
    }

    @Test
    fun differentOneOf() {
        val msg1 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            oneof = Equals.Oneof.Option1(42)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        val msg2 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            oneof = Equals.Oneof.Option2(42)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        assertNotEquals(msg1, msg2)
        assertNotEquals(msg1.hashCode(), msg2.hashCode())
    }

    @Test
    fun sameOneOf() {
        val msg1 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            oneof = Equals.Oneof.Option1(42)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        val msg2 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            oneof = Equals.Oneof.Option1(42)
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())
    }

    @Test
    fun sameEnum() {
        val msg1 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum = Equals.SomeEnum.VALUE1
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        val msg2 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum = Equals.SomeEnum.VALUE1
            someEnum2 = Equals.SomeEnum.VALUE1
        }

        assertEquals(msg1, msg2)
        assertEquals(msg1.hashCode(), msg2.hashCode())
    }

    @Test
    fun differentEnum() {
        val msg1 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum = Equals.SomeEnum.VALUE1
            someEnum2 = Equals.SomeEnum.VALUE2
        }

        val msg2 = Equals {
            str1 = "hello"
            bytes1 = byteArrayOf(1, 2, 3)
            someEnum = Equals.SomeEnum.VALUE2
            someEnum2 = Equals.SomeEnum.VALUE2
        }

        assertNotEquals(msg1, msg2)
        assertNotEquals(msg1.hashCode(), msg2.hashCode())
    }
}
