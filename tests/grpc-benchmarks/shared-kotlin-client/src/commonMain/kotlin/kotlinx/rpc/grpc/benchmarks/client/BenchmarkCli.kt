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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.grpc.client.GrpcClient
import kotlin.time.Duration.Companion.seconds

internal fun benchmarkCommand(
    backend: BenchmarkBackend,
    registry: BenchmarkRegistry = BenchmarkRegistry(unaryBenchmarks(backend) + streamingBenchmarks(backend)),
): CliktCommand = BenchmarkRootCommand().subcommands(
    HelpCommand(),
    ListBenchmarksCommand(registry),
    PingCommand(backend),
    RunBenchmarksCommand(registry, backend),
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
    override fun help(context: Context): String =
        "List the available benchmarks and their cases. The JSON format feeds the workload equivalence check."

    private val format by option("--format", help = "Output format")
        .choice("human" to OutputFormat.HUMAN, "json" to OutputFormat.JSON)
        .default(OutputFormat.HUMAN)

    override fun run() {
        when (format) {
            OutputFormat.HUMAN, OutputFormat.CSV -> echo(renderBenchmarkList(registry))
            OutputFormat.JSON -> echo(encodeJson(registry.toJson(), pretty = true))
        }
    }
}

private fun renderBenchmarkList(registry: BenchmarkRegistry): String = buildString {
    appendLine("Available benchmarks:")
    for (benchmark in registry.all) {
        val cases = if (benchmark.cases.size == 1) "" else " (${benchmark.cases.size} cases)"
        appendLine("  ${benchmark.name.padEnd(28)} ${benchmark.description}$cases")
        for (benchmarkCase in benchmark.cases) {
            val parameters = benchmarkCase.parameters
            appendLine(
                "    ${benchmarkCase.name.padEnd(18)} " +
                    "warmup=${parameters.warmupOperations}, operations=${parameters.operations}, " +
                    "concurrency=${parameters.concurrency}, " +
                    "request=${parameters.requestBytes} B, response=${parameters.responseBytes} B",
            )
        }
    }
}.trimEnd()

private class PingCommand(
    private val backend: BenchmarkBackend,
) : CliktCommand(name = "ping") {
    override fun help(context: Context): String =
        "Check that the server accepts a call, and report the connection readiness time."

    private val target by option(
        "--target",
        help = "Benchmark server",
        metavar = "HOST:PORT",
    ).default("localhost:50051")

    private val timeoutSeconds by option("--timeout", help = "Readiness timeout in seconds").int()
        .default(30)
        .check("must be positive") { it > 0 }

    private val format by option("--format", help = "Output format")
        .choice("human" to "human", "json" to "json")
        .default("human")

    override fun run() {
        val serverTarget = try {
            ServerTarget.parse(target)
        } catch (error: IllegalArgumentException) {
            echo(error.message ?: "Invalid target '$target'.", err = true)
            throw ProgramResult(2)
        }
        val ready = runBlocking {
            val client = GrpcClient(serverTarget.host, serverTarget.port) { credentials = plaintext() }
            try {
                awaitReadiness(client, backend, serverTarget.toString(), timeoutSeconds.seconds)
            } catch (error: ReadinessTimeoutException) {
                null
            } finally {
                client.shutdownNow()
                client.awaitTermination(30.seconds)
            }
        }
        when {
            ready != null && format == "json" ->
                echo("{\"target\":\"$serverTarget\",\"ready\":true,\"connection_ready_ns\":${ready.inWholeNanoseconds}}")
            ready != null ->
                echo("The server at '$serverTarget' accepted a readiness call after ${ready.inWholeMilliseconds} ms.")
            else -> {
                echo("The server at '$serverTarget' did not accept a readiness call within ${timeoutSeconds} s.", err = true)
                throw ProgramResult(1)
            }
        }
    }
}

