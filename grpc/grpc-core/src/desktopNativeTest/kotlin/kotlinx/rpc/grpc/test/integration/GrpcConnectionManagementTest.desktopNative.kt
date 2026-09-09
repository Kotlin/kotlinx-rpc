/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.withService
import kotlin.test.assertEquals
import kotlin.time.Duration

/**
 * C-core does not log server channel args, so this only asserts that a server configured with the
 * connection limits still serves calls. The arg mapping itself is covered by [ServerChannelArgsTest].
 */
internal actual fun GrpcTestBase.testConnectionManagement(
    maxIdle: Duration,
    maxAge: Duration,
    maxAgeGrace: Duration,
) {
    runGrpcTest(
        serverConfiguration = {
            maxConnectionIdle = maxIdle
            maxConnectionAge = maxAge
            maxConnectionAgeGrace = maxAgeGrace
        }
    ) {
        val response = it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Hello" })
        assertEquals("Hello", response.message)
    }
}
