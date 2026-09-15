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
import kotlin.time.Duration.Companion.seconds

internal fun benchmarkCommand(
    backend: BenchmarkBackend,
    registry: BenchmarkRegistry = BenchmarkRegistry(unaryBenchmarks(backend)),
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
