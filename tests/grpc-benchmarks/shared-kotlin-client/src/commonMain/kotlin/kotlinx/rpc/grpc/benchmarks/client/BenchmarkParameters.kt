/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

/** Optional command-line values that replace a benchmark's default parameters. */
internal data class BenchmarkOverrides(
    val warmupOperations: Int? = null,
    val operations: Int? = null,
    val concurrency: Int? = null,
    val requestBytes: Int? = null,
    val responseBytes: Int? = null,
) {
    fun applyTo(parameters: BenchmarkParameters): BenchmarkParameters = parameters.copy(
        warmupOperations = warmupOperations ?: parameters.warmupOperations,
        operations = operations ?: parameters.operations,
        concurrency = concurrency ?: parameters.concurrency,
        requestBytes = requestBytes ?: parameters.requestBytes,
        responseBytes = responseBytes ?: parameters.responseBytes,
    )
}

/**
 * Configures one benchmark case.
 *
 * For streaming benchmarks, [operations] is the number of messages per direction, split across
 * [concurrency] parallel streams on one channel.
 * For paced unary calls, [pauseMs] is the idle time before each group of up to [concurrency] calls.
 */
internal data class BenchmarkParameters(
    val warmupOperations: Int,
    val operations: Int,
    val concurrency: Int,
    val requestBytes: Int,
    val responseBytes: Int,
    val pauseMs: Int = 0,
) {
    init {
        require(warmupOperations >= 0) { "--warmup must be at least 0" }
        require(operations > 0) { "--operations must be greater than 0" }
        require(concurrency > 0) { "--concurrency must be greater than 0" }
        require(requestBytes >= 0) { "--request-bytes must be at least 0" }
        require(pauseMs >= 0) { "Pause must be at least 0 ms" }
        require(responseBytes >= 0) { "--response-bytes must be at least 0" }
    }

    fun toJson(): JsonObject {
        val fields = listOf(
            "warmup_operations" to JsonLong(warmupOperations.toLong()),
            "operations" to JsonLong(operations.toLong()),
            "concurrency" to JsonLong(concurrency.toLong()),
            "request_bytes" to JsonLong(requestBytes.toLong()),
            "response_bytes" to JsonLong(responseBytes.toLong()),
        )
        return JsonObject(if (pauseMs > 0) fields + ("pause_ms" to JsonLong(pauseMs.toLong())) else fields)
    }
}
