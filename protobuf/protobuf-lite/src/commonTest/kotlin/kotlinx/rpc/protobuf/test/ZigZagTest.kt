/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.rpc.protobuf.internal.SInt32
import kotlinx.rpc.protobuf.internal.SInt64
import kotlinx.rpc.protobuf.internal.ZigZag
import kotlin.test.Test
import kotlin.test.assertEquals

class ZigZagTest {
    @Test
    fun encode32() {
        assertEquals(0, ZigZag.encode(SInt32(0)).value)
        assertEquals(1, ZigZag.encode(SInt32(-1)).value)
        assertEquals(2, ZigZag.encode(SInt32(1)).value)
        assertEquals(3, ZigZag.encode(SInt32(-2)).value)
        assertEquals(-2, ZigZag.encode(SInt32(Int.MAX_VALUE)).value)
        assertEquals(-1, ZigZag.encode(SInt32(Int.MIN_VALUE)).value)
    }

    @Test
    fun encode64() {
        assertEquals(0L, ZigZag.encode(SInt64(0L)).value)
        assertEquals(1L, ZigZag.encode(SInt64(-1L)).value)
        assertEquals(2L, ZigZag.encode(SInt64(1L)).value)
        assertEquals(-2L, ZigZag.encode(SInt64(Long.MAX_VALUE)).value)
        assertEquals(-1L, ZigZag.encode(SInt64(Long.MIN_VALUE)).value)
    }

    @Test
    fun roundTrip() {
        for (value in listOf(0, -1, 1, -2, Int.MAX_VALUE, Int.MIN_VALUE)) {
            assertEquals(value, ZigZag.decode(ZigZag.encode(SInt32(value))).value)
        }
        for (value in listOf(0L, -1L, 1L, Long.MIN_VALUE, Long.MAX_VALUE)) {
            assertEquals(value, ZigZag.decode(ZigZag.encode(SInt64(value))).value)
        }
    }
}