private class RunBenchmarksCommand(
    private val registry: BenchmarkRegistry,
    private val backend: BenchmarkBackend,
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
    ).default("localhost:50051")

    private val warmupOperations by nonNegativeIntOption("--warmup", "Warmup operations before measurement")
    private val operations by positiveIntOption("--operations", "Measured operations")
    private val concurrency by positiveIntOption("--concurrency", "Concurrent workers or streams")
    private val requestBytes by nonNegativeIntOption("--request-bytes", "Request payload bytes")
    private val responseBytes by nonNegativeIntOption("--response-bytes", "Response payload bytes")
    private val injectDelayMs by nonNegativeIntOption(
        "--inject-delay-ms",
        "Test hook: adds this delay to every measured operation. The trial record reports it.",
    )
    private val readinessTimeoutSeconds by option(
        "--readiness-timeout",
        help = "Seconds to wait for the server readiness probe",
    ).int().default(30).check("must be positive") { it > 0 }
    private val platformLabel by option(
        "--platform-label",
        help = "Platform name written to the results. Defaults to the detected platform.",
    )
    private val caseName by option("--case", help = "Run one named benchmark case")
    private val format by option("--format", help = "Output format")
        .choice("human" to OutputFormat.HUMAN, "csv" to OutputFormat.CSV, "json" to OutputFormat.JSON)
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
        if (caseName != null && benchmarkName == "all") {
            currentContext.fail("--case requires a specific benchmark name")
        }

        val benchmarkCases = benchmarks.flatMap { benchmark ->
            val cases = caseName?.let { requestedCase ->
                listOf(
                    benchmark.cases.firstOrNull { it.name == requestedCase }
                        ?: currentContext.fail("Benchmark '${benchmark.name}' has no case '$requestedCase'"),
                )
            } ?: benchmark.cases
            cases.map { benchmark to it }
        }
        val overrides = BenchmarkOverrides(warmupOperations, operations, concurrency, requestBytes, responseBytes)
        val serverTarget = try {
            ServerTarget.parse(target)
        } catch (error: IllegalArgumentException) {
            currentContext.fail(error.message ?: "Invalid target '$target'.")
        }
        var progressLineLength = 0

        val records = try {
            runBlocking {
                val records = mutableListOf<TrialRecord>()
                for ((index, benchmarkAndCase) in benchmarkCases.withIndex()) {
                    val (benchmark, benchmarkCase) = benchmarkAndCase
                    if (benchmarkCases.size > 1) {
                        val progress = renderBenchmarkProgress(
                            completed = index,
                            total = benchmarkCases.size,
                            platform = platformLabel ?: currentPlatform,
                            implementation = backend.implementationName,
                            benchmark = benchmark.name,
                            benchmarkCase = benchmarkCase.name,
                        )
                        echo(
                            renderBenchmarkProgressUpdate(
                                progress = progress,
                                previousLength = progressLineLength,
                                finished = false,
                            ),
                            trailingNewline = false,
                            err = true,
                        )
                        progressLineLength = progress.length
                    }
                    records += runTrial(benchmark, benchmarkCase, overrides, serverTarget)
                }
                if (benchmarkCases.size > 1) {
                    val progress = renderBenchmarkProgress(
                        completed = benchmarkCases.size,
                        total = benchmarkCases.size,
                        platform = platformLabel ?: currentPlatform,
                        implementation = backend.implementationName,
                    )
                    echo(
                        renderBenchmarkProgressUpdate(
                            progress = progress,
                            previousLength = progressLineLength,
                            finished = true,
                        ),
                        trailingNewline = false,
                        err = true,
                    )
                    progressLineLength = 0
                }
                records
            }
        } catch (error: Throwable) {
            if (error is ProgramResult) throw error
            echo(renderBenchmarkProgressLineBreak(progressLineLength), trailingNewline = false, err = true)
            echo("Benchmark run failed: ${error.message ?: error::class.simpleName}", err = true)
            throw ProgramResult(1)
        }

        echo(render(records, format))
        if (records.any { it.trial.status == TrialStatus.FAILED }) {
            throw ProgramResult(1)
        }
    }

    /**
     * Runs one case on its own channel and returns its record. The record keeps a failed status
     * when readiness or the case itself fails, so the remaining cases still run.
     */
    private suspend fun runTrial(
        benchmark: Benchmark,
        benchmarkCase: BenchmarkCase,
        overrides: BenchmarkOverrides,
        serverTarget: ServerTarget,
    ): TrialRecord {
        val startedAtEpochMs = epochMillis()
        val client = GrpcClient(serverTarget.host, serverTarget.port) { credentials = plaintext() }
        try {
            val connectionReady = awaitReadiness(
                client = client,
                backend = backend,
                target = serverTarget.toString(),
                timeout = readinessTimeoutSeconds.seconds,
            )
            val parameters = overrides.applyTo(benchmarkCase.parameters)
            val trial = benchmark.run(client, benchmarkCase, parameters, injectDelayMs ?: 0)
            return TrialRecord(
                implementationName = backend.implementationName,
                implementationVersion = backend.implementationVersion,
                platform = platformLabel ?: currentPlatform,
                target = serverTarget.toString(),
                startedAtEpochMs = startedAtEpochMs,
                connectionReadyNanos = connectionReady.inWholeNanoseconds,
                trial = trial,
            )
        } catch (error: Throwable) {
            if (error is CancellationException) throw error
            return TrialRecord(
                implementationName = backend.implementationName,
                implementationVersion = backend.implementationVersion,
                platform = platformLabel ?: currentPlatform,
                target = serverTarget.toString(),
                startedAtEpochMs = startedAtEpochMs,
                connectionReadyNanos = null,
                trial = TrialResult(
                    benchmarkName = benchmark.name,
                    caseName = benchmarkCase.name,
                    parameters = overrides.applyTo(benchmarkCase.parameters),
                    latencyMetric = benchmark.latencyMetric,
                    operationUnit = benchmark.operationUnit,
                    injectedDelayMs = injectDelayMs ?: 0,
                    status = TrialStatus.FAILED,
                    error = error.message ?: error::class.simpleName,
                    elapsedNanos = 0,
                    operations = 0,
                    requestMessages = if (benchmark.operationUnit == OperationUnit.MESSAGE) 0L else null,
                    responseMessages = if (benchmark.operationUnit == OperationUnit.MESSAGE) 0L else null,
                    applicationRequestBytes = 0,
                    applicationResponseBytes = 0,
                    latencySamplesNanos = LongArray(0),
                ),
            )
        } finally {
            client.shutdownNow()
            client.awaitTermination(30.seconds)
        }
    }
}

