/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalRpcApi::class, ExperimentalStdlibApi::class)

package kotlinx.rpc.protobuf.test

import kotlinx.io.readByteArray
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlin.test.Test
import kotlin.test.assertEquals

class ProtobufSizeCalculationTest {

    @Test
    fun testRepeatedMessageSizeIsCorrect() {
        val msg = Repeated {
            listMessage = List(10) { i ->
                Repeated.Other { a = i }
            }
        }

        val internalMessage = msg as RepeatedInternal
        val declaredSize = internalMessage._size

        val bytes = RepeatedInternal.CODEC.encode(msg).readByteArray()
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

        val bytes = RepeatedInternal.CODEC.encode(msg).readByteArray()
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

        val bytes = TestMapInternal.CODEC.encode(msg).readByteArray()
        val actualSize = bytes.size

        assertEquals(
            actualSize,
            declaredSize,
            "The declared _size ($declaredSize) should match the actual encoded size ($actualSize) for map entries. Actual bytes: ${bytes.toHexString()}"
        )
    }
}
