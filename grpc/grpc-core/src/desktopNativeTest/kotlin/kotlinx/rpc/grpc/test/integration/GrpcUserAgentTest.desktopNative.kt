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

actual fun GrpcTestBase.testUserAgent(
    userAgent: String,
) = runTest {
    val logs = captureGrpcLogs(
        nativeTracers = listOf("pick_first")
    ) {
        runGrpcTest(
            clientConfiguration = {
                this.userAgent = userAgent
            }
        ) {
            it.withService<EchoService>().UnaryEcho(EchoRequest { message = "Hello" })
        }
    }

    assertEquals(userAgent, extractPrimaryUserAgent(logs))
}

private fun extractPrimaryUserAgent(logs: String): String {
    val channelArgsPattern = Regex("""channel args: \{([^}]+)\}""")
    val channelArgsMatch = channelArgsPattern.find(logs)
        ?: error("Could not find channel args in logs")

    val argsText = channelArgsMatch.groupValues[1]

    return Regex("""grpc\.primary_user_agent=([^,}]+)""")
        .find(argsText)?.groupValues?.get(1)?.trim()
        ?: error("Could not find grpc.primary_user_agent in logs")
}
