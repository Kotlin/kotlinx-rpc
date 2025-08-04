/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.pb

import kotlinx.io.Buffer
import kotlinx.rpc.grpc.internal.MessageCodec
import kotlinx.rpc.grpc.test.common.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ProtosTest {

    private fun <M> decodeEncode(
        msg: M,
        codec: MessageCodec<M>
    ): M {
        val source = codec.encode(msg)
        return codec.decode(source)
    }

    @Test
    fun testAllPrimitiveProto() {
        val msg = AllPrimitives {
            int32 = 12
            int64 = 1234567890123456789L
            uint32 = 12345u
            uint64 = 1234567890123456789uL
            sint32 = -12
            sint64 = -1234567890123456789L
            fixed32 = 12345u
            fixed64 = 1234567890123456789uL
            sfixed32 = -12345
            sfixed64 = -1234567890123456789L
            bool = true
            float = 1.0f
            double = 3.0
            string = "test"
            bytes = byteArrayOf(1, 2, 3)
        }

        val msgObj = msg

        val decoded = decodeEncode(msgObj, AllPrimitivesInternal.CODEC)

        assertEquals(msg.double, decoded.double)
    }

    @Test
    fun testRepeatedProto() {
        val msg = Repeated {
            listFixed32 = listOf(1, 5, 3).map { it.toUInt() }
            listFixed32Packed = listOf(1, 2, 3).map { it.toUInt() }
            listInt32 = listOf(4, 7, 6)
            listInt32Packed = listOf(4, 5, 6)
            listString = listOf("a", "b", "c")
        }

        val decoded = decodeEncode(msg, RepeatedInternal.CODEC)

        assertEquals(msg.listInt32, decoded.listInt32)
        assertEquals(msg.listFixed32, decoded.listFixed32)
        assertEquals(msg.listString, decoded.listString)
    }

    @Test
    fun testPresenceCheckProto() {

        // Check a missing required field in a user-constructed message
        assertFailsWith<IllegalStateException>("PresenceCheck is missing required field: RequiredPresence") {
            PresenceCheck {}
        }

        // Test missing field during decoding of an encoded message
        val buffer = Buffer()
        val encoder = WireEncoder(buffer)
        encoder.writeFloat(2, 1f)
        encoder.flush()

        assertFailsWith<IllegalStateException>("PresenceCheck is missing required field: RequiredPresence") {
            PresenceCheckInternal.CODEC.decode(buffer)
        }
    }


}