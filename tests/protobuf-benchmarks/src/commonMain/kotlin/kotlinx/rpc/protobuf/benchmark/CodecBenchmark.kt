/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.protobuf.benchmark

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.rpc.grpc.marshaller.encodeToByteArray
import kotlinx.rpc.grpc.marshaller.grpcMarshallerOf

/**
 * Encodes and decodes each case with the in-memory marshaller functions.
 *
 * `_size` is cached for each message instance, so the encode benchmarks don't count the size computation.
 * [NewMessageBenchmark] counts it.
 */
// JMH subclasses each state class, so the classes are open.
@State(Scope.Benchmark)
open class CodecBenchmark {
    @Param(SCALARS, UNKNOWN_FIELDS, STRINGS, BLOB, PACKED, TREE, LARGE)
    var case: String = ""

    private lateinit var benchmarkCase: BenchmarkCase<*>

    @Setup
    fun setup() {
        benchmarkCase = benchmarkCase(case)
    }

    @Benchmark
    fun encodeToByteArray(): Any = benchmarkCase.encodeToByteArray()

    @Benchmark
    fun encodeToBuffer(): Any = benchmarkCase.encodeToBuffer()

    @Benchmark
    fun decodeFromByteArray(): Any? = benchmarkCase.decodeFromByteArray()

    @Benchmark
    fun decodeFromBuffer(): Any? = benchmarkCase.decodeFromBuffer()
}

/**
 * Builds a new [Scalars] message in each iteration, so the encode counts the size computation.
 *
 * Subtract [build] from [buildAndEncode] to get the size computation and the encode.
 */
@State(Scope.Benchmark)
open class NewMessageBenchmark {
    private val marshaller = grpcMarshallerOf<Scalars>()
    private var seed = 0

    @Benchmark
    fun build(): Any = scalars(seed++)

    @Benchmark
    fun buildAndEncode(): Any = marshaller.encodeToByteArray(scalars(seed++))
}
