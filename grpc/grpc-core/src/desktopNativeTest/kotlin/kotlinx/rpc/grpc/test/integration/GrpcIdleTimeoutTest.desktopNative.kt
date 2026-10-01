/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.test.runTest
import kotlinx.rpc.grpc.test.EchoRequest
import kotlinx.rpc.grpc.test.EchoService
import kotlinx.rpc.grpc.test.captureGrpcLogs
import kotlinx.rpc.grpc.test.invoke
import kotlinx.rpc.withService
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal actual fun GrpcTestBase.testIdleTimeout(timeout: Duration) = runTest {
    val logs = captureGrpcLogs(nativeTracers = listOf("pick_first")) {
        runGrpcTest(clientConfiguration = { idleTimeout = timeout }) {
            it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Hello" })
        }
    }

    val timeoutMs = Regex("""grpc\.client_idle_timeout_ms=(\d+)""")
        .find(logs)?.groupValues?.get(1)?.toInt()
        ?: error("Could not find grpc.client_idle_timeout_ms in logs")
    assertEquals(timeout, timeoutMs.milliseconds)
}
