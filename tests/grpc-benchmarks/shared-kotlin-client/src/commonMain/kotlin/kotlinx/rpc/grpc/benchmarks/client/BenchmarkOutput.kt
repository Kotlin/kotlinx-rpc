/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.DurationUnit

/** Selects the representation used to print benchmark results. */
internal enum class OutputFormat {
    HUMAN,
    CSV,
}

internal fun render(results: List<BenchmarkResult>, target: String, format: OutputFormat): String {
    return when (format) {
        OutputFormat.HUMAN -> results.joinToString("\n\n") { it.renderHuman(target) }
        OutputFormat.CSV -> buildString {
            appendLine(CSV_HEADER)
            results.forEach { appendLine(it.renderCsv(target)) }
        }.trimEnd()
    }
}

private fun BenchmarkResult.renderHuman(target: String): String = buildString {
    appendLine("benchmark: $benchmarkName")
    appendLine("case: $caseName")
    appendLine("implementation: $implementationName")
    appendLine("platform: $platform")
    appendLine("target: $target")
    appendLine("calls: ${parameters.calls} (${parameters.warmupCalls} warmup)")
    appendLine("concurrency: ${parameters.concurrency}")
    appendLine("payload: ${parameters.requestBytes} B request / ${parameters.responseBytes} B response")
    appendLine("elapsed: ${elapsed.formatSeconds()} s")
    appendLine(
        "throughput: ${callsPerSecond.format(2)} calls/s, " +
            "${applicationBytesPerSecond.formatBytesPerSecond()} application data",
    )
    appendLine("latency (us):")
    appendLine("  min=${latency.minimum.micros()} mean=${latency.mean.micros()} p50=${latency.p50.micros()}")
    appendLine("  p90=${latency.p90.micros()} p95=${latency.p95.micros()} p99=${latency.p99.micros()}")
    append("  p99.9=${latency.p999.micros()} max=${latency.maximum.micros()}")
}

private const val CSV_HEADER =
    "benchmark,case,implementation,platform,target,warmup_calls,calls,concurrency,request_bytes,response_bytes," +
        "elapsed_seconds," +
        "calls_per_second,application_bytes_per_second,latency_min_us,latency_mean_us,latency_p50_us," +
        "latency_p90_us,latency_p95_us,latency_p99_us,latency_p999_us,latency_max_us"

private fun BenchmarkResult.renderCsv(target: String): String {
    return listOf(
        benchmarkName,
        caseName,
        implementationName,
        platform,
        target,
        parameters.warmupCalls,
        parameters.calls,
        parameters.concurrency,
        parameters.requestBytes,
        parameters.responseBytes,
        elapsed.formatSeconds(),
        callsPerSecond.format(4),
        applicationBytesPerSecond.format(4),
        latency.minimum.micros(),
        latency.mean.micros(),
        latency.p50.micros(),
        latency.p90.micros(),
        latency.p95.micros(),
        latency.p99.micros(),
        latency.p999.micros(),
        latency.maximum.micros(),
    ).joinToString(",")
}

private fun Duration.micros(): String = toDouble(DurationUnit.MICROSECONDS).format(3)

private fun Duration.formatSeconds(): String = toDouble(DurationUnit.SECONDS).format(6)

internal fun Double.formatBytesPerSecond(): String = when {
    this >= BYTES_PER_GIGABYTE -> "${(this / BYTES_PER_GIGABYTE).format(2)} GB/s"
    this >= BYTES_PER_MEGABYTE -> "${(this / BYTES_PER_MEGABYTE).format(2)} MB/s"
    this >= BYTES_PER_KILOBYTE -> "${(this / BYTES_PER_KILOBYTE).format(2)} KB/s"
    else -> "${format(2)} B/s"
}

private fun Double.format(decimalPlaces: Int): String {
    val multiplier = POWERS_OF_TEN[decimalPlaces]
    return (this * multiplier).roundToLong().let { rounded ->
        val whole = rounded / multiplier
        val fraction = (rounded % multiplier).toString().padStart(decimalPlaces, '0')
        "$whole.$fraction"
    }
}

private const val BYTES_PER_KILOBYTE = 1_000.0
private const val BYTES_PER_MEGABYTE = 1_000_000.0
private const val BYTES_PER_GIGABYTE = 1_000_000_000.0
private val POWERS_OF_TEN = longArrayOf(1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L)
