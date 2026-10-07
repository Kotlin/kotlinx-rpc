/*
 * Copyright 2023-2025 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
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
    fun `client minimum keepalive durations propagate to the runtime`() = testKeepAlive(
        time = 10.seconds,
        timeout = 10.milliseconds,
        withoutCalls = false,
    )

    @Test
    fun `server accepts minimum keepalive durations`() = testServerKeepAlive(
        time = 10.seconds,
        timeout = 10.milliseconds,
    )

    @Test
    fun `client rejects keepalive time below ten seconds`() {
        for (time in listOf(Duration.ZERO, 1.milliseconds, 10.seconds - 1.nanoseconds)) {
            val error = assertFailsWith<IllegalArgumentException> {
                GrpcClient("localhost", 1) { keepAlive { this.time = time } }
            }
            assertContains(error.message!!, "keepalive time must be at least 10s")
        }
    }

    @Test
    fun `server rejects keepalive time below ten seconds`() {
        for (time in listOf(Duration.ZERO, 1.milliseconds, 10.seconds - 1.nanoseconds)) {
            val error = assertFailsWith<IllegalArgumentException> {
                GrpcServer(0) { keepAlive { this.time = time } }
            }
            assertContains(error.message!!, "keepalive time must be at least 10s")
        }
    }

    @Test
    fun `client rejects keepalive timeout below ten milliseconds`() {
        for (timeout in listOf(Duration.ZERO, 1.milliseconds, 10.milliseconds - 1.nanoseconds)) {
            val error = assertFailsWith<IllegalArgumentException> {
                GrpcClient("localhost", 1) { keepAlive { this.timeout = timeout } }
            }
            assertContains(error.message!!, "keepalive timeout must be at least 10ms")
        }
    }

    @Test
    fun `server rejects keepalive timeout below ten milliseconds`() {
        for (timeout in listOf(Duration.ZERO, 1.milliseconds, 10.milliseconds - 1.nanoseconds)) {
            val error = assertFailsWith<IllegalArgumentException> {
                GrpcServer(0) { keepAlive { this.timeout = timeout } }
            }
            assertContains(error.message!!, "keepalive timeout must be at least 10ms")
        }
    }

    @Test
    fun `infinite keepalive durations are accepted`() {
        runGrpcTest(
            clientConfiguration = {
                keepAlive {
                    time = Duration.INFINITE
                    timeout = Duration.INFINITE
                }
            },
            serverConfiguration = {
                keepAlive {
                    time = Duration.INFINITE
                    timeout = Duration.INFINITE
                }
            },
        ) {
            val response = it.withService<EchoService>().unaryEcho(EchoRequest { message = "Hello" })
            assertEquals("Hello", response.message)
        }
    }

    @Test
    fun `client rejects negative keepalive time`() {
        val error = assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { keepAlive { time = (-1).seconds } }
        }
        assertContains(error.message!!, "keepalive time must be at least 10s")
    }

    @Test
    fun `client rejects negative keepalive timeout`() {
        val error = assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { keepAlive { timeout = (-1).seconds } }
        }
        assertContains(error.message!!, "keepalive timeout must be at least 10ms")
    }

    @Test
    fun `server rejects negative keepalive time`() {
        val error = assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { keepAlive { time = (-1).seconds } }
        }
        assertContains(error.message!!, "keepalive time must be at least 10s")
    }

    @Test
    fun `server rejects negative keepalive timeout`() {
        val error = assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { keepAlive { timeout = (-1).seconds } }
        }
        assertContains(error.message!!, "keepalive timeout must be at least 10ms")
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
