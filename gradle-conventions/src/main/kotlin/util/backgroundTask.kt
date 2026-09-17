/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayDeque
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

private const val DEFAULT_READY_TIMEOUT_SECONDS = 30
private const val DEFAULT_STARTUP_GRACE_MILLIS = 300L
private const val DEFAULT_SHUTDOWN_TIMEOUT_SECONDS = 3
private const val DEFAULT_FORCE_SHUTDOWN_TIMEOUT_SECONDS = 3
private const val DEFAULT_MAX_CAPTURED_OUTPUT_LINES = 200

/** Defines how long a background process is retained. */
enum class BackgroundTaskLifetime {
    /** Use a process dedicated to one task and stop it after that task succeeds. */
    TASK,

    /** Share a process between consumers with the same [BackgroundTaskConfig.sharedName]. */
    BUILD,
}

/** Defines how a background process output line signals readiness. */
enum class BackgroundTaskReadiness {
    CONTAINS,
    EXACT,
    REGEX,
}

/** Parameters for the background process build service. */
internal interface BackgroundTaskParameters : BuildServiceParameters {
    val command: ListProperty<String>
    val workingDirectory: Property<String>
    val readyString: Property<String>
    val readiness: Property<String>
    val readyLinePrefix: Property<String>
    val readyTimeoutSeconds: Property<Int>
    val startupGraceMillis: Property<Long>
    val maxCapturedOutputLines: Property<Int>
    val redirectErrorStream: Property<Boolean>
    val failOnUnexpectedExit: Property<Boolean>
    val shutdownTimeoutSeconds: Property<Int>
    val forceShutdownTimeoutSeconds: Property<Int>
    val outputPrefix: Property<String>
}

/**
 * Owns one background process, starts it lazily, and always stops it when Gradle closes the service.
 */
internal abstract class BackgroundTaskService : BuildService<BackgroundTaskParameters>, AutoCloseable {
    @Transient
    private var startAttempted: Boolean = false

    @Transient
    private var process: Process? = null

    @Transient
    private var outputThreads: List<Thread> = emptyList()

    @Transient
    private var waiterThread: Thread? = null

    @Transient
    private var readinessRegex: Regex? = null

    @Transient
    private var resolvedCommand: List<String> = emptyList()

    @Transient
    private val ready: AtomicBoolean = AtomicBoolean()

    @Transient
    private val stopping: AtomicBoolean = AtomicBoolean()

    @Transient
    private val startupFailure: AtomicReference<String?> = AtomicReference()

    @Transient
    private val unexpectedExit: AtomicReference<String?> = AtomicReference()

    @Transient
    private val recentOutput: ArrayDeque<String> = ArrayDeque()

    @Transient
    private val logger = Logging.getLogger(BackgroundTaskService::class.java)

    @Synchronized
    fun startIfNeeded() {
        assertHealthy()
        if (ready.get()) return
        if (startAttempted) {
            throw failure("The background process failed during an earlier startup attempt")
        }
        startAttempted = true

        val readiness = BackgroundTaskReadiness.valueOf(parameters.readiness.get())
        val readyString = parameters.readyString.get()
        readinessRegex = if (readyString.isNotEmpty() && readiness == BackgroundTaskReadiness.REGEX) {
            try {
                Regex(readyString)
            } catch (cause: IllegalArgumentException) {
                throw failure("Invalid readiness regular expression '$readyString'", cause)
            }
        } else {
            null
        }

        val signal = CountDownLatch(1)
        resolvedCommand = parameters.command.get()
        logger.lifecycle("${parameters.outputPrefix.get()} Starting: ${resolvedCommand.joinToString(" ")}")

        val startedProcess = try {
            ProcessBuilder(resolvedCommand)
                .apply {
                    parameters.workingDirectory.get().takeIf(String::isNotEmpty)?.let { directory(File(it)) }
                    redirectErrorStream(parameters.redirectErrorStream.get())
                }
                .start()
        } catch (cause: Exception) {
            throw failure("Could not start the background process", cause)
        }
        process = startedProcess

        outputThreads = if (parameters.redirectErrorStream.get()) {
            listOf(startOutputReader(startedProcess.inputStream, null, signal))
        } else {
            listOf(
                startOutputReader(startedProcess.inputStream, "stdout", signal),
                startOutputReader(startedProcess.errorStream, "stderr", signal),
            )
        }
        waiterThread = thread(name = "background-task-waiter", isDaemon = true) {
            waitForExit(startedProcess, signal)
        }

        if (readyString.isEmpty()) {
            waitForStartupGrace(startedProcess)
        } else {
            waitForReadiness(signal)
        }

        startupFailure.get()?.let { message ->
            stop()
            throw failure(message)
        }
        if (!startedProcess.isAlive) {
            stop()
            throw failure("The background process exited immediately after reporting readiness")
        }
        ready.set(true)
        logger.lifecycle("${parameters.outputPrefix.get()} Ready")
    }

