/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.GrpcStatusCode
import kotlinx.rpc.grpc.GrpcStatusException
import kotlinx.rpc.grpc.client.GrpcClient
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

class GrpcWaitForReadyTest : GrpcTestBase() {
    private companion object {
        // Port 1 (tcpmux) is privileged and never has a listener in dev/CI environments, so
        // dialing it fails with an immediate connection refused. Unlike reserving an ephemeral
        // port by binding and releasing it, this cannot race with a concurrent test suite
        // being handed the same port between release and dial.
        const val UNAVAILABLE_PORT = 1
    }

    override fun RpcServer.registerServices() {
        registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `wait for ready keeps unavailable call pending until its deadline`() = runBlocking {
        val client = GrpcClient("localhost", UNAVAILABLE_PORT) {
            credentials = plaintext()
            clientInterceptor {
                callOptions.waitForReady = true
                callOptions.timeout = 500.milliseconds
                proceed(it)
            }.forEach { intercept(it) }
        }

        try {
            val error = assertFailsWith<GrpcStatusException> {
                client.withService<EchoService>().UnaryEcho(EchoRequest { message = "Echo" })
            }
            assertEquals(GrpcStatusCode.DEADLINE_EXCEEDED, error.getStatus().statusCode)
        } finally {
            client.shutdownNow()
            client.awaitTermination(30.seconds)
        }
    }

    @Test
    fun `wait for ready is unset by default`() {
        runGrpcTest(clientInterceptors = clientInterceptor {
            assertNull(callOptions.waitForReady)
            proceed(it)
        }) {
            it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Echo" })
        }
    }

    @Test
    fun `call without wait for ready fails fast when no connection is available`() = runBlocking {
        assertFailsFastWithUnavailable(waitForReady = null)
    }

    @Test
    fun `explicitly disabled wait for ready fails fast when no connection is available`() = runBlocking {
        assertFailsFastWithUnavailable(waitForReady = false)
    }

    private suspend fun assertFailsFastWithUnavailable(waitForReady: Boolean?) {
        val client = GrpcClient("localhost", UNAVAILABLE_PORT) {
            credentials = plaintext()
            clientInterceptor {
                callOptions.waitForReady = waitForReady
                // Hang guard: a call that incorrectly waits for ready hits this deadline and
                // surfaces as DEADLINE_EXCEEDED, failing the UNAVAILABLE assertion below.
                callOptions.timeout = 10.seconds
                proceed(it)
            }.forEach { intercept(it) }
        }

        try {
            val error = assertFailsWith<GrpcStatusException> {
                client.withService<EchoService>().UnaryEcho(EchoRequest { message = "Echo" })
            }
            assertEquals(GrpcStatusCode.UNAVAILABLE, error.getStatus().statusCode)
        } finally {
            client.shutdownNow()
            client.awaitTermination(30.seconds)
        }
    }

}
