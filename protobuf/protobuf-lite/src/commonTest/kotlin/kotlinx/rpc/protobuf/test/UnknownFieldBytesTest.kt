/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.io.Buffer
import kotlinx.io.UnsafeIoApi
import kotlinx.io.readByteArray
import kotlinx.io.unsafe.UnsafeBufferOperations
import kotlinx.rpc.grpc.marshaller.decodeFromSource
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf
import kotlinx.rpc.protobuf.ProtoConfig
import kotlinx.rpc.protobuf.ProtobufDecodingException
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class UnknownFieldBytesTest {
    @Test
    @OptIn(UnsafeIoApi::class)
    fun copyingUnknownFieldsPreservesInputIteration() {
        val payload = ByteArray(2048) { it.toByte() }
        val unknownBytes = bytes(0x12, 0x80, 0x10) + payload
        val input = Buffer()
        UnsafeBufferOperations.moveToTail(input, unknownBytes + bytes(0x08, 123))
        UnsafeBufferOperations.moveToTail(input, bytes(0x18, 0x81, 0x00, 0x08, 42))

        val decoded = grpcMarshallerOf<UnknownFieldsSubset>().decodeFromSource(input)
        assertEquals(42, decoded.field1)
        assertContentEquals(unknownBytes + bytes(0x18, 0x81, 0x00), decoded.asInternal()._unknownFields.readByteArray())
        assertTrue(input.exhausted())
    }

    @Test
    fun preservesOriginalBytesFromEveryReaderKind() {
        val original = unknownFieldBytes()
        val marshaller = grpcMarshallerOf<UnknownFieldsSubset>()
        for (kind in readerKinds) {
            val reader = kind.create(original)
            val decoded = marshaller.decode(reader)
            assertEquals(0, reader.remaining, kind.name)
            assertContentEquals(original, decoded.asInternal()._unknownFields.copy().readByteArray(), kind.name)
            assertContentEquals(original, marshaller.encodeToByteArray(decoded), kind.name)
        }
    }

    @Test
    fun discardsNonMinimalVarintsAndGroups() {
        val original = unknownFieldBytes()
        val marshaller = grpcMarshallerOf<UnknownFieldsSubset>(ProtoConfig { discardUnknownFields = true })
        for (kind in readerKinds) {
            val reader = kind.create(original)
            val decoded = marshaller.decode(reader)
            assertEquals(0, reader.remaining, kind.name)
            assertEquals(0L, decoded.asInternal()._unknownFields.size, kind.name)
            assertTrue(marshaller.encodeToByteArray(decoded).isEmpty(), kind.name)
        }
    }

    @Test
    fun malformedUnknownFieldsFailWithOrWithoutDiscard() {
        val malformed = listOf(
            bytes(0x10, 0x80),
            bytes(0x10) + ByteArray(10) { 0x80.toByte() },
            bytes(0x2D, 1, 2, 3),
            bytes(0x31, 1, 2, 3, 4, 5, 6, 7),
            bytes(0x22, 0x80, 0x01, 1, 2, 3),
            bytes(0x22, 0xFF, 0xFF, 0xFF, 0xFF, 0x0F),
            bytes(0x3B, 0x10, 1),
            bytes(0x3B, 0x44),
        )
        for (discard in listOf(false, true)) {
            val marshaller = grpcMarshallerOf<UnknownFieldsSubset>(ProtoConfig { discardUnknownFields = discard })
            for (input in malformed) {
                for (kind in readerKinds) {
                    assertFailsWith<ProtobufDecodingException>(kind.name) {
                        marshaller.decode(kind.create(input))
                    }
                }
            }
        }
    }

    @Test
    fun largePayloadIsPreservedFromEveryReaderKind() {
        val payload = ByteArray(20_000) { it.toByte() }
        val original = bytes(0x22, 0xA0, 0x9C, 0x01) + payload
        val marshaller = grpcMarshallerOf<UnknownFieldsSubset>()
        for (kind in readerKinds) {
            val reader = kind.create(original)
            val decoded = marshaller.decode(reader)
            assertEquals(0, reader.remaining, kind.name)
            assertContentEquals(original, marshaller.encodeToByteArray(decoded), kind.name)
        }
    }
}

/** Unknown fields with non-minimal varints, fixed-width values, and nested groups. */
internal fun unknownFieldBytes(): ByteArray = bytes(
    0x10, 0x81, 0x00,
    0x18, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0x01,
    0x22, 0x83, 0x00, 0x61, 0x62, 0x63,
    0x2D, 1, 2, 3, 4,
    0x31, 1, 2, 3, 4, 5, 6, 7, 8,
    0x3B, 0x08, 0x82, 0x00, 0x43, 0x4A, 0x81, 0x00, 0xFF, 0x44, 0x3C,
    0xFD, 0xFF, 0xFF, 0xFF, 0x0F, 1, 2, 3, 4,
)