    private fun startOutputReader(input: InputStream, streamName: String?, signal: CountDownLatch): Thread = thread(
        name = "background-task-${streamName ?: "output"}",
        isDaemon = true,
    ) {
        try {
            BufferedReader(InputStreamReader(input)).use { reader ->
                reader.lineSequence().forEach { line ->
                    capture(line, streamName)
                    val streamLabel = streamName?.let { " [$it]" }.orEmpty()
                    logger.lifecycle("${parameters.outputPrefix.get()}$streamLabel $line")
                    inspectReadinessLine(line, signal)
                }
            }
        } catch (cause: Exception) {
            if (!stopping.get()) {
                recordProcessFailure("Could not read background process output: ${cause.message}")
                signal.countDown()
            }
        }
    }

    private fun waitForExit(startedProcess: Process, signal: CountDownLatch) {
        val exitCode = runCatching { startedProcess.waitFor() }.getOrNull()
        if (!stopping.get()) {
            val message = if (ready.get()) {
                "The background process exited unexpectedly with code ${exitCode ?: "unknown"}"
            } else {
                "The background process exited before readiness with code ${exitCode ?: "unknown"}"
            }
            recordProcessFailure(message)
            logger.error("${parameters.outputPrefix.get()} $message")
        }
        signal.countDown()
    }

    private fun recordProcessFailure(message: String) {
        if (ready.get()) {
            if (parameters.failOnUnexpectedExit.get()) {
                unexpectedExit.compareAndSet(null, message)
            }
        } else {
            startupFailure.compareAndSet(null, message)
        }
    }

    private fun inspectReadinessLine(line: String, signal: CountDownLatch) {
        if (ready.get()) return

        val readyString = parameters.readyString.get()
        val matches = when (BackgroundTaskReadiness.valueOf(parameters.readiness.get())) {
            BackgroundTaskReadiness.CONTAINS -> line.contains(readyString)
            BackgroundTaskReadiness.EXACT -> line == readyString
            BackgroundTaskReadiness.REGEX -> readinessRegex?.matches(line) == true
        }
        if (matches) {
            ready.set(true)
            signal.countDown()
            return
        }

        val readyLinePrefix = parameters.readyLinePrefix.get()
        if (readyLinePrefix.isNotEmpty() && line.startsWith(readyLinePrefix)) {
            startupFailure.compareAndSet(
                null,
                "Incompatible readiness line. Expected '$readyString', received '$line'",
            )
            signal.countDown()
        }
    }

    private fun waitForStartupGrace(startedProcess: Process) {
        try {
            Thread.sleep(parameters.startupGraceMillis.get())
        } catch (cause: InterruptedException) {
            Thread.currentThread().interrupt()
            stop()
            throw failure("Interrupted while waiting for the background process startup grace period", cause)
        }
        startupFailure.get()?.let { message ->
            stop()
            throw failure(message)
        }
        if (!startedProcess.isAlive) {
            stop()
            throw failure("The background process exited during its startup grace period")
        }
    }

    private fun waitForReadiness(signal: CountDownLatch) {
        val timeoutSeconds = parameters.readyTimeoutSeconds.get()
        val signalled = try {
            signal.await(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        } catch (cause: InterruptedException) {
            Thread.currentThread().interrupt()
            stop()
            throw failure("Interrupted while waiting for background process readiness", cause)
        }
        if (!signalled) {
            stop()
            throw failure("Timed out after ${timeoutSeconds}s waiting for background process readiness")
        }
    }

    fun assertHealthy() {
        unexpectedExit.get()?.let { throw failure(it) }
        if (ready.get() && parameters.failOnUnexpectedExit.get() && process?.isAlive != true) {
            throw failure("The background process is no longer running")
        }
    }

    private fun capture(line: String, streamName: String?) {
        synchronized(recentOutput) {
            recentOutput.addLast(streamName?.let { "[$it] $line" } ?: line)
            while (recentOutput.size > parameters.maxCapturedOutputLines.get()) {
                recentOutput.removeFirst()
            }
        }
    }

    private fun capturedOutput(): String = synchronized(recentOutput) {
        if (recentOutput.isEmpty()) {
            "<no process output>"
        } else {
            recentOutput.joinToString(System.lineSeparator())
        }
    }

    private fun failure(message: String, cause: Throwable? = null): GradleException {
        val command = resolvedCommand.takeIf(List<String>::isNotEmpty)?.joinToString(" ") ?: "<not resolved>"
        val details = "$message.${System.lineSeparator()}Command: $command" +
                "${System.lineSeparator()}Recent process output:${System.lineSeparator()}${capturedOutput()}"
        return if (cause == null) GradleException(details) else GradleException(details, cause)
    }

    @Synchronized
    fun stop() {
        stopping.set(true)
        val currentProcess = process
        if (currentProcess != null && currentProcess.isAlive) {
            logger.lifecycle("${parameters.outputPrefix.get()} Stopping")
            currentProcess.destroy()
            if (!runCatching {
                    currentProcess.waitFor(parameters.shutdownTimeoutSeconds.get().toLong(), TimeUnit.SECONDS)
                }.getOrDefault(false)
            ) {
                currentProcess.destroyForcibly()
                runCatching {
                    currentProcess.waitFor(parameters.forceShutdownTimeoutSeconds.get().toLong(), TimeUnit.SECONDS)
                }
            }
        }
        outputThreads.forEach { outputThread -> runCatching { outputThread.join(1_000) } }
        runCatching { waiterThread?.join(1_000) }
        process = null
        outputThreads = emptyList()
        waiterThread = null
    }

    override fun close() {
        val failure = unexpectedExit.get()
        stop()
        if (failure != null) throw failure(failure)
    }
}

/** Configures a process managed by [withBackgroundTask]. */
class BackgroundTaskConfig internal constructor(private val project: Project) {
    internal var commandProvider: Provider<List<String>>? = null
    internal var workingDirectoryProvider: Provider<String> = project.provider { "" }

