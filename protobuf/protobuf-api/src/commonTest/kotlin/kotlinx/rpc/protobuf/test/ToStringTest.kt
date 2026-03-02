/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import ToString
import invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class ToStringTest {
    @Test
    fun noOptionals() {
        val message = ToString {
            text = "hello"
            bytes = byteArrayOf(1, 2, 3)
        }

        assertEquals("""
            ToString(
                text=hello,
                bytes=[1, 2, 3],
                optionalBytes=<unset>,
                nested=<unset>,
                enum=<unset>,
                list=[],
                map={},
                oneof=null,
            )
        """.trimIndent(), message.toString())
    }

    @Test
    fun withOptionals() {
        val message = ToString {
            text = "hello"
            bytes = byteArrayOf(1, 2, 3)
            optionalBytes = byteArrayOf(1, 2, 4)
            nested = ToString.Nested { }
            enum = ToString.SomeEnum.VALUE1
        }

        assertEquals("""
            ToString(
                text=hello,
                bytes=[1, 2, 3],
                optionalBytes=[1, 2, 4],
                nested=ToString.Nested(
                    recursive=<unset>,
                ),
                enum=VALUE1,
                list=[],
                map={},
                oneof=null,
            )
        """.trimIndent(), message.toString())
    }

    @Test
    fun list() {
        val message = ToString {
            text = "hello"
            bytes = byteArrayOf(1, 2, 3)
            list = listOf("a", "b", "c")
        }
        assertEquals("""
            ToString(
                text=hello,
                bytes=[1, 2, 3],
                optionalBytes=<unset>,
                nested=<unset>,
                enum=<unset>,
                list=[a, b, c],
                map={},
                oneof=null,
            )
        """.trimIndent(), message.toString())
    }

    @Test
    fun map() {
        val message = ToString {
            text = "hello"
            bytes = byteArrayOf(1, 2, 3)
            map = mapOf(1 to 1, 2 to 2, 3 to 3)
        }
        assertEquals("""
            ToString(
                text=hello,
                bytes=[1, 2, 3],
                optionalBytes=<unset>,
                nested=<unset>,
                enum=<unset>,
                list=[],
                map={1=1, 2=2, 3=3},
                oneof=null,
            )
        """.trimIndent(), message.toString())
    }

    @Test
    fun oneOf() {
        val message = ToString {
            text = "hello"
            bytes = byteArrayOf(1, 2, 3)
            oneof = ToString.Oneof.Option1("option1_value")
        }
        assertEquals("""
            ToString(
                text=hello,
                bytes=[1, 2, 3],
                optionalBytes=<unset>,
                nested=<unset>,
                enum=<unset>,
                list=[],
                map={},
                oneof=Option1(value=option1_value),
            )
        """.trimIndent(), message.toString())

        val message2 = ToString {
            text = "hello"
            bytes = byteArrayOf(1, 2, 3)
            oneof = ToString.Oneof.Option3(42)
        }
        assertEquals("""
            ToString(
                text=hello,
                bytes=[1, 2, 3],
                optionalBytes=<unset>,
                nested=<unset>,
                enum=<unset>,
                list=[],
                map={},
                oneof=Option3(value=42),
            )
        """.trimIndent(), message2.toString())
    }

    @Test
    fun recursive() {
        val message = ToString {
            text = "hello"
            bytes = byteArrayOf(1, 2, 3)
            nested = ToString.Nested {
                recursive = ToString.Nested {
                    recursive = ToString.Nested { }
                }
            }
        }

        assertEquals("""
            ToString(
                text=hello,
                bytes=[1, 2, 3],
                optionalBytes=<unset>,
                nested=ToString.Nested(
                    recursive=ToString.Nested(
                        recursive=ToString.Nested(
                            recursive=<unset>,
                        ),
                    ),
                ),
                enum=<unset>,
                list=[],
                map={},
                oneof=null,
            )
        """.trimIndent(), message.toString())
    }
}
