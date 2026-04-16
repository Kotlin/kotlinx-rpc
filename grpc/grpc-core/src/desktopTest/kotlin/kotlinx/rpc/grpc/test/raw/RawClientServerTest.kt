/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.raw

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlin.time.Duration.Companion.seconds
import kotlinx.io.writeString
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.internal.bidirectionalStreamingRpc
import kotlinx.rpc.grpc.client.internal.clientStreamingRpc
import kotlinx.rpc.grpc.client.internal.serverStreamingRpc
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodDescriptor
import kotlinx.rpc.grpc.internal.serviceDescriptor
import kotlinx.rpc.grpc.server.internal.PlatformServer
import kotlinx.rpc.grpc.server.internal.ServerBuilder
import kotlinx.rpc.grpc.server.internal.ServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.bidiStreamingServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.clientStreamingServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.serverStreamingServerMethodDefinition
import kotlinx.rpc.grpc.server.internal.unaryServerMethodDefinition
import kotlinx.rpc.grpc.server.serverServiceDefinition
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertEquals

class RawClientServerTest {
    @Test
    fun unaryCall() = runTest(
        methodName = "unary",
        type = GrpcMethodType.UNARY,
        methodDefinition = { descriptor ->
            unaryServerMethodDefinition(descriptor, typeOf<String>(), emptyList()) { it + it }
        },
    ) { client, descriptor ->
        val response = client.unaryRpc(descriptor, "Hello")

        assertEquals("HelloHello", response)
    }

    @Test
    fun serverStreamingCall() = runTest(
        methodName = "serverStreaming",
        type = GrpcMethodType.SERVER_STREAMING,
        methodDefinition = { descriptor ->
            serverStreamingServerMethodDefinition(descriptor, typeOf<String>(), emptyList()) {
                flowOf(it, it)
            }
        }
    ) { client, descriptor ->
        val response = client.serverStreamingRpc(descriptor, "Hello")

        assertEquals(listOf("Hello", "Hello"), response.toList())
    }

    @Test
    fun clientStreamingCall() = runTest(
        methodName = "clientStreaming",
        type = GrpcMethodType.CLIENT_STREAMING,
        methodDefinition = { descriptor ->
            clientStreamingServerMethodDefinition(descriptor, typeOf<String>(), emptyList()) {
                it.toList().joinToString(separator = "")
            }
        }
    ) { client, descriptor ->
        val response = client.clientStreamingRpc(descriptor, flowOf("Hello", "World"))

        assertEquals("HelloWorld", response)
    }

    @Test
    fun bidirectionalStreamingCall() {
        runTest(
            methodName = "bidirectionalStreaming",
            type = GrpcMethodType.BIDI_STREAMING,
            methodDefinition = { descriptor ->
                bidiStreamingServerMethodDefinition(descriptor, typeOf<String>(), emptyList()) {
                    it.map { str -> str + str }
                }
            }
        ) { client, descriptor ->
            val response = client.bidirectionalStreamingRpc(descriptor, flowOf("Hello", "World"))
                .toList()

            assertEquals(listOf("HelloHello", "WorldWorld"), response)
        }
    }

    private fun runTest(
        methodName: String,
        type: GrpcMethodType,
        methodDefinition: CoroutineScope.(GrpcMethodDescriptor<String, String>) -> ServerMethodDefinition<String, String>,
        block: suspend (GrpcClient, GrpcMethodDescriptor<String, String>) -> Unit,
    ) = kotlinx.coroutines.test.runTest {
        val serverJob = Job()
        val serverScope = CoroutineScope(serverJob)

        val descriptor = methodDescriptor(
            fullMethodName = "${SERVICE_NAME}/$methodName",
            requestMarshaller = simpleMarshaller,
            responseMarshaller = simpleMarshaller,
            type = type,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

        val methods = listOf(descriptor)

        val builder = ServerBuilder(0).addService(
            serverServiceDefinition(
                serviceDescriptor = serviceDescriptor(
                    name = SERVICE_NAME,
                    methods = methods,
                    schemaDescriptor = Unit,
                ),
                methods = methods.map { serverScope.methodDefinition(it) },
            )
        )
        val server = PlatformServer(builder)
        server.start()

        val client = GrpcClient("localhost", server.port) {
            credentials = plaintext()
        }

        try {
            block(client, descriptor)
        } finally {
            serverJob.cancelAndJoin()
            client.shutdownNow()
            server.shutdownNow()
            server.awaitTermination(30.seconds)
            client.awaitTermination(30.seconds)
        }
    }

    companion object {
        private const val SERVICE_NAME = "TestService"

        private val simpleMarshaller = object : GrpcMarshaller<String> {
            override fun encode(value: String, config: GrpcMarshallerConfig?): Source {
                return Buffer().apply { writeString(value) }
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): String {
                return source.readString()
            }
        }
    }
}
