/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.server.testing

import kotlinx.rpc.test.environmentVariable

internal fun testRepeatCount(): Int =
    environmentVariable("GRPC_SERVER_TEST_REPEATS")?.toIntOrNull()?.coerceAtLeast(1) ?: 1
