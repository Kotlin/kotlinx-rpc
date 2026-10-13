/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.CompletableDeferred
import kotlinx.io.Buffer
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.marshaller.GrpcEncodedMessage
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerResolver
import kotlinx.rpc.grpc.marshaller.GrpcMessageReader
import kotlinx.rpc.grpc.test.captureStdOut
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Grpc
private interface RunBatchExceptionService {
    suspend fun unary(value: String): String
}

// Regression test for https://github.com/Kotlin/kotlinx-rpc/issues/749
class RunBatchExceptionTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        registerService<RunBatchExceptionService> {
            object : RunBatchExceptionService {
                override suspend fun unary(value: String): String = value + value
            }
        }
    }

    @Test
    fun `exception in response marshaller should not produce uncaught exception`() {
        val decodeAttempted = CompletableDeferred<Unit>()
        val throwingDecodeMarshaller = object : GrpcMarshaller<String> by stringMarshaller {
            override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): String {
                decodeAttempted.complete(Unit)
                throw RuntimeException("Simulated decode failure")
            }
        }

        runGrpcTest(
            serverConfiguration = { messageMarshallerResolver = stringResolver(stringMarshaller) },
            clientConfiguration = { messageMarshallerResolver = stringResolver(throwingDecodeMarshaller) },
        ) { client ->
            val service = client.withService<RunBatchExceptionService>()
            val output = captureStdOut {
                assertFailsWith<GrpcStatusException> {
                    service.unary("Hello")
                }
            }

            assertTrue(decodeAttempted.isCompleted, "The client must attempt to decode the response.")
            assertFalse(
                output.contains("Uncaught Kotlin exception"),
                "The decode exception must reach the caller without an uncaught gRPC thread exception. Stdout: $output",
            )
        }
    }

    companion object {
        private fun stringResolver(marshaller: GrpcMarshaller<String>) = GrpcMarshallerResolver { type ->
            if (type.classifier == String::class) marshaller else null
        }

        private val stringMarshaller = object : GrpcMarshaller<String> {
            override fun prepare(value: String, config: GrpcMarshallerConfig?): GrpcEncodedMessage =
                GrpcEncodedMessage.of(Buffer().apply { writeString(value) })

            override fun decode(reader: GrpcMessageReader, config: GrpcMarshallerConfig?): String =
                reader.asSource().readString()
        }
    }
}
