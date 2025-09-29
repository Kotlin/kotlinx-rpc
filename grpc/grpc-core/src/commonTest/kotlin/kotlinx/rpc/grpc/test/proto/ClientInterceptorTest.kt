/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.ClientCallScope
import kotlinx.rpc.grpc.ClientInterceptor
import kotlinx.rpc.grpc.GrpcClient
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
import kotlin.test.assertTrue

class ClientInterceptorTest : GrpcProtoTest() {

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `throw during intercept - should fail with thrown exception`() {
        val error = assertFailsWith<IllegalStateException> {
            val interceptor = interceptor {
                throw IllegalStateException("Failing in interceptor")
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(error.message, "Failing in interceptor")
    }

    @Test
    fun `throw during onHeader - should fail with status exception containing the thrown exception`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                onHeaders {
                    throw IllegalStateException("Failing in onHeader")
                }
                proceed(it)
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Failing in onHeader", error.cause?.message)
    }

    @Test
    fun `throw during onClose - should fail with status exception containing the thrown exception`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                onClose { _, _ ->
                    throw IllegalStateException("Failing in onClose")
                }
                proceed(it)
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Failing in onClose", error.cause?.message)
    }

    @Test
    fun `cancel in intercept - should fail with cancellation`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                cancel("Canceling in interceptor", IllegalStateException("Cancellation cause"))
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::unaryCall)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertContains(error.message!!, "Canceling in interceptor")
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Cancellation cause", error.cause?.message)
    }

    @Test
    fun `cancel in request flow - should fail with cancellation`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                proceed(it.map {
                    val msg = it as EchoRequest
                    if (msg.message == "Echo-3") {
                        cancel("Canceling in request flow", IllegalStateException("Cancellation cause"))
                    }
                    it
                })
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::bidiStream)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertContains(error.message!!, "Canceling in request flow")
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Cancellation cause", error.cause?.message)
    }

    @Test
    fun `cancel in response flow - should fail with cancellation`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                flow {
                    proceed(it).collect { resp ->
                        val msg = resp as EchoResponse
                        if (msg.message == "Echo-3") {
                            cancel("Canceling in response flow", IllegalStateException("Cancellation cause"))
                        }
                        emit(resp)
                    }
                }
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::bidiStream)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertContains(error.message!!, "Canceling in response flow")
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Cancellation cause", error.cause?.message)
    }

    @Test
    fun `cancel onHeaders - should fail with cancellation`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                this.onHeaders {
                    cancel("Canceling in headers", IllegalStateException("Cancellation cause"))
                }
                proceed(it)
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::bidiStream)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertContains(error.message!!, "Canceling in headers")
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Cancellation cause", error.cause?.message)
    }

    @Test
    fun `cancel onClose - should fail with cancellation`() {
        val error = assertFailsWith<StatusException> {
            val interceptor = interceptor {
                this.onClose { _, _ ->
                    cancel("Canceling in onClose", IllegalStateException("Cancellation cause"))
                }
                proceed(it)
            }
            runGrpcTest(clientInterceptors = interceptor, test = ::bidiStream)
        }
        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertContains(error.message!!, "Canceling in onClose")
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Cancellation cause", error.cause?.message)
    }

    @Test
    fun `cancel in two interceptors - should fail with cancellation`() {
        val error = assertFailsWith<StatusException> {
            val interceptor1 = interceptor {
                onClose { _, _ -> cancel("[1] Canceling in onClose", IllegalStateException("Cancellation cause")) }
                proceed(it)
            }
            val interceptor2 = interceptor {
                onClose { _, _ -> cancel("[2] Canceling in onClose", IllegalStateException("Cancellation cause")) }
                proceed(it)
            }
            runGrpcTest(clientInterceptors = interceptor1 + interceptor2, test = ::unaryCall)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertContains(error.message!!, "[1] Canceling in onClose")
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Cancellation cause", error.cause?.message)
    }

    @Test
    fun `cancel in two interceptors withing response stream - should fail with cancellation`() {
        val error = assertFailsWith<StatusException> {
            val interceptor1 = interceptor {
                proceed(it).map {
                    val msg = it as EchoResponse
                    if (msg.message == "Echo-3") {
                        cancel("[1] Canceling in response flow", IllegalStateException("Cancellation cause"))
                    }
                    it
                }
            }
            val interceptor2 = interceptor {
                proceed(it).map {
                    val msg = it as EchoResponse
                    // this is cancelled before the first one
                    if (msg.message == "Echo-2") {
                        cancel("[2] Canceling in response flow", IllegalStateException("Cancellation cause"))
                    }
                    it
                }
            }
            runGrpcTest(clientInterceptors = interceptor1 + interceptor2, test = ::bidiStream)
        }

        assertEquals(StatusCode.CANCELLED, error.getStatus().statusCode)
        assertContains(error.message!!, "[2] Canceling in response flow")
        assertIs<IllegalStateException>(error.cause)
        assertEquals("Cancellation cause", error.cause?.message)
    }

    @Test
    fun `modify request message - should return modified message`() {
        val interceptor = interceptor {
            val modified = it.map { EchoRequest { message = "Modified" } }
            proceed(modified)
        }
        runGrpcTest(clientInterceptors = interceptor) {
            val service = it.withService<EchoService>()
            val response = service.UnaryEcho(EchoRequest { message = "Hello" })
            assertEquals("Modified", response.message)
        }
    }

    @Test
    fun `modify response message - should return modified message`() {
        val interceptor = interceptor {
            proceed(it).map { EchoResponse { message = "Modified" } }
        }
        runGrpcTest(clientInterceptors = interceptor) {
            val service = it.withService<EchoService>()
            val response = service.UnaryEcho(EchoRequest { message = "Hello" })
            assertEquals("Modified", response.message)
        }
    }

    @Test
    fun `append a response message once closed`() {
        val interceptor = interceptor {
            channelFlow {
                proceed(it).collect {
                    trySend(it)
                }
                onClose { status, _ ->
                    trySend(EchoResponse { message = "Appended-after-close-with-${status.statusCode}" })
                }
            }
        }

        runGrpcTest(
            clientInterceptors = interceptor
        ) { client ->
            val svc = client.withService<EchoService>()
            val responses = svc.BidirectionalStreamingEcho(flow {
                repeat(5) {
                    emit(EchoRequest { message = "Eccchhooo" })
                }
            }).toList()
            assertEquals(6, responses.size)
            assertTrue(responses.any { it.message == "Appended-after-close-with-OK" })
        }
    }

    @Test
    fun `test exact order of interceptor execution`() {
        val order = mutableListOf<Int>()
        val interceptor1 = interceptor { request ->
            order.add(1)
            flow {
                order.add(2)
                val req = request.map { order.add(5); it }
                proceed(req).collect {
                    order.add(8)
                    emit(it)
                }
                order.add(10)
            }
        }
        val interceptor2 = interceptor { request ->
            order.add(3)
            flow {
                order.add(4)
                val req = request.map { order.add(6); it }
                proceed(req).collect {
                    order.add(7)
                    emit(it)
                }
                order.add(9)
            }
        }

        val both = interceptor1 + interceptor2
        runGrpcTest(clientInterceptors = both) { unaryCall(it) }

        assertEquals(
            listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
            order
        )
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
    block: ClientCallScope<Any, Any>.(Flow<Any>) -> Flow<Any>,
): List<ClientInterceptor> {
    return listOf(object : ClientInterceptor {
        @Suppress("UNCHECKED_CAST")
        override fun <Req, Resp> ClientCallScope<Req, Resp>.intercept(
            request: Flow<Req>,
        ): Flow<Resp> {
            with(this as ClientCallScope<Any, Any>) {
                return block(request as Flow<Any>) as Flow<Resp>
            }
        }
    })
}