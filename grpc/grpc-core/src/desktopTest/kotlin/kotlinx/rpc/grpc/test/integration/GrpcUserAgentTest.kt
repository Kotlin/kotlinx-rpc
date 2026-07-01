/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.RpcServer
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.EchoServiceImpl
import kotlinx.rpc.registerService
import kotlin.test.Test

/**
 * Tests that the client-configured [userAgent] is applied at the channel level and used as a prefix
 * of the `User-Agent` actually sent on the wire.
 *
 * Like [GrpcKeepAliveTest], this is verified platform-specifically: on JVM by reflecting the composed
 * user-agent stored on the channel, on native by capturing the gRPC trace output and reading the
 * `grpc.primary_user_agent` channel argument.
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

expect fun GrpcTestBase.testUserAgent(
    userAgent: String,
)
