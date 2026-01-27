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

actual fun GrpcTestBase.testKeepAlive(
    time: Duration,
    timeout: Duration,
    withoutCalls: Boolean,
) = runTest {
    val logs = captureGrpcLogs(
        nativeTracers = listOf("pick_first")
    ) {
        runGrpcTest(
            clientConfiguration = {
                keepAlive {
                    this.time = time
                    this.timeout = timeout
                    this.withoutCalls = withoutCalls
                }
            }
        ) {
            it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Hello" })
        }
    }

    val keepAliveSettings = extractKeepAliveSettings(logs)
    assertEquals(time, keepAliveSettings.time)
    assertEquals(timeout, keepAliveSettings.timeout)
    assertEquals(withoutCalls, keepAliveSettings.permitWithoutCalls)
}

private data class KeepAliveSettings(
    val permitWithoutCalls: Boolean,
    val time: Duration,
    val timeout: Duration
)

private fun extractKeepAliveSettings(logs: String): KeepAliveSettings {
    val channelArgsPattern = Regex("""channel args: \{([^}]+)\}""")
    val channelArgsMatch = channelArgsPattern.find(logs)
        ?: error("Could not find channel args in logs")

    val argsText = channelArgsMatch.groupValues[1]

    val permitWithoutCalls = Regex("""grpc\.keepalive_permit_without_calls=(\d+)""")
        .find(argsText)?.groupValues?.get(1)?.toInt() == 1

    val timeMs = Regex("""grpc\.keepalive_time_ms=(\d+)""")
        .find(argsText)?.groupValues?.get(1)?.toInt()
        ?: error("Could not find grpc.keepalive_time_ms in logs")

    val timeoutMs = Regex("""grpc\.keepalive_timeout_ms=(\d+)""")
        .find(argsText)?.groupValues?.get(1)?.toInt()
        ?: error("Could not find grpc.keepalive_timeout_ms in logs")

    return KeepAliveSettings(
        permitWithoutCalls = permitWithoutCalls,
        time = timeMs.milliseconds,
        timeout = timeoutMs.milliseconds
    )
}
