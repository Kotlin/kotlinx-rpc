/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.TimeSource

internal data class BenchmarkOverrides(
    val warmupCalls: Int? = null,
    val calls: Int? = null,
    val concurrency: Int? = null,
    val requestBytes: Int? = null,
    val responseBytes: Int? = null,
) {
    fun applyTo(parameters: BenchmarkParameters): BenchmarkParameters = parameters.copy(
        warmupCalls = warmupCalls ?: parameters.warmupCalls,
        calls = calls ?: parameters.calls,
        concurrency = concurrency ?: parameters.concurrency,
        requestBytes = requestBytes ?: parameters.requestBytes,
        responseBytes = responseBytes ?: parameters.responseBytes,
    )
}

internal data class BenchmarkParameters(
    val warmupCalls: Int,
    val calls: Int,
    val concurrency: Int,
    val requestBytes: Int,
    val responseBytes: Int,
) {
    init {
        require(warmupCalls >= 0) { "--warmup must be at least 0" }
        require(calls > 0) { "--calls must be greater than 0" }
        require(concurrency > 0) { "--concurrency must be greater than 0" }
        require(requestBytes >= 0) { "--request-bytes must be at least 0" }
        require(responseBytes >= 0) { "--response-bytes must be at least 0" }
    }
}

internal interface Benchmark {
    val name: String
    val description: String
    val defaults: BenchmarkParameters

    suspend fun run(client: GrpcClient, parameters: BenchmarkParameters): BenchmarkResult
}

internal class BenchmarkRegistry(benchmarks: List<Benchmark>) {
    val all: List<Benchmark> = benchmarks.sortedBy(Benchmark::name)

    init {
        val duplicateNames = benchmarks.groupingBy(Benchmark::name).eachCount().filterValues { it > 1 }.keys
        require(duplicateNames.isEmpty()) { "Duplicate benchmark names: ${duplicateNames.joinToString()}" }
    }

    fun find(name: String): Benchmark? = all.firstOrNull { it.name == name }
}

internal data class MeasuredCall(
    val applicationBytes: Long,
    val execute: suspend () -> Unit,
)

internal class CallBenchmark(
    override val name: String,
    override val description: String,
    override val defaults: BenchmarkParameters,
    private val prepare: (GrpcClient, BenchmarkParameters) -> MeasuredCall,
) : Benchmark {
    override suspend fun run(client: GrpcClient, parameters: BenchmarkParameters): BenchmarkResult {
        val call = prepare(client, parameters)

        executeCalls(
            count = parameters.warmupCalls,
            concurrency = parameters.concurrency,
            recordLatency = false,
            call = call.execute,
        )

        val started = TimeSource.Monotonic.markNow()
        val samples = executeCalls(
            count = parameters.calls,
            concurrency = parameters.concurrency,
            recordLatency = true,
            call = call.execute,
        )
        val elapsed = started.elapsedNow()

        return BenchmarkResult(
            benchmarkName = name,
            platform = currentPlatform,
            parameters = parameters,
            elapsed = elapsed,
            applicationBytes = call.applicationBytes * parameters.calls,
            latency = LatencyStatistics.from(samples),
        )
    }
}

private suspend fun executeCalls(
    count: Int,
    concurrency: Int,
    recordLatency: Boolean,
    call: suspend () -> Unit,
): LongArray {
    if (count == 0) return LongArray(0)

    return coroutineScope {
        val workerCount = minOf(count, concurrency)
        val baseCallsPerWorker = count / workerCount
        val workersWithExtraCall = count % workerCount

        List(workerCount) { workerIndex ->
            async(Dispatchers.Default) {
                val workerCalls = baseCallsPerWorker + if (workerIndex < workersWithExtraCall) 1 else 0
                val samples = if (recordLatency) LongArray(workerCalls) else LongArray(0)

                repeat(workerCalls) { callIndex ->
                    if (recordLatency) {
                        val started = TimeSource.Monotonic.markNow()
                        call()
                        samples[callIndex] = started.elapsedNow().inWholeNanoseconds
                    } else {
                        call()
                    }
                }
                samples
            }
        }.awaitAll().combine()
    }
}

private fun List<LongArray>.combine(): LongArray {
    val result = LongArray(sumOf(LongArray::size))
    var offset = 0
    for (array in this) {
        array.copyInto(result, destinationOffset = offset)
        offset += array.size
    }
    return result
}

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

internal data class BenchmarkResult(
    val benchmarkName: String,
    val platform: String,
    val parameters: BenchmarkParameters,
    val elapsed: Duration,
    val applicationBytes: Long,
    val latency: LatencyStatistics,
) {
    val callsPerSecond: Double
        get() = parameters.calls / elapsed.inWholeNanoseconds.toDouble() * 1_000_000_000.0

    val applicationBytesPerSecond: Double
        get() = applicationBytes / elapsed.inWholeNanoseconds.toDouble() * 1_000_000_000.0
}
