/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

/** A prepared operation and its application-level payload sizes per call. */
internal data class MeasuredCall(
    val applicationRequestBytes: Long,
    val applicationResponseBytes: Long,
    val execute: suspend () -> Unit,
)

/**
 * Reports the failure of one operation.
 *
 * [completedOperations] counts the operations that completed before the failure, and
 * [samplesSoFar] holds their latency samples.
 */
internal class OperationFailureException(
    var completedOperations: Long,
    var samplesSoFar: LongArray,
    cause: Throwable,
) : Exception(cause)

/** Runs a prepared operation through warmup and concurrent measurement phases. */
internal class CallBenchmark(
    override val name: String,
    override val description: String,
    override val cases: List<BenchmarkCase>,
    private val prepare: (GrpcClient, BenchmarkParameters) -> MeasuredCall,
) : Benchmark {
    override val latencyMetric: LatencyMetric = LatencyMetric.CALL_ROUND_TRIP
    override val operationUnit: OperationUnit = OperationUnit.RPC

    override suspend fun run(
        client: GrpcClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int,
    ): TrialResult {
        val call = try {
            prepare(client, parameters)
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            return failedTrial(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                "Preparing the measured call failed: ${error.message ?: error::class.simpleName}",
            )
        }

        try {
            executeOperations(
                count = parameters.warmupOperations,
                concurrency = parameters.concurrency,
                pauseMs = parameters.pauseMs,
                injectedDelayMs = injectedDelayMs,
                recordLatency = false,
                call = call.execute,
            )
        } catch (error: OperationFailureException) {
            return failedTrial(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                "Warmup operation ${error.completedOperations + 1} failed: " +
                    "${error.cause?.message ?: error::class.simpleName}",
            )
        }

        val resourcesBefore = processResourceUsage()
        val started = TimeSource.Monotonic.markNow()
        return try {
            val samples = executeOperations(
                count = parameters.operations,
                concurrency = parameters.concurrency,
                pauseMs = parameters.pauseMs,
                injectedDelayMs = injectedDelayMs,
                recordLatency = true,
                call = call.execute,
            )
            val resourcesAfter = processResourceUsage()
            val cpu = cpuDelta(resourcesBefore, resourcesAfter)
            TrialResult(
                benchmarkName = name,
                caseName = benchmarkCase.name,
                parameters = parameters,
                latencyMetric = latencyMetric,
                operationUnit = operationUnit,
                injectedDelayMs = injectedDelayMs,
                status = TrialStatus.COMPLETED,
                error = null,
                elapsedNanos = started.elapsedNow().inWholeNanoseconds,
                operations = parameters.operations.toLong(),
                requestMessages = null,
                responseMessages = null,
                applicationRequestBytes = call.applicationRequestBytes * parameters.operations,
                applicationResponseBytes = call.applicationResponseBytes * parameters.operations,
                latencySamplesNanos = samples,
                cpuUserNanos = cpu?.cpuUserNanos,
                cpuSystemNanos = cpu?.cpuSystemNanos,
                cpuTotalNanos = cpu?.cpuTotalNanos,
                maxRssBytes = resourcesAfter?.maxRssBytes,
            )
        } catch (error: OperationFailureException) {
            val resourcesAfter = processResourceUsage()
            val cpu = cpuDelta(resourcesBefore, resourcesAfter)
            failedTrial(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                elapsedNanos = started.elapsedNow().inWholeNanoseconds,
                operations = error.completedOperations,
                applicationRequestBytes = call.applicationRequestBytes * error.completedOperations,
                applicationResponseBytes = call.applicationResponseBytes * error.completedOperations,
                samples = error.samplesSoFar,
                error = "Measured operation ${error.completedOperations + 1} of ${parameters.operations} failed: " +
                    "${error.cause?.message ?: error::class.simpleName}",
                cpuUserNanos = cpu?.cpuUserNanos,
                cpuSystemNanos = cpu?.cpuSystemNanos,
                cpuTotalNanos = cpu?.cpuTotalNanos,
                maxRssBytes = resourcesAfter?.maxRssBytes,
            )
        }
    }

    private fun failedTrial(
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int,
        error: String,
        elapsedNanos: Long = 0,
        operations: Long = 0,
        applicationRequestBytes: Long = 0,
        applicationResponseBytes: Long = 0,
        samples: LongArray = LongArray(0),
        cpuUserNanos: Long? = null,
        cpuSystemNanos: Long? = null,
        cpuTotalNanos: Long? = null,
        maxRssBytes: Long? = null,
    ): TrialResult = TrialResult(
        benchmarkName = name,
        caseName = benchmarkCase.name,
        parameters = parameters,
        latencyMetric = latencyMetric,
        operationUnit = operationUnit,
        injectedDelayMs = injectedDelayMs,
        status = TrialStatus.FAILED,
        error = error,
        elapsedNanos = elapsedNanos,
        operations = operations,
        requestMessages = null,
        responseMessages = null,
        applicationRequestBytes = applicationRequestBytes,
        applicationResponseBytes = applicationResponseBytes,
        latencySamplesNanos = samples,
        cpuUserNanos = cpuUserNanos,
        cpuSystemNanos = cpuSystemNanos,
        cpuTotalNanos = cpuTotalNanos,
        maxRssBytes = maxRssBytes,
    )
}

