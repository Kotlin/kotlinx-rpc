/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.test.integration

import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal actual fun GrpcTestBase.testConnectionManagement(
    maxIdle: Duration,
    maxAge: Duration,
    maxAgeGrace: Duration,
) {
    val args = createdServerChannelArgs {
        maxConnectionIdle = maxIdle
        maxConnectionAge = maxAge
        maxConnectionAgeGrace = maxAgeGrace
    }

    assertEquals(maxIdle, args.getValue("grpc.max_connection_idle_ms").milliseconds)
    assertEquals(maxAge, args.getValue("grpc.max_connection_age_ms").milliseconds)
    assertEquals(maxAgeGrace, args.getValue("grpc.max_connection_age_grace_ms").milliseconds)
}
