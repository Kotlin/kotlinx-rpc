/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.server.GrpcServer
import kotlinx.rpc.grpc.server.internal.ServerBuilder
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

internal actual fun GrpcTestBase.testConnectionManagement(
    maxIdle: Duration,
    maxAge: Duration,
    maxAgeGrace: Duration,
) = runBlocking {
    val server = GrpcServer(0) {
        maxConnectionIdle = maxIdle
        maxConnectionAge = maxAge
        maxConnectionAgeGrace = maxAgeGrace
    }

    try {
        val builder = server.getField<ServerBuilder<*>>("serverBuilder")
        assertEquals(maxIdle, builder.getField<Long>("maxConnectionIdleInNanos").nanoseconds)
        assertEquals(maxAge, builder.getField<Long>("maxConnectionAgeInNanos").nanoseconds)
        assertEquals(maxAgeGrace, builder.getField<Long>("maxConnectionAgeGraceInNanos").nanoseconds)
    } finally {
        server.shutdownNow()
        server.awaitTermination(30.seconds)
    }
}
