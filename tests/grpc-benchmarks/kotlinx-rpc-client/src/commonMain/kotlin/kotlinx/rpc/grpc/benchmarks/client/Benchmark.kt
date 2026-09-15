/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import kotlinx.rpc.grpc.client.GrpcClient

/** Defines a benchmark that can run against a gRPC client. */
internal interface Benchmark {
    val name: String
    val description: String
    val defaults: BenchmarkParameters

    suspend fun run(client: GrpcClient, parameters: BenchmarkParameters): BenchmarkResult
}

/** Provides name-based access to the available benchmarks. */
internal class BenchmarkRegistry(benchmarks: List<Benchmark>) {
    val all: List<Benchmark> = benchmarks.sortedBy(Benchmark::name)

    init {
        val duplicateNames = benchmarks.groupingBy(Benchmark::name).eachCount().filterValues { it > 1 }.keys
        require(duplicateNames.isEmpty()) { "Duplicate benchmark names: ${duplicateNames.joinToString()}" }
    }

    fun find(name: String): Benchmark? = all.firstOrNull { it.name == name }
}
