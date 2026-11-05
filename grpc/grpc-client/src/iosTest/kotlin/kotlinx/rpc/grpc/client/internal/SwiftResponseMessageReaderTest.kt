/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.grpc.client.internal

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.kotlinx.serialization.asMarshallerResolver
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SwiftResponseMessageReaderTest {
    @Test
    fun kotlinxSerializationResponseCopiesFromScopedBytes() {
        val bytes = "\"hello, swift\"".encodeToByteArray()
        @Suppress("UNCHECKED_CAST")
        val marshaller = Json.asMarshallerResolver().resolveOrNull(typeOf<String>()) as GrpcMarshaller<String>
        lateinit var reader: SwiftResponseMessageReader

        bytes.usePinned { pinned ->
            reader = SwiftResponseMessageReader(pinned.addressOf(0), bytes.size)
            assertEquals("hello, swift", reader.use { marshaller.decode(it) })
        }

        assertFailsWith<IllegalStateException> { reader.readByte() }
        assertFailsWith<IllegalStateException> { reader.asSource() }
        assertFailsWith<IllegalStateException> { reader.accessBuffer {} }
    }

    @Test
    fun emptyResponseNeedsNoPointer() {
        val reader = SwiftResponseMessageReader(null, 0)
        assertContentEquals(byteArrayOf(), reader.use { it.readByteArray() })
        assertFailsWith<IllegalStateException> { reader.readByte() }
        assertFailsWith<IllegalArgumentException> { SwiftResponseMessageReader(null, 1) }
    }

    @Test
    fun requestWriterPreservesEmptyAndLargeMessages() {
        for (expected in listOf(byteArrayOf(), ByteArray(4 * 1024 * 1024 - 1024) { it.toByte() })) {
            val writer = SwiftRequestMessageWriter(expected.size)
            writer.write(expected)
            assertEquals(0, writer.remaining)
            val request = writer.requestBytes()
            assertEquals(expected.size.toLong(), request.length())
            request.withWritableBytes { bytes, count ->
                SwiftResponseMessageReader(bytes, count.toInt()).use { reader ->
                    assertContentEquals(expected, reader.readByteArray())
                }
            }
        }
    }

    @Test
    fun metadataCopyOwnsItsBytesAndRejectsInvalidRegions() {
        val expected = byteArrayOf(0, 127, -128, -1)
        val source = expected.copyOf()
        val result = source.usePinned { copySwiftByteArray(it.addressOf(0), source.size.toLong()) }
        source.fill(0)
        assertContentEquals(expected, result)
        assertContentEquals(byteArrayOf(), copySwiftByteArray(null, 0))
        assertFailsWith<IllegalArgumentException> { copySwiftByteArray(null, 1) }
        assertFailsWith<IllegalArgumentException> { copySwiftByteArray(null, -1) }
        assertFailsWith<IllegalArgumentException> { copySwiftByteArray(null, Int.MAX_VALUE.toLong() + 1) }
    }
}
