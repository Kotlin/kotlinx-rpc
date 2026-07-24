/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.raw

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.client.internal.unaryRpc
import kotlinx.rpc.grpc.descriptor.GrpcMethodType
import kotlinx.rpc.grpc.descriptor.methodDescriptor
import kotlinx.rpc.grpc.internal.serviceDescriptor
import kotlinx.rpc.grpc.marshaller.GrpcMarshaller
import kotlinx.rpc.grpc.marshaller.GrpcMarshallerConfig
import kotlinx.rpc.grpc.server.internal.PlatformServer
import kotlinx.rpc.grpc.server.internal.ServerBuilder
import kotlinx.rpc.grpc.server.internal.unaryServerMethodDefinition
import kotlinx.rpc.grpc.server.serverServiceDefinition
import kotlinx.rpc.grpc.test.captureStdErr
import kotlinx.rpc.grpc.test.captureStdOut
import kotlin.reflect.typeOf
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.seconds

/**
 * Regression test for https://github.com/Kotlin/kotlinx-rpc/issues/749
 *
 * Verifies that an exception thrown inside the `onSuccess` callback of
 * `NativeClientCall.runBatch` does not escape onto the native gRPC
 * completion queue thread as an unhandled exception.
 *
 * The test uses a response marshaller that throws on `decode()`. This call
 * sits inside `runBatch`'s `onSuccess` lambda (in `request()`) but outside
 * `safeUserCode`, so without the catch block the exception propagates through
 * `CallbackFuture.complete()` → `opsCompleteCb()` → native gRPC thread,
 * producing an "Uncaught Kotlin exception" warning (and SIGABRT on iOS).
 *
 * - Without the fix: stderr contains "Uncaught Kotlin exception" → test fails.
 * - With the fix: exception is caught in runBatch, no uncaught exception → test passes.
 */
class RunBatchExceptionTest {

    @Test
    fun `exception in response marshaller should not produce uncaught exception`() =
        kotlinx.coroutines.test.runTest {
            val serverJob = Job()
            val serverScope = CoroutineScope(serverJob)

            val serverDescriptor = methodDescriptor(
                fullMethodName = "$SERVICE_NAME/Unary",
                requestMarshaller = normalMarshaller,
                responseMarshaller = normalMarshaller,
                type = GrpcMethodType.UNARY,
                schemaDescriptor = Unit,
                idempotent = true,
                safe = true,
                sampledToLocalTracing = true,
            )

            val clientDescriptor = methodDescriptor(
                fullMethodName = "$SERVICE_NAME/Unary",
                requestMarshaller = normalMarshaller,
                responseMarshaller = throwingDecodeMarshaller,
                type = GrpcMethodType.UNARY,
                schemaDescriptor = Unit,
                idempotent = true,
                safe = true,
                sampledToLocalTracing = true,
            )

            val methods = listOf(serverDescriptor)

            val builder = ServerBuilder(0).addService(
                serverServiceDefinition(
                    serviceDescriptor = serviceDescriptor(
                        name = SERVICE_NAME,
                        methods = methods,
                        schemaDescriptor = Unit,
                    ),
                    methods = listOf(
                        serverScope.unaryServerMethodDefinition(
                            serverDescriptor,
                            typeOf<String>(),
                            emptyList(),
                        ) { it + it }
                    ),
                )
            )
            val server = PlatformServer(builder)
            server.start()

            val client = GrpcClient("localhost", server.port) {
                credentials = plaintext()
            }

            try {
                val output = captureStdOut {
                    assertFailsWith<GrpcStatusException> {
                        client.unaryRpc(clientDescriptor, "Hello")
                    }
                }

                assertFalse(
                    output.contains("Uncaught Kotlin exception"),
                    "Exception from response marshaller should be caught in runBatch, " +
                        "not escape as an uncaught exception onto the gRPC thread. " +
                        "Captured stdout: $output"
                )
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

        private val normalMarshaller = object : GrpcMarshaller<String> {
            override fun encode(value: String, config: GrpcMarshallerConfig?): Source {
                return Buffer().apply { writeString(value) }
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): String {
                return source.readString()
            }
        }

        private val throwingDecodeMarshaller = object : GrpcMarshaller<String> {
            override fun encode(value: String, config: GrpcMarshallerConfig?): Source {
                return Buffer().apply { writeString(value) }
            }

            override fun decode(source: Source, config: GrpcMarshallerConfig?): String {
                throw RuntimeException("Simulated decode failure (e.g. corrupted response)")
            }
        }
    }
}
