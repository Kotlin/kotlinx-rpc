/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import com.github.ajalt.clikt.testing.test
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class BenchmarkCliTest {
    @Test
    fun listsBenchmarks() {
        val result = benchmarkCommand().test("list")

        assertEquals(0, result.statusCode)
        assertContains(result.stdout, "unary-latency")
        assertContains(result.stdout, "unary-throughput")
    }

    @Test
    fun showsHelpCommand() {
        val rootHelp = benchmarkCommand().test("help")
        val runHelp = benchmarkCommand().test("help run")

        assertEquals(0, rootHelp.statusCode)
        assertContains(rootHelp.stdout, "Commands:")
        assertContains(rootHelp.stdout, "help")
        assertContains(rootHelp.stdout, "list")
        assertContains(rootHelp.stdout, "run")
        assertEquals(0, runHelp.statusCode)
        assertContains(runHelp.stdout, "--target")
        assertContains(runHelp.stdout, "--concurrency")
    }

    @Test
    fun describesRunOptions() {
        val result = benchmarkCommand().test("run --help")

        assertEquals(0, result.statusCode)
        assertContains(result.stdout, "--target")
        assertContains(result.stdout, "--concurrency")
        assertContains(result.stdout, "--request-bytes")
        assertContains(result.stdout, "--format")
    }

    @Test
    fun rejectsInvalidNumbers() {
        val result = benchmarkCommand().test("run unary-latency --calls many")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "--calls")
        assertContains(result.stderr, "invalid", ignoreCase = true)
    }

    @Test
    fun rejectsUnknownBenchmarks() {
        val result = benchmarkCommand().test("run unknown")

        assertEquals(1, result.statusCode)
        assertContains(result.stderr, "registered benchmark")
    }
}
