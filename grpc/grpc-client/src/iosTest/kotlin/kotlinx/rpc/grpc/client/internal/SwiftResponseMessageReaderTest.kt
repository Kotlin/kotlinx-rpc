/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(
    kotlinx.cinterop.ExperimentalForeignApi::class,
    kotlinx.rpc.internal.utils.ExperimentalRpcApi::class,
    kotlinx.rpc.internal.utils.InternalRpcApi::class,
)

package kotlinx.rpc.grpc.client.internal

import io.grpc.testing.integration.*
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.client.GrpcCallOptions
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.testing.GrpcClientTestServer
import kotlinx.rpc.grpc.client.testing.grpcClientTest
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodDescriptor
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.marshaller.kotlinx.serialization.asMarshallerResolver
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.seconds

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
        assertFailsWith<IllegalStateException> { reader.accessStorage { true } }
    }

    @Test
    fun emptyResponseNeedsNoPointer() {
        val reader = SwiftResponseMessageReader(null, 0)
        assertContentEquals(byteArrayOf(), reader.use { it.readByteArray() })
        assertFailsWith<IllegalStateException> { reader.readByte() }
        assertFailsWith<IllegalArgumentException> { SwiftResponseMessageReader(null, 1) }
    }

    @Test
    fun nearFourMiBProtobufResponse() = grpcClientTest {
        val size = 4 * 1024 * 1024 - 1024
        val response = testService.unaryCall(SimpleRequest {
            responseType = PayloadType.COMPRESSABLE
            responseSize = size
        })

        assertContentEquals(ByteArray(size), response.payload.body.toByteArray())
        assertUnaryLifecycle()
    }

    @Test
    fun decodeFailureFailsCallWithoutCrossingSwiftFrame() = runTest {
        val expected = IllegalStateException("response decode failed")
        val client = GrpcClient(GrpcClientTestServer.HOST, GrpcClientTestServer.PORT) {
            credentials = plaintext()
        }
        try {
            val failingMarshaller = object : GrpcMarshaller<Int> {
                override fun prepare(value: Int, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
                    error("not used")

                override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): Int =
                    throw expected
            }
            val method = methodDescriptor(
                fullMethodName = "grpc.testing.TestService/UnaryCall",
                requestMarshaller = SimpleRequestInternal.MARSHALLER,
                responseMarshaller = failingMarshaller,
                type = GrpcMethodType.UNARY,
                schemaDescriptor = null,
                idempotent = false,
                safe = false,
                sampledToLocalTracing = false,
            )

            val failure = assertFailsWith<IllegalStateException> {
                withContext(Dispatchers.Default) {
                    withTimeout(10.seconds) {
                        client.transport.execute(
                            method = method,
                            requests = flowOf(SimpleRequest { responseSize = 1 }),
                            headers = GrpcMetadata(),
                            callOptions = GrpcCallOptions(),
                        ).toList()
                    }
                }
            }
            assertSame(expected, failure)
        } finally {
            client.shutdownNow()
        }
    }
}
