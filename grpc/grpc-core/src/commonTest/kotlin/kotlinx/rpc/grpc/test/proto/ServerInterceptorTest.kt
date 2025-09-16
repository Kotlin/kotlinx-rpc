/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcTrailers
import kotlinx.rpc.grpc.ServerCallScope
import kotlinx.rpc.grpc.ServerInterceptor
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerInterceptorTest : GrpcProtoTest() {

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `throw during intercept - should fail with thrown exception`() {
        val error = assertFailsWith<IllegalStateException> {
            val interceptor = interceptor { scope, headers, request ->
                scope.proceed(request)
            }
            runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(error.message, "Failing in interceptor")
    }


    private suspend fun unaryCall(grpcClient: GrpcClient) {
        val service = grpcClient.withService<EchoService>()
        val response = service.UnaryEcho(EchoRequest { message = "Hello" })
        assertEquals("Hello", response.message)
    }
}


private fun interceptor(
    block: (ServerCallScope<Any, Any>, GrpcTrailers, Flow<Any>) -> Flow<Any>,
): List<ServerInterceptor> {
    return listOf(object : ServerInterceptor {
        @Suppress("UNCHECKED_CAST")
        override fun <Req, Resp> intercept(
            scope: ServerCallScope<Req, Resp>,
            requestHeaders: GrpcTrailers,
            request: Flow<Req>,
        ): Flow<Resp> {
            return block(scope as ServerCallScope<Any, Any>, requestHeaders, request as Flow<Any>) as Flow<Resp>
        }
    })
}