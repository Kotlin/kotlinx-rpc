/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlinx.rpc.protobuf.internal.WireTag
import kotlinx.rpc.protobuf.internal.WireType
import kotlinx.rpc.protobuf.internal.decodeWire
import kotlinx.rpc.protobuf.internal.encodeWire
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WireTagTest {
    @Test
    fun readsEveryWireTypeAtFieldNumberBoundaries() {
        for (field in listOf(1, 15, 16, 536_870_911)) {
            for (type in WireType.VARINT..WireType.FIXED32) {
                val tag = WireTag(field, type)
                val buffer = Buffer()
                encodeWire(buffer) { it.writeTag(tag) }
                decodeWire(buffer) { decoder ->
                    val decoded = decoder.readTag()
                    assertEquals(tag, decoded)
                    assertEquals(field, decoded.fieldNr)
                    assertEquals(type, decoded.wireType)
                    assertFalse(decoded.isEnd)
                    assertTrue(decoder.readTag().isEnd)
                }
            }
        }
    }

    @Test
    fun emptyInputReturnsZeroEndMarker() {
        decodeWire(Buffer()) { decoder ->
            repeat(2) {
                val tag = decoder.readTag()
                assertTrue(tag.isEnd)
                assertEquals(WireTag.END, tag)
                assertEquals(0, tag.raw)
                assertEquals(0, tag.fieldNr)
            }
        }
    }

    @Test
    fun rejectsZeroFieldNumbersAndInvalidWireTypes() {
        for (raw in (0..7) + listOf(0x0E, 0x0F, -2, -1)) {
            val buffer = Buffer()
            encodeWire(buffer) { it.writeTag(WireTag(raw)) }
            assertFailsWith<ProtobufDecodingException> {
                decodeWire(buffer) { it.readTag() }
            }
        }
    }

    @Test
    fun negativeRawValueUsesAnUnsignedVarint() {
        val buffer = Buffer()
        encodeWire(buffer) { it.writeTag(WireTag(-3)) }
        assertContentEquals(bytes(0xFD, 0xFF, 0xFF, 0xFF, 0x0F), buffer.readByteArray())
    }
}
