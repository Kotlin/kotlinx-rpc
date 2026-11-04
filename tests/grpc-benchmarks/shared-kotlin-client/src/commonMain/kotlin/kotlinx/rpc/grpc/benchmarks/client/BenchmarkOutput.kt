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
    JSON,
}

internal fun render(records: List<TrialRecord>, format: OutputFormat): String {
    return when (format) {
        OutputFormat.HUMAN -> records.joinToString("\n\n") { it.renderHuman() }
        OutputFormat.CSV -> buildString {
            appendLine(CSV_HEADER)
            records.forEach { appendLine(it.renderCsv()) }
        }.trimEnd()
        // One JSON object per line, so a run over many cases writes one record per line.
        OutputFormat.JSON -> records.joinToString("\n") { encodeJson(it.toJson()) }
    }
}

private fun TrialRecord.renderHuman(): String {
    val trial = this.trial
    val parameters = trial.parameters
    val statistics = trial.latencyStatistics
    return buildString {
        appendLine("benchmark: ${trial.benchmarkName}")
        appendLine("case: ${trial.caseName}")
        appendLine("implementation: $implementationName${implementationVersion?.let { " ($it)" } ?: ""}")
        appendLine("platform: $platform")
        appendLine("target: $target")
        appendLine("status: ${trial.status.wireName}${trial.error?.let { " ($it)" } ?: ""}")
        appendLine("operations: ${parameters.operations} (${parameters.warmupOperations} warmup) ${trial.operationUnit.wireName}s")
        appendLine("concurrency: ${parameters.concurrency}")
        appendLine("payload: ${parameters.requestBytes} B request / ${parameters.responseBytes} B response")
        appendLine("injected delay: ${trial.injectedDelayMs} ms per operation")
        appendLine("elapsed: ${trial.elapsedNanos.formatNanos(DurationUnit.SECONDS, 6)} s")
        appendLine("connection ready after: ${connectionReadyNanos?.formatNanosSafe(DurationUnit.SECONDS, 3) ?: "not measured"}")
        if (trial.operationUnit == OperationUnit.RPC) {
            appendLine("throughput: ${operationsPerSecond(trial).format(2)} calls/s")
        }
        trial.requestMessages?.let { request ->
            trial.responseMessages?.let { response ->
                appendLine(
                    "messages: $request request / $response response, " +
                        "${(request / seconds(trial)).format(2)} request/s, " +
                        "${(response / seconds(trial)).format(2)} response/s",
                )
            }
        }
        if (trial.applicationRequestBytes > 0 || trial.applicationResponseBytes > 0) {
            appendLine(
                "application data: ${(trial.applicationRequestBytes / seconds(trial)).formatBytesPerSecond()} request, " +
                    "${(trial.applicationResponseBytes / seconds(trial)).formatBytesPerSecond()} response",
            )
        }
        appendLine("latency (${trial.latencyMetric.wireName}, ${statistics.sampleCount} samples, us):")
        appendLine(
            "  min=${statistics.minimum.formatMicros()} mean=${statistics.mean.formatMicros()} " +
                "p50=${statistics.p50.formatMicros()}",
        )
        appendLine(
            "  p90=${statistics.p90.formatMicros()} p95=${statistics.p95.formatMicros()} " +
                "p99=${statistics.p99.formatMicros()}",
        )
        append("  p99.9=${statistics.p999.formatMicros()} max=${statistics.maximum.formatMicros()}")
        trial.timeToFirstResponseNanos.takeIf { it.isNotEmpty() }?.let {
            appendLine()
            appendLine("time to first response (mean of ${it.size} streams): ${meanNanos(it).formatNanosSafe(DurationUnit.MICROSECONDS, 3)} us")
        }
        trial.streamDurationNanos.takeIf { it.isNotEmpty() }?.let {
            appendLine()
            appendLine("stream duration (mean of ${it.size} streams): ${meanNanos(it).formatNanosSafe(DurationUnit.MICROSECONDS, 3)} us")
        }
        trial.finalResponseLatencyNanos.takeIf { it.isNotEmpty() }?.let {
            appendLine()
            append("final response latency (mean of ${it.size} streams): ${meanNanos(it).formatNanosSafe(DurationUnit.MICROSECONDS, 3)} us")
        }
    }
}

