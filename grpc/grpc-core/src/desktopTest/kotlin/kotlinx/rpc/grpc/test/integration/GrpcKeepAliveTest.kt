/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.registerService
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class GrpcKeepAliveTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `client keepalive propagates to the runtime`() = testKeepAlive(
        time = 15.seconds,
        timeout = 5.seconds,
        withoutCalls = true,
    )

    @Test
    fun `server keepalive propagates to the runtime`() = testServerKeepAlive(
        time = 15.seconds,
        timeout = 5.seconds,
    )

    @Test
    fun `client rejects negative keepalive time`() {
        val error = assertFailsWith<IllegalArgumentException> {
            runGrpcTest(
                clientConfiguration = {
                    keepAlive {
                        this.time = (-1).seconds
                    }
                }
            ) {}
        }
        assertContains(error.message!!, "keepalive time must be at least 1ms")
    }

    @Test
    fun `client rejects negative keepalive timeout`() {
        val error = assertFailsWith<IllegalArgumentException> {
            runGrpcTest(
                clientConfiguration = {
                    keepAlive {
                        this.timeout = (-1).seconds
                    }
                }
            ) {}
        }
        assertContains(error.message!!, "keepalive timeout must be at least 1ms")
    }

    @Test
    fun `server rejects negative keepalive time`() {
        val error = assertFailsWith<IllegalArgumentException> {
            runGrpcTest(
                serverConfiguration = {
                    keepAlive {
                        this.time = (-1).seconds
                    }
                }
            ) {}
        }
        assertContains(error.message!!, "keepalive time must be at least 1ms")
    }

    @Test
    fun `server rejects negative keepalive timeout`() {
        val error = assertFailsWith<IllegalArgumentException> {
            runGrpcTest(
                serverConfiguration = {
                    keepAlive {
                        this.timeout = (-1).seconds
                    }
                }
            ) {}
        }
        assertContains(error.message!!, "keepalive timeout must be at least 1ms")
    }
}

expect fun GrpcTestBase.testKeepAlive(
    time: Duration,
    timeout: Duration,
    withoutCalls: Boolean,
)

expect fun GrpcTestBase.testServerKeepAlive(
    time: Duration,
    timeout: Duration,
)
