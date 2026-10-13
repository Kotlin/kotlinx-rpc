/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.rpc.grpc.internal.GrpcArg
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.GrpcServerConfiguration
import kotlinx.rpc.grpc.server.internal.buildServerChannelArgs
import kotlinx.rpc.grpc.server.internal.channelArgs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ServerChannelArgsTest {
    @Test
    fun `maps all configured options to channel args`() {
        val args = buildServerChannelArgs(
            maxInboundMessageSize = 4096,
            maxInboundMetadataSize = 1024,
            keepAliveTime = 30.seconds,
            keepAliveTimeout = 5.seconds,
            maxConnectionIdle = 5.minutes,
            maxConnectionAge = 10.minutes,
            maxConnectionAgeGrace = 30.seconds,
        ).toMap()

        assertEquals(4096, args["grpc.max_receive_message_length"])
        assertEquals(1024, args["grpc.max_metadata_size"])
        assertEquals(1024, args["grpc.absolute_max_metadata_size"])
        assertEquals(30_000, args["grpc.keepalive_time_ms"])
        assertEquals(5_000, args["grpc.keepalive_timeout_ms"])
        assertEquals(1, args["grpc.keepalive_permit_without_calls"])
        assertEquals(300_000, args["grpc.max_connection_idle_ms"])
        assertEquals(600_000, args["grpc.max_connection_age_ms"])
        assertEquals(30_000, args["grpc.max_connection_age_grace_ms"])
    }

    @Test
    fun `infinite durations map to the INT_MAX unbounded value`() {
        val args = buildServerChannelArgs(
            maxInboundMessageSize = 4 * 1024 * 1024,
            maxInboundMetadataSize = 8 * 1024,
            keepAliveTime = Duration.INFINITE,
            keepAliveTimeout = Duration.INFINITE,
            maxConnectionIdle = Duration.INFINITE,
            maxConnectionAge = Duration.INFINITE,
            maxConnectionAgeGrace = Duration.INFINITE,
        ).toMap()

        assertEquals(Int.MAX_VALUE, args["grpc.keepalive_time_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.keepalive_timeout_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.max_connection_idle_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.max_connection_age_ms"])
        assertEquals(Int.MAX_VALUE, args["grpc.max_connection_age_grace_ms"])
    }

    @Test
    fun `finite duration at the INT_MAX boundary is rejected rather than truncated`() {
        assertFailsWith<IllegalArgumentException> {
            buildServerChannelArgs(maxConnectionAge = Int.MAX_VALUE.milliseconds)
        }
    }

    @Test
    fun `duration above the Int range is rejected rather than wrapping`() {
        assertFailsWith<IllegalArgumentException> {
            buildServerChannelArgs(maxConnectionAge = 30.days)
        }
    }

    @Test
    fun `negative duration is rejected rather than wrapping`() {
        assertFailsWith<IllegalArgumentException> {
            buildServerChannelArgs(maxConnectionAgeGrace = (-1).days)
        }
    }

    private fun buildServerChannelArgs(
        maxConnectionAge: Duration = Duration.INFINITE,
        maxConnectionAgeGrace: Duration = Duration.INFINITE,
    ): List<GrpcArg> = buildServerChannelArgs(
        maxInboundMessageSize = 4 * 1024 * 1024,
        maxInboundMetadataSize = 8 * 1024,
        keepAliveTime = null,
        keepAliveTimeout = null,
        maxConnectionIdle = Duration.INFINITE,
        maxConnectionAge = maxConnectionAge,
        maxConnectionAgeGrace = maxConnectionAgeGrace,
    )
}

/**
 * Returns the channel args that a [GrpcServer] built with [configure] passes to C-core.
 */
internal suspend fun createdServerChannelArgs(configure: GrpcServerConfiguration.() -> Unit): Map<String, Int> {
    val server = GrpcServer(0, configure = configure)
    try {
        return server.channelArgs.toMap()
    } finally {
        server.shutdownNow()
        server.awaitTermination(30.seconds)
    }
}

private fun List<GrpcArg>.toMap(): Map<String, Int> =
    associate { it.key to (it as GrpcArg.Integer).value }
