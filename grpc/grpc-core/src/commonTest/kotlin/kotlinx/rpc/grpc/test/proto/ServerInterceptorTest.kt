/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcClient
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.ServerCallScope
import kotlinx.rpc.grpc.ServerInterceptor
import kotlinx.rpc.grpc.Status
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoResponse
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ServerInterceptorTest : GrpcProtoTest() {

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `throw during intercept - should fail with unknown status on client`() {
        var cause: Throwable? = null
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                onClose { status, _ -> cause = status.getCause() }
                // this exception is not propagated to the client (only as UNKNOWN status code)
                throw IllegalStateException("Failing in interceptor")
            }
            runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.UNKNOWN, error.getStatus().statusCode)
        assertIs<IllegalStateException>(cause)
        assertEquals("Failing in interceptor", cause?.message)
    }


    @Test
    fun `close during intercept - should fail with correct status on client`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                close(Status(StatusCode.UNAUTHENTICATED, "Close in interceptor"), GrpcMetadata())
            }
            runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.UNAUTHENTICATED, error.getStatus().statusCode)
        assertContains(error.getStatus().getDescription()!!, "Close in interceptor")
    }

    @Test
    fun `close during request flow - should fail with correct status on client`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                proceed(
                    it.map {
                        close(Status(StatusCode.UNAUTHENTICATED, "Close in request flow"), GrpcMetadata())
                    }
                )
            }
            runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.UNAUTHENTICATED, error.getStatus().statusCode)
        assertContains(error.message!!, "Close in request flow")
    }

    @Test
    fun `close during response flow - should fail with correct status on client`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                proceed(it).map {
                    close(Status(StatusCode.UNAUTHENTICATED, "Close in response flow"), GrpcMetadata())
                }
            }
            runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.UNAUTHENTICATED, error.getStatus().statusCode)
        assertContains(error.message!!, "Close in response flow")
    }

    @Test
    fun `close during onClose - should fail with correct status on client`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                onClose { _, _ -> close(Status(StatusCode.UNAUTHENTICATED, "Close in onClose"), GrpcMetadata()) }
                proceed(it)
            }
            runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.UNAUTHENTICATED, error.getStatus().statusCode)
        assertContains(error.message!!, "Close in onClose")
    }

    @Test
    fun `dont proceed and return custom message - should succeed on client`() {
        val interceptor = interceptor {
            flowOf(EchoResponse { message = "Custom message" })
        }
        runGrpcTest(serverInterceptors = interceptor) {
            val service = it.withService<EchoService>()
            val response = service.UnaryEcho(EchoRequest { message = "Hello" })
            assertEquals("Custom message", response.message)
        }
    }

    @Test
    fun `manipulate request - should succeed on client`() {
        val interceptor = interceptor {
            proceed(it.map { EchoRequest { message = "Modified" } })
        }
        runGrpcTest(serverInterceptors = interceptor) {
            val service = it.withService<EchoService>()
            val response = service.UnaryEcho(EchoRequest { message = "Hello" })
            assertEquals("Modified", response.message)
        }
    }

    @Test
    fun `manipulate response - should succeed on client`() {
        val interceptor = interceptor {
            proceed(it).map { EchoResponse { message = "Modified" } }
        }
        runGrpcTest(serverInterceptors = interceptor) {
            val service = it.withService<EchoService>()
            val response = service.UnaryEcho(EchoRequest { message = "Hello" })
            assertEquals("Modified", response.message)
        }
    }

    private suspend fun unaryCall(grpcClient: GrpcClient) {
        val service = grpcClient.withService<EchoService>()
        val response = service.UnaryEcho(EchoRequest { message = "Hello" })
        assertEquals("Hello", response.message)
    }
}


private fun interceptor(
    block: ServerCallScope<Any, Any>.(Flow<Any>) -> Flow<Any>,
): List<ServerInterceptor> {
    return listOf(object : ServerInterceptor {
        @Suppress("UNCHECKED_CAST")
        override fun <Req, Resp> ServerCallScope<Req, Resp>.intercept(
            request: Flow<Req>,
        ): Flow<Resp> {
            with(this as ServerCallScope<Any, Any>) {
                return block(request as Flow<Any>) as Flow<Resp>
            }
        }
    })
}