/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.test.PrimitiveTest
import kotlinx.rpc.grpc.test.decodeWith
import kotlinx.rpc.grpc.test.encodeWith
import kotlinx.rpc.grpc.test.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAllPrimitive {


    @Test
    fun testAllPrimitiveProto() {
        val msg = PrimitiveTest {
            double = 3.0
            int32Opt = 12
        }

        val buffer = Buffer()
        val encoder = WireEncoder(buffer)

        msg.encodeWith(encoder)
        encoder.flush()

        val decoded = WireDecoder(buffer).use {
            PrimitiveTest.decodeWith(it)
        }

        assertEquals(msg.double, decoded?.double)
        assertEquals(msg.int32Opt, decoded?.int32Opt)
    }
}