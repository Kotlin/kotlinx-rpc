/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

@file:OptIn(ExperimentalForeignApi::class)

package kotlinx.rpc.protobuf.benchmark

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.benchmark.TearDown
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.free
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.set
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageReader
import kotlinx.rpc.grpc.marshaller.internal.NativeBufferMessageWriter

/**
 * Encodes into and decodes from native memory, as the native gRPC runtimes do.
 *
 * A reader and a writer are single-use, so each iteration makes a new one over the same native memory.
 */
@State(Scope.Benchmark)
open class NativeMemoryBenchmark {
    @Param(SCALARS, UNKNOWN_FIELDS, STRINGS, BLOB, PACKED, TREE, LARGE)
    var case: String = ""

    private lateinit var benchmarkCase: BenchmarkCase<*>
    private lateinit var encoded: CPointer<ByteVar>
    private lateinit var target: CPointer<ByteVar>
    private var size: Int = 0

    @Setup
    fun setup() {
        benchmarkCase = benchmarkCase(case)
        val bytes = benchmarkCase.bytes
        size = bytes.size
        encoded = nativeHeap.allocArray(maxOf(size, 1))
        target = nativeHeap.allocArray(maxOf(size, 1))
        bytes.forEachIndexed { index, byte -> encoded[index] = byte }
    }

    @TearDown
    fun tearDown() {
        nativeHeap.free(encoded)
        nativeHeap.free(target)
    }

    @Benchmark
    fun encodeToNativeMemory(): Int {
        val writer = NativeMemoryWriter(target, size)
        benchmarkCase.prepare().writeTo(writer)
        return writer.written
    }

    @Benchmark
    fun decodeFromNativeMemory(): Any? {
        val reader = NativeMemoryReader(encoded, size)
        return try {
            benchmarkCase.decode(reader)
        } finally {
            reader.close()
        }
    }
}

private class NativeMemoryReader(
    private val base: CPointer<ByteVar>,
    size: Int,
) : NativeBufferMessageReader(size) {
    override fun accessBuffer(block: (CPointer<ByteVar>?) -> Unit) {
        block(base.takeIf { size > 0 })
    }
}

private class NativeMemoryWriter(
    private val base: CPointer<ByteVar>,
    size: Int,
) : NativeBufferMessageWriter(size) {
    override fun accessBuffer(block: (base: CPointer<ByteVar>?) -> Unit) {
        block(base.takeIf { size > 0 })
    }
}
