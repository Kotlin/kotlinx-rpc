/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ServerTargetTest {
    @Test
    fun parsesHostAndPort() {
        assertEquals(ServerTarget("localhost", 50051), ServerTarget.parse("localhost:50051"))
    }

    @Test
    fun parsesIpv6HostWithBrackets() {
        assertEquals(ServerTarget("::1", 1234), ServerTarget.parse("[::1]:1234"))
    }

    @Test
    fun formatsIpv6HostWithBrackets() {
        assertEquals("[::1]:1234", ServerTarget("::1", 1234).toString())
        assertEquals("localhost:50051", ServerTarget("localhost", 50051).toString())
    }

    @Test
    fun rejectsInvalidTargets() {
        for (invalidTarget in listOf("", "localhost", "[::1]1234", ":50051", "localhost:0", "localhost:99999")) {
            assertFailsWith<IllegalArgumentException>("Expected '$invalidTarget' to be rejected.") {
                ServerTarget.parse(invalidTarget)
            }
        }
    }

    @Test
    fun reportsTheExpectedFormat() {
        val error = assertFailsWith<IllegalArgumentException> { ServerTarget.parse("localhost") }
        assertEquals("Invalid target 'localhost'. Expected HOST:PORT.", error.message)
    }
}