private const val CSV_HEADER =
    "schema_version,implementation,platform,benchmark,case,status,latency_metric,operation_unit," +
        "injected_delay_ms,target,warmup_operations,operations,concurrency,request_bytes,response_bytes," +
        "elapsed_seconds,operations_per_second,request_messages,response_messages," +
        "request_messages_per_second,response_messages_per_second," +
        "application_request_bytes_per_second,application_response_bytes_per_second," +
        "latency_sample_count,latency_min_us,latency_mean_us,latency_p50_us,latency_p90_us," +
        "latency_p95_us,latency_p99_us,latency_p999_us,latency_max_us," +
        "time_to_first_response_mean_us,stream_duration_mean_us,final_response_latency_mean_us,error"

private fun TrialRecord.renderCsv(): String {
    val trial = this.trial
    val statistics = trial.latencyStatistics
    val callsPerSecond = if (trial.operationUnit == OperationUnit.RPC && trial.status == TrialStatus.COMPLETED) {
        operationsPerSecond(trial).format(4)
    } else {
        ""
    }
    return listOf(
        TRIAL_SCHEMA_VERSION.toString(),
        csvField(implementationName),
        csvField(platform),
        csvField(trial.benchmarkName),
        csvField(trial.caseName),
        trial.status.wireName,
        trial.latencyMetric.wireName,
        trial.operationUnit.wireName,
        trial.injectedDelayMs.toString(),
        csvField(target),
        trial.parameters.warmupOperations.toString(),
        trial.parameters.operations.toString(),
        trial.parameters.concurrency.toString(),
        trial.parameters.requestBytes.toString(),
        trial.parameters.responseBytes.toString(),
        trial.elapsedNanos.formatNanos(DurationUnit.SECONDS, 6),
        callsPerSecond,
        trial.requestMessages?.toString() ?: "",
        trial.responseMessages?.toString() ?: "",
        trial.requestMessages?.let { (it / seconds(trial)).format(4) } ?: "",
        trial.responseMessages?.let { (it / seconds(trial)).format(4) } ?: "",
        (trial.applicationRequestBytes / seconds(trial)).format(4),
        (trial.applicationResponseBytes / seconds(trial)).format(4),
        statistics.sampleCount.toString(),
        statistics.minimum.formatMicros(),
        statistics.mean.formatMicros(),
        statistics.p50.formatMicros(),
        statistics.p90.formatMicros(),
        statistics.p95.formatMicros(),
        statistics.p99.formatMicros(),
        statistics.p999.formatMicros(),
        statistics.maximum.formatMicros(),
        trial.timeToFirstResponseNanos.meanMicros() ?: "",
        trial.streamDurationNanos.meanMicros() ?: "",
        trial.finalResponseLatencyNanos.meanMicros() ?: "",
        csvField(trial.error ?: ""),
    ).joinToString(",")
}

/** Completed operations per second of the measured phase. */
private fun operationsPerSecond(trial: TrialResult): Double = trial.operations / seconds(trial)

private fun seconds(trial: TrialResult): Double = trial.elapsedNanos / 1_000_000_000.0

private fun meanNanos(values: List<Long>): Long =
    (values.sum().toDouble() / values.size).toLong()

/** Mean of the per-stream event durations in microseconds, or null when there is no event. */
private fun List<Long>.meanMicros(): String? =
    if (isEmpty()) null else meanNanos(this).nanosString(DurationUnit.MICROSECONDS, 3)

private fun csvField(value: String): String =
    if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }

private fun Duration?.formatMicros(): String =
    this?.let { it.toDouble(DurationUnit.MICROSECONDS).format(3) } ?: ""

private fun Long.formatNanos(unit: DurationUnit, decimalPlaces: Int): String =
    nanosString(unit, decimalPlaces)

private fun Long.formatNanosSafe(unit: DurationUnit, decimalPlaces: Int): String =
    nanosString(unit, decimalPlaces)

private fun Long.nanosString(unit: DurationUnit, decimalPlaces: Int): String =
    (this / when (unit) {
        DurationUnit.SECONDS -> 1_000_000_000.0
        DurationUnit.MICROSECONDS -> 1_000.0
        else -> 1.0
    }).format(decimalPlaces)

internal fun Double.formatBytesPerSecond(): String = when {
    this >= BYTES_PER_GIGABYTE -> "${(this / BYTES_PER_GIGABYTE).format(2)} GB/s"
    this >= BYTES_PER_MEGABYTE -> "${(this / BYTES_PER_MEGABYTE).format(2)} MB/s"
    this >= BYTES_PER_KILOBYTE -> "${(this / BYTES_PER_KILOBYTE).format(2)} KB/s"
    else -> "${format(2)} B/s"
}

internal fun Double.format(decimalPlaces: Int): String {
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
private val POWERS_OF_TEN = longArrayOf(1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L, 10_000_000L)
