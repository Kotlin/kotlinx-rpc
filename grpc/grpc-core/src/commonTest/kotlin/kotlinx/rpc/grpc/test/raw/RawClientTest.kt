/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.raw

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.descriptor.MethodDescriptor
import kotlinx.rpc.grpc.descriptor.MethodType
import kotlinx.rpc.grpc.client.internal.bidirectionalStreamingRpc
import kotlinx.rpc.grpc.client.internal.clientStreamingRpc
import kotlinx.rpc.grpc.descriptor.methodDescriptor
import kotlinx.rpc.grpc.client.internal.serverStreamingRpc
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoRequestInternal
import kotlinx.rpc.grpc.test.EchoResponse
import kotlinx.rpc.grpc.test.EchoResponseInternal
import kotlinx.rpc.grpc.test.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for JVM and Native clients.
 *
 * To run the tests, you must first start the server with `tests/grpc-test-server`.
 */
class RawClientTest {

    @Test
    fun unaryEchoTest() = runTest(
        methodName = "UnaryEcho",
        type = MethodType.UNARY,
    ) { client, descriptor ->
        val response = client.unaryRpc(descriptor, EchoRequest { message = "Eccchhooo" })
        assertEquals("Eccchhooo", response.message)
    }

    @Test
    fun serverStreamingEchoTest() = runTest(
        methodName = "ServerStreamingEcho",
        type = MethodType.SERVER_STREAMING,
    ) { client, descriptor ->
        val response = client.serverStreamingRpc(descriptor, EchoRequest { message = "Eccchhooo" })
        var i = 0
        response.collect {
            println("Received: ${i++}")
            assertEquals("Eccchhooo", it.message)
        }
    }

    @Test
    fun clientStreamingEchoTest() = runTest(
        methodName = "ClientStreamingEcho",
        type = MethodType.CLIENT_STREAMING,
    ) { client, descriptor ->
        val response = client.clientStreamingRpc(descriptor, flow {
            repeat(5) {
                println("Sending: ${it + 1}")
                emit(EchoRequest { message = "Eccchhooo" })
            }
        })
        val expected = "Eccchhooo, Eccchhooo, Eccchhooo, Eccchhooo, Eccchhooo"
        assertEquals(expected, response.message)
    }

    @Test
    fun bidirectionalStreamingEchoTest() = runTest(
        methodName = "BidirectionalStreamingEcho",
        type = MethodType.BIDI_STREAMING,
    ) { client, descriptor ->
        val response = client.bidirectionalStreamingRpc(descriptor, flow {
            repeat(5) {
                emit(EchoRequest { message = "Eccchhooo" })
            }
        })

        var i = 0
        response.collect {
            i++
            assertEquals("Eccchhooo", it.message)
        }
        assertEquals(5, i)
    }

    fun runTest(
        methodName: String,
        type: MethodType,
        block: suspend (GrpcClient, MethodDescriptor<EchoRequest, EchoResponse>) -> Unit,
    ) = runTest {
        val client = GrpcClient("localhost:50051") {
            credentials = plaintext()
        }

        val methodDescriptor = methodDescriptor(
            fullMethodName = "kotlinx.rpc.grpc.test.EchoService/$methodName",
            requestCodec = EchoRequestInternal.CODEC,
            responseCodec = EchoResponseInternal.CODEC,
            type = type,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

        try {
            block(client, methodDescriptor)
        } finally {
            client.shutdown()
            client.awaitTermination()
        }
    }
}