/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.seconds
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.status
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.assertGrpcFailure
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class GrpcEdgeCaseTest {

    @Test
    fun `test flow retry - should always return the same status code`() {
        // create a client without starting a server
        val client = GrpcClient("invalid.host.jetbrains.com", 1234) {
            credentials = plaintext()
        }
        try {
            assertGrpcFailure(GrpcStatusCode.UNAVAILABLE) {
                runTest {
                    val service = client.withService<EchoService>()
                    service.ServerStreamingEcho(message = EchoRequest {
                        message = "Echo"
                    }).retryWhen { cause, attempt ->
                        // we expect the cause to be UNAVAILABLE for every retry
                        println("Caused by: $cause, attempt: $attempt")
                        assertEquals(GrpcStatusCode.UNAVAILABLE, (cause as? GrpcStatusException)?.status?.statusCode)
                        attempt < 3
                    }.collect { println(it) }
                }
            }
        } finally {
            client.shutdownNow()
            runBlocking { client.awaitTermination(30.seconds) }
        }
    }

}
