/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package proto2_unittest

import kotlinx.io.Buffer
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.internal.InternalMessage
import kotlinx.rpc.protobuf.internal.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.WireDecoder
import kotlinx.rpc.protobuf.internal.checkForPlatformDecodeException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Tests for the message recursion limit in [WireDecoder].
 */
class RecursionLimitTest {

    /**
     * A minimal InternalMessage for testing.
     */
    private class SimpleMsg : InternalMessage(fieldsWithPresence = 0) {
        var nested: SimpleMsg? = null
        var value: Int = 0

        override val _size: Int get() = 0
        override val _unknownFields: Buffer = Buffer()
    }

    /**
     * Builds raw protobuf bytes for a nested message structure.
     * Schema: field 1 = sub-message, field 2 = int32.
     * The innermost message has field 2 = 42.
     */
    private fun buildNestedBytes(depth: Int): ByteArray {
        // Innermost message: tag(2, VARINT) + varint(42) = [0x10, 0x2A]
        var inner = byteArrayOf(0x10, 0x2A)

        repeat(depth) {
            // Wrap in a sub-message: tag(1, LENGTH_DELIMITED) + length + inner
            val tag: Byte = 0x0A // (1 << 3) | 2
            val len = inner.size
            // Simple varint encoding for length (works for len < 128)
            inner = byteArrayOf(tag, len.toByte()) + inner
        }

        return inner
    }

    private fun decodeNested(bytes: ByteArray, recursionLimit: Int): SimpleMsg {
        val buffer = Buffer()
        buffer.write(bytes)
        WireDecoder(buffer).use { decoder ->
            decoder.recursionLimit = recursionLimit
            val msg = SimpleMsg()
            checkForPlatformDecodeException {
                decodeWith(msg, decoder)
            }
            return msg
        }
    }

    private fun decodeWith(msg: SimpleMsg, decoder: WireDecoder) {
        while (true) {
            val tag = decoder.readTag() ?: break
            when (tag.fieldNr) {
                1 -> {
                    if (msg.nested == null) {
                        msg.nested = SimpleMsg()
                    }
                    decoder.readMessage(msg.nested!!) { nested, dec ->
                        decodeWith(nested, dec)
                    }
                }
                2 -> {
                    msg.value = decoder.readInt32()
                }
                else -> decoder.skipUnknownField(tag)
            }
        }
    }

    @Test
    fun testRecursionWithinLimit() {
        val depth = 5
        val bytes = buildNestedBytes(depth)
        val msg = decodeNested(bytes, recursionLimit = 10)

        // Walk down to the deepest level
        var current: SimpleMsg? = msg
        repeat(depth) {
            current = current?.nested
        }
        assertEquals(42, current?.value)
    }

    @Test
    fun testRecursionLimitExceeded() {
        val depth = 10
        val bytes = buildNestedBytes(depth)

        assertFailsWith<ProtobufDecodingException> {
            decodeNested(bytes, recursionLimit = 5)
        }
    }

    @Test
    fun testRecursionLimitExactBoundary() {
        val depth = 5

        // Exactly at the limit should succeed
        val msg = decodeNested(buildNestedBytes(depth), recursionLimit = depth)
        var current: SimpleMsg? = msg
        repeat(depth) {
            current = current?.nested
        }
        assertEquals(42, current?.value)

        // One below the limit should fail
        assertFailsWith<ProtobufDecodingException> {
            decodeNested(buildNestedBytes(depth), recursionLimit = depth - 1)
        }
    }

    @Test
    fun testDefaultRecursionLimit() {
        val buffer = Buffer()
        WireDecoder(buffer).use { decoder ->
            assertEquals(ProtoConfig.DEFAULT_RECURSION_LIMIT, decoder.recursionLimit)
            assertEquals(100, decoder.recursionLimit)
        }
    }
}
