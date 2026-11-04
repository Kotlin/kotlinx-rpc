/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

/** The outcome of one benchmark case within one client process. */
internal enum class TrialStatus {
    COMPLETED,
    FAILED,
}

/** Percentile summary of the latency samples of one trial. */
internal data class LatencyStatistics(
    val sampleCount: Int,
    val minimum: Duration?,
    val mean: Duration?,
    val p50: Duration?,
    val p90: Duration?,
    val p95: Duration?,
    val p99: Duration?,
    val p999: Duration?,
    val maximum: Duration?,
) {
    companion object {
        /**
         * Computes the summary with the nearest-rank percentile method.
         *
         * Returns a summary with null values when there are no samples, which happens when a
         * stream delivers fewer than two messages.
         */
        fun from(samples: LongArray): LatencyStatistics {
            if (samples.isEmpty()) {
                return LatencyStatistics(0, null, null, null, null, null, null, null, null)
            }
            val sorted = samples.sortedArray()

            fun percentile(value: Double): Duration {
                val index = (value * sorted.lastIndex).roundToInt()
                return sorted[index].nanoseconds
            }

            return LatencyStatistics(
                sampleCount = sorted.size,
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

/**
 * The measurement of one benchmark case in one client process.
 *
 * [elapsedNanos] covers only the measured phase. Channel creation, the readiness probe, and
 * warmup run before it and are never included.
 *
 * A trial stops at the first failed operation. [operations] and the byte counts then hold the
 * work that completed before the failure, and [latencySamplesNanos] holds its samples.
 */
internal data class TrialResult(
    val benchmarkName: String,
    val caseName: String,
    val parameters: BenchmarkParameters,
    val latencyMetric: LatencyMetric,
    val operationUnit: OperationUnit,
    val injectedDelayMs: Int,
    val status: TrialStatus,
    val error: String?,
    val elapsedNanos: Long,
    val operations: Long,
    val requestMessages: Long?,
    val responseMessages: Long?,
    val applicationRequestBytes: Long,
    val applicationResponseBytes: Long,
    val latencySamplesNanos: LongArray,
    val timeToFirstResponseNanos: List<Long> = emptyList(),
    val streamDurationNanos: List<Long> = emptyList(),
    val finalResponseLatencyNanos: List<Long> = emptyList(),
) {
    val latencyStatistics: LatencyStatistics get() = LatencyStatistics.from(latencySamplesNanos)
}

/** Schema version of the JSON trial record, shared with the Swift client and the analysis tools. */
internal const val TRIAL_SCHEMA_VERSION: Int = 1

/**
 * A [TrialResult] with its process context: implementation identity, platform label, server
 * target, wall-clock start time, and the measured connection readiness of the channel.
 */
internal data class TrialRecord(
    val implementationName: String,
    val implementationVersion: String?,
    val platform: String,
    val target: String,
    val startedAtEpochMs: Long,
    val connectionReadyNanos: Long?,
    val trial: TrialResult,
) {
    fun toJson(): JsonObject {
        val trial = this.trial
        return JsonObject.of(
            "schema_version" to JsonLong(TRIAL_SCHEMA_VERSION.toLong()),
            "implementation" to JsonString(implementationName),
            "implementation_version" to (implementationVersion?.let(::JsonString) ?: JsonNullValue),
            "platform" to JsonString(platform),
            "benchmark" to JsonString(trial.benchmarkName),
            "case" to JsonString(trial.caseName),
            "parameters" to trial.parameters.toJson(),
            "latency_metric" to JsonString(trial.latencyMetric.wireName),
            "operation_unit" to JsonString(trial.operationUnit.wireName),
            "load_shape" to JsonString(LOAD_SHAPE),
            "target" to JsonString(target),
            "injected_delay_ms" to JsonLong(trial.injectedDelayMs.toLong()),
            "status" to JsonString(trial.status.wireName),
            "error" to (trial.error?.let(::JsonString) ?: JsonNullValue),
            "started_at_epoch_ms" to JsonLong(startedAtEpochMs),
            "connection_ready_ns" to (connectionReadyNanos?.let(::JsonLong) ?: JsonNullValue),
            "elapsed_ns" to JsonLong(trial.elapsedNanos),
            "counts" to JsonObject.of(
                "operations" to JsonLong(trial.operations),
                "request_messages" to (trial.requestMessages?.let(::JsonLong) ?: JsonNullValue),
                "response_messages" to (trial.responseMessages?.let(::JsonLong) ?: JsonNullValue),
                "application_request_bytes" to JsonLong(trial.applicationRequestBytes),
                "application_response_bytes" to JsonLong(trial.applicationResponseBytes),
            ),
            "latency_samples_ns" to JsonArray.ofNanos(trial.latencySamplesNanos),
            "events" to JsonObject.of(
                "time_to_first_response_ns" to JsonArray.ofNanos(trial.timeToFirstResponseNanos),
                "stream_duration_ns" to JsonArray.ofNanos(trial.streamDurationNanos),
                "final_response_latency_ns" to JsonArray.ofNanos(trial.finalResponseLatencyNanos),
            ),
        )
    }
}

/** Wire name used in every result format, shared with the Swift client. */
internal val LatencyMetric.wireName: String
    get() = when (this) {
        LatencyMetric.CALL_ROUND_TRIP -> "call_round_trip"
        LatencyMetric.MESSAGE_INTERARRIVAL -> "message_interarrival"
        LatencyMetric.REQUEST_WRITE_SUSPENSION -> "request_write_suspension"
        LatencyMetric.PING_PONG_ROUND_TRIP -> "ping_pong_round_trip"
    }

internal val OperationUnit.wireName: String
    get() = when (this) {
        OperationUnit.RPC -> "rpc"
        OperationUnit.MESSAGE -> "message"
    }

internal val TrialStatus.wireName: String
    get() = when (this) {
        TrialStatus.COMPLETED -> "completed"
        TrialStatus.FAILED -> "failed"
    }
