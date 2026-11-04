/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BenchmarkParametersTest {
    @Test
    fun appliesParameterOverrides() {
        val defaults = BenchmarkParameters(1, 2, 3, 4, 5)

        assertEquals(
            BenchmarkParameters(1, 20, 3, 4, 50),
            BenchmarkOverrides(calls = 20, responseBytes = 50).applyTo(defaults),
        )
        assertFailsWith<IllegalArgumentException> {
            BenchmarkOverrides(concurrency = 0).applyTo(defaults)
        }
    }
}