    var lifetime: BackgroundTaskLifetime = BackgroundTaskLifetime.TASK
    var sharedName: String? = null
    var workingDir: File? = null
        set(value) {
            field = value
            workingDirectoryProvider = project.provider { value?.absolutePath.orEmpty() }
        }
    var readyString: String? = null
    var readiness: BackgroundTaskReadiness = BackgroundTaskReadiness.CONTAINS
    var readyLinePrefix: String? = null
    var readyTimeoutSec: Int = DEFAULT_READY_TIMEOUT_SECONDS
    var startupGraceMillis: Long = DEFAULT_STARTUP_GRACE_MILLIS
    var maxCapturedOutputLines: Int = DEFAULT_MAX_CAPTURED_OUTPUT_LINES
    var redirectErrorStream: Boolean = false
    var failOnUnexpectedExit: Boolean = true
    var shutdownTimeoutSec: Int = DEFAULT_SHUTDOWN_TIMEOUT_SECONDS
    var forceShutdownTimeoutSec: Int = DEFAULT_FORCE_SHUTDOWN_TIMEOUT_SECONDS
    var outputPrefix: String? = null

    fun workingDirectory(directory: Provider<Directory>) {
        workingDir = null
        workingDirectoryProvider = directory.map { it.asFile.absolutePath }
    }

    fun commandLine(command: String, vararg args: String) {
        commandLineProvider(project.provider { listOf(command) + args })
    }

    fun commandLineProvider(command: Provider<List<String>>) {
        if (commandProvider != null) {
            throw GradleException("Only one commandLine may be configured for a background task")
        }
        commandProvider = command
    }
}

private fun String.asBuildServiceName(): String = replace(Regex("[^A-Za-z0-9_.-]"), "-")

internal fun Project.registerBackgroundTaskService(
    task: Task,
    config: BackgroundTaskConfig,
): Provider<BackgroundTaskService> {
    val command = config.commandProvider
        ?: throw GradleException("withBackgroundTask requires commandLine to be configured")
    val serviceIdentity = when (config.lifetime) {
        BackgroundTaskLifetime.TASK -> "task-${task.path}"
        BackgroundTaskLifetime.BUILD -> {
            val sharedName = config.sharedName?.takeIf(String::isNotBlank)
                ?: throw GradleException("A build-scoped background task requires a non-blank sharedName")
            "build-$sharedName"
        }
    }
    val serviceName = "background-$serviceIdentity".asBuildServiceName()

    return gradle.sharedServices.registerIfAbsent(serviceName, BackgroundTaskService::class.java) {
        parameters.command.set(command)
        parameters.workingDirectory.set(config.workingDirectoryProvider)
        parameters.readyString.set(config.readyString.orEmpty())
        parameters.readiness.set(config.readiness.name)
        parameters.readyLinePrefix.set(config.readyLinePrefix.orEmpty())
        parameters.readyTimeoutSeconds.set(config.readyTimeoutSec)
        parameters.startupGraceMillis.set(config.startupGraceMillis)
        parameters.maxCapturedOutputLines.set(config.maxCapturedOutputLines)
        parameters.redirectErrorStream.set(config.redirectErrorStream)
        parameters.failOnUnexpectedExit.set(config.failOnUnexpectedExit)
        parameters.shutdownTimeoutSeconds.set(config.shutdownTimeoutSec)
        parameters.forceShutdownTimeoutSeconds.set(config.forceShutdownTimeoutSec)
        parameters.outputPrefix.set(config.outputPrefix ?: "[background:${task.path}]")
    }
}

/**
 * Runs this task with a lazily started background process.
 *
 * Task-scoped processes stop after a successful task and always stop when the build ends.
 * Build-scoped processes are shared by [BackgroundTaskConfig.sharedName] and stop when the build ends.
 */
fun Task.withBackgroundTask(
    configure: BackgroundTaskConfig.() -> Unit = {},
) {
    val config = BackgroundTaskConfig(project).apply(configure)
    val service = project.registerBackgroundTaskService(this, config)

    usesService(service)
    doFirst("startBackgroundTask") {
        service.get().startIfNeeded()
        service.get().assertHealthy()
    }
    when (config.lifetime) {
        BackgroundTaskLifetime.TASK -> doLast("stopBackgroundTask") {
            try {
                service.get().assertHealthy()
            } finally {
                service.get().stop()
            }
        }
        BackgroundTaskLifetime.BUILD -> doLast("checkBackgroundTask") {
            service.get().assertHealthy()
        }
    }
}
