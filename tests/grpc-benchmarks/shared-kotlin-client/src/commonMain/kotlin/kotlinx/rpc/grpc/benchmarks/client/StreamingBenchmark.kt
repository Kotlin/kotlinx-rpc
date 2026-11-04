/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.time.TimeSource

/** Measurements produced by one completed streaming RPC. */
internal data class StreamMeasurement(
    val requestMessages: Long,
    val responseMessages: Long,
    val applicationRequestBytes: Long,
    val applicationResponseBytes: Long,
    val latencySamples: LongArray,
    val timeToFirstResponseNanos: Long? = null,
    val streamDurationNanos: Long? = null,
    val finalResponseLatencyNanos: Long? = null,
)

/**
 * Runs a streaming workload whose [BenchmarkParameters.operations] is the number of messages per
 * direction, split across [BenchmarkParameters.concurrency] parallel streams on one channel.
 */
internal class StreamingBenchmark(
    override val name: String,
    override val description: String,
    override val cases: List<BenchmarkCase>,
    override val latencyMetric: LatencyMetric,
    private val prepare: (GrpcClient) -> suspend (Int, BenchmarkParameters, Int) -> StreamMeasurement,
) : Benchmark {
    override val operationUnit: OperationUnit = OperationUnit.MESSAGE

    override suspend fun run(
        client: GrpcClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int,
    ): TrialResult {
        val executeStream = try {
            prepare(client)
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            return failedTrial(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                "Preparing the measured stream failed: ${error.message ?: error::class.simpleName}",
            )
        }

        try {
            executeStreams(parameters.warmupOperations, parameters, injectedDelayMs, executeStream)
        } catch (error: OperationFailureException) {
            return failedTrial(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                "Warmup stream failed: ${error.cause?.message ?: error::class.simpleName}",
            )
        }

        val started = TimeSource.Monotonic.markNow()
        val outcome = executeStreams(parameters.operations, parameters, injectedDelayMs, executeStream)
        val elapsed = started.elapsedNow().inWholeNanoseconds
        val measurements = outcome.completed

        return if (outcome.failure == null) {
            trialResult(benchmarkCase, parameters, injectedDelayMs, TrialStatus.COMPLETED, null, elapsed, measurements)
        } else {
            val cause = outcome.failure.cause
            val failureMessage = cause?.message ?: "unknown error"
            trialResult(
                benchmarkCase,
                parameters,
                injectedDelayMs,
                TrialStatus.FAILED,
                "Measured stream failed: $failureMessage",
                elapsed,
                measurements,
            )
        }
    }

    private fun trialResult(
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int,
        status: TrialStatus,
        error: String?,
        elapsedNanos: Long,
        measurements: List<StreamMeasurement>,
    ): TrialResult {
        // A failed trial counts only the streams that completed. The messages of the stream that
        // failed, and of the streams cancelled with it, aren't counted.
        val requestMessages = measurements.sumOf(StreamMeasurement::requestMessages)
        val responseMessages = measurements.sumOf(StreamMeasurement::responseMessages)
        return TrialResult(
            benchmarkName = name,
            caseName = benchmarkCase.name,
            parameters = parameters,
            latencyMetric = latencyMetric,
            operationUnit = operationUnit,
            injectedDelayMs = injectedDelayMs,
            status = status,
            error = error,
            elapsedNanos = elapsedNanos,
            operations = requestMessages + responseMessages,
            requestMessages = requestMessages,
            responseMessages = responseMessages,
            applicationRequestBytes = measurements.sumOf(StreamMeasurement::applicationRequestBytes),
            applicationResponseBytes = measurements.sumOf(StreamMeasurement::applicationResponseBytes),
            latencySamplesNanos = measurements.map(StreamMeasurement::latencySamples).combine(),
            timeToFirstResponseNanos = measurements.mapNotNull(StreamMeasurement::timeToFirstResponseNanos),
            streamDurationNanos = measurements.mapNotNull(StreamMeasurement::streamDurationNanos),
            finalResponseLatencyNanos = measurements.mapNotNull(StreamMeasurement::finalResponseLatencyNanos),
        )
    }

    private fun failedTrial(
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int,
        error: String,
    ): TrialResult = trialResult(
        benchmarkCase,
        parameters,
        injectedDelayMs,
        TrialStatus.FAILED,
        error,
        elapsedNanos = 0,
        measurements = emptyList(),
    )
}

/** The completed streams of one phase, plus the failure that stopped the remaining ones, if any. */
private class StreamsOutcome(
    val completed: List<StreamMeasurement>,
    val failure: OperationFailureException?,
)

private suspend fun executeStreams(
    messageCount: Int,
    parameters: BenchmarkParameters,
    injectedDelayMs: Int,
    executeStream: suspend (Int, BenchmarkParameters, Int) -> StreamMeasurement,
): StreamsOutcome {
    if (messageCount == 0) return StreamsOutcome(emptyList(), null)

    val streamCount = minOf(messageCount, parameters.concurrency)
    val messagesPerStream = messageCount / streamCount
    val streamsWithExtraMessage = messageCount % streamCount
    val results = arrayOfNulls<StreamMeasurement>(streamCount)

    try {
        coroutineScope {
            List(streamCount) { streamIndex ->
                async(Dispatchers.Default) {
                    val messages = messagesPerStream + if (streamIndex < streamsWithExtraMessage) 1 else 0
                    results[streamIndex] = executeStream(messages, parameters, injectedDelayMs)
                }
            }.awaitAll()
        }
        return StreamsOutcome(results.filterNotNull(), null)
    } catch (error: Throwable) {
        if (error is CancellationException) throw error
        return StreamsOutcome(results.filterNotNull(), OperationFailureException(0, LongArray(0), error))
    }
}

private fun List<LongArray>.combine(): LongArray {
    val result = LongArray(sumOf(LongArray::size))
    var offset = 0
    for (samples in this) {
        samples.copyInto(result, destinationOffset = offset)
        offset += samples.size
    }
    return result
}
