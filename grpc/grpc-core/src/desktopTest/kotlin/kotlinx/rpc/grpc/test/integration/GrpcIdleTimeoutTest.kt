/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.GrpcClient
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.registerService
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class GrpcIdleTimeoutTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `idle timeout is propagated to the runtime`() {
        testIdleTimeout(2.seconds)
    }

    @Test
    fun `idle timeout rejects values below one second`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { idleTimeout = 999.milliseconds }
        }
    }

    @Test
    fun `idle timeout rejects the native unlimited sentinel as finite`() {
        assertFailsWith<IllegalArgumentException> {
            GrpcClient("localhost", 1) { idleTimeout = Int.MAX_VALUE.milliseconds }
        }
    }

    @Test
    fun `infinite idle timeout is accepted`() {
        runBlocking {
            val client = GrpcClient("localhost", 1) { idleTimeout = Duration.INFINITE }
            client.shutdownNow()
            client.awaitTermination(30.seconds)
        }
    }
}

internal expect fun GrpcTestBase.testIdleTimeout(timeout: Duration)
