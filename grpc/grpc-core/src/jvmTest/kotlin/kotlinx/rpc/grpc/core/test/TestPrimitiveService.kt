/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.core.test

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.test.AllPrimitives
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.grpc.test.PrimitiveService
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

class PrimitiveServiceImpl : PrimitiveService {
    override suspend fun Echo(message: AllPrimitives): AllPrimitives {
        return message
    }
}

class TestPrimitiveService : GrpcServerTest() {
    override fun RpcServer.registerServices() {
        registerService<PrimitiveService> { PrimitiveServiceImpl() }
    }

    @Test
    fun testPrimitive(): Unit = runGrpcTest { grpcClient ->
        val service = grpcClient.withService<PrimitiveService>()
        val result = service.Echo(AllPrimitives {
            int32 = 42
        })

        assertEquals(42, result.int32)
    }
}