/**
 * Runs [count] operations with [concurrency] parallel workers and returns one latency sample per
 * measured operation.
 *
 * The sample window of one operation covers the injected delay and the call, so the delay looks
 * like slower work to the measurement. The per-worker arrays live in the shared [WorkerProgress],
 * so a failed trial can report the operations and samples that completed before its failure.
 */
private class WorkerProgress(
    val sampleArrays: Array<LongArray?>,
    val completed: LongArray,
) {
    fun completedOperations(): Long = completed.sum()

    // The sample arrays are sized for the planned operations, so this method cuts each one down
    // to the samples that were recorded before the failure.
    fun samplesSoFar(): LongArray {
        val result = LongArray(completed.sum().toInt())
        var offset = 0
        for (workerIndex in sampleArrays.indices) {
            val array = sampleArrays[workerIndex] ?: continue
            val recorded = completed[workerIndex].toInt()
            array.copyInto(result, destinationOffset = offset, startIndex = 0, endIndex = recorded)
            offset += recorded
        }
        return result
    }
}

private suspend fun executeOperations(
    count: Int,
    concurrency: Int,
    injectedDelayMs: Int,
    recordLatency: Boolean,
    pauseMs: Int = 0,
    call: suspend () -> Unit,
): LongArray {
    if (count == 0) return LongArray(0)

    if (pauseMs > 0) {
        val batches = mutableListOf<LongArray>()
        var completed = 0
        try {
            while (completed < count) {
                // Idle time is outside each RPC sample, including the first group after warmup.
                delay(pauseMs.milliseconds)
                val batchSize = minOf(concurrency, count - completed)
                batches += executeOperations(batchSize, concurrency, injectedDelayMs, recordLatency, call = call)
                completed += batchSize
            }
        } catch (error: OperationFailureException) {
            error.completedOperations += completed
            error.samplesSoFar = (batches + listOf(error.samplesSoFar)).combine()
            throw error
        }
        return batches.combine()
    }

    val workerCount = minOf(count, concurrency)
    val baseCallsPerWorker = count / workerCount
    val workersWithExtraCall = count % workerCount
    val progress = WorkerProgress(
        sampleArrays = arrayOfNulls(workerCount),
        completed = LongArray(workerCount),
    )

    try {
        return coroutineScope {
            List(workerCount) { workerIndex ->
                async(Dispatchers.Default) {
                    val workerCalls = baseCallsPerWorker + if (workerIndex < workersWithExtraCall) 1 else 0
                    val samples = if (recordLatency) LongArray(workerCalls) else LongArray(0)
                    progress.sampleArrays[workerIndex] = samples

                    repeat(workerCalls) { callIndex ->
                        val started = TimeSource.Monotonic.markNow()
                        if (injectedDelayMs > 0) {
                            delay(injectedDelayMs.milliseconds)
                        }
                        try {
                            call()
                        } catch (error: Throwable) {
                            if (error is CancellationException) throw error
                            throw OperationFailureException(0, LongArray(0), error)
                        }
                        progress.completed[workerIndex]++
                        if (recordLatency) {
                            samples[callIndex] = started.elapsedNow().inWholeNanoseconds
                        }
                    }
                    samples
                }
            }.awaitAll().combine()
        }
    } catch (error: OperationFailureException) {
        error.completedOperations = progress.completedOperations()
        error.samplesSoFar = progress.samplesSoFar()
        throw error
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
