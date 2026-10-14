/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.grpc.client.internal.ManagedChannel
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.withService
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

internal actual fun GrpcTestBase.testIdleTimeout(timeout: Duration?) {
    runGrpcTest(clientConfiguration = {
        if (timeout != null) idleTimeout = timeout
    }) {
        it.withService<EchoService>().unaryEcho(EchoRequest { message = "Hello" })

        val idleTimeout = it.getField<ManagedChannel>("channel")
            .platformApi
            .getField<Long>("delegate", "idleTimeoutMillis")
            .milliseconds

        assertEquals(timeout ?: 30.minutes, idleTimeout)
    }
}
