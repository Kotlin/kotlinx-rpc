/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.time.TimeSource

/** A prepared operation and its application-level payload size. */
internal data class MeasuredCall(
    val applicationBytes: Long,
    val execute: suspend () -> Unit,
)

/** Runs a prepared call through warmup and concurrent measurement phases. */
internal class CallBenchmark(
    override val name: String,
    override val description: String,
    override val defaults: BenchmarkParameters,
    private val implementationName: String,
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
            implementationName = implementationName,
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
