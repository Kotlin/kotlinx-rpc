/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test

import io.github.timortel.kmpgrpc.core.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class KmpGrpcTest {

    val COUNT = 100000

    @Test
    fun kmpRun() = runBlocking {

        val request = echoRequest {
            message = "Echo"
        }

        val channel = Channel.Builder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build()

        val stub = EchoGrpc.EchoServiceStub(channel)

        repeat(COUNT) {
            val response = stub.UnaryEcho(request)
            assertEquals("Echo", response.message)
        }
    }

    @Test
    fun kotlinxRun() = runBlocking {
        val request = EchoRequest { message = "Echo" }

        val client = GrpcClient("localhost", 50051) {
            usePlaintext()
        }

        val service = client.withService<EchoService>()

        repeat(COUNT) {
            val response = service.UnaryEcho(request)
            assertEquals("Echo", response.message)
        }
    }


}