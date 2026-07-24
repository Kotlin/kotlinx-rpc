/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.grpc.internal.GrpcArg
import kotlinx.rpc.grpc.server.internal.buildServerChannelArgs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ServerChannelArgsTest {
    @Test
    fun `maps all configured options to channel args`() {
        // Arrange & Act
        val args = buildServerChannelArgs(
            maxInboundMessageSize = 4096,
            maxInboundMetadataSize = 1024,
            keepAliveTime = 30.seconds,
            keepAliveTimeout = 5.seconds,
            maxConnectionIdle = 5.minutes,
            maxConnectionAge = 10.minutes,
            maxConnectionAgeGrace = 30.seconds,
        ).toMap()

        // Assert
        assertEquals(4096, args["grpc.max_receive_message_length"])
        assertEquals(1024, args["grpc.max_metadata_size"])
        assertEquals(1024, args["grpc.absolute_max_metadata_size"])
        assertEquals(30_000, args["grpc.keepalive_time_ms"])
        assertEquals(5_000, args["grpc.keepalive_timeout_ms"])
        assertEquals(300_000, args["grpc.max_connection_idle_ms"])
        assertEquals(600_000, args["grpc.max_connection_age_ms"])
        assertEquals(30_000, args["grpc.max_connection_age_grace_ms"])
    }

    @Test
    fun `infinite durations map to the INT_MAX unbounded value`() {
        // Arrange & Act
        val args = buildServerChannelArgs(
            maxInboundMessageSize = null,
            maxInboundMetadataSize = null,
            keepAliveTime = Duration.INFINITE,
            keepAliveTimeout = Duration.INFINITE,
            maxConnectionIdle = Duration.INFINITE,
            maxConnectionAge = Duration.INFINITE,
            maxConnectionAgeGrace = Duration.INFINITE,
        ).toMap()

        // Assert
        assertEquals(Int.MAX_VALUE, args["grpc.keepalive_time_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.keepalive_timeout_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.max_connection_idle_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.max_connection_age_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.max_connection_age_grace_ms"])
    }

    @Test
    fun `unset options produce no args`() {
        // Arrange & Act
        val args = buildServerChannelArgs(
            maxInboundMessageSize = null,
            maxInboundMetadataSize = null,
            keepAliveTime = null,
            keepAliveTimeout = null,
            maxConnectionIdle = null,
            maxConnectionAge = null,
            maxConnectionAgeGrace = null,
        )

        // Assert
        assertTrue(args.isEmpty())
    }

    @Test
    fun `finite duration at the INT_MAX boundary is rejected rather than truncated`() {
        // Arrange & Act & Assert
        // Int.MAX_VALUE is what Duration.INFINITE maps to, so a finite value must stay below it.
        assertFailsWith<IllegalArgumentException> {
            buildServerChannelArgs(maxConnectionAge = Int.MAX_VALUE.milliseconds)
        }
    }

    @Test
    fun `duration above the Int range is rejected rather than wrapping`() {
        // Arrange & Act & Assert
        // 30 days overflows a C int; without the guard it would wrap to a nonsense value.
        assertFailsWith<IllegalArgumentException> {
            buildServerChannelArgs(maxConnectionAge = 30.days)
        }
    }

    @Test
    fun `negative duration is rejected rather than wrapping`() {
        // Arrange & Act & Assert
        assertFailsWith<IllegalArgumentException> {
            buildServerChannelArgs(maxConnectionAgeGrace = (-1).days)
        }
    }

    private fun buildServerChannelArgs(
        maxConnectionAge: Duration? = null,
        maxConnectionAgeGrace: Duration? = null,
    ): List<GrpcArg> = buildServerChannelArgs(
        maxInboundMessageSize = null,
        maxInboundMetadataSize = null,
        keepAliveTime = null,
        keepAliveTimeout = null,
        maxConnectionIdle = null,
        maxConnectionAge = maxConnectionAge,
        maxConnectionAgeGrace = maxConnectionAgeGrace,
    )

    private fun List<GrpcArg>.toMap(): Map<String, Int> =
        associate { it.key to (it as GrpcArg.Integer).value }
}
