/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

/** Summarizes the latency samples collected during a benchmark run. */
internal data class LatencyStatistics(
    val minimum: Duration,
    val mean: Duration,
    val p50: Duration,
    val p90: Duration,
    val p95: Duration,
    val p99: Duration,
    val p999: Duration,
    val maximum: Duration,
) {
    companion object {
        fun from(samples: LongArray): LatencyStatistics {
            require(samples.isNotEmpty()) { "At least one latency sample is required" }
            val sorted = samples.sortedArray()

            fun percentile(value: Double): Duration {
                val index = (value * sorted.lastIndex).roundToInt()
                return sorted[index].nanoseconds
            }

            return LatencyStatistics(
                minimum = sorted.first().nanoseconds,
                mean = (samples.sum().toDouble() / samples.size).toLong().nanoseconds,
                p50 = percentile(0.50),
                p90 = percentile(0.90),
                p95 = percentile(0.95),
                p99 = percentile(0.99),
                p999 = percentile(0.999),
                maximum = sorted.last().nanoseconds,
            )
        }
    }
}

/** Contains the measured duration, throughput inputs, and latency statistics of a benchmark run. */
internal data class BenchmarkResult(
    val benchmarkName: String,
    val caseName: String,
    val implementationName: String,
    val platform: String,
    val parameters: BenchmarkParameters,
    val elapsed: Duration,
    val applicationBytes: Long,
    val latency: LatencyStatistics,
    val requestMessages: Long? = null,
    val responseMessages: Long? = null,
    val timeToFirstResponse: Duration? = null,
    val finalResponseLatency: Duration? = null,
) {
    val callsPerSecond: Double
        get() = parameters.calls / elapsed.inWholeNanoseconds.toDouble() * 1_000_000_000.0

    val applicationBytesPerSecond: Double
        get() = applicationBytes / elapsed.inWholeNanoseconds.toDouble() * 1_000_000_000.0

    val messagesPerSecond: Double?
        get() {
            if (requestMessages == null && responseMessages == null) return null
            val messages = (requestMessages ?: 0) + (responseMessages ?: 0)
            return messages / elapsed.inWholeNanoseconds.toDouble() * 1_000_000_000.0
        }
}
