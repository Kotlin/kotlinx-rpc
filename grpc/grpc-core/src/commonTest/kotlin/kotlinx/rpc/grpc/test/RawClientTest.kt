/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.ManagedChannelBuilder
import kotlinx.rpc.grpc.buildChannel
import kotlinx.rpc.grpc.internal.*
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests for JVM and Native clients.
 */
// TODO: Start echo service server automatically
class RawClientTest {

    @Test
    fun unaryEchoTest() = runTest(
        serviceMainClassName = "kotlinx.rpc.grpc.test.services.EchoServiceMain",
        methodName = "UnaryEcho",
        type = MethodType.UNARY,
    ) { channel, descriptor ->
        val response = unaryRpc(channel, descriptor, EchoRequest { message = "Eccchhooo" })
        assertEquals("Eccchhooo", response.message)
    }

    @Test
    fun serverStreamingEchoTest() = runTest(
        serviceMainClassName = "kotlinx.rpc.grpc.test.services.EchoServiceMain",
        methodName = "ServerStreamingEcho",
        type = MethodType.SERVER_STREAMING,
    ) { channel, descriptor ->
        val response = serverStreamingRpc(channel, descriptor, EchoRequest { message = "Eccchhooo" })
        var i = 0
        response.collect {
            println("Received: ${i++}")
            assertEquals("Eccchhooo", it.message)
        }
    }

    @Test
    fun clientStreamingEchoTest() = runTest(
        serviceMainClassName = "kotlinx.rpc.grpc.test.services.EchoServiceMain",
        methodName = "ClientStreamingEcho",
        type = MethodType.CLIENT_STREAMING,
    ) { channel, descriptor ->
        val response = clientStreamingRpc(channel, descriptor, flow {
            repeat(5) {
                delay(100)
                println("Sending: ${it + 1}")
                emit(EchoRequest { message = "Eccchhooo" })
            }
        })
        val expected = "Eccchhooo, Eccchhooo, Eccchhooo, Eccchhooo, Eccchhooo"
        assertEquals(expected, response.message)
    }

    @Test
    fun bidirectionalStreamingEchoTest() = runTest(
        serviceMainClassName = "kotlinx.rpc.grpc.test.services.EchoServiceMain",
        methodName = "BidirectionalStreamingEcho",
        type = MethodType.BIDI_STREAMING,
    ) { channel, descriptor ->
        val response = bidirectionalStreamingRpc(channel, descriptor, flow {
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
        serviceMainClassName: String,
        methodName: String,
        type: MethodType,
        block: suspend (GrpcChannel, MethodDescriptor<EchoRequest, EchoResponse>) -> Unit,
    ) = runTest {
        ExternalTestService(serviceMainClassName).use {
            val channel = ManagedChannelBuilder("localhost:${BaseGrpcServiceTest.PORT}")
                .usePlaintext()
                .buildChannel()

            val methodDescriptor = methodDescriptor(
                fullMethodName = "kotlinx.rpc.grpc.test.services.EchoService/$methodName",
                requestCodec = EchoRequestInternal.CODEC,
                responseCodec = EchoResponseInternal.CODEC,
                type = type,
                schemaDescriptor = Unit,
                idempotent = true,
                safe = true,
                sampledToLocalTracing = true,
            )

            try {
                block(channel.platformApi, methodDescriptor)
            } finally {
                channel.shutdown()
                channel.awaitTermination()
            }
        }
    }
}