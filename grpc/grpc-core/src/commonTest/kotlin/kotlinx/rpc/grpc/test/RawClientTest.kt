/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.grpc.internal.MethodType
import kotlinx.rpc.grpc.internal.bidirectionalStreamingRpc
import kotlinx.rpc.grpc.internal.clientStreamingRpc
import kotlinx.rpc.grpc.internal.methodDescriptor
import kotlinx.rpc.grpc.internal.serverStreamingRpc
import kotlinx.rpc.grpc.internal.unaryRpc
import kotlinx.rpc.registerService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration

private const val PORT = 50051

/**
 * Tests for JVM and Native clients.
 *
 * To run the tests you must first start the server with the [EchoServiceImpl.runServer] method on JVM.
 */
// TODO: Start external service server automatically (KRPC-208)
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


class EchoServiceImpl : EchoService {

    override suspend fun UnaryEcho(message: EchoRequest): EchoResponse {
        delay(message.timeout.toLong())
        return EchoResponse { this.message = message.message }
    }

    override fun ServerStreamingEcho(message: EchoRequest): Flow<EchoResponse> {
        val count = message.serverStreamReps ?: 5u
        return flow {
            repeat(count.toInt()) {
                emit(EchoResponse { this.message = message.message })
            }
        }
    }

    override suspend fun ClientStreamingEcho(message: Flow<EchoRequest>): EchoResponse {
        val result = message.toList().joinToString(", ") { it.message }
        return EchoResponse { this.message = result }
    }

    override fun BidirectionalStreamingEcho(message: Flow<EchoRequest>): Flow<EchoResponse> {
        return flow {
            message.collect {
                emit(EchoResponse { this.message = it.message })
            }
        }
    }


    /**
     * Run this on JVM before executing tests.
     */
    @Test
    fun runServer() = runTest(timeout = Duration.INFINITE) {
        val server = GrpcServer(
            port = PORT,
        ) { services { registerService<EchoService> { EchoServiceImpl() } } }

        try {
            server.start()
            println("Server started")
            server.awaitTermination()
        } finally {
            server.shutdown()
            server.awaitTermination()
        }
    }
}
