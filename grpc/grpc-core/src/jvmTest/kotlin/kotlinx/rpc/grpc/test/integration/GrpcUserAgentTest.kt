/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.client.internal.ManagedChannel
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.registerService
import kotlinx.rpc.withService
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Tests that the client-configured [userAgent] is applied at the channel level and used as a prefix
 * of the `User-Agent` actually sent on the wire.
 *
 * Like [GrpcKeepAliveTest], the check reflects the composed user-agent stored on the channel.
 */
class GrpcUserAgentTest : GrpcTestBase() {
    override fun RpcServer.registerServices() {
        return registerService<EchoService> { EchoServiceImpl() }
    }

    @Test
    fun `test userAgent set - should propagate as prefix to core libraries`() = testUserAgent(
        userAgent = "MyApp/1.2.3",
    )
}

fun GrpcTestBase.testUserAgent(
    userAgent: String,
) {
    runGrpcTest(
        clientConfiguration = {
            this.userAgent = userAgent
        }
    ) {
        it.withService<EchoService>().unaryEcho(EchoRequest { message = "Hello" })

        // grpc-java stores the composed user-agent ("<prefix> grpc-java-<transport>/<version>")
        // on ManagedChannelImpl, reachable through the forwarding wrapper's `delegate`.
        val composedUserAgent = it.getField<ManagedChannel>("channel")
            .getField<String>("channel", "delegate", "userAgent")

        assertTrue(
            composedUserAgent.startsWith(userAgent),
            "Expected composed user-agent to start with '$userAgent', but was '$composedUserAgent'",
        )
    }
}
