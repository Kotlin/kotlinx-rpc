/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.test

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcServer
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class PrimitiveServiceImpl : PrimitiveService {
    override suspend fun Echo(message: AllPrimitives): AllPrimitives {
        return message
    }
}

class TestPrimitiveService {
    @Test
    fun testPrimitive(): Unit = runBlocking {
        val grpcClient = GrpcClient("localhost", 8080) {
            usePlaintext()
        }

        val grpcServer = GrpcServer(8080) {
            registerService<PrimitiveService> { PrimitiveServiceImpl() }
        }

        grpcServer.start()

        val service = grpcClient.withService<PrimitiveService>()
        val result = service.Echo(AllPrimitives {
            int32 = 42
        })

        assertEquals(42, result.int32)
    }
}
