/*
 * Copyright 2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package kotlinx.rpc.grpc.benchmarks.client

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.check
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

internal fun benchmarkCommand(
    registry: BenchmarkRegistry = BenchmarkRegistry(unaryBenchmarks()),
): CliktCommand = BenchmarkRootCommand().subcommands(
    HelpCommand(),
    ListBenchmarksCommand(registry),
    RunBenchmarksCommand(registry),
)

private class BenchmarkRootCommand : NoOpCliktCommand(name = "kotlinx-rpc-grpc-benchmark-client") {
    override val printHelpOnEmptyArgs: Boolean = true

    override fun help(context: Context): String =
        "Run kotlinx-rpc gRPC client benchmarks against a benchmark server."
}

private class HelpCommand : CliktCommand(name = "help") {
    override fun help(context: Context): String = "Show help for the CLI or one of its commands."

    private val requestedCommand by argument(
        name = "command",
        help = "Command to show help for",
    ).optional()

    override fun run() {
        val root = requireNotNull(currentContext.parent).command
        val command = requestedCommand?.let { name ->
            root.registeredSubcommands().firstOrNull { it.commandName == name }
                ?: currentContext.fail("Unknown command '$name'")
        } ?: root

        echo(command.getFormattedHelp())
    }
}

private class ListBenchmarksCommand(
    private val registry: BenchmarkRegistry,
) : CliktCommand(name = "list") {
    override fun help(context: Context): String = "List the available benchmarks."

    override fun run() {
        echo(
            buildString {
                appendLine("Available benchmarks:")
                for (benchmark in registry.all) {
                    appendLine("  ${benchmark.name.padEnd(20)} ${benchmark.description}")
                }
            }.trimEnd(),
        )
    }
}

private class RunBenchmarksCommand(
    private val registry: BenchmarkRegistry,
) : CliktCommand(name = "run") {
    override fun help(context: Context): String = "Run one benchmark, or all registered benchmarks."

    private val benchmarkName by argument(
        name = "benchmark",
        help = "Benchmark name, or 'all'",
    ).check("must be 'all' or the name of a registered benchmark") {
        it == "all" || registry.find(it) != null
    }

    private val target by option(
        "--target",
        help = "Benchmark server",
        metavar = "HOST:PORT",
    ).default("localhost:50051").check("must not be blank") { it.isNotBlank() }

    private val warmupCalls by nonNegativeIntOption("--warmup", "Warmup calls before measurement")
    private val calls by positiveIntOption("--calls", "Measured calls")
    private val concurrency by positiveIntOption("--concurrency", "Concurrent workers")
    private val requestBytes by nonNegativeIntOption("--request-bytes", "Request payload bytes")
    private val responseBytes by nonNegativeIntOption("--response-bytes", "Response payload bytes")
    private val format by option("--format", help = "Output format")
        .choice("human" to OutputFormat.HUMAN, "csv" to OutputFormat.CSV)
        .default(OutputFormat.HUMAN)

    private fun nonNegativeIntOption(name: String, help: String) =
        option(name, help = help).int().check("must be non-negative") { it >= 0 }

    private fun positiveIntOption(name: String, help: String) =
        option(name, help = help).int().check("must be positive") { it > 0 }

    override fun run() {
        val benchmarks = if (benchmarkName == "all") {
            registry.all
        } else {
            listOf(requireNotNull(registry.find(benchmarkName)))
        }
        val overrides = BenchmarkOverrides(warmupCalls, calls, concurrency, requestBytes, responseBytes)
        val parameterSets = benchmarks.associateWith { overrides.applyTo(it.defaults) }

        val results = try {
            runBlocking {
                val client = GrpcClient(target) {
                    credentials = plaintext()
                }
                try {
                    benchmarks.map { benchmark -> benchmark.run(client, parameterSets.getValue(benchmark)) }
                } finally {
                    client.shutdownNow()
                    client.awaitTermination(30.seconds)
                }
            }
        } catch (error: Throwable) {
            echo("Benchmark failed: ${error.message ?: error::class.simpleName}", err = true)
            throw ProgramResult(1)
        }
        echo(render(results, target, format))
    }
}

internal enum class OutputFormat {
    HUMAN,
    CSV,
}

private fun render(results: List<BenchmarkResult>, target: String, format: OutputFormat): String {
    return when (format) {
        OutputFormat.HUMAN -> results.joinToString("\n\n") { it.renderHuman(target) }
        OutputFormat.CSV -> buildString {
            appendLine(CSV_HEADER)
            results.forEach { appendLine(it.renderCsv(target)) }
        }.trimEnd()
    }
}

private fun BenchmarkResult.renderHuman(target: String): String = buildString {
    appendLine("benchmark: $benchmarkName")
    appendLine("platform: $platform")
    appendLine("target: $target")
    appendLine("calls: ${parameters.calls} (${parameters.warmupCalls} warmup)")
    appendLine("concurrency: ${parameters.concurrency}")
    appendLine("payload: ${parameters.requestBytes} B request / ${parameters.responseBytes} B response")
    appendLine("elapsed: ${elapsed.formatSeconds()} s")
    appendLine(
        "throughput: ${callsPerSecond.format(2)} calls/s, " +
            "${applicationBytesPerSecond.formatBytesPerSecond()} application data",
    )
    appendLine("latency (us):")
    appendLine("  min=${latency.minimum.micros()} mean=${latency.mean.micros()} p50=${latency.p50.micros()}")
    appendLine("  p90=${latency.p90.micros()} p95=${latency.p95.micros()} p99=${latency.p99.micros()}")
    append("  p99.9=${latency.p999.micros()} max=${latency.maximum.micros()}")
}

private const val CSV_HEADER =
    "benchmark,platform,target,warmup_calls,calls,concurrency,request_bytes,response_bytes,elapsed_seconds," +
        "calls_per_second,application_bytes_per_second,latency_min_us,latency_mean_us,latency_p50_us," +
        "latency_p90_us,latency_p95_us,latency_p99_us,latency_p999_us,latency_max_us"

private fun BenchmarkResult.renderCsv(target: String): String {
    return listOf(
        benchmarkName,
        platform,
        target,
        parameters.warmupCalls,
        parameters.calls,
        parameters.concurrency,
        parameters.requestBytes,
        parameters.responseBytes,
        elapsed.formatSeconds(),
        callsPerSecond.format(4),
        applicationBytesPerSecond.format(4),
        latency.minimum.micros(),
        latency.mean.micros(),
        latency.p50.micros(),
        latency.p90.micros(),
        latency.p95.micros(),
        latency.p99.micros(),
        latency.p999.micros(),
        latency.maximum.micros(),
    ).joinToString(",")
}

private fun Duration.micros(): String = toDouble(DurationUnit.MICROSECONDS).format(3)

private fun Duration.formatSeconds(): String = toDouble(DurationUnit.SECONDS).format(6)

internal fun Double.formatBytesPerSecond(): String = when {
    this >= BYTES_PER_GIGABYTE -> "${(this / BYTES_PER_GIGABYTE).format(2)} GB/s"
    this >= BYTES_PER_MEGABYTE -> "${(this / BYTES_PER_MEGABYTE).format(2)} MB/s"
    this >= BYTES_PER_KILOBYTE -> "${(this / BYTES_PER_KILOBYTE).format(2)} KB/s"
    else -> "${format(2)} B/s"
}

private fun Double.format(decimalPlaces: Int): String {
    val multiplier = POWERS_OF_TEN[decimalPlaces]
    return (this * multiplier).roundToLong().let { rounded ->
        val whole = rounded / multiplier
        val fraction = (rounded % multiplier).toString().padStart(decimalPlaces, '0')
        "$whole.$fraction"
    }
}

private const val BYTES_PER_KILOBYTE = 1_000.0
private const val BYTES_PER_MEGABYTE = 1_000_000.0
private const val BYTES_PER_GIGABYTE = 1_000_000_000.0
private val POWERS_OF_TEN = longArrayOf(1L, 10L, 100L, 1_000L, 10_000L, 100_000L, 1_000_000L)