private const val PROGRESS_BAR_WIDTH = 20

internal fun renderBenchmarkProgressUpdate(
    progress: String,
    previousLength: Int,
    finished: Boolean,
): String {
    val overwrite = if (previousLength > 0) renderBenchmarkProgressClear(previousLength) else ""
    return overwrite + progress + if (finished) "\n" else ""
}

internal fun renderBenchmarkProgressLineBreak(previousLength: Int): String = if (previousLength > 0) "\n" else ""

private fun renderBenchmarkProgressClear(length: Int): String = "\r" + " ".repeat(length) + "\r"

internal fun renderBenchmarkProgress(
    completed: Int,
    total: Int,
    platform: String,
    implementation: String,
    benchmark: String? = null,
    benchmarkCase: String? = null,
): String {
    require(total > 0) { "Progress total must be positive" }
    require(completed in 0..total) { "Completed benchmark count must be between 0 and total" }
    require((benchmark == null) == (benchmarkCase == null)) {
        "Benchmark and case must either both be specified or both be omitted"
    }

    val filled = PROGRESS_BAR_WIDTH * completed / total
    val bar = buildString(PROGRESS_BAR_WIDTH) {
        append("=".repeat(filled))
        if (completed < total) {
            append('>')
            append("-".repeat(PROGRESS_BAR_WIDTH - filled - 1))
        }
    }
    val remaining = total - completed
    val activity = if (benchmark != null) {
        "running benchmark=$benchmark case=$benchmarkCase"
    } else {
        "finished"
    }
    return "[$bar] $completed/$total complete, $remaining remaining | " +
        "platform=$platform implementation=$implementation | $activity"
}
