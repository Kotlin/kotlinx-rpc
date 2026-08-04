/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.raw

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.descriptor.GrpcMethodDescriptor
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodDescriptor
import kotlinx.rpc.grpc.internal.serviceDescriptor
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.server.internal.PlatformServer
import kotlinx.rpc.grpc.server.internal.ServerBuilder
import kotlinx.rpc.grpc.server.internal.unaryServerMethodDefinition
import kotlinx.rpc.grpc.server.serverServiceDefinition
import kotlinx.rpc.grpc.test.captureStdOut
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.seconds

// Regression test for https://github.com/Kotlin/kotlinx-rpc/issues/749
class RunBatchExceptionTest {

    @Test
    fun `exception in response marshaller should not produce uncaught exception`() = runTest {
        runWithThrowingMarshaller { client, descriptor ->
            val output = captureStdOut {
                assertFailsWith<GrpcStatusException> {
                    client.unaryRpc(descriptor, "Hello")
                }
            }

            assertFalse(
                output.contains("Uncaught Kotlin exception"),
                "Exception should be caught in runBatch, not escape onto the gRPC thread. Stdout: $output"
            )
        }
    }

    private suspend fun CoroutineScope.runWithThrowingMarshaller(
        block: suspend (GrpcClient, descriptor: GrpcMethodDescriptor<String, String>) -> Unit,
    ) {
        val serverDescriptor = stringDescriptor(normalMarshaller)
        val clientDescriptor = stringDescriptor(throwingDecodeMarshaller)

        val builder = ServerBuilder(0).addService(
            serverServiceDefinition(
                serviceDescriptor = serviceDescriptor(
                    name = SERVICE_NAME,
                    methods = listOf(serverDescriptor),
                    schemaDescriptor = Unit,
                ),
                methods = listOf(
                    unaryServerMethodDefinition(
                        serverDescriptor, typeOf<String>(), emptyList(),
                    ) { it + it }
                ),
            )
        )
        val server = PlatformServer(builder)
        server.start()

        val client = GrpcClient("localhost", server.port) { credentials = plaintext() }

        try {
            block(client, clientDescriptor)
        } finally {
            client.shutdownNow()
            server.shutdownNow()
            server.awaitTermination(30.seconds)
            client.awaitTermination(30.seconds)
        }
    }

    companion object {
        private const val SERVICE_NAME = "TestService"

        private fun stringDescriptor(responseMarshaller: GrpcMarshaller<String>) = methodDescriptor(
            fullMethodName = "$SERVICE_NAME/Unary",
            requestMarshaller = normalMarshaller,
            responseMarshaller = responseMarshaller,
            type = GrpcMethodType.UNARY,
            schemaDescriptor = Unit,
            idempotent = true,
            safe = true,
            sampledToLocalTracing = true,
        )

        private val normalMarshaller = object : GrpcMarshaller<String> {
            override fun encode(value: String, config: GrpcMarshallerConfig?): Source =
                Buffer().apply { writeString(value) }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): String =
                source.readString()
        }

        private val throwingDecodeMarshaller = object : GrpcMarshaller<String> {
            override fun encode(value: String, config: GrpcMarshallerConfig?): Source =
                Buffer().apply { writeString(value) }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): String =
                throw RuntimeException("Simulated decode failure")
        }
    }
}
