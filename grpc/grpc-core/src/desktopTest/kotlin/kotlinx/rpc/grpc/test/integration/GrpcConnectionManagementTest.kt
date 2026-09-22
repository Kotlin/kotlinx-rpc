/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.registerService
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GrpcConnectionManagementTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `connection limits are propagated to the runtime`() {
        testConnectionManagement(
            maxIdle = 5.seconds,
            maxAge = 10.seconds,
            maxAgeGrace = 2.seconds,
        )
    }

    @Test
    fun `connection idle rejects values below one second`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { maxConnectionIdle = 999.milliseconds }
        }
    }

    @Test
    fun `connection age rejects values below one second`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { maxConnectionAge = 999.milliseconds }
        }
    }

    @Test
    fun `connection age grace rejects negative values`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { maxConnectionAgeGrace = (-1).milliseconds }
        }
    }

    @Test
    fun `connection limits reject the native unlimited sentinel as finite`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcServer(0) { maxConnectionAge = Int.MAX_VALUE.milliseconds }
        }
    }

    @Test
    fun `infinite connection limits are accepted`() {
        runBlocking {
            val server = GrpcServer(0) {
                maxConnectionIdle = Duration.INFINITE
                maxConnectionAge = Duration.INFINITE
                maxConnectionAgeGrace = Duration.INFINITE
            }
            server.shutdownNow()
            server.awaitTermination(30.seconds)
        }
    }
}

internal expect fun GrpcTestBase.testConnectionManagement(
    maxIdle: Duration,
    maxAge: Duration,
    maxAgeGrace: Duration,
)
