/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.codec.MessageCodec
import kotlinx.rpc.grpc.internal.GrpcChannel
import kotlinx.rpc.grpc.internal.MethodDescriptor
import kotlinx.rpc.grpc.internal.MethodType
import kotlinx.rpc.grpc.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.internal.bidiStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.bidirectionalStreamingRpc
import kotlinx.rpc.grpc.internal.clientStreamingRpc
import kotlinx.rpc.grpc.internal.clientStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.methodDescriptor
import kotlinx.rpc.grpc.internal.serverStreamingRpc
import kotlinx.rpc.grpc.internal.serverStreamingServerMethodDefinition
import kotlinx.rpc.grpc.internal.serviceDescriptor
import kotlinx.rpc.grpc.internal.unaryRpc
import kotlinx.rpc.grpc.internal.unaryServerMethodDefinition
import kotlin.test.Test
import kotlin.test.assertEquals

private const val PORT = 8082

class RawClientServerTest {
    @Test
    fun unaryCall() = runTest(
        methodName = "unary",
        type = MethodType.UNARY,
        methodDefinition = { descriptor ->
            unaryServerMethodDefinition(descriptor) { it + it }
        },
    ) { channel, descriptor ->
        val response = unaryRpc(channel, descriptor, "Hello")

        assertEquals("HelloHello", response)
    }

    @Test
    fun serverStreamingCall() = runTest(
        methodName = "serverStreaming",
        type = MethodType.SERVER_STREAMING,
        methodDefinition = { descriptor ->
            serverStreamingServerMethodDefinition(descriptor) {
                flowOf(it, it)
            }
        }
    ) { channel, descriptor ->
        val response = serverStreamingRpc(channel, descriptor, "Hello")

        assertEquals(listOf("Hello", "Hello"), response.toList())
    }

    @Test
    fun clientStreamingCall() = runTest(
        methodName = "clientStreaming",
        type = MethodType.CLIENT_STREAMING,
        methodDefinition = { descriptor ->
            clientStreamingServerMethodDefinition(descriptor) {
                it.toList().joinToString(separator = "")
            }
        }
    ) { channel, descriptor ->
        val response = clientStreamingRpc(channel, descriptor, flowOf("Hello", "World"))

        assertEquals("HelloWorld", response)
    }

    @Test
    fun bidirectionalStreamingCall() = runTest(
        methodName = "bidirectionalStreaming",
        type = MethodType.BIDI_STREAMING,
        methodDefinition = { descriptor ->
            bidiStreamingServerMethodDefinition(descriptor) {
                it.map { str -> str + str }
            }
        }
    ) { channel, descriptor ->
        val response = bidirectionalStreamingRpc(channel, descriptor, flowOf("Hello", "World"))
            .toList()

        assertEquals(listOf("HelloHello", "WorldWorld"), response)
    }

    private fun runTest(
        methodName: String,
        type: MethodType,
        methodDefinition: CoroutineScope.(MethodDescriptor<String, String>) -> ServerMethodDefinition<String, String>,
        block: suspend (GrpcChannel, MethodDescriptor<String, String>) -> Unit,
    ) = kotlinx.coroutines.test.runTest {
        val serverJob = Job()
        val serverScope = CoroutineScope(serverJob)

        val clientChannel = ManagedChannelBuilder("localhost", PORT).apply {
            usePlaintext()
        }.buildChannel()

        val descriptor = methodDescriptor(
            fullMethodName = "${SERVICE_NAME}/$methodName",
            requestCodec = simpleCodec,
            responseCodec = simpleCodec,
            type = type,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

        val methods = listOf(descriptor)

        val builder = ServerBuilder(PORT).addService(
            serverServiceDefinition(
                serviceDescriptor = serviceDescriptor(
                    name = SERVICE_NAME,
                    methods = methods,
                    schemaDescriptor = Unit,
                ),
                methods = methods.map { serverScope.methodDefinition(it) },
            )
        )
        val server = Server(builder)
        server.start()

        block(clientChannel.platformApi, descriptor)

        serverJob.cancelAndJoin()
        clientChannel.shutdown()
        clientChannel.awaitTermination()
        server.shutdown()
        server.awaitTermination()
    }

    companion object {
        private const val SERVICE_NAME = "TestService"

        private val simpleCodec = object : MessageCodec<String> {
            override fun encode(value: String): Source {
                return Buffer().apply { writeString(value) }
            }

            override fun decode(stream: Source): String {
                return stream.readString()
            }
        }
    }
}
