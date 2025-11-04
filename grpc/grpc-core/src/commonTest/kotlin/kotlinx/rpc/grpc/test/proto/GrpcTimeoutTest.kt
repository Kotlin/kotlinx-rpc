/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.StatusCode
import kotlinx.rpc.grpc.StatusException
import kotlinx.rpc.grpc.statusCode
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GrpcTimeoutTest : GrpcProtoTest() {

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `test timeout causes DEADLINE_EXCEEDED when call exceeds timeout`() {
        val exc = assertFailsWith<StatusException> {
            runGrpcTest(
                clientInterceptors = clientInterceptor {
                    callOptions.timeout = 500.milliseconds
                    proceed(it)
                }
            ) {
                // Server will delay for 1 second, but timeout is 0.5 seconds
                val request = EchoRequest { message = "Echo"; timeout = 1000u }
                it.withService<EchoService>().UnaryEcho(request)
            }
        }
        assertEquals(StatusCode.DEADLINE_EXCEEDED, exc.getStatus().statusCode)
    }

    @Test
    fun `test timeout does not trigger when call completes within timeout`() {
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                callOptions.timeout = 2.seconds
                proceed(it)
            }
        ) {
            // Server will delay for 500ms, timeout is 2 seconds
            val request = EchoRequest { message = "Success"; timeout = 500u }
            val response = it.withService<EchoService>().UnaryEcho(request)
            assertEquals("Success", response.message)
        }
    }

    @Test
    fun `test default timeout is null`() {
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                // Verify default timeout is null
                assertNull(callOptions.timeout)
                proceed(it)
            }
        ) {
            val request = EchoRequest { message = "Default timeout"; timeout = 100u }
            val response = it.withService<EchoService>().UnaryEcho(request)
            assertEquals("Default timeout", response.message)
        }
    }

    @Test
    fun `test timeout applies to actual call duration not just processing time`() {
        val exc = assertFailsWith<StatusException> {
            runGrpcTest(
                clientInterceptors = clientInterceptor {
                    // Set timeout before making the call
                    callOptions.timeout = 500.milliseconds
                    proceed(it)
                }
            ) {
                // Server delays for 1 second
                val request = EchoRequest { message = "Echo"; timeout = 500u }
                it.withService<EchoService>().UnaryEcho(request)
            }
        }
        assertEquals(StatusCode.DEADLINE_EXCEEDED, exc.getStatus().statusCode)
    }

    @Test
    fun `test timeout set to very short milliseconds triggers immediately`() {
        val exc = assertFailsWith<StatusException> {
            runGrpcTest(
                clientInterceptors = clientInterceptor {
                    callOptions.timeout = 1.milliseconds
                    proceed(it)
                }
            ) {
                // Even with no server delay, 0ms timeout should trigger
                val request = EchoRequest { message = "Echo"; timeout = 0u }
                it.withService<EchoService>().UnaryEcho(request)
            }
        }
        assertEquals(StatusCode.DEADLINE_EXCEEDED, exc.getStatus().statusCode)
    }

    @Test
    fun `test timeout boundary condition - call completes just before timeout`() {
        runGrpcTest(
            clientInterceptors = clientInterceptor {
                callOptions.timeout = 2.seconds
                proceed(it)
            }
        ) {
            // Server delays for slightly less than timeout
            val request = EchoRequest { message = "Just in time"; timeout = 1800u }
            val response = it.withService<EchoService>().UnaryEcho(request)
            assertEquals("Just in time", response.message)
        }
    }

}