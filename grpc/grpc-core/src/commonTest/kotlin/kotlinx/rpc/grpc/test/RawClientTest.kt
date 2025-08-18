/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import grpc.examples.echo.*
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
class RawClientTest {

    @Test
    fun unaryEchoTest() = runTest(
        methodName = "UnaryEcho",
        type = MethodType.UNARY,
    ) { channel, descriptor ->
        val response = unaryRpc(channel, descriptor, EchoRequest { message = "Eccchhooo" })
        assertEquals("Eccchhooo", response.message)
    }

    @Test
    fun serverStreamingEchoTest() = runTest(
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
        methodName: String,
        type: MethodType,
        block: suspend (GrpcChannel, MethodDescriptor<EchoRequest, EchoResponse>) -> Unit,
    ) = runTest {
        val channel = ManagedChannelBuilder("localhost:50051")
            .usePlaintext()
            .buildChannel()

        val methodDescriptor = methodDescriptor(
            fullMethodName = "grpc.examples.echo.Echo/$methodName",
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