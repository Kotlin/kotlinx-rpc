/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcProtocol
import kotlinx.rpc.grpc.test.AllPrimitives
import kotlinx.rpc.grpc.test.PrimitiveService
import kotlinx.rpc.grpc.test.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

class PrimitiveServiceImpl : PrimitiveService {
    override suspend fun echo(message: AllPrimitives): AllPrimitives {
        return message
    }
}

class TestPrimitiveService : GrpcTestBase() {
    override fun RpcServer<GrpcProtocol>.registerServices() {
        registerService<PrimitiveService> { PrimitiveServiceImpl() }
    }

    @Test
    fun testPrimitive() = runGrpcTest { grpcClient ->
        val service = grpcClient.service<PrimitiveService>()
        val result = service.echo(AllPrimitives {
            int32 = 42
        })

        assertEquals(42, result.int32)
    }
}
