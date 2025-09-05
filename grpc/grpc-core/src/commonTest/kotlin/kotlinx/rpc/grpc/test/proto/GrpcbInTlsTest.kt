/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import hello.HelloRequest
import hello.HelloService
import hello.invoke
import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.withService
import kotlin.test.Test


class GrpcbInTlsTest {

    val grpcClient = GrpcClient("grpcb.in", 9001) {
//        usePlaintext()
    }

    @Test
    fun testTlsCall() = runTest {
        val service = grpcClient.withService<HelloService>()

        val request = HelloRequest {
            greeting = "Postman"
        }
        val result = service.SayHello(request)

        println(result.reply)
    }


}