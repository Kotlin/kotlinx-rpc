/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class GrpcServerConfigurationTest {
    @Test
    fun connectionManagementDefaults() {
        val config = GrpcServerConfiguration()

        assertNull(config.keepAlive)
        assertEquals(Duration.INFINITE, config.maxConnectionIdle)
        assertEquals(Duration.INFINITE, config.maxConnectionAge)
        assertEquals(Duration.INFINITE, config.maxConnectionAgeGrace)

        config.keepAlive {}
        val keepAlive = assertNotNull(config.keepAlive)
        assertEquals(2.hours, keepAlive.time)
        assertEquals(20.seconds, keepAlive.timeout)
    }

    @Test
    fun messageAndMetadataSizeBoundaries() {
        val config = GrpcServerConfiguration()
        for ((property, minimum) in listOf(config::maxInboundMessageSize to 0, config::maxInboundMetadataSize to 1)) {
            for (value in listOf(minimum, minimum + 1, Int.MAX_VALUE)) {
                property.set(value)
                assertEquals(value, property.get(), property.name)
            }
            for (value in listOf(Int.MIN_VALUE, -1, minimum - 1).distinct()) {
                val error = assertFailsWith<IllegalArgumentException>("${property.name}: $value") {
                    property.set(value)
                }
                assertContains(error.message.orEmpty(), property.name)
                assertEquals(Int.MAX_VALUE, property.get(), property.name)
            }
        }
    }

    @Test
    fun durationBoundaries() {
        val config = GrpcServerConfiguration().apply { keepAlive {} }
        val keepAlive = assertNotNull(config.keepAlive)
        for ((property, minimum) in listOf(
            config::maxConnectionIdle to 1.seconds,
            config::maxConnectionAge to 1.seconds,
            config::maxConnectionAgeGrace to Duration.ZERO,
            keepAlive::time to 10.seconds,
            keepAlive::timeout to 10.milliseconds,
        )) {
            val accepted = listOf(
                minimum,
                minimum + 1.nanoseconds,
                Int.MAX_VALUE.milliseconds - 1.nanoseconds,
                Duration.INFINITE,
            )
            for (value in accepted) {
                property.set(value)
                assertEquals(value, property.get(), property.name)
            }
            val rejected = listOf(
                -Duration.INFINITE,
                (-1).nanoseconds,
                minimum - 1.nanoseconds,
                Int.MAX_VALUE.milliseconds,
                Int.MAX_VALUE.milliseconds + 1.nanoseconds,
            )
            for (value in rejected) {
                assertFailsWith<IllegalArgumentException>("${property.name}: $value") { property.set(value) }
                assertEquals(Duration.INFINITE, property.get(), property.name)
            }
        }
    }

    @Test
    fun keepAliveBlocksAndConfigurationsHaveIndependentState() {
        val config = GrpcServerConfiguration()
        config.keepAlive {
            time = 10.seconds
            timeout = 10.milliseconds
        }
        config.keepAlive {}

        val keepAlive = assertNotNull(config.keepAlive)
        assertEquals(2.hours, keepAlive.time)
        assertEquals(20.seconds, keepAlive.timeout)
        assertNull(GrpcServerConfiguration().keepAlive)
    }
}
