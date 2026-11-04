/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

/** Optional command-line values that replace a benchmark's default parameters. */
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

/** Configures the warmup, measured calls, concurrency, and payload sizes of a benchmark run. */
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
