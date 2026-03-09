/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalRpcApi::class, ExperimentalStdlibApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.io.readByteArray
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.protobuf.internal.InternalExtensionDescriptor
import kotlin.test.Test
import kotlin.test.assertEquals

class ProtobufSizeCalculationTest {
    private val stringExtension = InternalExtensionDescriptor.string(
        fieldNumber = 99,
        name = "testString",
        extendee = ExtensionBase::class,
    )

    @Test
    fun testRepeatedMessageSizeIsCorrect() {
        val msg = Repeated {
            listMessage = List(10) { i ->
                Repeated.Other { a = i }
            }
        }

        val internalMessage = msg as RepeatedInternal
        val declaredSize = internalMessage._size

        val bytes = RepeatedInternal.MARSHALLER.encode(msg).readByteArray()
        val actualSize = bytes.size

        assertEquals(
            actualSize,
            declaredSize,
            "The declared _size ($declaredSize) should match the actual encoded size ($actualSize) for repeated messages. Actual bytes: ${bytes.toHexString()}"
        )
    }

    @Test
    fun testRepeatedStringSizeIsCorrect() {
        val msg = Repeated {
            listString = List(10) { i -> "item-$i" }
        }

        val internalMessage = msg as RepeatedInternal
        val declaredSize = internalMessage._size

        val bytes = RepeatedInternal.MARSHALLER.encode(msg).readByteArray()
        val actualSize = bytes.size

        assertEquals(
            actualSize,
            declaredSize,
            "The declared _size ($declaredSize) should match the actual encoded size ($actualSize) for repeated strings. Actual bytes: ${bytes.toHexString()}"
        )
    }

    @Test
    fun testMapSizeIsCorrect() {
        val msg = TestMap {
            primitives = mapOf("one" to 1, "two" to 2, "three" to 3)
        }

        val internalMessage = msg as TestMapInternal
        val declaredSize = internalMessage._size

        val bytes = TestMapInternal.MARSHALLER.encode(msg).readByteArray()
        val actualSize = bytes.size

        assertEquals(
            actualSize,
            declaredSize,
            "The declared _size ($declaredSize) should match the actual encoded size ($actualSize) for map entries. Actual bytes: ${bytes.toHexString()}"
        )
    }

    @Test
    fun testExtensionInt32SizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                int32 = 42
            },
            label = "int32 extension",
        )
    }

    @Test
    fun testExtensionEnumSizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                enum = MyEnum.THREE
            },
            label = "enum extension",
        )
    }

    @Test
    fun testExtensionMessageSizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                msg = AllPrimitives {
                    int32 = 123
                    string = "test"
                }
            },
            label = "message extension",
        )
    }

    @Test
    fun testNestedExtensionMessageSizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                subExt = ExtensionBase {
                    int32 = 123
                }
            },
            label = "nested message extension",
        )
    }

    @Test
    fun testLengthDelimitedScalarExtensionSizeIsCorrect() {
        val message = ExtensionBaseInternal().apply {
            setExtensionValue(stringExtension, "hello".repeat(200))
        }

        assertExtensionBaseSizeMatchesEncoding(
            message = message,
            label = "string extension",
        )
    }

    @Test
    fun testPackedRepeatedInt32ExtensionSizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                repeatedInt32 = listOf(1, 2, 3)
            },
            label = "packed repeated int32 extension",
        )
    }

    @Test
    fun testPackedRepeatedEnumExtensionSizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                repeatedEnum = listOf(MyEnum.ONE, MyEnum.THREE)
            },
            label = "packed repeated enum extension",
        )
    }

    @Test
    fun testRepeatedMessageExtensionSizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                repeatedMsg = listOf(
                    AllPrimitives {
                        int32 = 1
                    },
                    AllPrimitives {
                        string = "two"
                    },
                )
            },
            label = "repeated message extension",
        )
    }

    @Test
    fun testGroupExtensionSizeIsCorrect() {
        assertExtensionBaseSizeMatchesEncoding(
            message = ExtensionBase {
                testgroup = TestGroup {
                    int32 = 123
                    string = "group-string"
                }
            },
            label = "group extension",
        )
    }

    @Test
    fun testNestedDefinedExtensionSizeIsCorrect() {
        val message = ExtensionBase {
            conflicting = "apfelstrudel"
            with(MessageScopedExtensions) {
                conflicting = 121
                with(MessageScopedExtensions.MoreNestedExtensions) {
                    conflicting = 122
                }
            }
        }

        assertExtensionBaseSizeMatchesEncoding(
            message = message,
            label = "nested-defined conflicting extensions",
        )
    }

    private fun assertExtensionBaseSizeMatchesEncoding(message: ExtensionBase, label: String) {
        val internalMessage = message.asInternal()
        val declaredSize = internalMessage._size

        val bytes = ExtensionBaseInternal.MARSHALLER.encode(message).readByteArray()
        val actualSize = bytes.size

        assertEquals(
            actualSize,
            declaredSize,
            "The declared _size ($declaredSize) should match the actual encoded size ($actualSize) for $label. Actual bytes: ${bytes.toHexString()}"
        )
    }
}
