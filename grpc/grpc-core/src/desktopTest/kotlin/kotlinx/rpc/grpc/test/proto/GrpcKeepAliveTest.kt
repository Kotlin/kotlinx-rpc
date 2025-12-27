/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.proto

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.registerService
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Tests that the client can configure the compression of requests.
 *
 * This test is hard to realize on native, as the gRPC-Core doesn't expose internal headers like
 * `grpc-encoding` to the user application. This means we cannot verify that the client or sever
 * actually sent those headers on native. Instead, we capture the grpc trace output (written to stderr)
 * and verify that the client and server actually used the compression algorithm.
 */
class GrpcKeepAliveTest : GrpcProtoTest() {
    override fun RpcServer.registerServices() {
        return registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `test keepalive set - should propagate settings to core libraries`() = testKeepAlive(
        time = 15.seconds,
        timeout = 5.seconds,
        withoutCalls = true,
    )

    @Test
    fun `test keepalive negative time - should fail`() {
        val error = assertFailsWith<IllegalArgumentException> {
            runGrpcTest(
                configure = {
                    keepAlive {
                        this.time = (-1).seconds
                    }
                }
            ) {
                // not reached
            }
        }
        assertContains(error.message!!, "keepalive time must be positive")
    }

    @Test
    fun `test keepalive negative timeout - should fail`() {
        val error = assertFailsWith<IllegalArgumentException> {
            runGrpcTest(
                configure = {
                    keepAlive {
                        this.timeout = (-1).seconds
                    }
                }
            ) {
                // not reached
            }
        }
        assertContains(error.message!!, "keepalive timeout must be positive")
    }
}

expect fun GrpcProtoTest.testKeepAlive(
    time: Duration,
    timeout: Duration,
    withoutCalls: Boolean,
)
