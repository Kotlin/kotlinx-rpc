/*
 * Copyright 2023-2026 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package util

import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.IOException
import java.util.ArrayDeque
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

private const val DEFAULT_READY_TIMEOUT_SECONDS = 30
private const val SHUTDOWN_TIMEOUT_SECONDS = 3L
private const val MAX_CAPTURED_OUTPUT_LINES = 200

internal interface BackgroundTaskParameters : BuildServiceParameters {
    val command: ListProperty<String>
    val workingDirectory: DirectoryProperty
    val readyLine: Property<String>
    val readyLinePrefix: Property<String>
    val readyTimeoutSeconds: Property<Int>
    val outputPrefix: Property<String>
}

/**
 * Owns one background process. The service starts the process on first use and stops it when Gradle
 * closes the service at the end of the build.
 *
 * The service writes the stdout and stderr of the process to the Gradle log. Every failure has
 * the most recent output lines.
 */
internal abstract class BackgroundTaskService : BuildService<BackgroundTaskParameters>, AutoCloseable {
    private val logger = Logging.getLogger(BackgroundTaskService::class.java)
    private val prefix: String get() = parameters.outputPrefix.get()
    private val recentOutput = ArrayDeque<String>()
    private val readinessDecided = CountDownLatch(1)

    /** The message of the first startup or runtime failure. Every later use of the service fails with it. */
    private val failure = AtomicReference<String?>()

    @Volatile
    private var ready: Boolean = false

    @Volatile
    private var stopping: Boolean = false

    private var startAttempted: Boolean = false
    private var process: Process? = null
    private var outputReader: Thread? = null

    /** Starts the process on the first call only, then runs [checkHealthy]. */
    @Synchronized
    fun ensureRunning() {
        if (!startAttempted) {
            startAttempted = true
            start()
        }
        checkHealthy()
    }

    /** Fails if the process failed to start or exited after it became ready. */
    fun checkHealthy() {
        failure.get()?.let { throw failure(it) }
    }

    private fun start() {
        val command = parameters.command.get()
        logger.lifecycle("$prefix Starting: ${command.joinToString(" ")}")
        val started = try {
            ProcessBuilder(command)
                .directory(parameters.workingDirectory.get().asFile)
                .redirectErrorStream(true)
                .start()
        } catch (cause: IOException) {
            throw failure("The background process didn't start", cause)
        }
        process = started
        outputReader = thread(name = "background-task-output", isDaemon = true) { readOutput(started) }

        val timeoutSeconds = parameters.readyTimeoutSeconds.get()
        val decided = try {
            readinessDecided.await(timeoutSeconds.toLong(), TimeUnit.SECONDS)
        } catch (cause: InterruptedException) {
            Thread.currentThread().interrupt()
            stop()
            throw failure("An interrupt stopped the wait for the ready line", cause)
        }
        if (!decided) recordFailure("The background process didn't print its ready line in ${timeoutSeconds}s")
        failure.get()?.let { message ->
            stop()
            throw failure(message)
        }
        logger.lifecycle("$prefix Ready")
    }

    /** Runs on [outputReader] until the process closes its output, then records how the process ended. */
    private fun readOutput(started: Process) {
        try {
            started.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    capture(line)
                    logger.lifecycle("$prefix $line")
                    if (!ready) checkReadiness(line)
                }
            }
        } catch (cause: IOException) {
            if (!stopping) recordFailure("Can't read the background process output: ${cause.message}")
        }

        val exitCode = runCatching { started.waitFor() }.getOrNull() ?: "unknown"
        if (!stopping) {
            val stage = if (ready) "unexpectedly" else "before readiness"
            recordFailure("The background process exited $stage with code $exitCode")
        }
        readinessDecided.countDown()
    }

    private fun checkReadiness(line: String) {
        val readyLine = parameters.readyLine.get()
        if (line == readyLine) {
            ready = true
            readinessDecided.countDown()
            return
        }
        // A ready line from a process with a different configuration fails the startup now, not after the timeout.
        val readyLinePrefix = parameters.readyLinePrefix.orNull
        if (readyLinePrefix != null && line.startsWith(readyLinePrefix)) {
            recordFailure(
                "Incompatible ready line. Expected '$readyLine', got '$line'. " +
                    "Rebuild the process so that it prints the expected ready line"
            )
            readinessDecided.countDown()
        }
    }

    private fun recordFailure(message: String) {
        if (failure.compareAndSet(null, message)) logger.error("$prefix $message")
    }

    private fun capture(line: String) {
        synchronized(recentOutput) {
            recentOutput.addLast(line)
            if (recentOutput.size > MAX_CAPTURED_OUTPUT_LINES) recentOutput.removeFirst()
        }
    }

    private fun failure(message: String, cause: Throwable? = null): GradleException {
        val command = parameters.command.get().joinToString(" ")
        val output = synchronized(recentOutput) {
            recentOutput.joinToString(System.lineSeparator()).ifEmpty { "<no process output>" }
        }
        val details = listOf("$message.", "Command: $command", "Recent process output:", output)
            .joinToString(System.lineSeparator())
        return GradleException(details, cause)
    }

    @Synchronized
    private fun stop() {
        stopping = true
        val current = process ?: return
        if (current.isAlive) {
            logger.lifecycle("$prefix Stopping")
            current.destroy()
            if (!current.waitFor(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                current.destroyForcibly()
                current.waitFor(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            }
        }
        outputReader?.join(1_000)
        process = null
        outputReader = null
    }

    override fun close() {
        stop()
    }
}

/** Configures the process started by [withBackgroundTask]. */
class BackgroundTaskConfig internal constructor() {
    /** The command line of the process. */
    var command: Provider<List<String>>? = null

    /** The directory the process runs in. */
    var workingDirectory: Provider<Directory>? = null

    /** The exact output line that the process prints when it is ready. */
    var readyLine: String? = null

    /**
     * The prefix of [readyLine]. A different line with this prefix fails the startup immediately. For example, the
     * process reports a different endpoint or protocol version.
     */
    var readyLinePrefix: String? = null

    var readyTimeoutSeconds: Int = DEFAULT_READY_TIMEOUT_SECONDS

    /** The prefix of the Gradle log lines for this process. */
    var outputPrefix: String = "[background]"
}

/**
 * Runs this task with the background process [name].
 *
 * - The process starts before the first task that uses it and stops when the build finishes.
 * - All tasks with the same [name] use one process. Only the configuration of the first registration has an
 *   effect, so all these tasks must configure the process in the same way.
 * - A task fails if the process can't start or exits while the task runs.
 */
fun Task.withBackgroundTask(name: String, configure: BackgroundTaskConfig.() -> Unit) {
    val config = BackgroundTaskConfig().apply(configure)
    val service = project.gradle.sharedServices.registerIfAbsent(
        "background-$name",
        BackgroundTaskService::class.java,
    ) {
        parameters.command.set(config.command ?: throw GradleException("Background task '$name' needs a command"))
        parameters.workingDirectory.set(
            config.workingDirectory ?: throw GradleException("Background task '$name' needs a working directory")
        )
        parameters.readyLine.set(config.readyLine ?: throw GradleException("Background task '$name' needs a readyLine"))
        parameters.readyLinePrefix.set(config.readyLinePrefix)
        parameters.readyTimeoutSeconds.set(config.readyTimeoutSeconds)
        parameters.outputPrefix.set(config.outputPrefix)
    }

    usesService(service)
    doFirst("startBackgroundTask") { service.get().ensureRunning() }
    doLast("checkBackgroundTask") { service.get().checkHealthy() }
}
