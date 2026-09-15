/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.nanoseconds

class BenchmarkResultTest {
    @Test
    fun computesLatencyPercentiles() {
        val statistics = LatencyStatistics.from(longArrayOf(1, 2, 3, 4, 100))

        assertEquals(1.nanoseconds, statistics.minimum)
        assertEquals(22.nanoseconds, statistics.mean)
        assertEquals(3.nanoseconds, statistics.p50)
        assertEquals(100.nanoseconds, statistics.p90)
        assertEquals(100.nanoseconds, statistics.maximum)
    }
}
