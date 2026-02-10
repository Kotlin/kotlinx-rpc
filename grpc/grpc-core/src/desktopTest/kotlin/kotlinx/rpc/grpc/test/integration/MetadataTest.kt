/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readByteArray
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.GrpcMetadataKey
import kotlinx.rpc.grpc.append
import kotlinx.rpc.grpc.appendBinary
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.codec.CodecConfig
import kotlinx.rpc.grpc.codec.MessageCodec
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
import kotlinx.rpc.grpc.test.AllPrimitives
import kotlinx.rpc.grpc.test.AllPrimitivesInternal
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoRequestInternal
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.internal.utils.ExperimentalRpcApi
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetadataTest : GrpcTestBase() {

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
            appendBinary("my-proto-bin", EchoRequestInternal.CODEC.encode(protoMsg).readByteArray())
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
            val decodedProto = EchoRequestInternal.CODEC.decode(proto)
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

    // Helper codecs and types for testing typed keys

    // Custom test type for ASCII encoding (non-binary methods)
    data class TestUser(val name: String, val age: Int)

    // ASCII codec - encodes to/from ASCII string (for non-binary methods)
    @OptIn(ExperimentalRpcApi::class)
    private object TestUserAsciiCodec : MessageCodec<TestUser> {
        override fun encode(value: TestUser, config: CodecConfig?): Source {
            // Encode as ASCII string in format "name:age"
            val asciiString = "${value.name}:${value.age}"
            return Buffer().apply { writeString(asciiString) }
        }

        override fun decode(source: Source, config: CodecConfig?   ): TestUser {
            // Decode from ASCII string
            val asciiString = Buffer().apply { transferFrom(source) }.readString()
            val parts = asciiString.split(":")
            return TestUser(parts[0], parts[1].toInt())
        }
    }

    // Tests for typed key methods
    // Testing ASCII methods: get, getAll, append, remove, removeAll

    @Test
    fun `test typed key get returns last value`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata().apply {
            append(key, TestUser("Alice", 30))
            append(key, TestUser("Bob", 25))
            append(key, TestUser("Charlie", 35))
        }

        assertEquals(TestUser("Charlie", 35), metadata.get(key))
    }

    @Test
    fun `test typed key get returns null for non-existent key`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()

        assertEquals(null, metadata.get(key))
    }

    @Test
    fun `test typed key get works with multiple keys`() {
        val key1 = GrpcMetadataKey("user-key", TestUserAsciiCodec)
        val key2 = GrpcMetadataKey("other-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata().apply {
            append(key1, TestUser("Alice", 30))
            append(key2, TestUser("Bob", 25))
        }

        assertEquals(TestUser("Alice", 30), metadata.get(key1))
        assertEquals(TestUser("Bob", 25), metadata.get(key2))
    }

    @Test
    fun `test typed key getAll returns all values in order`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata().apply {
            append(key, TestUser("Alice", 30))
            append(key, TestUser("Bob", 25))
            append(key, TestUser("Charlie", 35))
        }

        assertEquals(
            listOf(TestUser("Alice", 30), TestUser("Bob", 25), TestUser("Charlie", 35)),
            metadata.getAll(key)
        )
    }

    @Test
    fun `test typed key getAll returns empty list for non-existent key`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()

        assertEquals(emptyList(), metadata.getAll(key))
    }

    @Test
    fun `test typed key append adds value`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()

        metadata.append(key, TestUser("Alice", 30))

        assertEquals(TestUser("Alice", 30), metadata.get(key))
        assertEquals(listOf(TestUser("Alice", 30)), metadata.getAll(key))
    }

    @Test
    fun `test typed key append adds multiple values in order`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()

        metadata.append(key, TestUser("Alice", 30))
        metadata.append(key, TestUser("Bob", 25))
        metadata.append(key, TestUser("Charlie", 35))

        assertEquals(
            listOf(TestUser("Alice", 30), TestUser("Bob", 25), TestUser("Charlie", 35)),
            metadata.getAll(key)
        )
    }

    @Test
    fun `test typed key append allows duplicate values`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()
        val user = TestUser("Alice", 30)

        metadata.append(key, user)
        metadata.append(key, user)
        metadata.append(key, user)

        assertEquals(listOf(user, user, user), metadata.getAll(key))
    }

    @Test
    fun `test typed key remove removes first occurrence`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val alice = TestUser("Alice", 30)
        val bob = TestUser("Bob", 25)
        val metadata = GrpcMetadata().apply {
            append(key, alice)
            append(key, bob)
            append(key, alice)
        }

        val result = metadata.remove(key, alice)

        assertTrue(result)
        assertEquals(listOf(bob, alice), metadata.getAll(key))
    }

    @Test
    fun `test typed key remove returns false for non-existent value`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata().apply {
            append(key, TestUser("Alice", 30))
        }

        val result = metadata.remove(key, TestUser("Bob", 25))

        assertFalse(result)
        assertEquals(listOf(TestUser("Alice", 30)), metadata.getAll(key))
    }

    @Test
    fun `test typed key remove returns false for non-existent key`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()

        val result = metadata.remove(key, TestUser("Alice", 30))

        assertFalse(result)
    }

    @Test
    fun `test typed key removeAll removes all values and returns them`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val users = listOf(TestUser("Alice", 30), TestUser("Bob", 25), TestUser("Charlie", 35))
        val metadata = GrpcMetadata().apply {
            users.forEach { append(key, it) }
        }

        val removed = metadata.removeAll(key)

        assertEquals(users, removed)
        assertEquals(emptyList(), metadata.getAll(key))
        assertEquals(null, metadata.get(key))
    }

    @Test
    fun `test typed key removeAll returns empty list for non-existent key`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()

        val removed = metadata.removeAll(key)

        assertEquals(emptyList(), removed)
    }

    // Tests for typed binary key methods: getBinary, getAllBinary, appendBinary, removeBinary, removeAllBinary

    @Test
    fun `test typed binary key getBinary returns last value`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives { int32 = 1; string = "first" })
            appendBinary(key, AllPrimitives { int32 = 2; string = "second" })
            appendBinary(key, AllPrimitives { int32 = 3; string = "third" })
        }

        val result = metadata.getBinary(key)
        assertEquals(3, result?.int32)
        assertEquals("third", result?.string)
    }

    @Test
    fun `test typed binary key getBinary returns null for non-existent key`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()

        assertEquals(null, metadata.getBinary(key))
    }

    @Test
    fun `test typed binary key getBinary works with different data`() {
        val key = GrpcMetadataKey("data-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives {
                int32 = 42
                int64 = 123456789L
                double = 3.14
                string = "test"
                bool = true
            })
        }

        val result = metadata.getBinary(key)!!
        assertEquals(42, result.int32)
        assertEquals(123456789L, result.int64)
        assertEquals(3.14, result.double)
        assertEquals("test", result.string)
        assertEquals(true, result.bool)
    }

    @Test
    fun `test typed binary key getAllBinary returns all values in order`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives { int32 = 1 })
            appendBinary(key, AllPrimitives { int32 = 2 })
            appendBinary(key, AllPrimitives { int32 = 3 })
        }

        val results = metadata.getAllBinary(key)
        assertEquals(3, results.size)
        assertEquals(1, results[0].int32)
        assertEquals(2, results[1].int32)
        assertEquals(3, results[2].int32)
    }

    @Test
    fun `test typed binary key getAllBinary returns empty list for non-existent key`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()

        assertEquals(emptyList(), metadata.getAllBinary(key))
    }

    @Test
    fun `test typed binary key appendBinary adds value`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()
        val value = AllPrimitives { int32 = 42; string = "test" }

        metadata.appendBinary(key, value)

        val result = metadata.getBinary(key)!!
        assertEquals(42, result.int32)
        assertEquals("test", result.string)
    }

    @Test
    fun `test typed binary key appendBinary adds multiple values in order`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()

        metadata.appendBinary(key, AllPrimitives { int32 = 1 })
        metadata.appendBinary(key, AllPrimitives { int32 = 2 })
        metadata.appendBinary(key, AllPrimitives { int32 = 3 })

        val results = metadata.getAllBinary(key)
        assertEquals(listOf(1, 2, 3), results.map { it.int32 })
    }

    @Test
    fun `test typed binary key appendBinary with complex data`() {
        val key = GrpcMetadataKey("complex-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()

        val complexValue = AllPrimitives {
            double = 1.23
            float = 4.56f
            int32 = 789
            int64 = 123456789012345L
            uint32 = 111u
            uint64 = 222uL
            bool = true
            string = "complex data"
            bytes = byteArrayOf(1, 2, 3, 4, 5)
        }

        metadata.appendBinary(key, complexValue)

        val result = metadata.getBinary(key)!!
        assertEquals(1.23, result.double)
        assertEquals(4.56f, result.float)
        assertEquals(789, result.int32)
        assertEquals(123456789012345L, result.int64)
        assertEquals(111u, result.uint32)
        assertEquals(222uL, result.uint64)
        assertEquals(true, result.bool)
        assertEquals("complex data", result.string)
        assertContentEquals(byteArrayOf(1, 2, 3, 4, 5), result.bytes)
    }

    @Test
    fun `test typed binary key removeBinary removes first occurrence`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val value1 = AllPrimitives { int32 = 1 }
        val value2 = AllPrimitives { int32 = 2 }
        val metadata = GrpcMetadata().apply {
            appendBinary(key, value1)
            appendBinary(key, value2)
            appendBinary(key, value1)
        }

        val result = metadata.removeBinary(key, value1)

        assertTrue(result)
        val remaining = metadata.getAllBinary(key)
        assertEquals(2, remaining.size)
        assertEquals(2, remaining[0].int32)
        assertEquals(1, remaining[1].int32)
    }

    @Test
    fun `test typed binary key removeBinary returns false for non-existent value`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives { int32 = 1 })
        }

        val result = metadata.removeBinary(key, AllPrimitives { int32 = 2 })

        assertFalse(result)
    }

    @Test
    fun `test typed binary key removeBinary returns false for non-existent key`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()

        val result = metadata.removeBinary(key, AllPrimitives { int32 = 1 })

        assertFalse(result)
    }

    @Test
    fun `test typed binary key removeAllBinary removes all values and returns them`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives { int32 = 1 })
            appendBinary(key, AllPrimitives { int32 = 2 })
            appendBinary(key, AllPrimitives { int32 = 3 })
        }

        val removed = metadata.removeAllBinary(key)

        assertEquals(3, removed.size)
        assertEquals(listOf(1, 2, 3), removed.map { it.int32 })
        assertEquals(emptyList(), metadata.getAllBinary(key))
        assertEquals(null, metadata.getBinary(key))
    }

    @Test
    fun `test typed binary key removeAllBinary returns empty list for non-existent key`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()

        val removed = metadata.removeAllBinary(key)

        assertEquals(emptyList(), removed)
    }

    // Integration tests

    @Test
    fun `test typed key case insensitivity`() {
        val key1 = GrpcMetadataKey("My-Key", TestUserAsciiCodec)
        val key2 = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val key3 = GrpcMetadataKey("MY-KEY", TestUserAsciiCodec)
        val metadata = GrpcMetadata()
        val user = TestUser("Alice", 30)

        metadata.append(key1, user)

        // All case variations should access the same key
        assertEquals(user, metadata.get(key2))
        assertEquals(user, metadata.get(key3))
        assertEquals(listOf(user), metadata.getAll(key1))
        assertEquals(listOf(user), metadata.getAll(key2))
        assertEquals(listOf(user), metadata.getAll(key3))
    }

    @Test
    fun `test typed binary key case insensitivity`() {
        val key1 = GrpcMetadataKey("My-Key-bin", AllPrimitivesInternal.CODEC)
        val key2 = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val key3 = GrpcMetadataKey("MY-KEY-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()
        val value = AllPrimitives { int32 = 42 }

        metadata.appendBinary(key1, value)

        // All case variations should access the same key
        assertEquals(42, metadata.getBinary(key2)?.int32)
        assertEquals(42, metadata.getBinary(key3)?.int32)
    }

    @Test
    fun `test typed key with copy operation`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val original = GrpcMetadata().apply {
            append(key, TestUser("Alice", 30))
            append(key, TestUser("Bob", 25))
        }

        val copied = original.copy()

        assertEquals(
            listOf(TestUser("Alice", 30), TestUser("Bob", 25)),
            copied.getAll(key)
        )

        // Verify independence
        copied.append(key, TestUser("Charlie", 35))
        assertEquals(2, original.getAll(key).size)
        assertEquals(3, copied.getAll(key).size)
    }

    @Test
    fun `test typed binary key with copy operation`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val original = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives { int32 = 1 })
            appendBinary(key, AllPrimitives { int32 = 2 })
        }

        val copied = original.copy()

        assertEquals(listOf(1, 2), copied.getAllBinary(key).map { it.int32 })

        // Verify independence
        copied.appendBinary(key, AllPrimitives { int32 = 3 })
        assertEquals(2, original.getAllBinary(key).size)
        assertEquals(3, copied.getAllBinary(key).size)
    }

    @Test
    fun `test typed key with merge operation`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val md1 = GrpcMetadata().apply {
            append(key, TestUser("Alice", 30))
        }
        val md2 = GrpcMetadata().apply {
            append(key, TestUser("Bob", 25))
        }

        val merged = md1 + md2

        assertEquals(
            listOf(TestUser("Alice", 30), TestUser("Bob", 25)),
            merged.getAll(key)
        )
        assertEquals(1, md1.getAll(key).size)
        assertEquals(1, md2.getAll(key).size)
    }

    @Test
    fun `test typed binary key with merge operation`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val md1 = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives { int32 = 1 })
        }
        val md2 = GrpcMetadata().apply {
            appendBinary(key, AllPrimitives { int32 = 2 })
        }

        val merged = md1 + md2

        assertEquals(listOf(1, 2), merged.getAllBinary(key).map { it.int32 })
        assertEquals(1, md1.getAllBinary(key).size)
        assertEquals(1, md2.getAllBinary(key).size)
    }

    @Test
    fun `test typed key preserves value order across operations`() {
        val key = GrpcMetadataKey("my-key", TestUserAsciiCodec)
        val metadata = GrpcMetadata()

        // Add values in specific order
        for (i in 1..5) {
            metadata.append(key, TestUser("User$i", 20 + i))
        }

        // Verify order is preserved
        val values = metadata.getAll(key)
        assertEquals(5, values.size)
        for (i in 1..5) {
            assertEquals("User$i", values[i - 1].name)
            assertEquals(20 + i, values[i - 1].age)
        }

        // Remove first occurrence of User3
        metadata.remove(key, TestUser("User3", 23))

        val afterRemove = metadata.getAll(key)
        assertEquals(4, afterRemove.size)
        assertEquals("User1", afterRemove[0].name)
        assertEquals("User2", afterRemove[1].name)
        assertEquals("User4", afterRemove[2].name)
        assertEquals("User5", afterRemove[3].name)
    }

    @Test
    fun `test typed binary key preserves value order across operations`() {
        val key = GrpcMetadataKey("my-key-bin", AllPrimitivesInternal.CODEC)
        val metadata = GrpcMetadata()

        // Add values in specific order
        for (i in 1..5) {
            metadata.appendBinary(key, AllPrimitives { int32 = i; int64 = i.toLong() * 100 })
        }

        // Verify order is preserved
        val values = metadata.getAllBinary(key)
        assertEquals(5, values.size)
        for (i in 1..5) {
            assertEquals(i, values[i - 1].int32)
            assertEquals(i.toLong() * 100, values[i - 1].int64)
        }

        // Remove value with int32 = 3
        metadata.removeBinary(key, AllPrimitives { int32 = 3; int64 = 300L })

        val afterRemove = metadata.getAllBinary(key)
        assertEquals(4, afterRemove.size)
    }

    @Test
    fun `test send and receive typed key headers and trailers`() = runTest {
        // Define typed keys for ASCII metadata
        val userKey = GrpcMetadataKey("user-metadata", TestUserAsciiCodec)
        val multiUserKey = GrpcMetadataKey("multi-user-metadata", TestUserAsciiCodec)

        // Define typed keys for binary metadata
        val dataBinKey = GrpcMetadataKey("data-metadata-bin", AllPrimitivesInternal.CODEC)
        val multiDataBinKey = GrpcMetadataKey("multi-data-metadata-bin", AllPrimitivesInternal.CODEC)

        var clientHeaders: GrpcMetadata? = null
        val responseHeadersDef = CompletableDeferred<GrpcMetadata>()
        val responseTrailersDef = CompletableDeferred<GrpcMetadata>()

        // Helper function to append typed metadata
        fun GrpcMetadata.appendTypedMetadata() {
            // Add single ASCII value
            append(userKey, TestUser("Alice", 30))

            // Add multiple ASCII values
            append(multiUserKey, TestUser("Bob", 25))
            append(multiUserKey, TestUser("Charlie", 35))
            append(multiUserKey, TestUser("David", 28))

            // Add single binary value with complex data
            appendBinary(dataBinKey, AllPrimitives {
                int32 = 42
                int64 = 9876543210L
                double = 3.14159
                float = 2.718f
                bool = true
                string = "Complex metadata"
                bytes = byteArrayOf(10, 20, 30, 40, 50)
                uint32 = 100u
                uint64 = 200uL
                sint32 = -50
                sint64 = -1000L
                fixed32 = 777u
                fixed64 = 888uL
                sfixed32 = -99
                sfixed64 = -999L
            })

            // Add multiple binary values
            appendBinary(multiDataBinKey, AllPrimitives {
                int32 = 1
                string = "First binary"
                double = 1.1
            })
            appendBinary(multiDataBinKey, AllPrimitives {
                int32 = 2
                string = "Second binary"
                double = 2.2
            })
            appendBinary(multiDataBinKey, AllPrimitives {
                int32 = 3
                string = "Third binary"
                double = 3.3
            })
        }

        runGrpcTest(
            clientInterceptors = clientInterceptor {
                requestHeaders.appendTypedMetadata()
                onHeaders { headers -> responseHeadersDef.complete(headers) }
                onClose { _, trailers -> responseTrailersDef.complete(trailers) }
                proceed(it)
            },
            serverInterceptors = serverInterceptor {
                responseHeaders.appendTypedMetadata()
                responseTrailers.appendTypedMetadata()
                clientHeaders = this.requestHeaders
                proceed(it)
            }
        ) { unaryEcho(it) }

        // Helper function to assert typed metadata
        fun GrpcMetadata.assertTypedMetadata() {
            // Verify single ASCII value
            val user = get(userKey)
            assertEquals("Alice", user?.name)
            assertEquals(30, user?.age)

            // Verify multiple ASCII values
            val multiUsers = getAll(multiUserKey)
            assertEquals(3, multiUsers.size)
            assertEquals(TestUser("Bob", 25), multiUsers[0])
            assertEquals(TestUser("Charlie", 35), multiUsers[1])
            assertEquals(TestUser("David", 28), multiUsers[2])

            // Verify last multi-user is returned by get()
            val lastMultiUser = get(multiUserKey)
            assertEquals(TestUser("David", 28), lastMultiUser)

            // Verify single binary value with all fields
            val data = getBinary(dataBinKey)!!
            assertEquals(42, data.int32)
            assertEquals(9876543210L, data.int64)
            assertEquals(3.14159, data.double)
            assertEquals(2.718f, data.float)
            assertEquals(true, data.bool)
            assertEquals("Complex metadata", data.string)
            assertContentEquals(byteArrayOf(10, 20, 30, 40, 50), data.bytes)
            assertEquals(100u, data.uint32)
            assertEquals(200uL, data.uint64)
            assertEquals(-50, data.sint32)
            assertEquals(-1000L, data.sint64)
            assertEquals(777u, data.fixed32)
            assertEquals(888uL, data.fixed64)
            assertEquals(-99, data.sfixed32)
            assertEquals(-999L, data.sfixed64)

            // Verify multiple binary values
            val multiData = getAllBinary(multiDataBinKey)
            assertEquals(3, multiData.size)
            assertEquals(1, multiData[0].int32)
            assertEquals("First binary", multiData[0].string)
            assertEquals(1.1, multiData[0].double)
            assertEquals(2, multiData[1].int32)
            assertEquals("Second binary", multiData[1].string)
            assertEquals(2.2, multiData[1].double)
            assertEquals(3, multiData[2].int32)
            assertEquals("Third binary", multiData[2].string)
            assertEquals(3.3, multiData[2].double)

            // Verify last multi-data is returned by getBinary()
            val lastMultiData = getBinary(multiDataBinKey)!!
            assertEquals(3, lastMultiData.int32)
            assertEquals("Third binary", lastMultiData.string)
            assertEquals(3.3, lastMultiData.double)
        }

        // Assert client request headers received by server
        clientHeaders!!.assertTypedMetadata()

        // Assert response headers received by client
        responseHeadersDef.await().assertTypedMetadata()

        // Assert response trailers received by client
        responseTrailersDef.await().assertTypedMetadata()
    }

}