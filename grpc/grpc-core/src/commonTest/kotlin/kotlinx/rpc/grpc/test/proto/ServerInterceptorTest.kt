/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.GrpcMetadata
import kotlinx.rpc.grpc.server.ServerCallScope
import kotlinx.rpc.grpc.server.ServerInterceptor
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
    fun `throw during onClosing - should fail propagate the exception to the server root`() {
        val error = assertFailsWith<IllegalStateException> {
            val interceptor = interceptor {
                onClose { _, _ -> throw IllegalStateException("Illegal failing in onClose") }
                proceed(it)
            }
            runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        }

        assertContains(error.message!!, "Illegal failing in onClose")
        // check that the error is indeed causing a server crash
        assertContains(error.stackTraceToString(), "suspendServerCall")
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
    fun `close in two interceptors - should fail with correct status on client`() {
        val error = assertFailsWith<StatusException> {
            val interceptor1 = interceptor {
                onClose { _, _ -> close(Status(StatusCode.UNAUTHENTICATED, "[1] Close in onClose"), GrpcMetadata()) }
                proceed(it)
            }
            val interceptor2 = interceptor {
                onClose { _, _ -> close(Status(StatusCode.UNAUTHENTICATED, "[2] Close in onClose"), GrpcMetadata()) }
                proceed(it)
            }
            runGrpcTest(serverInterceptors = interceptor1 + interceptor2, test = ::unaryCall)
        }

        assertEquals(StatusCode.UNAUTHENTICATED, error.getStatus().statusCode)
        assertContains(error.message!!, "[1] Close in onClose")
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

    @Test
    fun `proceedFlow - should succeed on client`() {
        val interceptor = interceptor {
            flow {
                proceedUnmodified(it)
            }
        }
        runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
    }

    @Test
    fun `test exact order of interceptor execution`() {
        val order = mutableListOf<Int>()
        val interceptor1 = interceptor { request ->
            flow {
                order.add(1)
                var i1 = 0
                val ids = listOf(3, 7)
                val req = request.map { order.add(ids[i1++]); it }

                var i2 = 0
                val respIds = listOf(6, 10)
                proceed(req).collect {
                    order.add(respIds[i2++])
                    emit(it)
                }

                order.add(12)
            }
        }

        val interceptor2 = interceptor { request ->
            flow {
                order.add(2)
                var i1 = 0
                val reqIds = listOf(4, 8)
                val req = request.map { order.add(reqIds[i1++]); it }

                var i2 = 0
                val respIds = listOf(5, 9)
                proceed(req).collect {
                    order.add(respIds[i2++])
                    emit(it)
                }

                order.add(11)
            }
        }
        val both = interceptor1 + interceptor2

        runGrpcTest(serverInterceptors = both) { bidiStream(it, 2) }

        assertEquals(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
            order
        )
    }

    @Test
    fun `method descriptor - full method name is exposed`() {
        var methodName: String? = null
        val interceptor = interceptor {
            methodName = method.getFullMethodName()
            proceed(it)
        }
        runGrpcTest(serverInterceptors = interceptor, test = ::unaryCall)
        assertContains(methodName!!, "EchoService/UnaryEcho")
    }

    private suspend fun unaryCall(grpcClient: GrpcClient) {
        val service = grpcClient.withService<EchoService>()
        val response = service.UnaryEcho(EchoRequest { message = "Hello" })
        assertEquals("Hello", response.message)
    }

    private suspend fun bidiStream(grpcClient: GrpcClient, count: Int = 5) {
        val service = grpcClient.withService<EchoService>()
        val responses = service.BidirectionalStreamingEcho(flow {
            repeat(count) {
                emit(EchoRequest { message = "Echo-$it" })
            }
        }).toList()
        assertEquals(count, responses.size)
        repeat(count) {
            assertEquals("Echo-$it", responses[it].message)
        }
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