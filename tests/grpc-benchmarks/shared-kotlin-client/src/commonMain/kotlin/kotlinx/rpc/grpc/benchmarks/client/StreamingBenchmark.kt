/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.TimeSource

/** Measurements produced by one finite streaming RPC. */
internal data class StreamMeasurement(
    val requestMessages: Long,
    val responseMessages: Long,
    val applicationBytes: Long,
    val latencySamples: LongArray,
    val timeToFirstResponseNanoseconds: Long? = null,
    val finalResponseLatencyNanoseconds: Long? = null,
)

/** Runs a streaming workload whose `calls` parameter is the total messages per direction. */
internal class StreamingBenchmark(
    override val name: String,
    override val description: String,
    override val cases: List<BenchmarkCase>,
    private val implementationName: String,
    private val prepare: (GrpcClient) -> suspend (Int, BenchmarkParameters) -> StreamMeasurement,
) : Benchmark {
    override suspend fun run(
        client: GrpcClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
    ): BenchmarkResult {
        val executeStream = prepare(client)
        if (parameters.warmupCalls > 0) {
            executeStreams(parameters.warmupCalls, parameters, executeStream)
        }

        val started = TimeSource.Monotonic.markNow()
        val measurements = executeStreams(parameters.calls, parameters, executeStream)
        val elapsed = started.elapsedNow()
        val latencySamples = measurements.map(StreamMeasurement::latencySamples).combineSamples()

        return BenchmarkResult(
            benchmarkName = name,
            caseName = benchmarkCase.name,
            implementationName = implementationName,
            platform = currentPlatform,
            parameters = parameters,
            elapsed = elapsed,
            applicationBytes = measurements.sumOf(StreamMeasurement::applicationBytes),
            latency = LatencyStatistics.from(latencySamples),
            requestMessages = measurements.sumOf(StreamMeasurement::requestMessages),
            responseMessages = measurements.sumOf(StreamMeasurement::responseMessages),
            timeToFirstResponse = measurements.averageDuration(StreamMeasurement::timeToFirstResponseNanoseconds),
            finalResponseLatency = measurements.averageDuration(StreamMeasurement::finalResponseLatencyNanoseconds),
        )
    }
}

private suspend fun executeStreams(
    messageCount: Int,
    parameters: BenchmarkParameters,
    executeStream: suspend (Int, BenchmarkParameters) -> StreamMeasurement,
): List<StreamMeasurement> = coroutineScope {
    val streamCount = minOf(messageCount, parameters.concurrency)
    val messagesPerStream = messageCount / streamCount
    val streamsWithExtraMessage = messageCount % streamCount

    List(streamCount) { streamIndex ->
        async(Dispatchers.Default) {
            val messages = messagesPerStream + if (streamIndex < streamsWithExtraMessage) 1 else 0
            executeStream(messages, parameters)
        }
    }.awaitAll()
}

private fun List<LongArray>.combineSamples(): LongArray {
    val result = LongArray(sumOf(LongArray::size))
    var offset = 0
    for (samples in this) {
        samples.copyInto(result, destinationOffset = offset)
        offset += samples.size
    }
    return result
}

private fun List<StreamMeasurement>.averageDuration(
    selector: (StreamMeasurement) -> Long?,
) = mapNotNull(selector).takeIf { it.isNotEmpty() }?.average()?.toLong()?.nanoseconds
