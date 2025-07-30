/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.test.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TestProtos {

    private fun <T : Any> decodeEncode(
        msg: T,
        enc: T.(WireEncoder) -> Unit,
        dec: (WireDecoder) -> T?
    ): T? {
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)

        msg.enc(encoder)
        encoder.flush()

        return WireDecoder(buffer).use {
            dec(it)
        }
    }


    @Test
    fun testAllPrimitiveProto() {
        val msg = AllPrimitives {
            double = 3.0
        }

        val decoded = decodeEncode(msg, { encodeWith(it) }, AllPrimitives::decodeWith)

        assertEquals(msg.double, decoded?.double)
    }

    @Test
    fun testRepeatedProto() {
        val msg = Repeated {
            listFixed32 = listOf(1, 2, 3).map { it.toUInt() }
            listInt32 = listOf(4, 5, 6)
            listString = listOf("a", "b", "c")
        }

        val decoded = decodeEncode(msg, { encodeWith(it) }, Repeated::decodeWith)

        assertEquals(msg.listInt32, decoded?.listInt32)
        assertEquals(msg.listFixed32, decoded?.listFixed32)
        assertEquals(msg.listString, decoded?.listString)
    }

}