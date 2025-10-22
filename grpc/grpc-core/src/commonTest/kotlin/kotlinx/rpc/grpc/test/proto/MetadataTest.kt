/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.contains
import kotlinx.rpc.grpc.copy
import kotlinx.rpc.grpc.get
import kotlinx.rpc.grpc.getAll
import kotlinx.rpc.grpc.getAllBinary
import kotlinx.rpc.grpc.getBinary
import kotlinx.rpc.grpc.keys
import kotlinx.rpc.grpc.plus
import kotlinx.rpc.grpc.remove
import kotlinx.rpc.grpc.removeAll
import kotlinx.rpc.grpc.removeAllBinary
import kotlinx.rpc.grpc.removeBinary
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoRequestInternal
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.protobuf.input.stream.asInputStream
import kotlinx.rpc.protobuf.input.stream.asSource
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MetadataTest : GrpcProtoTest() {

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `test send and receive headers and trailers`() = runTest {
        var clientHeaders: GrpcMetadata? = null
        val responseHeadersDef = CompletableDeferred<GrpcMetadata>()
        val responseTrailersDef = CompletableDeferred<GrpcMetadata>()

        // helper function to append metadata to GrpcMetadata
        fun GrpcMetadata.appendMetadata() {
            append("my-key", "my-value")
            appendBinary("my-key-bin", byteArrayOf(1, 2, 3))
            appendBinary("my-key-bin", byteArrayOf(4, 5, 6))
            append("my-multi-key", "my-value1")
            val protoMsg = EchoRequest { message = "My Proto Header" }
            appendBinary("my-proto-bin", EchoRequestInternal.CODEC.encode(protoMsg).asSource().readByteArray())
            append("my-multi-key", "my-value2")
            append("my-multi-key", "my-value3")
        }

        runGrpcTest(
            clientInterceptors = clientInterceptor {
                requestHeaders.appendMetadata()
                onHeaders { headers -> responseHeadersDef.complete(headers) }
                onClose { _, trailers -> responseTrailersDef.complete(trailers) }
                proceed(it)
            },
            serverInterceptors = serverInterceptor {
                responseHeaders.appendMetadata()
                responseTrailers.appendMetadata()
                clientHeaders = this.requestHeaders
                proceed(it)
            }
        ) { unaryEcho(it) }

        // helper function to assert GrpcMetadata (headers and trailers)
        fun GrpcMetadata.assertValues() {
            // if user-agent is set, we expect 4 keys, otherwise 3 keys
            var expectedSize = 4
            // calculate expected size based on potentially present default headers
            if ("user-agent" in this) expectedSize++
            if ("content-type" in this) expectedSize++
            if ("grpc-accept-encoding" in this) expectedSize++
            if ("grpc-encoding" in this) expectedSize++
            assertEquals(expectedSize, keys().size)
            assertTrue { contains("my-key") }
            assertTrue { contains("my-key-bin") }
            assertTrue { contains("my-multi-key") }
            assertTrue { contains("my-proto-bin") }
            assertEquals("my-value", get("my-key"))
            assertContentEquals(byteArrayOf(4, 5, 6), getBinary("my-key-bin"))
            assertContentEquals(byteArrayOf(1, 2, 3), getAllBinary("my-key-bin")[0])
            assertEquals(listOf("my-value1", "my-value2", "my-value3"), getAll("my-multi-key"))
            assertEquals("my-value3", get("my-multi-key"))
            val proto = Buffer().apply {
                write(getBinary("my-proto-bin")!!)
            }
            val decodedProto = EchoRequestInternal.CODEC.decode(proto.asInputStream())
            assertEquals("My Proto Header", decodedProto.message)
        }


        clientHeaders!!.assertValues()
        responseHeadersDef.await().assertValues()
        responseTrailersDef.await().assertValues()
    }

    @Test
    fun `test invalid keys`() {
        val metadata = GrpcMetadata()
        assertFailsWith<IllegalArgumentException> { metadata["invalid-key-bin"] }
        assertFailsWith<IllegalArgumentException> { metadata.getBinary("invalid-key") }
        assertFailsWith<IllegalArgumentException> { metadata.getAll("invalid-key-bin") }
        assertFailsWith<IllegalArgumentException> { metadata.getAllBinary("invalid-key") }
        assertFailsWith<IllegalArgumentException> { metadata.append("invalid-key-bin", "value") }
        assertFailsWith<IllegalArgumentException> { metadata.appendBinary("invalid-key", byteArrayOf(1)) }
        assertFailsWith<IllegalArgumentException> { metadata.remove("invalid-key-bin", "value") }
        assertFailsWith<IllegalArgumentException> { metadata.removeBinary("invalid-key", byteArrayOf(1)) }
        assertFailsWith<IllegalArgumentException> { metadata.removeAll("invalid-key-bin") }
        assertFailsWith<IllegalArgumentException> { metadata.removeAllBinary("invalid-key") }
        // space in key is not allowed
        assertFailsWith<IllegalArgumentException> { metadata.append(" my-key", "my-value") }
        // not alphanumeric key with .-_
        assertFailsWith<IllegalArgumentException> { metadata.append("my>key", "my-value") }
    }

    @Test
    fun `test remove`() {
        GrpcMetadata().apply {
            append("my-key", "my-value")
            assertTrue(remove("my-key", "my-value"))
            assertEquals(0, keys().size)
        }

        GrpcMetadata().apply {
            append("my-key", "my-value")
            append("my-key", "my-value2")
            assertEquals(2, removeAll("my-key").size)
            assertEquals(0, keys().size)
        }

        GrpcMetadata().apply {
            append("my-key", "my-value")
            append("my-key", "my-value2")
            assertEquals("my-value2", get("my-key"))
            remove("my-key", "my-value2")
            assertEquals("my-value", get("my-key"))
            assertEquals(1, keys().size)
            remove("my-key", "my-value")
            assertEquals(null, get("my-key"))
            assertEquals(0, keys().size)
        }

        GrpcMetadata().apply {
            val arr1 = byteArrayOf(1, 2, 3)
            val arr2 = byteArrayOf(4, 5, 6)
            appendBinary("my-key-bin", arr1)
            appendBinary("my-key-bin", arr2)
            assertEquals(arr2, getBinary("my-key-bin"))
            assertEquals(1, keys().size)
            removeBinary("my-key-bin", arr2)
            removeBinary("my-key-bin", arr1)
            assertEquals(null, getBinary("my-key-bin"))
            assertEquals(0, keys().size)
        }
    }

    @Test
    fun `test ascii replacement`() {
        GrpcMetadata().apply {
            append("my-key", "my-value你")
            assertEquals("my-value?", get("my-key"))
        }
    }

    @Test
    fun `test merge`() {
        val md1 = GrpcMetadata().apply {
            append("my-key", "my-value-1")
            appendBinary("my-key-bin", byteArrayOf(1, 2, 3))
            append("my-key-1", "my-value-1")
            appendBinary("my-key-1-bin", byteArrayOf(1, 2, 3))
            append("my-key-common", "my-value-common")
        }

        val md2 = GrpcMetadata().apply {
            append("my-key", "my-value-2")
            appendBinary("my-key-bin", byteArrayOf(4, 5, 6))
            append("my-key-2", "my-value-2")
            appendBinary("my-key-2-bin", byteArrayOf(4, 5, 6))
            append("my-key-common", "my-value-common")
        }

        val mdPlus = md1 + md2

        mdPlus.run {
            assertEquals(7, keys().size)
            assertEquals(listOf("my-value-1"), getAll("my-key-1"))
            assertEquals(listOf("my-value-2"), getAll("my-key-2"))
            assertEquals(listOf("my-value-1", "my-value-2"), getAll("my-key"))
            assertEquals(listOf("my-value-common", "my-value-common"), getAll("my-key-common"))

            assertContentEquals(byteArrayOf(1, 2, 3), getAllBinary("my-key-bin")[0])
            assertContentEquals(byteArrayOf(4, 5, 6), getAllBinary("my-key-bin")[1])
            assertContentEquals(byteArrayOf(1, 2, 3), getAllBinary("my-key-1-bin")[0])
            assertContentEquals(byteArrayOf(4, 5, 6), getAllBinary("my-key-2-bin")[0])
        }
    }

    @Test
    fun `test plusAssign operator`() {
        var md1 = GrpcMetadata().apply {
            append("my-key", "my-value-1")
        }

        val md2 = GrpcMetadata().apply {
            append("my-key", "my-value-2")
        }

        md1 += md2

        assertEquals(listOf("my-value-1", "my-value-2"), md1.getAll("my-key"))
        assertEquals(listOf("my-value-2"), md2.getAll("my-key"))
    }

    @Test
    fun `test copy`() {
        val original = GrpcMetadata().apply {
            append("my-key", "my-value")
            appendBinary("my-key-bin", byteArrayOf(1, 2, 3))
        }

        val copy = original.copy()

        assertEquals(original.get("my-key"), copy.get("my-key"))
        assertContentEquals(original.getBinary("my-key-bin"), copy.getBinary("my-key-bin"))

        // Verify they are independent
        copy.append("my-key", "my-value-2")
        assertEquals("my-value", original.get("my-key"))
        assertEquals("my-value-2", copy.get("my-key"))
    }

    @Test
    fun `test empty metadata`() {
        val metadata = GrpcMetadata()
        assertEquals(0, metadata.keys().size)
        assertEquals(null, metadata.get("non-existent"))
        assertEquals(null, metadata.getBinary("non-existent-bin"))
        assertEquals(emptyList(), metadata.getAll("non-existent"))
        assertEquals(emptyList(), metadata.getAllBinary("non-existent-bin"))
        assertEquals(false, metadata.contains("non-existent"))
    }

    @Test
    fun `test get returns last value`() {
        val metadata = GrpcMetadata().apply {
            append("my-key", "first")
            append("my-key", "second")
            append("my-key", "third")
        }

        assertEquals("third", metadata.get("my-key"))
        assertEquals(listOf("first", "second", "third"), metadata.getAll("my-key"))
    }

    @Test
    fun `test getBinary returns last value`() {
        val metadata = GrpcMetadata().apply {
            appendBinary("my-key-bin", byteArrayOf(1, 2, 3))
            appendBinary("my-key-bin", byteArrayOf(4, 5, 6))
            appendBinary("my-key-bin", byteArrayOf(7, 8, 9))
        }

        assertContentEquals(byteArrayOf(7, 8, 9), metadata.getBinary("my-key-bin"))
        assertEquals(3, metadata.getAllBinary("my-key-bin").size)
        assertContentEquals(byteArrayOf(1, 2, 3), metadata.getAllBinary("my-key-bin")[0])
        assertContentEquals(byteArrayOf(4, 5, 6), metadata.getAllBinary("my-key-bin")[1])
        assertContentEquals(byteArrayOf(7, 8, 9), metadata.getAllBinary("my-key-bin")[2])
    }

    @Test
    fun `test case insensitivity`() {
        val metadata = GrpcMetadata().apply {
            append("My-Key", "my-value")
            appendBinary("My-Key-bin", byteArrayOf(1, 2, 3))
        }

        assertEquals("my-value", metadata.get("my-key"))
        assertEquals("my-value", metadata.get("MY-KEY"))
        assertEquals("my-value", metadata.get("My-Key"))
        assertContentEquals(byteArrayOf(1, 2, 3), metadata.getBinary("my-key-bin"))
        assertContentEquals(byteArrayOf(1, 2, 3), metadata.getBinary("MY-KEY-bin"))
        assertTrue(metadata.contains("my-key"))
        assertTrue(metadata.contains("MY-KEY"))
    }

    @Test
    fun `test remove returns false for non-existent value`() {
        val metadata = GrpcMetadata().apply {
            append("my-key", "my-value")
        }

        assertEquals(false, metadata.remove("my-key", "other-value"))
        assertEquals(false, metadata.remove("non-existent", "value"))
        assertEquals(true, metadata.remove("my-key", "my-value"))
    }

    @Test
    fun `test removeBinary returns false for non-existent value`() {
        val value = byteArrayOf(1, 2, 3)
        val metadata = GrpcMetadata().apply {
            appendBinary("my-key-bin", value)
        }

        assertEquals(false, metadata.removeBinary("my-key-bin", byteArrayOf(4, 5, 6)))
        assertEquals(false, metadata.removeBinary("non-existent-bin", value))
        assertEquals(true, metadata.removeBinary("my-key-bin", value))
    }

    @Test
    fun `test removeAll returns empty list for non-existent key`() {
        val metadata = GrpcMetadata()
        assertEquals(emptyList(), metadata.removeAll("non-existent"))
    }

    @Test
    fun `test removeAllBinary returns empty list for non-existent key`() {
        val metadata = GrpcMetadata()
        assertEquals(emptyList(), metadata.removeAllBinary("non-existent-bin"))
    }

    @Test
    fun `test removeAll removes all values and returns them`() {
        val metadata = GrpcMetadata().apply {
            append("my-key", "value1")
            append("my-key", "value2")
            append("my-key", "value3")
        }

        val removed = metadata.removeAll("my-key")
        assertEquals(listOf("value1", "value2", "value3"), removed)
        assertEquals(null, metadata.get("my-key"))
        assertEquals(false, metadata.contains("my-key"))
    }

    @Test
    fun `test removeAllBinary removes all values and returns them`() {
        val metadata = GrpcMetadata().apply {
            appendBinary("my-key-bin", byteArrayOf(1, 2, 3))
            appendBinary("my-key-bin", byteArrayOf(4, 5, 6))
        }

        val removed = metadata.removeAllBinary("my-key-bin")
        assertEquals(2, removed.size)
        assertContentEquals(byteArrayOf(1, 2, 3), removed[0])
        assertContentEquals(byteArrayOf(4, 5, 6), removed[1])
        assertEquals(null, metadata.getBinary("my-key-bin"))
        assertEquals(false, metadata.contains("my-key-bin"))
    }

    @Test
    fun `test duplicate values allowed`() {
        val metadata = GrpcMetadata().apply {
            append("my-key", "value")
            append("my-key", "value")
            append("my-key", "value")
        }

        assertEquals(listOf("value", "value", "value"), metadata.getAll("my-key"))
        assertEquals(true, metadata.remove("my-key", "value"))
        assertEquals(listOf("value", "value"), metadata.getAll("my-key"))
    }

    @Test
    fun `test keys returns snapshot`() {
        val metadata = GrpcMetadata().apply {
            append("key1", "value1")
            append("key2", "value2")
        }

        val keys = metadata.keys()
        assertEquals(2, keys.size)

        metadata.append("key3", "value3")
        // Keys set should still be 2 since it's a snapshot
        assertEquals(2, keys.size)
    }

    @Test
    fun `test plus operator does not modify originals`() {
        val md1 = GrpcMetadata().apply {
            append("key1", "value1")
        }

        val md2 = GrpcMetadata().apply {
            append("key2", "value2")
        }

        val md3 = md1 + md2

        assertEquals(listOf("value1"), md1.getAll("key1"))
        assertEquals(false, md1.contains("key2"))
        assertEquals(listOf("value2"), md2.getAll("key2"))
        assertEquals(false, md2.contains("key1"))
        assertEquals(listOf("value1"), md3.getAll("key1"))
        assertEquals(listOf("value2"), md3.getAll("key2"))
    }


    private suspend fun unaryEcho(grpcClient: GrpcClient) {
        val service = grpcClient.withService<EchoService>()
        val response = service.UnaryEcho(EchoRequest { message = "Echo" })
        assertEquals("Echo", response.message)
    }

}