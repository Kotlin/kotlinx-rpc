/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.annotations.Grpc
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoResponse
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals

@Grpc(protoServiceName = "TestService")
private interface ServerService {
    suspend fun echo(request: EchoRequest): EchoResponse
}

@Grpc(protoServiceName = "TestService")
private interface ClientService {
    suspend fun echo(request: EchoRequest): EchoResponse
}

class GrpcServiceNameTest : GrpcTestBase() {

    override fun RpcServer.registerServices() {
        registerService<ServerService> { object : ServerService {
            override suspend fun echo(request: EchoRequest): EchoResponse =
                EchoResponse { message = request.message }
        } }
    }

    @Test
    fun `test two service interfaces with the same name`() = runGrpcTest { client ->
        val request = EchoRequest { message = "Hello" }
        val response = client.withService<ClientService>().echo(request)
        assertEquals("Hello", response.message)
    }
}