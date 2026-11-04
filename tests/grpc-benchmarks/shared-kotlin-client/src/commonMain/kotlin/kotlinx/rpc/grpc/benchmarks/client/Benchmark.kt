/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.rpc.grpc.client.GrpcClient

/** One reproducible parameter point within a benchmark. */
internal data class BenchmarkCase(
    val name: String,
    val parameters: BenchmarkParameters,
) {
    init {
        require(name.isNotBlank()) { "Benchmark case name must not be blank" }
    }
}

/** Defines a benchmark that can run against a gRPC client. */
internal interface Benchmark {
    val name: String
    val description: String
    val cases: List<BenchmarkCase>
    val latencyMetric: LatencyMetric
    val operationUnit: OperationUnit

    /**
     * Runs one case on a channel that has already passed the readiness probe.
     *
     * Returns a failed [TrialResult] instead of throwing when an operation fails, so one failed
     * case can't drop the results of the remaining cases of a process.
     */
    suspend fun run(
        client: GrpcClient,
        benchmarkCase: BenchmarkCase,
        parameters: BenchmarkParameters,
        injectedDelayMs: Int,
    ): TrialResult
}

/** Provides name-based access to the available benchmarks. */
internal class BenchmarkRegistry(benchmarks: List<Benchmark>) {
    val all: List<Benchmark> = benchmarks.sortedBy(Benchmark::name)

    init {
        val duplicateNames = benchmarks.groupingBy(Benchmark::name).eachCount().filterValues { it > 1 }.keys
        require(duplicateNames.isEmpty()) { "Duplicate benchmark names: ${duplicateNames.joinToString()}" }

        for (benchmark in benchmarks) {
            require(benchmark.cases.isNotEmpty()) { "Benchmark '${benchmark.name}' has no cases" }
            val duplicateCases = benchmark.cases.groupingBy(BenchmarkCase::name).eachCount().filterValues { it > 1 }.keys
            require(duplicateCases.isEmpty()) {
                "Duplicate cases in benchmark '${benchmark.name}': ${duplicateCases.joinToString()}"
            }
        }
    }

    fun find(name: String): Benchmark? = all.firstOrNull { it.name == name }

    /** The workload matrix in the JSON format that the workload equivalence check compares across clients. */
    fun toJson(): JsonArray = JsonArray(
        all.map { benchmark ->
            JsonObject.of(
                "benchmark" to JsonString(benchmark.name),
                "description" to JsonString(benchmark.description),
                "latency_metric" to JsonString(benchmark.latencyMetric.wireName),
                "operation_unit" to JsonString(benchmark.operationUnit.wireName),
                "cases" to JsonArray(
                    benchmark.cases.map { benchmarkCase ->
                        JsonObject.of(
                            "case" to JsonString(benchmarkCase.name),
                            "parameters" to benchmarkCase.parameters.toJson(),
                        )
                    },
                ),
            )
        },
    )
}
